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

package content.tests;

import jade.content.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import test.common.*;
import content.*;
import content.testOntology.Exists;

public class TestOntoAID extends Test{

  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	final Logger l = Logger.getLogger();
  	
  	try {
  		final ACLMessage msg = (ACLMessage) getGroupArgument(ContentTesterAgent.MSG_NAME);
  		return new SuccessExpectedInitiator(a, ds, resultKey) {
  			protected ACLMessage prepareMessage() throws Exception {
  				Exists e = new Exists(OntoAID.wrap(myAgent.getAID()));
  				myAgent.getContentManager().fillContent(msg, e);
  				l.log("Content correctly encoded");
  				l.log(msg.getContent());
  				return msg;
  			}
  			
  			protected boolean checkReply(ACLMessage reply) throws Exception {
  				Exists e = (Exists) myAgent.getContentManager().extractContent(reply);
  				l.log("Content correctly decoded");
  				AID id = (AID) e.getWhat();
  				if (id.equals(myAgent.getAID())) {
  					l.log("Content OK");
  					return true;
  				}
  				else {
  					l.log("Wrong content: expected "+myAgent.getAID()+", found "+id);
  					return false;
  				}
  			}
  		};
  	}
  	catch (Exception e) {
  		throw new TestException("Wrong group argument", e);
  	}
  }

}
