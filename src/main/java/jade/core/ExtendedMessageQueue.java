package jade.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class ExtendedMessageQueue implements MessageQueue {

	protected LinkedList<ACLMessage> list;
	protected int maxSize;
	protected int warningLimit;
	protected MessageTemplate warningDiscardTemplate;
	protected Agent myAgent;
	protected Logger myLogger = Logger.getJADELogger(getClass().getName());

	public ExtendedMessageQueue(int maxSize, int warningLimit, MessageTemplate warningDiscardTemplate, Agent a) {
		this.maxSize = maxSize;
		this.warningLimit = warningLimit;
		this.warningDiscardTemplate = warningDiscardTemplate;
		myAgent = a;
		list = new LinkedList<ACLMessage>();
	}

	public ExtendedMessageQueue() {
		this(0, 0, null, null);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void setMaxSize(int maxSize) throws IllegalArgumentException {
		if(maxSize < 0) {
			maxSize = 0;
		}
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return the number of messages
	 * currently in the queue
	 **/
	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void addFirst(ACLMessage msg) {
		if((maxSize != 0) && (list.size() >= maxSize)) {
			ACLMessage discardedMsg = list.removeLast();
			handleDiscarded(discardedMsg, false);
		}
		list.addFirst(msg);
	}

	@Override
	public void addLast(ACLMessage msg) {
		if((maxSize != 0) && (list.size() >= maxSize)) {
			// Max-size exceeded. Discard message
			handleDiscarded(msg, false);
			return;
		}
		else if (warningLimit > 0 && list.size() >= warningLimit) {
			// Warning-limit exceeded. Discard message if warningDiscardTemplate matches
			if (warningDiscardTemplate != null && warningDiscardTemplate.match(msg)) {
				handleDiscarded(msg, true);
				return;
			}
		}
		
		list.addLast(msg);
	}

	/**
	 * This method is invoked when a message is discarded because the queue max size has been reached 
	 * or the warning limit has been exceeded and the discard-message-template matches.
	 * The default implementation just print a WARNING.
	 * Subclasses may redefine this method to implement application specific discard actions such as
	 * sending back a FAILURE to the sender
	 * @param msg The message that was discarded
	 * @param warningLimitExceeded A boolean indication stating that the message was discarded because 
	 * the warning limit has been exceeded.
	 */
	protected void handleDiscarded(ACLMessage msg, boolean warningLimitExceeded) {
		if (warningLimitExceeded) {
			myLogger.log(Logger.SEVERE, "Agent "+getAgentName()+" - Message queue warning-limit exceeded. Message "+msg.shortToString()+" discarded!!!!!");
		}
		else {
			myLogger.log(Logger.SEVERE, "Agent "+getAgentName()+" - Message queue max-size exceeded. Message "+msg.shortToString()+" discarded!!!!!");
		}
	}
	
	@Override
	public ACLMessage receive(MessageTemplate pattern) {
		ACLMessage result = null;
		for (Iterator messages = list.iterator(); messages.hasNext();) {
			ACLMessage msg = (ACLMessage)messages.next();
			if (pattern == null || pattern.match(msg)) {
				messages.remove();
				result = msg;
				break;
			}
		}
		return result;
	}
	
	@Override
	public List<ACLMessage> receive(MessageTemplate pattern, int max) {
		List<ACLMessage> mm = null;
		int cnt = 0;
		for (Iterator messages = list.iterator(); messages.hasNext();) {
			ACLMessage msg = (ACLMessage)messages.next();
			if (pattern == null || pattern.match(msg)) {
				messages.remove();
				if (mm == null) {
					mm = new ArrayList<ACLMessage>(max);
				}
				mm.add(msg);
				cnt++;
				if (cnt == max) {
					break;
				}
			}
		}
		return mm;
	}

	@Override
	public void copyTo(jade.util.leap.List messages) {
		for (Iterator<ACLMessage> i = list.iterator(); i.hasNext(); messages.add(i.next()));
	}
	
	private String getAgentName() {
		return myAgent != null ? myAgent.getLocalName() : "null";
	}
	
	public String dump(int limit) { 
		StringBuilder sb = new StringBuilder();
		Object[] messages = list.toArray();
		if (messages.length > 0) {
			int max = limit > 0 ? limit : messages.length;
			for (int j = 0; j < max; ++j) {
				sb.append("Message # ");
				sb.append(j);
				sb.append('\n');
				sb.append(messages[j]);
				sb.append('\n');
			}
		}
		else {
			sb.append("Queue is empty\n");
		}
		return sb.toString();
	}
}
