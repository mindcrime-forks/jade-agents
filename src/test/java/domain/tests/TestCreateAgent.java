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

package domain.tests;

import test.common.*;
import domain.*;

import java.util.Vector;

import jade.content.*;
import jade.content.lang.*;
import jade.content.onto.basic.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.proto.*;

public class TestCreateAgent extends Test {
	private static final String TARGET = "Target";
	
  public String getName() {
  	return "CreateAgent";
  }

  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	try {
  		//Object[] args = getGroupArguments();
  		//ACLMessage msg = (ACLMessage) args[0];
  		final ACLMessage msg = (ACLMessage) getGroupArgument(JADEManagementOntologyTesterAgent.INFORM_MSG_KEY);
                final Agent fa = a;
  		Behaviour b = new SuccessExpectedInitiator(a, msg, ds, resultKey) {
    		protected Vector prepareRequests(ACLMessage request) {
    			Vector v = new Vector();
    			CreateAgent ca = new CreateAgent();
    			ca.setAgentName(TARGET);
    			ca.setClassName(TestUtility.CONFIGURABLE_AGENT);
    			ca.setContainer((ContainerID) myAgent.here());
    			Action action = new Action(fa.getAMS(), ca);
    			try {
    				myAgent.getContentManager().fillContent(request, action);
    				v.add(request);    				
    			}
    			catch (Exception e) {
    				e.printStackTrace();
    			}
    			return v;
    		}
    		
    		protected boolean check(ACLMessage inform) {
    			try {
    				// Parse the reply just to check it is correct
    				ContentElement ce = myAgent.getContentManager().extractContent(inform);
    				if (!(ce instanceof Done)) {
    					return false;
    				}
    				// Kill the target agent thus checking that it was actually there
    				TestUtility.killAgent(myAgent, new AID(TARGET, AID.ISLOCALNAME));
    				return true;
    			}
    			catch (Exception e) {
    				e.printStackTrace();
    				return false;
    			}
    		}
  		};
  		return b;
  	}
  	catch (Exception e) {
  		throw new TestException("Wrong group argument", e);
  	}
  }

}
