//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.AsynchronousServerSocketChannel;
//import java.nio.channels.AsynchronousSocketChannel;
//import java.nio.channels.CompletionHandler;
//import java.util.Scanner;
//import java.util.Vector;
//
//public class Buffer_Channel_Server {
//    public static Vector<ClientHandler> clients = new Vector<>();
//
//    public static void main(String[] args) {
//        try {
//            AsynchronousServerSocketChannel sschannel = AsynchronousServerSocketChannel.open();
//            sschannel.bind(new InetSocketAddress(5001));
//
//            System.out.println("Game server is ready.");
//            sschannel.accept(sschannel, new CompletionHandler<AsynchronousSocketChannel,AsynchronousServerSocketChannel >() {
//                @Override
//                public void completed(AsynchronousSocketChannel sockChannel, AsynchronousServerSocketChannel serverSock ) {
//                    //연결 성공 -> 다음 클라이언트 연결 대기, 클라이언트 확인은 주소로
//                    serverSock.accept( serverSock, this );
//                    System.out.printf("Client connected\n");
//                    receive(sockChannel);
//                    //연결 환영 메시지 전송
//                    HelperMethods.sendMessage(sockChannel, "Hello");
//                }
//
//                @Override
//                public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
//                    System.out.println( "Fail to connect");
//                }
//            } );
//
//            Scanner s = new Scanner(System.in);
//            String cmd = s.nextLine();
//            if(cmd.equals("exit")){
//
//            }
//            s.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void receive(AsynchronousSocketChannel sock){
//        ByteBuffer buffer = ByteBuffer.allocate(2048);
//        sock.read(buffer, sock, new CompletionHandler<Integer,AsynchronousSocketChannel>() {
//            @Override
//            public void completed(Integer result, AsynchronousSocketChannel attachment) {
//                //수신 완료 후 재수신 대기 버퍼 스트링으로 바꿈
//                // 스프링 (스플릿 함수) <- 찾아봄
//                receive(sock);
//
//                HelperMethods.sendMessage(sock, new String(buffer.array()));
//            }
//            @Override
//            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
//                // TODO Auto-generated method stub
//            }
//        });
//    }
//}