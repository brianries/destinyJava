package Nio;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Brian on 2017-02-27.
 */

public interface NioConnection {

    void listen(int port);

    void connect(InetAddress destination, int port);

    void read();

    void send();

    void close();

}
