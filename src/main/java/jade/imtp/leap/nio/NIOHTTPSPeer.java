package jade.imtp.leap.nio;

import java.io.IOException;
import java.net.Socket;

import jade.imtp.leap.TransportProtocol;
import jade.imtp.leap.JICP.Connection;
import jade.imtp.leap.JICP.ConnectionFactory;
import jade.imtp.leap.http.HTTPSProtocol;
import jade.mtp.TransportAddress;

/**
 *
 * @author Eduard Drenth: Logica, 12-jul-2009
 * 
 */
public class NIOHTTPSPeer  extends NIOHTTPPeer {

    public ConnectionFactory getConnectionFactory() {
        return new ConnectionFactory() {

            public Connection createConnection(Socket s) {
                return new NIOHTTPSConnection();
            }

            public Connection createConnection(TransportAddress ta) throws IOException {
                return new NIOHTTPSConnection();
            }
        };
    }

    public TransportProtocol getProtocol() {
        return HTTPSProtocol.getInstance();
    }

}
