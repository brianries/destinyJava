package Nio;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Brian on 2/9/2017.
 */
public class NioClientImpl implements NioClient {

    private NioChannel channel;

    public NioClientImpl(NioChannel channel) {
        this.channel = channel;
    }

    @Override
    public boolean connect(InetAddress hostAddress, int port) throws IOException {
        channel.connect(hostAddress, port);
        return true;
    }
}
