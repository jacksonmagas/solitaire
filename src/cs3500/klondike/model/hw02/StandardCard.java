package cs3500.klondike.model.hw02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A standard playing card Ace - King in one of the four suits hearts, diamonds, spades, or clubs.
 * Can return its name, value
 */
public class StandardCard implements Card {
  private final String stringValue;
  private final int intValue;
  private final char suit;

  /**
   * Construct a new card from a value and a suit.
   * @param value One of A, K, Q, J, or an integer 2-10
   * @param suit A string representing clubs, spades, diamonds, or hearts.
   * @throws IllegalArgumentException If value is Null or not a valid card value.
   * @throws IllegalArgumentException If suit is Null or not a valid suit representation.
   */
  public StandardCard(String value, String suit) {
    if (value == null || suit == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }

    List<String> validValues = new ArrayList<>(List.of("A"));
    for (int i = 2; i <= 10 ; i++) {
      validValues.add(Integer.toString(i));
    }
    validValues.addAll(Arrays.asList("J", "Q", "K"));

    if (validValues.contains(value.toUpperCase())) {
      this.stringValue = value.toUpperCase();
      this.intValue = validValues.indexOf(value.toUpperCase()) + 1;
    } else {
      throw new IllegalArgumentException("Value must be A, K, Q, "
          + "J, or an integer 2-10");
    }
    this.suit = chooseSuit(suit);
  }

  /**
   * Returns the suit symbol from various representations of suits as a string.
   * For example club, clubs, and ♣ will all return the char ♣.
   * @param suit A string representing the suit
   * @return the symbol of the given suit
   * @throws IllegalArgumentException if suit is not a valid representation of a suit
   */
  public static char chooseSuit(String suit) {
    switch (suit.toLowerCase()) {
      case "club":
      case "clubs":
      case "♣":
        return '♣';
      case "spade":
      case "spades":
      case "♠":
        return '♠';
      case "heart":
      case "hearts":
      case "♡":
        return '♡';
      case "diamond":
      case "diamonds":
      case "♢":
        return '♢';
      default:
        throw new IllegalArgumentException("Must input a valid suit");
    }
  }

  /**
   * Renders a card with its value followed by its suit as one of
   * the following symbols (♣, ♠, ♡, ♢).
   * For example, the 3 of Hearts is rendered as {@code "3♡"}.
   * @return the formatted card
   */
  public String toString() {
    return this.stringValue + this.suit;
  }

  /**
   * Determines if the object o is a card with the same value as this.
   * @param o Object to compare with this
   * @return true if {@code o} is a card with the same suit and value
   */
  public boolean equals(Object o) {
    return o instanceof Card
        && ((Card) o).toString().equals(this.toString());
  }

  /**
   * Creates a hashcode for this object based on its suit and value.
   * @return the hashCode of this.toString()
   */
  public int hashCode() {
    return this.toString().hashCode() * 7777;
  }

  /**
   * Returns the point value of this card.
   * @return the point value of this card
   */
  public int getPointValue() {
    return this.intValue;
  }

  /**
   * Returns the character representing this card's suit.
   * @return the suit
   */
  public char getSuit() {
    return this.suit;
  }

  /**
   * Compares this card with another card by their values.
   * @param other another card to compare by value.
   * @return 0 if this has the same value as {@code other},
   *         < 0 if this has a lower value than {@code other},
   *         > 0 if this has a higher value than {@code other}
   *
   */
  public int compareValue(Card other) {
    if (other == null) {
      throw new IllegalArgumentException("Argument cannot be null");
    }
    return Integer.compare(intValue, other.getPointValue());
  }
}
