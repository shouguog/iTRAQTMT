package itraqgui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.Vector;
import java.awt.*;
import javax.swing.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.math3.distribution.TDistribution;

public class volcanoPicture extends JFrame {

  String pictureDescription;
  Hashtable pictureData;
  Vector vtPictureData;
  Vector vtHeader;
  JTable jTableExpression;
  String[] lineSpots = {};
  public volcanoLabel label;

  public volcanoPicture(Hashtable hashExpression, String pictureDes, String type) throws HeadlessException {
    super("volcano plot");
    pictureDescription = pictureDes;
    pictureData        = hashExpression;
    label = new volcanoLabel(pictureData, pictureDescription, lineSpots);
    this.getContentPane().setBackground(Color.white);
//Top Add label
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setDividerLocation(400);
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.add(label,JSplitPane.TOP);

//    this.getContentPane().add(label, new XYConstraints(25, 10,450,360));
//
    vtPictureData = new Vector(1);
    vtHeader      = new Vector(1);
    vtHeader.addElement("Gene Name");
    Enumeration enumElement = pictureData.elements();

    int intHyb = ((Vector)enumElement.nextElement()).size();
    for(int i=0; i<intHyb; i++){
      vtHeader.addElement("Exp" + (i+1));
    }

    Enumeration enumKey = pictureData.keys();
    while(enumKey.hasMoreElements()){
      Vector vtLine = new Vector(1);
      String key = (String)enumKey.nextElement();
      vtLine.addElement(key);
      Vector vtLineExpression = (Vector) pictureData.get(key);
      for(int i=0; i<vtLineExpression.size(); i++){
        vtLine.addElement(vtLineExpression.get(i));
      }
      vtPictureData.addElement(vtLine);
    }
    jTableExpression = new JTable(vtPictureData, vtHeader);
    jTableExpression.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    JScrollPane pane = new JScrollPane();
    pane.getViewport().add(jTableExpression);
//
    //
    
    JButton jButtonFindSelect = new JButton("Save the Data");
    jButtonFindSelect.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            int state = chooser.showSaveDialog(null);
            chooser.setSelectedFile(new File((Data.expressionFileName).replaceAll(".txt", "") + "_protein.txt"));
            File file = chooser.getSelectedFile();
            String s = "CANCELED";
            if(state == JFileChooser.APPROVE_OPTION)
                s = file.getPath();
               //    JOptionPane.showMessageDialog(null, s);
            try{
                BufferedOutputStream bufOut=new BufferedOutputStream(new FileOutputStream(s));
                DataOutputStream dataOut = new DataOutputStream(bufOut);
                dataOut.writeBytes("Channel\tPC1\tPC2\n");
                Enumeration enumKey = pictureData.keys();
                while(enumKey.hasMoreElements()){
                    String key = (String)enumKey.nextElement();
                    Vector vtLineExpression = (Vector) pictureData.get(key);
                    dataOut.writeBytes(key+"\t"+vtLineExpression.get(0)+"\t"+vtLineExpression.get(1)+"\n");
                }
               bufOut.close();
               dataOut.close();}
             catch(Exception e1){
               System.out.println("can not create file" + e1.getMessage());
            }
            return;
        }
    });

    JButton jButtonSave = new JButton("Save the Figure");
    jButtonSave.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int width = 450, height = 350;
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, 450, 350);
    g.setColor(Color.black);
    g.fillRect(30, 30, 400, 3);
    g.fillRect(30, 30, 3, 300);
    g.fillRect(30, 330, 400, 3);
    g.fillRect(430, 30, 3, 300);
    double doubleYmax = -100.0;
    double doubleYmin = 100.0;
    double doubleXmax = -100.0;
    double doubleXmin = 100.0;
    for (int i = 0; i < vtPictureData.size(); i++) {
        Vector vtLine = (Vector) vtPictureData.get(i);
        try{
            //The first one is label
            if (doubleYmax < Double.parseDouble( (String) vtLine.get(2)))
              doubleYmax = Double.parseDouble( (String) vtLine.get(2));
            if (doubleYmin > Double.parseDouble( (String) vtLine.get(2)))
              doubleYmin = Double.parseDouble( (String) vtLine.get(2));
            if (doubleXmax < Double.parseDouble( (String) vtLine.get(1)))
              doubleXmax = Double.parseDouble( (String) vtLine.get(1));
            if (doubleXmin > Double.parseDouble( (String) vtLine.get(1)))
              doubleXmin = Double.parseDouble( (String) vtLine.get(1));
        }catch(Exception ex){}
    }
    System.out.println("doubleYmax:"+doubleYmax);
    System.out.println("doubleYmin:"+doubleYmin);
    System.out.println("doubleXmax:"+doubleXmax);
    System.out.println("doubleXmin:"+doubleXmin);
    //Draw Coordinates
    g.fillRect(30, 80, 10, 3);
    g.fillRect(30, 280, 10, 3);
    g.fillRect(30, 180, 10, 3);
    System.out.println(doubleXmax+"\t"+doubleXmin+"\t"+doubleYmax+"\t"+doubleYmin);
    g.setColor(Color.black);
    g.drawString((Double.toString(doubleYmax)).substring(0,1), 0, 80);
    g.drawString((Double.toString(doubleYmin)).substring(0,1), 0, 280);
    g.drawString((Double.toString(doubleXmin)).substring(0,1), 28, 360);
    g.drawString((Double.toString(doubleXmax)).substring(0,1), 400, 360);
    //int intYStart = new Double(200 *((Double.parseDouble( (String) vtLine.get(j))) - doubleYmin) / (doubleYmax - doubleYmin)).intValue();
    //g.fillOval(28 + j * intervalHyb, 278 - intYStart,5,5);
    for (int i = 0; i < vtPictureData.size(); i++) {
        Vector vtLine = (Vector) vtPictureData.get(i);
        int intYStart = new Double(200 *((Double.parseDouble( (String) vtLine.get(2))) - doubleYmin) / (doubleYmax - doubleYmin)).intValue();
        int intXStart = new Double(200 *((Double.parseDouble( (String) vtLine.get(1))) - doubleXmin) / (doubleXmax - doubleXmin)).intValue();
        if(Double.parseDouble( (String) vtLine.get(2))<-2)
            g.setColor(Color.red);
        else
            g.setColor(Color.black);
        g.fillOval(28 + intXStart, 278 - intYStart,3,3);
    }
    g.setColor(Color.black);
    g.setFont(new java.awt.Font("Dialog", 0, 14));
    g.drawString(pictureDescription, 60, 370);
            BasicStroke stroke = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g.setPaint(Color.lightGray);
            g.setStroke(stroke);
            try{
                JFileChooser chooser = new JFileChooser();
                int state = chooser.showSaveDialog(null);
                File file = chooser.getSelectedFile();
                String s = "CANCELED";
                if(state == JFileChooser.APPROVE_OPTION)
                    s = file.getPath();
                //ImageIO.write(bi, "GIF", new File(s));
                SetDPI4TIFF.Main(bi, file.getPath());
            }catch(Exception ex){
                ;
            } catch (Throwable ex) {
                Logger.getLogger(volcanoPicture.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
    });

    //Bottom add table and buttons
    JPanel bottom = new JPanel();
    bottom.setLayout(new BorderLayout());
    bottom.add(pane, BorderLayout.CENTER);

    JPanel south = new JPanel();
    south.setLayout(new GridLayout(1,2));
    south.add(jButtonFindSelect);
    south.add(jButtonSave);
    bottom.add(south, BorderLayout.SOUTH);

    jSplitPane1.add(bottom, JSplitPane.BOTTOM);
  //Add to frame
    this.getContentPane().add(jSplitPane1);

  }

  public void setlineSpots(String[] spot){
    lineSpots = spot;
  }
  //Close the dialog
  void cancel() {
    dispose();
  }
  public static void main(String[] args) throws HeadlessException {
            Hashtable hashSample = new Hashtable();
            TDistribution tDist = new TDistribution(7);
            Random fRandom = new Random();
            double MEAN = 0.0f; 
            double VARIANCE = 2.0f;
            for(int i= 0; i<80; i++){
                double ratio=MEAN + fRandom.nextGaussian() * VARIANCE;
                Vector line = new Vector(1);
                line.addElement(String.valueOf(ratio+fRandom.nextGaussian()*0.5));
                line.addElement(String.valueOf(Math.log10(Math.min(tDist.cumulativeProbability(ratio), 1-tDist.cumulativeProbability(ratio)))));
                hashSample.put("pep:" + i, line);
            }
            System.out.println(hashSample);
            volcanoPicture volcanoPicture1  = new volcanoPicture(hashSample, "Just a test", "Peptide");
            volcanoPicture1.label.repaint();
            volcanoPicture1.setLocation(50, 50);
            volcanoPicture1.setSize(500, 700);
            volcanoPicture1.setVisible(true);
  }
}
