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

import java.awt.*;

import java.net.InetAddress;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.tree.MutableTreeNode;

import jade.core.AID;

import jade.gui.AgentTreeModel;
import jade.gui.AgentTree;

import jade.lang.acl.ACLMessage;

/**
   
   @author Francisco Regi, Andrea Soracchi - Universita` di Parma
   @version $Date$ $Revision$
 */
class MainWindow extends JFrame {

  private MainPanel tree;
  private ActionProcessor actPro;
  private PopupMenuAgent popA;
  private PopupMenuContainer popC;

  public MainWindow (rma anRMA) {
    super("JADE Remote Agent Management GUI");
    tree = new MainPanel(anRMA, this);
    actPro = new ActionProcessor(anRMA, this, tree);
    setJMenuBar(new MainMenu(this,actPro));
    popA = new PopupMenuAgent(actPro);
    popC = new PopupMenuContainer(actPro);

    tree.treeAgent.register("FIPAAGENT",popA,"images/runtree.gif");
    tree.treeAgent.register("FIPACONTAINER",popC,"images/TreeClosed.gif");

    setForeground(Color.black);
    setBackground(Color.lightGray);
    addWindowListener(new WindowCloser(anRMA));

    getContentPane().add(new ToolBar(tree,this,actPro),"North"); // new ToolBar(tree, this, ActionProcessor.actions)
    getContentPane().add(tree,"Center");
  }

  public void ShowCorrect() {
    pack();
    setSize(600, 400);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int centerX = (int)screenSize.getWidth() / 2;
    int centerY = (int)screenSize.getHeight() / 2;
    setLocation(centerX - 300, centerY - 200);
    tree.adjustDividersLocation();
    setVisible(true);
    toFront();
  }

  // Perform asynchronous disposal to avoid nasty InterruptedException
  // printout.
  public void disposeAsync() {

    class disposeIt implements Runnable {
      private Window toDispose;

      public disposeIt(Window w) {
	toDispose = w;
      }

      public void run() {
	toDispose.dispose();
      }

    }

    // Make AWT Event Dispatcher thread dispose RMA window for us.
    SwingUtilities.invokeLater(new disposeIt(this));

  }

  public AgentTreeModel getModel() {
    return tree.treeAgent.getModel();
  }

  public void addContainer(final String name, final InetAddress addr) {
    Runnable addIt = new Runnable() {
      public void run() {
        MutableTreeNode node = tree.treeAgent.createNewNode(name, 0);
        tree.treeAgent.addContainerNode((AgentTree.ContainerNode)node,"FIPACONTAINER",addr);
      }
    };
    SwingUtilities.invokeLater(addIt);
  }

  public void removeContainer(final String name) {

    // Remove a container from the tree model
    Runnable removeIt = new Runnable() {

      public void run() {
       tree.treeAgent.removeContainerNode(name);
     }
    };
    SwingUtilities.invokeLater(removeIt);
  }

  public void addAgent(final String containerName, final AID agentID) {

    // Add an agent to the specified container
    Runnable addIt = new Runnable() {
      public void run() {
	      String agentName = agentID.getName();
       	AgentTree.Node node = tree.treeAgent.createNewNode(agentName, 1);
       	Iterator add = agentID.getAllAddresses();
       	String agentAddresses = "";
       	while(add.hasNext())
       		agentAddresses = agentAddresses + add.next() + " ";
        
        //tree.treeAgent.addAgentNode((AgentTree.AgentNode)node, containerName, agentName, "agentAddress", "FIPAAGENT");
       		tree.treeAgent.addAgentNode((AgentTree.AgentNode)node, containerName, agentName, agentAddresses, "FIPAAGENT");
      }
    };
    SwingUtilities.invokeLater(addIt);
  }

  public void removeAgent(final String containerName, final AID agentID) {

    // Remove an agent from the specified container
    Runnable removeIt = new Runnable() {
      public void run() {
	String agentName = agentID.getName();
	tree.treeAgent.removeAgentNode(containerName, agentName);
      }
    };
    SwingUtilities.invokeLater(removeIt);
  }

  public void showErrorDialog(String text, ACLMessage msg) {
    String messages[] = new String[3];
    messages[0] = text;
    messages[1] = "";
    messages[2] = "Do you want to view the ACL message ?";
    int answer = JOptionPane.showConfirmDialog(this, messages, "RMA Error !!!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
    switch(answer) {
    case JOptionPane.YES_OPTION:
      jade.gui.AclGui.showMsgInDialog(msg, this);
      break;
    default:
      break;
    }
  }

  private void setUI(String ui) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf."+ui);
      SwingUtilities.updateComponentTreeUI(this);
      pack();
    }
    catch(Exception e) {
	System.out.println(e);
	e.printStackTrace(System.out);
    }
  }
  /**
     enables Motif L&F
  */
  public void setUI2Motif() {
    setUI("motif.MotifLookAndFeel");
  }

  /**
     enables Windows L&F
  */
  public void setUI2Windows() {
    setUI("windows.WindowsLookAndFeel");
  }

  /**
     enables Multi L&F
  */
  public void setUI2Multi() {
    setUI("multi.MultiLookAndFeel");
  }

  /**
     enables Metal L&F
  */
  public void setUI2Metal() {
    setUI("metal.MetalLookAndFeel");
  }

} // End of MainWindow
