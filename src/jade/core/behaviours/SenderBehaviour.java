/*
  $Log$
  Revision 1.2  1999/06/22 13:14:36  rimassa
  Removed a redundant private instance variable.

  Revision 1.1  1999/05/20 13:43:17  rimassa
  Moved all behaviour classes in their own subpackage.

  Revision 1.6  1999/04/06 00:09:44  rimassa
  Documented public classes with Javadoc. Reduced access permissions wherever possible.

  Revision 1.5  1999/02/25 08:31:06  rimassa
  Changed a getName() to getLocalName() call.

  Revision 1.4  1998/10/10 19:17:13  rimassa
  Fixed some compilation errors.

  Revision 1.3  1998/10/05 20:15:02  Giovanni
  Made 'final' SenderBehaviour class.

  Revision 1.2  1998/10/04 18:01:15  rimassa
  Added a 'Log:' field to every source file.

*/

package jade.core.behaviours;

import jade.core.Agent;
import jade.core.AgentGroup;

import jade.lang.acl.ACLMessage;

/**
   Behaviour for sending an ACL message. This class encapsulates a
   <code>send()</code> as an atomic operation. This behaviour sends a
   given ACL message and terminates.
   @see jade.core.behaviours.ReceiverBehaviour
   @see jade.core.Agent#send(ACLMessage msg)
   @see jade.lang.acl.ACLMessage

   @author Giovanni Rimassa - Universita` di Parma
   @version $Date$ $Revision$

 */
public final class SenderBehaviour extends OneShotBehaviour {


  // The ACL message to send
  private ACLMessage message;

  // An AgentGroup to perform multicasting
  private AgentGroup receivers;

  /**
     Send a given ACL message to an agent group. This constructor
     creates a <code>SenderBehaviour</code> which sends an ACL
     message, multicasting it to an <code>AgentGroup</code>.
     @param a The agent this behaviour belongs to, and that will
     <code>send()</code> the message.
     @param msg An ACL message to send.
     @param ag The agent group to send the message to.
  */
  public SenderBehaviour(Agent a, ACLMessage msg, AgentGroup ag) {
    super(a);
    message = msg;
    receivers = ag;

    message.setSource(myAgent.getLocalName());
  }

  /**
     Send a given ACL message. This constructor creates a
     <code>SenderBehaviour</code> which sends an ACL message.
     @param a The agent this behaviour belongs to, and that will
     <code>send()</code> the message.
     @param msg An ACL message to send.
  */
  public SenderBehaviour(Agent a, ACLMessage msg) {
    this(a, msg, null);
  }

  /**
     Actual behaviour implementation. This method sends an ACL
     message, using either the given <code>AgentGroup</code> or the
     <code>:receiver</code> message slot to get the message recipient
     names.
  */
  public void action() {
    if(receivers == null)
      myAgent.send(message);
    else
      myAgent.send(message, receivers);
  }

}
