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

import  javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionListener;
import java.awt.Font;
import javax.swing.tree.DefaultMutableTreeNode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.tree.MutableTreeNode;
import java.util.Enumeration;
import java.awt.event.MouseListener;
import java.awt.event.*;

/**
   
   @author Francisco Regi, Andrea Soracchi - Universita` di Parma
   @version $Date$ $Revision$
 */
public class AgentTree extends JPanel{
  public JTree tree;
  static protected  Icon[] icons;
  private DescriptionNode desNode;

  static {
       icons = new Icon[4];
       icons[0]=GuiProperties.getIcon("TreeData.SuspendedIcon");
       icons[1]=GuiProperties.getIcon("TreeData.RunningIcon");
       icons[2]=GuiProperties.getIcon("TreeData.FolderIcon");
       icons[3]=GuiProperties.getIcon("TreeData.FolderIcon1");
   }


   // This class is abstract and represents the general node

  public abstract class  Node extends DefaultMutableTreeNode {

   protected Icon img;
   protected String name;
   protected int idIcon;

   public Node(int idIcon,String name) {
      this.idIcon = idIcon;
      this.name = name;
    }

   public Icon getIcon() {
    return icons[idIcon];
   }

   public String getName(){
    return name;
   }

   public void changeIcon(int idI) {
    idIcon=idI;
   }

   public abstract String getType();
   public abstract void setType(String type);
   public abstract String getToolTipText();

} // End of class Node

  public class AgentNode extends Node {
   private String typeAgent;
   private String stateAgent;
   private String addressAgent;

   public AgentNode(int idIcon,String name) {
     super(idIcon,name);
    }

    public void address(String address) {
     addressAgent=address;
    }

    public void setType(String type) {
     typeAgent=type;
    }

    public String getType() {
     return typeAgent;
    }

    public void setState(String state){
     stateAgent=state;
    }

    public String getAddress() {
     return addressAgent;
    }

    public String getToolTipText() {
     return stateAgent;
    }

}  // End of AgentNode


  // class that represents the ContainerNode

 public class ContainerNode extends Node {
  InetAddress addressmachine;
  String typeContainer;

  public ContainerNode(int idIcon,String name) {
     super(idIcon,name);
     try {
       addressmachine = InetAddress.getLocalHost(); // FIXME: Need the remote address of the container
     }
     catch (UnknownHostException a)
       { System.out.println("Error LocalHost");}
   }

  public void setType(String type) {
     typeContainer=type;
  }

  public String getType() {
     return typeContainer;
  }

  public String getToolTipText() {
     return name + ":" + addressmachine.getHostName() + "[" + addressmachine.getHostAddress() + "]";
    }

 } // End of ContainerNode

 public class SuperContainer extends Node {

  public SuperContainer(int idIcon,String name) {
   super(idIcon,name);
  }

  public String getToolTipText() {
   return ("Java Agent DEvelopment Framework");
  }

  public String getType(){
   return "";
  }

  public void setType(String noType) {}
}

 public AgentTree(Font f) {

  TreeSelectionModel selModel;
  TreeIconRenderer treeR;

  tree=new JTree();
  tree.setFont(f);
  tree.setModel(new AgentTreeModel(new SuperContainer(3,"JADE")));
  tree.setLargeModel(false);
  selModel = tree.getSelectionModel();
  selModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
  ToolTipManager.sharedInstance().registerComponent(tree);
  tree.setShowsRootHandles(true);
  treeR = new TreeIconRenderer();
  tree.setCellRenderer(treeR);
  tree.setRowHeight(0);

  desNode=new DescriptionNode();

  }

  public void listenerTree(TreeSelectionListener  panel) {
   tree.addTreeSelectionListener(panel);
  }

  public AgentTree.Node createNewNode(String name,int i) {
    switch(i) {
     case 0: return new AgentTree.ContainerNode(2,name);
     case 1 : return new AgentTree.AgentNode(1,name);
    }
   return null;
 }

  public void addContainerNode(Node node,String typeContainer) {
    AgentTreeModel model = getModel();
    MutableTreeNode root = (MutableTreeNode)model.getRoot();
    model.insertNodeInto(node, root, root.getChildCount());
    node.setType(typeContainer);
    return;
  }


  public void removeContainerNode(String nameNode) {
    AgentTreeModel model = getModel();
    MutableTreeNode root = (MutableTreeNode)model.getRoot();
	  Enumeration containers = root.children();
	    while(containers.hasMoreElements()) {
	      AgentTree.Node node = (AgentTree.Node)containers.nextElement();
	      String nodeName = node.getName();
	       if(nodeName.equalsIgnoreCase(nameNode)) {
	         model.removeNodeFromParent(node);
           return;
         }
      }
  }

  public void addAgentNode(Node node,String containerName,String agentName,String agentAddress,String agentType) {
      AgentTreeModel model = getModel();
      MutableTreeNode root = (MutableTreeNode)model.getRoot();
      node.setType(agentType);
      AgentTree.AgentNode nod=(AgentTree.AgentNode) node;
       nod.address(agentAddress);
       nod.setState("Running");
       	// Search for the agent container 'containerName'
	      Enumeration containers = root.children();
	        while(containers.hasMoreElements()) {
	          AgentTree.Node container = (AgentTree.Node)containers.nextElement();
	          String contName = container.getName();
	            if(contName.equalsIgnoreCase(containerName)) {
                // Add this new agent to this container and return
	              model.insertNodeInto(node, container, container.getChildCount());
                return;
             }
          }
  }

  public void removeAgentNode(String containerName, String agentName) {
   	AgentTreeModel model = getModel();
	  MutableTreeNode root = (MutableTreeNode)model.getRoot();

     // Search for the agent container 'containerName'
    Enumeration containers = root.children();
    while(containers.hasMoreElements()) {
	    AgentTree.Node container = (AgentTree.Node)containers.nextElement();
	    String contName = container.getName();
	     if(contName.equalsIgnoreCase(containerName)) {
        // Search for the agent 'agentName' in this agent container
	      Enumeration agents = container.children();
	      while(agents.hasMoreElements()) {
	        AgentTree.Node agent = (AgentTree.Node)agents.nextElement();
	        String agName = agent.getName();
	         if(agName.equalsIgnoreCase(agentName)){
             model.removeNodeFromParent(agent);
             return;
           }
        }
      }
    }
  }

  public AgentTreeModel getModel()
  {
      if (tree.getModel() instanceof AgentTreeModel)
	return (AgentTreeModel)tree.getModel();
      else {
	System.out.println(tree.getModel());
	return null;
      }
  }

  public void setParameter(String key, JPopupMenu popmenu) {
   if (!desNode.existsKey(key))
           desNode.addElementMap(key,popmenu);
  }

 public JPopupMenu getPopupMenu(String key) {
     return desNode.getPopupMenuMap(key);
 }    
} // End Of AgentTree;
