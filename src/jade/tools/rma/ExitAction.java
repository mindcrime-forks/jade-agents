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


package jade.tools.rma;

import javax.swing.JOptionPane;

/**
   
   @author Francisco Regi, Andrea Soracchi - Universita` di Parma
   @version $Date$ $Revision$
 */
	class ExitAction extends FixedAction {

  private rma myRMA;

  public ExitAction (rma anRMA,ActionProcessor actPro) {
    super ("ExitActionIcon","Exit this Container",actPro);
    myRMA = anRMA;
  }

  public void doAction() {
  	
  	int n = JOptionPane.showConfirmDialog(
                            myRMA.getRMAGui(),"Are you really sure to exit ?",
                            "Exit this container",
                            JOptionPane.YES_NO_OPTION);
    if (n == JOptionPane.YES_OPTION) 
    	myRMA.exit();
  }

} //End of ExitAction