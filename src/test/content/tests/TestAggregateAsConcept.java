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

package test.content.tests;

import test.content.Test;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.content.*;
import jade.content.abs.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import test.content.testOntology.*;
import examples.content.ecommerceOntology.*;
import examples.content.musicShopOntology.*;

public class TestAggregateAsConcept extends Test{
  public String getName() {
  	return "Aggregate-as-concept";
  }
  public String getDescription() {
  	StringBuffer sb = new StringBuffer("Tests a content with an aggregate where a concept is required");
  	return sb.toString();
  }
  public int execute(ACLMessage msg,  Agent a, boolean verbose) {
  	try {
  		AbsAggregate agg = new AbsAggregate(BasicOntology.SEQUENCE);
  		AbsConcept t = new AbsConcept(MusicShopOntology.TRACK);
  		t.set(MusicShopOntology.TRACK_NAME, "Every breath you take");
  		agg.add(t);
  		
  		AbsPredicate e = new AbsPredicate(TestOntology.EXISTS);
  		// Compilation should allows that as AbsAggregate extends AbsConcept
  		// However an OntologyException should be thrown as an 
  		// aggregate is not a concept
  		e.set(TestOntology.EXISTS_WHAT, agg);
  		a.getContentManager().fillContent(msg, e);
  		// We should get an exception here
  		return DONE_FAILED;
  	}
  	catch (OntologyException oe) {
  		System.out.println("Ontology exception thrown as expected: "+oe.getMessage());
  		if (verbose) {
  			oe.printStackTrace();
  		}
  		return DONE_PASSED;
  	}
  	catch (Throwable t) {
  		if (verbose) {
  			t.printStackTrace();
  		}
  		return DONE_FAILED;
  	}
  }
}
