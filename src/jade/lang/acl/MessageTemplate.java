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

package jade.lang.acl;

import java.io.Writer;
import java.io.IOException;
import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import jade.core.AID;

/**
   A pattern for matching incoming ACL messages. This class allows to
   build complex slot patterns to select ACL messages. These patterns
   can then be used in <code>receive()</code> operations.
   @see jade.core.Agent#receive(MessageTemplate mt)
   
   
   @author Giovanni Rimassa - Universita` di Parma
   @version $Date$ $Revision$

*/
public class MessageTemplate implements Serializable {

  // Names of the various fields of an ACL messages.
  // Used to build the names of get()/set() methods.
  private static final String[] stringFields = { "Content",
						 "ConversationId",
						 "Encoding",
						 "InReplyTo",
						 "Language",
						 "Ontology",
						 "Protocol",
						 "ReplyBy",
						 "ReplyWith",
  };

  private static final String[] listFields = { 
    "Receiver",
    "ReplyTo"
  };

  private static interface MatchExpression {
    boolean match(ACLMessage msg);
    void toText(Writer w);
  }

  private static class AndExpression implements MatchExpression, Serializable {

    private MatchExpression op1;
    private MatchExpression op2;

    public AndExpression(MatchExpression e1, MatchExpression e2) {
      op1 = e1;
      op2 = e2;
    }

    public boolean match(ACLMessage msg) {
      return op1.match(msg) && op2.match(msg);
    }

    public void toText(Writer w) {
      try {
	w.write("( ");
	op1.toText(w);
	w.write(" AND ");
	op2.toText(w);
	w.write(" )");
      }
      catch(IOException ioe) {
	ioe.printStackTrace();
      }
    }

  } // End of AndExpression class

  private static class OrExpression implements MatchExpression, Serializable {

    private MatchExpression op1;
    private MatchExpression op2;

    public OrExpression(MatchExpression e1, MatchExpression e2) {
      op1 = e1;
      op2 = e2;
    }

    public boolean match(ACLMessage msg) {
      return op1.match(msg) || op2.match(msg);
    }

    public void toText(Writer w) {
      try {
	w.write("( ");
	op1.toText(w);
	w.write(" OR ");
	op2.toText(w);
	w.write(" )");
      }
      catch(IOException ioe) {
	ioe.printStackTrace();
      }
    }

  } // End of OrExpression class

  private static class NotExpression implements MatchExpression, Serializable {
    private MatchExpression op;

    public NotExpression(MatchExpression e) {
      op = e;
    }

    public boolean match(ACLMessage msg) {
      return ! op.match(msg);
    }

    public void toText(Writer w) {
      try {
	w.write(" NOT ");
	op.toText(w);
      }
      catch(IOException ioe) {
	ioe.printStackTrace();
      }
    }

  } // End of NotExpression class

  private static class Literal implements MatchExpression, Serializable {

    private class WildCardedMessage {
      private boolean hasPerformative;
      private ACLMessage myMessage;

      public WildCardedMessage(ACLMessage msg, boolean b) {
	myMessage = msg;
	hasPerformative = b;
      }

      public boolean matchPerformative() {
	return hasPerformative;
      }

      public ACLMessage getMsg() {
	return myMessage;
      }

    }

    private WildCardedMessage template;

    public Literal(ACLMessage msg, boolean wildcardOnPerformative) {
      template = new WildCardedMessage((ACLMessage)msg.clone(), wildcardOnPerformative);
    }

    public boolean match(ACLMessage msg) {
      Class ACLMessageClass = msg.getClass();

      ACLMessage templMessage = template.getMsg();

      if(template.matchPerformative()) {
	int perf1 = templMessage.getPerformative();
	int perf2 = msg.getPerformative();
	if(perf1 != perf2)
	  return false;
      }

      try {
	// Match String slots
	for(int i = 0; i < stringFields.length; i++) {

	  String name = stringFields[i];
	  Method getValue = ACLMessageClass.getMethod("get" + name, new Class[] { });

	  // This means: s1 = templMessage.get<value>();
	  String s1 = (String)getValue.invoke(templMessage, new Object[] { });

	  // This means: s2 = msg.get<value>();
	  String s2 = (String)getValue.invoke(msg, new Object[] { });

	  if(s1 != null)
	    if((s1.length() > 0) && (!s1.equalsIgnoreCase(s2)))
	      return false;
	}

	// Match 'sender' slot
	AID id1 = templMessage.getSender();
	AID id2 = msg.getSender();
	if(id1 != null)
	  if(!id1.equals(id2))
	    return false;

	// Match List slots
	for(int i = 0; i < listFields.length; i++) {
	  String name = listFields[i];
	  Method getValues = ACLMessageClass.getMethod("getAll" + name, new Class[] { });

	  // This means: it1 = templMessage.getAll<name>();
	  Iterator it1 = (Iterator)getValues.invoke(templMessage, new Object[] { });
	  while(it1.hasNext()) {
	    Object templateListElement = it1.next();

	    // This means: it2 = templMessage.getAll<name>();
	    Iterator it2 = (Iterator)getValues.invoke(msg, new Object[] { });
	    boolean found = false;
	    while(it2.hasNext()) {
	      if(templateListElement.equals(it2.next())) {
		found = true;
		break; // Out of the inner while loop
	      }
	    }
	    // If an element of the template list is not found within
	    // the message list, the message does not match.
	    if(found == false)
	      return false;
	  }

	}

      }
      catch(Exception e) {
	e.printStackTrace();
	return false;
      }

      return true;

    }

