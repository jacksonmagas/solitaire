package cs3500.klondike.view;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.view.KlondikeTextualView;
import cs3500.klondike.view.TextualView;
import org.junit.Test;
import org.junit.Assert;

/**
 * Tests the viewer, including by printing to console.
 */
public class TestView {
  @Test
  public void testNewGame() {
    KlondikeModel game = new BasicKlondike();
    TextualView gameViewer = new KlondikeTextualView(game);
    game.startGame(game.getDeck(), false, 7, 3);
    String now = gameViewer.toString();
    System.out.println(now);
    game.moveToFoundation(0,0);
    String then = gameViewer.toString();
    System.out.println(gameViewer);
    Assert.assertNotEquals(now, then);
    game.movePile(5, 1, 0);
    System.out.println(gameViewer);
    game.moveToFoundation(2,1);
    System.out.println(gameViewer);
    game.discardDraw();
    System.out.println(gameViewer);
    game.discardDraw();
    System.out.println(gameViewer);
    game.moveDraw(3);
    System.out.println(gameViewer);
    game.discardDraw();
    System.out.println(gameViewer);
    game.moveDraw(1);
    System.out.println(gameViewer);
    game.moveDraw(2);
    System.out.println(gameViewer);
    game.moveDraw(4);
    System.out.println(gameViewer);
    game.movePile(1,2,4);
    System.out.println(gameViewer);
    game.moveDraw(5);
    System.out.println(gameViewer);
    game.movePile(2,2,5);
    System.out.println(gameViewer);
    game.moveToFoundation(1,0);
    System.out.println(gameViewer);
    game.moveToFoundation(2,0);
    System.out.println(gameViewer);
  }
}
