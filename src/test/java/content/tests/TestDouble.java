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

import jade.content.ContentManager;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import test.common.*;
import content.*;
import content.testOntology.*;

public class TestDouble extends Test{
	private static final double VALUE = 18923.345;

  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	final Logger l = Logger.getLogger();
  	
  	try {
  		final ACLMessage msg = (ACLMessage) getGroupArgument(ContentTesterAgent.MSG_NAME);
  		return new SuccessExpectedInitiator(a, ds, resultKey) {
  			protected ACLMessage prepareMessage() throws Exception {
  				Position p = new Position(0.05, VALUE);
  				Exists e = new Exists(p);
  				myAgent.getContentManager().fillContent(msg, e);
  				l.log("Content correctly encoded");
  				l.log(msg.getContent());
  				return msg;
  			}
  			
  			protected boolean checkReply(ACLMessage reply) throws Exception {
  				Exists e = (Exists) myAgent.getContentManager().extractContent(reply);
  				l.log("Content correctly decoded");
  				Position p = (Position) e.getWhat();
  				double value = p.getY();
  				if (value == VALUE) {
  					l.log("double value OK");
  					return true;
  				}
  				else {
  					l.log("Wrong double value: expected "+VALUE+", received "+value); 
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
