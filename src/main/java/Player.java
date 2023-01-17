/**
 * Player class containing all data
 * about the player
 */

public class Player {
    /**
     * Variable that stores ClientHandler
     */
    private ClientHandler clientHandler;
    /**
     * Variable that stores PlayerDeck
     */
    private PlayerDeck deck;
    /**
     * Variable that stores playerName
     */
    private String playerName;
    /**
     * Variable that stores points
     */
    private int points;
    /**
     * Variable that stores place
     */
    private int place;

    /**
     * Player constructor
     */
    Player(ClientHandler clientHandler, String playerName) {
        this.clientHandler = clientHandler;
        deck = new PlayerDeck(0, this);
        this.playerName = playerName;
        this.points = 0;
        this.place = 0;
    }

    /**
     * Method that increases points by value
     */

    public void increasePoints(int value) {
        this.points += value;
    }
    /**
     * Method that decreases points by value
     */
    public void decreasePoints(int value) {
        this.points -= value;
    }

    /**
     * Getter that gets playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Getter that gets clientHandler
     */
    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    /**
     * Getter that gets deck
     */
    public PlayerDeck getDeck() {
        return deck;
    }

    /**
     * Method that clears deck
     */
    public void clearDeck() {
        deck.getDeck().clear();
    }

    /**
     * Methods that gets deck that contains available cards for current round
     */
    public PlayerDeck getCorrectDeck() {
        PlayerDeck correctDeck = new PlayerDeck(0, this);
        for(int i = 0; i < deck.getDeck().size(); i++) {
            Card card = deck.getDeck().get(i);
            if(card.getColor().equals(Deck.currentCardType)) {
                correctDeck.addCard(card);
            }
        }
        if(correctDeck.getDeck().size() == 0) {
            return deck;
        }
        return correctDeck;
    }
    /**
     * Getter that gets points
     */
    public int getPoints() {
        return points;
    }
    /**
     * Method that resets points
     */
    public void resetPoints() {
        this.points = 0;
    }
    /**
     * Getter that gets place
     */
    public int getPlace() {
        return place;
    }
    /**
     * Setter that sets place
     */
    public void setPlace(int place) {
        this.place = place;
    }
}
