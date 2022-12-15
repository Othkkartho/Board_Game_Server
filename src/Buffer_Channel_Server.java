import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Set;

public class Buffer_Channel_Server {
    public static Vector<ClientHandler> clients = new Vector<>();
    public static int[] board;

    private static void informNew(String name) throws IOException {
        for (ClientHandler handler : clients)
            HelperMethods.sendMessage(handler.client, name + " is just logged in");
    }

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel sschannel = ServerSocketChannel.open();

            sschannel.bind(new InetSocketAddress(5001));
            sschannel.configureBlocking(false);

            sschannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(64);
            board = ClientHandler.boardSetup();
            System.out.println("Game server is ready.");

            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isAcceptable()) {
                        SocketChannel client = sschannel.accept();
                        System.out.println("Connect client: " + client.getRemoteAddress());
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    }

                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        String received = HelperMethods.receiveMessage(client);

                        System.out.println("Received: " + received);

                        StringTokenizer tokenizer = new StringTokenizer(received, "#");
                        String what = tokenizer.nextToken();
                        String data = tokenizer.nextToken();

                        if (what.equals("name")) {
                            ClientHandler handler = new ClientHandler(client, data, 0);
                            informNew(data);
                            clients.add(handler);
                            System.out.println(data + " 게임 참가 완료");
                        }
                        else {
                            int diceNum = Integer.parseInt(data);
                            int i = 0;

                            for (ClientHandler handler : clients) {
                                if (handler.name.equals(what)) {
                                    handler.player(handler.client, diceNum, board);
                                }
                            }
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
