package proto.responderBehaviours.propose;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import test.common.TestUtility;

/**
   @author Jerome Picault - Motorola Labs
 */
public class NotUnderstoodReplier extends ProposeResponder {
	public NotUnderstoodReplier() {
		super(null, createMessageTemplate(FIPANames.InteractionProtocol.FIPA_PROPOSE));
	}
	
  protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
		TestUtility.log(myAgent.getName()+": prepare response");
  	ACLMessage response = request.createReply();
		response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		return response;
  }
}
