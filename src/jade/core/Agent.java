/*
 * $Id$
 */

package jade.core;

import java.io.Reader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

import jade.lang.acl.*;
import jade.domain.AgentManagementOntology;
import jade.domain.FIPAException;

/**************************************************************

  Name: Agent

  Responsibility and Collaborations:

  + Abstract placeholder for user-defined agents.

  + Provides primitives for sending and receiving messages.
    (ACLMessage)

  + Schedules and executes complex behaviours.
    (Behaviour, Scheduler)

****************************************************************/
public class Agent implements Runnable, CommBroadcaster {


  // Agent Platform Life-Cycle states

  public static final int AP_MIN = -1;   // Hand-made type checking
  public static final int AP_INITIATED = 1;
  public static final int AP_ACTIVE = 2;
  public static final int AP_SUSPENDED = 3;
  public static final int AP_WAITING = 4;
  public static final int AP_DELETED = 5;
  public static final int AP_MAX = 6;    // Hand-made type checking

  // Domain Life-Cycle states

  public static final int D_MIN = 9;     // Hand-made type checking
  public static final int D_ACTIVE = 10;
  public static final int D_SUSPENDED = 20;
  public static final int D_RETIRED = 30;
  public static final int D_UNKNOWN = 40;
  public static final int D_MAX = 41;    // Hand-made type checking

  protected Vector msgQueue = new Vector();
  protected Vector listeners = new Vector();

  protected String myName = null;
  protected String myAddress = null;

  protected Thread myThread;
  protected Scheduler myScheduler;
  protected Behaviour currentBehaviour;
  protected ACLMessage currentMessage;

  private int myAPState;
  private int myDomainState;
  private Vector blockedBehaviours = new Vector();

  protected ACLParser myParser = ACLParser.create();


  public Agent() {
    myAPState = AP_INITIATED;
    myDomainState = D_UNKNOWN;
    myThread = new Thread(this);
    myScheduler = new Scheduler(this);
  }

  public String getName() {
    return myName;
  }

  // State transition methods for Agent Platform Life-Cycle

  public void doStart(String name, String platformAddress) { // Transition from Initiated to Active

    // Set this agent's name and address and start its embedded thread
    myName = new String(name);
    myAddress = new String(platformAddress);

    myThread.setName(myName);
    myThread.start();

  }

  public void doMove() { // Transition from Active to Initiated
    myAPState = AP_INITIATED;
    // FIXME: Should do something more
  }

  public void doSuspend() { // Transition from Active to Suspended
    myAPState = AP_SUSPENDED;
    // FIXME: Should do something more
  }

  public void doActivate() { // Transition from Suspended to Active
    myAPState = AP_ACTIVE;
    // FIXME: Should do something more
  }

  public synchronized void doWait() { // Transition from Active to Waiting
    myAPState = AP_WAITING;
    try {
      wait(); // Blocks on its monitor
    }
    catch(InterruptedException ie) {
      // Do nothing
    }
  }

  public synchronized void doWake() { // Transition from Waiting to Active
    myAPState = AP_ACTIVE;
    notify(); // Wakes up the embedded thread
  }

  public void doDelete() { // Transition to destroy the agent
    myAPState = AP_DELETED; // FIXME: Should do something more
  }

  public final void run() {

    try{
      registerWithAMS(null,Agent.AP_ACTIVE,null,null,null);

      setup();

      mainLoop();

      destroy();
    }
    catch(Exception e) {
      System.err.println("***  Uncaught Exception for agent " + myName + "  ***");
      e.printStackTrace();
      destroy();
    }

  }

  protected void setup() {}

  private void mainLoop() {
    while(myAPState != AP_DELETED) {

      // Select the next behaviour to execute
      currentBehaviour = myScheduler.schedule();

      // Just do it!
      currentBehaviour.action();

      // When it is needed no more, delete it from the behaviours queue
      if(currentBehaviour.done()) {
	myScheduler.remove(currentBehaviour);
	currentBehaviour = null;
      }
      else if(!currentBehaviour.isRunnable()) {
	// Remove blocked behaviours from scheduling queue and put it
	// in blocked behaviours queue
	myScheduler.remove(currentBehaviour);
	blockedBehaviours.addElement(currentBehaviour);
	currentBehaviour = null;
      }

      // Now give CPU control to other agents
      Thread.yield();

    }
  }

  private void destroy() { // FIXME: Should remove the agent from all agents tables.
  }

  public void addBehaviour(Behaviour b) {
    myScheduler.add(b);
  }

