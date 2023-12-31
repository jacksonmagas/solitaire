package cs3500.klondike.model.hw02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Builder class for making a deck of cards.
 */
public class DeckBuilder {
  int maxValue;
  List<String> suits;
  HashMap<Integer, String> values;

  /**
   * Constructs an empty deck of cards to build on.
   * Also creates a map of integer value to card value;
   */
  public DeckBuilder() {
    int maxValue = 0;
    this.suits = new ArrayList<>();
    this.values = new HashMap<>();
    values.put(1, "A");
    for (int i = 2; i < 11; i++) {
      values.put(i, Integer.toString(i));
    }
    values.put(11, "J");
    values.put(12, "Q");
    values.put(13, "K");
  }

  /**
   * Set the length of run for each suit.
   * @param maxValue the length of run
   * @return this
   */
  public DeckBuilder setMax(int maxValue) {
    this.maxValue = maxValue;
    return this;
  }

  /**
   * Add a copy of the suit ot this deck.
   * @param suit the suit to add
   * @return this DeckBuilder
   */
  public DeckBuilder addSuit(String suit) {
    this.suits.add(suit);
    return this;
  }

  /**
   * Remove a copy of the suit from this deck.
   * @param suit the suit to add
   * @return this DeckBuilder
   */
  public DeckBuilder removeSuit(String suit) {
    this.suits.remove(suit);
    return this;
  }

  /**
   * Create the deck this object has been building with maxValue cards of each included suit.
   * @return A valid deck of cards
   */
  public List<Card> getDeck() {
    List<Card> deck = new ArrayList<>();
    for (String suit : suits) {
      for (int value = 1; value <= maxValue; value++) {
        deck.add(new StandardCard(values.get(value), suit));
      }
    }
    return deck;
  }
}
