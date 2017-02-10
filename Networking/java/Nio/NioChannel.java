package Nio;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Brian on 2/9/2017.
 */
public interface NioChannel {

    void connect(InetAddress address, int port) throws IOException;

    void sendData(byte[] data);

}
