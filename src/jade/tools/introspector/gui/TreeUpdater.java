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


package jade.tools.introspector.gui;

import jade.core.BehaviourID;
import jade.domain.introspection.AddedBehaviour;
import jade.domain.introspection.RemovedBehaviour;
import jade.domain.introspection.ChangedBehaviourState;

import javax.swing.tree.*;
import javax.swing.JTextArea;
import java.util.*;
import javax.swing.JTree;
import java.lang.reflect.*;

/**
 Receives a BehaviourEvent and updates the behaviour tree.

 @author Andrea Squeri,Corti Denis,Ballestracci Paolo -  Universita` di Parma
 */
public class TreeUpdater implements Runnable {
    private boolean current;
    private boolean add;
    private BehaviourID behaviour;
    private DefaultTreeModel model;
    private BehaviourPanel gui;
    private JTree behaviourTree;

    public TreeUpdater(AddedBehaviour b, BehaviourPanel bp) {
        behaviour = b.getBehaviour();
        gui = bp;
        behaviourTree = bp.getBehaviourTree();
        model = (DefaultTreeModel)behaviourTree.getModel();
        add = true;
        current = false;
    }
    
    public TreeUpdater(RemovedBehaviour b, BehaviourPanel bp) {
        behaviour = b.getBehaviour();
        gui=bp;
        behaviourTree = bp.getBehaviourTree();
        model=(DefaultTreeModel)behaviourTree.getModel();
        add = false;
        current = false;
    }
    
    public TreeUpdater(ChangedBehaviourState b, BehaviourPanel bp) {
        behaviour = b.getBehaviour();
        gui=bp;
        behaviourTree = bp.getBehaviourTree();
        model=(DefaultTreeModel)behaviourTree.getModel();
        add = false;
        current = true;
    }

    public static void description(JTextArea t, BehaviourID b){
        t.setText(b.getName());
    }

    public void createTree(DefaultMutableTreeNode r,Iterator v){
        while(v.hasNext()){
            BehaviourID b=(BehaviourID)v.next();
            DefaultMutableTreeNode rc=new DefaultMutableTreeNode(b);
            
            if (!b.isSimple()) {
                createTree(rc,b.getAllChildren());
            }
            
            r.add(rc);
        }
    }

    public void run()
    {
        boolean bFound = false;
        
        DefaultMutableTreeNode root=(DefaultMutableTreeNode)model.getRoot();

        if (current)
        {
            behaviourTree.clearSelection();
            Enumeration e=root.breadthFirstEnumeration();
            if(e.hasMoreElements())
                e.nextElement();

            while(e.hasMoreElements())
            {
                DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
                BehaviourID b=(BehaviourID) node.getUserObject();
                if (b.equals(behaviour))
                {
                    Object[]o=node.getPath();
                    TreePath tp= new TreePath(o);
                    behaviourTree.setSelectionPath(tp);
                    this.description(gui.getBehaviourText(),b);
                    bFound = true;
                    break;
                }
            }
        }

        if (add || !bFound)
        {
            DefaultMutableTreeNode beh=new DefaultMutableTreeNode(behaviour);

            if (!behaviour.isSimple()) {
                createTree(beh, behaviour.getAllChildren());
            }

            model.insertNodeInto(beh,root,model.getChildCount(root));
        }
        else if (!add)
        {
            Enumeration e=root.breadthFirstEnumeration();
            e.nextElement();
            while(e.hasMoreElements())
            {
                DefaultMutableTreeNode node =(DefaultMutableTreeNode)e.nextElement();
                BehaviourID b=(BehaviourID)node.getUserObject();
                if (b.equals(behaviour)){
                    model.removeNodeFromParent(node);
                    break;
                }
            }
        }
    }
}
