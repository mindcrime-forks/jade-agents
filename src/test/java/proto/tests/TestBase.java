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

package proto.tests;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import test.common.Test;
import test.common.TestException;
import test.common.TestUtility;

/**
   @author Giovanni Caire - TILAB
 */
public abstract class TestBase extends Test {
	protected static final String RESPONDER_NAME = "responder";

	protected String[] responderBehaviours;
	private AID[] responders;
	
  protected void initialize(Agent a, ACLMessage msg) throws TestException {
		
  	responders = new AID[responderBehaviours.length];
  	for (int i = 0; i < responderBehaviours.length; ++i) {
  		String resp = RESPONDER_NAME+i;
  		AID id = TestUtility.createAgent(a, resp, TestUtility.CONFIGURABLE_AGENT, null);
  		TestUtility.addBehaviour(a, id, responderBehaviours[i]);
  		msg.addReceiver(id);
  		responders[i] = id;
  	}
  }
  
  public void clean(Agent a) {
  	try {
  		for (int i = 0; i < responders.length; ++i) {
  			TestUtility.killAgent(a, responders[i]);
  		}
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
  	// Wait a bit to give agents time to actually terminate.
  	// FIXME: The problem is that currently the AMS, when requested
  	// to kill an agent notifies the requester without waiting for
  	// the killed agent to actually terminate.
  	try {
  		Thread.sleep(500);
  	}
  	catch (InterruptedException ie) {
  		ie.printStackTrace();
  	}
  } 
  
}

