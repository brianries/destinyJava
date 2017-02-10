package Nio;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Brian on 2/9/2017.
 */
public interface NioClient {

    boolean connect(InetAddress hostAddress, int port) throws IOException;

}

