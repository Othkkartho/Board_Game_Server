import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Buffer_Channel_Server {
    public static Vector<ClientHandler> clients = new Vector<>();
    public static int[] board;

    private static void informNew(String name) throws IOException {
        for (ClientHandler handler : clients)
            HelperMethods.sendMessage(handler.client, name + " is just logged in");
    }

    private static Selector acceptClient() {
        Selector selector = null;
        try {
            selector = Selector.open();
            ServerSocketChannel sschannel = ServerSocketChannel.open();

            sschannel.bind(new InetSocketAddress(5001));
            sschannel.configureBlocking(false);

            sschannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(64);
            board = ClientHandler.boardSetup();
            System.out.println("Game server is ready.");

            boolean open = false;

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
                            if (clients.size() == 1) {
                                HelperMethods.sendMessage(handler.client, "host");
                            }
                            System.out.println(data + " 게임 참가 완료");
                        }

                        if (what.equals("open")) {
                            open = true;
                        }
                    }
                    iterator.remove();
                }
                if (open == true) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return selector;
    }

    private static void startGame(Selector selector) {
        System.out.println("참가자들 게임 시작");
        for (ClientHandler handler : clients)
            HelperMethods.sendMessage(handler.client, "Game Start");

        try {
            int i = 0;

            HelperMethods.sendMessage(clients.get(i).client, "Your Turn");

            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

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
                        } else if (data.equals("quit")) {
                            clients.removeIf(handler -> handler.name.equals(what));
                            for (ClientHandler handler : clients)
                                if (!handler.name.equals(what)) {
                                    HelperMethods.sendMessage(handler.client, what+" is leaved");
                                }
                            client.close();
                        } else {
                            int diceNum = Integer.parseInt(data);

                            for (ClientHandler handler : clients) {
                                if (handler.name.equals(what)) {
                                    handler.player(handler.client, diceNum, board);
                                }
                            }
                        }
                    }
                    iterator.remove();
                }

                if (clients.size() != 0) {
                    i = (i+1)% clients.size();
                    HelperMethods.sendMessage(clients.get(i).client, "Your Turn");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Selector selector = acceptClient();
        startGame(selector);
    }
}
