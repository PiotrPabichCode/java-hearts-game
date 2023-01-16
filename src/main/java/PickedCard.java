/**
 * PickedCard class that contains data
 * about the card selected by the player
 */

public class PickedCard {
    private Player player;
    private Card card;

    PickedCard(Player player, Card card) {
        this.player = player;
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public Player getPlayer() {
        return player;
    }
}
