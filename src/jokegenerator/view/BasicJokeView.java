package jokegenerator.view;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import java.util.List;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class BasicJokeView extends JFrame implements JokeView {

  private final JTextArea textPanel;
  private final JButton jokeRequestBotton;
  private final List<JokeViewListener> listeners;

  /**
   * Construct a Swing Frame View that has the given width and height.
   * @param width is requested width of the frame
   * @param height is requested height of the frame
   * @throws IllegalArgumentException if any of the input is non-posivite
   */
  public BasicJokeView(int width, int height) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("The input width and height must be positive.\n");
    }

    this.listeners = new ArrayList<>();

    this.textPanel = new JTextArea("nothing yet~");
    this.textPanel.setLineWrap(true);
    this.add(this.textPanel);

    this.jokeRequestBotton = new JButton();
    this.jokeRequestBotton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (JokeViewListener listener : BasicJokeView.this.listeners) {
          listener.randomJokeRequested();
        }
      }
    });

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.pack();
    this.setVisible(true);
  }


  @Override
  public void setOutputText(String text) {
    textPanel.setText(text);
  }


  @Override
  public void refresh() {
    this.repaint();
  }


  public void addListener(JokeViewListener listener) {
    this.listeners.add(listener);
  }
}
