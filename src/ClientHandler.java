import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class ClientHandler {
    SocketChannel client;
    String name;
    int sum;
    String msg = null;
    int[] board = new int[200];

    public ClientHandler() {
        board = board(board);
    }

    public ClientHandler(SocketChannel client, String name, int sum) {
        this.client = client;
        this.name = name;
        this.sum = sum;
    }

    private void informLeave(ClientHandler handler) throws IOException {
        for (ClientHandler mc : Buffer_Channel_Server_Test.clients) {
            if (!mc.name.equals(handler.name)) {
                msg = handler.name + " is just leaved.";
                HelperMethods_Test.sendMessage(mc.client, msg);
            }
        }
    }

    public void player(SocketChannel channel, int num) {
        boolean rest = false;

        System.out.println(num);
        try {
            if (num == 0) {
                System.out.println(name + " is just leaved.");
                HelperMethods_Test.sendMessage(client, "Good Bye");
                client.close();
                informLeave(this);
            }

            if (rest == true) {
                msg = "You can move on your next turn.";
                rest = false;
            }
            else {
                sum += num;
                if (sum >= board.length) {
                    HelperMethods_Test.sendMessage(channel, "win");
                }
            }

            if (catchs())
                return;

            if (board[sum] == 1) {
                sum += num;
                msg = "Jump " + num + "!!! You are in the " + sum + " column.";
            }
            else if (board[sum] == 2) {
                sum -= num;
                msg = "Back " + num + "!!! You are in the " + sum + " column.";
            }
            else if (board[sum] == 3) {
                msg = "Your stranded on an uninhabited island.";
                rest = true;
            }
            else {
                msg = "You are in the " + sum + " column.";
            }

            if (catchs(msg))
                return;
        } catch (IOException ex) {
        throw new RuntimeException(ex);
        }

        HelperMethods_Test.sendMessage(client, msg);
        for (ClientHandler handler : Buffer_Channel_Server_Test.clients) {
            if (!handler.name.equals(name))
                HelperMethods_Test.sendMessage(handler.client, name + " >> " + msg);
        }
    }

    public boolean catchs() {
        for (ClientHandler handler : Buffer_Channel_Server_Test.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods_Test.sendMessage(client, "catch You are" + sum + " column.");
                HelperMethods_Test.sendMessage(handler.client, "Grab the opponent's piece and the opponent will return to the beginning.");
                return true;
            }
        }
        return false;
    }

    public boolean catchs(String msg) {
        for (ClientHandler handler : Buffer_Channel_Server_Test.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods_Test.sendMessage(client, msg + " and catch other player");
                HelperMethods_Test.sendMessage(handler.client, "Grab the opponent's piece and the opponent will return to the beginning.");
                return true;
            }
        }
        return false;
    }

    public int[] board(int[] board) {
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