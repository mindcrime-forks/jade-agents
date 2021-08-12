package inProcess;

import test.common.*;
import java.io.*;
import java.net.InetAddress;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.*;
import jade.wrapper.*;

/**
   @author Fabio Bellifemine - TILAB
 */
public class TesterAgent extends test.common.TesterAgent {

	protected TestGroup getTestGroup() {
			return (new TestGroup("test/inProcess/inProcessTestsList.xml"));
	}

	
}
