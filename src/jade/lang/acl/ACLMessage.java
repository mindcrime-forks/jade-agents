////////////////////////////////////////////////////////////////////////
// Copyright (c) 1998 FIPA All Rights Reserved
//
// This software module was developed by
// CSELT (Centro Studi e Laboratori Telecomunicazioni S.p.A)
// and DII University of Parma
// in the course of development of the FIPA97 standard. 
// It is an implementation of the parser of the 
// FIPA97 Agent Communication Language
// specified by the FIPA97 standard.
//
//
//
// The copyright of this software belongs to FIPA. FIPA gives
// free license to its members to use this software module or
// modifications thereof for hardware or software products claiming
// conformance to the FIPA97 standard.
//
//
//
// Those intending to use this software module in hardware or software
// products are advised that use may infringe existing  patents. The
// original developers of this software module and their companies, the
// subsequent editors and their companies, and FIPA have no liability
// for use of this software module or modification thereof in an
// implementation.
//
//
//
// FIPA thanks Fabio Bellifemine, Agostino Poggi, and Paolo Marenzoni
// for releasing the copyright of this software.
// Part of this software has been developed within the framework of the
// FACTS project, AC317, of the European ACTS Programme.
////////////////////////////////////////////////////////////////////////
/*
 $Log$
 Revision 1.21  1999/09/02 15:02:50  rimassa
 Removed obsolete getMessage() method.
 Avoided null values for message slots.
 Added a 'throws ParseException' specification to fromText() method.

 Revision 1.20  1999/09/01 13:40:54  rimassa
 Added a createReply() method to handle automatically
 ':conversation-id' and ':reply-with' slots.

 Revision 1.19  1999/07/25 23:52:36  rimassa
 Replaced String data members with StringBuffer, to overcome an UTF
 data format limitation to 64 kB in size.

 Revision 1.18  1999/06/25 12:40:52  rimassa
 Fixed a bug in toText() method: missing parentheses when multiple
 receivers were present.

 Revision 1.17  1999/04/08 12:01:21  rimassa
 Changed clone() method to correctly implement a deep copy.

 Revision 1.16  1999/04/06 13:24:46  rimassa
 Fixed a wrong link in Javadoc comments.

 Revision 1.15  1999/04/06 00:10:08  rimassa
 Documented public classes with Javadoc. Reduced access permissions wherever possible.

 Revision 1.14  1999/03/09 13:23:24  rimassa
 Added a 'getFirstDest()' method and made older, deprecated 'getDest()'
 method call it.

 Revision 1.13  1999/03/07 22:56:47  rimassa
 Deprecated getMessage() method.
 Added support for more agent names in ':receiver' slot. Now methods
 getDest() and setDest() are deprecated and addDest(), removeDest(),
 getDests() and removeAllDests() methods have been added.

 Revision 1.12  1999/02/22 09:25:18  rimassa
 Added support for ISO 8601 time format using a custom new class for
 all format conversions.

 Revision 1.11  1999/02/04 11:28:26  rimassa
 Added checks for null object references in modifier methods.
 Removed redundant code from time handling.

 Revision 1.10  1999/02/03 11:53:59  rimassa
 Added a more flexible time handling to use in ':reply-by' ACL message
 field.

 Revision 1.9  1998/10/18 16:01:17  rimassa
 Deprecated constructor without arguments and dump() method. Added a
 newer constructor and a fromText() static Factory Method.
 Modified toText() output formatting to insert a newline character
 after each message field.

 Revision 1.8  1998/10/04 18:02:09  rimassa
 Added a 'Log:' field to every source file.

 Revision 1.7  1998/10/04 15:25:19  rimassa
 Fixed a bug: an 'if' clause tested the wrong variable.

 Revision 1.6  1998/09/28 22:40:44  Giovanni
 Added a 'toText()' method to write an ACLMessage on an arbitrary
 Writer (String, OutputStream, File, ecc.).

 Revision 1.5  1998/09/02 00:42:16  rimassa
 Added code to make ACLMessages cloneable. Now a public clone() method
 is provided to make copies of an ACL message.

 Revision 1.4  1998/08/08 21:37:22  rimassa
 Added a missing import clause: 'import java.ioSerializable'.

 Revision 1.3  1998/08/08 18:06:53  rimassa
 Used official FIPA version of Java class for ACL messages. Some minor changes:

  - Changed class name from 'alcMessage' to 'ACLMessage'.
  - Added 'package jade.lang.acl` declaration.
  - Added 'implements Serializable' to be able to send ACL messages with Java RMI.

*/

package jade.lang.acl;

