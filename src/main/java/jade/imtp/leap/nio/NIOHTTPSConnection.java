/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jade.imtp.leap.nio;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import jade.imtp.leap.ICPException;

/**
 *
 * @author eduard
 */
public class NIOHTTPSConnection extends NIOHTTPConnection {
    private SSLEngineHelper helper = null;
    private static Logger log = Logger.getLogger(NIOHTTPSConnection.class.getName());

    @Override
    public void close() throws IOException {
        try {
            helper.close();
        } 
        catch (IOException ex) {
        } 
        catch (Exception e) {
			log.log(Level.WARNING, "Unexpected error closing SSLHelper.", e);
		}
		
        super.close();
    }

    /**
     * need to set a HTTPHelper here, that can do wrapping of JICPPacket in HTTPPacket and unwrapping the other way
     * @param channel
     * @throws ICPException
     */
    void init(SocketChannel channel) throws ICPException {
        super.init(channel);
        helper = new SSLEngineHelper(channel.socket().getInetAddress().getHostAddress(), channel.socket().getPort(), this);
        addBufferTransformer(helper);
    }

}
