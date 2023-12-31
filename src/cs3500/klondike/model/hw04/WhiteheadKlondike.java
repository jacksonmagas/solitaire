package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is a model for playing a game of whitehead klondike according to the
 * KlondikeModel interface.
 *
 * <p>Whitehead klondike has several rule changes compares to basic klondike:
 * <ul><li>All the cards in the cascade piles are dealt face-up.</li>
 * <li>Instead of alternating colors, builds must be single-colored: red cards on red cards,
 * black cards on black cards.</li>
 * <li>When moving multiple cards from one cascade pile to another, all the moved cards must be
 * all the same suit, not merely a valid build.</li>
 * <li>When moving a card into an empty cascade pile, it can be any card value, not just a king.
 * (When moving multiple cards into an cascade pile, it must be a single-suit run, as above,
 * but it can start from any value.)</li>
 * </ul>
 *
 * <p>INVARIANT: All cards in cascade piles are visible.
 */
public class WhiteheadKlondike implements KlondikeModel {
  private final BaseKlondike delegate;

  /**
   * Create a new WhiteheadKlondike model by initializing a new BaseKlondike delegate.
   */
  public WhiteheadKlondike() {
    this.delegate = new BaseKlondike();
  }

  @Override
  public List<Card> getDeck() {
    return delegate.getDeck();
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
      throws IllegalArgumentException {
    delegate.startGame(deck, shuffle, numPiles, numDraw);
    revealAllCascadePiles();
  }

  /**
   * Flip over every card in the cascade piles.
   */
  private void revealAllCascadePiles() {
    for (int pile = 0; pile < getNumPiles(); pile++) {
      for (int card = 0; card < getPileHeight(pile); card++) {
        delegate.flipCardAt(pile, card);
      }
    }
  }

  /**
   * A card in Whitehead can stack on a cascade pile if the pile is empty or if it is the same
   * color as the top card of the destination and one less in value.
   * @param srcCard the card to check if you can move
   * @param dest the cascade pile to move to
   * @return True if the given card can stack on the given pile
   */
  private boolean cardCanStackOnCascade(Card srcCard, int dest) {
    boolean destEmpty = getPileHeight(dest) == 0;
    boolean validSuit = false;
    boolean srcOneLess = false;
    if (!destEmpty) {
      Card destCard = getTopCard(dest);
      boolean bothBlack =
          "♣♠".contains("" + srcCard.getSuit()) && "♣♠".contains("" + destCard.getSuit());
      boolean bothRed =
          "♡♢".contains("" + srcCard.getSuit()) && "♡♢".contains("" + destCard.getSuit());
      validSuit = bothBlack || bothRed;
      srcOneLess = destCard.getPointValue() - srcCard.getPointValue() == 1;
    }
    return destEmpty
        || (validSuit && srcOneLess);
  }

  /**
   * Determines if all the cards in the list the same suit.
   * @param cards A list of cards to see whether they are the same suit
   * @return True if all of the cards are the same suit
   */
  private boolean sameSuit(List<Card> cards) {
    char firstSuit = cards.get(0).getSuit();
    return cards.stream()
        .map(Card::getSuit)
        .allMatch((Character c) -> c == firstSuit);
  }

  /**
   * Determines if all the cards in the list are in decreasing order.
   * @param cards cards to check order
   * @return True if the cards are in decreasing order
   */
  private boolean inOrder(List<Card> cards) {
    List<Card> sortedCards = new ArrayList<>(cards);
    sortedCards.sort(Collections.reverseOrder(Card::compareValue));
    return cards.equals(sortedCards);
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile) throws IllegalStateException {
    int srcHeight = getPileHeight(srcPile);
    if (numCards > srcHeight || numCards < 0) {
      throw new IllegalArgumentException("Not enough cards in source pile.");
    }
    if (srcPile == destPile) {
      throw new IllegalArgumentException("Source and destination piles must be distinct.");
    }
    List<Card> cardsToMove = IntStream.range(srcHeight - numCards, srcHeight)
        .mapToObj((int i) -> getCardAt(srcPile, i))
        .collect(Collectors.toList());
    if (cardCanStackOnCascade(cardsToMove.get(0), destPile)
        && sameSuit(cardsToMove)
        && inOrder(cardsToMove)) {
      delegate.movePile(srcPile, numCards, destPile);
    } else {
      throw new IllegalStateException("Illegal move");
    }
  }

  @Override
  public void moveDraw(int destPile) throws IllegalStateException {
    if (!getDrawCards().isEmpty() && cardCanStackOnCascade(getDrawCards().get(0), destPile)) {
      delegate.moveDraw(destPile);
    } else {
      throw new IllegalStateException("Draw pile is empty or card can't stack on the target pile");
    }
  }

  /**
   * Determines if the top card of the source pile can stack on the foundation pile.
   * @param sourceCard source pile to take top card from 0 indexed from left
   * @param foundationPile to move to 0 indexed from left
   * @return True if the top card of the source pile is the same suit as the destination pile
   *         and one greater in value
   *         or if the destination pile is empty and the source card is an ace.
   */
  private boolean canStackOnFoundation(Card sourceCard, int foundationPile) {
    Card destCard = getCardAt(foundationPile);
    boolean foundationEmpty = destCard == null;
    boolean sameSuit = destCard != null
        && sourceCard.getSuit() == destCard.getSuit();
    boolean srcOneGreater = destCard != null
        && sourceCard.getPointValue() - destCard.getPointValue() == 1;
    boolean srcCardAce = sourceCard.getPointValue() == 1;

    return (sameSuit && srcOneGreater)
        || (foundationEmpty && srcCardAce);
  }

  /**
   * Get the top card of the source pile.
   * @param srcPile the pile to get a card from 0 indexed from the left
   * @return the top card of the pile
   */
  private Card getTopCard(int srcPile) {
    return getCardAt(srcPile, getPileHeight(srcPile) - 1);
  }

  @Override
  public void moveToFoundation(int srcPile, int foundationPile) throws IllegalStateException {
    if (getPileHeight(srcPile) > 0 && canStackOnFoundation(getTopCard(srcPile), foundationPile)) {
      delegate.moveToFoundation(srcPile, foundationPile);
    } else {
      throw new IllegalStateException("The move is not allowable");
    }
  }

  @Override
  public void moveDrawToFoundation(int foundationPile) throws IllegalStateException {
    if (foundationPile >= getNumFoundations() || foundationPile < 0) {
      throw new IllegalArgumentException("foundationPile must be a valid index");
    }
    if (!getDrawCards().isEmpty() && canStackOnFoundation(getDrawCards().get(0), foundationPile)) {
      delegate.moveDrawToFoundation(foundationPile);
    } else {
      throw new IllegalStateException("The move is not allowable");
    }
  }

  @Override
  public void discardDraw() throws IllegalStateException {
    delegate.discardDraw();
  }

  @Override
  public int getNumRows() {
    return delegate.getNumRows();
  }

  @Override
  public int getNumPiles() {
    return delegate.getNumPiles();
  }

  @Override
  public int getNumDraw() {
    return delegate.getNumDraw();
  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    //error checking
    delegate.isGameOver();

    Predicate<Card> toAnyFoundationLegal = (Card c) -> IntStream.range(0,
        getNumFoundations()).anyMatch((int i) -> canStackOnFoundation(c, i));
    //can the draw card be moved to any foundation pile
    boolean drawToFoundationLegal = !getDrawCards().isEmpty()
        && toAnyFoundationLegal.test(getDrawCards().get(0));
    //can the top card of any non-empty cascade pile be moved to any foundation pile
    boolean cascadeToFoundationLegal = IntStream.range(0, getNumPiles())
        //filter non-empty
        .filter((int pileNum) -> getPileHeight(pileNum) > 0)
        //get top card
        .mapToObj((int pile) -> getCardAt(pile, getPileHeight(pile) - 1))
        //check if each card can move to any foundation
        .anyMatch(toAnyFoundationLegal);
    Predicate<Card> toAnyCascadeLegal = (Card c) -> IntStream.range(0, getNumPiles())
        .anyMatch((int i) -> cardCanStackOnCascade(c, i));
    //can the draw card be moved to any cascade pile
    boolean drawToCascadeLegal = !getDrawCards().isEmpty()
        && toAnyCascadeLegal.test(getDrawCards().get(0));
    //does any cascade pile have a face up card that can be moved to any cascade pile?
    boolean movePileLegal = IntStream.range(0, getNumPiles())
        //filter non-empty
        .filter((int pileNum) -> getPileHeight(pileNum) > 0)
        .anyMatch((int pileNum) -> IntStream.range(0, getPileHeight(pileNum) - 1)
            //map to card
            .mapToObj((int card) -> getCardAt(pileNum, card))
            //check if card can go to any foundation
            .anyMatch(toAnyCascadeLegal));
    boolean discardLegal = !getDrawCards().isEmpty();

    return !discardLegal
        && !drawToFoundationLegal
        && !drawToCascadeLegal
        && !cascadeToFoundationLegal
        && !movePileLegal;
  }

  @Override
  public int getScore() throws IllegalStateException {
    return delegate.getScore();
  }

  @Override
  public int getPileHeight(int pileNum) throws IllegalStateException {
    return delegate.getPileHeight(pileNum);
  }

  @Override
  public Card getCardAt(int pileNum, int card) throws IllegalStateException {
    return delegate.getCardAt(pileNum, card);
  }

  @Override
  public Card getCardAt(int foundationPile) throws IllegalStateException {
    return delegate.getCardAt(foundationPile);
  }

  @Override
  public boolean isCardVisible(int pileNum, int card) throws IllegalStateException {
    return delegate.isCardVisible(pileNum, card);
  }

  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    return delegate.getDrawCards();
  }

  @Override
  public int getNumFoundations() throws IllegalStateException {
    return delegate.getNumFoundations();
  }
}
