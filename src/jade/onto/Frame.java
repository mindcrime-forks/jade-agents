package jade.onto;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Frame {

  protected static class NoSuchSlotException extends OntologyException {
    public NoSuchSlotException(String frameName, String slotName) {
      super("No slot named " + slotName + " in frame " + frameName);
    }
  }

  private String myName;
  private Map slotsByName;
  private List slotsByPosition;

  public Frame(String name) {
    myName = name;
    slotsByName = new HashMap();
    slotsByPosition = new ArrayList();
  }

  public String getName() {
    return myName;
  }

  public void putSlot(String name, Object value) {
    slotsByName.put(new Name(name), value);
    slotsByPosition.add(value);
  }

  public void putSlot(Object value) {
    // generate a name with an underscore followed by the position number
    String dummyName = "_" + Integer.toString(slotsByPosition.size());

    // Add more underscores as needed
    while(slotsByName.containsKey(dummyName))
      dummyName = "_" + dummyName;

    putSlot(dummyName, value);

  }

  public Object getSlot(String name) throws NoSuchSlotException {
    Object result = slotsByName.get(new Name(name));
    if(result == null)
      throw new NoSuchSlotException(myName, name);
    return result;
  }

  public Object getSlot(int position) throws NoSuchSlotException { 
    try {
      return slotsByPosition.get(position);
    }
    catch(IndexOutOfBoundsException ioobe) {
      throw new NoSuchSlotException(myName, "@" + position);
    }
  }

  final Iterator terms() {
    return slotsByPosition.iterator();
  }

  public void dump() {
    System.out.println("Frame: " + myName);
    Iterator i = slotsByName.entrySet().iterator();
    while(i.hasNext()) {
      Map.Entry e = (Map.Entry)i.next();
      Name name = (Name)e.getKey();
      Object slot = e.getValue();
      System.out.print("( " + name + " ");
      if(slot instanceof Frame) {
	Frame f = (Frame)slot;
	f.dump();
      }
      else
	System.out.print(slot.toString());

      System.out.println(" )");
    }
  }

}

