package Nio;

import java.net.InetAddress;

/**
 * Created by Brian on 2017-02-27.
 */

public interface NioConnection {

    void accept(int port);

    void connect(InetAddress destination, int port);

    void read();

    void send();

    void close();

}
