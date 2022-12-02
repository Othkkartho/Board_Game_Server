import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class StreamServer {
    public static Vector<ClientHandler> clients = new Vector<>();
    private static void informNew(String name) throws IOException {
        for (ClientHandler handler : clients)
            handler.dos.writeUTF(name + " is just logged in");
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(3005);

            while (true) {
                System.out.println("Server is waiting");
                Socket socket = server.accept();
                System.out.println("client is connected: " + socket);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                String name = dis.readUTF();
                System.out.println(name + ": Welcome to the server.");
                informNew(name);

                ClientHandler handler = new ClientHandler(socket, dis, dos, name, 0);
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