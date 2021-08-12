package roundTripTime;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RoundTripReceiver extends Agent {

    void roundTripTime() {
        ACLMessage msg = blockingReceive();
        msg = msg.createReply();
        send(msg);
    }

    public void setup() {

        addBehaviour( new CyclicBehaviour(this)
        {
            public void action() {
                roundTripTime();
            }
        });
    }
}
