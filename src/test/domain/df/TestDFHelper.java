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

package test.domain.df;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestDFHelper {
	
	public static DFAgentDescription getSampleDFD(Agent a) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(a.getAID());
		dfd.addOntologies("Test-ontology");
		dfd.addLanguages("Test-language");
		dfd.addProtocols("Test-protocol");
		
		// Service 1
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setName("Test-service-1");
		sd1.setType("Test-type-1");
		sd1.addOntologies("Test-ontology-1.1");
		sd1.addOntologies("Test-ontology-1.2");
		sd1.addLanguages("Test-language-1.1");
		sd1.addLanguages("Test-language-1.2");
		sd1.addProtocols("Test-protocol-1");
		dfd.addServices(sd1);
		
		// Service 2
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setName("Test-service-2");
		sd2.setType("Test-type-2");
		sd2.addOntologies("Test-ontology-2");
		sd2.addLanguages("Test-language-2");
		sd2.addProtocols("Test-protocol-2.1");
		sd2.addProtocols("Test-protocol-2.2");
		dfd.addServices(sd2);
		
		return dfd;
	}
	
	public static DFAgentDescription getSampleTemplate1() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Test-type-1");
		dfd.addServices(sd);
		return dfd;
	}
	
	public static boolean compare(DFAgentDescription dfd1, DFAgentDescription dfd2) {
		return true;
	}
}
