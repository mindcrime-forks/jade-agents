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

package jade.domain;

import java.lang.reflect.Method;
import java.util.Vector;

import jade.util.leap.HashMap;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import jade.util.leap.Iterator;
import java.net.InetAddress;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.FIPAAgentManagementOntology;
import jade.domain.FIPAAgentManagement.MissingParameter;
import jade.domain.FIPAAgentManagement.AlreadyRegistered;
import jade.domain.FIPAAgentManagement.NotRegistered;
import jade.domain.FIPAAgentManagement.Unauthorised;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.UnsupportedFunction;
import jade.domain.FIPAAgentManagement.UnrecognisedValue;
import jade.domain.FIPAAgentManagement.UnexpectedAct;

import jade.onto.Ontology;
import jade.onto.Frame;
import jade.onto.OntologyException;
import jade.onto.basic.TrueProposition;
import jade.domain.JADEAgentManagement.*;

import jade.domain.DFGUIManagement.*;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.sl.SL0Codec;
import jade.onto.basic.Action;
import jade.onto.basic.ResultPredicate;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.onto.basic.DonePredicate;

/**
  Standard <em>Directory Facilitator</em> agent. This class implements
  <em><b>FIPA</b></em> <em>DF</em> agent. <b>JADE</b> applications
  cannot use this class directly, but interact with it through
  <em>ACL</em> message passing. More <em>DF</em> agents can be created
  by application programmers to divide a platform into many
  <em><b>Agent Domains</b></em>.

  Each DF has a GUI but, by default, it is not visible. The GUI of the
  agent platform includes a menu item that allows to show the GUI of the
  default DF. 

  In order to show the GUI, you should simply send the following message
  to each DF agent: <code>(request :content (action DFName (SHOWGUI))
  :ontology jade-extensions :protocol fipa-request)</code>
 
  @author Giovanni Rimassa - Universita` di Parma
  @author Tiziana Trucco - TILAB S.p.A.
  @version $Date$ $Revision$

*/
public class df extends GuiAgent implements DFGUIAdapter {

  private class RecursiveSearchBehaviour extends RequestFIPAServiceBehaviour 
  {
  	RecursiveSearchHandler rsh;
  	RecursiveSearchBehaviour(RecursiveSearchHandler rsh,AID children,DFAgentDescription dfd,SearchConstraints constraints) throws FIPAException
  	{
  		super(df.this,children,FIPAAgentManagementOntology.SEARCH,dfd,constraints);
  		this.rsh = rsh;
  	}
  	
  	protected void handleInform(ACLMessage reply)
  	{
  		super.handleInform(reply);
  		try{
  			// Convert search result from array to list
  			Object[] r = getSearchResults();
  			List l = new ArrayList();
  			for (int i = 0; i < r.length; ++i) {
  				l.add(r[i]);
  			}
  			rsh.addResults(this, l);
  		}catch (FIPAException e){ e.printStackTrace();
  		}catch(NotYetReady nyr){ nyr.printStackTrace();}
  	}
  	
  	protected void handleRefuse(ACLMessage reply)
  	{
  		super.handleRefuse(reply);
  		try{
  			rsh.addResults(this,new ArrayList(0));
  		}catch(FIPAException e){e.printStackTrace();}
  	}
  	
  	protected void handleFailure(ACLMessage reply)
  	{
  		super.handleFailure(reply);
  		try{
  			rsh.addResults(this,new ArrayList(0));
  		}catch(FIPAException e){e.printStackTrace();}

  	}
  	protected void handleNotUnderstood(ACLMessage reply)
  	{
  		super.handleNotUnderstood(reply);
  		try{
  			rsh.addResults(this,new ArrayList(0));
  		}catch(FIPAException e){e.printStackTrace();}

  	}

      //send a not understood message if an out of sequence message arrives.
      protected void handleOutOfSequence(ACLMessage reply){
	  super.handleOutOfSequence(reply);
	  try{
	      rsh.addResults(this,new ArrayList(0));
	      ACLMessage res = reply.createReply();
	      res.setPerformative(ACLMessage.NOT_UNDERSTOOD);
	      UnexpectedAct ua = new UnexpectedAct(ACLMessage.getPerformative(reply.getPerformative()));
	      String cont = "( (action "+reply.getSender()+" "+reply+") "+ua.getMessage()+")";
	      res.setContent(cont);
	      myAgent.send(res);
	  }catch(FIPAException e){e.printStackTrace();}
      }

      //called when the timeout is expired.
      protected void handleAllResponses(Vector reply){
	  super.handleAllResponses(reply);
	  try{
	      if(reply.size() == 0) //the timeout has expired: no replies received.
		  rsh.addResults(this,new ArrayList(0));
	  }catch(FIPAException e){e.printStackTrace();}
      }
  }//End class RecursiveSearchBehaviour
  
     
    /*
      This method called into the DFFIPAAgentManagementBehaviour add the behaviours for a recursive search.
return true if the Df has children, false otherwise
     */
	protected boolean performRecursiveSearch(List l, SearchConstraints constraints, DFAgentDescription dfd, ACLMessage request, Action action){

	    boolean out = false;

	    Long maxResults=constraints.getMaxResults();
 
	    RecursiveSearchHandler rsh = new RecursiveSearchHandler(l, constraints, dfd, request, action);
	    SearchConstraints newConstr = new SearchConstraints();
	    
	    newConstr.setMaxDepth(new Long ((new Integer(constraints.getMaxDepth().intValue()-1)).longValue()));
	    
	    if(maxResults != null)
		newConstr.setMaxResults(new Long((new Integer(constraints.getMaxResults().intValue() - l.size())).longValue()));	    
	    Iterator childIt = children.iterator();
	    while(childIt.hasNext()){
		try{
		    out = true;
		    RecursiveSearchBehaviour b = new RecursiveSearchBehaviour(rsh,(AID)childIt.next(), dfd, newConstr);
		    addBehaviour(b);
		    rsh.addChildren(b);
		}catch(FIPAException e){}
	    }
	    return out;
	}
	
