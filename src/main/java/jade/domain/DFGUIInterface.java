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

package jade.domain;

import jade.core.AID;
import jade.util.leap.Iterator;
import jade.util.leap.List;

/**
  
  This interface is used to avoid any relationship between the df 
  and the tools packages.
  A gui for a DF must implements this interface.

  @author Tiziana Trucco - CSELT S.p.A.
  @version $Date$ $Revision$

*/

public interface DFGUIInterface
{

  public void addParent(AID parentName);
  public void removeParent(AID parentName);
  
  public void addAgentDesc(AID name); 
  public void removeAgentDesc(AID name, AID df);
  
  public void addChildren(AID childrenName);
  public void removeChildren(AID childrenName);
  
  public void setAdapter(DFGUIAdapter dfa);
  
  public void showStatusMsg(String msg);
	public void refreshLastSearchResults(List l, AID df);
  public void removeSearchResult(AID name);
  public void disposeAsync();
  public void setVisible(boolean b);
  public void refresh(Iterator AIDOfAllAgentRegistered,Iterator parents,Iterator children) ;

	
}
