/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent systems in compliance with the FIPA specifications.
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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.*;

/**
Javadoc documentation for the file
@author Giovanni Rimassa - Universita` di Parma
@version $Date$ $Revision$
*/

/**
 * DummyAgentAction spawns an external application passing as parameters a 
 * String containing ALL agents selected in the Tree
 * @see jade.gui.AMSAbstractAction
 */
public class DummyAgentAction extends AMSAbstractAction
{
  /**
   * Progressive Number to give always a new name to DummyAgent
   */
  private  static int progressiveNumber = 0;

  private rma myRMA;

    public DummyAgentAction(rma anRMA) {
      super ("DummyAgentActionIcon","Start DummyAgent");
      progressiveNumber = 0;
      myRMA = anRMA;
    }
	
    public void actionPerformed(ActionEvent e) 
    {
      myRMA.newAgent("da"+progressiveNumber, "jade.tools.DummyAgent.DummyAgent", new String());
      progressiveNumber++;
    }
}
