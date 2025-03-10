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

package domain;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import jade.wrapper.*;
import test.common.*;

/**
 * @author Giovanni Caire - TILAB
 * @author Elisabetta Cortese - TILAB
 */

public class JADEManagementOntologyTesterAgent extends TesterAgent {
	// Keys and default values for group arguments
	public static final String INFORM_MSG_KEY = "inform-msg";
	
	private static final String TEST_CONVERSATION = "Test-conversation";
	private static final String TEST_RESPONSE_ID = "Test-response";
	
	protected TestGroup getTestGroup() {
		TestGroup tg = new TestGroup("test/domain/jadeManagementOntoTestsList.xml"){		
			
			private AID resp;
			
			public void initialize(Agent a) throws TestException {
				// Load the codec (SL) and ontology (JADEManagementOntology) to be used
				Codec codec = new SLCodec();
				Ontology ontology = JADEManagementOntology.getInstance();
    		a.getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
    		a.getContentManager().registerOntology(ontology);
    		
				// Prepare the message that will be used in all tests
				ACLMessage msg  = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(getAMS());
  			msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
  			msg.setOntology(ontology.getName());
    		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
  			msg.setConversationId(TEST_CONVERSATION);
  			msg.setReplyWith(TEST_RESPONSE_ID);
				setArgument(INFORM_MSG_KEY, msg);
			}
			
		};
				
		return tg;
	}
				
	// Main method that allows launching this test as a stand-alone program	
	public static void main(String[] args) {
		try {
      // Get a hold on JADE runtime
      Runtime rt = Runtime.instance();

      // Exit the JVM when there are no more containers around
      rt.setCloseVM(true);

      Profile pMain = new ProfileImpl(null, Test.DEFAULT_PORT, null);

      AgentContainer mc = rt.createMainContainer(pMain);

      AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
      rma.start();

      AgentController tester = mc.createNewAgent("tester", "test.domain.JADEManagementOntologyTesterAgent", args);
      tester.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}
