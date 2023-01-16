/**
 * Class containing data about the card
 */

public class Card {
    private String color;
    private int value;
    private String name;
    public boolean isTaken;

    Card(String color, int value, String name) {
        this.color = color;
        this.value = value;
        this.name = name;
        isTaken = false;
    }

    String getCardDetails() {
        return "[" + name + "|" + color + "]";
    }

    public String getColor() {
        return this.color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public int getValue() {
        return this.value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