    public void toText(Writer w) { // FIXME: This method just prints out the String slots.
      try {
	w.write("(\n");
	for(int i = 0; i < stringFields.length; i++) {
	  String name = stringFields[i];
	  String value = null;
	  try {
	    ACLMessage msg = template.getMsg();
	    Method getValue = ACLMessage.class.getMethod("get" + name, new Class[] { });
	    // This means: s1 = msg.get<value>();
	    value = (String)getValue.invoke(msg, new Object[] { });
	  }
	  catch(Exception e) {
	    e.printStackTrace();
	  }
	  if(value != null)
	    w.write(" :" + name + " == " + value + "\n");
	}
	w.write(")\n");
      }
      catch(IOException ioe) {
	ioe.printStackTrace();
      }

    }

  } // End of Literal class

  /**
  @serial
  */
  private MatchExpression toMatch;

  // Creates an ACL message with all fields set to the special,
  // out-of-band wildcard value.
  private static ACLMessage allWildCard() {
    ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);

    try {
      for(int i = 0; i < stringFields.length; i++) {

	Class ACLMessageClass = ACLMessage.class;
	String name = stringFields[i];

	// This means: msg.set<name>(param)
	Method setValue = ACLMessageClass.getMethod("set" + name, new Class[] { String.class });
	setValue.invoke(msg, new Object[] { null });

	msg.setSender(null); // The 'sender' slot is different, because it is of AID type.
      }

      for(int i = 0; i < listFields.length; i++) {

	Class ACLMessageClass = ACLMessage.class;
	String name = listFields[i];

	// This means: msg.clearAll<name>(param)
	Method clearValue = ACLMessageClass.getMethod("clearAll" + name, new Class[] { });
	clearValue.invoke(msg, new Object[] { });

	msg.setSender(null); // The 'sender' slot is different, because it is of AID type.
      }

    }
    catch(Exception e) {
      e.printStackTrace();
    }

