/*
  $Log$
  Revision 1.10  1999/02/15 11:46:52  rimassa
  Changed a line of code to correctly use Agent.getName().

  Revision 1.9  1999/02/14 23:25:31  rimassa
  Changed addBehaviour() calls to addSubBehaviour() calls where
  appropriate.

  Revision 1.8  1998/12/08 00:11:31  rimassa
  Removed handmade parsing of message content; now updated
  fromText() method is used from various AMS actions.

  Revision 1.7  1998/11/15 23:11:52  rimassa
  Added two public methods killContainer() and shutDownPlatform(), used
  as GUI callbacks.
  Added new ACLMessage object to hold requests to AMS for
  'kill-container' action.

  Revision 1.6  1998/11/09 00:27:11  rimassa
  Added 'RMA' as sender name in ACL messages to the AMS.
  Closing GUI on RMA agent exit.

  Revision 1.5  1998/11/05 23:38:06  rimassa
  Added GUI callback methods to create new agents and to kill them.

  Revision 1.4  1998/11/03 00:39:52  rimassa
  Added processing of 'inform' messages received from AMS in response to
  AgentPlatform events.

  Revision 1.3  1998/11/02 02:06:23  rimassa
  Started to add a Behaviour to handle 'inform' messages the AMS sends
  when some AgentPlatform event occurs that can be of interest of Remote
  Management Agent.

  Revision 1.2  1998/11/01 15:02:29  rimassa
  Added a Behaviour to register with the AMS as a listener of Agent
  Container Event notifications.

  Revision 1.1  1998/10/26 00:12:30  rimassa
  New domain agent to perform platform administration: this agent has a GUI to
  manage the Agent Platform and special access rights to the AMS.

*/


package jade.domain;


import java.io.StringReader;
import java.io.StringWriter;

import jade.core.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.gui.*;

/**************************************************************

  Name: rma

  Responsibility and Collaborations:

  + Serves as Remote Management Agent for the Agent Platform,
    according to our proposal to FIPA 97 specification.

  + Relies on the AMS to perform Agent Management actions, talking
    with it through simple ACL mesages.
    (ams)

****************************************************************/
public class rma extends Agent {

  private ACLMessage AMSSubscription = new ACLMessage("subscribe");
  private ACLMessage AMSCancellation = new ACLMessage("cancel");
  private ACLMessage newAgentMsg = new ACLMessage("request");
  private ACLMessage killAgentMsg = new ACLMessage("request");
  private ACLMessage killContainerMsg = new ACLMessage("request");

  private class AMSListenerBehaviour extends CyclicBehaviour {

    private MessageTemplate listenTemplate;

    AMSListenerBehaviour() {

      MessageTemplate mt1 = MessageTemplate.MatchLanguage("SL");
      MessageTemplate mt2 = MessageTemplate.MatchOntology("jade-agent-management");
      MessageTemplate mt12 = MessageTemplate.and(mt1, mt2);

      mt1 = MessageTemplate.MatchReplyTo("RMA-subscription");
      mt2 = MessageTemplate.MatchType("inform");
      listenTemplate = MessageTemplate.and(mt1, mt2);
      listenTemplate = MessageTemplate.and(listenTemplate, mt12);

    }

    public void action() {

      ACLMessage current = receive(listenTemplate);
      if(current != null) {
	// Handle inform messages from AMS
	StringReader text = new StringReader(current.getContent());
	try {

	  AgentManagementOntology.AMSEvent amse = AgentManagementOntology.AMSEvent.fromText(text);
	  int k = amse.getKind();

	  String container = null;
	  AgentManagementOntology.AMSAgentDescriptor amsd = null;

	  switch(k) {
	  case AgentManagementOntology.AMSEvent.NEWCONTAINER:
	    AgentManagementOntology.AMSContainerEvent ev1 = (AgentManagementOntology.AMSContainerEvent)amse;
	    container = ev1.getContainerName();
	    myGUI.addContainer(container);
	    break;
	  case AgentManagementOntology.AMSEvent.DEADCONTAINER:
	    AgentManagementOntology.AMSContainerEvent ev2 = (AgentManagementOntology.AMSContainerEvent)amse;
	    container = ev2.getContainerName();
	    myGUI.removeContainer(container);
	    break;
	  case AgentManagementOntology.AMSEvent.NEWAGENT:
	    AgentManagementOntology.AMSAgentEvent ev3 = (AgentManagementOntology.AMSAgentEvent)amse;
	    container = ev3.getContainerName();
	    amsd = ev3.getAgentDescriptor();
	    myGUI.addAgent(container, amsd.getName(), amsd.getAddress(), "fipa-agent");
	    break;
	  case AgentManagementOntology.AMSEvent.DEADAGENT:
	    AgentManagementOntology.AMSAgentEvent ev4 = (AgentManagementOntology.AMSAgentEvent)amse;
	    container = ev4.getContainerName();
	    amsd = ev4.getAgentDescriptor();
	    myGUI.removeAgent(container, amsd.getName());
	    break;
	  }

	}
	catch(ParseException pe) {
	  pe.printStackTrace();
	}
	catch(TokenMgrError tme) {
	  tme.printStackTrace();
	}

      }
      else
	block();

    }

  } // End of AMSListenerBehaviour

