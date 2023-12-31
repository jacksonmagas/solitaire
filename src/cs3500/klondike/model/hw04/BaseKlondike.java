package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.ListMove;
import cs3500.klondike.model.hw02.StandardCard;
import cs3500.klondike.model.hw02.VisibiltyCard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is a base implementation of a klondike model which does
 * no checking for logical move legality beside null checks, checking for game start,
 * and bounds checks.
 */
public class BaseKlondike implements cs3500.klondike.model.hw02.KlondikeModel {
  private List<Stack<VisibiltyCard>> foundationPiles;
  private List<List<VisibiltyCard>> cascadePiles;
  private List<VisibiltyCard> drawCards;
  private final List<Card> deck = new ArrayList<>();
  private int numDraw;
  private boolean gameStarted;

  /**
   * Constructor produces a default deck of 52 cards.
   */
  public BaseKlondike() {
    List<String> values = Arrays.asList("A","2","3","4","5","6","7","8","9","10","J","Q","K");
    List<String> suits = Arrays.asList("♣", "♠", "♡", "♢");
    for (String s : suits) {
      for (String v : values) {
        this.deck.add(new StandardCard(v, s));
      }
    }
    this.gameStarted = false;
  }

  /**
   * Return a deck containing the cards this game can work with.
   * @return the deck this game was started with
   *         or a standard 52 card deck if the game hasn't started
   */
  @Override
  public List<Card> getDeck() {
    return this.deck;
  }

