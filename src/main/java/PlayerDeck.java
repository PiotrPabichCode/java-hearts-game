import java.util.ArrayList;

/**
 * PlayerDeck class containing data on single player cards
 */

public class PlayerDeck {
    /**
     * Variable that stores deck size
     */
    private int size;
    /**
     * Variable that stores all cards in deck
     */
    private ArrayList<Card> deck;
    /**
     * Variable that stores player
     */
    private Player player;

    /**
     * PlayerDeck constructor
     */
    PlayerDeck(int size, Player player) {
        deck = new ArrayList<>();
        this.size = size;
        this.player = player;
    }

    /**
     * Method that displays deck
     */
    public void displayDeck() {
        ClientHandler clientHandler = player.getClientHandler();
        for(int i = 0; i < deck.size(); i++) {
            String cardData = deck.get(i).getCardDetails();
            int position = i + 1;
            clientHandler.sendMessage(position + ". " + cardData);
        }
    }

    /**
     * Method that adds card and increases size
     */
    public void addCard(Card card) {
        deck.add(card);
        increaseSize();
    }
    /**
     * Method that deletes card from deck and decreases size
     */
    public void deleteCard(Card card) {
        deck.remove(card);
        decreaseSize();
    }
    /**
     * Setter that sets size
     */
    public void setSize(int size) {
        this.size = size;
    }
    /**
     * Setter that sets deck
     */
    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }
    /**
     * Method that increase size
     */
    public void increaseSize() {
        this.size++;
    }
    /**
     * Method that decreases size
     */
    public void decreaseSize() {
        if(this.size > 0) {
            this.size--;
        }
    }
    /**
     * Getter that gets size
     */
    public int getSize() {
        return deck.size();
    }
    /**
     * Getter that gets deck
     */
    public ArrayList<Card> getDeck() {
        return deck;
    }
    /**
     * Getter that gets player
     */
    public Player getPlayer() {
        return player;
    }
}
