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

package content.tests;

import test.common.*;
import content.*;
import content.testOntology.Exists;
import examples.content.ecommerceOntology.*;
import jade.content.ContentManager;
import jade.content.abs.*;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import test.common.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestSlotOrder extends Test{  
  
  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException {
  	final Logger l = Logger.getLogger();
  	
  	try {
  		final ACLMessage msg = (ACLMessage) getGroupArgument(ContentTesterAgent.MSG_NAME);
  		return new SuccessExpectedInitiator(a, ds, resultKey) {
  			protected ACLMessage prepareMessage() throws Exception {
  				AbsConcept i = new AbsConcept(ECommerceOntology.ITEM);
  				i.set(ECommerceOntology.ITEM_SERIALID, 35624);
  				
  				AbsConcept p = new AbsConcept(ECommerceOntology.PRICE);
  				p.set(ECommerceOntology.PRICE_VALUE, 5.37);
  				p.set(ECommerceOntology.PRICE_CURRENCY, "Euro");
  				
  				AbsPredicate c = new AbsPredicate(ECommerceOntology.COSTS);
  				// Set the price before the item
  				c.set(ECommerceOntology.COSTS_PRICE, p);
  				c.set(ECommerceOntology.COSTS_ITEM, i);
  				
  				myAgent.getContentManager().fillContent(msg, c);
  				l.log("Content correctly encoded");
  				l.log(msg.getContent());
  				return msg;
  			}
  			
  			protected boolean checkReply(ACLMessage reply) throws Exception {
  				Costs c = (Costs) myAgent.getContentManager().extractContent(reply);
  				l.log("Content correctly decoded");
  				Price p = c.getPrice();
  				Item i = c.getItem();
  				l.log("Content OK");
  				return true;
  			}
  		};
  	}
  	catch (Exception e) {
  		throw new TestException("Wrong group argument", e);
  	}
  }

}
