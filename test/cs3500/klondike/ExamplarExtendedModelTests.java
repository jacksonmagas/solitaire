package cs3500.klondike;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;
import cs3500.klondike.model.hw04.WhiteheadKlondike;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class containing many tests on Whitehead and LimitedDraw Klondike models.
 */
public class ExamplarExtendedModelTests {
  KlondikeModel model;
  List<Card> baseDeck;

  @Before
  public void initWhitehead() {
    model = new WhiteheadKlondike();
    baseDeck = model.getDeck();
  }

  @Before
  public void initLimitedDraw(int num) {
    model = new LimitedDrawKlondike(num);
    baseDeck = model.getDeck();
  }

  /**
   * Takes a deck of cards and returns the deck corresponding to the given string.
   * @param baseDeck A deck of cards which contains at least every card wanted in the final deck.
   *                 (Can contain more cards then needed)
   * @param deck A string of card names in the order in which they should appear in the deck.
   *             For example "A♣2♣3♣4♣A♠2♠3♠4♠A♡2♡3♡4♡A♢2♢3♢4♢"
   * @return the stacked deck
   * @throws IllegalArgumentException if the string is not a valid deck, or the string contains a
   *                                  card not in the input deck
   */
  private List<Card> stackDeck(List<Card> baseDeck, String deck) {
    List<Card> finalDeck = new ArrayList<Card>();
    for (int i = 0; i < deck.length() - 1; i ++) {
      for (Card c : baseDeck) {
        if (c.toString().equals(deck.substring(i, i + 2))
            || (i < deck.length() - 2 && c.toString().equals(deck.substring(i, i + 3)))) {
          finalDeck.add(c);
          break;
        }
      }
    }
    return finalDeck;
  }

  @Test
  public void testAssertValidMovePile() {
    KlondikeModel standard = KlondikeCreator.create(GameType.BASIC);
    standard.startGame(stackDeck(standard.getDeck(), "3♣A♣2♡2♣4♣A♠2♠3♠4♠A♡3♡4♡A♢2♢3♢4♢"),
        false, 2, 1);
    assertValidMovePile(standard, 1, 1, 0);
  }

  //shared tests

  /**
   * Apply several tests to a valid move pile command and ensure that it preserves and modifies
   * the correct properties.
   * @param postGameStartModel the model to run the tests on after calling start game
   * @param srcPile the pile to move from
   * @param numCards the number of cards to move
   * @param destPile the pile to move to
   */
  public void assertValidMovePile(KlondikeModel postGameStartModel,
      int srcPile,
      int numCards,
      int destPile) {
    int srcPileInitialHeight = postGameStartModel.getPileHeight(srcPile);
    int destPileInitialHeight = postGameStartModel.getPileHeight(destPile);
    List<Card> cardsToMove = IntStream
        .range(srcPileInitialHeight - numCards, srcPileInitialHeight)
        .mapToObj((int cardNum) -> postGameStartModel.getCardAt(srcPile, cardNum))
        .collect(Collectors.toList());
    postGameStartModel.movePile(srcPile, numCards, destPile);
    //assert move removes numCards from source pile
    Assert.assertEquals(srcPileInitialHeight - numCards,
        postGameStartModel.getPileHeight(srcPile));
    //assert move adds numCards to the destination pile
    Assert.assertEquals(destPileInitialHeight + numCards,
        postGameStartModel.getPileHeight(destPile));
    //assert the cards to move end up on top of the destination pile in order
    List<Card> topNumCardsOfDestination = IntStream
        .range(destPileInitialHeight, destPileInitialHeight + numCards)
        .mapToObj((int cardNum) -> postGameStartModel.getCardAt(destPile, cardNum))
        .collect(Collectors.toList());
    Assert.assertEquals(cardsToMove, topNumCardsOfDestination);
    //assert that the top card of the source pile is visible after move
    // (if the pile had at least numCards + 1 cards)
    Assert.assertTrue(srcPileInitialHeight <= numCards
        || postGameStartModel.isCardVisible(srcPile, srcPileInitialHeight - numCards - 1));
  }

  /**
   * Test that logically illegal move pile throws the correct exception.
   * @param postGameStartModel model to run on after calling start game
   * @param srcPile pile to move from
   * @param numCards number of cards to move
   * @param destPile pile to move to
   */
  public void assertIllegalMovePileThrows(KlondikeModel postGameStartModel,
      int srcPile,
      int numCards,
      int destPile) {
    List<Object> initialState = getGameState(postGameStartModel);
    Assert.assertThrows(IllegalStateException.class,
        () -> postGameStartModel.movePile(srcPile, numCards, destPile));
    List<Object> finalState = getGameState(postGameStartModel);
    Assert.assertEquals(initialState, finalState);
  }

