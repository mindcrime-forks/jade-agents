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

import java.rmi.*;
import java.rmi.registry.*;

import java.util.Iterator;
/**
@author Giovanni Rimassa - Universita` di Parma
@version $Date$ $Revision$
*/

/**
   This utility class is a <em>Facade</em> to JADE runtime system and
   is only used to start up JADE.
   @see jade.Boot
 */
public class Starter {

  // Private constructor to forbid instantiation
  private Starter() {
  }

  /**
     Starts up a suitable JADE runtime system, according to its
     parameters.
     @param isPlatform <code>true</code> if <code>-platform</code> is
     given on the command line, <code>false</code> otherwise.
     @param platformID An <em>globally unique ID</em> for the
     platform, built from JADE default settings and command line
     parameters.
     @param agents An <code>Iterator</code> containing names and classes
     of the agents to fire up during JADE startup.
     @ param MTPs is an array of Strings, as parsed from the command line,
     containing names and arguments to the MTPs to be activated
     @ param ACLCodecs is an array of Strings, as parsed from the command line,
     containing the class names of the ACLCodecs to be activated
     @param args Command line arguments, used by CORBA ORB.
  */
  public static void startUp(boolean isPlatform, String platformID, String host, int port, Iterator agents, String[] MTPs,String[] ACLCodecs ) {
      AgentContainerImpl theContainer;

      try{
	  // Configure the JADE runtime so that it exits the VM when
	  // there are no more active containers.
	  Runtime.instance().setCloseVM(true);

	  Profile p = new ProfileImpl(host, (new Integer(port)).toString(), platformID);

	  String platformRMI = "rmi://" + host + ":" + port + "/JADE";

          theContainer = new AgentContainerImpl(p);
          MainContainer mc;

	  if(isPlatform) {
	      // Create an embedded RMI Registry within the platform and
	      // bind the Main Container to it.
              mc = new MainContainerImpl(p);

              jade.imtp.rmi.MainContainerRMI mcRMI = new jade.imtp.rmi.MainContainerRMIImpl((MainContainerImpl)mc);
	      Registry theRegistry = LocateRegistry.createRegistry(port);
	      Naming.bind(platformRMI, mcRMI);
	  }
	  else {
            // Look the remote Main Container up into the
            // RMI Registry, then create a Smart Proxy for it.
            jade.imtp.rmi.MainContainerRMI remoteMCRMI = (jade.imtp.rmi.MainContainerRMI)Naming.lookup(platformRMI);
            MainContainer remoteMC = new jade.imtp.rmi.MainContainerAdapter(remoteMCRMI);
            mc = new MainContainerProxy(remoteMC);
          }

          Runtime.instance().beginContainer();
          theContainer.joinPlatform(mc, agents, MTPs, ACLCodecs);

	  Runtime.instance().setDefaultToolkit(theContainer); // FIXME: Temporary hack for JSP example

      }catch(ConnectException ce) {
      // This one is thrown when trying to bind in an RMIRegistry that
      // is not on the current host
      System.out.println("ERROR: trying to bind to a remote RMI registry.");
      System.out.println("If you want to start a JADE main container:");
      System.out.println("  Make sure the specified host name or IP address belongs to the local machine.");
      System.out.println("  Please use '-host' and/or '-port' options to setup JADE host and port.");
      System.out.println("If you want to start a JADE non-main container: ");
      System.out.println("  Use the '-container' option, then use '-host' and '-port' to specify the ");
      System.out.println("  location of the main container you want to connect to.");
      System.exit(1);
    }
    catch(RemoteException re) {
      System.err.println("Communication failure while starting JADE Runtime System.");
      re.printStackTrace();
    }
    catch(Exception e) {
      System.err.println("Some other error while starting JADE Runtime System.");
      e.printStackTrace();
    }

  }

}