  public void removeBehaviour(Behaviour b) {
    myScheduler.remove(b);
  }


  // Event based message sending -- unicast
  public final void send(ACLMessage msg) {
    CommEvent event = new CommEvent(this, msg);
    broadcastEvent(event);
  }

  // Event based message sending -- multicast
  public final void send(ACLMessage msg, AgentGroup g) {
    CommEvent event = new CommEvent(this, msg, g);
    broadcastEvent(event);
  }

  // Non-blocking receive
  public final synchronized ACLMessage receive() {
    if(msgQueue.isEmpty()) {
      return null;
    }
    else {
      ACLMessage msg = (ACLMessage)msgQueue.firstElement();
      currentMessage = msg;
      msgQueue.removeElementAt(0);
      return msg;
    }
  }

  // Non-blocking receive with pattern matching on messages
  public final synchronized ACLMessage receive(MessageTemplate pattern) {
    ACLMessage msg = null;

    Enumeration messages = msgQueue.elements();

    while(messages.hasMoreElements()) {
      ACLMessage cursor = (ACLMessage)messages.nextElement();
      if(pattern.match(cursor)) {
	msg = cursor;
	currentMessage = cursor;
	msgQueue.removeElement(cursor);
	break; // Exit while loop
      }
    }

    return msg;
  }

  // Blocking receive
  public final synchronized ACLMessage blockingReceive() {
    ACLMessage msg = receive();
    while(msg == null) {
      doWait();
      msg = receive();
    }
    return msg;
  }

  // Blocking receive with pattern matching on messages
  public final synchronized ACLMessage blockingReceive(MessageTemplate pattern) {
    ACLMessage msg = receive(pattern);
    while(msg == null) {
      doWait();
      msg = receive(pattern);
    }
    return msg;
  }

  // Put a received message back in message queue
  public final synchronized void putBack(ACLMessage msg) {
    msgQueue.insertElementAt(msg,0);
  }

  // Build an ACL message from a character stream
  public ACLMessage parse(Reader text) {
    ACLMessage msg = null;
    try {
      msg = myParser.parse(text);
    }
    catch(ParseException pe) {
      pe.printStackTrace();
      System.exit(1);
    }
    return msg;
  }

  private ACLMessage FipaRequestMessage(String dest, String replyString) {
    ACLMessage request = new ACLMessage();

    request.setType("request");
    request.setSource(myName);
    request.setDest(dest);
    request.setLanguage("SL0");
    request.setOntology("fipa-agent-management");
    request.setProtocol("fipa-request");
    request.setReplyWith(replyString);

    return request;
  }

  private void doFipaRequestClient(ACLMessage request, String replyString) {

    send(request);

    ACLMessage reply = blockingReceive(MessageTemplate.MatchReplyTo(replyString));

    // FIXME: Should unmarshal content of 'refuse' and 'failure'
    // messages and convert them to Java exceptions
    if(reply.getType().equalsIgnoreCase("agree")) {
      reply =  blockingReceive(MessageTemplate.MatchReplyTo(replyString));

      if(!reply.getType().equalsIgnoreCase("inform")) {
	System.out.println(replyString + " failed for " + myName + "!!!");
      }

    }
    else {
      System.out.println(replyString + " refused for " + myName + "!!!");
    }

  }


  // Register yourself with platform AMS
  public void registerWithAMS(String signature, int APState, String delegateAgent,
			      String forwardAddress, String ownership) throws FIPAException {
				
    String replyString = myName + "-ams-registration";
    ACLMessage request = FipaRequestMessage("ams", replyString);
    AgentManagementOntology o = AgentManagementOntology.instance();

    // Build an AMS action object for the request

    AgentManagementOntology.AMSAction a = new AgentManagementOntology.AMSAction();
    AgentManagementOntology.AMSAgentDescriptor amsd = new AgentManagementOntology.AMSAgentDescriptor();

    amsd.setName(myName + "@" + myAddress);
    amsd.setAddress(myAddress);
    amsd.setAPState(APState);
    amsd.setDelegateAgentName(delegateAgent);
    amsd.setForwardAddress(forwardAddress);
    amsd.setOwnership(ownership);

    a.setName(AgentManagementOntology.AMSAction.REGISTERAGENT);
    a.setArg(amsd);

    // Convert it to a String and write it in content field of the request

    StringWriter text = new StringWriter();
    a.toText(text);
    request.setContent("( action ams " + text + " )");

    // Send message and collect reply
    doFipaRequestClient(request, replyString);

  }