  private SequentialBehaviour AMSSubscribe = new SequentialBehaviour();

  private AMSMainFrame myGUI = new AMSMainFrame(this);

  public void setup() {

    // Fill ACL messages fields

    AMSSubscription.setSource(myName);
    AMSSubscription.setDest("AMS");
    AMSSubscription.setLanguage("SL");
    AMSSubscription.setOntology("jade-agent-management");
    AMSSubscription.setReplyWith("RMA-subscription");
    AMSSubscription.setConversationId(myName+'@'+myAddress);

    // Please inform me whenever container list changes and send me
    // the difference between old and new container lists, complete
    // with every AMS agent descriptor
    String content = "iota ?x ( :container-list-delta ?x )";
    AMSSubscription.setContent(content);

    AMSCancellation.setSource(myName);
    AMSCancellation.setDest("AMS");
    AMSCancellation.setLanguage("SL");
    AMSCancellation.setOntology("jade-agent-management");
    AMSCancellation.setReplyWith("RMA-cancellation");
    AMSCancellation.setConversationId(getName());

    // No content is needed (cfr. FIPA 97 Part 2 page 26)

    killContainerMsg.setSource(myName);
    killContainerMsg.setDest("AMS");
    killContainerMsg.setProtocol("fipa-request");
    killContainerMsg.setOntology("fipa-agent-management");
    killContainerMsg.setLanguage("SL0");

    killAgentMsg.setSource(myName);
    killAgentMsg.setDest("AMS");
    killAgentMsg.setProtocol("fipa-request");
    killAgentMsg.setOntology("fipa-agent-management");
    killAgentMsg.setLanguage("SL0");

    newAgentMsg.setSource(myName);
    newAgentMsg.setDest("AMS");
    newAgentMsg.setProtocol("fipa-request");
    newAgentMsg.setOntology("fipa-agent-management");
    newAgentMsg.setLanguage("SL0");

    // Send 'subscribe' message to the AMS
    AMSSubscribe.addSubBehaviour(new SenderBehaviour(this, AMSSubscription));

    // Handle incoming 'inform' messages
    AMSSubscribe.addSubBehaviour(new AMSListenerBehaviour());

    // Schedule Behaviour for execution
    addBehaviour(AMSSubscribe);

    // Show Graphical User Interface
    myGUI.ShowCorrect();

  }

  public void takeDown() {
    send(AMSCancellation);
    myGUI.setVisible(false);
    myGUI.dispose();
  }


  // Callback methods from GUI

  public void newAgent(String agentName, String className, String containerName) {

    AgentManagementOntology.CreateAgentAction caa = new AgentManagementOntology.CreateAgentAction();
    AgentManagementOntology.AMSAgentDescriptor amsd = new AgentManagementOntology.AMSAgentDescriptor();

    if(agentName.indexOf('@') < 0)
      agentName = agentName.concat('@' + myAddress);

    if(containerName.equals(""))
      containerName = "MainContainer";

    amsd.setName(agentName);

    caa.setArg(amsd);
    caa.setClassName(className);
    caa.addProperty(AgentManagementOntology.CreateAgentAction.CONTAINER, containerName);

    StringWriter createText = new StringWriter();
    caa.toText(createText);
    newAgentMsg.setContent(createText.toString());

    send(newAgentMsg);

    // FIXME: Should do a complete 'fipa-request' protocol

  }

  public void killAgent(String name) {
    AgentManagementOntology.KillAgentAction kaa = new AgentManagementOntology.KillAgentAction();
    kaa.setAgentName(name);
    StringWriter killText = new StringWriter();
    kaa.toText(killText);
    killAgentMsg.setContent(killText.toString());

    send(killAgentMsg);

    // FIXME: Should do a complete 'fipa-request' protocol

  }

  public void killContainer(String name) {

    AgentManagementOntology.KillContainerAction kca = new AgentManagementOntology.KillContainerAction();
    kca.setContainerName(name);
    StringWriter killText = new StringWriter();
    kca.toText(killText);
    killContainerMsg.setContent(killText.toString());

    send(killContainerMsg);

    // FIXME: Should do a complete 'fipa-request' protocol

  }

  public void shutDownPlatform() {

    killContainer("Container-0"); // FIXME: Container name is hardwired within RMA

  }

}
