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
import jade.content.onto.*;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import test.common.*;
import content.*;
import content.testOntology.*;

public class TestMissingMandatory extends Test{
  
  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	try {
  		final ACLMessage msg = (ACLMessage) getGroupArgument(ContentTesterAgent.MSG_NAME);
  		return new FailureExpectedInitiator(a, ds, resultKey) {
  			protected ACLMessage prepareMessage() throws Exception {
  				AbsConcept p = new AbsConcept(TestOntology.POSITION);
  				p.set(TestOntology.POSITION_X, 0.005);
  				// POSITION_Y attribute not set
  				AbsPredicate e = new AbsPredicate(TestOntology.EXISTS);
  				e.set(TestOntology.EXISTS_WHAT, p);
  				myAgent.getContentManager().fillContent(msg, e);
  				return msg;
  			}
  		};
  	}
  	catch (Exception e) {
  		throw new TestException("Wrong group argument", e);
  	}
  }

}
