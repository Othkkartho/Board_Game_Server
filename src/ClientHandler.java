import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ClientHandler {
    SocketChannel client;
    String name;
    int sum;
    String msg = null;
    int rest = 0;

    public static int[] boardSetup() {
        int[] board = new int[25];
        board(board);
        return board;
    }

    public ClientHandler(SocketChannel client, String name, int sum) {
        this.client = client;
        this.name = name;
        this.sum = sum;
    }

    private void informLeave(ClientHandler handler) throws IOException, InterruptedException {
        for (ClientHandler mc : Buffer_Channel_Server.clients) {
            if (!mc.name.equals(handler.name)) {
                msg = handler.name + "님이 게임에서 떠났습니다";
                HelperMethods.sendMessage(mc.client, msg);
            }
        }
    }

    public void player(SocketChannel channel, int num, int[] board) throws InterruptedException {

        System.out.println(num);
        try {
            if (num == 0) {
                System.out.println(name + "님이 게임에서 떠났습니다");
                HelperMethods.sendMessage(client, "안녕히 가세요");
                client.close();
                informLeave(this);
            }

            if (this.rest == 1 && board[sum] == 3) {
                msg = "다음턴에 이동할 수 있습니다.";
                this.rest = 2;
            } else if (this.rest == 1) {
                this.rest = 0;
            } else {
                sum += num;
                if (sum >= board.length) {
                    HelperMethods.sendMessage(channel, "win");
                    channel.close();
                    Buffer_Channel_Server.clients.removeIf(handler -> handler.name.equals(name));
                    for (ClientHandler handler : Buffer_Channel_Server.clients) {
                        if (!handler.name.equals(name)) {
                            HelperMethods.sendMessage(handler.client, "lose");
                            handler.client.close();
                            Buffer_Channel_Server.clients.remove(handler);
                        }
                    }
                    return;
                }
            }

            if (catchs())
                return;

            if (board[sum] == 1) {
                sum += num;
                msg = num+"칸을 점프해 "+sum+"칸에 도착했습니다";
            } else if (board[sum] == 2) {
                sum -= num;
                msg = num+"칸 뒤로 가 "+sum+"칸에 도착했습니다";
            } else if (board[sum] == 3 && this.rest == 0) {
                msg = sum+"칸에 도착했지만 무인도라 한 턴 쉽니다";
                this.rest = 1;
            } else if (this.rest == 2) {
                this.rest += 1;
            } else if (this.rest == 3) {
                this.rest = 0;
                msg = num+"칸을 이동해 "+sum+"칸에 도착했습니다";
            } else {
                msg = num+"칸을 이동해 "+sum+"칸에 도착했습니다";
            }

            if (catchs(msg))
                return;
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        HelperMethods.sendMessage(client, msg);
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (!handler.name.equals(name)) {
                HelperMethods.sendMessage(handler.client, name + "이/가 " + msg);
            }
        }
    }

    public boolean catchs() throws InterruptedException {
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods.sendMessage(client, sum+"칸에서 상대방 말을 잡아 상대 말이 처음으로 돌아갑니다");
                HelperMethods.sendMessage(handler.client, name+"가 "+sum+"에서 당신의 말을 잡아 말이 처음으로 돌아갑니다");
                return true;
            }
        }
        return false;
    }

    public boolean catchs(String msg) throws InterruptedException {
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods.sendMessage(client, msg+"\n"+sum+"칸에서 상대방 말을 잡아 상대 말이 처음으로 돌아갑니다");
                HelperMethods.sendMessage(handler.client, name+"가 "+sum+"에서 당신의 말을 잡아 말이 처음으로 돌아갑니다");
                return true;
            }
        }
        return false;
    }

    public static int[] board(int[] board) {
        for (int n : board)
            board[n] = 0;
        int i = 0;
        Random random = new Random();

        while (true) {
//            i += random.nextInt(5, 15);
            i += 3;
            if (i >= board.length)
                break;
            board[i] = 1;   // jump
        }

        i = 0;
        while (true) {
//            i += random.nextInt(5, 20);
            i += 4;
            if (i >= board.length)
                break;
            if (board[i] == 0)
                board[i] = 2;   // back
        }

        i = 0;
        while (true) {
//            i += random.nextInt(20, 30);
            i += 5;
            if (i >= board.length)
                break;
            if (board[i] == 0)
                board[i] = 3;   // skip
        }
        return board;
    }
}