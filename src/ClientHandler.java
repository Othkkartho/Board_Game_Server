import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class ClientHandler {
    SocketChannel client;
    String name;
    int sum;
    String msg = null;
    int[] board = new int[200];

    public ClientHandler(SocketChannel client, String name, int sum) {
        this.client = client;
        this.name = name;
        this.sum = sum;
    }

    public void setup() {
        board = board(board);
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
        int diceNum = num;
        boolean rest = false;

        System.out.println(diceNum);
        try {
            if (diceNum == 0) {
                System.out.println(name + " is just leaved.");
                HelperMethods_Test.sendMessage(client, "Good Bye");
                client.close();
                informLeave(this);
            }

            if (rest == true) {
                msg = "다음 턴에 이동할 수 있습니다.";
                rest = false;
            }
            else {
                this.sum += diceNum;
                if (this.sum >= board.length) {
                    HelperMethods_Test.sendMessage(channel, "win");
                }
            }

            catchs();

            if (board[this.sum] == 1) {
                this.sum += diceNum;
                msg = "Jump " + diceNum + "!!! You are in the " + this.sum + " column.";
            }
            else if (board[this.sum] == 2) {
                this.sum -= diceNum;
                msg = "Back " + diceNum + "!!! You are in the " + this.sum + " column.";
            }
            else if (board[this.sum] == 3) {
                msg = "Your stranded on an uninhabited island.";
                rest = true;
            }
            else {
                msg = "You are in the " + this.sum + " column.";
            }

            catchs();
        } catch (IOException ex) {
        throw new RuntimeException(ex);
        }

        HelperMethods_Test.sendMessage(client, msg);
    }

    public void other_player(SocketChannel client, int num, String name) {
        int diceNum = num;
        boolean rest = false;

        System.out.println(diceNum);

        try {
            if (diceNum == 0) {
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
                if (this.sum >= board.length) {
                    HelperMethods_Test.sendMessage(client, "lose");
                }
            }

            catchs();

            if (board[sum] == 1) {
                this.sum += diceNum;
                msg = "Player has Jump " + diceNum + "!!! You are in the " + sum + " column.";
            }
            else if (board[this.sum] == 2) {
                this.sum -= diceNum;
                msg = "Player has Back " + diceNum + "!!! You are in the " + this.sum + " column.";
            }
            else if (board[this.sum] == 3) {
                msg = "Player is stranded on an uninhabited island.";
                rest = true;
            }
            else {
                msg = "Player is in the " + this.sum + " column.";
            }

            msg = name + " " + msg;

            catchs();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        HelperMethods_Test.sendMessage(client, msg);
    }

    private void catchs() {
        for (ClientHandler handler : Buffer_Channel_Server_Test.clients) {
            if (handler.name.equals(name))
                continue;
            if (sum > 0 && handler.sum > 0 && handler.sum == sum) {
                handler.sum = 0;
                HelperMethods_Test.sendMessage(client, "Grab the opponent's piece and the opponent will return to the beginning.");
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