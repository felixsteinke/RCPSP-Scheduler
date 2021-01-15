package scheduler.graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class GuiPanel extends JPanel {

    private final JFrame parent;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int[][] resources;
    int[] resultArray;
    int dauer;
    int maxRes;
    int scaleHeight;
    int scaleWidth;

    public GuiPanel(JFrame parent, int[][] resources, int dauer) {
        this.parent = parent;
        this.scaleWidth = 30 * (dauer + 5);
        initComponents();
        this.resources = resources;
        this.dauer = dauer;
        this.maxRes = this.getMaxRes();
        this.scaleHeight = ((screenSize.height - 20) / (resources[0].length)) / maxRes;

        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.darkGray);//Background Color
        g.fillRect(0, 0, scaleWidth, parent.getHeight()); //Background

        for (int i = 1; i <= resources[0].length; i++) {
            paintRaster(i, g);
        }
        for (int i = 1; i <= resources[0].length; i++) {
            paintGraph(g, i);
        }
    }

    private int getMaxRes() {
        int runNr = 0;
        int maxresult = 0;
        for (int[] res : resources) {
            for (int i : res) {
                if (i > maxresult) {
                    maxresult = i;
                }
            }
            runNr++;
            if (runNr > this.dauer) break;
        }
        return maxresult;
    }

    private int getMaxRes(int resultResNr) {
        int runNr = 0;
        int maxresult = 0;
        for (int[] res : resources) {
            int resNr = 1;
            for (int i : res) {
                if (i > maxresult && resNr == resultResNr) {
                    maxresult = i;
                }
                resNr++;
            }
            runNr++;
            if (runNr > this.dauer) break;
        }
        return maxresult;
    }

    private void paintUpRect(int x, int y, int with, int height, Graphics g) {
        g.fillRect(x, this.getHeight() - height - y, with, height);
    }

    private void paintRaster(int currentRow, Graphics g) {
        g.setColor(Color.black);
        for (int i = 0; i < maxRes * scaleHeight; i += scaleHeight) {
            g.drawLine(0, ((screenSize.height - 20) / 4 * (currentRow - 1)) + i + 6, scaleWidth, ((screenSize.height - 20) / 4 * (currentRow - 1)) + i + 6); // I dont know why 6 ...
        }
    }

    private void paintGraph(Graphics g, int currentRow) {
        if (currentRow > this.resources[0].length || currentRow == 0) {
            throw new IllegalArgumentException("rowcount to large data not parent or 0");
        }
        switch (currentRow) {
            case 1:
                g.setColor(Color.BLUE);
                break;
            case 2:
                g.setColor(Color.cyan);
                break;
            case 3:
                g.setColor(Color.green);
                break;
            case 4:
                g.setColor(Color.yellow);
                break;
        }


        int runNr = 0;
        for (int[] res : resources) {
            int resNr = 1;
            for (int unUsed : res) {
                // System.out.println("at Run "+runNr+" with Recource "+resNr+": "+unUsed+" left");
                if (resNr == currentRow) {
                    paintUpRect(runNr * 30, (((currentRow - 1) * ((screenSize.height - 20) / this.resources[0].length))), 29, (getMaxRes(currentRow) - unUsed) * scaleHeight, g);
                }
                resNr++;
            }
            runNr++;
            if (runNr > this.dauer) break;
        }

        g.setColor(Color.black);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.black);
        Graphics2D g3 = (Graphics2D) g.create();
        g3.setStroke(new BasicStroke(4));
        g3.setColor(Color.red);
        int r;
        switch (currentRow) {
            case 1:
                r = 4;
                g3.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight, this.getWidth(), (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight));
                g2.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r, this.getWidth(), (screenSize.height - 20) / 4 * r));
                break;
            case 2:
                r = 3;
                g3.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight, this.getWidth(), (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight));
                g2.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r, this.getWidth(), (screenSize.height - 20) / 4 * r));
                break;
            case 3:
                r = 2;
                g3.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight, this.getWidth(), (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight));
                g2.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r, this.getWidth(), (screenSize.height - 20) / 4 * r));
                break;
            case 4:
                r = 1;
                g3.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight, this.getWidth(), (screenSize.height - 20) / 4 * r - getMaxRes(currentRow) * scaleHeight));
                g2.draw(new Line2D.Float(0, (screenSize.height - 20) / 4 * r, this.getWidth(), (screenSize.height - 20) / 4 * r));
                break;
        }
        g2.dispose();
        g3.dispose();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, scaleWidth, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, this.screenSize.height - 20, Short.MAX_VALUE)
        );
    }

}
