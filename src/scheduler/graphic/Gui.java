package scheduler.graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Gui extends JFrame {

    private static Gui shiftInstance;
    private static Gui normalInstance;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public JScrollPane jScrollPane;
    JPanel panel;

    public Gui(Integer dauer, int[][] resources, boolean shift) {
        this.panel = new GuiPanel(this, resources, dauer);
        initComponents();
        this.setVisible(true);

        if (shift) {
            setOpacity(1f);
            this.setTitle("shift");
            shiftInstance = this;
        } else {
            setOpacity(1f);
            this.setTitle("normal");
            normalInstance = this;
        }

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    shiftInstance.setVisible(false);
                    normalInstance.setVisible(true);
                } else if (e.getKeyCode() == KeyEvent.VK_F2) {
                    shiftInstance.setVisible(true);
                    normalInstance.setVisible(false);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });

    }

    private void initComponents() {

        jScrollPane = new JScrollPane();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.getHorizontalScrollBar().setUnitIncrement(100);
        jScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        jScrollPane.setViewportView(panel);

        getContentPane().add(jScrollPane);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        pack();
    }
}
