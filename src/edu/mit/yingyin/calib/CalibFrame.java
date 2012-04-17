package edu.mit.yingyin.calib;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.mit.yingyin.util.SystemUtil;

public class CalibFrame extends JFrame implements KeyListener {
  private class ImagePanel extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;

    private static final int OVAL_WIDTH = 10;

    private BufferedImage myimg = null;
    private GeoCalibModel model;

    public ImagePanel(CalibFrame imf, Dimension d, GeoCalibModel icm) {
      super();
      setLayout(null);
      setPreferredSize(d);
      this.model = icm;
      addMouseListener(this);
      setOpaque(false);
    }

    public void setImage(BufferedImage img) {
      this.myimg = img;
      repaint();
    }

    public void update(Graphics g) {
      paint(g);
    }

    public void update() {
      // Validates this container and all of its subcomponents.
      // The validate method is used to cause a container to lay out its
      // subcomponents again.
      // It should be invoked when this container's subcomponents are modified
      // (added to or removed from
      // the container, or layout-related information changed) after the 
      // container has been displayed.
      validate();
      repaint();
    }

    public void paint(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
      if (myimg != null)
        g2d.drawImage(myimg, null, 0, 0);

      List<Point> pts = model.getImagePoints();

      g2d.setColor(Color.RED);
      for (Point p : pts) {
        if (model.isScrnCoord())
          SwingUtilities.convertPointFromScreen(p, this);

        g2d.drawOval(p.x - OVAL_WIDTH / 2, p.y - OVAL_WIDTH / 2, OVAL_WIDTH, 
            OVAL_WIDTH);
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      Point p = e.getPoint();

      // left click
      if ((e.getModifiersEx() | InputEvent.BUTTON1_DOWN_MASK) == 
          InputEvent.BUTTON1_DOWN_MASK) {
        // Convert a point from a component's coordinate system to screen
        // coordinates.
        if (model.isScrnCoord())
          SwingUtilities.convertPointToScreen(p, this);

        model.addImagePoint(p);
        repaint();
      }

      // right click
      if ((e.getModifiersEx() | InputEvent.BUTTON3_DOWN_MASK) == 
          InputEvent.BUTTON3_DOWN_MASK) {
        model.removeLastPoint();
        repaint();
      }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {}
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent arg0) {}
    
    @Override
    public void mouseExited(MouseEvent arg0) {}
  }
  
  private static final long serialVersionUID = -6672495506940884693L;

  ImagePanel ip;
  GeoCalibModel model;

  public CalibFrame(GeoCalibModel icm) {
    super("Calibration Pattern");
    setUndecorated(true);
    setResizable(false);

    this.model = icm;
    BufferedImage bi = icm.getImage();

    ip = new ImagePanel(this, new Dimension(bi.getWidth(), bi.getHeight()), 
                        icm);

    ip.setImage(bi);
    getContentPane().add(ip);
    addKeyListener(this);
    
    Dimension screenSize = SystemUtil.getVirtualScreenBounds().getSize();
    int xLoc = (screenSize.width - bi.getWidth()) / 2;
    int yLoc = screenSize.height - bi.getHeight();
    xLoc = xLoc < 0 ? 0 : xLoc;
    yLoc = yLoc < 0 ? 0 : yLoc;
    this.setLocation(xLoc, yLoc);
  }

  public void showUI() {
    pack();
    setVisible(true);
  }

  public void setStatus(String status) {}

  @Override
  public void keyPressed(KeyEvent ke) {
    switch (ke.getKeyCode()) {
    case KeyEvent.VK_S:
      String fileName = (String)JOptionPane.showInputDialog(this, "File name:", 
          "Save as", JOptionPane.PLAIN_MESSAGE, null, null, 
          model.getPointsFileName());
      model.saveImagePoints(fileName);
      break;

    case KeyEvent.VK_P:
      model.createPoints();
      ip.update();
      break;

    case KeyEvent.VK_C:
      model.clearPoints();
      ip.update();
      break;

    default:
      break;
    }
  }

  @Override
  public void keyReleased(KeyEvent arg0) {}

  @Override
  public void keyTyped(KeyEvent arg0) {}

}