  private class RecursiveSearchHandler {
  	List children;
  	long deadline;
  	List results;
  	SearchConstraints constraints;
  	DFAgentDescription dfd;
  	ACLMessage request;
  	Action action;
        int DEFAULTTIMEOUT = 300000; // 5 minutes	
  	long MAXRESULTS = 100; //Maximum number of results if not set 
  	
    //constructor
    RecursiveSearchHandler(List l, SearchConstraints c, DFAgentDescription dfd, ACLMessage msg, Action a) { 
	    this.results = l;
	    this.constraints = new SearchConstraints();
	    constraints.setMaxDepth(c.getMaxDepth()); //MaxDepth is not null by definition of this point of the code
	    if(c.getMaxResults() != null)
	      constraints.setMaxResults(c.getMaxResults());
	    // else
	    // constraints.setMaxResults(new Long(MAXRESULTS));
	    this.dfd = dfd;
	    this.request = msg;
	    this.children = new ArrayList();
	 
	    //the replybyDate should have been set; if not the recursive handler set a deadline after 5 minutes.  
	  if (this.request.getReplyByDate() == null)
  		   this.deadline = System.currentTimeMillis() + DEFAULTTIMEOUT;
  	  else 
  	     this.deadline = this.request.getReplyByDate().getTime();

  	  this.action = a;   
  	}
  	
    void addChildren(Behaviour b) {
  		this.children.add(b);
  	}
  	void removeChildren(Behaviour b) {
  		this.children.remove(b);
  	}
  	
      void addResults(Behaviour b, List localResults) throws FIPAException {
	  
	  this.children.remove(b);
	  
	  if(constraints.getMaxResults() != null){
	      //number of results still missing	 
	      int remainder = constraints.getMaxResults().intValue() - results.size();

	      if(remainder > 0){
		  //add the local result to fill the list of results
		  Iterator it = localResults.iterator();
		  for(int i =0; i < remainder && it.hasNext(); i++){
		      results.add(it.next());
		  }
	      }
	  }else{// add all the results returned by the children.
	      for (Iterator i=localResults.iterator(); i.hasNext(); )
		  results.add(i.next());    
	  }

	 
	  if   ((System.currentTimeMillis() >= deadline) || (children.size() == 0)) {
		  ACLMessage inform = request.createReply();
		  inform.setPerformative(ACLMessage.INFORM);
		  ResultPredicate r = new ResultPredicate();
		  r.set_0(action);
		  for (int i=0; i<results.size(); i++)
		      r.add_1(results.get(i));
		  ArrayList tuple = new ArrayList(1);
		  tuple.add(r);
		  fillMsgContent(inform,tuple); 
		  send(inform);
		  //kill the behaviours in children
	      } 
      }
  }
   
    //performs the ShowGui action: show the GUI of the DF.
    protected void showGuiAction(Action a) throws FailureException{
	//no AGREE sent
	if (!showGui()){
	    throw new FailureException("Gui_is_being_shown_already");
	}
    }

    //this method return an ACLMessage that will be sent to the applet to know the parent with which this df is federated.
    protected ACLMessage getParentAction(Action a,ACLMessage request)throws FailureException{
	try {
	    	    
	    ACLMessage inform = request.createReply();
	    inform.setPerformative(ACLMessage.INFORM);

	    ResultPredicate rp = new ResultPredicate();
	    rp.set_0(a);
	    for(int i = 0; i<parents.size() ;i++)
	      	rp.add_1(parents.get(i));
	    ArrayList list = new ArrayList(1);
	    list.add(rp);
	    fillMsgContent(inform,list);

	    return inform;
	    
	}
	catch(FIPAException e) { //FIXME no exception predicate in the DFAppletManagement ontology
	    throw new InternalError("Impossible_to_provide_the_needed_information");
	}
    }


    //Returns the description of this df. 
    //Used to reply to a request from the applet  
    protected ACLMessage getDescriptionOfThisDFAction(Action a,ACLMessage request) throws FailureException{

        try{
      	  ACLMessage inform = request.createReply();      
          inform.setPerformative(ACLMessage.INFORM);

	  ResultPredicate rp = new ResultPredicate();
	  rp.set_0(a);
	  rp.add_1(thisDF);
	  ArrayList list = new ArrayList(1);
	  list.add(rp);
	  fillMsgContent(inform,list);

	  return inform;
	  
	  }catch(FIPAException e) { //FIXME no exception predicate in the DFAppletManagement ontology
	   throw new InternalError("Impossible_to_provide_the_needed_information");
       }
    }
 
   
 	//this behaviour send the reply to the dfproxy	
    private class ThirdStep extends SimpleBehaviour
    {
	private boolean finished = false;
	private GUIRequestDFServiceBehaviour previousStep;
	private String token;
	private ACLMessage request;
	ThirdStep(GUIRequestDFServiceBehaviour b, String action,ACLMessage msg)
	{
	    super(df.this);
	    previousStep = b;
	    token = action;
	    request = msg;
	}
	
