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


package demo.MeetingScheduler;
/*
	A basic extension of the java.awt.Frame class
 */

import java.awt.*;

import java.util.Enumeration;

import symantec.itools.awt.util.Calendar;

import java.util.Date;

import jade.gui.GuiEvent;
import demo.MeetingScheduler.Ontology.*;

/**
Javadoc documentation for the file
@author Fabio Bellifemine - CSELT S.p.A
@version $Date$ $Revision$
*/
public class FixApp extends Frame
{
	public FixApp(String aName)
	{
		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		//{{INIT_CONTROLS
		setLayout(null);
		setVisible(false);
		setSize(530,600);
		setBackground(new Color(12632256));
		textArea1 = new java.awt.TextArea("",0,0,TextArea.SCROLLBARS_NONE);
		textArea1.setBounds(0,60,516,108);
                textArea1.setText(aName + " meets with ..");
		add(textArea1);
		calendar1 = new symantec.itools.awt.util.Calendar();
		calendar1.setBounds(0,216,250,200);
		calendar1.setFont(new Font("Dialog", Font.BOLD, 10));
		add(calendar1);
		calendar2 = new symantec.itools.awt.util.Calendar();
		calendar2.setBounds(276,216,250,200);
		calendar2.setFont(new Font("Dialog", Font.BOLD, 10));
		add(calendar2);
		label2 = new java.awt.Label("Starting On",Label.CENTER);
		label2.setBounds(12,168,228,40);
		label2.setFont(new Font("Dialog", Font.BOLD, 12));
		add(label2);
		label4 = new java.awt.Label("Ending With",Label.CENTER);
		label4.setBounds(288,168,228,40);
		label4.setFont(new Font("Dialog", Font.BOLD, 12));
		add(label4);
		listInvitedPersons = new java.awt.List(4);
		add(listInvitedPersons);
		listInvitedPersons.setBounds(336,456,180,108);
		listKnownPersons = new java.awt.List(4);
		add(listKnownPersons);
		listKnownPersons.setBounds(12,456,180,108);
		label1 = new java.awt.Label("Known Persons",Label.CENTER);
		label1.setBounds(12,420,180,36);
		label1.setFont(new Font("Dialog", Font.BOLD, 12));
		add(label1);
		label3 = new java.awt.Label("Invited Persons",Label.CENTER);
		label3.setBounds(336,420,180,36);
		label3.setFont(new Font("Dialog", Font.BOLD, 12));
		add(label3);
		buttonAddPerson = new java.awt.Button();
		buttonAddPerson.setLabel(">>");
		buttonAddPerson.setBounds(240,468,48,36);
		buttonAddPerson.setBackground(new Color(12632256));
		add(buttonAddPerson);
		buttonRemovePerson = new java.awt.Button();
		buttonRemovePerson.setLabel("<<");
		buttonRemovePerson.setBounds(240,516,48,36);
		buttonRemovePerson.setBackground(new Color(12632256));
		add(buttonRemovePerson);
		label5 = new java.awt.Label("Appointment Description",Label.CENTER);
		label5.setBounds(168,12,180,36);
		label5.setFont(new Font("Dialog", Font.BOLD, 12));
		add(label5);
		buttonOk = new java.awt.Button();
		buttonOk.setLabel("OK");
		buttonOk.setBounds(12,12,48,36);
		buttonOk.setBackground(new Color(12632256));
		add(buttonOk);
		buttonExit = new java.awt.Button();
		buttonExit.setLabel("Exit");
		buttonExit.setBounds(72,12,48,36);
		buttonExit.setBackground(new Color(12632256));
		add(buttonExit);
		textFieldErrMsg = new java.awt.TextField();
		textFieldErrMsg.setVisible(false);
		textFieldErrMsg.setBounds(20,578,480,20);
		textFieldErrMsg.setFont(new Font("Dialog", Font.ITALIC, 10));
		textFieldErrMsg.setBackground(new Color(16776960));
		add(textFieldErrMsg);
		setTitle(aName + " - Fix New Appointment");
		//}}

		//{{INIT_MENUS
		//}}

		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymMouse aSymMouse = new SymMouse();
		buttonExit.addMouseListener(aSymMouse);
		buttonOk.addMouseListener(aSymMouse);
		buttonAddPerson.addMouseListener(aSymMouse);
		buttonRemovePerson.addMouseListener(aSymMouse);
		//}}

		setLocation(50, 50);
	}

	public FixApp(String aName,String title)
	{
		this(aName);
		setTitle(aName + " - " + title);
	}

