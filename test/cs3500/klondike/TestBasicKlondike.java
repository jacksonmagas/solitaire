package cs3500.klondike;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.StandardCard;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test basic klondike model.
 */
public class TestBasicKlondike {
  List<Card> defaultDeck;
  KlondikeModel defaultGame;

  List<Card> doubleDeck;

  @Before
  public void init() {
    defaultGame = KlondikeCreator.create(GameType.BASIC);
    defaultDeck = defaultGame.getDeck();
    doubleDeck = new ArrayList<>(defaultDeck);
    doubleDeck.addAll(defaultDeck);
  }

  @Test
  public void testGetDeck() {
    init();
    Assert.assertTrue(defaultGame.getDeck().contains(new StandardCard("A", "spades")));
    Assert.assertTrue(defaultGame.getDeck().contains(new StandardCard("J", "hearts")));
  }

  @Test
  public void testStartGame() {
    init();
    Assert.assertTrue(Stream.of(null, new StandardCard("5", "spades")).anyMatch(Objects::isNull));

    Assert.assertThrows(IllegalArgumentException.class,
        () -> defaultGame.startGame(defaultDeck, false, 15, 2));
    defaultDeck.add(null);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> defaultGame.startGame(defaultDeck, false, 15, 2));
    init();
    defaultGame.startGame(defaultDeck, true, 7, 5);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> defaultGame.startGame(defaultDeck, false, 10, 2));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> defaultGame.startGame(defaultDeck, false, 10, 2));
    try {
      defaultGame.startGame(defaultDeck, false, 10, 2);
    } catch (Exception e) {
      Assert.assertThrows(IllegalArgumentException.class,
          () -> defaultGame.startGame(defaultDeck, false, 10, 2));
    }
  }

  @Test
  public void testNumFoundation() {
    init();
    defaultGame.startGame(defaultDeck, false, 7, 2);
    Assert.assertEquals(4, defaultGame.getNumFoundations());
    init();
    defaultGame.startGame(doubleDeck, true, 8, 2);
    Assert.assertEquals(8, defaultGame.getNumFoundations());
  }

  @Test
  public void testGetCardAtCascade() {
    init();
    defaultGame.startGame(defaultDeck, false, 3, 1);
    Assert.assertEquals(new StandardCard("A", "clubs"),
        defaultGame.getCardAt(0,0));
    Assert.assertEquals(new StandardCard("6", "clubs"),
        defaultGame.getCardAt(2,2));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> defaultGame.getCardAt(10,10));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> defaultGame.getCardAt(5,0));
  }

  @Test
  public void testGetCardAtFoundation() {
    init();
    defaultGame.startGame(defaultDeck, false, 3, 2);

    defaultGame.moveToFoundation(0,0);
    Assert.assertEquals(defaultGame.getCardAt(0),
        new StandardCard("A", "clubs"));
    //discard and draw cards until there is an ace on top
    int safety = 0;
    while (defaultGame.getDrawCards().get(0).getPointValue() != 1) {
      defaultGame.discardDraw();
      safety++;
      if (safety > 100) {
        break;
      }
    }
    Assert.assertThrows(IllegalStateException.class, () -> defaultGame.moveDrawToFoundation(0));
    defaultGame.moveDrawToFoundation(3);
    Assert.assertEquals(defaultGame.getCardAt(3),
        new StandardCard("A", "spade"));
  }

  @Test
  public void testGetScore() {
    init();
    defaultGame.startGame(defaultDeck, false, 3, 2);
    Assert.assertEquals(0, defaultGame.getScore());
  }
}
