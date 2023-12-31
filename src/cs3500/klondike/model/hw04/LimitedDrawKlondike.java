package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import java.util.List;

/**
 * This is a model to represent a game of Klondike Solitaire where the number of times each card can
 * be the top card on the draw pile is limited before it will be discarded.
 */
public class LimitedDrawKlondike implements KlondikeModel {
  private final int maxReDraws;
  private int numReDraws;
  private final BaseKlondike delegate;
  private final BasicLimitedHelper helper;
  private int numDiscards;
  private int deckSize;

  /**
   * Construct a new game of limited draw klondike where the given amount of draws are allowed.
   * @param numTimesRedrawAllowed the number of draws that are allowed.
   */
  public LimitedDrawKlondike(int numTimesRedrawAllowed) {
    if (numTimesRedrawAllowed < 0) {
      throw new IllegalArgumentException("You must be able to draw each card at least once.");
    }
    this.maxReDraws = numTimesRedrawAllowed;
    this.delegate = new BaseKlondike();
    this.helper = new BasicLimitedHelper(this.delegate);
    this.numReDraws = 0;
    this.numDiscards = 0;
  }

  @Override
  public List<Card> getDeck() {
    return delegate.getDeck();
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
      throws IllegalArgumentException, IllegalStateException {
    helper.startGame(deck, shuffle, numPiles, numDraw);
    deckSize = deck.size() - (numPiles * (numPiles + 1) / 2);
  }

  /**
   * Moves the requested number of cards from the source pile to the destination pile,
   * if allowable by the rules of the game.
   * @param srcPile  the 0-based index (from the left) of the pile to be moved
   * @param numCards how many cards to be moved from that pile
   * @param destPile the 0-based index (from the left) of the destination pile for the
   *                 moved cards
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if either pile number is invalid, if the pile
   *                  numbers are the same, or there are not enough cards to move from
   *                  the srcPile to the destPile (i.e. the move is not physically
   *                  possible)
   * @throws IllegalStateException if the move is not allowable (i.e. the move is not
   *                  logically possible)
   */
  @Override
  public void movePile(int srcPile, int numCards, int destPile)
      throws IllegalStateException {
    helper.movePile(srcPile, numCards, destPile);
  }

  /**
   * Moves the topmost draw-card to the destination pile.  If no draw cards remain,
   * reveal the next available draw cards
   * @param destPile the 0-based index (from the left) of the destination pile for the
   *                 card
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if destination pile number is invalid
   * @throws IllegalStateException if there are no draw cards, or if the move is not
   *                               allowable
   */
  @Override
  public void moveDraw(int destPile) throws IllegalStateException, IllegalArgumentException {
    helper.moveDraw(destPile);
    deckSize--;
  }

  /**
   * Moves the top card of the given pile to the requested foundation pile.
   * @param srcPile        the 0-based index (from the left) of the pile to move a card
   * @param foundationPile the 0-based index (from the left) of the foundation pile to
   *                       place the card
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if either pile number is invalid
   * @throws IllegalStateException if the source pile is empty or if the move is not
   *                               allowable
   */
  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
      throws IllegalStateException {
    helper.moveToFoundation(srcPile, foundationPile);
  }

  /**
   * Moves the topmost draw-card directly to a foundation pile.
   * @param foundationPile the 0-based index (from the left) of the foundation pile to
   *                       place the card
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if the foundation pile number is invalid
   * @throws IllegalStateException if there are no draw cards or if the move is not
   *                               allowable
   */
  @Override
  public void moveDrawToFoundation(int foundationPile) throws IllegalStateException {
    helper.moveDrawToFoundation(foundationPile);
    deckSize--;
  }

  /**
   * Discards the topmost draw-card.
   * The card gets added to the bottom of the deck if it has been redrawn
   * less than maxRedraws times otherwise it is permanently discarded.
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalStateException if move is not allowable
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    if (numReDraws < maxReDraws) {
      delegate.discardDraw();
      numDiscards++;
      numReDraws = numDiscards / deckSize;
    } else {
      delegate.removeDraw();
      deckSize--;
    }
  }

  /**
   * Returns the number of rows currently in the game.
   * @return the height of the current table of cards
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws java.util.NoSuchElementException if there are no cascade piles
   */
  @Override
  public int getNumRows() {
    return delegate.getNumRows();
  }

  /**
   * Returns the number of piles for this game.
   * @return the number of piles
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumPiles() {
    return delegate.getNumPiles();
  }

  /**
   * Returns the maximum number of visible cards in the draw pile.
   *
   * @return the number of visible cards in the draw pile
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumDraw() {
    return delegate.getNumDraw();
  }

  /**
   * Signal if the game is over or not.  A game is over if there are no more
   * possible moves to be made, or draw cards to be used (or discarded).
   *
   * @return true if game is over, false otherwise
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public boolean isGameOver() throws IllegalStateException {
    return helper.isGameOver();
  }

  /**
   * Return the current score, which is the sum of the values of the topmost cards
   * in the foundation piles.
   * @return the score
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getScore() throws IllegalStateException {
    return delegate.getScore();
  }

  /**
   * Returns the number of cards in the specified pile.
   * @param pileNum the 0-based index (from the left) of the pile
   * @return the number of cards in the specified pile
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if pile number is invalid
   */
  @Override
  public int getPileHeight(int pileNum) throws IllegalStateException {
    return delegate.getPileHeight(pileNum);
  }

  /**
   * Returns whether the card at the specified coordinates is face-up or not.
   * @param pileNum  column of the desired card (0-indexed from the left)
   * @param card     row of the desired card (0-indexed from the top)
   * @return whether the card at the given position is face-up or not
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  @Override
  public boolean isCardVisible(int pileNum, int card) throws IllegalStateException {
    return delegate.isCardVisible(pileNum, card);
  }

  /**
   * Returns the card at the specified coordinates, if it is visible.
   * @param pileNum  column of the desired card (0-indexed from the left)
   * @param card     row of the desired card (0-indexed from the top)
   * @return the card at the given position, or <code>null</code> if no card is there
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  @Override
  public Card getCardAt(int pileNum, int card) throws IllegalStateException {
    return delegate.getCardAt(pileNum, card);
  }

  /**
   * Returns the card at the top of the specified foundation pile.
   * @param foundationPile 0-based index (from the left) of the foundation pile
   * @return the card at the given position
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if the foundation pile number is invalid
   */
  @Override
  public Card getCardAt(int foundationPile) throws IllegalStateException {
    return delegate.getCardAt(foundationPile);
  }

  /**
   * Returns the currently available draw cards.
   * There should be at most {@link KlondikeModel#getNumDraw} cards (the number
   * specified when the game started) -- there may be fewer, if cards have been removed.
   * @return the ordered list of available draw cards (i.e. first element of this list
   *         is the first one to be drawn)
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    return delegate.getDrawCards();
  }

  /**
   * Return the number of foundation piles in this game.
   * @return the number of foundation piles
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumFoundations() throws IllegalStateException {
    return delegate.getNumFoundations();
  }
}
