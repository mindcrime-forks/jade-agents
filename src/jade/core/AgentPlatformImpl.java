/*
  $Log$
  Revision 1.44  1999/11/08 15:19:41  rimassaJade
  Added sniffOn() and sniffOff() methods to control message sniffing.

  Revision 1.43  1999/10/08 08:28:40  rimassa
  Added some fault tolerance to the platform in dealing with unreachable
  containers.

  Revision 1.42  1999/10/06 08:48:25  rimassa
  Added a printout message when the agent platform is ready to accept
  containers.
  Added the creation of a file containing the IIOP URL for the agent
  platform.

  Revision 1.41  1999/09/02 14:59:01  rimassa
  Handled new ParseException exception from ACLMessage.fromText().

  Revision 1.40  1999/08/31 17:23:57  rimassa
  Added a method to transfer agent identity between two agent
  containers.

  Revision 1.39  1999/08/27 15:44:43  rimassa
  Added Agent Descriptor locking in GADT lookup, to support
  transactional agent migration.

  Revision 1.38  1999/08/10 15:32:56  rimassa
  Added implementation for lookup() method, and changed some method names.

  Revision 1.37  1999/07/19 00:04:42  rimassa
  Added an empty moveAgent() method.

  Revision 1.36  1999/07/13 19:23:52  rimassa
  Implemented the revised AgentManager interface.
  Separated Platform Agent Descriptors from AMS Agent Descriptors. Now
  the AMS holds its own data about the agents.
  Removed all AMS related methods, implementing AMS actions.

  Revision 1.35  1999/06/04 07:48:07  rimassa
  Made package scoped this previously public class. Changed package
  scoping when using some String constants.

  Revision 1.34  1999/04/07 11:40:12  rimassa
  Fixed code indentation.

  Revision 1.33  1999/03/29 10:40:58  rimassa
  Fixed a bug raising a ConcurrentModificationException during Agent
  Platform shutdown.
  Made system agents (ACC, AMS and Default DF) run at a higher priority
  with respect to user agents.

  Revision 1.32  1999/03/24 12:19:02  rimassa
  Ported most data structures to the newer Java 2 Collection framework.

  Revision 1.31  1999/03/17 13:04:56  rimassa
  Many changes to support new proxy based design. Now platform agent
  table is indexed by the complete agent GUID and not just the local
  agent name. So even foreign or mobile agents can now register
  themselves with the platform.

  Revision 1.30  1999/03/15 15:25:39  rimassa
  Changed priority for system agents.

  Revision 1.29  1999/03/09 13:30:14  rimassa
  Used String constants for system agent names.
  Completely redesigned AgentPlatform startup: now system agents (AMS,
  ACC and default DF) run in a separate ThreadGroup.
  Added a custom joinPlatform() method to perform specific
  initialization.
  Now removeContainer() takes a String argument and no more an
  AgentContainer.

  Revision 1.28  1999/03/07 22:51:43  rimassa
  Added a debugging printout.

  Revision 1.27  1999/03/03 16:07:45  rimassa
  Added a getContainerFromAgent() method.
  Added methods to dispatch a suspend()/resume() request to the
  appropriate agent container.
  Improved method naming, distinguishing between AMS and AP.

  Revision 1.26  1999/02/25 08:26:12  rimassa
  Removed useless FIXME.
  Clarified various error messages appearing when an agent was not
  found.
  Completely redesigned platform shutdown procedure. Now it works like
  this:
    - Remove front end container from container list.
    - Shutdown all other container.
    - Kill all non system agents on front end container.
    - Kill default DF.
    - Kill ACC.
    - Kill AMS.
    - Disconnect platform IIOP server.

  Fixed a nasty deadlock problem: now AMSKillContainer spawns a separate
  thread calling RMI method AgentContainer.exit(), so the AMS is free to
  answer to 'deregister' messages from about-to-die agents.

  Revision 1.25  1999/02/15 11:43:21  rimassa
  Fixed a bug: a case sensitive comparison was incorrectly made on agent
  name during deregistration with AMS agent.

  Revision 1.24  1999/02/14 23:13:34  rimassa
  Removed an useless printout. Changed IOR file name from 'CSELT.IOR' to
  'JADE.IOR'.

  Revision 1.23  1999/02/04 12:57:07  rimassa
  Added a 'FIXME:' reminder.

  Revision 1.22  1999/02/03 10:13:58  rimassa
  Added server side CORBA support: now the AgentPlatform contains a
  CORBA object implementation for FIPA_Agent_97 IDL interface.
  During platform startup, the IOR or the URL for that CORBA object
  implementation is stored as the IIOP address of the whole agent
  platform.
  Modified addContainer() method to comply with new AgentPLatform
  interface.

  Revision 1.21  1998/11/15 23:03:33  rimassa
  Removed old printed messages about system agents, since now the Remote
  Agent Management GUI shows all agents present on the platform.
  Added a new AMSKillContainer method to be used from AMS agent to
  terminate Agent Containers.

  Revision 1.20  1998/11/09 22:15:58  Giovanni
  Added an overridden version of AgentContainerImpl shutDown() method:
  an AgentPlatform firstly shuts itself down as an ordinary
  AgentContainer (i.e. it removes itself from container list), then
  calls exit() remote method for every other AgentContainer in the
  platform, thereby completely terminating the Agent Platform.

  Revision 1.19  1998/11/09 00:13:46  rimassa
  Container list now is an Hashtable instead of a Vector, indexed by a
  String, which is used also as container name in RMA GUI; various
  changes throughout the code to support the new data structure.
  Added some public methods for the AMS to use, such as a method to
  obtain an Enumeration of container names or agent names.

  Revision 1.18  1998/11/03 00:30:25  rimassa
  Added AMS notifications for new agents and dead agents.

  Revision 1.17  1998/11/02 01:58:23  rimassa
  Removed every reference to deleted MessageDispatcher class; now
  AgentContainer is directly responsible for message dispatching.
  Added AMS notifications when an AgentContainer is created or
  deleted.

  Revision 1.16  1998/10/31 16:33:36  rimassa
  Changed AMSKillAgent() prototype, since now it accept also a password
  String (ignored for now).
  Fixed a tiny bug in AMSKillAgent(): 'agentName' was to be
  'simpleName'.

  Revision 1.15  1998/10/26 00:00:30  rimassa
  Added some methods for AMS to use in platform administration. When the
  AMS wants to create or kill an agent it relies on methods such as
  AMSCreateAgent() and AMSKillAgent() to actually do the job.

  Revision 1.14  1998/10/14 21:24:11  Giovanni
  Added a line to restore platform state when a new agent has a name
  clashing with a previous agent's name.

  Revision 1.13  1998/10/11 19:32:30  rimassa
  In method bornAgent() a sensible strategy has been implemented to
  recover from agent name collisions. When a new agent has a name
  already present in Global Agent Table, a name clash exception is
  raised, unless the old agent's container crashed, in which case the
  newer agent simply replaces the older one.
  Now lookup() method is able to distinguish between an unknown agent
  and an agent whose container has crashed, writing a suitable error
  message in the NotFoundException it raises.
  Fixed a missing toLowerCase().

  Revision 1.12  1998/10/07 22:16:21  Giovanni
  Changed code in various places to make agent descriptor tables
  case-insensitive. Now upper or lower case in agent names and addresses
  make no more difference; this is to comply with FIPA specification.

  Revision 1.11  1998/10/04 18:01:01  rimassa
  Added a 'Log:' field to every source file.

*/

