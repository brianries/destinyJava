package Nio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Brian on 2017-02-27.
 */
public class TCPConnection implements NioConnection {

    private static final Logger log = LogManager.getLogger(TCPConnection.class);

    private Selector selector;
    private ArrayList<SelectionKey> selectionKeys = new ArrayList<>();

    private AtomicBoolean shutdown = new AtomicBoolean(false);

    public TCPConnection() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();
        new Thread(this::mainLoop).start();
    }

    private void mainLoop() {
        log.debug("Starting mainLoop of NIO Service");
        while(!shutdown.get()) {
            try {
                // Blocks the thread waiting for activity
                if (selector.select() > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    for (SelectionKey key : selectionKeys) {
                        if (key.isAcceptable()) {

                        }
                        else if (key.isConnectable()) {

                        }
                        else if (key.isReadable()) {

                        }
                        else if (key.isWritable()) {

                        }
                    }
                    selectionKeys.clear();
                }
            } catch (IOException e) {
                log.error("Error in NIO Service thread. Stack Trace = ", e);
            }
        }
    }


    @Override
    public void accept(int port) {
        try {
            // Create a non blocking server socket channel
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);

            // Bind the server channel to the specified port
            InetSocketAddress isa = new InetSocketAddress(port);
            channel.socket().bind(isa);

            // Register the server channel, and set it to accept connections
            SelectionKey key = channel.register(selector, SelectionKey.OP_ACCEPT);

            // Example of 'tagging' this key to be identifiable - or giving it a buffer to use etc..
            key.attach("ServerAcceptPort");
            selectionKeys.add(key);
        }
        catch (IOException e) {
            log.error("Failed to set up accept on socket.  Stack Trace = ", e);
        }
    }


    @Override
    public void connect(InetAddress destination, int port) {


    }

    @Override
    public void read() {

    }

    @Override
    public void send() {

    }

    @Override
    public void close() {
        shutdown.set(true);
        selector.wakeup(); // breaks selector out of select() call
    }
}
