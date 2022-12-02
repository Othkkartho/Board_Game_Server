import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Vector;

public class Buffer_Channel_Server {
    public static void main(String[] args) {
        try {
            ServerSocketChannel sschannel = ServerSocketChannel.open();
            sschannel.configureBlocking(true);
            sschannel.bind(new InetSocketAddress(5001));

            while (true) {
                System.out.println("Chatting server is ready.");
                SocketChannel client = sschannel.accept();
                sschannel.configureBlocking(true);
                System.out.println("Connected client: " + client.getRemoteAddress());
                String msg;
                Scanner scn = new Scanner(System.in);

                while (true) {
                    System.out.print("> ");
                    msg = scn.nextLine();

                    HelperMethods.sendMessage(client, msg);

                    msg = HelperMethods.receiveMessage(client);
                    System.out.println("Received: " + msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}