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

package jade.imtp.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import jade.core.AID;
import jade.core.ContainerID;
import jade.core.AgentContainer;
import jade.core.MainContainer;
import jade.core.IMTPException;
import jade.core.NotFoundException;
import jade.core.NameClashException;
import jade.core.AgentProxy;

/**
   @author Giovanni Rimassa - Universita` di Parma
   @version $Date$ $Revision$
 */
public class MainContainerRMIImpl extends UnicastRemoteObject implements MainContainerRMI {

    private MainContainer impl;
    private RMIIMTPManager manager;

    /** Creates new MainContainerRMIImpl */
    public MainContainerRMIImpl(MainContainer mc, RMIIMTPManager mgr) throws RemoteException {
      impl = mc;
      manager = mgr;
    }

    public AgentContainerRMI lookup(ContainerID cid) throws RemoteException, NotFoundException, IMTPException {
      AgentContainer ac = impl.lookup(cid);
      return manager.getRMIStub(ac);
    }

    public void deadMTP(String mtpAddress, ContainerID cid) throws RemoteException, IMTPException {
      impl.deadMTP(mtpAddress, cid);
    }

    public AgentProxy getProxy(AID id) throws RemoteException, NotFoundException, IMTPException {
      return impl.getProxy(id);
    }

    public boolean transferIdentity(AID agentID, ContainerID src, ContainerID dest) throws RemoteException, NotFoundException, IMTPException {
      return impl.transferIdentity(agentID, src, dest);
    }

    public String getPlatformName() throws RemoteException, IMTPException {
      return impl.getPlatformName();
    }

    public void bornAgent(AID name, ContainerID cid) throws RemoteException, NameClashException, NotFoundException, IMTPException {
      impl.bornAgent(name, cid);
    }
    
    public void removeContainer(ContainerID cid) throws RemoteException, IMTPException {
      impl.removeContainer(cid);
    }
    
    public String addContainer(AgentContainerRMI ac, ContainerID cid) throws RemoteException, IMTPException {
      AgentContainer cont = manager.getAdapter(ac);
      return impl.addContainer(cont, cid);
    }

    public void deadAgent(AID name) throws RemoteException, NotFoundException, IMTPException {
      impl.deadAgent(name);
    }
    
    public void newMTP(String mtpAddress, ContainerID cid) throws RemoteException, IMTPException {
      impl.deadMTP(mtpAddress, cid);
    }
    
}
