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

import jade.util.leap.Serializable;


/**
   This interface represents a node of a JADE platform (i.e. a
   component that can host a slice of a platform-level service).
   Concrete nodes for a platform are created by the concrete IMTP
   manager.

   @see jade.core.IMTPManager

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public interface Node extends Serializable {

    void setName(String name);
    String getName();

    void exportSlice(String serviceName, Service.Slice localSlice);
    void unexportSlice(String serviceName);

    /**
       Accepts a command. If this node is a proxy, the
       <code>accept()</code> method is a remote method, forwarding the
       command to the remote location it represents.
       @param cmd The horizontal command to process.
       @return The object that is the result of processing the command.
       @throws IMTPException If a communication error occurs while
       contacting the remote node.
    */
    Object accept(HorizontalCommand cmd) throws IMTPException;

    /**
       Serves an incoming horizontal command, locally. This method is
       never remote, and uses the table of locally active service
       slices to find the target of this command (looking at the
       service attribute of the command).
       @param cmd The horizontal command to process.
       @return The object that is the result of processing the command.
       @throws ServiceException If the service the command belongs to
       is not present on this node.
    */
    Object serve(HorizontalCommand cmd) throws ServiceException;

    /**
       Serves an incoming vertical command, locally. This method is
       invoked if a new <code>VerticalCommand</code> object is
       generated by a slice targetted by a former
       <code>HorizontalCommand</code>, which happens if the
       <code>Slice.serve()</code> yields a non-null result.

       This method makes it so that the newly created vertical command
       is handed to the command processor to first pass through all
       incoming filters and then to be dispatched to its proper
       incoming command sink.
       @param cmd The vertical command to process.
       @return The object that is the result of processing the command.
       @throws ServiceException If some problem occurs.
    */
    Object serve(VerticalCommand cmd) throws ServiceException;

    void ping(boolean hang) throws IMTPException;
    void exit() throws IMTPException;

}
