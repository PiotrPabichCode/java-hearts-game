import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Game class containing all the important methods
 * and functionalities that allow the Hearts game to work properly
 */
public class Game {
    /**
     * Variable that stores room number
     */
    private final int roomNumber;
    /**
     * Variable that stores deck
     */
    private Deck deck;
    /**
     * Variable that stores ArrayList of players in game
     */
    private final ArrayList<Player> players = new ArrayList<>();
    /**
     * Flag that checks if round is active
     */
    boolean isActive = true;
    /**
     * Flag that checks if reset is available
     */
    boolean resetFlag = true;
    /**
     * Flag that checks if server made actions
     */
    boolean serverAction = false;
    /**
     * Variable that stores current round number
     */
    private int round;
    /**
     * Constant that stores maximum number of players
     */
    final int MAX_PLAYERS = 3;

    /**
     * Constant that stores maximum number of rounds
     */
    final int TOTAL_ROUNDS = 7;
    /**
     * Constant that stores maximum number of cards in player deck
     */
    final int PLAYER_DECK_SIZE;

    /**
     * Game constructor
     */
    Game(int roomNumber){
        this.roomNumber = roomNumber;
        deck = new Deck();
        PLAYER_DECK_SIZE = Deck.DECK_SIZE / MAX_PLAYERS;
        this.round = 1;
    }

    /**
     * Main game loop where all functionalites are used
     */

    void gameLoop() {
        round = 1;
        resetFlag = true;
        isActive = true;
        while(players.size() != MAX_PLAYERS) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(resetFlag) {
            if(round == 1) {
                startGame();
            }

            normalRound();
            if(round > TOTAL_ROUNDS) {
                endGame();
            }
            isActive = true;
        }
        deletePlayers();
    }

    /**
     * Method, which contains all the methods used in the case of a normal round
     */

    void normalRound() {
        broadcastMessage("ROUND NUMBER " + round);
        preparePlayerDecks();
        while(isActive) {
            String message = "[CARD TYPE / VALUE] = [" + Deck.currentCardType + " / " + deck.getLosePoints() + "]";
            broadcastMessage(message);
            displayPlayerDecks();
            ArrayList<PickedCard> pickedCards = pickCards();
            displayPickedCards(pickedCards);
            calculatePoints(pickedCards);
            checkEnd();
        }
        endRound(false, false);
    }

    /**
     * Method that removes all current players from the game
     */
    void deletePlayers() {
        for(int i = 0; i < players.size(); i++) {
            ClientHandler clientHandler = players.get(i).getClientHandler();
            clientHandler.getRoom(roomNumber).decreaseSize();
            clientHandler.setRoomNumber(clientHandler.NO_ROOM);
        }
        players.clear();
    }

    /**
     * Method that checks
     *        if the game has reached the end
     */
    boolean checkEnd() {
        if(deck.getDeck().size() == 0) {
            isActive = false;
            return true;
        }
        return false;
    }

    /**
     * Method that distributes random numbers among players
     */

    void giveRandomPoints() {
        int cardsCounter = players.get(0).getDeck().getSize();
        Random random = new Random();
        for(int i = 0; i < cardsCounter; i++) {
            for(int j = 0; j < players.size(); j++) {
                int value = random.nextInt(9);
                Player player = players.get(j);
                Card card = player.getDeck().getDeck().get(i);
                player.increasePoints(value * card.getValue());
            }
        }
    }

    /**
     * Method that clears a player's current deck of cards
     */

