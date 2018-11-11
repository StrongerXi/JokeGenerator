
import jokegenerator.model.JokeModel;
import jokegenerator.model.BasicJokeModel;
import jokegenerator.view.BasicJokeView;
import jokegenerator.view.JokeView;
import jokegenerator.control.JokeControl;
import jokegenerator.control.BasicJokeControl;


public class JokeGenerator {

  public static void main(String[] args) {

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        JokeView view = new BasicJokeView(500, 500);
        JokeModel model = new BasicJokeModel();
        JokeControl control = new BasicJokeControl(model, view);
        control.run();
      }
    });
  }
}
