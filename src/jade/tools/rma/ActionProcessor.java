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

import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.util.HashMap;
import jade.gui.AgentTree;

/**
   Javadoc documentation for the file
   @author Francisco Regi, Andrea Soracchi - Universita` di Parma
   @version $Date$ $Revision$
 */
class ActionProcessor {

  private MainPanel panel;
  private RMAAction action;

     public static final String  START_ACTION="Start new Agent";
     public static final String  KILL_ACTION="Kill";
     public static final String  SUSPEND_ACTION="Suspend Agent";
     public static final String  RESUME_ACTION="Resume Agent";
     public static final String  CUSTOM_ACTION="Custom Agent";
     public static final String  SNIFFER_ACTION="Start Sniffer";
     public static final String  DUMMYAG_ACTION="Start DummyAgent";
     public static final String  CLOSE_ACTION="Close RMA";
     public static final String  EXIT_ACTION="Exit RMA";
     public static final String  SHUTDOWN_ACTION="Shutdown action";
     public static final String  SHOWDF_ACTION="ShowDfGui Action";
     public static final Map actions=new HashMap(11);

 public ActionProcessor(rma anRma,MainWindow mWnd,MainPanel panel) {
      this.panel=panel;
      actions.put(START_ACTION,new StartNewAgentAction(anRma, mWnd,this));
      actions.put(KILL_ACTION,new KillAction(KILL_ACTION, anRma,this));
      actions.put(SUSPEND_ACTION,new SuspendAction(anRma,this));
      actions.put(RESUME_ACTION,new ResumeAction(anRma,this));
      actions.put(CUSTOM_ACTION,new CustomAction(anRma, mWnd,this));
      actions.put(SNIFFER_ACTION,new SnifferAction(anRma,this));
      actions.put(DUMMYAG_ACTION,new DummyAgentAction(anRma,this));
      actions.put(CLOSE_ACTION,new CloseRMAAction(anRma,this));
      actions.put(EXIT_ACTION,new ExitAction(anRma,this));
      actions.put(SHUTDOWN_ACTION,new ShutDownAction(anRma,this));
      actions.put(SHOWDF_ACTION,new ShowDFGuiAction(anRma,this));
} // End builder

 public void process(RMAAction a) {
  int lungpath;
  AgentTree.Node now;
  
  FixedAction fx;
  TreePath paths[];
   action = a;
   paths = panel.treeAgent.tree.getSelectionPaths();

   // Fixed actions are without parameters, so they are executed once,
   // regardless how many tree elements are selected
   if (action instanceof FixedAction)
     fixedAct();

   // Other actions are executed for every selected tree element. This
   // means that, if no selection is present, no action is performed.
   else {
     if(paths != null) {
       lungpath=paths.length;
       for (int i=0;i<lungpath;i++) {
         now = (AgentTree.Node) (paths[i].getLastPathComponent());
         if (action instanceof AgentAction) agentAct(now);
         else if (action instanceof ContainerAction) containerAct(now);
           else if (action instanceof GenericAction) genericAct(now);
       }
     }
   }

 } // End Process

 private void fixedAct(){
  FixedAction fx=(FixedAction)action;
  fx.doAction();
 }

 private void agentAct(AgentTree.Node node){
  AgentAction ag=(AgentAction) action;
  AgentTree.AgentNode nod;
    if(node instanceof AgentTree.AgentNode) {
       nod=(AgentTree.AgentNode)node;
       ag.doAction(nod);
    }
 }

 private void containerAct(AgentTree.Node node){
  ContainerAction ac=(ContainerAction) action;
  AgentTree.ContainerNode nod;
   try{
     if(node instanceof AgentTree.ContainerNode){
      nod=(AgentTree.ContainerNode)node;
      ac.doAction(nod);
     }
     else throw new StartException();
   } catch(StartException ex) {
	   StartException.handle();
     }
 }

 private void genericAct(AgentTree.Node node){
  AgentTree.AgentNode nod1;
  AgentTree.ContainerNode nod2;
  GenericAction ga=(GenericAction) action;
  if(node instanceof AgentTree.ContainerNode){
      nod2=(AgentTree.ContainerNode)node;
      ga.doAction(nod2);
    }
  else if(node instanceof AgentTree.AgentNode) {
       nod1=(AgentTree.AgentNode)node;
       ga.doAction(nod1);
    }
 }

} // End of ActionProcessor
