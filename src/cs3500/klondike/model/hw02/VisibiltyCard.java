package cs3500.klondike.model.hw02;

/**
 * Wraps a card in order to keep track of its visibility.
 */
public final class VisibiltyCard implements Card {
  private final Card delegate;
  private boolean isFaceUp;

  /**
   * Initiallize a new face down card wrapping the given card.
   * @param base The card to wrap
   */
  public VisibiltyCard(Card base) {
    this.delegate = base;
    this.isFaceUp = false;
  }

  @Override
  public char getSuit() {
    return delegate.getSuit();
  }

  @Override
  public int getPointValue() {
    return delegate.getPointValue();
  }

  @Override
  public int compareValue(Card other) {
    return delegate.compareValue(other);
  }

  /**
   * Checks if card is face up.
   * @return True iff this card is face up
   */
  public boolean isFaceUp() {
    return this.isFaceUp;
  }

  /**
   * Flip the card from visible to not visible and vice versa.
   */
  public void flip() {
    this.isFaceUp = !this.isFaceUp;
  }

  /**
   * Get the card wrapped by this object.
   * @return The card wrapped by this visibility card
   */
  public Card card() {
    return this.delegate;
  }
}
