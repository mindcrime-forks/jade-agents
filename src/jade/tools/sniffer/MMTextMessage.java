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



package jade.tools.sniffer;

import java.awt.Font;
import javax.swing.JTextField;
/**
Javadoc documentation for the file
@author Gianluca Tanca
@version $Date$ $Revision$
*/

/**
 * Is the field in the bottom of the main frame. Shows the type of message
 *
 * @see javax.swing.JTextField
 */
public class MMTextMessage extends JTextField {
	
  Font font = new Font("SanSerif",Font.PLAIN,14); 
	
	public MMTextMessage(){
		super();
		setDoubleBuffered(true);
		setEditable(false);
		setFont(font);
		setText("No Info Message");
	}
}