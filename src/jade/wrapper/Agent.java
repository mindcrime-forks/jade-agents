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

package jade.wrapper;

import jade.util.leap.LinkedList;

import jade.core.AID;
import jade.core.Location;


/**

   This class is a Proxy class, allowing access to a JADE agent.
   Invoking methods on instances of this class, it is possible to
   trigger state transition of the agent life cycle.  This class must
   not be instantiated by applications. Instead, use the
   <code>createAgent()</code> method in class
   <code>AgentContainer</code>.
   @see jade.wrapper.AgentContainer#createAgent(String nickname, String className, Object[] args) throws NotFoundException, StaleProxyException 
   @author Giovanni Rimassa - Universita` di Parma
 */
public class Agent {

  /**
     Constant representing an asynchronous rendez-vous policy.
     @see jade.wrapper.Agent#putO2AObject(Object o)
   */
  public static final boolean ASYNC = false;

  /**
     Constant representing a synchronous rendez-vous policy.
     @see jade.wrapper.Agent#putO2AObject(Object o)
   */
  public static final boolean SYNC = true;

  private AID agentID;
  private jade.core.Agent adaptee;

  /**
     Default constructor. This should not be called by applications,
     but the method <code>AgentContainer.createAgent()</code> should
     be used instead.

     @see jade.wrapper.AgentContainer#createAgent(String nickname, String className, Object[] args) throws NotFoundException, StaleProxyException 
     @param a A real JADE agent, that will be wrapped by this proxy.
   */
  Agent(AID id, jade.core.Agent a) {
    agentID = id;
    adaptee = a;
  }


  /**
     Triggers a state transition from <b>INITIATED</b> to
     <b>ACTIVE</b>. This call also starts the internal agent
     thread. If this call is performed on an already started agent,
     nothing happens.
     @exception StaleProxyException If the underlying agent is dead or
     gone.
   */
  public void start() throws StaleProxyException {
    if(adaptee == null)
      throw new StaleProxyException();
    adaptee.doStart(agentID.getLocalName());
  }

  /**
     Triggers a state transition from <b>ACTIVE</b> to
     <b>SUSPENDED</b>.
     @exception StaleProxyException If the underlying agent is dead or
     gone.
   */
  public synchronized void suspend() throws StaleProxyException {
    if(adaptee == null)
      throw new StaleProxyException();
    adaptee.doSuspend();    
  }

  /**
     Triggers a state transition from <b>SUSPENDED</b> to
     <b>ACTIVE</b>.
     @exception StaleProxyException If the underlying agent is dead or
     gone.
   */
  public void activate() throws StaleProxyException {
    if(adaptee == null)
      throw new StaleProxyException();
    adaptee.doActivate();
  }

  /**
     Triggers a state transition from <b>ACTIVE</b> to
     <b>DELETED</b>. This call also stops the internal agent thread
     and fully terminates the agent. If this call is performed on an
     already terminated agent, nothing happens.
     @exception StaleProxyException If the underlying agent is dead or
     gone.
   */
  public void delete() throws StaleProxyException {
    if(adaptee == null)
      throw new StaleProxyException();
    adaptee.doDelete();
    adaptee = null;
  }

  /**
     Triggers a state transition from <b>ACTIVE</b> to
     <b>TRANSIT</b>. This call also moves the agent code and data to
     another container. This calls terminates the locally running
     agent, so that this proxy object becomes detached from the moved
     agent that keeps on executing elsewhere (i.e., no proxy
     remotization is performed).

     @param where A <code>Location</code> object, representing the
     container the agent should move to.
     @exception StaleProxyException If the underlying agent is dead or
     gone.
  */
  public void move(Location where) throws StaleProxyException {
    if(adaptee == null)
      throw new StaleProxyException();
    adaptee.doMove(where);
    adaptee = null; // FIXME: Should check whether the migration transaction succeeded
  }


  /**
     Clones the current agent. Calling this method does not really
     trigger a state transition in the current agent
     lifecycle. Rather, it creates another agent on the given
     location, that is just a copy of this agent.

     @param where The <code>Location</code> object, representing the
     container where the new agent copy will start.
     @param newName The new nickname to give to the copy.
     @exception StaleProxyException If the underlying agent is dead or
     gone.
   */
  public void clone(Location where, String newName) throws StaleProxyException {
    if(adaptee == null)
      throw new StaleProxyException();
    adaptee.doClone(where, newName);
  }

  /**
     Passes an application-specific object to a local agent, created
     using JADE In-Process Interface. The object will be put into an
     internal agent queue, from where it can be picked using the
     <code>jade.core.Agent.getO2AObject()</code> method. The agent
     must first declare its will to accept passed objects, using the
     <code>jade.core.Agent.setEnabledO2ACommunication()</code> method.
     @param o The object to put in the private agent queue.
     @param blocking A flag, stating the desired rendez-vous policy;
     it can be <code>ASYNC</code>, for a non-blocking call, returning
     right after putting the object in the quque, or
     <code>SYNC</code>, for a blocking call that does not return until
     the agent picks the object from the private queue.
     @see jade.core.Agent#getO2AObject()
     @see jade.core.Agent#setEnabledO2ACommunication(boolean enabled, int queueSize)
   */
  public void putO2AObject(Object o, boolean blocking) throws InterruptedException {
    adaptee.putO2AObject(o, blocking);
  }

}
