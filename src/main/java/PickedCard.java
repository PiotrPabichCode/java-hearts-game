/**
 * PickedCard class that contains data
 * about the card selected by the player
 */

public class PickedCard {
    /**
     * Variable that stores player
     */
    private Player player;
    /**
     * Variable that stores card
     */
    private Card card;

    /**
     * PickedCard constructor
     */
    PickedCard(Player player, Card card) {
        this.player = player;
        this.card = card;
    }

    /**
     * Getter that gets card
     */

    public Card getCard() {
        return card;
    }
    /**
     * Getter that gets player
     */
    public Player getPlayer() {
        return player;
    }
}
