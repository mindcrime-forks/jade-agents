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

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

/**
   @author Giovanni Caire - TILAB
 */
public class ParticipantAgent extends Agent {
	private AID testerAgent;
	private AID lightAgent;
	private int nMessages;
	private int halfMessages;
	private int incCnt = 0;
	private int outCnt = 0;
	private long period;
	private ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
	private boolean testOK = false;
	
	protected void setup() {
		// Get the lightweight agent name, number of messages and period as
		// arguments
		Object[] args = getArguments();
		testerAgent = new AID((String) args[0], AID.ISLOCALNAME);
		lightAgent = new AID((String) args[1], AID.ISLOCALNAME);
		nMessages = Integer.parseInt((String) args[2]);
		halfMessages = nMessages / 2;
		period = Long.parseLong((String) args[3]);

		// Send the initial message
		msg.addReceiver(lightAgent);
		msg.setContent(String.valueOf(nMessages));
		send(msg);
		
		// Each time send a message to the light agent 
		addBehaviour(new TickerBehaviour(this, period + 20) {			
			protected void onTick() {
				msg.setContent(String.valueOf(++outCnt));
				myAgent.send(msg);
				// Check if we have finished
				if (outCnt == nMessages) {
					if (incCnt >= nMessages) {
						System.out.println(myAgent.getLocalName()+": completed successfully");
						testOK = true;
						notifyTester();
					}
					myAgent.removeBehaviour(this);
				}
				else if (outCnt == halfMessages) {
					System.out.println(myAgent.getLocalName()+": "+outCnt+" messages sent");
				}	
			}
		} );
		
		// Receive messages from the light agent
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					AID sender = msg.getSender();
					if (sender.equals(lightAgent)) {
						int val = Integer.parseInt(msg.getContent());
						System.out.println(myAgent.getLocalName()+": Received message N."+val);
						if (val == incCnt+1) {
							incCnt = val;
							// Check if we have finished
							if (incCnt == nMessages) {
								if (outCnt == nMessages) {
									System.out.println(myAgent.getLocalName()+": completed successfully");
									testOK = true;
									notifyTester();
								}
							}
							else if (incCnt == halfMessages) {
								System.out.println(myAgent.getLocalName()+": "+incCnt+" messages received");
							}
							else if (incCnt > nMessages) {
								System.out.println(myAgent.getLocalName()+": message received beyond limit "+val);
							}
						}
						else {
							System.out.println(myAgent.getLocalName()+": Error receiving message from light agent "+sender.getLocalName()+". Expected # "+String.valueOf(incCnt+1)+", found # "+val);
							notifyTester();
						}
					}
					else {
						System.out.println(myAgent.getLocalName()+": Unexpected message received from "+sender.getLocalName());
					}
				}
				else {
					block();
				}
			}
		} );		
	}	
	
	private void notifyTester() {
		// Notify the tester agent
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(testerAgent);
		msg.setContent(String.valueOf(testOK));
		send(msg);
	}	
}

