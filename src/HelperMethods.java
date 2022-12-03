import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class HelperMethods {
    static StringBuilder message = new StringBuilder();

    public static void sendMessage(SocketChannel socketChannel, String message) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(message.length() + 1);
            buffer.put(message.getBytes());
            buffer.put((byte) 0x00);
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
            System.out.println("Sent: " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int receiveIntMessage(SocketChannel socketChannel) {
        try {
            receiveMessage(socketChannel);
            return Integer.parseInt(String.valueOf(message));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static String receiveStringMessage(SocketChannel socketChannel) {
        try {
            receiveMessage(socketChannel);
            return message.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static void receiveMessage(SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        while (socketChannel.read(byteBuffer) > 0) {
            char byteRead = 0x00;
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                byteRead = (char) byteBuffer.get();
                if (byteRead == 0x00) {
                    break;
                }
                message.append(byteRead);
            }
            if (byteRead == 0x00) {
                break;
            }
            byteBuffer.clear();
        }
    }

}