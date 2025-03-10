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

package leap;

import test.common.*;

import javax.swing.JOptionPane;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.*;

/**
   @author Givanni Caire - TILAB
 */
public class LEAPTesterAgent extends TesterAgent {

	public static final int MEDIATOR_LOCAL_PORT = 9902;
	public static final String MEDIATOR_SERVICES = "jade.core.event.NotificationService;jade.imtp.leap.nio.BEManagementService";
	
	// Names and default values for group arguments
	public static final String LIGHT_CONTAINER_KEY = "light-container";
		
	public static JadeController mediatorController;
	
	protected TestGroup getTestGroup() {
		TestGroup tg = new TestGroup("test/leap/LEAPTestsList.xml"){		
			
			public void initialize(Agent a) throws TestException {
				mediatorController = TestUtility.launchJadeInstance("Mediator", null, "-container -local-port "+MEDIATOR_LOCAL_PORT+" -services "+MEDIATOR_SERVICES, null);
				
				String lightContainerName = JOptionPane.showInputDialog(null, "Connect a split container either to the main or to\nthe BEManagementService on the mediator container.\nThen insert the split container name and press OK");
				setArgument(LIGHT_CONTAINER_KEY, lightContainerName);
			}	
			
			public void shutdown(Agent a) {
				// Kill the mediatoir container
				mediatorController.kill();
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

      AgentController tester = mc.createNewAgent("tester", "test.leap.LEAPTesterAgent", args);
      tester.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
