/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

The updating of this file to JADE 2.0 has been partially supported by the IST-1999-10211 LEAP Project

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

package jade.domain;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/** Common base class for AMS and DF Knowledge Base*/
abstract class KBAbstractImpl implements KB {

  private Map facts = new HashMap();

  public Object register(Object name, Object fact) {
    return facts.put(name, fact);
  }

  public Object deregister(Object name) {
    return facts.remove(name);
  }

  // This abstract method has to perform pattern matching
  protected abstract boolean match(Object template, Object fact);

  public List search(Object template) {
    List result = new LinkedList();
    Iterator it = facts.values().iterator();
    while(it.hasNext()) {
      Object fact = it.next();
      if(match(template, fact))
	result.add(fact);
    }
    return result;
  }

  // Helper method to match two Agent Identifiers
  protected final boolean matchAID(AID template, AID fact) {

    // Match the GUID in the ':name' slot
    String templateName = template.getName();
    if(templateName != null) {
      String factName = fact.getName();
      if((factName == null) || (!templateName.equalsIgnoreCase(factName)))
	return false;
    }


    // Match the address sequence. See 'FIPA Agent Management Specification, Sect. 6.4.2.1'
    Iterator itTemplate = template.getAllAddresses();
    Iterator itFact = fact.getAllAddresses();

    // All the elements in the template sequence must appear in the
    // fact sequence, in the same order
    while(itTemplate.hasNext()) {
      String templateAddr = (String)itTemplate.next();

      // Search 'templateAddr' into the remaining part of the fact sequence
      boolean found = false;
      while(!found && itFact.hasNext()) {
	String factAddr = (String)itFact.next();
	found = templateAddr.equalsIgnoreCase(factAddr);
      }
      if(!found) // An element of the template does not appear in the fact sequence
	return false;
    }


    // Match the resolvers sequence. See 'FIPA Agent Management Specification, Sect. 6.4.2.1'
    itTemplate = template.getAllResolvers();
    itFact = fact.getAllResolvers();

    while(itTemplate.hasNext()) {
      AID templateRes = (AID)itTemplate.next();

      // Search 'templateRes' into the remaining part of the fact sequence
      boolean found = false;
      while(!found && itFact.hasNext()) {
	AID factRes = (AID)itFact.next();
	found = matchAID(templateRes, factRes); // Recursive call
      }
      if(!found) // An element of the template does not appear in the fact sequence
	return false;
    }

    return true;
  }


  // Helper method to match two Service Description objects
  protected final boolean matchServiceDesc(ServiceDescription template, ServiceDescription fact) {

    // Match name
    String n1 = template.getName();
    if(n1 != null) {
      String n2 = fact.getName();
      if((n2 == null) || (!n1.equalsIgnoreCase(n2)))
	return false;
    }

    // Match type
    String t1 = template.getType();
    if(t1 != null) {
      String t2 = fact.getType();
      if((t2 == null) || (!t1.equalsIgnoreCase(t2)))
	return false;
    }

    // Match ownership
    String o1 = template.getOwnership();
    if(o1 != null) {
      String o2 = fact.getOwnership();
      if((o2 == null) || (!o1.equalsIgnoreCase(o2)))
	return false;
    }

    // Match ontologies set
    Iterator itTemplate = template.getAllOntologies();
    while(itTemplate.hasNext()) {
      String templateOnto = (String)itTemplate.next();
      boolean found = false;
      Iterator itFact = fact.getAllOntologies();
      while(!found && itFact.hasNext()) {
	String factOnto = (String)itFact.next();
	found = templateOnto.equalsIgnoreCase(factOnto);
      }
      if(!found)
	return false;
    }

    // Match languages set
    itTemplate = template.getAllLanguages();
    while(itTemplate.hasNext()) {
      String templateLang = (String)itTemplate.next();
      boolean found = false;
      Iterator itFact = fact.getAllLanguages();
      while(!found && itFact.hasNext()) {
	String factLang = (String)itFact.next();
	found = templateLang.equalsIgnoreCase(factLang);
      }
      if(!found)
	return false;
    }

    // Match protocols set
    itTemplate = template.getAllProtocols();
    while(itTemplate.hasNext()) {
      String templateProto = (String)itTemplate.next();
      boolean found = false;
      Iterator itFact = fact.getAllProtocols();
      while(!found && itFact.hasNext()) {
	String factProto = (String)itFact.next();
	found = templateProto.equalsIgnoreCase(factProto);
      }
      if(!found)
	return false;
    }

    // FIXME: Should try to match the Properties, too ?

    return true;
  }


/*************** Main method, for testing purposes ******************************
  public static void main(String[] args) {

      KBAbstractImpl kb = new KBAbstractImpl() {
	protected boolean match(Object template, Object match) { return false; }
      };

      AID id1 = new AID();
      id1.setName("AbCd");
      id1.addAddresses("URL_abcd");
      id1.addAddresses("URL_efgh");
      AID res1 = new AID();
      res1.setName("Res1");
      id1.addResolvers(res1);
      AID res2 = new AID();
      res2.setName("Res2");
      id1.addResolvers(res2);

      AID template = new AID();
      template.setName("aaaa");
      System.out.println("Match 1 [FALSE]: " + kb.matchAID(template, id1));
      template.setName("AbCd");
      System.out.println("Match 2 [TRUE]: " + kb.matchAID(template, id1));
      template.setName("abcd");
      System.out.println("Match 3 [TRUE]: " + kb.matchAID(template, id1));
      template.addAddresses("URL_efgh");
      System.out.println("Match 4 [TRUE]: " + kb.matchAID(template, id1));
      template.addAddresses("URL_abcd");
      System.out.println("Match 5 [FALSE]: " + kb.matchAID(template, id1));
      template.clearAllAddresses();
      template.addAddresses("url_abcd");
      template.addAddresses("URL_efGh");
      System.out.println("Match 6 [TRUE]: " + kb.matchAID(template, id1));
      template.addAddresses("XXX");
      System.out.println("Match 7 [FALSE]: " + kb.matchAID(template, id1));
      template.clearAllAddresses();
      template.addResolvers(res2);
      System.out.println("Match 8 [TRUE]: " + kb.matchAID(template, id1));
      template.addResolvers(res1);
      System.out.println("Match 9 [FALSE]: " + kb.matchAID(template, id1));
      template.clearAllResolvers();
      template.addResolvers(res1);
      template.addResolvers(res2);
      System.out.println("Match 10 [TRUE]: " + kb.matchAID(template, id1));
      AID res3 = new AID();
      res3.setName("pippo");
      template.addResolvers(res3);
      System.out.println("Match 11 [FALSE]: " + kb.matchAID(template, id1));
  }

**********************************************************************************/

}