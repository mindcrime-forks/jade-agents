/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.proto;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.proto.states.MsgReceiver;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import jade.util.leap.Iterator;
import jade.util.leap.Map;
import jade.util.leap.HashMap;
import jade.util.leap.List;
import jade.util.leap.ArrayList;
import jade.util.leap.Serializable;

/**
 * @author Giovanni Caire - TILab
 **/
abstract class Initiator extends FSMBehaviour {	
    protected final String INITIATION_K = "__initiation" + hashCode();
    protected final String ALL_INITIATIONS_K = "__all-initiations" +hashCode();
    protected final String REPLY_K = "__reply" + hashCode();

    // FSM states names
    protected static final String PREPARE_INITIATIONS = "Prepare-initiations";
    protected static final String SEND_INITIATIONS = "Send-initiations";
    protected static final String RECEIVE_REPLY = "Receive-reply";
    protected static final String CHECK_IN_SEQ = "Check-in-seq";
    protected static final String HANDLE_NOT_UNDERSTOOD = "Handle-not-understood";
    protected static final String HANDLE_FAILURE = "Handle-failure";
    protected static final String HANDLE_OUT_OF_SEQ = "Handle-out-of-seq";
    protected static final String CHECK_SESSIONS = "Check-sessions";
    protected static final String DUMMY_FINAL = "Dummy-final";
	
    // This maps the AID of each responder to a Session object
    // holding the status of the protocol as far as that responder
    // is concerned. Sessions are protocol-specific
    protected Map sessions = new HashMap();	
    // The MsgReceiver behaviour used to receive replies 
    protected MsgReceiver replyReceiver = null;
    // The MessageTemplate used by the replyReceiver
		protected MessageTemplate replyTemplate = null; 
		
    private ACLMessage initiation;
    
    /**
     * Constructs an <code>Initiator</code> behaviour
     * @see #AchieveREInitiator(Agent, ACLMessage, DataStore)
     **/
    protected Initiator(Agent a, ACLMessage initiation){
			this(a, initiation, new DataStore());
    }

