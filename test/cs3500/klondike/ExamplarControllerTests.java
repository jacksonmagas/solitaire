package cs3500.klondike;

import cs3500.klondike.controller.BadAppendable;
import cs3500.klondike.controller.BadReadable;
import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.controller.MockKlondike;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import cs3500.klondike.view.KlondikeTextualView;
import cs3500.klondike.view.TextualView;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains examplar tests for a klondike controller, as well as tests written for the examplar
 * assignment which caught no chaffs but are useful test cases.
 */
public class ExamplarControllerTests {
  private StringBuilder log;
  private KlondikeModel mock;
  private StringBuilder output;
  private KlondikeModel realModel;
  private TextualView realView;


  @Before
  public void init() {
    log = new StringBuilder();
    mock = new MockKlondike(log);
    output = new StringBuilder();
    TextualView mockView = new KlondikeTextualView(mock);
    realModel = KlondikeCreator.create(GameType.BASIC);
    realView = new KlondikeTextualView(realModel);
  }

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

  //Useful tests but caught no chaffs
  @Test
  public void testNullControllerConstructor() {
    init();
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new KlondikeTextualController(null, output));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new KlondikeTextualController(new StringReader(""), null));
  }

  @Test
  public void testPlayGameNullModel() {
    init();
    Readable input = new StringReader("");
    KlondikeController controller = new KlondikeTextualController(input, output);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> controller.playGame(null, mock.getDeck(), false, 0, 0));
  }

  @Test
  public void testPlayGameIllegalState() {
    init();
    KlondikeController controller = new KlondikeTextualController(new BadReadable(),
        new BadAppendable());
    Assert.assertThrows(IllegalStateException.class,
        () -> controller.playGame(mock, mock.getDeck(), false, 0, 0));
  }

  @Test
  public void testBasicMockStartsAndFinishes() {
    init();
    Readable input = new StringReader("q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(mock, mock.getDeck(), false, 0, 0);
    Assert.assertTrue("The above compiled with no errors.", true);
  }

  @Test
  public void testImmediateEndOfGame() {
    init();
    Readable input = new StringReader("q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, realModel.getDeck(), false, 3, 2);
    String viewString = realView.toString();
    int viewSize = viewString.split("\n").length;
    String[] outputLines = output.toString().split("\n");
    StringBuilder startMessage = new StringBuilder();
    for (int line = 0; line < viewSize + 1; line ++) {
      startMessage.append(outputLines[line]);
    }

    StringBuilder quitMessage = new StringBuilder();
    for (int line = viewSize + 1; line < outputLines.length; line ++) {
      quitMessage.append(outputLines[line]);
    }
    Assert.assertTrue(output.toString().contains(viewString));
    Assert.assertTrue(startMessage.toString().contains("Score: 0"));
    Assert.assertTrue(quitMessage.toString().contains("Game quit!"));
    Assert.assertTrue(quitMessage.toString().contains("State of game when quit:"));
    Assert.assertTrue(quitMessage.toString().contains("Score: 0"));
    init();
    input = new StringReader("Q");
    controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, realModel.getDeck(), false, 3, 2);
    viewString = realView.toString();
    Assert.assertTrue(output.toString().contains("Score: 0"));
    Assert.assertTrue(output.toString().contains("Game quit!"));
    Assert.assertTrue(output.toString().contains("State of game when quit:"));
    Assert.assertTrue(output.toString().contains(viewString));
  }

  @Test
  public void testControllerToModel() {
    init();
    Readable input = new StringReader("mpp 4 2 5 "
        + "md 2 "
        + "mpf 6 3 "
        + "mdf 10 "
        + "dd "
        + "q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(mock, mock.getDeck(), false, 5, 2);
    Assert.assertTrue(log.toString().contains("movePile called:"));
    Assert.assertTrue(log.toString().contains("srcPile = 3"));
    Assert.assertTrue(log.toString().contains("numCards = 2"));
    Assert.assertTrue(log.toString().contains("destPile = 4"));
    Assert.assertTrue(log.toString().contains("moveDraw called:"));
    Assert.assertTrue(log.toString().contains("destPile = 1"));
    Assert.assertTrue(log.toString().contains("moveToFoundation called:"));
    Assert.assertTrue(log.toString().contains("srcPile = 5"));
    Assert.assertTrue(log.toString().contains("foundationPile = 2"));
    Assert.assertTrue(log.toString().contains("moveDrawToFoundation called:"));
    Assert.assertTrue(log.toString().contains("foundationPile = 9"));
    Assert.assertTrue(log.toString().contains("discardDraw called"));
  }

  @Test
  public void testInvalidInputFollowedByValidInput() {
    init();
    Readable input = new StringReader("mdf 1 q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, stackDeck(realModel.getDeck(),"2♢A♢2♣A♣"),
        false, 2, 1);
    Assert.assertTrue(output.toString().contains("Score: 1"));
    String correctInputs = output.toString();
    init();
    input = new StringReader("mdf l 1 q");
    controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, stackDeck(realModel.getDeck(),"2♢A♢2♣A♣"),
        false, 2, 1);
    Assert.assertTrue(output.toString().contains("Score: 1"));
    Assert.assertNotEquals(correctInputs, output.toString());
  }

  @Test
  public void testWinGame() {
    init();
    Readable input = new StringReader("mpf 1 1 q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, stackDeck(realModel.getDeck(),"A♢"),
        false, 1, 1);
    Assert.assertTrue(output.toString().contains("You win!"));
  }

  @Test
  public void testLoseGame() {
    init();
    Readable input = new StringReader("mdf 1 mpf 1 1 q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, stackDeck(realModel.getDeck(),"2♢A♣2♣A♢"),
        false, 2, 1);
    Assert.assertTrue(output.toString().contains("Game over."));
    Assert.assertTrue(output.toString().contains("Score: 1"));
  }

  @Test
  public void testValidMovePile() {
    init();
    Readable input = new StringReader("mpp 2 1 1 q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, stackDeck(realModel.getDeck(),"2♢A♢A♣2♣"),
        false, 2, 1);
    String viewString = realView.toString();
    Assert.assertEquals(realModel.getPileHeight(0), 2);
  }

  @Test
  public void testInvalidMovePIle() {
    init();
    Readable input = new StringReader("mpp 1 1 2 q");
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(realModel, stackDeck(realModel.getDeck(),"2♢A♢A♣2♣"),
        false, 2, 1);
    Assert.assertTrue(output.toString().contains("Invalid move. Play again."));
  }
}
