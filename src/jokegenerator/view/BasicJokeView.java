package jokegenerator.view;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.FlowLayout;
import javax.swing.BoxLayout;

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
    this.textPanel.setEditable(false);
    this.textPanel.setLineWrap(true);
    this.textPanel.setWrapStyleWord(true);
    // this.textPanel.setPreferredSize(new Dimension(width * 2 / 3, height / 2));
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
    this.add(this.jokeRequestBotton);

    this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.setPreferredSize(new Dimension(width, height));
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.pack();
  }


  @Override
  public void setOutputText(String text) {
    this.textPanel.setText(text);
  }


  @Override
  public void refresh() {
    this.repaint();
  }


  @Override
  public void addListener(JokeViewListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void makeVisible() {
    this.setVisible(true);
  }
}
