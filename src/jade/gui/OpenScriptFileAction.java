/*
  $Log$
  Revision 1.3  1998/10/10 19:37:17  rimassa
  Imported a newer version of JADE GUI from Fabio.

  Revision 1.2  1998/10/04 18:01:41  rimassa
  Added a 'Log:' field to every source file.
*/

package jade.gui;

import com.sun.java.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.*;
import java.util.Vector;


/** 
 * Open a Script File Action
 * @see jade.gui.AMSAbstractAction
 */
public class OpenScriptFileAction extends AMSAbstractAction
{
	public OpenScriptFileAction ()
	{
		super("OpenScriptFileActionIcon","Open Script File");
	}
	public void actionPerformed(ActionEvent evt)
	{
		FileDialog fileDialog = new FileDialog(new JFrame());
		fileDialog.setMode(FileDialog.LOAD);
		fileDialog.show();
		System.out.println("Executing script");
	}

}

