package jokegenerator.view;

public interface JokeViewListener {

  /**
   * A user has requested the generation for a random joke,
   * without any keyword or constraint.
   */
  void randomJokeRequested();
}