import java.io.Reader;
import java.io.Writer;
import java.io.Serializable;
import java.io.IOException;
import java.io.StringWriter;

import java.util.Enumeration;
import java.util.Date;

import jade.core.AgentGroup;

/**
 * The class ACLMessage implements an ACL message compliant to the <b>FIPA 97</b> specs.
 * All parameters are couples <em>keyword: value</em>.
 * All keywords are <code>private final String</code>.
 * All values can be set by using the methods <em>set</em> and can be read by using
 * the methods <em>get</em>. Notice that the <em>get</em> methods never
 * return null, rather they return an empty String.

 @author Fabio Bellifemine - CSELT
 @version $Date$ $Revision$

 */
public class ACLMessage implements Cloneable, Serializable {

  private static final String SOURCE          = new String(" :sender ");
  private static final String DEST            = new String(" :receiver ");
  private static final String CONTENT         = new String(" :content ");
  private static final String REPLY_WITH      = new String(" :reply-with ");
  private static final String IN_REPLY_TO     = new String(" :in-reply-to ");
  private static final String ENVELOPE        = new String(" :envelope ");
  private static final String LANGUAGE        = new String(" :language ");
  private static final String ONTOLOGY        = new String(" :ontology ");
  private static final String REPLY_BY        = new String(" :reply-by ");
  private static final String PROTOCOL        = new String(" :protocol ");
  private static final String CONVERSATION_ID = new String(" :conversation-id ");

  private StringBuffer source = new StringBuffer();
  private AgentGroup dests = new AgentGroup();
  private StringBuffer msgType = new StringBuffer();
  private StringBuffer content = new StringBuffer();
  private StringBuffer reply_with = new StringBuffer();
  private StringBuffer in_reply_to = new StringBuffer();
  private StringBuffer envelope = new StringBuffer();
  private StringBuffer language = new StringBuffer();
  private StringBuffer ontology = new StringBuffer();
  private StringBuffer reply_by = new StringBuffer();
  private long reply_byInMillisec;  
  private StringBuffer protocol = new StringBuffer();
  private StringBuffer conversation_id = new StringBuffer();

  /**
     @deprecated Since every ACL Message must have a message type, you
     should use the new constructor which gets a message type as a
     parameter.  To avoid problems, now this constructor silently sets
     the message type to <code>not-understood</code>.
     @see jade.lang.acl.ACLMessage#ACLMessage(String type)
  */
  public ACLMessage() {
    msgType.replace(0, msgType.length(), "not-understood");
  }

  /**
     Ordinary <code>ACLMessage</code> constructor. This constructor
     creates an ACL message object with the specified type.
     @param type The type of the communicative act represented by this
     message. 
   */
  public ACLMessage(String type) {
    msgType.replace(0, msgType.length(), type);
  }

  /**
     Parses an ACL message object from a text representation. Using
     this static <em>Factory Method</em>, an <code>ACLMessage</code>
     object can be built starting from a character stream.
     @param r A redable stream containing a string representation of
     an ACL message.
     @see jade.lang.acl.ACLMessage#toText(Writer w)
  */
    public static ACLMessage fromText(Reader r) throws ParseException {
      ACLMessage msg = null;
      msg = ACLParser.create().parse(r);
      return msg;
    }

  /**
     Writes the <code>:sender</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param source The new value for the slot.
     @see jade.lang.acl.ACLMessage#getSource()
  */
  public void setSource( String source ) {
    if (source != null)
      this.source = new StringBuffer(source);
    else
      this.source = new StringBuffer();
  }

  /**
     @deprecated Now <code>ACLMessage</code> class supports multiple
     receivers, so <code>addDest()</code>, <code>removeDest()</code> and
     <code> getDests()</code> should be used. Currently, this
     method removes all previous receivers and inserts the one
     given.
     @see jade.lang.acl.ACLMessage#addDest(String dest)
     @see jade.lang.acl.ACLMessage#removeDest(String dest)
     @see jade.lang.acl.ACLMessage#getDests()
  */
  public void setDest( String dest ) {
    if (dest != null) {
      dests = new AgentGroup();
      dests.addMember(new String(dest));
    } else
      removeAllDests();
  }

  /**
     Adds a value to <code>:receiver</code> slot. <em><b>Warning:</b>
     no checks are made to validate the slot value.</em>
     @param dest The value to add to the slot value set.
  */
  public void addDest(String dest) {
    if (dest != null) 
      dests.addMember(new String(dest));
  }