package jade.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.Vector;    // FIXME: This will go away

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import jade.domain.ams;
import jade.domain.acc;
import jade.domain.df;
import jade.domain.AgentManagementOntology;

import jade.lang.acl.ACLMessage;

import _FIPA_Agent_97ImplBase;

class AgentPlatformImpl extends AgentContainerImpl implements AgentPlatform, AgentManager {

  private static final String AMS_NAME = "ams";
  private static final String ACC_NAME = "acc";
  private static final String DEFAULT_DF_NAME = "df";

  // Initial size of containers hash table
  private static final int CONTAINERS_SIZE = 10;

  // Initial size of agent hash table
  private static final int GLOBALMAP_SIZE = 100;

  // Load factor of agent hash table
  private static final float GLOBALMAP_LOAD_FACTOR = 0.25f;
  private ThreadGroup systemAgentsThreads = new ThreadGroup("JADE System Agents");


  private ams theAMS;
  private df defaultDF;

  // For now ACC agent and ACC CORBA server are different objects and run
  // within different threads of control.
  private acc theACC;
  private InComingIIOP frontEndACC;

  private Map containers = Collections.synchronizedMap(new HashMap(CONTAINERS_SIZE));
  private Map platformAgents = Collections.synchronizedMap(new HashMap(GLOBALMAP_SIZE, GLOBALMAP_LOAD_FACTOR));

