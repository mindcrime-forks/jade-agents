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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import test.common.*;
import domain.df.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestDFService extends Test {
	
  public String getName() {
  	return "Test DFService";
  }
  
  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	final DataStore store = ds;
  	final String key = resultKey;
  	
  	Behaviour b = new OneShotBehaviour(a) {
  		int ret;
  		
  		public void action() {
  			Logger l = Logger.getLogger();
  			ret = Test.TEST_PASSED;
  			
  			// Register with the DF
  			DFAgentDescription dfd = TestDFHelper.getSampleDFD(myAgent.getAID());
  			try {
	  			DFService.register(myAgent, myAgent.getDefaultDF(), dfd);
  			}
  			catch (FIPAException fe) {
  				l.log("DF registration failed");
  				fe.printStackTrace();
  				ret = Test.TEST_FAILED;
  				return;
  			}	
  			l.log("DF registration 1 done");
  			
  			// Search 1 with the DF 
  			DFAgentDescription template = TestDFHelper.getSampleTemplate1();
  			DFAgentDescription[] result = null;
  			try {
	  			result = DFService.search(myAgent, myAgent.getDefaultDF(), template, new SearchConstraints());
  			}
  			catch (FIPAException fe) {
  				l.log("DF search-1 failed");
  				fe.printStackTrace();
  				ret = Test.TEST_FAILED;
  				return;
  			}	
  			l.log("DF search-1 done");
  			if (result.length != 1 || (!TestDFHelper.compare(result[0], dfd))) {
  				l.log("DF search-1 result different from what was expected");
  				l.log("Number of items: "+result.length);
  				for (int i = 0; i < result.length; ++i) {
  					l.log("- "+result[i].getName());
  				}
  				ret = Test.TEST_FAILED;
  				return;
  			}
  			l.log("DF search-1 result OK");
  			
  			// Search 2 with the DF 
  			template = TestDFHelper.getSampleTemplate2();
  			try {
	  			result = DFService.search(myAgent, myAgent.getDefaultDF(), template, new SearchConstraints());
  			}
  			catch (FIPAException fe) {
  				l.log("DF search-2 failed");
  				fe.printStackTrace();
  				ret = Test.TEST_FAILED;
  				return;
  			}	
  			l.log("DF search-2 done");
  			if (result.length != 1 || (!TestDFHelper.compare(result[0], dfd))) {
  				l.log("DF search-2 result different from what was expected");
  				ret = Test.TEST_FAILED;
  				return;
  			}
  			l.log("DF search-2 result OK");
  			
  			// Search 3 with the DF 
  			template = TestDFHelper.getSampleTemplate3();
  			try {
	  			result = DFService.search(myAgent, myAgent.getDefaultDF(), template, new SearchConstraints());
  			}
  			catch (FIPAException fe) {
  				l.log("DF search-3 failed");
  				fe.printStackTrace();
  				ret = Test.TEST_FAILED;
  				return;
  			}	
  			l.log("DF search-3 done");
  			if (result.length > 0) {
  				l.log("DF search-3 failed: no result expected, found "+result.length);
  				ret = Test.TEST_FAILED;
  				return;
  			}
  			l.log("DF search-3 no result found as expected. OK");
  			
  			// Search 4 with the DF 
  			template = TestDFHelper.getSampleTemplate4();
  			try {
	  			result = DFService.search(myAgent, myAgent.getDefaultDF(), template, new SearchConstraints());
  			}
  			catch (FIPAException fe) {
  				l.log("DF search-4 failed");
  				fe.printStackTrace();
  				ret = Test.TEST_FAILED;
  				return;
  			}	
  			l.log("DF search-4 done");
  			if (result.length > 0) {
  				l.log("DF search-4 failed: no result expected, found "+result.length);
  				ret = Test.TEST_FAILED;
  				return;
  			}
  			l.log("DF search-4 no result found as expected. OK");
  			
  			// Deregister with the DF
  			try {
	  			DFService.deregister(myAgent, myAgent.getDefaultDF(), new DFAgentDescription());
  			}
  			catch (FIPAException fe) {
  				l.log("DF de-registration failed");
  				fe.printStackTrace();
  				ret = Test.TEST_FAILED;
  				return;
  			}	
  			l.log("DF de-registration done");
  			
  	  	// Search again with the DF
  			template = TestDFHelper.getSampleTemplate1();
  			try {
  				result = DFService.search(myAgent, myAgent.getDefaultDF(), template, new SearchConstraints());
  			}
  			catch (FIPAException fe) {
  				l.log("DF search-5 failed");
  				fe.printStackTrace();
  				ret = Test.TEST_FAILED;
  				return;
  			}	
  			l.log("DF search-5 done");
  			if (result.length != 0) {
  				l.log("DF search-5 failed: no result expected, found "+result.length);
 					ret = Test.TEST_FAILED;
  				return;
  			}
  			l.log("DF search-5 no result found as expected. OK");
  		}
  		
  		public int onEnd() {
  			store.put(key, new Integer(ret));
  			return 0;
  		}	
  	};
  	
  	return b;
  }
}
