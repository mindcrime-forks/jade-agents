package jade.gui;

import com.sun.java.swing.*;
import java.util.Properties;
import java.io.*;
/**
 * This class encapsulates some informations used by the program
 */
  public class GuiProperties 
{
	  protected static UIDefaults MyDefaults;
	  protected static GuiProperties foo = new GuiProperties();
	  public static final String ImagePath = "";
	  static
	  {
		  Object[] icons = {
				    "AMSAbstractAction.AddAgentPlatformActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/connect.gif"),
					"AMSAbstractAction.AddAgentActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/cervello.gif"),
				    "AMSAbstractAction.CustomActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/custom.gif"),
				    "AMSAbstractAction.ExitActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/kill.gif"),
				    "AMSAbstractAction.getPropertiesActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/properties.gif"),
			 	    "AMSAbstractAction.OpenScriptFileActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/open.gif"),
					"AMSAbstractAction.KillActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/kill.gif"),
					"AMSAbstractAction.PingActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/ping.gif"),
					"AMSAbstractAction.ReloadActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/reload.gif"),
					"AMSAbstractAction.RemoveActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/ex.gif"),
					"AMSAbstractAction.ResumeActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/sveglia.gif"),
					"AMSAbstractAction.SnifferActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/sniffer.gif"),
					"AMSAbstractAction.StartActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/start.gif"),
					"AMSAbstractAction.StartNewAgentActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/baby.gif"),
					"AMSAbstractAction.SuspendActionIcon",LookAndFeel.makeIcon(foo.getClass(), "images/suspend.gif"),
					"TreeData.SuspendedIcon",LookAndFeel.makeIcon(foo.getClass(), "images/stopTree.gif"),
					"TreeData.RunningIcon",LookAndFeel.makeIcon(foo.getClass(), "images/fg.gif"),
					"TreeData.FolderIcon",LookAndFeel.makeIcon(foo.getClass(), "images/TreeClosed.gif")

		  };
		  MyDefaults = new UIDefaults (icons);
	  }

    public static final Icon getIcon(String key)
	{
	Icon i = MyDefaults.getIcon(key);
	if (i == null)
	{
		System.out.println(key);
		System.exit(-1);
		return null;
	}
	else return MyDefaults.getIcon(key);
	}
}

