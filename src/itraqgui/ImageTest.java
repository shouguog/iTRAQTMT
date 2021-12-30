/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itraqgui;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
/**
 *
 * @author gaos2
 */
public class ImageTest {
      static public void main(String args[]) throws Exception {
        int width = 200, height = 200;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D ig2 = bi.createGraphics();
        ig2.fillRect(0, 0, width - 1, height - 1);

        BasicStroke stroke = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        ig2.setPaint(Color.lightGray);
        ig2.setStroke(stroke);
        ig2.draw(new Ellipse2D.Double(0, 0, 100, 100));
        ig2.drawString("I am here", 50, 30);
        ImageIO.write(bi, "GIF", new File("a.gif"));
  }
}
