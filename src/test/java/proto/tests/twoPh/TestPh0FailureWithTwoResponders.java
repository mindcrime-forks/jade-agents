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

package proto.tests.twoPh;

import test.common.*;
import test.common.Logger;

import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.proto.*;

import java.util.Enumeration;
import java.util.Date;

/**
   Test the Two phase commit protocol support in the case that
   all responders reply successfully in all steps.
   @author Giovanni Caire - TILAB
   @author Elena Quarantotto - TILAB
 */
public class TestPh0FailureWithTwoResponders extends Test {

	private static final String R1 = "r1";
	private static final String R2 = "r2";
	private AID r1, r2;
    private boolean success = true;

  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	final DataStore store = ds;
  	final String key = resultKey;
    final Logger l = Logger.getLogger();

    // Start the responders

    r1 = TestUtility.createAgent(a, R1, "test.proto.tests.twoPh.TestPh0FailureWithTwoResponders$Responder",
            new String[] {ACLMessage.getPerformative(ACLMessage.FAILURE)});
    r2 = TestUtility.createAgent(a, R2, "test.proto.tests.twoPh.TestPh0FailureWithTwoResponders$Responder",
            new String[] {ACLMessage.getPerformative(ACLMessage.PROPOSE)});

    // Create the initial message
    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
    cfp.setProtocol(TwoPhConstants.JADE_TWO_PHASE_COMMIT);
    cfp.addReceiver(r1);
    cfp.addReceiver(r2);
    cfp.setReplyByDate(new Date(System.currentTimeMillis() + 30000)); // 30 sec timeout

    // Create and return the behaviour that will actually perform the test
  	Behaviour b = new TwoPhInitiator(a, cfp) {
  		int cnt = 0;

  		protected void handlePropose(ACLMessage propose) {
            l.log(myAgent.getLocalName()+"[PH0]: PROPOSE message received");
  		}

      protected void handleFailure(ACLMessage failure) {
            if(getCurrentPhase().equals(TwoPhInitiator.PH0_STATE)) {
	            l.log(myAgent.getLocalName()+"[PH0]: FAILURE message received");
            }
            else {
	            l.log(myAgent.getLocalName()+": Unexpected FAILURE message received in phase "+getCurrentPhase());
              success = success && false;
            }
  		}

	    protected void handleAllPh0Responses(Vector responses, Vector proposes, Vector pendings, Vector nextPhMsgs) {
	    	boolean error = false;
	    	if (proposes.size() != 1) {
          l.log(myAgent.getLocalName()+"[PH0]: "+proposes.size()+" PROPOSE received while 1 was expected.");
	    		error = true;
	    	}
	    	if (pendings.size() != 0) {
          l.log(myAgent.getLocalName()+"[PH0]: "+pendings.size()+" pending CFP while 0 were expected.");
	    		error = true;
	    	}
	    	Enumeration e = nextPhMsgs.elements();
	    	int cnt = 0;
	    	while (e.hasMoreElements()) {
	    		ACLMessage msg = (ACLMessage) e.nextElement();
	    		if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
	    			cnt++;
	    		}
	    	}
	    	if (cnt != 1) {
          l.log(myAgent.getLocalName()+"[PH0]: "+cnt+" REJECT_PROPOSAL prepared while 1 was expected.");
	    		error = true;
	    	}
	    	if (error) {
	    		nextPhMsgs.clear();
          success = success && false;
	    	}
	    }

  		protected void handlePh2Inform(ACLMessage inform) {
            l.log(myAgent.getLocalName()+"[PH2]: INFORM message received");
  		}

		  protected void handleAllPh2Responses(Vector responses) {
	    	Enumeration e = responses.elements();
	    	int cnt = 0;
	    	while (e.hasMoreElements()) {
	    		ACLMessage msg = (ACLMessage) e.nextElement();
	    		if (msg.getPerformative() == ACLMessage.INFORM) {
	    			cnt++;
	    		}
	    	}
	    	if (cnt != 1) {
          l.log(myAgent.getLocalName()+"[PH2]: "+cnt+" INFORM received while 1 was expected.");
	    	}
	    	else {
          success = success && true;
	    	}
		  }

  		public int onEnd() {
              int ret;
              if(!success)
                ret = Test.TEST_FAILED;
              else
                ret = Test.TEST_PASSED;
  			store.put(key, new Integer(ret));
  			return 0;
  		}
  	};

  	return b;
  }

  public void clean(Agent a) {
  	try {
  		TestUtility.killAgent(a, r1);
  		TestUtility.killAgent(a, r2);
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
  }

  /**
     Inner class Responder
   */
  public static class Responder extends Agent {
    final Logger l = Logger.getLogger();
    private String ph0Response = null;

  	protected void setup() {
        Object[] args = getArguments();
		ph0Response = (String) args[0];

  		addBehaviour(new TwoPhResponder(this, TwoPhResponder.createMessageTemplate()) {
  			protected ACLMessage handleCfp(ACLMessage cfp) {
                l.log("\n\nLOG - (Responder, handleCfp(), " + myAgent.getLocalName() +
                        ") - received --------------> " + cfp);
                ACLMessage response = null;
                try {
                    response = cfp.createReply();
                    if(ph0Response.equals(ACLMessage.getPerformative(ACLMessage.PROPOSE)))
                        response.setPerformative(ACLMessage.PROPOSE);
                    else
                        response.setPerformative(ACLMessage.FAILURE);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                l.log("\n\nLOG - (Responder, handleCfp(), " + myAgent.getLocalName() +
                        ") - send --------------> " + response);
                return response;
  			}

              protected ACLMessage handleRejectProposal(ACLMessage reject) {
                  l.log("\n\nLOG - (Responder, handleRejectProposal(), " + myAgent.getLocalName() +
                        ") - received --------------> " + reject);
                  ACLMessage inform = reject.createReply();
                  inform.setPerformative(ACLMessage.INFORM);
                  l.log("\n\nLOG - (Responder, handleRejectProposal(), " + myAgent.getLocalName() +
                        ") - send --------------> " + inform);
                  return inform;
              }

  		} );
  	}
  } // END of inner class Responder
}