  /**
   * Ensures that the given deck is non-null and contains equal runs of each included suit.
   * @param deck A list of cards to be validated
   * @throws IllegalArgumentException if the deck is null or contains null cards,
   *                                  or does not contain equal runs per suit
   */
  private void validateDeck(List<Card> deck) throws IllegalArgumentException {
    if (deck == null || deck.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("Deck must not be null.");
    }

    List<String> heartValues = deck.stream().map(Card::toString)
        .filter((String s) -> s.contains("♡")).map((String s) -> s.substring(0, s.length() - 2))
        .sorted(String::compareTo).collect(Collectors.toList());
    List<String> spadeValues = deck.stream().map(Card::toString)
        .filter((String s) -> s.contains("♠")).map((String s) -> s.substring(0, s.length() - 2))
        .sorted(String::compareTo).collect(Collectors.toList());
    List<String> clubValues = deck.stream().map(Card::toString)
        .filter((String s) -> s.contains("♣")).map((String s) -> s.substring(0, s.length() - 2))
        .sorted(String::compareTo).collect(Collectors.toList());
    List<String> diamondValues = deck.stream().map(Card::toString)
        .filter((String s) -> s.contains("♢")).map((String s) -> s.substring(0, s.length() - 2))
        .sorted(String::compareTo).collect(Collectors.toList());

    BiFunction<List<String>, List<String>, Boolean> nonEmptyAndDistinct;
    nonEmptyAndDistinct = (List<String> l1, List<String> l2) ->
        !l1.isEmpty() && !l2.isEmpty() && !l1.equals(l2);

    if (nonEmptyAndDistinct.apply(heartValues, spadeValues)
        || nonEmptyAndDistinct.apply(heartValues, clubValues)
        || nonEmptyAndDistinct.apply(heartValues, diamondValues)
        || nonEmptyAndDistinct.apply(spadeValues, clubValues)
        || nonEmptyAndDistinct.apply(spadeValues, diamondValues)
        || nonEmptyAndDistinct.apply(clubValues, diamondValues)) {
      throw new IllegalArgumentException("Deck must contain equal runs of each suit");
    }
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
   * <p>Cascade piles are dealt face down, it is the responsibility of users of this class to reveal
   * any card that should be visible using the flip method of this class.</p>
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
  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
      throws IllegalArgumentException, IllegalStateException {
    //validate deck is not null, contains no null cards,
    // and has equal length runs of each included suit
    this.validateDeck(deck);
    //verify that numPiles is valid for this deck
    int minDeckSize = IntStream.range(1, numPiles + 1).sum();
    if (deck.size() < minDeckSize || numPiles < 1) {
      throw new IllegalArgumentException("There are not enough cards to create "
          + numPiles + " Cascades");
    }
    //verify numDraw is valid
    if (numDraw < 1) {
      throw new IllegalArgumentException("numDraw must be at least 1");
    }

    if (this.gameStarted) {
      throw new IllegalStateException("Game has already started");
    }
    this.gameStarted = true;

    //shuffle the deck if needed
    if (shuffle) {
      Collections.shuffle(this.deck);
    }
    //initialize draw cards as the deck
    this.drawCards = deck.stream().map(VisibiltyCard::new).collect(Collectors.toList());

    //create an array of numPiles empty lists of cards
    this.cascadePiles = new ArrayList<List<VisibiltyCard>>(numPiles);
    for (int i = 0; i < numPiles; i++) {
      this.cascadePiles.add(new ArrayList<VisibiltyCard>());
    }

    //deal cards from the draw cards pile in the cascade pattern
    for (int i = 0; i < numPiles; i++) {
      for (int j = i; j < numPiles; j++) {
        ListMove.<VisibiltyCard>moveFirstToCollection(this.drawCards, this.cascadePiles.get(j));
      }
    }

    //count the number of aces in the deck and create that many empty foundation piles
    int numAces = (int) deck.stream().filter((Card c) -> c.toString().charAt(0) == 'A').count();
    this.foundationPiles = new ArrayList<Stack<VisibiltyCard>>();
    for (int i = 0; i < numAces; i++) {
      this.foundationPiles.add(new Stack<VisibiltyCard>());
    }

    //set numDraw

    this.numDraw = numDraw;

    //draw the first numDraw cards
    flipNextDrawCardsFaceUp();
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
   */
  @Override
  public void movePile(int srcPile, int numCards, int destPile)
      throws IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (srcPile >= this.cascadePiles.size()
        || destPile >= this.cascadePiles.size()
        || srcPile == destPile) {
      throw new IllegalArgumentException("Distinct source and destination piles must exist");
    }
    List<VisibiltyCard> source = this.cascadePiles.get(srcPile);
    List<VisibiltyCard> dest = this.cascadePiles.get(destPile);
    if (source.size() < numCards || numCards < 1) {
      throw new IllegalArgumentException("Source does not have enough cards to move");
    }
    ListMove.moveLastN(numCards, source, dest);
  }

  /**
   * Ensures that the top card of a cascade pile is face up if it has one.
   * @param numPile the pile number to check 0 indexed from the left
   * @throws IllegalArgumentException if numPile is not valid coordinate
   */
  private void ensureTopCardVisible(int numPile) {
    List<VisibiltyCard> pile = this.cascadePiles.get(numPile);
    if (!pile.isEmpty() && !isCardVisible(numPile, pile.size() - 1)) {
      pile.get(pile.size() - 1).flip();
    }
  }

  /**
   * Moves the topmost draw-card to the destination pile.  If no draw cards remain,
   * reveal the next available draw cards
   * @param destPile the 0-based index (from the left) of the destination pile for the
   *                 card
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if destination pile number is invalid
   * @throws IllegalStateException if there are no draw cards
   */
  @Override
  public void moveDraw(int destPile) throws IllegalStateException, IllegalArgumentException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (destPile < 0 || destPile >= getNumPiles()) {
      throw new IllegalArgumentException("There is no cascade pile at the given index");
    }
    if (drawCards.isEmpty()) {
      throw new IllegalStateException("Draw pile is empty");
    }
    Card moveCard = drawCards.get(0);
    List<VisibiltyCard> dest = this.cascadePiles.get(destPile);
    ListMove.moveFirstToCollection(drawCards, dest);
    flipNextDrawCardsFaceUp();
  }

  /**
   * Ensure that the top numDraw cards are flipped.
   */
  private void flipNextDrawCardsFaceUp() {
    int toDraw = Math.min(numDraw, this.drawCards.size());
    for (int i = 0; i < toDraw; i++) {
      if (!this.drawCards.get(i).isFaceUp()) {
        this.drawCards.get(i).flip();
      }
    }
  }

  /**
   * Moves the top card of the given pile to the requested foundation pile.
   * @param srcPile        the 0-based index (from the left) of the pile to move a card
   * @param foundationPile the 0-based index (from the left) of the foundation pile to
   *                       place the card
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalArgumentException if either pile number is invalid
   * @throws IllegalStateException if the source pile is empty
   */
  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
      throws IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (srcPile < 0 || srcPile >= this.getNumPiles()
        || foundationPile < 0 || foundationPile >= this.getNumFoundations()) {
      throw new IllegalArgumentException("Invalid pile number");
    }
    List<VisibiltyCard> source = this.cascadePiles.get(srcPile);
    if (source.isEmpty()) {
      throw new IllegalStateException("The source pile is empty");
    }
    ListMove.moveLastToCollection(source, this.foundationPiles.get(foundationPile));
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
    if (!gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (foundationPile < 0 || foundationPile >= foundationPiles.size()) {
      throw new IllegalArgumentException("There is no foundation pile at the specified index");
    }
    if (getDrawCards().isEmpty()) {
      throw new IllegalStateException("There are no available draw cards.");
    }
    ListMove.moveFirstToCollection(drawCards, foundationPiles.get(foundationPile));
    flipNextDrawCardsFaceUp();
  }