	public void action()
	{
	    System.out.println("Agent: " + getName() + "in ThirdStep...Token: " + token);
	    ACLMessage reply = request.createReply();
	    if(previousStep.correctly)
		{
		    if(token.equalsIgnoreCase(DFAppletManagementOntology.SEARCHON))
			
			try{	
			    reply.setPerformative(ACLMessage.INFORM); 
			    List l = extractMsgContent(request);
			    Action a = (Action)l.get(0);
			    
			    // Convert search result from array to list
			    Object[] r = previousStep.getSearchResults();
			    List result = new ArrayList();
			    for (int i = 0; i < r.length; ++i) {
				result.add(r[i]);
			    }
			    
			    ResultPredicate rp = new ResultPredicate();
			    rp.set_0(a);
			    
			    for (int i=0; i<result.size(); i++)
				rp.add_1(result.get(i));
			    
			    result.clear();
			    result.add(rp);
			    fillMsgContent(reply,result);
			}catch(FIPAException e){ //FIXME no exception predicate in the DFAppletManagement ontology
			    reply.setPerformative(ACLMessage.FAILURE);
			    reply.setContent("( ( action " + myAgent.getLocalName() + " "+ token + " )" +" action_not_possible )");
			}catch(RequestFIPAServiceBehaviour.NotYetReady nyr){
			    reply.setPerformative(ACLMessage.FAILURE);
			    reply.setContent("( ( action " + myAgent.getLocalName() + " "+ token + " )" +" action_not_possible )");
			}
		    
		    else
			{
			    reply.setPerformative(ACLMessage.INFORM);
			    reply.setContent("( done (" + token + " ) )" );
			}
		}
	    else
		{
		    reply.setPerformative(ACLMessage.FAILURE);
		    reply.setContent("( ( action " + myAgent.getLocalName() + " "+ token + " )" +" action_not_possible )");
		}
	    send(reply);
	    finished = true;
	}			
	
	
	public boolean done()
	{
	    return finished;
	}
	
    }

    // request another DF to federate this DF (require by the applet)
    protected void federateWithAction(Action a, ACLMessage request){
	FederateWithBehaviour fwb = new FederateWithBehaviour(a,request);
	addBehaviour(fwb);
    }

  //This behaviour allows the federation of this df with another df required by the APPLET
  private class FederateWithBehaviour extends SequentialBehaviour {

    			
      FederateWithBehaviour(Action action, ACLMessage msg)
      {
	  super(df.this);

	  String token = DFAppletManagementOntology.FEDERATEWITH;
 			
	  try{
	    	      
	      Federate f = (Federate)action.getAction(); 	
	      AID parentDF = (AID)f.getParentDF();
	      DFAgentDescription dfd = (DFAgentDescription)f.getChildrenDF();
	      //send request to parentDF
	      GUIRequestDFServiceBehaviour secondStep = new GUIRequestDFServiceBehaviour(parentDF,FIPAAgentManagementOntology.REGISTER,dfd,null,gui);
	      addSubBehaviour(secondStep);
	      
	      addSubBehaviour(new ThirdStep(secondStep,token,msg));
	      
	  }catch(FIPAException e){
	      //FIXME: set the content of the failure message
	      System.err.println(e.getMessage());
	      ACLMessage failure = msg.createReply();
	      failure.setPerformative(ACLMessage.FAILURE);
	      msg.setContent(createExceptionalMsgContent(action, e)); 
	      send(failure);
	  }
	  
      }
      
  }//end FederateWithBehaviour
    
    
    //This returns the description of this df. 
    //It is used to reply to a request from the applet 
    protected ACLMessage getDescriptionUsedAction(Action a, ACLMessage request) throws FailureException{
	try{
      	  
	    GetDescriptionUsed act = (GetDescriptionUsed)a.getAction();
	    AID parent = (AID)act.getParentDF();
	        
	    ACLMessage inform = request.createReply();      
	    inform.setPerformative(ACLMessage.INFORM);
       
	    ResultPredicate rp = new ResultPredicate();
	    rp.set_0(a);
	    rp.add_1(dscDFParentMap.get(parent));
	    
	    ArrayList list = new ArrayList(1);
	    list.add(rp);
	    fillMsgContent(inform,list);
	    return inform;

       }catch(FIPAException e) { //FIXME no exception predicate in the DFAppletManagement ontology
	   throw new InternalError("Impossible_to_provide_the_needed_information");
       }
    }


    protected void deregisterFromAction(Action a, ACLMessage request){
	DeregisterFromBehaviour dfb = new DeregisterFromBehaviour(a,request);
	addBehaviour(dfb);
    }
   
  //This behaviour allow the applet to required the df to deregister itself from a parent of the federation
  private class DeregisterFromBehaviour extends SequentialBehaviour{
     
      DeregisterFromBehaviour(Action action, ACLMessage msg)
      {
	  String token = DFAppletManagementOntology.DEREGISTERFROM;
 			
	  try{
	     
	      DeregisterFrom f = (DeregisterFrom)action.getAction(); 	
	      AID parentDF = (AID)f.getParentDF();
	      DFAgentDescription dfd = (DFAgentDescription)f.getChildrenDF();
	      //send request to parentDF
	      GUIRequestDFServiceBehaviour secondStep = new GUIRequestDFServiceBehaviour(parentDF,FIPAAgentManagementOntology.DEREGISTER,dfd,null,gui);
	      addSubBehaviour(secondStep);
	      
	      addSubBehaviour(new ThirdStep(secondStep,token,msg));
	      
	  }catch(FIPAException e){ //FIXME no exception predicate in the DFAppletManagement ontology
	      //FIXME: send a failure
	      ACLMessage failure = msg.createReply();
	      failure.setPerformative(ACLMessage.FAILURE);
	      failure.setContent(createExceptionalMsgContent(action, e)); 
	      send(failure); 
	      System.err.println(e.getMessage());
	  }
	  
      }
      
   } // End of DeregisterFromBehaviour
  

    protected void registerWithAction(Action a, ACLMessage request){
	RegisterWithBehaviour rwb = new RegisterWithBehaviour(a,request);
	addBehaviour(rwb);
    }

  //This behaviour allow the applet to require the df to register an agent with another df federated with it
  private class RegisterWithBehaviour extends SequentialBehaviour{

