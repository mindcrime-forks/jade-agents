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

package mobility;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import test.common.JadeController;
import test.common.Test;
import test.common.TestException;
import test.common.TestGroup;
import test.common.TestUtility;
import test.common.TesterAgent;

/**
   @author Giovanni Caire - TILAB
   @author Elisabetta Cortese - TILAB
   
 */
public class MobilityTesterAgent extends TesterAgent {
	public static final String CONTAINER1_KEY = "C1";
	public static final String CONTAINER2_KEY = "C2";
	public static final String ADDITIONAL_CLASSPATH_KEY = "classpath";
	public static final String ADDITIONAL_CLASSPATH_DEFAULT = "separate.jar";
	
	protected TestGroup getTestGroup() {
		TestGroup tg = new TestGroup("test/mobility/mobilityTestsList.xml"){		
			 
			private transient JadeController jc1;
			private transient JadeController jc2;
			
			protected void initialize(Agent a) throws TestException {
				jc1 = TestUtility.launchJadeInstance("Container-1", null,
						"-container -host "+TestUtility.getLocalHostName()+" -port "+String.valueOf(Test.DEFAULT_PORT), null); 
				setArgument(CONTAINER1_KEY, jc1.getContainerName());
				
				jc2 = TestUtility.launchJadeInstance("Container-2", null,
						"-container -host "+TestUtility.getLocalHostName()+" -port "+String.valueOf(Test.DEFAULT_PORT), null); 
				setArgument(CONTAINER2_KEY, jc2.getContainerName());
			}
			
			protected void shutdown(Agent a) {
				jc1.kill();
				jc2.kill();
			}
		};
		tg.specifyArgument(ADDITIONAL_CLASSPATH_KEY, "Additional classpath", ADDITIONAL_CLASSPATH_DEFAULT);

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

      AgentController tester = mc.createNewAgent("tester", "test.mobility.MobilityTesterAgent", args);
      tester.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}
