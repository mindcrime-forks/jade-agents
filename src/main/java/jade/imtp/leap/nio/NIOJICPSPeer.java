/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jade.imtp.leap.nio;

import java.io.IOException;
import java.net.Socket;

import jade.imtp.leap.JICP.Connection;
import jade.imtp.leap.JICP.ConnectionFactory;
import jade.mtp.TransportAddress;

/**
 * This class provides a {@link ConnectionFactory} that will construct {@link NIOJICPSConnection NIOJICPSConnections}.
 * Before the NIOJICPSConnections can be used {@link NIOJICPSConnection#init(java.nio.channels.SelectionKey)} must be called.
 * @author eduard
 */
public class NIOJICPSPeer extends NIOJICPPeer {

    public ConnectionFactory getConnectionFactory() {
        return new ConnectionFactory() {

            public Connection createConnection(Socket s) {
                return new NIOJICPSConnection();
            }

            public Connection createConnection(TransportAddress ta) throws IOException {
                return new NIOJICPSConnection();
            }
        };
    }



}
