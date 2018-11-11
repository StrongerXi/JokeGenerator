
import jokegenerator.model.JokeModel;
import jokegenerator.model.JokeModelImpl;
import jokegenerator.view.BasicJokeView;
import jokegenerator.view.JokeView;
import jokegenerator.control.JokeControl;
import jokegenerator.control.BasicJokeControl;

public class JokeGenerator {

  public static void main(String[] args) {

    JokeView view = new BasicJokeView(500, 500);
    JokeModel model = new JokeModelImpl();
    JokeControl control = new BasicJokeControl(model, view);
    control.run();
  }
}
