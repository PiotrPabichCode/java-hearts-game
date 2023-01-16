import java.util.ArrayList;

/**
 * PlayerDeck class containing data on single player cards
 */

public class PlayerDeck {

    private int size;
    private ArrayList<Card> deck;
    private Player player;

    PlayerDeck(int size, Player player) {
        deck = new ArrayList<>();
        this.size = size;
        this.player = player;
    }

    public void displayDeck() {
        ClientHandler clientHandler = player.getClientHandler();
        for(int i = 0; i < deck.size(); i++) {
            String cardData = deck.get(i).getCardDetails();
            int position = i + 1;
            clientHandler.sendMessage(position + ". " + cardData);
        }
    }

    public void addCard(Card card) {
        deck.add(card);
        increaseSize();
    }

    public void deleteCard(Card card) {
        deck.remove(card);
        decreaseSize();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public void increaseSize() {
        this.size++;
    }

    public void decreaseSize() {
        if(this.size > 0) {
            this.size--;
        }
    }

    public int getSize() {
        return deck.size();
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }
}