    /**
     * Constructs an <code>Initiator</code> behaviour
     * @param a The agent performing the protocol
     * @param initiation The message that must be used to initiate the protocol.
     * @param s The <code>DataStore</code> that will be used by this 
     * <code>Initiator</code>
     */
    protected Initiator(Agent a, ACLMessage initiation, DataStore store) {
	super(a);
		
	setDataStore(store);
	this.initiation = initiation;
	
	// Register the FSM transitions
	registerDefaultTransition(PREPARE_INITIATIONS, SEND_INITIATIONS);
	registerTransition(SEND_INITIATIONS, DUMMY_FINAL, 0); // Exit the protocol if no initiation message is sent
	registerDefaultTransition(SEND_INITIATIONS, RECEIVE_REPLY);
	registerTransition(RECEIVE_REPLY, CHECK_SESSIONS, MsgReceiver.TIMEOUT_EXPIRED); 
	registerTransition(RECEIVE_REPLY, CHECK_SESSIONS, MsgReceiver.INTERRUPTED); 
	registerDefaultTransition(RECEIVE_REPLY, CHECK_IN_SEQ);
	registerTransition(CHECK_IN_SEQ, HANDLE_NOT_UNDERSTOOD, ACLMessage.NOT_UNDERSTOOD);		
	registerTransition(CHECK_IN_SEQ, HANDLE_FAILURE, ACLMessage.FAILURE);		
	registerDefaultTransition(CHECK_IN_SEQ, HANDLE_OUT_OF_SEQ);		
	registerDefaultTransition(HANDLE_NOT_UNDERSTOOD, CHECK_SESSIONS);
	registerDefaultTransition(HANDLE_FAILURE, CHECK_SESSIONS);
	registerDefaultTransition(HANDLE_OUT_OF_SEQ, RECEIVE_REPLY);
	registerDefaultTransition(CHECK_SESSIONS, RECEIVE_REPLY, getToBeReset());
			
	// Create and register the states that make up the FSM
	Behaviour b = null;
	// PREPARE_INITIATIONS
	b = new OneShotBehaviour(myAgent) {
  	private static final long     serialVersionUID = 3487495895818000L;
			
		public void action() {
		  DataStore ds = getDataStore();
		  Vector allInitiations = (Vector) ds.get(ALL_INITIATIONS_K);
		  if (allInitiations == null || allInitiations.size() == 0) {
			  allInitiations = prepareInitiations((ACLMessage) ds.get(INITIATION_K));
			  ds.put(ALL_INITIATIONS_K, allInitiations);
		  }
		}
	};
	b.setDataStore(getDataStore());		
	registerFirstState(b, PREPARE_INITIATIONS);
		
	// SEND_INITIATIONS
	b = new OneShotBehaviour(myAgent) {
  	private static final long     serialVersionUID = 3487495895818001L;
  	
		public void action() {
		  Vector allInitiations = (Vector) getDataStore().get(ALL_INITIATIONS_K);
		  if (allInitiations != null) {
			  sendInitiations(allInitiations);
		  }
		}	
		public int onEnd() {
			return sessions.size();
		}
	};
	b.setDataStore(getDataStore());		
	registerState(b, SEND_INITIATIONS);
	
	// RECEIVE_REPLY
	replyReceiver = new MsgReceiver(myAgent, null, MsgReceiver.INFINITE, getDataStore(), REPLY_K);
	registerState(replyReceiver, RECEIVE_REPLY);
	
	// CHECK_IN_SEQ
	b = new OneShotBehaviour(myAgent) {
		int ret;
  	private static final long     serialVersionUID = 3487495895818002L;
  			
		public void action() {
		  ACLMessage reply = (ACLMessage) getDataStore().get(REPLY_K);
			if (checkInSequence(reply)) {
				ret = reply.getPerformative();
			}
			else {
				ret = -1;
			}
		}
		public int onEnd() {
		    return ret;
		}
	};
	b.setDataStore(getDataStore());		
	registerState(b, CHECK_IN_SEQ);
		
	// HANDLE_NOT_UNDERSTOOD
	b = new OneShotBehaviour(myAgent) {
  	private static final long     serialVersionUID = 3487495895818005L;
  	
		public void action() {
		    handleNotUnderstood((ACLMessage) getDataStore().get(REPLY_K));
		}
	};
	b.setDataStore(getDataStore());		
	registerState(b, HANDLE_NOT_UNDERSTOOD);
	
	// HANDLE_FAILURE
	b = new OneShotBehaviour(myAgent) {
  	private static final long     serialVersionUID = 3487495895818007L;
  	
		public void action() {
		    handleFailure((ACLMessage) getDataStore().get(REPLY_K));
		}
	};
	b.setDataStore(getDataStore());		
	registerState(b, HANDLE_FAILURE);
	
	// HANDLE_OUT_OF_SEQ
	b = new OneShotBehaviour(myAgent) {
  	private static final long     serialVersionUID = 3487495895818008L;
  	
		public void action() {
		    handleOutOfSequence((ACLMessage) getDataStore().get(REPLY_K));
		}
	};
	b.setDataStore(getDataStore());		
	registerState(b, HANDLE_OUT_OF_SEQ);
	
	// CHECK_SESSIONS
	b = new OneShotBehaviour(myAgent) {
		int ret;
  	private static final long     serialVersionUID = 3487495895818009L;
  	
		public void action() {
		  ACLMessage reply = (ACLMessage) getDataStore().get(REPLY_K);
			ret = checkSessions(reply);
		}		
		public int onEnd() {
		    return ret;
		}
	};
	b.setDataStore(getDataStore());		
	registerState(b, CHECK_SESSIONS);
	
	// DUMMY_FINAL
	b = new OneShotBehaviour(myAgent) {
  	private static final long     serialVersionUID = 3487495895818010L;
  	
		public void action() {}
	};
	registerLastState(b, DUMMY_FINAL);
    }

    /**
       Specialize (if necessary) the initiation message for each receiver
     */    
    protected abstract Vector prepareInitiations(ACLMessage initiation);
    /**
       Create and initialize the Sessions and sends the initiation messages
     */    
    protected abstract void sendInitiations(Vector initiations);
    /**
       Check whether a reply is in-sequence and update the appropriate Session
     */    
    protected abstract boolean checkInSequence(ACLMessage reply);
    /**
       Check the global status of the sessions after the reception of the last reply
       or the expiration of the timeout
     */    
    protected abstract int checkSessions(ACLMessage reply);
    /**
		   Return the states that must be reset before they are visited again.
		   Note that resetting a state before visiting it again is required
		   only if
		   - The onStart() method is redefined
		   - The state has an "internal memory"
     */
    protected abstract String[] getToBeReset();
    
    /**
     * This method is called every time a <code>not-understood</code>
     * message is received, which is not out-of-sequence according
     * to the protocol rules.
     * This default implementation does nothing; programmers might
     * wish to override the method in case they need to react to this event.
     * @param notUnderstood the received not-understood message
     **/
    protected void handleNotUnderstood(ACLMessage notUnderstood) {
    }
    
    /**
     * This method is called every time a <code>failure</code>
     * message is received, which is not out-of-sequence according
     * to the protocol rules.
     * This default implementation does nothing; programmers might
     * wish to override the method in case they need to react to this event.
     * @param failure the received failure message
     **/
    protected void handleFailure(ACLMessage failure) {
    }
    
