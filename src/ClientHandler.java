import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class ClientHandler implements Runnable {
    SocketChannel client;
    String name;
    int sum;
    String msg = null;

    public ClientHandler(SocketChannel client, String name, int sum) {
        this.client = client;
        this.name = name;
        this.sum = sum;
    }

    private void informLeave(ClientHandler handler) throws IOException {
        for (ClientHandler mc : Buffer_Channel_Server.clients) {
            if (!mc.name.equals(handler.name)) {
                msg = handler.name + " is just leaved.";
                HelperMethods.sendMessage(mc.client, msg);
            }
        }
    }

    @Override
    public void run() {
        int[] board = new int[500];
        board = board(board);
        int diceNum;
        boolean rest = false;

        while (true) {
            try {
                diceNum = HelperMethods.receiveIntMessage(this.client);
                System.out.println(diceNum);
                if (diceNum == 0) {
                    System.out.println(this.name + " is just leaved.");
                    HelperMethods.sendMessage(this.client, "Good Bye");
                    this.client.close();
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
                        for (ClientHandler handler : Buffer_Channel_Server.clients) {
                            if (handler.name.equals(this.name))
                                continue;
                            if (handler.sum == this.sum) {
                                handler.sum = 0;
                                HelperMethods.sendMessage(this.client, "win");
                                HelperMethods.sendMessage(handler.client, "lose");
                            }
                        }
                    }

                    catchs();

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

                    catchs();
                }
                HelperMethods.sendMessage(this.client, msg);

                for (ClientHandler handler : Buffer_Channel_Server.clients) {
                    if (!handler.name.equals(this.name)) {
                        msg = this.name + "는 " + msg;
                        HelperMethods.sendMessage(handler.client, msg);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void catchs() {
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods.sendMessage(this.client, "상대방의 말을 잡아 상대가 처음으로 돌아갑니다.");
                HelperMethods.sendMessage(handler.client, name + "에게 말이 잡혀 처음으로 돌아갑니다.");
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