  private class InComingIIOP extends _FIPA_Agent_97ImplBase {
    public void message(String acl_message) {

      try {
      // Recover ACL message object from String
      ACLMessage msg = ACLMessage.fromText(new StringReader(acl_message));
      // Create and handle a suitable communication event
      CommEvent ev = new CommEvent(theACC, msg);
      CommHandle(ev);
      }
      catch (jade.lang.acl.ParseException e) {
	e.printStackTrace();
      }
    }
  }

  public AgentPlatformImpl(String args[]) throws RemoteException {
    super(args);
    myName = AgentManagementOntology.PlatformProfile.MAIN_CONTAINER_NAME;
    systemAgentsThreads.setMaxPriority(Thread.NORM_PRIORITY + 1);
    initIIOP();
    initAMS();
    initACC();
    initDF();
  }

  private void initAMS() {

    theAMS = new ams(this);

    // Subscribe as a listener for the AMS agent
    theAMS.addCommListener(this);

    // Insert AMS into local agents table
    localAgents.put(AMS_NAME, theAMS);

    AgentDescriptor desc = new AgentDescriptor();
    RemoteProxyRMI rp = new RemoteProxyRMI(this);
    desc.setContainerName(myName);
    desc.setProxy(rp);

    String amsName = AMS_NAME + '@' + platformAddress;
    platformAgents.put(amsName.toLowerCase(), desc);

  }

  private void initACC() {
    theACC = new acc();

    // Subscribe as a listener for the AMS agent
    theACC.addCommListener(this);

    // Insert AMS into local agents table
    localAgents.put(ACC_NAME, theACC);

    AgentDescriptor desc = new AgentDescriptor();
    RemoteProxyRMI rp = new RemoteProxyRMI(this);
    desc.setContainerName(myName);
    desc.setProxy(rp);

    String accName = ACC_NAME + '@' + platformAddress;
    platformAgents.put(accName.toLowerCase(), desc);

  }

  private void initIIOP() {

    // Setup CORBA server
    frontEndACC = new InComingIIOP();
    myORB.connect(frontEndACC);

    // Generate and store IIOP URL for the platform
    try {
      OutGoingIIOP dummyChannel = new OutGoingIIOP(myORB, frontEndACC);
      platformAddress = dummyChannel.getIOR();
      System.out.println(platformAddress);

      try {
      	FileWriter f = new FileWriter("JADE.IOR");
      	f.write(platformAddress,0,platformAddress.length());
      	f.close();
      	f = new FileWriter("JADE.URL");
	String iiopAddress = dummyChannel.getURL();
      	f.write(iiopAddress,0,iiopAddress.length());
      	f.close();
      }
      catch (IOException io) {
      	io.printStackTrace();
      }
    }
    catch(IIOPFormatException iiopfe) {
      System.err.println("FATAL ERROR: Could not create IIOP server for the platform");
      iiopfe.printStackTrace();
      System.exit(0);
    }

  }

  private void initDF() {

    defaultDF = new df();

    // Subscribe as a listener for the AMS agent
    defaultDF.addCommListener(this);

    // Insert DF into local agents table
    localAgents.put(DEFAULT_DF_NAME, defaultDF);

    AgentDescriptor desc = new AgentDescriptor();
    RemoteProxyRMI rp = new RemoteProxyRMI(this);
    desc.setContainerName(myName);
    desc.setProxy(rp);

    String defaultDfName = DEFAULT_DF_NAME + '@' + platformAddress;
    platformAgents.put(defaultDfName.toLowerCase(), desc);

  }