  /**
     Removes a value from <code>:receiver</code>
     slot. <em><b>Warning:</b> no checks are made to validate the slot
     value.</em>
     @param dest The value to remove from the slot value set.
  */
  public void removeDest(String dest) {
    if (dest != null)
      dests.removeMember(new String(dest));
  }

  /**
     Removes all values from <code>:receiver</code>
     slot. <em><b>Warning:</b> no checks are made to validate the slot
     value.</em> 
  */
  public void removeAllDests() {
    dests.reset();
  }


  /**
     Writes the message type. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param type The new value for the slot.
     @see jade.lang.acl.ACLMessage#getType()
  */
  public void setType( String type ) {
    if (type != null)
      msgType = new StringBuffer(type);
    else
      msgType = new StringBuffer();
  }

  /**
     Writes the <code>:content</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param content The new value for the slot.
     @see jade.lang.acl.ACLMessage#getContent()
  */
  public void setContent( String content ) {
    if (content != null)
      this.content = new StringBuffer(content);
    else
      this.content = new StringBuffer();
  }

  /**
     Writes the <code>:reply-with</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param reply The new value for the slot.
     @see jade.lang.acl.ACLMessage#getReplyWith()
  */
  public void setReplyWith( String reply ) {
    if (reply != null)
      reply_with = new StringBuffer(reply);
    else
      reply_with = new StringBuffer();
  }

  /**
     Writes the <code>:in-reply-to</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param reply The new value for the slot.
     @see jade.lang.acl.ACLMessage#getReplyTo()
  */
  public void setReplyTo( String reply ) {
    if (reply != null)
      in_reply_to = new StringBuffer(reply);
    else
      in_reply_to = new StringBuffer();
  }
  
  /**
     Writes the <code>:envelope</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param str The new value for the slot.
     @see jade.lang.acl.ACLMessage#getEnvelope()
  */
  public void setEnvelope( String str ) {
    if (str != null)
      envelope = new StringBuffer(str);
    else
      envelope = new StringBuffer();
  }

  /**
     Writes the <code>:language</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param str The new value for the slot.
     @see jade.lang.acl.ACLMessage#getLanguage()
  */
  public void setLanguage( String str ) {
    if (str != null)
      language = new StringBuffer(str);
    else
      language = new StringBuffer();
  }

  /**
     Writes the <code>:ontology</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param str The new value for the slot.
     @see jade.lang.acl.ACLMessage#getOntology()
  */
  public void setOntology( String str ) {
    if (str != null)
      ontology = new StringBuffer(str);
    else
      ontology = new StringBuffer();
  }

  /**
     Writes the <code>:reply-by</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param str The new value for the slot, as ISO8601 time.
     @see jade.lang.acl.ACLMessage#getReplyBy()
  */
  public void setReplyBy( String str ) {
    if (str != null) {
      reply_by = new StringBuffer(str);
      try {
	reply_byInMillisec = ISO8601.toDate(str).getTime();
      } catch (Exception e) {
	reply_byInMillisec = new Date().getTime(); // now
      }
    } else {
      reply_by = new StringBuffer();
      reply_byInMillisec = new Date().getTime();
    }
  }	

  /**
     Writes the <code>:reply-by</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param date The new value for the slot.
     @see jade.lang.acl.ACLMessage#getReplyByDate()
  */
  public void setReplyByDate(Date date) {
   reply_byInMillisec = date.getTime();
   reply_by = new StringBuffer(ISO8601.toString(date));
  }

  /**
     Writes the <code>:protocol</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param str The new value for the slot.
     @see jade.lang.acl.ACLMessage#getProtocol()
  */
  public void setProtocol( String str ) {
    if (str != null)
      protocol = new StringBuffer(str);
    else
      protocol = new StringBuffer();
  }

  /**
     Writes the <code>:conversation-id</code> slot. <em><b>Warning:</b> no
     checks are made to validate the slot value.</em>
     @param str The new value for the slot.
     @see jade.lang.acl.ACLMessage#getConversationId()
  */
  public void setConversationId( String str ) {
    if (str != null)
      conversation_id = new StringBuffer(str);
    else
      conversation_id = new StringBuffer();
  }

 /**
  @deprecated Now <code>ACLMessage</code> class supports multiple
  receivers, so <code>addDest()</code>, <code>removeDest()</code> and
  <code>getDests()</code> should be used.
  @see jade.lang.acl.ACLMessage#addDest(String dest)
  @see jade.lang.acl.ACLMessage#removeDest(String dest)
  @see jade.lang.acl.ACLMessage#getDests() 
 */
  public String getDest() {
    return getFirstDest();
  }

