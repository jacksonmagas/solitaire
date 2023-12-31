package cs3500.klondike.controller;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock of a Klondike model to check that the model is receiving the correct inputs.
 */
public class MockKlondike implements KlondikeModel {
  private final StringBuilder log;

  public MockKlondike(StringBuilder log) {
    this.log = log;
  }

  @Override
  public List<Card> getDeck() {
    log.append("getDeck called");
    return new ArrayList<Card>();
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
      throws IllegalArgumentException, IllegalStateException {
    log.append("startGame called:\ndeck = ");
    for (Card c : deck) {
      log.append(c.toString());
    }
    log.append("\nshuffle = ").append(shuffle);
    log.append(String.format("\nnumPiles = %d", numPiles));
    log.append(String.format("\nnumDraw = %d", numDraw));
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile)
      throws IllegalArgumentException, IllegalStateException {
    log.append("movePile called:\n")
        .append(String.format("srcPile = %d\n", srcPile))
        .append(String.format("numCards = %d\n", numCards))
        .append(String.format("destPile = %d", destPile));
  }

  @Override
  public void moveDraw(int destPile) throws IllegalArgumentException, IllegalStateException {
    log.append(String.format("moveDraw called:\ndestPile = %d", destPile));
  }

  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
      throws IllegalArgumentException, IllegalStateException {
    log.append("moveToFoundation called:\n")
        .append(String.format("srcPile = %d\n", srcPile))
        .append(String.format("foundationPile = %d", foundationPile));
  }

  @Override
  public void moveDrawToFoundation(int foundationPile)
      throws IllegalArgumentException, IllegalStateException {
    log.append("moveDrawToFoundation called:\n")
        .append(String.format("foundationPile = %d", foundationPile));
  }

  @Override
  public void discardDraw() throws IllegalStateException {
    log.append("discardDraw called");
  }

  @Override
  public int getNumRows() throws IllegalStateException {
    log.append("getNumRows called");
    return 0;
  }

  @Override
  public int getNumPiles() throws IllegalStateException {
    log.append("getNumPiles called");
    return 0;
  }

  @Override
  public int getNumDraw() throws IllegalStateException {
    log.append("getNumDraw called");
    return 0;
  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    log.append("isGameOver called");
    return false;
  }

  @Override
  public int getScore() throws IllegalStateException {
    log.append("getScore called");
    return 0;
  }

  @Override
  public int getPileHeight(int pileNum) throws IllegalArgumentException, IllegalStateException {
    log.append("getPileHeight called:")
        .append(String.format("\npileNum = %d", pileNum));
    return 0;
  }

  @Override
  public boolean isCardVisible(int pileNum, int card)
      throws IllegalArgumentException, IllegalStateException {
    log.append("isCardVisible called:")
        .append(String.format("\npileNum = %d", pileNum))
        .append(String.format("\ncard = %d", card));
    return false;
  }

  @Override
  public Card getCardAt(int pileNum, int card)
      throws IllegalArgumentException, IllegalStateException {
    log.append("getCardAt called:")
        .append(String.format("\npileNum = %d", pileNum))
        .append(String.format("\ncard = %d", card));
    return null;
  }

  @Override
  public Card getCardAt(int foundationPile) throws IllegalArgumentException, IllegalStateException {
    log.append("getCardAt called:")
        .append(String.format("\nfoundationPile = %d", foundationPile));
    return null;
  }

  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    log.append("getDrawCards called");
    return new ArrayList<>();
  }

  @Override
  public int getNumFoundations() throws IllegalStateException {
    log.append("getNumFoundations called");
    return 0;
  }
}
