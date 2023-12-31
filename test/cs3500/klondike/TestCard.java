package cs3500.klondike;

import cs3500.klondike.model.hw02.StandardCard;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test Standard Card implementation and methods.
 */
public class TestCard {
  @Test
  public void testConstructorExceptions() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new StandardCard("11","hearts"));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new StandardCard("K", "heartless"));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new StandardCard(null, null));
    //Check to make sure valid constructors do not throw exceptions.
    try {
      new StandardCard("K", "hearts");
      new StandardCard("10", "♣");
      new StandardCard("A", "Club");
    } catch (Exception e) {
      throw new AssertionError("A valid constructor threw an error.");
    }
  }

  @Test
  public void testChooseSuit() {
    Assert.assertEquals(StandardCard.chooseSuit("Hearts"), '♡');
    Assert.assertEquals(StandardCard.chooseSuit("heart"), '♡');
    Assert.assertEquals(StandardCard.chooseSuit("♡"), '♡');
    Assert.assertEquals(StandardCard.chooseSuit("Spade"), '♠');
    Assert.assertEquals(StandardCard.chooseSuit("spades"), '♠');
    Assert.assertEquals(StandardCard.chooseSuit("♠"), '♠');
    Assert.assertEquals(StandardCard.chooseSuit("Diamonds"), '♢');
    Assert.assertEquals(StandardCard.chooseSuit("diamond"), '♢');
    Assert.assertEquals(StandardCard.chooseSuit("♢"), '♢');
    Assert.assertEquals(StandardCard.chooseSuit("Club"), '♣');
    Assert.assertEquals(StandardCard.chooseSuit("clubs"), '♣');
    Assert.assertEquals(StandardCard.chooseSuit("♣"), '♣');
    Assert.assertThrows(IllegalArgumentException.class,
        () -> StandardCard.chooseSuit("Hello World"));
  }

  @Test
  public void testToString() {
    StandardCard aceOfHearts = new StandardCard("a", "hearts");
    StandardCard tenOfClubs = new StandardCard("10", "clubs");
    StandardCard fourOfDiamonds = new StandardCard("4", "diamonds");

    Assert.assertEquals(aceOfHearts.toString(), "A♡");
    Assert.assertEquals(tenOfClubs.toString(), "10♣");
    Assert.assertEquals(fourOfDiamonds.toString(), "4♢");
  }

  @Test
  public void testEqualsAndHashCode() {
    StandardCard aceOfHearts = new StandardCard("A", "hearts");
    StandardCard anotherAceOfHearts = new StandardCard("A", "hearts");
    StandardCard tenOfHearts = new StandardCard("10", "hearts");
    StandardCard aceOfDiamonds = new StandardCard("A", "diamonds");

    Assert.assertEquals(aceOfHearts, anotherAceOfHearts);
    Assert.assertNotEquals(aceOfHearts, tenOfHearts);
    Assert.assertNotEquals(aceOfHearts, aceOfDiamonds);
    Assert.assertNotEquals("A♡", aceOfHearts);

    Assert.assertEquals(aceOfHearts.hashCode(), anotherAceOfHearts.hashCode());
    Assert.assertNotEquals(aceOfHearts.hashCode(), tenOfHearts.hashCode());
    Assert.assertNotEquals(aceOfHearts.hashCode(), aceOfDiamonds.hashCode());
    Assert.assertNotEquals("A♡".hashCode(), aceOfHearts.hashCode());
  }

  @Test
  public void testCompareValues() {
    StandardCard aceOfHearts = new StandardCard("A", "hearts");
    StandardCard eightOfHearts = new StandardCard("8", "hearts");
    StandardCard tenOfHearts = new StandardCard("10", "hearts");
    StandardCard aceOfDiamonds = new StandardCard("A", "diamonds");

    Assert.assertEquals(0, aceOfHearts.compareValue(aceOfDiamonds));
    Assert.assertTrue(eightOfHearts.compareValue(aceOfHearts) > 0);
    Assert.assertTrue(eightOfHearts.compareValue(tenOfHearts) < 0);
  }
}