      RegisterWithBehaviour(Action a, ACLMessage msg){
 		
	  String token = DFAppletManagementOntology.REGISTERWITH;
 		
	  try{
             
	      RegisterWith rf = (RegisterWith)a.getAction(); 	
	      AID df = rf.getDf();
	      DFAgentDescription dfd = rf.getDescription();
	      //send request to the DF indicated
	      GUIRequestDFServiceBehaviour secondStep = new GUIRequestDFServiceBehaviour(df,FIPAAgentManagementOntology.REGISTER,dfd,null,gui);
	      addSubBehaviour(secondStep);
	      
	      addSubBehaviour(new ThirdStep(secondStep,token,msg));
	      
	  }catch(FIPAException e){ //FIXME no exception predicate in the DFAppletManagement ontology
	      //FIXME: send a failure
	      ACLMessage failure = msg.createReply();
	      failure.setPerformative(ACLMessage.FAILURE);
	      failure.setContent(createExceptionalMsgContent(a, e)); 
	      send(failure); 
	      System.err.println(e.getMessage());
	  }
	  
      }
     
  } // End of RegisterWithBehaviour
  
    protected void modifyOnAction(Action a, ACLMessage request){
	ModifyOnBehaviour mob = new ModifyOnBehaviour(a, request);
	addBehaviour(mob);
    }

  //This behaviour allow the applet to require the df to modify the DFAgentDescription of an agent register with another df
  private class ModifyOnBehaviour extends SequentialBehaviour{

      
      ModifyOnBehaviour(Action a, ACLMessage msg){
	  String token = DFAppletManagementOntology.MODIFYON;

	  try{

	      ModifyOn mod = (ModifyOn)a.getAction(); 	
	      AID df = mod.getDf();
	      DFAgentDescription dfd = mod.getDescription();
	      //send request to the DF indicated
	      GUIRequestDFServiceBehaviour secondStep = new GUIRequestDFServiceBehaviour(df,FIPAAgentManagementOntology.MODIFY,dfd,null,gui);
	      addSubBehaviour(secondStep);
	      
	      addSubBehaviour(new ThirdStep(secondStep,token,msg));
	      
	  }catch(FIPAException e){ //FIXME no exception predicate in the DFAppletManagement ontology
	      // send a failure
	      ACLMessage failure = msg.createReply();
	      failure.setPerformative(ACLMessage.FAILURE);
	      failure.setContent(createExceptionalMsgContent(a, e)); 
	      send(failure); 
	      System.err.println(e.getMessage());
	  }
	  
      }   		
  } // End of ModifyOnBehaviour
  
    protected void searchOnAction(Action a, ACLMessage request){
	SearchOnBehaviour sob = new SearchOnBehaviour(a,request);
	addBehaviour(sob);
    }
  
//this class is  used to request an agent to perform a search. Used for the applet.
  private class SearchOnBehaviour extends SequentialBehaviour{
      
      SearchOnBehaviour(Action a, ACLMessage msg)
      {
	  
	 String token = DFAppletManagementOntology.SEARCHON;

	  try{
	      SearchOn s = (SearchOn)a.getAction(); 	
	      AID df = s.getDf();
	      DFAgentDescription dfd = s.getDescription();
	      SearchConstraints sc = s.getConstraints();
	      
	      //send request to the DF
	      GUIRequestDFServiceBehaviour secondStep = new GUIRequestDFServiceBehaviour(df,FIPAAgentManagementOntology.SEARCH,dfd,sc,gui);
	      addSubBehaviour(secondStep);
	      
	      addSubBehaviour(new ThirdStep(secondStep,token,msg));
	      
	  }catch(FIPAException e){ //FIXME no exception predicate in the DFAppletManagement ontology
	      //FIXME: send a failure
	      // send a failure
	      ACLMessage failure = msg.createReply();
	      failure.setPerformative(ACLMessage.FAILURE);
	      failure.setContent(createExceptionalMsgContent(a, e)); 
	      send(failure); 
	      System.err.println(e.getMessage());
	  }
      }
      		
  } // End of SearchOnBehaviour

  /**
  All the actions requested via the DFGUI to another df extends this behaviour
  **/
  private class GUIRequestDFServiceBehaviour extends RequestFIPAServiceBehaviour
  {
    String actionName;
    DFGUIInterface gui;
    AID receiverDF;
    DFAgentDescription dfd;
    boolean correctly = false; //used to verify if the protocol finish correctly
    
  	GUIRequestDFServiceBehaviour(AID receiverDF, String actionName, DFAgentDescription dfd, SearchConstraints constraints, DFGUIInterface gui) throws FIPAException{
  		
  		super(df.this,receiverDF,actionName,dfd,constraints);
  		
  		this.actionName = actionName;
  		this.gui = gui;
  		this.receiverDF = receiverDF;
  		this.dfd = dfd;
  	}
  	
