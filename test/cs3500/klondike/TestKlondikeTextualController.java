package cs3500.klondike;

import cs3500.klondike.controller.BadAppendable;
import cs3500.klondike.controller.BadReadable;
import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.DeckBuilder;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.StandardCard;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A class to test klondike controller. Includes a main method for playing the game in terminal.
 */
public class TestKlondikeTextualController {
  private KlondikeModel model;
  private Appendable out;
  private Readable in;
  List<Card> empty;
  List<Card> hasNull;
  List<Card> closeToWin;
  KlondikeController controller;

  /**
   * Main method for playing klondike using the textual controller.
   * @param argv Required command line arguments are number of aces to play with,
   *             whether to shuffle, number of piles, and number of draw cards to show
   */
  public static void main(String[] argv) {
    KlondikeModel model = new BasicKlondike();
    KlondikeController controller = new KlondikeTextualController(new InputStreamReader(System.in),
        System.out);
    List<Card> suit1 = model.getDeck().subList(0, 13);
    List<Card> suit2 = model.getDeck().subList(13, 26);
    List<Card> suit3 = model.getDeck().subList(26, 39);
    List<Card> suit4 = model.getDeck().subList(39, 52);
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < Integer.parseInt(argv[0]); i++) {
      switch (i % 4) {
        case 1:
          deck.addAll(suit1);
          break;
        case 2:
          deck.addAll(suit2);
          break;
        case 3:
          deck.addAll(suit3);
          break;
        default:
          deck.addAll(suit4);
          break;
      }
    }
    controller.playGame(model, deck, Boolean.parseBoolean(argv[1]),
        Integer.parseInt(argv[2]),
        Integer.parseInt(argv[3]));
  }

  /**
   * Initialize test data for controller tests.
   */
  @Before
  public void init() {
    model = new BasicKlondike();
    out = new StringBuilder();
    empty = new ArrayList<>();
    hasNull = new ArrayList<>();
    hasNull.add(new StandardCard("A", "hearts"));
    hasNull.add(null);
    closeToWin = new DeckBuilder().setMax(2).addSuit("hearts").addSuit("clubs").getDeck();
  }

  @Test
  public void testConstructorException() {
    init();
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new KlondikeTextualController(null, out));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new KlondikeTextualController(in, null));
  }

  @Test
  public void testIOExceptions() {
    init();
    Appendable badOut = new BadAppendable();
    Readable badIn = new BadReadable();
    Assert.assertThrows(IllegalStateException.class,
        () -> new KlondikeTextualController(badIn, out)
            .playGame(model, model.getDeck(), false, 5, 5));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new KlondikeTextualController(in, badOut)
            .playGame(model, model.getDeck(), false, 5, 5));
  }

  @Test
  public void testValidMovePile() {
    init();
    in = new StringReader("mpp 2 1 1 q");
    controller = new KlondikeTextualController(in, out);
    // set up 2 visible in one pile and ace visible in another or different suits
    closeToWin.add(2, closeToWin.get(0));
    closeToWin.remove(0);
    controller.playGame(model, closeToWin, false, 2, 2);
    Assert.assertFalse(out.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testInValidMovePile() {
    init();
    in = new StringReader("mpp 1 1 2 q");
    controller = new KlondikeTextualController(in, out);
    // set up 2 visible in one pile and ace visible in another or different suits
    closeToWin.add(2, closeToWin.get(0));
    closeToWin.remove(0);
    controller.playGame(model, closeToWin, false, 2, 2);
    Assert.assertTrue(out.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testInValidCommand() {
    init();
    in = new StringReader("mp 1 1 2 q");
    controller = new KlondikeTextualController(in, out);
    // set up 2 visible in one pile and ace visible in another or different suits
    closeToWin.add(2, closeToWin.get(0));
    closeToWin.remove(0);
    controller.playGame(model, closeToWin, false, 2, 2);
    Assert.assertTrue(out.toString()
        .contains("Invalid move. Play again. Commands must be mpp, md, mpf, mdf, or dd"));
  }

  @Test
  public void testGameOverNotWin() {
    init();
    in = new StringReader("");
    List<Card> deck = new DeckBuilder().addSuit("spades").setMax(3).getDeck();
    deck.add(0, deck.remove(1));
    controller = new KlondikeTextualController(in, out);
    controller.playGame(model, deck, false, 2, 1);
    Assert.assertTrue(out.toString().contains("Game over. Score: 0"));
  }
}
