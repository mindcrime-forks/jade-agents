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

package jade.core;

import jade.security.AgentPrincipal;


/**
@author Giovanni Rimassa - Universita` di Parma
@version $Date$ $Revision$
*/

class AgentDescriptor {

  private AgentProxy proxy;
  private ContainerID containerID;
  private AgentPrincipal principal;
  private boolean locked = false;

  public void setProxy(AgentProxy rp) {
    proxy = rp;
  }

  public AgentProxy getProxy() {
    return proxy;
  }

  public void setContainerID(ContainerID cid) {
    containerID = cid;
  }

  public ContainerID getContainerID() {
    return containerID;
  }

  public void setPrincipal(AgentPrincipal p) {
    principal = p;
  }

  public AgentPrincipal getPrincipal() {
    return principal;
  }

  public synchronized void lock() {
    while(locked) {
      try {
	wait();
      }
      catch(InterruptedException ie) {
	ie.printStackTrace();
      }
    }
    locked = true;
  }

  public synchronized void unlock() {
    locked = false;
    notifyAll();
  }

}
