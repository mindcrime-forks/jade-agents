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

package jade.core.nodeMonitoring;

//#APIDOC_EXCLUDE_FILE
//#MIDP_EXCLUDE_FILE

import jade.core.Node;
import jade.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * The <code>UDPMonitorClient</code> sends UDP ping messages 
 * in a specified interval to the main container.
 *
 * @author Roland Mungenast - Profactor
 * @since JADE 3.3
 */
class UDPMonitorClient {
 
  private boolean running = false;
  private boolean terminating = false;
  
  private DatagramChannel channel;
  private String serverHost;
  private int serverPort;
  private ByteBuffer ping;
  private int pingDelay;
  private Node node;
  private Thread sender;
  private Logger logger;
  
  /**
   * Private class which sends ping messages in regular time intervals
   * 
   * @author Roland Mungenast - Profactor
   * @since JADE 3.3
   */
  private class Sender implements Runnable {

    public void run() {
      
      while (running) {
        updatePing();
        try {
          channel.send(ping, new InetSocketAddress(serverHost, serverPort));
          Thread.sleep(pingDelay - 5);
        } catch (IOException e) {
          if(logger.isLoggable(Logger.SEVERE))
            logger.log(Logger.SEVERE,"Error sending UDP ping message for node " + node.getName());
        } catch (InterruptedException e) { 
          // ignore --> the ping with the termination flag has to be sent immediately
        }
      }
      
      try {
        channel.close();
      } catch (IOException e) {
        if(logger.isLoggable(Logger.FINER))
          logger.log(Logger.FINER,"Error closing UDP channel");
      }
    }
    
    private void updatePing() {
      String nodeName = node.getName();
      ping = ByteBuffer.allocate(4 + nodeName.length() + 1);
      ping.position(0);
      ping.putInt(nodeName.length());
      ping.put(nodeName.getBytes());
      
      if (terminating) {
        ping.put((byte)1);
        running = false;
      } else {
        ping.put((byte)0);
      }
      ping.position(0);
    }
  }
  
  /**
   * Constructor
   * @param node Node for which to send ping messages
   * @param serverHost hostname of the server
   * @param serverPort port on which the server is listening for ping messages
   */
  public UDPMonitorClient(Node node, String serverHost, int serverPort, int pingDelay) {
    logger = Logger.getMyLogger(this.getClass().getName());
    this.node = node;
    this.serverHost = serverHost;
    this.serverPort = serverPort;
    this.pingDelay = pingDelay;
  }

  /**
   * Start sending UDP ping messages to the node failure server
   * @throws IOException if the 
   */
  public void start() throws IOException {
    channel = DatagramChannel.open();
    running = true;
    sender = new Thread(new Sender());
    sender.start();
    
    if (logger.isLoggable(Logger.INFO))
      logger.log(Logger.INFO,"UDP monitoring client has been started.");
    
    if (logger.isLoggable(Logger.CONFIG))
      logger.log(Logger.CONFIG, "(Server host: '" + serverHost 
          + "', ServerPort: " + serverPort + ", Ping delay: " + pingDelay + ")");
  }
  
  /**
   * Stop sending UDP ping messages
   */
  public void stop() {
    terminating = true;
    sender.interrupt();
    
    if (logger.isLoggable(Logger.INFO))
      logger.log(Logger.INFO,"UDP monitoring client has been stopped.");
    
  }
}


