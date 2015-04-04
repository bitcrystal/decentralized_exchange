package de.bitcrystal.decentralizedexchange.upnp;
//author mike
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ServerSocketFactory;
import net.jxta.impl.endpoint.IPUtils;
import net.jxta.impl.util.TimeUtils;
import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

public class UPnPServerSocketFactory extends ServerSocketFactory {

    private static final Logger LOG = Logger.getLogger(UPnPServerSocketFactory.class.getName());
    private final InetAddress ANYADDRESS;
    private InternetGatewayDevice[] IGDs = null;
    private InternetGatewayDevice currentIGD = null;
    private long lastIGDQueryAt = 0;
    private static ServerSocketFactory INSTANCE = null;
    private static boolean hasUPnPSupport = false;

    public UPnPServerSocketFactory() throws IOException {
        // XXX 20070210 bondolo IPv4 specific.
        ANYADDRESS = InetAddress.getByName("0.0.0.0");

        if (null == getCurrentIGD()) {
            throw new IOException("No suitable internet gateway devices found.");
        }
    }

    public InetAddress getExternalAddress() throws IOException {
        InternetGatewayDevice useIGD = getCurrentIGD();

        if (null == useIGD) {
            throw new IOException("No suitable internet gateway devices found.");
        }
        try {
            String response = useIGD.getExternalIPAddress();

            InetAddress result = InetAddress.getByName(response);

            return result;
        } catch (UPNPResponseException ex) {
            IOException rethrow = new IOException("UPnP Operation Failed : " + ex.getMessage());
            rethrow.initCause(ex);

            throw rethrow;
        }
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        return createServerSocket(port, 0, ANYADDRESS);
    }

    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        return createServerSocket(port, backlog, ANYADDRESS);
    }

    public synchronized ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress)
            throws IOException {

        InternetGatewayDevice useIGD = getCurrentIGD();

        if (null == useIGD) {
            throw new IOException("No suitable internet gateway devices found.");
        }

        // The local socket. This is what we will pass back and what the router will forward to.
        ServerSocket localServerSocket = new ServerSocket(port, backlog, ifAddress);

        if (ifAddress.equals(ANYADDRESS)) {
            // FIXME 20070210 bondolo This is totally wrong and entirely speculative. 
            // But it does work if you're not mulithomed.
            ifAddress = InetAddress.getLocalHost();
        }


        try {
            boolean mapped = useIGD.addPortMapping("JXTA TCP ServerSocket",
                    null, localServerSocket.getLocalPort(),
                    localServerSocket.getLocalPort(), ifAddress.getHostAddress(),
                    0, "TCP");
        } catch (UPNPResponseException ex) {
            IOException rethrow = new IOException("UPnP Operation Failed : " + ex.getMessage());
            rethrow.initCause(ex);

            throw rethrow;
        } catch (IOException failed) {
            try {
                localServerSocket.close();
            } catch (IOException ignored) {
                ;
            }

            throw failed;
        }

        return localServerSocket;
    }

    private synchronized InternetGatewayDevice getCurrentIGD() {
        if (TimeUtils.toRelativeTimeMillis(TimeUtils.timeNow(), lastIGDQueryAt) > (5 * TimeUtils.AMINUTE)) {
            IGDs = null;
            currentIGD = null;

            try {
                IGDs = InternetGatewayDevice.getDevices((int) (5 * TimeUtils.ASECOND));

                if (null != IGDs) {
                    // FIXME 20070210 bondolo This may cause us to change IGD even though 
                    // our current one has not failed.
                    currentIGD = IGDs[0];
                }
            } catch (IOException failed) {
            }
            lastIGDQueryAt = TimeUtils.timeNow();
        }

        return currentIGD;
    }

    public static ServerSocketFactory getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new UPnPServerSocketFactory();
                hasUPnPSupport=true;
            } catch (Exception ex) {
                INSTANCE = getDefault();
                hasUPnPSupport=false;
            }
        }
        return INSTANCE;
    }

    public static ServerSocket getServerSocket(int port) throws IOException {
        if(!hasUPnPSupport)
            return new ServerSocket(port);
        return getInstance().createServerSocket(port);
    }
    
    public static boolean hasUPnPSupport()
    {
        if(INSTANCE==null)
        {
            INSTANCE=getInstance();
        }
        return hasUPnPSupport;
    }
}