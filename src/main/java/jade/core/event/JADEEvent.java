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

package jade.core.event;

import java.util.Date;

import jade.core.ContainerID;
import jade.util.Event;

/**
 * This class represents a generic JADE event.
 *
 * @author Giovanni Rimassa - Universita` di Parma
 * @author David I. Bell, Dick Cowan - Hewlett-Packard
 *
 * @version $Date$ $Revision$
 */
public class JADEEvent extends Event {

	private Date time;

	public JADEEvent(int type, ContainerID cid) {
		super(type, cid);
		time = new Date(); // Now
	}

	public ContainerID getPlace() {
		return (ContainerID)(getSource());
	}

	public Date getTime() {
		return time;
	}

}