	public void addNotify()
	{
	    // Record the size of the window prior to calling parents addNotify.
	    Dimension d = getSize();
	    
		super.addNotify();

		if (fComponentsAdjusted)
			return;

		// Adjust components according to the insets
		setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(insets().left, insets().top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
	}

    // Used for addNotify check.
	boolean fComponentsAdjusted = false;


    MeetingSchedulerAgent myAgent; 
    public FixApp(MeetingSchedulerAgent a, String selectedDate) {
        this(a.getLocalName());
        myAgent = a;
	try {
	  calendar1.setDate(selectedDate);
	} catch(java.beans.PropertyVetoException e) { 
	  System.err.println("Date not valid to set: "+selectedDate);
	}
	try {
	  calendar2.setDate(selectedDate);
	} catch(java.beans.PropertyVetoException e) { 
	  System.err.println("Date not valid to set: "+selectedDate);
	}
	Enumeration e = myAgent.getKnownPersons();
        listKnownPersons.clear();
        while (e.hasMoreElements()) {
	  listKnownPersons.addItem(((Person)e.nextElement()).getName());
        }
    }
    
    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
  /**
    public void setVisible(boolean b)
	{
	  if(b)
	      setLocation(50, 50);
	  super.setVisible(b);
	}
	**/
    static public void main(String args[])
	{
		(new FixApp("test")).setVisible(true);
	}
	
  /**
   public void addNotify() {
     // Record the size of the window prior to calling parents addNotify.
     Dimension d = getSize();
     
     super.addNotify();

     if (fComponentsAdjusted)
       return;

     // Adjust components according to the insets
     setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
     Component components[] = getComponents();
     for (int i = 0; i < components.length; i++)
       {
	 Point p = components[i].getLocation();
	 p.translate(insets().left, insets().top);
	 components[i].setLocation(p);
       }
     fComponentsAdjusted = true;
   }
   
  // Used for addNotify check.
  boolean fComponentsAdjusted = false;
  **/
	//{{DECLARE_CONTROLS
	java.awt.TextArea textArea1;
	symantec.itools.awt.util.Calendar calendar1;
	symantec.itools.awt.util.Calendar calendar2;
	java.awt.Label label2;
	java.awt.Label label4;
	java.awt.List listInvitedPersons;
	java.awt.List listKnownPersons;
	java.awt.Label label1;
	java.awt.Label label3;
	java.awt.Button buttonAddPerson;
	java.awt.Button buttonRemovePerson;
	java.awt.Label label5;
	java.awt.Button buttonOk;
	java.awt.Button buttonExit;
	java.awt.TextField textFieldErrMsg;
	//}}

	//{{DECLARE_MENUS
	//}}

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == FixApp.this)
				Frame1_WindowClosing(event);
		}
	}
	
	void Frame1_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);		 // hide the Frame
	}

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == buttonExit)
				buttonExit_MouseClicked(event);
			else if (object == buttonOk)
				buttonOk_MouseClicked(event);
			else if (object == buttonAddPerson)
				buttonAddPerson_MouseClicked(event);
			else if (object == buttonRemovePerson)
				buttonRemovePerson_MouseClicked(event);
		}
	}

	void buttonExit_MouseClicked(java.awt.event.MouseEvent event)
	{
		// Invalidate the Frame
		dispose();
	}





	void buttonOk_MouseClicked(java.awt.event.MouseEvent event)
	{
	    textFieldErrMsg.setVisible(false);	
	    		
	    //{{CONNECTION
	    Appointment a = new Appointment();
	    a.setInviter(myAgent.getAID());
	    a.setDescription(textArea1.getText());
				
	    a.setStartingOn(new Date(calendar1.getDate()));
	    a.setEndingWith(new Date(calendar2.getDate()));
				
	    for (int i=0; i<listInvitedPersons.countItems(); i++) 
	      a.addInvitedPersons(myAgent.getPerson(listInvitedPersons.getItem(i)));
	    try {
	      a.isValid();
	      System.err.println(" Fixing appointment "+a.toString());
	      GuiEvent ev = new GuiEvent(this, myAgent.FIXAPPOINTMENT);
	      ev.addParameter(a);
	      myAgent.postGuiEvent(ev); 
	      dispose();
	    }
	    catch (Exception e) { 
	      showErrorMessage(e.getMessage());
	    }
	}

    void showErrorMessage(String msg) {
        textFieldErrMsg.setText(msg);
        textFieldErrMsg.setVisible(true);
    }
    
	void buttonAddPerson_MouseClicked(java.awt.event.MouseEvent event)
	{
	  // Add a string to the List... Get the current item text
	  if (listKnownPersons.getSelectedItem() != null)
	    listInvitedPersons.addItem(listKnownPersons.getSelectedItem());
	}

	void buttonRemovePerson_MouseClicked(java.awt.event.MouseEvent event)
	{
	  // Delete an item from the List... Get the current item index
	  if (listInvitedPersons.getSelectedIndex() >= 0)
	    listInvitedPersons.delItem(listInvitedPersons.getSelectedIndex());
	}
}
