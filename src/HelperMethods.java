//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.SocketChannel;
//import java.nio.channels.AsynchronousSocketChannel;
//
//public class HelperMethods {
//    public static void sendMessage(AsynchronousSocketChannel socketChannel, String message) {
//        try {
//            ByteBuffer buffer = ByteBuffer.allocate(message.length() + 1);
//            buffer.put(message.getBytes());
//            buffer.put((byte) 0x00);
//            buffer.flip();
//            socketChannel.write(buffer);
//
//            System.out.println("Sent: " + message);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private static void receiveMessage(SocketChannel socketChannel) throws IOException {
//        StringBuilder message = new StringBuilder();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
//        while (socketChannel.read(byteBuffer) > 0) {
//            char byteRead = 0x00;
//            byteBuffer.flip();
//            while (byteBuffer.hasRemaining()) {
//                byteRead = (char) byteBuffer.get();
//                if (byteRead == 0x00) {
//                    break;
//                }
//                message.append(byteRead);
//            }
//            if (byteRead == 0x00) {
//                break;
//            }
//            byteBuffer.clear();
//        }
//    }
//}