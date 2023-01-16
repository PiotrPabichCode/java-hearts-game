import static org.junit.jupiter.api.Assertions.*;

class ClassesTest {
    @org.junit.jupiter.api.Test
    public void createCardTest() {
        Card card = new Card("BLUE", 10, "2");

        assertEquals(card.getColor(), "BLUE");
        assertEquals(card.getValue(), 10);
        assertEquals(card.getName(), "2");
    }

    @org.junit.jupiter.api.Test
    public void createDeckTest() {
        Deck deck = new Deck();
        int cardNamesSize = Deck.DECK_SIZE / 4;
        int cardTypesSize = 4;
        assertEquals(deck.getDeck().size(), Deck.DECK_SIZE);
        assertEquals(deck.getCardNames().size(), cardNamesSize);
        assertEquals(deck.getCardTypes().size(), cardTypesSize);
        assertEquals(deck.getDeck().get(7).getName(), "4");
    }

    @org.junit.jupiter.api.Test
    public void createGameTest() {
        int roomNumber = 10;
        Game game = new Game(roomNumber);
        assertEquals(game.getRoomNumber(), roomNumber);
        assertEquals(game.getRound(), 1);
        assertEquals(game.getPlayers().size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void createRoomTest() {
        int roomNumber = 10;
        int size = 3;
        Game game = new Game(roomNumber);
        Room room = new Room(roomNumber,size, game);

        assertEquals(room.getNumber(), roomNumber);
        assertEquals(room.getSize(), size);
        assertEquals(room.game, game);
        assertEquals(room.game.getRoomNumber(), room.getNumber());

    }

    @org.junit.jupiter.api.Test
    public void createPlayerTest() {
        String playerName = "Tomek";
        Player player = new Player(null, playerName);
        assertEquals(player.getPlayerName(), playerName);
        assertEquals(player.getPoints(), 0);
        assertEquals(player.getDeck().getSize(), 0);
        assertEquals(player.getPlace(), 0);
    }

    @org.junit.jupiter.api.Test
    public void createPlayerDeck() {
        String playerName = "Tomek";
        Player player = new Player(null, playerName);
        PlayerDeck playerDeck = new PlayerDeck(0, player);

        assertEquals(playerDeck.getSize(), 0);
        assertEquals(playerDeck.getPlayer(), player);
    }

    @org.junit.jupiter.api.Test
    public void createPickedDeck() {
        String playerName = "Karol";
        Player player = new Player(null, playerName);
        Card card = new Card("BLUE", 10, "8");
        PickedCard pickedCard = new PickedCard(player, card);

        assertEquals(pickedCard.getPlayer(), player);
        assertEquals(pickedCard.getCard(), card);
    }
}