  public void joinPlatform(String platformRMI, Vector agentNamesAndClasses) {
    try {
      myPlatform = (AgentPlatform)Naming.lookup(platformRMI);
    }
    catch(Exception e) {
      // Should never happen
      e.printStackTrace();
    }

    containers.put(AgentManagementOntology.PlatformProfile.MAIN_CONTAINER_NAME, this);

    // Notify AMS
    theAMS.postNewContainer(AgentManagementOntology.PlatformProfile.MAIN_CONTAINER_NAME);

    Agent a = theAMS;
    a.powerUp(AMS_NAME, platformAddress, systemAgentsThreads);
    a = theACC;
    a.powerUp(ACC_NAME, platformAddress, systemAgentsThreads);
    a = defaultDF;
    a.powerUp(DEFAULT_DF_NAME, platformAddress, systemAgentsThreads);

    for(int i = 0; i < agentNamesAndClasses.size(); i += 2) {
      String agentName = (String)agentNamesAndClasses.elementAt(i);
      String agentClass = (String)agentNamesAndClasses.elementAt(i+1);
      try {
	createAgent(agentName, agentClass, START);
      }
      catch(RemoteException re) {
	// It should never happen ...
	re.printStackTrace();
      }

    }

    System.out.println("Agent Platform ready to accept new containers...");


  }

  AgentContainer getContainerFromAgent(String agentName) throws NotFoundException {
    AgentDescriptor ad = (AgentDescriptor)platformAgents.get(agentName.toLowerCase());
    if(ad == null) {
      throw new NotFoundException("Agent " + agentName + " not found in getContainerFromAgent()");
    }
    ad.lock();
    String name = ad.getContainerName();
    AgentContainer ac = (AgentContainer)containers.get(name);
    ad.unlock();
    return ac;
  }

  public String getAddress() throws RemoteException {
    return platformAddress;
  }

  public String addContainer(AgentContainer ac) throws RemoteException {

    String name = AgentManagementOntology.PlatformProfile.AUX_CONTAINER_NAME + new Integer(containers.size()).toString();
    containers.put(name, ac);

    // Notify AMS
    theAMS.postNewContainer(name);

    // Return the name given to the new container
    return name;

  }

  public void removeContainer(String name) throws RemoteException {
    containers.remove(name);

    // Notify AMS
    theAMS.postDeadContainer(name);
  }

  public AgentContainer lookup(String name) throws RemoteException, NotFoundException {
    AgentContainer ac = (AgentContainer)containers.get(name);
    if(ac == null)
      throw new NotFoundException("Name Lookup failed: no such container");
    return ac;
  }

  public void bornAgent(String name, RemoteProxy rp, String containerName) throws RemoteException, NameClashException {
    AgentDescriptor desc = new AgentDescriptor();
    desc.setProxy(rp);
    desc.setContainerName(containerName);
    java.lang.Object old = platformAgents.put(name.toLowerCase(), desc);

    // If there's already an agent with name 'name' throw a name clash
    // exception unless the old agent's container is dead.
    if(old != null) {
      AgentDescriptor ad = (AgentDescriptor)old;
      RemoteProxy oldProxy = ad.getProxy();
      try {
	oldProxy.ping(); // Make sure agent is reachable, then raise a name clash exception
	platformAgents.put(name.toLowerCase(), ad);
	throw new NameClashException("Agent " + name + " already present in the platform ");
      }
      catch(UnreachableException ue) {
	System.out.println("Replacing a dead agent ...");
	theAMS.postDeadAgent(ad.getContainerName(), name);
      }
    }

    // Notify AMS
    theAMS.postNewAgent(containerName, name);

  }

  public void deadAgent(String name) throws RemoteException, NotFoundException {
    AgentDescriptor ad = (AgentDescriptor)platformAgents.get(name.toLowerCase());
    if(ad == null)
      throw new NotFoundException("DeadAgent failed to find " + name);
    String containerName = ad.getContainerName();
    platformAgents.remove(name.toLowerCase());

    // Notify AMS
    theAMS.postDeadAgent(containerName, name);
  }

  public RemoteProxy getProxy(String agentName, String agentAddress) throws RemoteException, NotFoundException {

    RemoteProxy rp;
    AgentDescriptor ad = (AgentDescriptor)platformAgents.get(agentName + '@' + agentAddress);

    if(ad == null)
      throw new NotFoundException("getProxy() failed to find " + agentName);
    else {
      ad.lock();
      rp = ad.getProxy();
      ad.unlock();
      try {
	rp.ping();
      }
      catch(UnreachableException ue) {
	throw new NotFoundException("Container for " + agentName + " is unreachable");
      }
      return rp;
    }
  }

