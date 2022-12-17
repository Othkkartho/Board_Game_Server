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
                msg = handler.name + " is just leaved.";
                HelperMethods.sendMessage(mc.client, msg);
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    public void player(SocketChannel channel, int num, int[] board) throws InterruptedException {

        System.out.println(num);
        try {
            if (num == 0) {
                System.out.println(name + " is just leaved.");
                HelperMethods.sendMessage(client, "Good Bye");
                client.close();
                informLeave(this);
            }

            if (this.rest == 1 && board[sum] == 3) {
                msg = "skip2#0";
                this.rest = 2;
            } else if (this.rest == 1) {
                this.rest = 0;
            } else {
                sum += num;
                if (sum >= board.length) {
                    HelperMethods.sendMessage(channel, "win#" + board.length);
                    channel.close();
                    for (ClientHandler handler : Buffer_Channel_Server.clients) {
                        if (!handler.name.equals(name)) {
                            HelperMethods.sendMessage(handler.client, "lose#"+handler.sum);
                            handler.client.close();
                            TimeUnit.SECONDS.sleep(1);
                        }
                    }
                }
            }

            if (catchs())
                return;

            if (board[sum] == 1) {
                sum += num;
                msg = "jump#"+sum+"#"+num;
            } else if (board[sum] == 2) {
                sum -= num;
                msg = "back#"+sum+"#"+num;
            } else if (board[sum] == 3 && this.rest == 0) {
                msg = "skip#"+sum;
                this.rest = 1;
            } else if (this.rest == 2) {
                this.rest += 1;
            } else if (this.rest == 3) {
                this.rest = 0;
                msg = "go#"+sum+"#"+num;
            } else {
                msg = "go#"+sum+"#"+num;
            }

            if (catchs(msg))
                return;
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        HelperMethods.sendMessage(client, msg);
        TimeUnit.SECONDS.sleep(1);
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (!handler.name.equals(name)) {
                HelperMethods.sendMessage(handler.client, name + "#" + msg);
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    public boolean catchs() throws InterruptedException {
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods.sendMessage(client, "catch#"+sum);
                TimeUnit.SECONDS.sleep(1);
                HelperMethods.sendMessage(handler.client, name+"#catch#"+sum);
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
                HelperMethods.sendMessage(client, "catch#"+msg);
                TimeUnit.SECONDS.sleep(1);
                HelperMethods.sendMessage(handler.client, name+"#catch");
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