    /**
     * This method is called every time a 
     * message is received, which is out-of-sequence according
     * to the protocol rules.
     * This default implementation does nothing; programmers might
     * wish to override the method in case they need to react to this event.
     * @param msg the received message
     **/
    protected void handleOutOfSequence(ACLMessage msg) {
    }
    
    /**
     */
    protected void registerPrepareInitiations(Behaviour b) {
			registerState(b, PREPARE_INITIATIONS);
			b.setDataStore(getDataStore());
    }
    
    /**
       This method allows to register a user defined <code>Behaviour</code>
       in the HANDLE_NOT_UNDERSTOOD state.
       This behaviour would override the homonymous method.
       This method also set the 
       data store of the registered <code>Behaviour</code> to the
       DataStore of this current behaviour.
       The registered behaviour can retrieve
       the <code>not-understood</code> ACLMessage object received
       from the datastore at the <code>REPLY_KEY</code>
       key.
       @param b the Behaviour that will handle this state
    */
    public void registerHandleNotUnderstood(Behaviour b) {
			registerState(b, HANDLE_NOT_UNDERSTOOD);
			b.setDataStore(getDataStore());
    }
    
    /**
       This method allows to register a user defined <code>Behaviour</code>
       in the HANDLE_FAILURE state.
       This behaviour would override the homonymous method.
       This method also set the 
       data store of the registered <code>Behaviour</code> to the
       DataStore of this current behaviour.
       The registered behaviour can retrieve
       the <code>failure</code> ACLMessage object received
       from the datastore at the <code>REPLY_KEY</code>
       key.
       @param b the Behaviour that will handle this state
    */
    public void registerHandleFailure(Behaviour b) {
			registerState(b, HANDLE_FAILURE);
			b.setDataStore(getDataStore());
    }
    
    /**
       This method allows to register a user defined <code>Behaviour</code>
       in the HANDLE_OUT_OF_SEQ state.
       This behaviour would override the homonymous method.
       This method also set the 
       data store of the registered <code>Behaviour</code> to the
       DataStore of this current behaviour.
       The registered behaviour can retrieve
       the <code>out of sequence</code> ACLMessage object received
       from the datastore at the <code>REPLY_KEY</code>
       key.
       @param b the Behaviour that will handle this state
    */
    public void registerHandleOutOfSequence(Behaviour b) {
			registerState(b, HANDLE_OUT_OF_SEQ);
			b.setDataStore(getDataStore());
    }
    
    /**
     * reset this behaviour by putting a null ACLMessage as message
     * to be sent
     **/
    public void reset(){
			reset(null);
    }

    /**
     * reset this behaviour
     * @param msg is the ACLMessage to be sent
     **/
    public void reset(ACLMessage msg){
			super.reset();
			replyReceiver.reset(null, MsgReceiver.INFINITE, getDataStore(),REPLY_K);
			initiation = msg;
			sessions.clear();
  	}

    /** 
       Override the onStart() method to initialize the vectors that
       will keep all the replies in the data store.
     */
    public void onStart() {
    	initializeDataStore(initiation);
    }
    
    /** 
       Override the setDataStore() method to initialize propagate this
       setting to all children.
     */
    public void setDataStore(DataStore ds) {
    	super.setDataStore(ds);
    	Iterator it = getChildren().iterator();
    	while (it.hasNext()) {
    		Behaviour b = (Behaviour) it.next();
    		b.setDataStore(ds);
    	}
    }
    
    /**
       Initialize the data store. 
     **/
    protected void initializeDataStore(ACLMessage initiation){
			getDataStore().put(INITIATION_K, initiation);
    }
    
    protected String createConvId(Vector msgs) {
			// If the conversation-id of the first message is set --> 
    	// use it. Otherwise create a default one
    	String convId = null;
		  if (msgs.size() > 0) {
		  	ACLMessage msg = (ACLMessage) msgs.elementAt(0);
				if ((msg == null) || (msg.getConversationId() == null)) {
					convId = "C"+hashCode()+"_"+System.currentTimeMillis();
				}
			  else {
					convId = msg.getConversationId();
			  }
		  }
		  return convId;
    }
    
    protected void adjustReplyTemplate(ACLMessage msg) {
			// If myAgent is among the receivers (strange case, but can happen)
			// then modify the replyTemplate to avoid intercepting the initiation
    	// message msg as if it was a reply
    	AID r = (AID) msg.getAllReceiver().next();
			if (myAgent.getAID().equals(r)) {
				replyTemplate = MessageTemplate.and(
					replyTemplate,
					MessageTemplate.not(MessageTemplate.MatchCustom(msg, true)));
			}
    }
}
	
		
		