  public boolean transferIdentity(String agentName, String src, String dest) throws RemoteException, NotFoundException {
    AgentDescriptor ad = (AgentDescriptor)platformAgents.get(agentName.toLowerCase());
    if(ad == null)
      throw new NotFoundException("transferIdentity() unable to find agent " + agentName);
    AgentContainer srcAC = lookup(src);
    AgentContainer destAC = lookup(dest);
    try {
      srcAC.ping();
      destAC.ping();
    }
    catch(RemoteException re) {
      // Abort transaction
      return false;
    }

    // Commit transaction and notify AMS
    ad.lock();
    ad.setProxy(new RemoteProxyRMI(destAC));
    theAMS.postMovedAgent(agentName, src, dest);
    ad.unlock();
    return true;
  }

  // This method overrides AgentContainerImpl.shutDown(); besides
  // behaving like the normal AgentContainer version, it makes all
  // other agent containers exit.
  public void shutDown() {

    // Deregister yourself as a container
    containers.remove(AgentManagementOntology.PlatformProfile.MAIN_CONTAINER_NAME);

    // Kill every other container
    Collection c = containers.values();
    Object[] allContainers = c.toArray();
    for(int i = 0; i < allContainers.length; i++) {
      AgentContainer ac = (AgentContainer)allContainers[i];
      try {
	APKillContainer(ac); // This call removes 'ac' from 'container' map and from the collection 'c'
      }
      catch(RemoteException re) {
	System.out.println("Container is unreachable. Ignoring...");
      } 
    }

    // Kill all non-system agents
    Set s = localAgents.keySet();
    Object[] allLocalAgents = s.toArray(); 
    for(int i = 0; i < allLocalAgents.length; i++) {
      String name = (String)allLocalAgents[i];
      if(name.equalsIgnoreCase(theAMS.getLocalName()) || 
				 name.equalsIgnoreCase(theACC.getLocalName()) ||
				 name.equalsIgnoreCase(defaultDF.getLocalName()))
					continue;

      // Kill agent and wait for its termination
      Agent a = (Agent)localAgents.get(name);
      a.doDelete();
      a.join();
    }

    // Kill system agents, at last

    Agent systemAgent = defaultDF;
    systemAgent.doDelete();
    systemAgent.join();

    systemAgent = theACC;
    systemAgent.doDelete();
    systemAgent.join();

    theAMS.removeCommListener(this);
    systemAgent = theAMS;
    systemAgent.doDelete();
    systemAgent.join();

    // Now, close CORBA link to outside world
    myORB.disconnect(frontEndACC);

  }

  // These methods dispatch agent management operations to
  // appropriate Agent Container through RMI.

