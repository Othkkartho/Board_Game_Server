import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Vector;

public class Buffer_Channel_Server {
    public static Vector<ClientHandler> clients = new Vector<>();

//    private static void informNew(String name) throws IOException {
//        for (ClientHandler handler : clients) {
//            String msg = name + " is just logged in";
//            HelperMethods.sendMessage(handler.client, msg);
//        }
//    }

    public static void main(String[] args) {
        String msg;
        int diceNum;

        try {
            ServerSocketChannel sschannel = ServerSocketChannel.open();
            sschannel.bind(new InetSocketAddress(5001));

            while (true) {
                System.out.println("Chatting server is ready.");
                SocketChannel client = sschannel.accept();

                String name = HelperMethods.receiveStringMessage(client);
                System.out.println(name + ": Welcome to the server.");
//                informNew(name);

                ClientHandler handler = new ClientHandler(client, name, 0);
                Thread thread = new Thread(handler);
                System.out.println("Adding this client to client vector");
                clients.add(handler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}