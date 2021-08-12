package leap.split.tests;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.JADEAgentManagement.JADEManagementVocabulary;
import test.common.*;


/**
 * The test start a leap split-container specifying on the command line an agent 
 * including the %C wildcard in its name.
 * The test is successfully extecuted if the agent exists and can be be properly killed. 
 * 
 * @author Tiziana Trucco
 * @version $Date:  $ $Revision: $
 *
 */
public class TestBootstrapAgentBasic extends Test{

	private JadeController jc = null;
	static String PREFIX = "prefix_";
	static String SUFFIX = "_suffix";
	String containerName;
	Agent myAgent;

	public Behaviour load(Agent a) throws TestException {  

		myAgent = a;
		SequentialBehaviour sb = new SequentialBehaviour(a);

		//Step 1: Initialization phase
		sb.addSubBehaviour(new OneShotBehaviour(a) {
			public void action() {
				try {
					log("Creating split container with bootstrap agent...");
					containerName = createSplitContainerWithBootstrapAgent();
					log("Split-container with bootstrap agent started...");
				}
				catch (Exception e) {
					failed("Error initilizing split-container with bootstrap agent. " + e);
					e.printStackTrace();
				}
			}
		});

		//step 2: kill bootstrap agent.
		sb.addSubBehaviour(new WakerBehaviour(a, 2000){
			public void onWake(){
				AID wildcardAgent = new AID(PREFIX + containerName + SUFFIX, AID.ISLOCALNAME);
				try {
					log("Killing agent "+wildcardAgent.getName()+"...");
					//to be sure that the agent has been created.
					Thread.sleep(1000); 
					TestUtility.killAgent(myAgent, wildcardAgent);
					log("Agent "+wildcardAgent.getName()+" found and killed as expected.");
				}
				catch (Exception e) {
					failed("Cannot kill agent "+wildcardAgent.getName()+". "+e);
					e.printStackTrace();
				}
			}
		});

		//Step 3: kill split container.
		sb.addSubBehaviour(new OneShotBehaviour(){
			public void action(){
				try{
					log("Killing split-container");
					TestUtility.killContainer(myAgent, containerName);
				}catch(TestException te){
					failed("Error in killing split-container.");
					te.printStackTrace();
				}
				//check if the container has been successfully ended.
				try{
					log("Retry killing split-container...");
					TestUtility.killContainer(myAgent, containerName);
					failed("Split-container does not exit successfully the first time.");
				}catch(TestException te){
					log("Exception occured as expected. " + te);
					passed("Split-container successfully killed the first time.");
				}
			}
		});
		return sb;
	}

	public void clean(Agent a) {
		if (jc != null) {
			jc.kill();
		}
	}

	/**
	 * Override this method to initialize the environment for the test.
	 * @return name the name of the split-container created.
	 * @throws TestException
	 */
	protected String createSplitContainerWithBootstrapAgent() throws TestException{
		log("Creating split container...");
		jc = TestUtility.launchSplitJadeInstance("Split-Container-1", null, new String("-host "+TestUtility.getLocalHostName()+" -port "+String.valueOf(Test.DEFAULT_PORT) + " " + PREFIX+JADEManagementVocabulary.CONTAINER_WILDCARD+SUFFIX+":jade.core.Agent"));
		return jc.getContainerName();
	}

}
