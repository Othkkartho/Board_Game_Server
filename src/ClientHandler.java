import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class ClientHandler {
    SocketChannel client;
    String name;
    int sum;
    String msg = null;

    public static int[] boardSetup() {
        int[] board = new int[200];
        board(board);
        return board;
    }

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

    public void player(SocketChannel channel, int num, int[] board) {
        boolean rest = false;

        System.out.println(num);
        try {
            if (num == 0) {
                System.out.println(name + " is just leaved.");
                HelperMethods.sendMessage(client, "Good Bye");
                client.close();
                informLeave(this);
            }

            if (rest == true && board[sum] == 3) {
                msg = "skip2";
                rest = false;
            } else if (rest == true) {
                rest = false;
            } else {
                sum += num;
                if (sum >= board.length) {
                    HelperMethods.sendMessage(channel, "win#" + board.length);
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
            } else if (board[sum] == 3) {
                msg = "skip#"+sum;
                rest = true;
            } else {
                msg = "go#"+sum+"#"+num;
            }

            if (catchs(msg))
                return;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        HelperMethods.sendMessage(client, msg);
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (!handler.name.equals(name)) {
                HelperMethods.sendMessage(handler.client, name + "#" + msg);
            }
        }
    }

    public boolean catchs() {
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods.sendMessage(client, "catch#"+sum);
                HelperMethods.sendMessage(handler.client, name+"#catch#"+sum);
                return true;
            }
        }
        return false;
    }

    public boolean catchs(String msg) {
        for (ClientHandler handler : Buffer_Channel_Server.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods.sendMessage(client, "catch#"+msg);
                HelperMethods.sendMessage(handler.client, name+"#catch"+msg);
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
            i += random.nextInt(5, 15);
            if (i >= board.length)
                break;
            board[i] = 1;   // jump
        }

        i = 0;
        while (true) {
            i += random.nextInt(5, 20);
            if (i >= board.length)
                break;
            if (board[i] == 0)
                board[i] = 2;   // back
        }

        i = 0;
        while (true) {
            i += random.nextInt(20, 30);
            if (i >= board.length)
                break;
            if (board[i] == 0)
                board[i] = 3;   // skip
        }
        return board;
    }
}