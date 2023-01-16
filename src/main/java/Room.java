import java.io.IOException;

/**
 * Class containing data about the room
 */
public class Room {
    private int number;
    private int size;
    public Game game;

    public Room(int number, int size, Game game) {
        this.number = number;
        this.size = size;
        this.game = game;
    }

    public void startRoom() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                game.gameLoop();
            }
        }).start();
    }

    public void addPlayer(ClientHandler player) {
        game.addPlayer(player);
    }

    public void deletePlayer(ClientHandler player) {
        game.deletePlayer(player);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void increaseSize() {
        this.size++;
    }

    public void decreaseSize() {
        this.size--;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

}
