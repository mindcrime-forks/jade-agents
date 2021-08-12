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
public class NullReplier extends ProposeResponder {
	public NullReplier() {
		super(null, createMessageTemplate(FIPANames.InteractionProtocol.FIPA_PROPOSE));
	}
	
  protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
		return null;
  }
}
