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


package jade.gui;

// Import required Java classes 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Import required JADE classes
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.core.Agent;
import jade.core.AID;

/**
@author Giovanni Caire - CSELT S.p.A
@version $Date$ $Revision$
*/

class DFGUIRegisterAction extends AbstractAction
{
	private DFGUI gui;

	public DFGUIRegisterAction(DFGUI gui)
	{
		super ("Register new agent");
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent e) 
	{	
		AID df; 
		System.out.println("REGISTER NEW AGENT");
		DFAgentDscDlg dlg = new DFAgentDscDlg((Frame) gui);
		DFAgentDescription editedDfd = dlg.ShowDFDGui(null,true,true);
	
		if (editedDfd != null)
		{
			int kind = gui.kindOfOperation();;
			if ((kind == DFGUI.PARENT_VIEW) || (kind == DFGUI.CHILDREN_VIEW)) // selected a df from the the federation
			{
				AID name = gui.getSelectedAgentInTable();
		    if (name != null)
		    	df = name; 
		    else	
			    df = gui.myAgent.getDescriptionOfThisDF().getName();
			}
			else
			df = gui.myAgent.getDescriptionOfThisDF().getName();

			gui.myAgent.postRegisterEvent((Object) gui, df, editedDfd);
		}
	}
}
	
