package cs3500.klondike.controller;

import java.io.IOException;

/**
 * Appendable class that always throws IOExceptions for testing purposes.
 */
public class BadAppendable implements Appendable {
  @Override
  public Appendable append(CharSequence csq) throws IOException {
    throw new IOException();
  }

  @Override
  public Appendable append(CharSequence csq, int start, int end) throws IOException {
    throw new IOException();
  }

  @Override
  public Appendable append(char c) throws IOException {
    throw new IOException();
  }
}
