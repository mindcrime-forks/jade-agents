package jade.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import jade.domain.ams;
import jade.domain.FIPAException;
import jade.domain.AgentAlreadyRegisteredException;

public class AgentPlatformImpl extends AgentContainerImpl implements AgentPlatform {

  // Initial size of agent hash table
  private static final int GLOBALMAP_SIZE = 100;

  // Load factor of agent hash table
  private static final float GLOBALMAP_LOAD_FACTOR = 0.25f;

  private ams theAMS;

  private Vector containers = new Vector();
  private Hashtable platformAgents = new Hashtable(GLOBALMAP_SIZE, GLOBALMAP_LOAD_FACTOR);

  private void initAMS() {

    System.out.print("Starting AMS... ");
    theAMS = new ams(this,"ams");

    // Subscribe as a listener for the AMS agent
    theAMS.addCommListener(this);

    // Insert AMS into local agents table
    localAgents.put("ams", theAMS);

    AgentDescriptor desc = new AgentDescriptor();
    desc.setAll("ams", myDispatcher, "ams@" + platformAddress, null, null, null, Agent.AP_INITIATED);
    desc.setName("ams");
    desc.setDemux(myDispatcher);

    platformAgents.put(desc.getName(), desc);
    System.out.println("AMS OK");
  }

  private void initACC() {
  }

  private void initDF() {
  }

  public AgentPlatformImpl() throws RemoteException {
    initAMS();
    initACC();
    initDF();
  }

  public void addContainer(AgentContainer ac) throws RemoteException {
    System.out.println("Adding container...");
    containers.addElement(ac);
  }

  public void removeContainer(AgentContainer ac) throws RemoteException {
    System.out.println("Removing container...");
    containers.removeElement(ac);
  }

  public void bornAgent(AgentDescriptor desc) throws RemoteException {
    System.out.println("Born agent " + desc.getName());
    platformAgents.put(desc.getName(), desc);
  }

  public void deadAgent(String name) throws RemoteException {
    System.out.println("Dead agent " + name);
    platformAgents.remove(name);
    // FIXME: Must update all container caches
  }

  public AgentDescriptor lookup(String agentName) throws RemoteException, NotFoundException {
    System.out.println("Looking up " + agentName + " in agents table...");
    Object o = platformAgents.get(agentName);
    if(o == null)
      throw new NotFoundException("Failed to find " + agentName);
    else
      return (AgentDescriptor)o;
  }


  // These methods are to be used only by AMS agent.


  // This one is called in response to a 'register-agent' action
  public void AMSNewData(String agentName, String address, String signature,
			 String delegateAgent, String forwardAddress, String APState)
    throws FIPAException, AgentAlreadyRegisteredException {

    try {
      AgentDescriptor ad = (AgentDescriptor)platformAgents.get(agentName);
      if(ad == null)
	throw new NotFoundException("Failed to find " + agentName);

      if(ad.getAPState() != Agent.AP_INITIATED) {
	throw new AgentAlreadyRegisteredException();
      }

      int state; // FIXME: Ugly code. All fipa-man-* objects must be gathered in a single place
      if(APState.equalsIgnoreCase("initiated"))
	state = Agent.AP_INITIATED;
      else if(APState.equalsIgnoreCase("active"))
	state = Agent.AP_ACTIVE;
      else if(APState.equalsIgnoreCase("suspended"))
	state = Agent.AP_SUSPENDED;
      else if(APState.equalsIgnoreCase("waiting"))
	state = Agent.AP_WAITING;
      else throw new jade.domain.UnrecognizedAttributeValueException();

      MessageDispatcher md = ad.getDemux(); // Preserve original MessageDispatcher

      ad.setAll(agentName, md, address, signature, delegateAgent, forwardAddress, state);
    }
    catch(NotFoundException nfe) {
      nfe.printStackTrace();
    }

  }

  // This one is called in response to a 'modify-agent' action
  public void AMSChangeData(String agentName, String address, String signature, String delegateAgent,
			    String forwardAddress, String APState) throws FIPAException {

    try {
      AgentDescriptor ad = (AgentDescriptor)platformAgents.get(agentName);
      if(ad == null)
	throw new NotFoundException("Failed to find " + agentName);

      if(address != null)
	ad.setAddress(address);
      if(signature != null)
	ad.setSignature(signature);
      if(signature != null)
	ad.setDelegateAgent(delegateAgent);
      if(forwardAddress != null)
	ad.setAddress(forwardAddress);

      if(APState != null) {
	int state; // FIXME: Ugly code. All fipa-man-* objects must be gathered in a single place
	if(APState.equalsIgnoreCase("initiated"))
	  state = Agent.AP_INITIATED;
	else if(APState.equalsIgnoreCase("active"))
	  state = Agent.AP_ACTIVE;
	else if(APState.equalsIgnoreCase("suspended"))
	  state = Agent.AP_SUSPENDED;
	else if(APState.equalsIgnoreCase("waiting"))
	  state = Agent.AP_WAITING;
	else throw new jade.domain.UnrecognizedAttributeValueException();

	ad.setAPState(state);
      }
    }
    catch(NotFoundException nfe) {
      nfe.printStackTrace();
    }

  }


  // This one is called in response to a 'deregister-agent' action
  public void AMSRemoveData(String agentName, String address, String signature, String delegateAgent,
			    String forwardAddress, String APState) throws FIPAException {

    AgentDescriptor ad = (AgentDescriptor)platformAgents.remove(agentName);
    if(ad == null)
      throw new jade.domain.UnableToDeregisterException();
  }

  public void AMSDumpData() {
    Enumeration descriptors = platformAgents.elements();
    while(descriptors.hasMoreElements()) {
      AgentDescriptor desc = (AgentDescriptor)descriptors.nextElement();
      System.out.println("===========================");
      System.out.println(":agent-name " + desc.getName());
      System.out.println(":address " + desc.getAddress());
      System.out.println(":signature " + desc.getSignature());
      System.out.println(":delegate-agent " + desc.getDelegateAgent());
      System.out.println(":forward-address " + desc.getForwardAddress());
      System.out.println(":ap-state " + desc.getAPState());
      System.out.println("===========================");
      System.out.println("");
    }

  }


}