  	protected void handleInform(ACLMessage msg)
  	{
	    super.handleInform(msg);
  		correctly =true;
  		if(actionName.equalsIgnoreCase(FIPAAgentManagementOntology.SEARCH))
  		{
  			try{
  				if(gui != null)
  				{ //the applet can request a search on a different df so the gui can be null
  					//the lastSearchResult table is update also in this case.
  					gui.showStatusMsg("Search request Processed. Ready for new request");
  					// Convert search result from array to list
  					Object[] r = getSearchResults();
  					List result = new ArrayList();
  					for (int i = 0; i < r.length; ++i) {
  						result.add(r[i]);
  					}
  				  gui.refreshLastSearchResults(result, msg.getSender());
  				}
  			}catch (Exception e){
  			e.printStackTrace();// should never happen
  			}
  		}
  		else
  		if(actionName.equalsIgnoreCase(FIPAAgentManagementOntology.REGISTER))
  		{
  			try{
  			  
  				if(gui != null) 
  					//this control is needed since this behaviour is used to handle the registration request by an applet.
  					//so the gui can be null and an exception can be thrown.	
  				  gui.showStatusMsg("Request Processed. Ready for new request");
  				
  				if(dfd.getName().equals(df.this.getAID()))
  				{ //if what I register is  myself then I have federated with a parent
  					addParent(receiverDF,dfd);
  				}
  			}catch (Exception e){
  			e.printStackTrace();// should never happen
  			}
  		}
  		else
  		if(actionName.equalsIgnoreCase(FIPAAgentManagementOntology.DEREGISTER))
  		{
  			try
  			{
  				//this control is needed since the request could be made by the applet.
  				if(gui != null)
  				   gui.showStatusMsg("Deregister request Processed. Ready for new request");
			       // this behaviour is never used to deregister an agent of this DF
			       // but only to deregister a parent or an agent that was registered with
			       // one of my parents or my children
  			    if(dfd.getName().equals(df.this.getAID()))
			      { 
			        //I deregister myself from a parent
			        removeParent(receiverDF);
			      }
  			    else
			      { 
			      	if(gui != null) //the deregistration can be requested by the applet
			         gui.removeSearchResult(dfd.getName());
			      }
  			}catch (Exception e){
  			e.printStackTrace();// should never happen
  			}
  		}
  		else 
  		if(actionName.equalsIgnoreCase(FIPAAgentManagementOntology.MODIFY))
  		{
  			try{
  				gui.showStatusMsg("Modify request processed. Ready for new request");
  			}catch(Exception e){
  			e.printStackTrace();
  			}
  		}

  	}
  	
  	protected void handleRefuse(ACLMessage msg)
      {
	  super.handleRefuse(msg);
	    try{
		gui.showStatusMsg("Request Refused: " + msg.getContent());
	    }catch(Exception e)
  		{}
  	}
  	
  	protected void handleFailure(ACLMessage msg)
  	{
	    super.handleFailure(msg);
  		try{
  		gui.showStatusMsg("Request Failed: " + msg.getContent());
  		}catch(Exception e){}
  	}
  	
  	protected void handleNotUnderstood(ACLMessage msg)
  	{
	    super.handleNotUnderstood(msg);
  		try{
  			gui.showStatusMsg("Request not understood: " + msg.getContent());
  		}catch(Exception e){}
  	}

      protected void handleOutOfSequence(ACLMessage msg){
	  super.handleOutOfSequence(msg);
	  try{
	      //the receiver replied with an out of sequence message.
	      gui.showStatusMsg("Out of sequence response." );
	 } catch(Exception e){}
      }

      //called when the timeout is expired.
      protected void handleAllResponses(Vector reply){
	  super.handleAllResponses(reply);
	  try{
	      if(reply.size() == 0)
		  gui.showStatusMsg("Timeout expired for request");
	  }catch(Exception e){}
      }
  }
  
  private static int NUMBER_OF_AGENT_FOUND = 1000;


   
   /**
  @serial
  */
  private List children = new ArrayList();
  /**
  @serial
  */

  private List parents = new ArrayList();
  
  /**
  @serial
  */
  private HashMap dscDFParentMap = new HashMap(); //corrispondence parent --> dfd description (of this df) used to federate.
  /**

  @serial
  */
  private DFGUIInterface gui;

  // Current description of the df
  /**
  @serial
  */
  private DFAgentDescription thisDF = null;
  
    private DFFipaAgentManagementBehaviour fipaRequestResponder;
    private DFJadeAgentManagementBehaviour jadeRequestResponder;
    private DFAppletManagementBehaviour appletRequestResponder;
  
