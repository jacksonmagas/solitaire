package cs3500.klondike;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Main Klondike class to allow playing Basic, Limited Draw, or Whitehead Klondike from the
 * command line.
 */
public final class Klondike {

  /**
   * Main method for playing klondike in the command line.
   * @param argv list of command line arguments
   *             First argument MUST be one of "basic", "limited", or "whitehead"
   *             If the first argument is "limited" then the second argument MUST be an integer
   *             representing the number of cards to draw.
   *             After the required argument(s) there are 2 more optional integer arguments.
   *             The first is the number of cascade piles to play with
   *             The second is the number of draw cards to show at a time.
   */
  public static void main(String[] argv) {
    int numCascades = 7;
    int numDraw = 3;
    int maxRedraw;
    int readIndex = 0;
    final int maxPiles = 9;
    if (argv.length == 0) {
      throw new IllegalArgumentException("Needs at least one argument");
    }
    HashMap<String, GameType> gameTypes = new HashMap<>();
    gameTypes.put("basic", GameType.BASIC);
    gameTypes.put("limited", GameType.LIMITED);
    gameTypes.put("whitehead", GameType.WHITEHEAD);
    GameType gametype = gameTypes.get(argv[readIndex]);
    readIndex++;
    if (gametype == null) {
      throw new IllegalArgumentException("Must specify a valid game type");
    } else if (gametype == GameType.LIMITED) {
      try {
        maxRedraw = Integer.parseInt(argv[readIndex]) - 1;
        readIndex++;
        KlondikeCreator.setNumRedraws(maxRedraw);
      } catch (NumberFormatException | IndexOutOfBoundsException e) {
        throw new IllegalArgumentException("limited must be followed by a valid integer");
      }
    }

    if (readIndex < argv.length) {
      try {
        numCascades = Math.min(Math.max(1, Integer.parseInt(argv[readIndex])), maxPiles);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("P argument must a valid integer if included");
      }
      readIndex++;
      if (readIndex < argv.length) {
        try {
          numDraw = Math.max(Integer.parseInt(argv[readIndex]), 1);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("D argument must a valid integer if included");
        }
      }
    }

    KlondikeModel model = KlondikeCreator.create(gametype);
    KlondikeController controller = new KlondikeTextualController(
        new InputStreamReader(System.in),
        System.out);
    controller.playGame(model, model.getDeck(), true, numCascades, numDraw);
  }
}
