package Nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by Brian on 2/9/2017.
 */
public class NioChannelImpl implements NioChannel {
    private SocketChannel socketChannel;

    public NioChannelImpl() {
        try {
            this.socketChannel = SocketChannel.open();
            this.socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(InetAddress hostAddress, int port) throws IOException {
        socketChannel.connect(new InetSocketAddress(hostAddress, port));
    }

    @Override
    public void sendData(byte[] data) {

    }
}