  public void kill(String agentName, String password) throws NotFoundException, UnreachableException {
    try {
      AgentContainer ac = getContainerFromAgent(agentName);
      String simpleName = agentName.substring(0,agentName.indexOf('@'));
      ac.killAgent(simpleName); // RMI call
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  public void APKillContainer(AgentContainer ac) throws RemoteException {
    try {
      ac.exit(); // RMI call
    }
    catch(UnmarshalException ue) {
      // FIXME: This is ignored, since we'd need oneway calls to
      // perform exit() remotely
    }
  }

  public void suspend(String agentName, String password) throws NotFoundException, UnreachableException {
    try {
      AgentContainer ac = getContainerFromAgent(agentName);
      String simpleName = agentName.substring(0,agentName.indexOf('@'));
      ac.suspendAgent(simpleName); // RMI call
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  public void activate(String agentName, String password) throws NotFoundException, UnreachableException {
    try {
      AgentContainer ac = getContainerFromAgent(agentName);
      String simpleName = agentName.substring(0,agentName.indexOf('@'));
      ac.resumeAgent(simpleName); // RMI call
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  public void wait(String agentName, String password) throws NotFoundException, UnreachableException {
    try {
      AgentContainer ac = getContainerFromAgent(agentName);
      String simpleName = agentName.substring(0,agentName.indexOf('@'));
      ac.waitAgent(simpleName); // RMI call
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  public void wake(String agentName, String password) throws NotFoundException, UnreachableException {
    try {
      AgentContainer ac = getContainerFromAgent(agentName);
      String simpleName = agentName.substring(0,agentName.indexOf('@'));
      ac.wakeAgent(simpleName); // RMI call
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  public void move(String agentName, String containerName, String password ) throws NotFoundException, UnreachableException {
    // FIXME: Not implemented
    // Lookup the container for 'agentName', throwing NotFoundException on failure
    // Tell the src container to send the agent code and data to the dest container
    // Update GADT to reflect new agent location
  }

  public void copy(String agentName, String containerName, String newAgentName, String password) throws NotFoundException, UnreachableException {
    // Retrieve the container for the original agent
    AgentContainer src = getContainerFromAgent(agentName);
    try {
      int atPos = agentName.indexOf('@');
      if(atPos != -1)
	agentName = agentName.substring(0,atPos);

      src.copyAgent(agentName, containerName, newAgentName); // RMI call
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  // These methods are to be used only by AMS agent.


  // This is used by AMS to obtain the set of all the Agent Containers of the platform.
  public Set containerNames() {
    return containers.keySet();
  }

  // This is used by AMS to obtain the list of all the agents of the platform.
  public Set agentNames() {
    return platformAgents.keySet();
  }

  // This maps the name of an agent to the name of the Agent Container the agent lives in.
  public String getContainerName(String agentName) throws NotFoundException {
    AgentDescriptor ad = (AgentDescriptor)platformAgents.get(agentName.toLowerCase());
    if(ad == null)
      throw new NotFoundException("Agent " + agentName + " not found in getContainerName()");
    ad.lock();
    String result = ad.getContainerName();
    ad.unlock();
    return result;
  }

  // This maps the name of an agent to its IIOP address.
  public String getAddress(String agentName) {
    // FIXME: Should not even exist; it would be better to put the
    // complete agent name in the hash table
    return platformAddress; 
  }

  // This is called in response to a 'create-agent' action
  public void create(String agentName, String className, String containerName) throws UnreachableException {
    String simpleName = agentName.substring(0,agentName.indexOf('@'));
    try {
      AgentContainer ac;
      // If no name is given, the agent is started on the AgentPlatform itself
      if(containerName == null)
	ac = this; 
      else
	ac = (AgentContainer)containers.get(containerName);

      // If a wrong name is given, then again the agent starts on the AgentPlatform itself
      if(ac == null)
	ac = this;
      ac.createAgent(simpleName, className, START); // RMI call
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  public void create(String agentName, Agent instance, String containerName) throws UnreachableException {
    String simpleName = agentName.substring(0,agentName.indexOf('@'));
    try {
      AgentContainer ac = (AgentContainer)containers.get(containerName);
      ac.createAgent(simpleName, instance, START); // RMI call, 'instance' is serialized
    }
    catch(ArrayIndexOutOfBoundsException aioobe) {
      throw new UnreachableException(aioobe.getMessage());
    }
    catch(RemoteException re) {
      throw new UnreachableException(re.getMessage());
    }
  }

  public void killContainer(String containerName) {

    // This call spawns a separate thread in order to avoid deadlock.
    final AgentContainer ac = (AgentContainer)containers.get(containerName);
    final String cName = containerName;
    Thread auxThread = new Thread(new Runnable() {
      public void run() {
	try {
	  APKillContainer(ac);
	}
	catch(RemoteException re) {
	  System.out.println("Container " + cName + " is unreachable.");
	  containers.remove(cName);
	  theAMS.postDeadContainer(cName);

	}
      }
    });
    auxThread.start();
  }


  public void sniffOn(String SnifferName, Map ToBeSniffed) throws UnreachableException  {

    Collection myContainersColl = containers.values();
    Iterator myContainers = myContainersColl.iterator();

    while (myContainers.hasNext()) {
      try {
	AgentContainer ac = (AgentContainer)myContainers.next();
	ac.enableSniffer(SnifferName, ToBeSniffed); // RMI call
      }
      catch (RemoteException re) {
	throw new UnreachableException(re.getMessage());
      } 
    }
  }

  public void sniffOff(String SnifferName, Map NotToBeSniffed) throws UnreachableException {

    Collection myContainersColl = containers.values();
    Iterator myContainers = myContainersColl.iterator();

    while (myContainers.hasNext()) {
      try {
	AgentContainer ac = (AgentContainer)myContainers.next();
	ac.disableSniffer(SnifferName, NotToBeSniffed); // RMI call
      }
      catch (RemoteException re) {
	throw new UnreachableException(re.getMessage());
      }
    }
  }


}

