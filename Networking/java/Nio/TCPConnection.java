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

    private final LinkedList<Runnable> mainThreadWorkQueue = new LinkedList<>();

    private Selector selector;
    private ArrayList<SelectionKey> selectionKeys = new ArrayList<>();

    private ArrayList<SocketChannel> connections = new ArrayList<>();

    private AtomicBoolean shutdown = new AtomicBoolean(false);

    public TCPConnection() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();
        new Thread(this::mainLoop).start();
    }

    private void mainLoop() {
        log.debug("Starting main loop of NIO Service");
        while(!shutdown.get()) {
            try {
                // Blocks the thread waiting for activity
                if (selector.select() > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        }
                        else if (key.isConnectable()) {
                            handleConnection(key);
                        }
                        else if (key.isReadable()) {

                        }
                        else if (key.isWritable()) {

                        }
                        iterator.remove();
                    }
                }

                // Complete any pending work for the main thread
                synchronized (mainThreadWorkQueue) {
                    for (Runnable work : mainThreadWorkQueue) {
                        work.run();
                    }
                    mainThreadWorkQueue.clear();
                }
            } catch (IOException e) {
                log.error("Error in NIO Service thread. Stack Trace = ", e);
            }
        }

        synchronized (connections) {
            for (SocketChannel connection : connections) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.error("Error closing connection. Stack trace = ", e);
                }
            }
            connections.clear();
        }
    }

    private void addWorkToMainThread(Runnable runnable) {
        synchronized (mainThreadWorkQueue) {
            mainThreadWorkQueue.add(runnable);
            selector.wakeup();
        }
    }

    @Override
    public void listen(int port) {
        addWorkToMainThread(() -> {
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
        });
    }


    @Override
    public void connect(InetAddress destination, int port) {
        addWorkToMainThread(() -> {
            try {
                SocketChannel newChannel = SocketChannel.open();
                newChannel.configureBlocking(false);

                SelectionKey key = newChannel.register(selector, SelectionKey.OP_CONNECT);
                key.attach("Connection");

                log.debug("Attempting connection to " + destination + ":" + port);
                newChannel.connect(new InetSocketAddress(destination, port));
            }
            catch (IOException e) {
                log.error("Failed to set up accept on socket.  Stack Trace = ", e);
            }
        });
    }

    @Override
    public void read() {

    }

    @Override
    public void send() {

    }

    private void handleConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Finish the connection. If the connection operation failed
        // this will raise an IOException.
        try {
            socketChannel.finishConnect();
            connections.add(socketChannel);

            log.debug("Successfully connected to " + socketChannel.getRemoteAddress().toString());
        } catch (IOException e) {
            log.error("Failed to finish connection. Stack trace: ", e);
            // Cancel the channel's registration with our selector
            key.cancel();
        }
    }


    private void handleAccept(SelectionKey key) throws IOException {
        log.debug("Handling new connection...");
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        log.debug("Accepted new connection from " + socketChannel.getRemoteAddress().toString());
        connections.add(socketChannel);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(this.selector, SelectionKey.OP_READ);
        log.debug("Connection complete ");
    }

    @Override
    public void close() {
        shutdown.set(true);
        selector.wakeup(); // breaks selector out of select() call
    }
}