  /**
   * Test each permutation of bad arguments to movePile and ensure they correctly throw.
   * @param preGameStartModel the model to test on, before calling startgame
   */
  public void assertBadArgsMovePileThrows(KlondikeModel preGameStartModel) {
    preGameStartModel.startGame(stackDeck(preGameStartModel.getDeck(), "A♣2♣3♣4♣"),
        false, 2, 1);
    List<Object> initialState = getGameState(preGameStartModel);
    // bad arguments
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.movePile(-1, 1, 0));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.movePile(0, -1, 1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.movePile(0, 1, -1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.movePile(0, 1, 0));
    // not enough cards
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.movePile(0, 2, 1));
    List<Object> finalState = getGameState(preGameStartModel);
    // no unwanted mutation
    Assert.assertEquals(initialState, finalState);
  }

  @Test
  public void testAssertValidMoveDraw() {
    KlondikeModel standard = KlondikeCreator.create(GameType.BASIC);
    standard.startGame(stackDeck(standard.getDeck(), "3♣A♣2♣2♡4♣A♡3♡4♡"), false, 2, 1);
    assertValidMoveDraw(standard, 0);
    standard = KlondikeCreator.create(GameType.BASIC);
    standard.startGame(stackDeck(standard.getDeck(), "A♣K♣2♣3♣4♣5♣6♣7♣8♣9♣10♣J♣Q♣"), false, 1, 3);
    standard.moveToFoundation(0, 0);
    assertValidMoveDraw(standard, 0);
  }

  /**
   * Test that a valid move draw changes what it should change
   * and preserves what it should preserve.
   * @param postGameStartModel the model to run on, with start game already called
   * @param destPile the pile to move to
   */
  public void assertValidMoveDraw(KlondikeModel postGameStartModel, int destPile) {
    int destPileInitialHeight = postGameStartModel.getPileHeight(destPile);
    List<Card> ogDrawCards = postGameStartModel.getDrawCards();
    Card cardToMove = ogDrawCards.get(0);
    postGameStartModel.moveDraw(destPile);
    //assert move changes draw cards
    Assert.assertNotEquals(ogDrawCards,
        postGameStartModel.getDrawCards());
    //assert move adds 1 card to the destination pile
    Assert.assertEquals(destPileInitialHeight + 1,
        postGameStartModel.getPileHeight(destPile));
    //assert the card to move ends up on top of the destination pile
    Assert.assertEquals(cardToMove, postGameStartModel.getCardAt(destPile, destPileInitialHeight));
  }

  /**
   * Test that logically illegal move draw throws the correct exception.
   * @param postGameStartModel model to run on with the game already started
   * @param destPile the pile to move to
   */
  public void assertIllegalMoveDrawThrows(KlondikeModel postGameStartModel, int destPile) {
    List<Object> initialState = getGameState(postGameStartModel);
    Assert.assertThrows(IllegalStateException.class,
        () -> postGameStartModel.moveDraw(destPile));
    List<Object> finalState = getGameState(postGameStartModel);
    Assert.assertEquals(initialState, finalState);
  }

  /**
   * Test that running move draw with any bad arguments correctly throws exception.
   * @param preGameStartModel The model to run on with the game NOT started
   */
  public void assertBadArgsMoveDrawThrows(KlondikeModel preGameStartModel) {
    preGameStartModel.startGame(stackDeck(preGameStartModel.getDeck(), "A♣2♣3♣4♣"),
        false, 2, 1);
    List<Object> initialState = getGameState(preGameStartModel);
    // bad arguments
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveDraw(-1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveDraw(2));
    List<Object> finalState = getGameState(preGameStartModel);
    // no unwanted mutation
    Assert.assertEquals(initialState, finalState);
  }

  @Test
  public void testAssertValidMoveToFoundation() {
    KlondikeModel standard = KlondikeCreator.create(GameType.BASIC);
    standard.startGame(stackDeck(standard.getDeck(), "A♣3♣2♣2♡4♣A♠2♠3♠4♠A♡3♡4♡A♢2♢3♢4♢"),
        false, 2, 1);
    assertValidMoveToFoundation(standard, 0, 0);
  }

  /**
   * Test that valid move to foundation preserves what should be preserved and modifies what should
   * be modified correctly.
   * @param postGameStartModel the model to run on with a game already started
   * @param srcPile the pile to move from
   * @param foundationPile the foundation pile to move to
   */
  public void assertValidMoveToFoundation(KlondikeModel postGameStartModel,
      int srcPile,
      int foundationPile) {
    int firstScore = postGameStartModel.getScore();
    int srcPileInitialHeight = postGameStartModel.getPileHeight(srcPile);
    Card cardToMove = postGameStartModel.getCardAt(srcPile, srcPileInitialHeight - 1);
    postGameStartModel.moveToFoundation(srcPile, foundationPile);
    //assert move to foundation increases score by 1
    Assert.assertEquals(firstScore + 1, postGameStartModel.getScore());
    //assert move to foundation removes 1 card from source pile
    Assert.assertEquals(srcPileInitialHeight - 1, postGameStartModel.getPileHeight(srcPile));
    //assert the top card of the source pile ends up as the top card of the foundation pile
    Assert.assertEquals(cardToMove, postGameStartModel.getCardAt(foundationPile));
    //assert the top card of the source pile has changed after move
    if (srcPileInitialHeight > 1) {
      Assert.assertNotEquals(cardToMove, postGameStartModel.getCardAt(srcPile,
          srcPileInitialHeight - 2));
    }
    //assert that the top card of the foundation pile is visible after move
    // (if the pile had more than 1 card)
    Assert.assertTrue(srcPileInitialHeight <= 1
        || postGameStartModel.isCardVisible(srcPile, srcPileInitialHeight - 2));
  }

  /**
   * Test that move to foundation with any invalid arguments throws the proper exception.
   * @param preGameStartModel the model to run on with the game NOT started
   */
  public void assertInvalidMoveToFoundationArgsThrows(KlondikeModel preGameStartModel) {
    preGameStartModel.startGame(stackDeck(baseDeck, "A♣"), false, 1, 1);
    List<Object> initialState = getGameState(preGameStartModel);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveToFoundation(-1, -1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveToFoundation(-1, 0));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveToFoundation(0, -1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveToFoundation(1, 0));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveToFoundation(0, 1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveToFoundation(1, 1));
    List<Object> finalState = getGameState(preGameStartModel);
    Assert.assertEquals(initialState, finalState);
  }

  /**
   * Test that logically illegal move to foundation calls throw the correct exception.
   * @param postGameStartModel the model to run on with a game already started
   * @param srcPile the pile to move from
   * @param foundationPile the pile to move to
   */
  public void assertLogicallyInvalidMoveToFoundationThrows(KlondikeModel postGameStartModel,
      int srcPile,
      int foundationPile) {
    List<Object> initialState = getGameState(postGameStartModel);
    Assert.assertThrows(IllegalStateException.class,
        () -> postGameStartModel.moveToFoundation(srcPile, foundationPile));
    List<Object> finalState = getGameState(postGameStartModel);
    Assert.assertEquals(initialState, finalState);
  }

  @Test
  public void testAssertValidDrawToFoundation() {
    KlondikeModel standard = KlondikeCreator.create(GameType.BASIC);
    standard.startGame(stackDeck(standard.getDeck(), "A♣3♣2♣A♠2♡4♣2♠3♠4♠A♡3♡4♡A♢2♢3♢4♢"),
        false, 2, 1);
    assertValidDrawToFoundation(standard, 0);
  }

  /**
   * Test that move draw to foundation with a valid move properly affects the state of the model.
   * @param postGameStartModel the model to run on with a game already started
   * @param foundationPile the foundation pile to move to
   */
  public void assertValidDrawToFoundation(KlondikeModel postGameStartModel, int foundationPile) {
    int firstScore = postGameStartModel.getScore();
    List<Card> initialDrawCards = postGameStartModel.getDrawCards();
    Card cardToMove = postGameStartModel.getDrawCards().get(0);
    postGameStartModel.moveDrawToFoundation(foundationPile);
    //assert draw to foundation increases score by 1
    Assert.assertEquals(firstScore + 1, postGameStartModel.getScore());
    //assert the top card of the draw pile ends up as the top card of the foundation pile
    Assert.assertEquals(cardToMove, postGameStartModel.getCardAt(foundationPile));
    //assert the draw pile has changed after move
    Assert.assertNotEquals(initialDrawCards, postGameStartModel.getDrawCards());
    //assert the number of draw cards has not increased
    Assert.assertTrue(postGameStartModel.getDrawCards().size()
        <= postGameStartModel.getNumDraw());
  }

  /**
   * Test to ensure that any illegal arguments  to draw to foundation correctly throw exceptions.
   * @param preGameStartModel The model to run on with a game NOT started
   */
  public void assertInvalidArgDrawToFoundationThrows(KlondikeModel preGameStartModel) {
    preGameStartModel.startGame(stackDeck(baseDeck, "A♣"), false, 1,
        1);
    //Assert illegal pile number throws correct exception
    List<Object> initialState = getGameState(preGameStartModel);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveDrawToFoundation(-1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> preGameStartModel.moveDrawToFoundation(1));
    List<Object> finalState = getGameState(preGameStartModel);
    Assert.assertEquals(initialState, finalState);
  }


  /**
   * Test to ensure that drawing from an empty foundation pile correctly throws.
   * @param preGameStartModel the model to run on with a game NOT started
   */
  public void assertEmptyDrawToFoundationThrows(KlondikeModel preGameStartModel) {
    preGameStartModel.startGame(stackDeck(preGameStartModel.getDeck(), "A♣"),
        false, 1, 1);
    List<Object> initialState = getGameState(preGameStartModel);
    //Assert moving from empty draw pile throws exception
    Assert.assertThrows(IllegalStateException.class,
        () -> preGameStartModel.moveDrawToFoundation(0));
    List<Object> finalState = getGameState(preGameStartModel);
    Assert.assertEquals(initialState, finalState);
  }

  /**
   * Test to ensure that logically invalid move correctly throws.
   * @param postGameStartModel the model to run on with a game NOT started
   */
  public void assertIllegalMoveDrawToFoundationThrows(KlondikeModel postGameStartModel,
      int foundationPile) {
    List<Object> initialState = getGameState(postGameStartModel);
    Assert.assertThrows(IllegalStateException.class,
        () -> postGameStartModel.moveDrawToFoundation(0));
    List<Object> finalState = getGameState(postGameStartModel);
    Assert.assertEquals(initialState, finalState);
  }

  /**
   * return a list containing all known information about the game
   * used to check that state hasn't changed from methods which threw exceptions.
   * @param postGameStartModel the game to check state of, with a game already started
   * @return A List containing all the properties of the object.
   *         The list is in no particular order and should only be used to compare with previous
   *         calls to getGameState
   */
  public List<Object> getGameState(KlondikeModel postGameStartModel) {
    List<Object> state = new ArrayList<>();
    state.add(postGameStartModel.getScore());
    state.add(postGameStartModel.isGameOver());
    state.add(postGameStartModel.getNumPiles());
    state.add(postGameStartModel.getNumRows());
    state.add(postGameStartModel.getNumFoundations());
    state.add(postGameStartModel.getDrawCards());
    for (int pile = 0; pile < postGameStartModel.getNumPiles(); pile++) {
      state.add(postGameStartModel.getPileHeight(pile));
      for (int card = 0; card < postGameStartModel.getPileHeight(pile); card++) {
        state.add(postGameStartModel.isCardVisible(pile, card));
        if (postGameStartModel.isCardVisible(pile, card)) {
          state.add(postGameStartModel.getCardAt(pile, card));
        }
      }
    }
    for (int foundation = 0; foundation < postGameStartModel.getNumFoundations(); foundation++) {
      state.add(postGameStartModel.getCardAt(foundation));
    }
    return state;
  }


  //Whitehead tests
  @Test
  public void testMovePileInvalidArgsWH() {
    initWhitehead();
    assertBadArgsMovePileThrows(model);
  }

  @Test
  public void testMovePileIllegalValueWH() {
    initWhitehead();
    model.startGame(baseDeck, false, 2, 1);
    assertIllegalMovePileThrows(model, 0, 1, 1);
  }

  @Test
  public void testMovePileIllegalColorWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "2♡2♣A♣A♡"), false, 2, 1);
    assertIllegalMovePileThrows(model, 0, 1, 1);
  }

  @Test
  public void testMovePileIllegalPileToMoveWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♣3♠4♠3♣4♣A♠2♠"),
        false, 2, 1);
    //set up pile 0: 3♠2♣, pile 1: 4♠
    model.moveToFoundation(0, 0);
    model.movePile(1, 1, 0);
    model.movePile(1, 1, 0);
    model.moveDraw(1);
    //try to move pile 0
    assertIllegalMovePileThrows(model, 0, 2, 1);
  }

  @Test
  public void testSingleCardToEmptyWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♣3♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertValidMovePile(model, 1, 1, 0);
  }

  @Test
  public void testSingleCardToNonEmptyWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "2♣3♣A♣"), false, 2, 1);
    assertValidMovePile(model, 1, 1, 0);
  }

  @Test
  public void testMoveMultipleCardsToEmptyWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "3♣2♣A♣"), false, 2, 1);
    assertValidMovePile(model, 1, 2, 0);
  }

  @Test
  public void testMoveMultipleCardsToNonEmptyWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣3♣2♣4♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    model.moveDraw(0);
    assertValidMovePile(model, 1, 2, 0);
  }

  @Test
  public void testMoveDrawBadArgsWH() {
    initWhitehead();
    assertBadArgsMoveDrawThrows(model);
  }

  @Test
  public void testDrawFromEmptyDrawPileWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣"), false, 1, 1);
    assertIllegalMoveDrawThrows(model, 0);
  }

  @Test
  public void testDrawToWrongColorWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "2♣A♡2♡A♣"), false, 1, 1);
    assertIllegalMoveDrawThrows(model, 0);
  }

  @Test
  public void testDrawToWrongValueWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "3♣A♣2♣"), false, 1, 1);
    assertIllegalMoveDrawThrows(model, 0);
  }

  @Test
  public void testDrawToEmptyWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♣"), false, 1, 1);
    model.moveToFoundation(0, 0);
    //move 2 to empty
    assertValidMoveDraw(model, 0);
  }
  
  @Test
  public void testDrawToNonEmptyWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "3♣2♣A♣"), false, 1, 1);
    assertValidMoveDraw(model, 0);
  }

  @Test
  public void testMoveToFoundationBadArgsWH() {
    initWhitehead();
    assertInvalidMoveToFoundationArgsThrows(model);
  }

  @Test
  public void testIllegalMoveToEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "3♣2♣A♣"), false, 1, 1);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testWrongValueToNonEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♣3♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testWrongSuitToNonEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♣2♡A♡"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testEmptyCascadeToFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♣3♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testValidMoveToEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♣3♣"), false, 2, 1);
    assertValidMoveToFoundation(model, 0, 0);
  }

  @Test
  public void testValidMoveToNonEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣3♣2♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertValidMoveToFoundation(model, 1, 0);
  }

  @Test
  public void testDrawToFoundationBadArgsWH() {
    initWhitehead();
    assertInvalidArgDrawToFoundationThrows(model);
  }

  @Test
  public void testIllegalDrawToEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣3♣2♣"), false, 1, 1);
    assertIllegalMoveDrawToFoundationThrows(model, 0);
  }

  @Test
  public void testWrongValueDrawToNonEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣3♣2♣"), false, 1, 1);
    model.moveToFoundation(0, 0);
    assertIllegalMoveDrawToFoundationThrows(model, 0);
  }

  @Test
  public void testWrongSuitDrawToNonEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♡2♣A♡"), false, 1, 1);
    model.moveToFoundation(0, 0);
    assertIllegalMoveDrawToFoundationThrows(model, 0);
  }

  @Test
  public void testEmptyDrawToFoundationWH() {
    initWhitehead();
    assertEmptyDrawToFoundationThrows(model);
  }

  @Test
  public void testValidDrawToEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♡2♣A♡"), false, 2, 1);
    assertValidDrawToFoundation(model, 0);
  }

  @Test
  public void testValidDrawToNonEmptyFoundationWH() {
    initWhitehead();
    model.startGame(stackDeck(baseDeck, "A♣2♡A♡2♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertValidDrawToFoundation(model, 0);
  }

  //limited draw tests

  @Test
  public void testIllegalMovePileToEmptyCascadeLD() {
    initLimitedDraw(1);
    model.startGame(stackDeck(baseDeck, "A♣2♡A♡2♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertIllegalMovePileThrows(model, 1, 1, 0);
  }

  @Test
  public void testDiscardDrawCards() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♡A♡2♣"), false, 2, 1);
    List<Card> drawCards = model.getDrawCards();
    for (int i = 0; i < 2 ; i++) {
      model.discardDraw();
    }
    Comparator<Card> byString = (Card c1, Card c2) -> c1.toString().compareTo(c2.toString());
    List<Card> remainingDrawCards = model.getDrawCards();
    drawCards.sort(byString);
    remainingDrawCards.sort(byString);
    Assert.assertEquals(drawCards, remainingDrawCards);
    model.discardDraw();
    remainingDrawCards = model.getDrawCards();
    remainingDrawCards.sort(byString);
    Assert.assertNotEquals(drawCards, remainingDrawCards);
  }

  @Test
  public void testMovePileInvalidArgsLD() {
    initLimitedDraw(2);
    assertBadArgsMovePileThrows(model);
  }

  @Test
  public void testMovePileIllegalValueLD() {
    initLimitedDraw(2);
    model.startGame(baseDeck, false, 2, 1);
    assertIllegalMovePileThrows(model, 0, 1, 1);
  }

  @Test
  public void testMovePileIllegalColorLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "2♡2♣A♣A♡"), false, 2, 1);
    assertIllegalMovePileThrows(model, 0, 1, 1);
  }

  @Test
  public void testSingleCardToNonEmptyLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "3♣A♡2♡A♣2♣3♡"), false, 2, 1);
    assertValidMovePile(model, 1, 1, 0);
  }

  @Test
  public void testMoveMultipleCardsToNonEmptyLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "3♣A♡2♡A♣2♣3♡"), false, 2, 1);
    model.moveDraw(1);
    assertValidMovePile(model, 1, 2, 0);
  }

  @Test
  public void testMoveDrawBadArgsLD() {
    initLimitedDraw(2);
    assertBadArgsMoveDrawThrows(model);
  }

  @Test
  public void testDrawFromEmptyDrawPileLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣"), false, 1, 1);
    assertIllegalMoveDrawThrows(model, 0);
  }

  @Test
  public void testDrawToWrongColorLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "2♣A♣A♡2♡"), false, 1, 1);
    assertIllegalMoveDrawThrows(model, 0);
  }

  @Test
  public void testDrawToWrongValueLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "3♣A♣2♣"), false, 1, 1);
    assertIllegalMoveDrawThrows(model, 0);
  }

  @Test
  public void testDrawToNonEmptyLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "3♣2♡2♣A♣3♡A♡"), false, 1, 1);
    assertValidMoveDraw(model, 0);
  }

  @Test
  public void testMoveToFoundationBadArgsLD() {
    initLimitedDraw(2);
    assertInvalidMoveToFoundationArgsThrows(model);
  }

  @Test
  public void testIllegalMoveToEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "3♣2♣A♣"), false, 1, 1);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testWrongValueToNonEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♣3♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testWrongSuitToNonEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♣2♡A♡"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testEmptyCascadeToFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♣3♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertLogicallyInvalidMoveToFoundationThrows(model, 0, 0);
  }

  @Test
  public void testValidMoveToEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♣3♣"), false, 2, 1);
    assertValidMoveToFoundation(model, 0, 0);
  }

  @Test
  public void testValidMoveToNonEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣3♣2♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertValidMoveToFoundation(model, 1, 0);
  }

  @Test
  public void testDrawToFoundationBadArgsLD() {
    initLimitedDraw(2);
    assertInvalidArgDrawToFoundationThrows(model);
  }

  @Test
  public void testIllegalDrawToEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣3♣2♣"), false, 1, 1);
    assertIllegalMoveDrawToFoundationThrows(model, 0);
  }

  @Test
  public void testWrongValueDrawToNonEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣3♣2♣"), false, 1, 1);
    model.moveToFoundation(0, 0);
    assertIllegalMoveDrawToFoundationThrows(model, 0);
  }

  @Test
  public void testWrongSuitDrawToNonEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♡2♣A♡"), false, 1, 1);
    model.moveToFoundation(0, 0);
    assertIllegalMoveDrawToFoundationThrows(model, 0);
  }

  @Test
  public void testEmptyDrawToFoundationLD() {
    initLimitedDraw(2);
    assertEmptyDrawToFoundationThrows(model);
  }

  @Test
  public void testValidDrawToEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♡2♣A♡"), false, 2, 1);
    assertValidDrawToFoundation(model, 0);
  }

  @Test
  public void testValidDrawToNonEmptyFoundationLD() {
    initLimitedDraw(2);
    model.startGame(stackDeck(baseDeck, "A♣2♡A♡2♣"), false, 2, 1);
    model.moveToFoundation(0, 0);
    assertValidDrawToFoundation(model, 0);
  }
}