  /**
     Reads <code>:receiver</code> slot.
     @return An <code>AgentGroup</code> containing the names of the
     receiver agents for this message.
  */
  public AgentGroup getDests() {
    return (AgentGroup)dests.clone();
  }

  /**
     Reads first value of <code>:receiver</code> slot.
     @return The first receiver agent name.
  */
  public String getFirstDest() {
    Enumeration e = dests.getMembers();
    if(e.hasMoreElements())
      return (String)e.nextElement();
    else
      return null;
  }

  /**
     Reads <code>:sender</code> slot.
     @return The value of <code>:sender</code>slot.
     @see jade.lang.acl.ACLMessage#setSource(String).
  */
  public String getSource() {
    return new String(source);
  }

  /**
     Reads message type.
     @return The value of the message type..
     @see jade.lang.acl.ACLMessage#setType(String).
  */
  public String getType() {
    return new String(msgType);
  }

  /**
     Reads <code>:content</code> slot.
     @return The value of <code>:content</code>slot.
     @see jade.lang.acl.ACLMessage#setContent(String).
  */
  public String getContent() {
    return new String(content);
  }

  /**
     Reads <code>:reply-with</code> slot.
     @return The value of <code>:reply-with</code>slot.
     @see jade.lang.acl.ACLMessage#setReplyWith(String).
  */
  public String getReplyWith() {
    return new String(reply_with);
  }

  /**
     Reads <code>:reply-to</code> slot.
     @return The value of <code>:reply-to</code>slot.
     @see jade.lang.acl.ACLMessage#setReplyTo(String).
  */
  public String getReplyTo() {
    return new String(in_reply_to);
  }



  /**
     Reads <code>:envelope</code> slot.
     @return The value of <code>:envelope</code>slot.
     @see jade.lang.acl.ACLMessage#setEnvelope(String).
  */
  public String getEnvelope() {
    return new String(envelope);
  }

  /**
     Reads <code>:language</code> slot.
     @return The value of <code>:language</code>slot.
     @see jade.lang.acl.ACLMessage#setLanguage(String).
  */
  public String getLanguage() {
    return new String(language);
  }

  /**
     Reads <code>:ontology</code> slot.
     @return The value of <code>:ontology</code>slot.
     @see jade.lang.acl.ACLMessage#setOntology(String).
  */
  public String getOntology() {
    return new String(ontology);
  }

  /**
     Reads <code>:reply-by</code> slot.
     @return The value of <code>:reply-by</code>slot, as a string.
     @see jade.lang.acl.ACLMessage#setReplyBy(String).
  */
  public String getReplyBy() {
    return new String(reply_by);
  }

  /**
     Reads <code>:reply-by</code> slot.
     @return The value of <code>:reply-by</code>slot, as a
     <code>Date</code> object.
     @see jade.lang.acl.ACLMessage#setReplyByDate(Date).
  */
  public Date getReplyByDate() {
   return new Date(reply_byInMillisec);
  }

  /**
     Reads <code>:protocol</code> slot.
     @return The value of <code>:protocol</code>slot.
     @see jade.lang.acl.ACLMessage#setProtocol(String).
  */
  public String getProtocol() {
    return new String(protocol);
  }

  /**
     Reads <code>:conversation-id</code> slot.
     @return The value of <code>:conversation-id</code>slot.
     @see jade.lang.acl.ACLMessage#setConversationId(String).
  */
  public String getConversationId() {
    return new String(conversation_id);
  }
 



  private static int counter = 0; // This variable is only used as a counter in dump()
  /**
     @deprecated This method dumps the message on System.out, so it's
     not suitable for use with GUIs or streams. Now
     <code>fromText()</code>/<code>toText()</code> methods allow
     reading and writing an ACL message on any stream; besides they
     are inverse of each other. Besides, this method will corrupt the
     ACL message object when more than one receiver is present.
     @see jade.lang.acl.ACLMessage#toText(Writer w)
  */
  public void dump() {
    counter++;
    String dest = getDest();
    System.out.println( counter + ") " + new String(msgType).toUpperCase());
    if (source.length() > 0)          System.out.println("   " + SOURCE + source);
    if (dest.length() > 0)            System.out.println("   " + DEST + dest);
    if (content.length() > 0)         System.out.println("   " + CONTENT + content);
    if (reply_with.length() > 0)      System.out.println("   " + REPLY_WITH + reply_with);
    if (in_reply_to.length() > 0)     System.out.println("   " + IN_REPLY_TO + in_reply_to);
    if (envelope.length() > 0)        System.out.println("   " + ENVELOPE + envelope);
    if (language.length() > 0)        System.out.println("   " + LANGUAGE + language);
    if (ontology.length() > 0)        System.out.println("   " + ONTOLOGY + ontology);
    if (reply_by.length() > 0)        System.out.println("   " + REPLY_BY + reply_by);
    if (protocol.length() > 0)        System.out.println("   " + PROTOCOL + protocol);
    if (conversation_id.length() > 0) System.out.println("   " + CONVERSATION_ID + conversation_id);
    System.out.println();
  }

