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


import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import jade.core.HorizontalCommand;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.Node;
import jade.core.IMTPException;

import jade.util.leap.Map;
import jade.util.leap.HashMap;


/**
   @author Giovanni Rimassa - FRAMeTech s.r.l.
 */
public class NodeRMIImpl extends UnicastRemoteObject implements NodeRMI {

    public NodeRMIImpl(NodeAdapter impl, int port) throws RemoteException {
	super(port);
	myNode = impl;
    }

    public Object accept(HorizontalCommand cmd, Class itf, Class[] classes) throws RemoteException, IMTPException {

	try {
	    if(terminating) {
		throw new IMTPException("Dead node");
	    }
	    return myNode.serve(cmd);
	}
	catch(ServiceException se) {
	    throw new IMTPException("Service Error", se);
	}
    }

    public boolean ping(boolean hang) throws RemoteException {
      if(hang) {
	  waitTermination();
      }
      return terminating;
    }

    public void exit() throws RemoteException {
	// Unblock threads hung in ping() method (this will deregister the container)
	terminating = true;
	notifyTermination();
    }

    public void interrupt() throws RemoteException {
	notifyTermination();
    }

    private void waitTermination() {
	synchronized(terminationLock) {
	    try {
		terminationLock.wait();
	    }
	    catch(InterruptedException ie) {
		System.out.println("PING wait interrupted");
		// Do nothing
	    }
	}
    }

    private void notifyTermination() {
      synchronized(terminationLock) {
	  terminationLock.notifyAll();
      }
    }


    // This monitor is used to hang a remote ping() call in order to
    // detect node failures.
    private Object terminationLock = new Object();
    private boolean terminating = false;

    private NodeAdapter myNode;

}
