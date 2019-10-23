package origin.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class BaseNio {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
//        ServerSocket socket = serverSocketChannel.socket();
        int port = 8080;
        SocketAddress endPoit = new InetSocketAddress(port);
//        socket.bind(endPoit);
        serverSocketChannel.bind(endPoit);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, serverSocketChannel.validOps());
        SocketChannel clientChannel = null;
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        System.out.println("8080 port begin listen");
        while (true) {
            int select = selector.select();
            if (select == 0) {
                System.err.println("select wakes up with error");
            }
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                int ops = key.interestOps();
                if ((ops & SelectionKey.OP_ACCEPT) != 0) {
                    clientChannel = serverSocketChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ, null);
                    System.out.println("2. server accepted and register read op client - " + clientChannel.socket().getInetAddress());
                }
                if ((ops & SelectionKey.OP_READ) != 0) {
                    System.out.println("3. server accepted and register read op client - " + clientChannel.socket().getInetAddress());
                }
            }

        }
    }
}
