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


package jade.domain.introspection;

import jade.content.Concept;
import jade.core.Channel;

/**
   An introspection event, recording the routing of an ACL message
   within the platform.

   @author Giovanni Rimassa -  Universita' di Parma
   @version $Date$ $Revision$
*/
public class RoutedMessage implements Concept {

    private Channel from;
    private Channel to;
    private ACLMessage message;

    public void setFrom(Channel c) {
	from = c;
    }

    public Channel getFrom() {
	return from;
    }

    public void setTo(Channel c) {
	to = c;
    }

    public Channel getTo() {
	return to;
    }

    public void setMessage(ACLMessage msg) {
	message = msg;
    }

    public ACLMessage getMessage() {
	return message;
    }

}