  // Authenticate yourself with platform AMS
  public void authenticateWithAMS(String signature, int APState, String delegateAgent,
				  String forwardAddress, String ownership) throws FIPAException {

  }

  // Deregister yourself with platform AMS
  public void deregisterWithAMS() throws FIPAException {

    String replyString = myName + "-ams-deregistration";

    // Get a semi-complete request message
    ACLMessage request = FipaRequestMessage("ams", replyString);
    

    // Build an AMS action object for the request

    AgentManagementOntology o = AgentManagementOntology.instance();
    AgentManagementOntology.AMSAction a = new AgentManagementOntology.AMSAction();
    AgentManagementOntology.AMSAgentDescriptor amsd = new AgentManagementOntology.AMSAgentDescriptor();

    amsd.setName(myName + "@" + myAddress);
    a.setName(AgentManagementOntology.AMSAction.DEREGISTERAGENT);
    a.setArg(amsd);


    // Convert it to a String and write it in content field of the request

    StringWriter text = new StringWriter();
    a.toText(text);
    request.setContent("( action ams " + text + " )");

    // Send message and collect reply
    doFipaRequestClient(request, replyString);

  }

  // Modify your registration with platform AMS
  public void modifyAMSRegistration(String signature, int APState, String delegateAgent,
				    String forwardAddress, String ownership) throws FIPAException {

    String replyString = myName + "-ams-modify";
    ACLMessage request = FipaRequestMessage("ams", replyString);
    AgentManagementOntology o = AgentManagementOntology.instance();

    // Build an AMS action object for the request

    AgentManagementOntology.AMSAction a = new AgentManagementOntology.AMSAction();
    AgentManagementOntology.AMSAgentDescriptor amsd = new AgentManagementOntology.AMSAgentDescriptor();

    amsd.setName(myName + "@" + myAddress);
    amsd.setAddress(myAddress);
    amsd.setAPState(APState);
    amsd.setDelegateAgentName(delegateAgent);
    amsd.setForwardAddress(forwardAddress);
    amsd.setOwnership(ownership);

    a.setName(AgentManagementOntology.AMSAction.MODIFYAGENT);
    a.setArg(amsd);

    // Convert it to a String and write it in content field of the request

    StringWriter text = new StringWriter();
    a.toText(text);
    request.setContent("( action ams " + text + " )");

    // Send message and collect reply
    doFipaRequestClient(request, replyString);

  }

  public void forwardWithACC(ACLMessage msg) {

    String replyString = myName + "-acc-forward";
    ACLMessage request = FipaRequestMessage("acc", replyString);
    AgentManagementOntology o = AgentManagementOntology.instance();


    // Build an ACC action object for the request

    AgentManagementOntology.ACCAction a = new AgentManagementOntology.ACCAction();
    a.setName(AgentManagementOntology.ACCAction.FORWARD);
    a.setArg(msg);

    // Convert it to a String and write it in content field of the request

    StringWriter text = new StringWriter();
    a.toText(text);
    request.setContent("( action acc " + text + " )");

    // Send message and collect reply
    doFipaRequestClient(request, replyString);

  }

  // Register yourself with a DF
  public void registerWithDF() {
  }

  // Deregister yourself with a DF
  public void deregisterWithDF() {
  }

  // Modify registration data with a DF
  public void modifyDFRegistration() {
  }

  // Search a DF for information
  public void searchDF() {
  }


  // Event handling methods


  // Broadcast communication event to registered listeners
  private void broadcastEvent(CommEvent event) {
    Enumeration e = listeners.elements();
    while(e.hasMoreElements()) {
      CommListener l = (CommListener)e.nextElement();
      l.CommHandle(event);
    }
  }

  // Register a new listener
  public final void addCommListener(CommListener l) {
    listeners.addElement(l);
  }

  // Remove a registered listener
  public final void removeCommListener(CommListener l) {
    listeners.removeElement(l);
  }

  private void activateAllBehaviours() {
    // Put all blocked behaviours back in ready queue
    while(!blockedBehaviours.isEmpty()) {
      Behaviour b = (Behaviour)blockedBehaviours.lastElement();
      blockedBehaviours.removeElementAt(blockedBehaviours.size() - 1);
      b.restart();
      myScheduler.add(b);
    }
  }

  // Put an incoming message in agent's message queue and activate all
  // blocking behaviours waiting for a message
  public final synchronized void postMessage (ACLMessage msg) {
    if(msg != null) msgQueue.addElement(msg);
    activateAllBehaviours();
    doWake();
  }

}



