/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/**
 * ************************************************************************
 * The LEAP libraries, when combined with certain JADE platform components,
 * provide a run-time environment for enabling FIPA agents to execute on
 * lightweight devices running Java. LEAP and JADE teams have jointly
 * designed the API for ease of integration and hence to take advantage
 * of these dual developments and extensions so that users only see
 * one development platform and a
 * single homogeneous set of APIs. Enabling deployment to a wide range of
 * devices whilst still having access to the full development
 * environment and functionalities that JADE provides.
 * Copyright (C) 2001 Motorola.
 * Copyright (C) 2001 Telecom Italia LAB S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * ************************************************************************
 */
package jade.imtp.leap.JICP;

//#MIDP_EXCLUDE_FILE

import jade.imtp.leap.ICP;
import jade.imtp.leap.ICPException;
import jade.util.leap.Properties;
import java.net.Socket;

/**
 * @author Giovanni Caire - Telecom Italia LAB S.p.A.
 */
public interface JICPMediator {
	// Mediator shut down modes
	public static final int NORMAL = 1;
	public static final int EXPIRED = 0;
	public static final int KILLED = -1;
	
  /**
     Initialize this JICPMediator
   */
  void init(JICPServer srv, String id, Properties props) throws ICPException;
  
  /**
     Make this JICPMediator terminate
   */
  void shutdown(int mode); 

  /**
   * Sets the socket connected to the mediated entity.
   * This is called by the JICPServer this Mediator is attached to
   * as soon as the mediated entity (re)connects.
   * @param s the socket connected to the mediated container
   */
  void setConnection(Socket s);
  
  /**
     Handle a JICP packet that was received by the JICPServer this
     JICPMediator is attached to.
   */
  JICPPacket handleJICPPacket(JICPPacket p) throws ICPException; 
}

