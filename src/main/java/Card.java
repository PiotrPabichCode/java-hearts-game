/**
 * Class containing data about the card
 */

public class Card {
    /**
     * Variable that stores card color
     */
    private String color;
    /**
     * Variable that stores card value
     */
    private int value;
    /**
     * Variable that stores card name
     */
    private String name;

    Card(String color, int value, String name) {
        this.color = color;
        this.value = value;
        this.name = name;
    }

    /**
     * Method that gets card details
     */
    String getCardDetails() {
        return "[" + name + "|" + color + "]";
    }

    /**
     * Getter that gets color
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Setter that sets color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Getter that gets value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Setter that sets value
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Getter that gets name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter that sets name
     */
    public void setName(String name) {
        this.name = name;
    }
}