    void clearPlayerDecks() {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).clearDeck();
        }
    }

    /**
     * Method that unlocks BufferedReader players
     */

    void unblockReaders() {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).getClientHandler().unblockReader();
        }
    }

    /**
     * Method that executes the methods used in the case of the end of the last round of the game
     */

    void endLastRound(boolean isServer) {
        isActive = false;
        if(isServer) {
            serverAction = true;
            unblockReaders();
        } else {
            serverAction = false;
        }
    }

    /**
     * Method that executes the methods used in the case of the end of the normal round of the game
     */

    void endNormalRound(boolean isServer, boolean isRandom) {
        if(isServer) {
            serverAction = true;
        }
        broadcastMessage("---------------------------------------\n" +
                "ROUND NUMBER " + round + " ENDED");
        giveRandomPoints();
        clearPlayerDecks();
        deck.setCurrentCardType();
        isActive = false;
        round++;
        displayPoints(true, true);
        if(!isRandom) {
            unblockReaders();
        }
    }

    /**
     * Method that executes the methods used in the case of the end of the round of the game
     */
    void endRound(boolean isServer, boolean isRandom) {
        if(round > TOTAL_ROUNDS) {
            endLastRound(isServer);
            return;
        }
        if(serverAction) {
            serverAction = false;
            return;
        }
        endNormalRound(isServer, isRandom);
    }

    /**
     * Method that ends the game by drawing random values between players
     */

    void randomEndGame() {
        while(round < TOTAL_ROUNDS + 1) {
            endRound(false, true);
            preparePlayerDecks();
        }
        unblockReaders();
    }

    /**
     * Method that ends the game
     */

    void endGame() {
        broadcastMessage("-----------------------------------------\n" +
                         "Game ended\n");
        displayPoints(false, true);
        resetGame();
    }

    /**
     * Method that display players points
     */

    void displayPoints(boolean isServer, boolean isPlayers) {
        String roundMessage = (round == 8) ? "END GAME" : "Round number " + round;
        System.out.println("NEW MESSAGE: [Room " + roomNumber + "]: " + roundMessage);
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String message = player.getPlayerName() + ": " + player.getPoints();
            System.out.println("NEW MESSAGE: [Room " + roomNumber + "]: " + message);
            if(isPlayers) {
                player.getClientHandler().sendMessage(message);
            }
        }
        if(!isServer) {
            ClientHandler.refreshServerScreen();
        }
    }

    /**
     * Method that calculate players points after turn
     */

    void calculatePoints(ArrayList<PickedCard> cards) {
        if(isActive && cards != null) {
            Player player = cards.get(0).getPlayer();
            int min = cards.get(0).getCard().getValue();
            for(int i = 0; i < cards.size(); i++) {
                PickedCard pickedCard = cards.get(i);
                int value = pickedCard.getCard().getValue();
                if(value > min) {
                    min = value;
                    player = pickedCard.getPlayer();
                }
            }
            player.decreasePoints(deck.getLosePoints());
            player.getClientHandler().sendMessage("You lost this turn: -" + deck.getLosePoints());
        }
    }

    /**
     * Method that pick card from player every turn
     */

    PickedCard pickCard(Player player) {
        if(isActive) {
            ClientHandler clientHandler = player.getClientHandler();
            clientHandler.sendMessage("Pick one card from list: ");
            PlayerDeck playerDeck = player.getCorrectDeck();
            playerDeck.displayDeck();
            int lowerBound = 1;
            int upperBound = playerDeck.getSize();
            int index = clientHandler.getInteger(lowerBound, upperBound);
            if(index == clientHandler.EXIT_VALUE) {
                return null;
            }
            Card card = playerDeck.getDeck().get(index - 1);
            System.out.println("NEW MESSAGE: [Room " + roomNumber + "]: " + player.getPlayerName() + " picked card");
            ClientHandler.refreshServerScreen();
            player.getDeck().deleteCard(card);
            return new PickedCard(player, card);
        }
        return null;
    }

    /**
     * Method that display picked cards by players
     */

    void displayPickedCards(ArrayList<PickedCard> cards) {
        if(isActive && cards != null) {
            broadcastMessage("Picked cards: ");
            for(int i = 0; i < cards.size(); i++) {
                String message = cards.get(i).getCard().getCardDetails();
                broadcastMessage(message);
            }
        }
    }

    /**
     * Method that pick cards from all players
     */

    ArrayList<PickedCard> pickCards() {
        if(isActive) {
            ArrayList<PickedCard> cards = new ArrayList<>();
            for(int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                for(int j = 0; j < players.size(); j++) {
                    if(i != j) {
                        players.get(j).getClientHandler().sendMessage(player.getPlayerName() + " is picking card...");
                    } else {
                        player.getClientHandler().sendMessage("Now it's your turn");
                    }
                }
                PickedCard card = pickCard(player);
                if(card == null) {
                    return null;
                }
                cards.add(card);
                displayPickedCards(cards);
            }
            return cards;
        }
        return null;
    }

    /**
     * Method that broadcast message between all players
     */

    void broadcastMessage(String message) {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).getClientHandler().sendMessage(message);
        }
    }

    /**
     * Method that shuffles the cards and distributes them among the players
     */

    void shuffleCards() {
        Collections.shuffle(deck.getDeck());
        for(int i = 0; i < players.size(); i++) {
            ArrayList<Card> cards = new ArrayList<>();
            for(int j = PLAYER_DECK_SIZE * i; j < PLAYER_DECK_SIZE * (i + 1); j++) {
                cards.add(deck.getDeck().get(j));
            }
            Player player = players.get(i);
            player.getDeck().setDeck(cards);
            player.getDeck().setSize(PLAYER_DECK_SIZE);
        }
    }

    /**
     * Method that prepares players decks
     */

    void preparePlayerDecks() {
        deck.createDeck();
        shuffleCards();
        deck.setCurrentCardType();
    }

    /**
     * Method that display players decks
     */

    void displayPlayerDecks() {
        if(isActive) {
            for(int i = 0; i < MAX_PLAYERS; i++) {
                Player player = players.get(i);
                player.getClientHandler().sendMessage("Your cards:");
                player.getDeck().displayDeck();
            }
        }
    }

    /**
     * Getter that get player by his client handler
     */

    Player getPlayer(ClientHandler clientHandler) {
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if(player.getClientHandler() == clientHandler) {
                return player;
            }
        }
        return null;
    }

    /**
     * Setter that add player by his client handler
     */

    void addPlayer(ClientHandler clientHandler) {
        Player player = new Player(clientHandler, clientHandler.getUserName());
        players.add(player);
    }

    /**
     * Setter that delete player by his client handler
     */

    void deletePlayer(ClientHandler clientHandler) {
        Player player = getPlayer(clientHandler);
        players.remove(player);
    }

    /**
     * Method that starts game
     */

    void startGame() {
        String message = "Room nr." + roomNumber + " game is starting...";
        System.out.println("NEW MESSAGE: " + message);
        broadcastMessage("---------------------------------------\n" + message);
        ClientHandler.refreshServerScreen();
    }

    /**
     * Method that display players
     */

    void displayPlayers() {
        System.out.println("NEW MESSAGE: [Room " + roomNumber + "]: " + "Currently playing");
        broadcastMessage("Currently playing");

        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int position = i + 1;
            String message = position + ". " + player.getPlayerName();
            System.out.println(message);
            broadcastMessage(message);
        }
        ClientHandler.refreshServerScreen();
    }

    /**
     * Getter that gets roomNumber
     */

    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * Getter that gets round
     */

    public int getRound() {
        return round;
    }

    /**
     * Getter that gets players
     */

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Method that resets game
     */

    void resetGame() {
        broadcastMessage("Would you like to play again?\n" +
                         "1. Yes\n" +
                         "2. No");
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int lowerBound = 1;
            int upperBound = 2;
            int option = player.getClientHandler().getInteger(lowerBound, upperBound);
            if(option == 2) {
                resetFlag = false;
            }
        }
        if(resetFlag) {
            round = 1;
            isActive = true;
            for(int i = 0; i < players.size(); i++) {
                players.get(i).resetPoints();
            }
            broadcastMessage("GAME STARTS AGAIN!!!");
        } else {
            broadcastMessage("END GAME");
        }
    }
}
