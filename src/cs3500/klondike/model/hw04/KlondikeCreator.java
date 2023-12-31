package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * A factory class for creating Klondike Models. Number of redraws defaults to 2
 * but can be customized.
 */
public class KlondikeCreator {
  private static int numTimesRedrawAllowed = 2;

  /**
   * Enum containing each type of KlondikeModel this factory can create.
   */
  public enum GameType {
    BASIC, LIMITED, WHITEHEAD;
  }

  /**
   * Create a new KlondikeModel of the appropriate type.
   * @param type enum for choosing which klondikeModel to create
   * @return the KlondikeModel
   */
  public static KlondikeModel create(GameType type) {
    KlondikeModel product;
    switch (type) {
      case BASIC:
        product = new BasicKlondike();
        break;
      case LIMITED:
        product = new LimitedDrawKlondike(numTimesRedrawAllowed);
        break;
      case WHITEHEAD:
        product = new WhiteheadKlondike();
        break;
      default:
        throw new RuntimeException("How on earth have you managed to get here?");
    }
    return product;
  }

  /**
   * Change the number of redraws from its default of 2.
   * @param numRedraws the new number of redraws allowed.
   */
  public static void setNumRedraws(int numRedraws) {
    numTimesRedrawAllowed = numRedraws;
  }
}