  /**
     Writes an ACL message object on a stream as a character
     string. This method allows to write a string representation of an
     <code>ACLMessage</code> object onto a character stream.
     @param w A <code>Writer</code> object to write the message onto.
     @see jade.lang.acl.ACLMessage#fromText(Reader r)
  */
  public void toText(Writer w) {
    try {
      w.write("(");
      w.write(msgType + "\n");
      if(source.length() > 0)
	w.write(SOURCE + " " + source + "\n");
      Enumeration e = dests.getMembers();
      if(e.hasMoreElements()) 
	w.write(DEST + "\n");
	  if (dests.size() > 1)
		w.write("(");
      while(e.hasMoreElements())
	w.write((String)e.nextElement() + "\n");
	  if (dests.size() > 1)
		w.write(")");
      if(content.length() > 0)
	w.write(CONTENT + " " + content + "\n");
      if(reply_with.length() > 0)
	w.write(REPLY_WITH + " " + reply_with + "\n");
      if(in_reply_to.length() > 0)
	w.write(IN_REPLY_TO + " " + in_reply_to + "\n");
      if(envelope.length() > 0)
	w.write(ENVELOPE + " " + envelope + "\n");
      if(language.length() > 0)
	w.write(LANGUAGE + " " + language + "\n");
      if(ontology.length() > 0)
	w.write(ONTOLOGY + " " + ontology + "\n");
      if(reply_by.length() > 0)
       w.write(REPLY_BY + " " + reply_by + "\n");
      if(protocol.length() > 0)
	w.write(PROTOCOL + " " + protocol + "\n");
      if(conversation_id.length() > 0)
	w.write(CONVERSATION_ID + " " + conversation_id + "\n");
      w.write(")");
      w.flush();
    }
    catch(IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
     Clone an <code>ACLMessage</code> object.
     @return A copy of this <code>ACLMessage</code> object. The copy
     must be casted back to <code>ACLMessage</code> type before being
     used.
  */
  public synchronized Object clone() {

    ACLMessage result;

    try {
      result = (ACLMessage)super.clone();
      result.dests = getDests(); // Deep copy
    }
    catch(CloneNotSupportedException cnse) {
      throw new InternalError(); // This should never happen
    }

    return result;
  }

  /**
     Convert an ACL message to its string representation. This method
     writes a representation of this <code>ACLMessage</code> into a
     character string.
     @return A <code>String</code> representing this message.
     @see jade.lang.acl.ACLMessage#fromText(Reader r)
  */
  public String toString(){
    StringWriter text = new StringWriter();
    toText(text);
    return text.toString();
  }

 /**
  * Resets all the message slots.
 */
 public void reset() {
    /* Fabio, 26/8/99. Attenzione new StringBuffer(null) genera una
       NullPointerException. Allo stesso modo new String(null).
       Pertanto non inizializzare direttamente a null.*/
  source=new StringBuffer();
  dests=new AgentGroup();
  msgType=new StringBuffer("not-understood");
  content=new StringBuffer();
  reply_with=new StringBuffer();
  in_reply_to=new StringBuffer();
  envelope=new StringBuffer();
  language=new StringBuffer();
  ontology=new StringBuffer();
  reply_by=new StringBuffer();
  reply_byInMillisec = new Date().getTime();
  protocol=new StringBuffer();
  conversation_id=new StringBuffer();
 }

  /**
   * create a new ACLMessage that is a reply to this message.
   * In particular, it sets the following parameters of the new message:
   * receiver, language, ontology, protocol, conversation-id,
   * in-reply-to, reply-with.
   * The programmer needs to set the communicative-act and the content.
   * Of course, if he wishes to do that, he can reset any of the fields.
   * @return the ACLMessage to send as a reply
   */
public ACLMessage createReply() {
  ACLMessage m = (ACLMessage)clone();
  m.removeAllDests();
  m.addDest(getSource());
  m.setSource(null);
  //m.setSource(getLocalName());
  m.setReplyTo(getReplyWith());
  m.setReplyWith(getSource()+java.lang.System.currentTimeMillis()); 
  m.setReplyBy(null);
  m.setContent(null);
  m.setEnvelope(null);
  return m;
}

}
