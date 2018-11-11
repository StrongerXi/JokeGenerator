package jokegenerator.view;


public interface JokeView {

  /**
   * Set the output text that JokeView will display.
   */
  void setOutputText(String text);

  /**
   * Add the given listener so that any specified event will
   * alert the listener.
   */
  void addListener(JokeViewListener listener);

  /**
   * Refresh the view.
   */
  void refresh();
}
