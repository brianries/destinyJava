package Nio;

import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Created by Brian on 2/9/2017.
 */
public class TestNioConnection extends TestCase {
    Mockery context = new Mockery();
    NioConnection connection = context.mock(NioConnection.class);

    public void testConnection() {
        /*
        try {
            final InetAddress localHost = InetAddress.getByName("localhost");
            final int port = 10;

            context.checking(new Expectations() {{
                oneOf(channel).connect(localHost, port);
            }});

            NioClientImpl client = new NioClientImpl(channel);
            assertTrue(client.connect(localHost, port));
        }
        catch (Exception e) {
            assertTrue("Exception caught " + e.getMessage(), false);
        }*/
    }
}
