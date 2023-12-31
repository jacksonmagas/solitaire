package cs3500.klondike.view;

import java.io.IOException;

/**
 * A textual view is a class which can render a model.
 */
public interface TextualView {
  /**
   * Renders a model in some manner (e.g. as text, or as graphics, etc.).
   * @throws IOException if the rendering fails for some reason
   */
  void render() throws IOException;
}