  /**
   * Discards the topmost draw-card.
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalStateException if move is not allowable
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (getDrawCards().isEmpty()) {
      throw new IllegalStateException("There is no face up draw card to discard");
    }
    drawCards.get(0).flip();
    ListMove.moveFirstToCollection(drawCards, drawCards);
    flipNextDrawCardsFaceUp();
  }

  /**
   * Returns the number of rows currently in the game.
   * @return the height of the current table of cards
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws java.util.NoSuchElementException if there are no cascade piles
   */
  @Override
  public int getNumRows() {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    return this.cascadePiles.stream().map(List::size).max(Integer::compareTo).orElseThrow();
  }

  /**
   * Returns the number of piles for this game.
   * @return the number of piles
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumPiles() {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    return this.cascadePiles.size();
  }

  /**
   * Returns the maximum number of visible cards in the draw pile.
   *
   * @return the number of visible cards in the draw pile
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumDraw() {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    return this.numDraw;
  }

  /**
   * A base klondike doesn't know if the game is over because it doesn't know what constitutes a
   * legal move in the specific implementation.
   *
   * @return false
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public boolean isGameOver() throws IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }

    return false;
  }

  /**
   * Return the current score, which is the sum of the values of the topmost cards
   * in the foundation piles.
   * @return the score
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getScore() throws IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    Predicate<Stack<VisibiltyCard>> isNonEmpty = (Stack<VisibiltyCard> s) -> !s.isEmpty();

    return this.foundationPiles.stream()
        .filter(isNonEmpty)
        .map(Stack::peek)
        .mapToInt(Card::getPointValue)
        .sum();
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
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (pileNum < 0 || pileNum >= getNumPiles()) {
      throw new IllegalArgumentException("No pile at the given location");
    }
    return this.cascadePiles.get(pileNum).size();
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
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (pileNum >= 0 && pileNum < getNumPiles()
        && card >= 0 && card < getPileHeight(pileNum)) {
      return this.cascadePiles.get(pileNum).get(card).isFaceUp();
    } else {
      throw new IllegalArgumentException("There is no card at this location");
    }
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
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (!isCardVisible(pileNum, card)) {
      throw new IllegalArgumentException("The card at this location is not visible");
    }
    if (pileNum >= 0 && pileNum < getNumPiles()
        && card >= 0 && card < getPileHeight(pileNum)) {
      return this.cascadePiles.get(pileNum).get(card).card();
    } else {
      throw new IllegalArgumentException("There is no card at those co-ordinates");
    }
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
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (foundationPile < 0 || foundationPile >= this.foundationPiles.size()) {
      throw new IllegalArgumentException("No foundation pile at this index");
    }
    if (this.foundationPiles.get(foundationPile).isEmpty()) {
      return null;
      //throw new IllegalArgumentException("Foundation pile is empty");
    }
    return this.foundationPiles.get(foundationPile).peek().card();
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
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    return this.drawCards.stream()
        .filter(VisibiltyCard::isFaceUp)
        .map(VisibiltyCard::card)
        .collect(Collectors.toList());
  }

  /**
   * Return the number of foundation piles in this game.
   * @return the number of foundation piles
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumFoundations() throws IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    return this.foundationPiles.size();
  }

  /**
   * Flip the card at the given row\column index.
   * @param pileNum Cascade pile to flip card in, 0 indexed from left
   * @param cardNum Card to flip in the pile, 0 indexed from top
   */
  public void flipCardAt(int pileNum, int cardNum) {
    this.cascadePiles.get(pileNum).get(cardNum).flip();
  }

  /**
   * Remove the top draw card from the deck.
   * @throws IllegalStateException if the deck is empty
   */
  public void removeDraw() {
    if (this.drawCards.isEmpty()) {
      throw new IllegalStateException("Can't remove a card from an empty list");
    }
    this.drawCards.remove(0);
    flipNextDrawCardsFaceUp();
  }
}
