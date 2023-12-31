package cs3500.klondike.controller;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * Readable class that always throws IOException for testing purposes.
 */
public class BadReadable implements Readable {

  @Override
  public int read(CharBuffer cb) throws IOException {
    throw new IOException();
  }
}
