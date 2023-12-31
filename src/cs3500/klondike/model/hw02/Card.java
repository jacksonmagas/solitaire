package cs3500.klondike.model.hw02;

/**
 * This (essentially empty) interface marks the idea of cards.  You will need to
 * implement this interface in order to use your model.
 * 
 * <p>The only behavior guaranteed by this class is its {@link Card#toString()} method,
 * which will render the card as specified in the assignment.
 * 
 * <p>In particular, you <i>do not</i> know what implementation of this interface is
 * used by the Examplar wheats and chaffs, so your tests must be defined sufficiently
 * broadly that you do not rely on any particular constructors or methods of cards.
 */
public interface Card {
  /**
   * Renders a card with its value followed by its suit as one of
   * the following symbols (♣, ♠, ♡, ♢).
   * For example, the 3 of Hearts is rendered as {@code "3♡"}.
   * @return the formatted card
   */
  String toString();

  /**
   * Returns the character representing this card's suit.
   * @return the suit
   */
  public char getSuit();

  /**
   * Returns the point value of this card.
   * @return the point value of this card
   */
  public int getPointValue();

  /**
   * Compares this card with another card by their values.
   * @param other another card to compare by value.
   * @return 0 if this has the same value as {@code other},
   *         < 0 if this has a lower value than {@code other},
   *         > 0 if this has a higher value than {@code other}
   *
   */
  public int compareValue(Card other);
}
