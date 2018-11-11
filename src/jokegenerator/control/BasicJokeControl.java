package jokegenerator.control;

import jokegenerator.model.JokeModel;
import jokegenerator.view.JokeView;
import jokegenerator.view.JokeViewListener;

public class BasicJokeControl implements JokeControl, JokeViewListener {
  private JokeModel model;
  private JokeView view;
  private int counter = 0;

  public BasicJokeControl(JokeModel model, JokeView view) {
    this.model = model;
    this.view = view;
    view.addListener(this);
  }

  @Override
  public void run() {
    this.view.makeVisible();
  }

  @Override
  public void randomJokeRequested() {
    this.counter += 1;
    this.view.setOutputText(this.model.generateJoke());
    this.view.refresh();
  }
}
