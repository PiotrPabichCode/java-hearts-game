import java.io.IOException;

/**
 * Class containing data about the room
 */
public class Room {
    /**
     * Variable that stores room number
     */
    private int number;
    /**
     * Variable that stores room size
     */
    private int size;
    /**
     * Variable that stores Game Class
     */
    public Game game;

    /**
     * Room constructor
     */
    public Room(int number, int size, Game game) {
        this.number = number;
        this.size = size;
        this.game = game;
    }

    /**
     * Thread that handles room connection
     */

    public void startRoom() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    game.gameLoop();
                }
            }
        }).start();
    }
    /**
     * Method that adds player by its ClientHandler
     */
    public void addPlayer(ClientHandler player) {
        game.addPlayer(player);
    }
    /**
     * Method that deletes player by its ClientHandler
     */
    public void deletePlayer(ClientHandler player) {
        game.deletePlayer(player);
    }
    /**
     * Setter that sets number
     */
    public void setNumber(int number) {
        this.number = number;
    }
    /**
     * Setter that sets size
     */
    public void setSize(int size) {
        this.size = size;
    }
    /**
     * Method that increases size
     */
    public void increaseSize() {
        this.size++;
    }
    /**
     * Method that decreases size
     */
    public void decreaseSize() {
        this.size--;
    }
    /**
     * Getter that gets roomNumber
     */
    public int getNumber() {
        return number;
    }
    /**
     * Getter that gets size
     */
    public int getSize() {
        return size;
    }

}
