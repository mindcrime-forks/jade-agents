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

package proto.responderBehaviours.contractNet;

import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetResponder;

/**
   @author Giovanni Caire - TILAB
 */
public class ProposeInformReplier extends ContractNetResponder {
	public ProposeInformReplier() {
		super(null, createMessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET));
	}

	@Override
	protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
		ACLMessage response = cfp.createReply();
		response.setPerformative(ACLMessage.PROPOSE);
		return response;
	}


	@Override
	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept ) throws FailureException {
		ACLMessage resNotif = accept.createReply();
		resNotif.setPerformative(ACLMessage.INFORM);
		return resNotif;
	}
}