    return msg;
  }


  // Private constructor: use static factory methods to create message
  // templates.
  private MessageTemplate(MatchExpression e) {
    toMatch = e;
  }

  // Private constructor: use static factory methods to create message
  // templates.
  private MessageTemplate(ACLMessage msg, boolean matchPerformative) {
    toMatch = new Literal((ACLMessage)msg.clone(), matchPerformative);
  }


  /**
     This <em>Factory Method</em> returns a message template that
     matches any message.
     @return A new <code>MessageTemplate</code> matching any given
     value.
  */
  public static MessageTemplate MatchAll() {
    return new MessageTemplate(allWildCard(), false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:sender</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchSender(AID value) {
    ACLMessage msg = allWildCard();
    msg.setSender(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:receiver</code> slot.
     @param values A <code>List</code> of Agent IDs against which the
     value of the message slot will be matched.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchReceiver(List values) {
    ACLMessage msg = allWildCard();
    msg.clearAllReceiver();
    Iterator it = values.iterator();
    while(it.hasNext())
      msg.addReceiver((AID)it.next());
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:content</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchContent(String value) {
    ACLMessage msg = allWildCard();
    msg.setContent(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:reply-with</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchReplyWith(String value) {
    ACLMessage msg = allWildCard();
    msg.setReplyWith(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:in-reply-to</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchInReplyTo(String value) {
    ACLMessage msg = allWildCard();
    msg.setInReplyTo(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:reply-to</code> slot.
     @param values A <code>List</code> of Agent IDs against which the
     value of the message slot will be matched.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchReplyTo(List values) {
    ACLMessage msg = allWildCard();
    msg.clearAllReplyTo();
    Iterator it = values.iterator();
    while(it.hasNext())
      msg.addReplyTo((AID)it.next());
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:language</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchLanguage(String value) {
    ACLMessage msg = allWildCard();
    msg.setLanguage(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:encoding</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchEncoding(String value) {
    ACLMessage msg = allWildCard();
    msg.setEncoding(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:ontology</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchOntology(String value) {
    ACLMessage msg = allWildCard();
    msg.setOntology(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:reply-by</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchReplyBy(String value) {
    ACLMessage msg = allWildCard();
    msg.setReplyBy(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:protocol</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchProtocol(String value) {
    ACLMessage msg = allWildCard();
    msg.setProtocol(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given <code>:conversation-id</code> slot.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTemplate</code> matching the given
     value.
  */
  public static MessageTemplate MatchConversationId(String value) {
    ACLMessage msg = allWildCard();
    msg.setConversationId(value);
    return new MessageTemplate(msg, false);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches any message with a given performative.
     @param value The value the message slot will be matched against.
     @return A new <code>MessageTenplate</code>matching the given
     value.
  */
  public static MessageTemplate MatchPerformative(int value){
    ACLMessage msg = allWildCard();
    msg.setPerformative(value);  	
    return new MessageTemplate(msg,true);
  }

  /**
     This <em>Factory Method</em> returns a message template that
     matches ACL messages against a given one, passed as
     parameter. The following algorithm is used:
     When the given <code>ACLMessage</code> has a non
     <code>null</code> slot, subsequent messages must have the same
     slot value in that slot to have a match.
     When the given <code>ACLMessage</code> has a <code>null</code>
     slot, subsequent messages can have any value for that slot and
     still match the template.
     In short, a <code>null</code> value for a slot means <em>don't
     care</em>.
     @param msg The <code>ACLMessage</code> used to build a custom
     message template.
     @param matchPerformative a <code>bool</code> value. When
     <code>true</code>, the performative of the <code>msg</code> will
     be considered as a part of the template (i.e. the message
     template will match only ACL messages with the same performativa
     as <code>msg</code>).
     When <false>, the performative of <code>msg</code> is ignored and
     the resulting message template will not consider it when matching
     messages.
     @return A new <code>MessageTemplate</code>, matching the given
     message according to the above algorithm.
  */
  public static MessageTemplate MatchCustom(ACLMessage msg, boolean matchPerformative) {
    ACLMessage message = (ACLMessage)msg.clone();
    return new MessageTemplate(message, matchPerformative);
  }

  /**
     Logical <b>and</b> between two <code>MessageTemplate</code>
     objects. This method creates a new message template that is
     matched by those ACL messages matching <b><em>both</b></em>
     message templates given as operands.
     @param op1 The first <em>and</em> operand.
     @param op2 The second <em>and</em> operand.
     @return A new <code>MessageTemplate</code> object.
     @see jade.lang.acl.MessageTemplate#or(MessageTemplate op1, MessageTemplate op2)
  */
  public static MessageTemplate and(MessageTemplate op1, MessageTemplate op2) {
    AndExpression e = new AndExpression(op1.toMatch, op2.toMatch);
    MessageTemplate result = new MessageTemplate(e);
    return result;
  }

  /**
     Logical <b>or</b> between two <code>MessageTemplate</code>
     objects. This method creates a new message template that is
     matched by those ACL messages matching <b><em>any</b></em> of the
     two message templates given as operands.
     @param op1 The first <em>or</em> operand.
     @param op2 The second <em>or</em> operand.
     @return A new <code>MessageTemplate</code> object.
     @see jade.lang.acl.MessageTemplate#and(MessageTemplate op1, MessageTemplate op2)
  */
  public static MessageTemplate or(MessageTemplate op1, MessageTemplate op2) {
    OrExpression e = new OrExpression(op1.toMatch, op2.toMatch);
    MessageTemplate result = new MessageTemplate(e);
    return result;
  }

  /**
     Logical <b>not</b> of a <code>MessageTemplate</code> object. This
     method creates a new message template that is matched by those
     ACL messages <b><em>not</em></b> matching the message template
     given as operand.
     @param op The <em>not</em> operand.
     @return A new <code>MessageTemplate</code> object.
  */
  public static MessageTemplate not(MessageTemplate op) {
    NotExpression e = new NotExpression(op.toMatch);
    MessageTemplate result = new MessageTemplate(e);
    return result;
  }

  /**
     Matches an ACL message against this <code>MessageTemplate</code>
     object.
     @param msg The <code>ACLMessage</code> to check for matching.
     @return <code>true</code> if the ACL message matches this
     template, <code>false</code> otherwise.
  */
  public boolean match(ACLMessage msg) {
    return toMatch.match(msg);
  }

}
