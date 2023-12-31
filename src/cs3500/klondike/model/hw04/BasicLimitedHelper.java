package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.Card;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Helper class containing further shared functionality between basic and limited draw klondike
 * beyond what base klondike provides.
 * Should be protected, but can't because BasicKlondike and LimitedDrawKlondike are in different
 * packages.
 */
public class BasicLimitedHelper {
  BaseKlondike delegate;

  /**
   * Initialize a new helper by assigning it the delegate of the class using this.
   * @param delegate The base klondike to execute commands on
   */
  public BasicLimitedHelper(BaseKlondike delegate) {
    this.delegate = delegate;
  }

  /**
   * <p>Deal a new game of Klondike.
   * The cards to be used and their order are specified by the the given deck,
   * unless the {@code shuffle} parameter indicates the order should be ignored.</p>
   *
   * <p>This method first verifies that the deck is valid. It deals cards in rows
   * (left-to-right, top-to-bottom) into the characteristic cascade shape
   * with the specified number of rows, followed by (at most) the specified number of
   * draw cards. When {@code shuffle} is {@code false}, the {@code deck} must be used in
   * order and the 0th card in {@code deck} is used as the first card dealt.
   * There will be as many foundation piles as there are Aces in the deck.</p>
   *
   * <p>A valid deck must consist cards that can be grouped into equal-length,
   * consecutive runs of cards (each one starting at an Ace, and each of a single
   * suit).</p>
   *
   * <p>This method should have no side effects other than configuring this model
   * instance, and should work for any valid arguments.</p>
   *
   * @param deck      the deck to be dealt
   * @param shuffle   if {@code false}, use the order as given by {@code deck},
   *                  otherwise use a randomly shuffled order
   * @param numPiles  number of piles to be dealt
   * @param numDraw   maximum number of draw cards available at a time
   * @throws IllegalStateException if the game has already started
   * @throws IllegalArgumentException if the deck is null or invalid,
   *                  a full cascade cannot be dealt with the given sizes,
   *                  or another input is invalid
   */
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
      throws IllegalArgumentException, IllegalStateException {
    delegate.startGame(deck, shuffle, numPiles, numDraw);
    //flip the top card of each cascade pile
    for (int pile = 0; pile < numPiles; pile++) {
      delegate.flipCardAt(pile, delegate.getPileHeight(pile) - 1);
    }
  }

  /**
   * Determines if the top card can be stacked on the given cascade pile.
   * (the top card is one lower in value and opposite color to the top card)
   * @param toMove the card to attempt to stack on top of another card
   * @param destPile the foundation pile to stack onto (0 indexed from the left)
   * @return true if the cards can legally stack
   */
  private boolean canStackOnCascade(Card toMove, int destPile) {
    int destHeight = delegate.getPileHeight(destPile);
    if (destHeight == 0) {
      //Return true if the card to move onto an empty pile is a King
      return toMove.getPointValue() == 13;
    } else {
      Card bottom = delegate.getCardAt(destPile, destHeight - 1);
      boolean oppositeColors = "♣♠".contains(Character.toString(toMove.getSuit()))
          ^ "♣♠".contains(Character.toString(bottom.getSuit()));
      boolean topOneLess = toMove.getPointValue() + 1 == bottom.getPointValue();
      return oppositeColors && topOneLess;
    }
  }