  /**
    This constructor creates a new <em>DF</em> agent. This can be used
    to create additional <em>DF</em> agents, beyond the default one
    created by <em><b>JADE</b></em> on platform startup.
  */
    public df() {
	MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),MessageTemplate.MatchOntology(FIPAAgentManagementOntology.NAME));

    // Associate each DF action name with the behaviour to execute
    // when the action is requested in a 'request' ACL message

    fipaRequestResponder = new DFFipaAgentManagementBehaviour(this,mt);

    // Behaviour to deal with the GUI
    
    MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),MessageTemplate.MatchOntology(JADEAgentManagementOntology.NAME));
    jadeRequestResponder = new DFJadeAgentManagementBehaviour(this,mt1);

    MessageTemplate mt2 = MessageTemplate.and(
                             MessageTemplate.MatchOntology(DFAppletManagementOntology.NAME),
    	                     MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
    appletRequestResponder = new DFAppletManagementBehaviour(this,mt2);

  }


    /**
     * Create the content for a so-called "exceptional" message, i.e.
     * one of NOT_UNDERSTOOD, FAILURE, REFUSE message
     * @param a is the Action that generated the exception
     * @param e is the generated Exception
     * @return a String containing the content to be sent back in the reply
     * message; in case an exception is thrown somewhere, the method
     * try to return anyway a valid content with a best-effort strategy
     **/
    //FIXME. This method is only used for create the reply to the APPLET request.
    private String createExceptionalMsgContent(Action a, FIPAException e) {
	ACLMessage temp = new ACLMessage(ACLMessage.NOT_UNDERSTOOD); 
	temp.setLanguage(SL0Codec.NAME);
	temp.setOntology(FIPAAgentManagementOntology.NAME);
	List l = new ArrayList(2);
	if (a == null) {
	    a = new Action();
	    a.set_0(getAID());
	    a.set_1("UnknownAction");
	}
	l.add(a);
	l.add(e);
	try {
	    fillMsgContent(temp,l);
	} catch (Exception ee) { // in any case try to return some good content
	    return e.getMessage();
	} 
	return temp.getContent();
    }


  /**
    This method starts all behaviours needed by <em>DF</em> agent to
    perform its role within <em><b>JADE</b></em> agent platform.
  */
  protected void setup() {
    // register the codec of the language
    registerLanguage(SL0Codec.NAME,new SL0Codec());	
		
    // register the ontology used by application
    registerOntology(FIPAAgentManagementOntology.NAME, FIPAAgentManagementOntology.instance());
    registerOntology(JADEAgentManagementOntology.NAME, JADEAgentManagementOntology.instance());
    registerOntology(DFAppletManagementOntology.NAME,DFAppletManagementOntology.instance());

    // Add a message dispatcher behaviour
    addBehaviour(fipaRequestResponder);
   
    addBehaviour(jadeRequestResponder);

    addBehaviour(appletRequestResponder);
    setDescriptionOfThisDF(getDefaultDescription());
   
  }  // End of method setup()

	/**
	  This method make visible the GUI of the DF.
	  @return true if the GUI was not visible already, false otherwise.
	*/
  public boolean showGui() {
   if (gui == null) 
  		{
		 
  			try{
  				Class c = Class.forName("jade.tools.dfgui.DFGUI");
  			  gui = (DFGUIInterface)c.newInstance();
		      gui.setAdapter(df.this); //this method must be called to avoid reflection (the constructor of the df gui has no parameters).		
  			  DFAgentDescription matchEverything = new DFAgentDescription();
		      List agents = agentDescriptions.search(matchEverything);
		      List AIDList = new ArrayList();
		      Iterator it = agents.iterator();
		      while(it.hasNext())
		      	AIDList.add(((DFAgentDescription)it.next()).getName());
		    
		      gui.refresh(AIDList.iterator(), parents.iterator(), children.iterator());
		      gui.setVisible(true);
		      return true;
  			
  			}catch(Exception e){e.printStackTrace();}
  		}
 
   return false;
  }
   
 


  /**
    Cleanup <em>DF</em> on exit. This method performs all necessary
    cleanup operations during agent shutdown.
  */
  protected void takeDown() {

    if(gui != null)
	gui.disposeAsync();
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    Iterator it = parents.iterator();
    while(it.hasNext()) {
      AID parentName = (AID)it.next();
      try {
        DFService.deregister(this, parentName, dfd);
      }
      catch(FIPAException fe) {
        fe.printStackTrace();
      }
    }
  }

  private boolean isADF(DFAgentDescription dfd) {
  	try {
  		return ((ServiceDescription)dfd.getAllServices().next()).getType().equalsIgnoreCase("fipa-df");
  	} catch (Exception e) {
  		return false;
  	}
  }
  
  /**
  * checks that all the mandatory slots for a register/modify/deregister action
  * are present.
  * @param actionName is the name of the action (one of 
  * <code>FIPAAgentManagementOntology.REGISTER</code>,
  * <code>FIPAAgentManagementOntology.MODIFY</code>,
  * <code>FIPAAgentManagementOntology.DEREGISTER</code>)
  * @param dfd is the DFAgentDescription to be checked for
  * @throws MissingParameter if one of the mandatory slots is missing
  **/
  void checkMandatorySlots(String actionName, DFAgentDescription dfd) throws MissingParameter {
  	try {
  	  if (dfd.getName().getName().length() == 0)
  		  throw new MissingParameter(FIPAAgentManagementOntology.DFAGENTDESCRIPTION, "name");
  	} catch (Exception e) {
  		throw new MissingParameter(FIPAAgentManagementOntology.DFAGENTDESCRIPTION, "name");
  	}
  	if (!actionName.equalsIgnoreCase(FIPAAgentManagementOntology.DEREGISTER))
  	 for (Iterator i=dfd.getAllServices(); i.hasNext();) {
  		ServiceDescription sd =(ServiceDescription)i.next();
  		try {
  		  if (sd.getName().length() == 0)
  		   throw new MissingParameter(FIPAAgentManagementOntology.SERVICEDESCRIPTION, "name");
  	  } catch (Exception e) {
  		   throw new MissingParameter(FIPAAgentManagementOntology.SERVICEDESCRIPTION, "name");
  	  }
  	  try {
  		  if (sd.getType().length() == 0)
  		   throw new MissingParameter(FIPAAgentManagementOntology.SERVICEDESCRIPTION, "type");
  	  } catch (Exception e) {
  		   throw new MissingParameter(FIPAAgentManagementOntology.SERVICEDESCRIPTION, "type");
  	  }
  	 } //end of for
  }
  
  /**
  @serial
  */
  private KB agentDescriptions = new KBAbstractImpl() {
      protected boolean match(Object template, Object fact) {

	try {
	  DFAgentDescription templateDesc = (DFAgentDescription)template;
	  DFAgentDescription factDesc = (DFAgentDescription)fact;

	  // Match name
	  AID id1 = templateDesc.getName();
	  if(id1 != null) {
	    AID id2 = factDesc.getName();
	    if((id2 == null) || (!matchAID(id1, id2)))
	      return false;
	  }

	  // Match protocol set
	  Iterator itTemplate = templateDesc.getAllProtocols();
	  while(itTemplate.hasNext()) {
	    String templateProto = (String)itTemplate.next();
	    boolean found = false;
	    Iterator itFact = factDesc.getAllProtocols();
	    while(!found && itFact.hasNext()) {
	      String factProto = (String)itFact.next();
	      found = templateProto.equalsIgnoreCase(factProto);
	    }
	    if(!found)
	      return false;
	  }

	  // Match ontologies set
	  itTemplate = templateDesc.getAllOntologies();
	  while(itTemplate.hasNext()) {
	    String templateOnto = (String)itTemplate.next();
	    boolean found = false;
	    Iterator itFact = factDesc.getAllOntologies();
	    while(!found && itFact.hasNext()) {
	      String factOnto = (String)itFact.next();
	      found = templateOnto.equalsIgnoreCase(factOnto);
	    }
	    if(!found)
	      return false;
	  }

	  // Match languages set
	  itTemplate = templateDesc.getAllLanguages();
	  while(itTemplate.hasNext()) {
	    String templateLang = (String)itTemplate.next();
	    boolean found = false;
	    Iterator itFact = factDesc.getAllLanguages();
	    while(!found && itFact.hasNext()) {
	      String factLang = (String)itFact.next();
	      found = templateLang.equalsIgnoreCase(factLang);
	    }
	    if(!found)
	      return false;
	  }

	  // Match services set
	  itTemplate = templateDesc.getAllServices();
	  while(itTemplate.hasNext()) {
	    ServiceDescription templateSvc = (ServiceDescription)itTemplate.next();
	    boolean found = false;
	    Iterator itFact = factDesc.getAllServices();
	    while(!found && itFact.hasNext()) {
	      ServiceDescription factSvc = (ServiceDescription)itFact.next();
	      found = matchServiceDesc(templateSvc, factSvc);
	    }
	    if(!found)
	      return false;
	  }

	  return true;
	}
	catch(ClassCastException cce) {
	  return false;
	}
      }
    };

       
    void DFRegister(DFAgentDescription dfd) throws AlreadyRegistered {
	
	//checkMandatorySlots(FIPAAgentManagementOntology.REGISTER, dfd);
	
	Object old = agentDescriptions.register(dfd.getName(), dfd);
	if(old != null)
	    throw new AlreadyRegistered();
	
	if (isADF(dfd)) {
	    children.add(dfd.getName());
	    try {
    		gui.addChildren(dfd.getName());
	    } catch (Exception ex) {}
	}
	try{ //refresh the GUI if shown, exception thrown if the GUI was not shown
	    gui.addAgentDesc(dfd.getName());
	    gui.showStatusMsg("Registration of agent: " + dfd.getName().getName() + " done.");
	}catch(Exception ex){}
	
    }

    //this method is called into the prepareResponse of the DFFipaAgentManagementBehaviour to perform a Deregister action
    
    void DFDeregister(DFAgentDescription dfd) throws NotRegistered {
	//checkMandatorySlots(FIPAAgentManagementOntology.DEREGISTER, dfd);
      
      Object old = agentDescriptions.deregister(dfd.getName());
      if(old == null)
	  throw new NotRegistered();
      
      
      if (children.remove(dfd.getName()))
	  try {
	      gui.removeChildren(dfd.getName());
	  } catch (Exception e) {}
      try{ 
	  // refresh the GUI if shown, exception thrown if the GUI was not shown
	  // this refresh must be here, otherwise the GUI is not synchronized with 
	  // registration/deregistration made without using the GUI
	  gui.removeAgentDesc(dfd.getName(),df.this.getAID());
	  gui.showStatusMsg("Deregistration of agent: " + dfd.getName().getName() +" done.");
      }catch(Exception e1){}	
    }
    

    
    void DFModify(DFAgentDescription dfd) throws NotRegistered {
	//	checkMandatorySlots(FIPAAgentManagementOntology.MODIFY, dfd);
	
	Object old = agentDescriptions.deregister(dfd.getName());
	if(old == null)
	    throw new NotRegistered();
	agentDescriptions.register(dfd.getName(), dfd);    
	try{
	    gui.removeAgentDesc(dfd.getName(), df.this.getAID());
	    gui.addAgentDesc(dfd.getName());
	    gui.showStatusMsg("Modify of agent: "+dfd.getName().getName() + " done.");
	}catch(Exception e){}
	
    }

  List DFSearch(DFAgentDescription dfd, SearchConstraints constraints, ACLMessage reply){
    // Search has no mandatory slots
  	
    return agentDescriptions.search(dfd);
    
  }
	
	// AGENT DATA MODIFICATIONS FOLLOWING GUI EVENTS
	protected void onGuiEvent(GuiEvent ev)
	{
		try
		{
		
			switch(ev.getType()) 
			{
			case DFGUIAdapter.EXIT:
				gui.disposeAsync();
				gui = null;
				doDelete();
				break;
			case DFGUIAdapter.CLOSEGUI:
				gui.disposeAsync();
				gui = null;
				break;
			case DFGUIAdapter.REGISTER:
		
				if (ev.getParameter(0).equals(getName()) || ev.getParameter(0).equals(getLocalName())) 
				{
					// Register an agent with this DF
				    DFAgentDescription dfd = (DFAgentDescription)ev.getParameter(1);
				    checkMandatorySlots(FIPAAgentManagementOntology.REGISTER, dfd);
				    DFRegister(dfd);
					
				}
				else 
				{
				  // Register an agent with another DF. 
				  try
				    {
				      gui.showStatusMsg("Process your request & waiting for result...");
				      addBehaviour(new GUIRequestDFServiceBehaviour((AID)ev.getParameter(0),FIPAAgentManagementOntology.REGISTER,(DFAgentDescription)ev.getParameter(1),null,gui));
				    }catch (FIPAException fe) {
				      fe.printStackTrace(); //it should never happen
				    } catch(Exception ex){} //Might happen if the gui has been closed
				}
				break;
			case DFGUIAdapter.DEREGISTER:

				if(ev.getParameter(0).equals(getName()) || ev.getParameter(0).equals(getLocalName())) 
				{
					// Deregister an agent with this DF
				    DFAgentDescription dfd = (DFAgentDescription)ev.getParameter(1);
				    checkMandatorySlots(FIPAAgentManagementOntology.DEREGISTER, dfd);
				    DFDeregister(dfd);
					
				}
				else 
				{
					// Deregister an agent with another DF. 
				try
		 		{
		  	   gui.showStatusMsg("Process your request & waiting for result...");
		  		 addBehaviour(new GUIRequestDFServiceBehaviour((AID)ev.getParameter(0),FIPAAgentManagementOntology.DEREGISTER,(DFAgentDescription)ev.getParameter(1),null,gui));
		 		}catch (FIPAException fe) {
		 			fe.printStackTrace(); //it should never happen
		 			} catch(Exception ex){} //Might happen if the gui has been closed
				}
				break;
			case DFGUIAdapter.MODIFY:
				
				if(ev.getParameter(0).equals(getName()) || ev.getParameter(0).equals(getLocalName())) 
				{
					// Modify the description of an agent with this DF
				    DFAgentDescription dfd = (DFAgentDescription)ev.getParameter(1);
				    checkMandatorySlots(FIPAAgentManagementOntology.MODIFY, dfd);
				    DFModify(dfd);
					
				}
				else 
				{
					// Modify the description of an agent with another DF
					try{
						gui.showStatusMsg("Process your request & waiting for result..");
						addBehaviour(new GUIRequestDFServiceBehaviour((AID)ev.getParameter(0), FIPAAgentManagementOntology.MODIFY, (DFAgentDescription)ev.getParameter(1),null,gui));
					}catch(FIPAException fe1){
						fe1.printStackTrace();
					}//it should never happen
		 			catch(Exception ex){} //Might happen if the gui has been closed
				}
				break;
		  case DFGUIAdapter.SEARCH:
		  	 
		  	try{
		  		gui.showStatusMsg("Process your request & waiting for result...");
	  		  addBehaviour(new GUIRequestDFServiceBehaviour((AID)ev.getParameter(0),FIPAAgentManagementOntology.SEARCH,(DFAgentDescription)ev.getParameter(1),(SearchConstraints)ev.getParameter(2),gui));
	  	  }catch(FIPAException fe){
	  	   fe.printStackTrace();
	  	  }catch(Exception ex1){} //Might happen if the gui has been closed.
		  	 
		  	break;
		 	case DFGUIAdapter.FEDERATE:
		 		try
		 		{
		  	   gui.showStatusMsg("Process your request & waiting for result...");
		  	   
		  	   if(ev.getParameter(0).equals(getAID()) || ev.getParameter(0).equals(getLocalName()))
		 	  		gui.showStatusMsg("Self Federation not allowed");
		  		else
		  		  addBehaviour(new GUIRequestDFServiceBehaviour((AID)ev.getParameter(0),FIPAAgentManagementOntology.REGISTER,(DFAgentDescription)ev.getParameter(1),null,gui));
		 		}catch (FIPAException fe) {
		 			fe.printStackTrace(); //it should never happen
		 			} catch(Exception ex){} //Might happen if the gui has been closed
		  	  
		  	
		 		break;
		 	
		 
			} // END of switch
		} // END of try
		catch(FIPAException fe) 
		{
			fe.printStackTrace();
		
		}
	}

	
	/**
	This method returns the descriptor of an agent registered with the df.
	*/
	public DFAgentDescription getDFAgentDsc(AID name) throws FIPAException
	{
	  DFAgentDescription template = new DFAgentDescription();
	  template.setName(name);
	  List l = agentDescriptions.search(template);
	  if(l.isEmpty())
	    return null;
	  else
	    return (DFAgentDescription)l.get(0);
	}

	/**
  * This method creates the DFAgent descriptor for this df used to federate with other df.
	*/
	private DFAgentDescription getDefaultDescription()
	{
	  	DFAgentDescription out = new DFAgentDescription();
	
			out.setName(getAID());
			out.addOntologies(FIPAAgentManagementOntology.NAME);
			out.addLanguages(SL0Codec.NAME);
			out.addProtocols("fipa-request");
			ServiceDescription sd = new ServiceDescription();
			sd.setName("df-service");
			sd.setType("fipa-df");
			sd.addOntologies(FIPAAgentManagementOntology.NAME);
			sd.addLanguages(SL0Codec.NAME);
			sd.addProtocols("fipa-request");
      try{
		  	sd.setOwnership(InetAddress.getLocalHost().getHostName());
		  }catch (java.net.UnknownHostException uhe){
		  	sd.setOwnership("unknown");}
		  
		  out.addServices(sd);
		  
		  return out;
	}

	
	/**
	* This method set the description of the df according to the DFAgentAgentDescription passed.
	* The programmers can call this method to provide a different initialization of the description of the df they are implemented.
	* The method is called inside the setup of the agent and set the df description using a default description.
	*/
	public void setDescriptionOfThisDF(DFAgentDescription dfd)
	{
		thisDF = dfd;
	}
	/**
	* This method returns the current description of this DF
	*/
	public DFAgentDescription getDescriptionOfThisDF()
	{
	    thisDF.setName(getAID());
	    return thisDF;
	}
	
	/**
	* This method returns the description of this df used to federate with the given parent
	*/
	public DFAgentDescription getDescriptionOfThisDF(AID parent)
	{
		return (DFAgentDescription)dscDFParentMap.get(parent);
	}
	
	/**
	* This method can be used to add a parent (a DF with which the this DF is federated). 
	* @param dfName the parent df (the df with which this df has been registered)
	* @param dfd the description used by this df to register with the parent.
	*/
	public void addParent(AID dfName, DFAgentDescription dfd)
	{
	  parents.add(dfName);
	  if(gui != null) // the gui can be null if this method is called in order to manage a request made by the df-applet.
	    gui.addParent(dfName);
    dscDFParentMap.put(dfName,dfd); //update the table of corrispondence between parents and description of this df used to federate.

	}
	
	/**
	this method can be used to remove a parent (a DF with which this DF is federated).
	*/
	public void removeParent(AID dfName)
	{
		parents.remove(dfName); 
		if(gui != null) //the gui can be null is this method is called in order to manage a request from the df applet
		  gui.removeParent(dfName);
		dscDFParentMap.remove(dfName);

	}
  

	
}
