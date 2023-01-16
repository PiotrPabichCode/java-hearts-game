import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Deck class containing data about
 * the current deck of cards
 */

public class Deck {
    static final int DECK_SIZE = 48;
    private List<String> cardNames = new ArrayList<>();
    private List<String> cardTypes = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    static String currentCardType;
    private int losePoints;

    Deck() {
        createDeck();
    }

    void createCardNames() {
        for(int i = 3; i <= 10; i++) {
            cardNames.add(Integer.toString(i));
        }
        cardNames.add("J");
        cardNames.add("Q");
        cardNames.add("K");
        cardNames.add("A");
    }

    void createCardTypes() {
        cardTypes.add("BLACK");
        cardTypes.add("RED");
        cardTypes.add("BLUE");
        cardTypes.add("GREEN");
    }

    void setCurrentCardType() {
        Random random = new Random();
        int choice = random.nextInt(cardTypes.size());
        currentCardType = cardTypes.get(choice);
        losePoints = random.nextInt(20,100);
    }
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

    public int getLosePoints() {
        return losePoints;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public List<String> getCardNames() {
        return cardNames;
    }

    public List<String> getCardTypes() {
        return cardTypes;
    }
}
