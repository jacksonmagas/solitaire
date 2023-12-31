package cs3500.klondike.view;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import java.io.IOException;
import java.util.List;

/**
 * A simple text-based rendering of the Klondike game.
 */
public class KlondikeTextualView implements TextualView {
  private final KlondikeModel model;
  private Appendable out;

  public KlondikeTextualView(KlondikeModel model) {
    this.model = model;
  }

  public KlondikeTextualView(KlondikeModel model, Appendable out) {
    this.model = model;
    this.out = out;
  }

  /**
   * Returns a string that can be used to view the game of solitaire.
   * Below is an example game rendering. Note that the output does not end with a newline.
   * Draw: 8♣, 6♢, K♡
   * Foundation: &lt;none&gt;, A♡, 3♢, &lt;none&gt;
   *  A♣  ?  X  ?  ?  ?  ?
   *     2♠     ?  ?  ?  ?
   *            ?  ?  ?  ?
   *           5♡  ?  ?  ?
   *              6♡  ?  ?
   *                 7♢  ?
   *                    7♠
   * @return the formatted String
   */
  public String toString() {
    StringBuilder result = new StringBuilder("Draw: " + renderDrawCards() + "\n");
    result.append("Foundation: ").append(renderFoundationPiles()).append("\n");
    for (int i = 0; i < model.getNumRows(); i++) {
      result.append(renderRow(i)).append("\n");
    }
    result.delete(result.length() - 1, result.length());
    return result.toString();
  }

  private String renderDrawCards() {
    List<Card> drawCards = model.getDrawCards();
    return drawCards.stream().map(Card::toString).reduce(
        (String s1, String s2) -> s1 + ", " + s2).orElse("");
  }

  private String renderFoundationPiles() {
    StringBuilder foundationTops = new StringBuilder();
    for (int i = 0; i < model.getNumFoundations(); i++) {
      try {
        Card c = model.getCardAt(i);
        String toAppend = c.toString();
        foundationTops.append(toAppend);
      } catch (IllegalArgumentException | NullPointerException e) {
        foundationTops.append("<none>");
      }
      foundationTops.append(", ");
    }
    if (foundationTops.length() != 0) {
      foundationTops.delete(foundationTops.length() - 2, foundationTops.length());
    }
    return foundationTops.toString();
  }

  /**
   * Render the given row or the model as a string.
   * @param rowNum zero indexed from top
   * @return rendered row
   */
  private String renderRow(int rowNum) {
    StringBuilder row = new StringBuilder();
    for (int i = 0; i < model.getNumPiles(); i++) {
      if (rowNum < model.getPileHeight(i)) {
        if (model.isCardVisible(i, rowNum)) {
          row.append(leftPadToThree(model.getCardAt(i, rowNum).toString()));
        } else {
          row.append(leftPadToThree("?"));
        }
      } else if (rowNum == 0) {
        row.append(leftPadToThree("X"));
      } else {
        row.append("   ");
      }
    }
    return row.toString();
  }

  /**
   * Left pads the given string with spaces until it is at least 3 characters long.
   * @param str string to left pad
   * @return padded string
   */
  private String leftPadToThree(String str) {
    StringBuilder result = new StringBuilder().append(str);
    while (result.length() < 3) {
      result.insert(0, " ");
    }
    return result.toString();
  }

  @Override
  public void render() throws IOException {
    out.append(this.toString()).append(System.lineSeparator());
  }
}
