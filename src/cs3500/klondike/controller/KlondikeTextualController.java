package cs3500.klondike.controller;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

import cs3500.klondike.view.KlondikeTextualView;
import cs3500.klondike.view.TextualView;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * A text based controller for playing a game of klondike solitaire from a model following the
 * KlondikeModel interface.
 */
public class KlondikeTextualController implements cs3500.klondike.controller.KlondikeController {
  private final Readable in;
  private final Appendable out;

  /**
   * Constructs a controller for a game of Klondike controlled and displayed using text.
   * @param r A non-null readable that is the source of text input
   * @param a A non-null appendable to which output from the game will be pushed
   * @throws IllegalArgumentException if either parameter is null
   */
  public KlondikeTextualController(Readable r, Appendable a) throws IllegalArgumentException {
    try {
      this.in = Objects.requireNonNull(r);
      this.out = Objects.requireNonNull(a);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("Both inputs must be non-null", e);
    }
  }

  /**
   * Append the given string to the output appendable handling IOExceptions.
   * @param s String to append
   * @throws IllegalStateException if the write to the appendable fails for any reason
   */
  private void output(String s) throws IllegalStateException {
    try {
      out.append(s).append(System.lineSeparator());
    } catch (IOException e) {
      throw new IllegalStateException("Could not write to appendable", e);
    }
  }

  /**
   * Waits until the user enters a valid parameter for their move or quits.
   * A valid parameter is an integer, q or Q
   * @param scan A scanner reading from the input
   * @return An optional containing an integer if the user entered an integer, and an empty
   *         optional if they want to quit.
   * @throws IllegalStateException if the readable can't pass more inputs.
   */
  private Optional<Integer> waitValidParameters(Scanner scan) {
    // Regex for matching natural number, q or Q
    final Pattern intPattern = Pattern.compile("^[0-9]*$");
    final Pattern quitPattern = Pattern.compile("^q$|^Q$");
    while (scan.hasNext()) {
      String next = scan.next();
      if (intPattern.matcher(next).matches()) {
        return Optional.of(Integer.valueOf(next));
      } else if (quitPattern.matcher(next).matches()) {
        return Optional.empty();
      }
    }
    throw new IllegalStateException("Readable can't pass more input.");
  }

  /**
   * The primary method for beginning and playing a game.
   *
   * @param model The game of solitaire to be played
   * @param deck The deck of cards to be used
   * @param shuffle Whether to shuffle the deck or not
   * @param numPiles How many piles should be in the initial deal
   * @param numDraw How many draw cards should be visible
   * @throws IllegalArgumentException if the model is null
   * @throws IllegalStateException if the game cannot be started,
   *          or if the controller cannot interact with the player.
   */
  @Override
  public void playGame(KlondikeModel model,
      List<Card> deck, boolean shuffle, int numPiles, int numDraw) {
    if (model == null) {
      throw new IllegalArgumentException("The model cannot be null");
    }
    try {
      model.startGame(deck, shuffle, numPiles, numDraw);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("The game cannot be started");
    }
    TextualView view = new KlondikeTextualView(model, out);
    Scanner scan = new Scanner(in);
    boolean quitGame = false;
    while (!model.isGameOver() && !quitGame) {
      //Transmit game state to appendable
      try {
        view.render();
      } catch (IOException e) {
        throw new IllegalStateException("Appendable unable to transmit view", e);
      }
      //output score
      output("Score: " + model.getScore());

      int[] params = new int[3];
      Optional<Integer> userInput;
      if (!scan.hasNext()) {
        throw new IllegalStateException("No more input in the readable.");
      }
      switch (scan.next()) {
        case "mpp":
          // wait for 3 parameters, if any are q break out of loop to end of game
          for (int curParam = 0; curParam < 3; curParam ++) {
            userInput = waitValidParameters(scan);
            if (userInput.isEmpty()) {
              quitGame = true;
              break;
            }
            params[curParam] = userInput.get() - 1;
          }
          if (!quitGame) {
            try {
              model.movePile(params[0], params[1] + 1, params[2]);
            } catch (IllegalArgumentException e) {
              output("Invalid move. Play again. There is no source pile at the given location "
                  + "with enough cards to move, or there is no distinct destination pile at the "
                  + "given location.");
            } catch (IllegalStateException e) {
              output("Invalid move. Play again. That move is not allowable.");
            }
          }
          break;
        case "md":
          userInput = waitValidParameters(scan);
          if (userInput.isEmpty()) {
            quitGame = true;
          } else {
            params[0] = userInput.get() - 1;
            try {
              model.moveDraw(params[0]);
            } catch (IllegalArgumentException e) {
              output("Invalid move. Play again. There is no destination pile "
                  + "at the given location.");
            } catch (IllegalStateException e) {
              output("Invalid move. Play again. There are no draw cards or "
                  + "that move is not allowable.");
            }
          }
          break;
        case "mpf":
          // wait for 2 parameters, if any are q break out of loop to end of game
          for (int curParam = 0; curParam < 2; curParam ++) {
            userInput = waitValidParameters(scan);
            if (userInput.isEmpty()) {
              quitGame = true;
              break;
            }
            params[curParam] = userInput.get() - 1;
          }
          if (!quitGame) {
            try {
              model.moveToFoundation(params[0], params[1]);
            } catch (IllegalArgumentException e) {
              output("Invalid move. Play again. One of the pile numbers is invalid.");
            } catch (IllegalStateException e) {
              output("Invalid move. Play again. The source pile is empty or "
                  + "that move is not allowable.");
            }
          }
          break;
        case "mdf":
          userInput = waitValidParameters(scan);
          if (userInput.isEmpty()) {
            quitGame = true;
          } else {
            params[0] = userInput.get() - 1;
            try {
              model.moveDrawToFoundation(params[0]);
            } catch (IllegalArgumentException e) {
              output("Invalid move. Play again. There is no destination pile "
                  + "at the given location.");
            } catch (IllegalStateException e) {
              output("Invalid move. Play again. There are no draw cards or "
                  + "that move is not allowable.");
            }
          }
          break;
        case "dd":
          try {
            model.discardDraw();
          } catch (IllegalStateException e) {
            output("Invalid move. Play again. There are no more draw cards to discard.");
          }
          break;
        case "q":
        case "Q":
          quitGame = true;
          break;
        default:
          output("Invalid move. Play again. Commands must be mpp, md, mpf, mdf, or dd");
          break;
      }
    }
    if (quitGame) {
      output("Game quit!");
      output("State of game when quit:");
      try {
        view.render();
      } catch (IOException e) {
        throw new IllegalStateException("Appendable unable to transmit view", e);
      }
      //output score
      output("Score: " + model.getScore());
    } else {
      try {
        view.render();
      } catch (IOException e) {
        throw new IllegalStateException("Could not write to appendable.");
      }
      if (model.getScore() == deck.size()) {
        output("You win!");
      } else {
        output("Game over. Score: " + model.getScore());
      }
    }
  }
}
