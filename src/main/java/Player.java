/**
 * Player class containing all data
 * about the player
 */

public class Player {
    private ClientHandler clientHandler;
    private PlayerDeck deck;
    private String playerName;
    private int points;
    private int place;

    Player(ClientHandler clientHandler, String playerName) {
        this.clientHandler = clientHandler;
        deck = new PlayerDeck(0, this);
        this.playerName = playerName;
        this.points = 0;
        this.place = 0;
    }

    public void increasePoints(int value) {
        this.points += value;
    }
    public void decreasePoints(int value) {
        this.points -= value;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public PlayerDeck getDeck() {
        return deck;
    }

    public void clearDeck() {
        deck.getDeck().clear();
    }

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


    public int getPoints() {
        return points;
    }

    public void resetPoints() {
        this.points = 0;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}
