package Nio;

import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Created by Brian on 2/9/2017.
 */
public class TestNioClient extends TestCase {
    Mockery context = new Mockery();
    NioChannel channel = context.mock(NioChannel.class);

    public void testConstruction() {
        try {
            NioClientImpl client = new NioClientImpl(channel);
        }
        catch (Exception e) {
            assertTrue("Failed to construct", false);
        }

    }

    public void testConnection() {
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
        }
    }
}
