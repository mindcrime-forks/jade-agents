package jade.imtp.leap.nio;

import java.net.InetAddress;

import jade.imtp.leap.ICPException;
import jade.imtp.leap.JICP.Connection;
import jade.imtp.leap.JICP.JICPMediator;
import jade.imtp.leap.JICP.JICPPacket;
import jade.util.leap.Properties;

/**
   Base interface for all mediators that can be managed by 
   asynchronous JICPMediatorManagers such as the
   <code>BEManagementService</code>.
   @author Giovanni Caire - Telecom Italia LAB S.p.A.
 */
public interface NIOMediator extends JICPMediator {
	/**
	   Overloaded version of the handleJICPPacket() method including
	   the <code>Connection</code> the incoming JICPPacket was received
	   from. This information is important since, unlike normal mediators,
	   a NIOMediator may not read packets from connections on its own (the
	   JICPMediatorManager does that in general).
	 */
	JICPPacket handleJICPPacket(Connection c, JICPPacket p, InetAddress addr, int port) throws ICPException;

	/**
	   Notify this NIOMediator that an error occurred on one of the 
	   Connections it was using. This information is important since, 
	   unlike normal mediators, a NIOMediator may not read packets from 
	   connections on its own (the JICPMediatorManager does that in general).
	 */
	void handleConnectionError(Connection c, Exception e);
	
	/**
	   Retrieve the startup Properties for this NIOMediator.
	 */
	Properties getProperties();
}

