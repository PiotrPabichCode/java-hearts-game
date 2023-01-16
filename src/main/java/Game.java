import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Game class containing all the important methods
 * and functionalities that allow the Hearts game to work properly
 */
public class Game {
    private final int roomNumber;
    private Deck deck;
    private final ArrayList<Player> players = new ArrayList<>();
    boolean isActive = true;
    boolean resetFlag = true;
    boolean serverAction = false;
    private int round;
    final int MAX_PLAYERS = 3;
    final int TOTAL_ROUNDS = 7;
    final int PLAYER_DECK_SIZE;

    Game(int roomNumber){
        this.roomNumber = roomNumber;
        deck = new Deck();
        PLAYER_DECK_SIZE = Deck.DECK_SIZE / MAX_PLAYERS;
        this.round = 1;
    }

    void gameLoop() {
        while(isActive && players.size() != MAX_PLAYERS) {
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
            if(round > TOTAL_ROUNDS) {
                endGame();
                broadcastMessage("END GAME");
            }
            isActive = true;
        }
        broadcastMessage("OUTSIDE LOOP");
    }

    boolean checkEnd() {
        if(deck.getDeck().size() == 0) {
            isActive = false;
            return true;
        }
        return false;
    }

    void giveRandomPoints() {
        int cardsCounter = players.get(0).getDeck().getSize();
        System.out.println("SIZE: " + cardsCounter);
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

    void clearPlayerDecks() {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).clearDeck();
        }
    }

    void unblockReaders() {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).getClientHandler().unblockReader();
        }
    }
    void endRound(boolean isServer, boolean isRandom) {
        if(round > TOTAL_ROUNDS) {
            isActive = false;
            if(isServer) {
                serverAction = true;
                unblockReaders();
            } else {
                serverAction = false;
            }
            return;
        }
        if(serverAction) {
            serverAction = false;
            return;
        }
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
        displayPoints(true);
        if(!isRandom) {
            unblockReaders();
        }
    }

    void randomEndGame() {
        while(round < TOTAL_ROUNDS + 1) {
            endRound(false, true);
            preparePlayerDecks();
        }
        unblockReaders();
    }

    void endGame() {
        broadcastMessage("-----------------------------------------\n" +
                         "Game ended\n");
        displayPoints(true);
//        unblockReaders();
        resetGame();
    }

    void displayPoints(boolean isPlayers) {
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
        ClientHandler.refreshServerScreen();
    }

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

    void displayPickedCards(ArrayList<PickedCard> cards) {
        if(isActive && cards != null) {
            broadcastMessage("Picked cards: ");
            for(int i = 0; i < cards.size(); i++) {
                String message = cards.get(i).getCard().getCardDetails();
                broadcastMessage(message);
            }
        }
    }

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

    void broadcastMessage(String message) {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).getClientHandler().sendMessage(message);
        }
    }

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

    void preparePlayerDecks() {
        deck.createDeck();
        shuffleCards();
        deck.setCurrentCardType();
    }

    void displayPlayerDecks() {
        if(isActive) {
            for(int i = 0; i < MAX_PLAYERS; i++) {
                Player player = players.get(i);
                player.getClientHandler().sendMessage("Your cards:");
                player.getDeck().displayDeck();
            }
        }
    }

    Player getPlayer(ClientHandler clientHandler) {
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if(player.getClientHandler() == clientHandler) {
                return player;
            }
        }
        return null;
    }

    void addPlayer(ClientHandler clientHandler) {
        Player player = new Player(clientHandler, clientHandler.getUserName());
        players.add(player);
    }

    void deletePlayer(ClientHandler clientHandler) {
        Player player = getPlayer(clientHandler);
        players.remove(player);
    }

    void startGame() {
        String message = "Room nr." + roomNumber + " game is starting...";
        System.out.println("NEW MESSAGE: " + message);
        broadcastMessage("---------------------------------------\n" + message);
        ClientHandler.refreshServerScreen();
    }

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
        }
    }
}
