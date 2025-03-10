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
import jade.content.abs.*;
import jade.content.onto.BasicOntology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import test.common.*;
import content.*;
import content.testOntology.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestObject extends Test{
  
  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	final Logger l = Logger.getLogger();
  	
  	try {
  		final ACLMessage msg = (ACLMessage) getGroupArgument(ContentTesterAgent.MSG_NAME);
  		return new SuccessExpectedInitiator(a, ds, resultKey) {
  			protected ACLMessage prepareMessage() throws Exception {
  				msg.setPerformative(ACLMessage.INFORM);
  				AbsPredicate p = new AbsPredicate(TestOntology.ELEMENT);
  				p.set(TestOntology.ELEMENT_WHAT, BasicOntology.getInstance().fromObject(myAgent.getAID()));
  				myAgent.getContentManager().fillContent(msg, p);
  				l.log("Content correctly encoded");
  				l.log(msg.getContent());
  				return msg;
  			}
  			
  			protected boolean checkReply(ACLMessage reply) throws Exception {
  				AbsPredicate p = (AbsPredicate) myAgent.getContentManager().extractContent(reply);
  				l.log("Content correctly decoded");
  				AbsTerm t = p.getAbsTerm(TestOntology.ELEMENT_WHAT);
  				AID id = (AID) BasicOntology.getInstance().toObject(t);
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