  /**
   * Determines if the top card can be stacked on the given foundation pile.
   * Aces can stack on empty foundation piles, other cards can stack on foundation piles
   * if they are the same suit and 1 greater in value.
   * @param toMove the card to attempt to stack on top of another card
   * @param foundationPile the foundation pile to stack onto (0 indexed from the left)
   * @return true if the cards can legally stack
   */
  private boolean canStackOnFoundation(Card toMove, int foundationPile) {
    if (delegate.getCardAt(foundationPile) == null) {
      //Return true if the card is an ace
      return toMove.getPointValue() == 1;
    } else {
      Card bottom = delegate.getCardAt(foundationPile);
      boolean sameSuit = toMove.getSuit() == bottom.getSuit();
      boolean topOneMore = toMove.getPointValue() - 1 == bottom.getPointValue();
      return sameSuit && topOneMore;
    }
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
  public void movePile(int srcPile, int numCards, int destPile)
      throws IllegalStateException {
    Card bottomCardOfStackToMove = delegate.getCardAt(srcPile,
        delegate.getPileHeight(srcPile) - numCards);
    if (srcPile == destPile) {
      throw new IllegalArgumentException("Piles must be distinct");
    }
    if (!canStackOnCascade(bottomCardOfStackToMove, destPile)) {
      throw new IllegalStateException("Illegal move");
    }
    delegate.movePile(srcPile, numCards, destPile);
    ensureTopCardVisible(srcPile);
  }

  /**
   * Ensures that the top card of a cascade pile is face up if it has one.
   * @param numPile the pile number to check 0 indexed from the left
   * @throws IllegalArgumentException if numPile is not valid coordinate
   */
  private void ensureTopCardVisible(int numPile) {
    if (delegate.getNumPiles() <= numPile || numPile < 0) {
      throw new IllegalArgumentException("numPile is not a valid coordinate");
    }
    if (delegate.getPileHeight(numPile) > 0) {
      delegate.flipCardAt(numPile, delegate.getPileHeight(numPile) - 1);
    }
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
  public void moveDraw(int destPile) throws IllegalStateException, IllegalArgumentException {
    if (!delegate.getDrawCards().isEmpty()
        && canStackOnCascade(delegate.getDrawCards().get(0), destPile)) {
      delegate.moveDraw(destPile);
    } else {
      throw new IllegalStateException("Draw card can't stack on destination pile");
    }
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
  public void moveToFoundation(int srcPile, int foundationPile)
      throws IllegalStateException {
    if (delegate.getPileHeight(srcPile) > 0 && canStackOnFoundation(delegate.getCardAt(srcPile,
        delegate.getPileHeight(srcPile) - 1), foundationPile)) {
      delegate.moveToFoundation(srcPile, foundationPile);
      ensureTopCardVisible(srcPile);
    } else {
      throw new IllegalStateException("The move is not allowable");
    }
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
  public void moveDrawToFoundation(int foundationPile) throws IllegalStateException {
    if (foundationPile >= delegate.getNumFoundations() || foundationPile < 0) {
      throw new IllegalArgumentException("foundationPile must be a valid index");
    }
    if (!delegate.getDrawCards().isEmpty()
        && canStackOnFoundation(delegate.getDrawCards().get(0), foundationPile)) {
      delegate.moveDrawToFoundation(foundationPile);
    } else {
      throw new IllegalStateException("The move is not allowable or draw pile is empty");
    }
  }

  /**
   * Signal if the game is over or not.  A game is over if there are no more
   * possible moves to be made, or draw cards to be used (or discarded).
   *
   * @return true if game is over, false otherwise
   * @throws IllegalStateException if the game hasn't been started yet
   */
  public boolean isGameOver() throws IllegalStateException {
    //error checking
    delegate.isGameOver();

    boolean discardLegal = !delegate.getDrawCards().isEmpty();
    Predicate<Card> toAnyFoundationLegal = (Card c) -> IntStream.range(0,
        delegate.getNumFoundations()).anyMatch((int i) -> canStackOnFoundation(c, i));
    //can the draw card be moved to any foundation pile
    boolean drawToFoundationLegal = !delegate.getDrawCards().isEmpty()
        && toAnyFoundationLegal.test(delegate.getDrawCards().get(0));
    //can the top card of any non-empty cascade pile be moved to any foundation pile
    boolean cascadeToFoundationLegal = IntStream.range(0, delegate.getNumPiles())
        //filter non-empty
        .filter((int pileNum) -> delegate.getPileHeight(pileNum) > 0)
        //get top card
        .mapToObj((int pile) -> delegate.getCardAt(pile, delegate.getPileHeight(pile) - 1))
        //check if each card can move to any foundation
        .anyMatch(toAnyFoundationLegal);
    Predicate<Card> toAnyCascadeLegal = (Card c) -> IntStream.range(0, delegate.getNumPiles())
        .anyMatch((int i) -> canStackOnCascade(c, i));
    //can the draw card be moved to any cascade pile
    boolean drawToCascadeLegal = !delegate.getDrawCards().isEmpty()
        && toAnyCascadeLegal.test(delegate.getDrawCards().get(0));
    //does any cascade pile have a face up card that can be moved to any cascade pile?
    boolean movePileLegal = IntStream.range(0, delegate.getNumPiles())
        //filter non-empty
        .filter((int pileNum) -> delegate.getPileHeight(pileNum) > 0)
        .anyMatch((int pileNum) -> IntStream.range(0, delegate.getPileHeight(pileNum) - 1)
            //filter visible
            .filter((int card) -> delegate.isCardVisible(pileNum, card))
            //map to card
            .mapToObj((int card) -> delegate.getCardAt(pileNum, card))
            //check if card can go to any foundation
            .anyMatch(toAnyCascadeLegal));
    return !discardLegal
        && !drawToFoundationLegal
        && !drawToCascadeLegal
        && !cascadeToFoundationLegal
        && !movePileLegal;
  }
}
