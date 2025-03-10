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

package domain.df.tests;

import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.DFGUIManagement.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import test.common.*;
import domain.df.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestFederation extends Test {
	private AID df1;
	private DFAgentDescription[] dfds;
	private static final int N_REGISTRATIONS = 3;
	  
  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	final DataStore store = ds;
  	final String key = resultKey;
 		final Codec codec = new SLCodec();
    final Logger l = Logger.getLogger();
  	
  	// Create another DF called DF1
  	df1 = TestUtility.createAgent(a, "DF1", "jade.domain.df", null, a.getAMS(), null);
  	// Register some DFDs with DF1
  	dfds = new DFAgentDescription[N_REGISTRATIONS];
  	for (int i = 0; i < N_REGISTRATIONS; ++i) {
  		dfds[i] = TestDFHelper.getSampleDFD(new AID("a"+i, AID.ISLOCALNAME));
  		try {
	  		DFService.register(a, df1, dfds[i]);
  		}
  		catch (FIPAException fe) {
  			throw new TestException("Error registering a dfd", fe);
  		}
  	}
  	// Register one of the above dfds with the default DF too.
  	// If we will find it twice it is an error
		try {
  		DFService.register(a, a.getDefaultDF(), dfds[1]);
		}
		catch (FIPAException fe) {
			throw new TestException("Error registering a dfd", fe);
		}
  	
  	
    // Register the ontologies and codec required to request the federation
    a.getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
    a.getContentManager().registerOntology(DFAppletOntology.getInstance());
    
  	// Create the REQUEST message to be sent to df1 to make it federate with the default DF
  	ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
  	request.addReceiver(df1);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		request.setOntology(DFAppletOntology.getInstance().getName());
		try {
			Federate f = new Federate();
			f.setDf(a.getDefaultDF());
			Action act = new Action(df1, f);
			a.getContentManager().fillContent(request, act);
		}
		catch (Exception e) {
  		throw new TestException("Error creating the message to request the federation", e);
		}
		
		// Create and return the behaviour that will actually perform the test
  	Behaviour b = new AchieveREInitiator(a, request, store) {
  		int ret = Test.TEST_FAILED;
  		
    	protected void handleInform(ACLMessage inform) {
    		// If the federation succeeded, search the default DF
 				try {
 					Done d = (Done) myAgent.getContentManager().extractContent(inform);
 					l.log("Federation done");
  				// Search with the DF
  				try {
  					DFAgentDescription template = TestDFHelper.getSampleTemplate1();
  					SearchConstraints constraints = new SearchConstraints();
  					constraints.setMaxDepth(new Long(1));
  					constraints.setMaxResults(new Long(-1));
	  				DFAgentDescription[] result = DFService.search(myAgent, myAgent.getDefaultDF(), template, constraints);
	  				l.log("Recursive search done");
  					if (result.length != N_REGISTRATIONS) {
  						l.log("Recursive search result NOT OK: "+result.length+" items found, while "+N_REGISTRATIONS+" were expected");
  						l.log("Items found: ");
  						for (int i = 0; i < result.length; ++i) {
  							l.log(result[i].getName().toString());
  						}
  					}
  					else {
  						l.log("Recursive search result OK");
  						ret = Test.TEST_PASSED;
  					}
  				}
  				catch (FIPAException fe) {
  					l.log("Recursive search failed");
  					fe.printStackTrace();
  				}	
 				}
 				catch (Exception e) {
  				l.log("Error decoding federation notification");
  				e.printStackTrace();
 					e.printStackTrace();
 				}
    	}
    	
    	protected void handleRefuse(ACLMessage refuse) {
    		l.log("Federation request refused: message is "+refuse);
    	}
    	
    	protected void handleNotUnderstood(ACLMessage notUnderstood) {
    		l.log("Federation request not understood: message is "+notUnderstood);
    	}
    	
    	protected void handleFailure(ACLMessage failure) {
    		l.log("Federation failed: message is "+failure);
    	}
    	
  		public int onEnd() {
  			store.put(key, new Integer(ret));
  			return 0;
  		}	
  	};
  	
  	return b;
  }
  
  public void clean(Agent a) {
  	try {
	  	TestUtility.killAgent(a, df1);
	  	DFService.deregister(a, dfds[1]);
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
  }  
}
