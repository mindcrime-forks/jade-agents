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

package leap.tests;

import test.common.*;
import leap.LEAPTesterAgent;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

/**
   This Test measures the performances of the communication 
   mechanism (the round-trip time) in the wireless environment.   
   @author Giovanni Caire - TILAB
 */
public class TestPerformances extends Test {
	private static final String PING_AGENT = "ping";
	private static final String CONV_ID = "ping_conv";
	
	private String lightContainerName = "Container-1";
	private int nMessages = 30;
	private AID ping;
	
  public Behaviour load(final Agent a) throws TestException {  	
		// MIDP container name as group argument
		lightContainerName = (String) getGroupArgument(LEAPTesterAgent.LIGHT_CONTAINER_KEY);
		
		// Get the number of messages as test argument
		try {
			nMessages = Integer.parseInt(getTestArgument("n-iterations"));
		}
		catch (Exception e) {
			// Ignore and keep default
		}
		
		// Create the Ping agent on the Light container
		ping = TestUtility.createAgent(a, PING_AGENT, "test.leap.midp.PingAgent", null, a.getAMS(), lightContainerName);
		log("Ping agent correctly created.");
		
		
		Behaviour b = new SimpleBehaviour(a) {
			private MessageTemplate template = MessageTemplate.MatchConversationId(CONV_ID);
			private ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			private long initTime;
			private boolean error = false;
			private int cnt = 0;
			
			public void onStart() {
				msg.addReceiver(ping);
				msg.setContent("PING");
				msg.setConversationId(CONV_ID);

				initTime = System.currentTimeMillis();
				
				myAgent.send(msg);
			}
			
			public void action() {
				ACLMessage reply = myAgent.receive(template);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.FAILURE && reply.getSender().equals(myAgent.getAMS())) {
						failed("Ping agent does not exist.");
					}
					else {
						cnt++;
						if (cnt < nMessages) {
							myAgent.send(msg);
						}
						else {
							printResult();
							passed("");
						}
					}
				}
				else {
					block();
				}
			}
			
			public boolean done() {
				return (cnt >= nMessages);
			}
			
			private void printResult() {
				long endTime = System.currentTimeMillis();
				log("---------------------------------------------");
				log("Round trip time = "+ ((endTime - initTime) / nMessages)); 
				log("---------------------------------------------");
			}
		};
		return b;
  }
  
	public void clean(Agent a) {
		// Kill the ping agent
		if (ping != null) {
			try {
				TestUtility.killAgent(a, ping);
			}
			catch (Exception e) {
				log(a.getLocalName()+": exception killing ping agent");
				e.printStackTrace();
			}
		}
	}
}

