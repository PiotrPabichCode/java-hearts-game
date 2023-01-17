import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Deck class containing data about
 * the current deck of cards
 */

public class Deck {
    /**
     * Constant that covers maximum size of the deck
     */
    static final int DECK_SIZE = 48;
    /**
     * Variable that stores all card names
     */
    private List<String> cardNames = new ArrayList<>();
    /**
     * Variable that stores all card types
     */
    private List<String> cardTypes = new ArrayList<>();
    /**
     * Variable that stores all cards in deck
     */
    private ArrayList<Card> deck = new ArrayList<>();
    /**
     * Variable that stores current round card type
     */
    static String currentCardType;
    /**
     * Variable that stores current round losing points
     */
    private int losePoints;

    /**
     * Deck constructor
     */
    Deck() {
        createDeck();
    }

    /**
     * Method that creates card names
     */

    void createCardNames() {
        for(int i = 3; i <= 10; i++) {
            cardNames.add(Integer.toString(i));
        }
        cardNames.add("J");
        cardNames.add("Q");
        cardNames.add("K");
        cardNames.add("A");
    }

    /**
     * Method that creates card types
     */
    void createCardTypes() {
        cardTypes.add("BLACK");
        cardTypes.add("RED");
        cardTypes.add("BLUE");
        cardTypes.add("GREEN");
    }

    /**
     * Method that sets current round card type
     */

    void setCurrentCardType() {
        Random random = new Random();
        int choice = random.nextInt(cardTypes.size());
        currentCardType = cardTypes.get(choice);
        int lowerBound = 20;
        int upperBound = 100;
        losePoints = random.nextInt(lowerBound,upperBound);
    }

    /**
     * Method that creates deck
     */
    void createDeck() {
        createCardNames();
        createCardTypes();
        for(int i = 0; i < DECK_SIZE / 4; i++) {
            deck.add(new Card(cardTypes.get(0), i + 3, cardNames.get(i)));
            deck.add(new Card(cardTypes.get(1), i + 3, cardNames.get(i)));
            deck.add(new Card(cardTypes.get(2), i + 3, cardNames.get(i)));
            deck.add(new Card(cardTypes.get(3), i + 3, cardNames.get(i)));
        }
        setCurrentCardType();
    }

    /**
     * Getter that gets lose points
     */
    public int getLosePoints() {
        return losePoints;
    }

    /**
     *  Getter that gets deck
     */
    public ArrayList<Card> getDeck() {
        return deck;
    }

    /**
     *  Getter that gets cardNames
     */

    public List<String> getCardNames() {
        return cardNames;
    }

    /**
     *  Getter that gets cardTypes
     */

    public List<String> getCardTypes() {
        return cardTypes;
    }
}
