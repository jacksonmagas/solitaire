package cs3500.klondike;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Examplar test class for KlondikeModel interface.
 */
public class ExamplarModelTests {

  /**
   * Takes a deck of cards and returns the deck corresponding to the given string.
   * @param baseDeck A deck of cards which contains at least every card wanted in the final deck.
   *                 (Can contain more cards then needed)
   * @param deck A string of card names in the order in which they should appear in the deck.
   *             For example "A♣2♣3♣4♣A♠2♠3♠4♠A♡2♡3♡4♡A♢2♢3♢4♢"
   *             10 of any suit is not supported (and will cause bugs)
   * @return the stacked deck
   * @throws IllegalArgumentException if the string is not a valid deck, or the string contains a
   *                                  card not in the input deck
   */
  private List<Card> stackDeck(List<Card> baseDeck, String deck) {
    List<Card> finalDeck = new ArrayList<Card>();
    for (int i = 0; i < deck.length(); i += 2) {
      for (Card c : baseDeck) {
        if (c.toString().equals(deck.substring(i, i + 2))) {
          finalDeck.add(c);
          break;
        }
      }
    }
    return finalDeck;
  }

  @Test
  public void testMoveToFoundation() {
    KlondikeModel test = KlondikeCreator.create(GameType.BASIC);
    List<Card> newDeck = stackDeck(test.getDeck(),"A♣2♣3♣4♣A♠2♠3♠4♠A♡2♡3♡4♡A♢2♢3♢4♢");
    Card aceOfClubs = newDeck.get(0);
    test.startGame(newDeck, false, 1, 1);
    test.moveToFoundation(0,0);
    Assert.assertEquals(test.getCardAt(0), aceOfClubs);
  }

  @Test
  public void testDrawToFoundation() {
    KlondikeModel test = KlondikeCreator.create(GameType.BASIC);
    List<Card> newDeck = stackDeck(test.getDeck(),"A♣2♣3♣4♣A♠2♠3♠4♠A♡2♡3♡4♡A♢2♢3♢4♢");
    Card twoOfClubs = newDeck.get(1);
    Card threeOfClubs = newDeck.get(2);
    test.startGame(newDeck, false, 1, 1);
    test.moveToFoundation(0,0);
    test.moveDrawToFoundation(0);
    //does the draw card end up in the foundation pile
    Assert.assertEquals(test.getCardAt(0),twoOfClubs);
    //does the draw pile draw a new card?
    Assert.assertEquals(test.getDrawCards().get(0), threeOfClubs);
  }

  @Test
  public void testDrawToCascadePile() {
    KlondikeModel test = KlondikeCreator.create(GameType.BASIC);
    List<Card> newDeck = stackDeck(test.getDeck(),"3♣2♢4♣A♣A♠2♠3♠4♠A♡2♡3♡4♡A♢2♣3♢4♢");
    Card twoOfDiamonds = newDeck.get(1);
    test.startGame(newDeck, false, 1, 1);
    test.moveDraw(0);
    //does the card get moved to the correct pile?
    Assert.assertEquals(twoOfDiamonds, test.getCardAt(0,1));
    //is the card still in the original location?
    Assert.assertNotEquals(test.getDrawCards().get(0), twoOfDiamonds);
    //does an illegal move throw an exception?
    Assert.assertThrows(IllegalStateException.class, () -> test.moveDraw(0));
  }

  @Test
  public void testValidMovePile() {
    KlondikeModel test = KlondikeCreator.create(GameType.BASIC);
    List<Card> newDeck = stackDeck(test.getDeck(),"3♣2♢4♡4♣A♣A♠2♠3♠4♠A♡2♡3♡A♢2♣3♢4♢");
    test.startGame(newDeck, false, 2, 1);
    test.movePile(0, 1, 1);
    Assert.assertEquals(test.getPileHeight(1), 3);
  }

  @Test
  public void testInvalidMovePile() {
    KlondikeModel test = KlondikeCreator.create(GameType.BASIC);
    List<Card> newDeck = stackDeck(test.getDeck(),"3♣2♢4♣4♡A♣A♠2♠3♠4♠A♡2♡3♡A♢2♣3♢4♢");
    test.startGame(newDeck, false, 2, 1);
    Assert.assertThrows(Exception.class,() -> test.movePile(1,1, 0));
  }

  @Test
  public void testDiscard() {
    KlondikeModel test = KlondikeCreator.create(GameType.BASIC);
    List<Card> newDeck = stackDeck(test.getDeck(),"A♣A♢A♡A♠");
    Card aceOfHearts = newDeck.get(2);
    test.startGame(newDeck, false, 1, 2);
    test.discardDraw();
    Assert.assertEquals(test.getDrawCards().get(0), aceOfHearts);

    //does discarding from an empty deck throw an exception
    KlondikeModel small = KlondikeCreator.create(GameType.BASIC);
    List<Card> ittyBittyDeck = stackDeck(test.getDeck(), "A♣");
    small.startGame(ittyBittyDeck, false, 1, 1);
    Assert.assertThrows(Exception.class, small::discardDraw);
  }

  @Test
  public void testIsGameOverNoLegalMoves() {
    KlondikeModel small = KlondikeCreator.create(GameType.BASIC);
    List<Card> ittyBittyDeck = stackDeck(small.getDeck(), "2♣A♢2♢A♣");
    small.startGame(ittyBittyDeck, false, 2, 1);
    Assert.assertFalse(small.isGameOver());
    small.moveDrawToFoundation(0);
    small.moveToFoundation(0,0);
    Assert.assertTrue(small.isGameOver());
  }

  @Test
  public void testDrawCards() {
    KlondikeModel test = KlondikeCreator.create(GameType.BASIC);
    List<Card> newDeck = stackDeck(test.getDeck(),"A♣2♣3♣4♣A♠2♠3♠4♠A♡2♡3♡4♡A♢2♢3♢4♢");
    test.startGame(newDeck, false, 4, 1);
    Assert.assertEquals(test.getDrawCards().remove(0), test.getDrawCards().get(0));
  }
}
