import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ClientHandler implements Runnable {
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    String name;
    int sum;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, String name, int sum) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.sum = sum;
    }

    private void informLeave(ClientHandler handler) throws IOException {
        for (ClientHandler mc : StreamServer.clients) {
            if (!mc.name.equals(handler.name)) {    // 로그아웃하는 클라이언트가 아니면
                mc.dos.writeUTF(handler.name + " is just leaved.");
            }
        }
    }

    @Override
    public void run() {
        int[] board = new int[500];
        board = board(board);
        int diceNum = 0;
        boolean rest = false;
        String msg = null;
        String data = null;

        while (true) {
            try {
                diceNum = dis.read();
                System.out.println(diceNum);
                if (diceNum == 0) {
                    System.out.println(this.name + " is just leaved.");
                    dos.writeUTF("Good Bye");
                    this.s.close(); // 접속을 종료하는 클라이언트의 서버 쪽 소켓을 닫음
                    informLeave(this);
                    break;
                }

                if (rest == true) {
                    msg = "다음 턴에 이동할 수 있습니다.";
                    rest = false;
                }
                else {
                    this.sum += diceNum;
                    if (this.sum > 500) {
                        for (ClientHandler handler : StreamServer.clients) {
                            if (handler.name.equals(this.name))
                                continue;
                            if (handler.sum == this.sum) {
                                handler.sum = 0;
                                dos.writeUTF("win");
                                handler.dos.writeUTF("lose");
                            }
                        }
                    }

                    catchs(name, sum, dos);

                    if (board[this.sum] == 1) {
                        this.sum += diceNum;
                        msg = diceNum + "만큼 점프해 현재 " + this.sum + "칸 입니다.";
                    }
                    else if (board[this.sum] == 2) {
                        this.sum -= diceNum;
                        msg = diceNum + "만큼 후퇴해 현재 " + this.sum + "칸 입니다.";
                    }
                    else if (board[this.sum] == 3) {
                        msg = "무인도에 걸려 한턴을 쉽니다.";
                        rest = true;
                    }
                    else {
                        msg = "현재 " + this.sum + "칸 입니다.";
                    }

                    catchs(name, sum, dos);
                }
                dos.writeUTF(msg);

                for (ClientHandler handler : StreamServer.clients) {
                    if (!handler.name.equals(this.name)) {
                        data = this.name + "는 " + msg;
                        handler.dos.writeUTF(data);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void catchs(String name, int sum, DataOutputStream dos) throws IOException {
        for (ClientHandler handler : StreamServer.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                dos.writeUTF("상대방의 말을 잡아 상대가 처음으로 돌아갑니다.");
                handler.dos.writeUTF(name + "에게 말이 잡혀 처음으로 돌아갑니다.");
            }
        }
    }

    public int[] board(int[] board) {
        for (int n : board)
            board[n] = 0;
        int i = 0;
        Random random = new Random();

        while (true) {
            i += random.nextInt(5, 15);
            if (i >= 500)
                break;
            board[i] = 1;   // jump
        }

        i = 0;
        while (true) {
            i += random.nextInt(5, 20);
            if (i >= 500)
                break;
            if (board[i] == 0)
                board[i] = 2;   // back
        }

        i = 0;
        while (true) {
            i += random.nextInt(20, 30);
            if (i >= 500)
                break;
            if (board[i] == 0)
                board[i] = 3;   // skip
        }
        return board;
    }
}
