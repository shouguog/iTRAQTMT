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
import java.util.logging.Level;
import java.util.logging.Logger;
import javastat.multivariate.PCA;
import javastat.util.DataManager;
import javax.imageio.ImageIO;

public class expressionProfilePicture extends JFrame {

  String pictureDescription;
  Hashtable pictureData;
  Vector vtPictureData;
  Vector vtHeader;
  Vector vtChanelName;
  JTable jTableExpression;
  String[] lineSpots = {};
  public expressionProfileLabel label;

  public expressionProfilePicture(Hashtable hashExpression, String pictureDes) throws HeadlessException {
    super("Expression Profiles");
    pictureDescription = pictureDes;
    pictureData        = hashExpression;
    label = new expressionProfileLabel(pictureData, pictureDescription, lineSpots);
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
    vtChanelName      = new Vector(1);
    vtHeader.addElement("Channel");
    Enumeration enumElement = pictureData.elements();

    int intHyb = ((Vector)enumElement.nextElement()).size();
    for(int i=0; i<intHyb; i++){
      vtHeader.addElement("PC" + (i+1));
    }

    Enumeration enumKey = pictureData.keys();
    while(enumKey.hasMoreElements()){
      Vector vtLine = new Vector(1);
      String key = (String)enumKey.nextElement();
      vtLine.addElement(key);
      vtChanelName.add(key);
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
            chooser.setSelectedFile(new File((Data.expressionFileName).replaceAll(".txt", "") + "_PCA.txt"));
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
    System.out.println(vtChanelName);
    System.out.println(vtPictureData);
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
        //g.fillOval(,5,5);
        g.drawString((String)vtChanelName.get(i), 28 + intXStart, 278 - intYStart);
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
//                ImageIO.write(bi, "GIF", new File(s));
                SetDPI4TIFF.Main(bi, file.getPath());
            }catch(Exception ex){
                ;
            } catch (Throwable ex) {
                Logger.getLogger(expressionProfilePicture.class.getName()).log(Level.SEVERE, null, ex);
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
        double[][] testscores = { {36, 62, 31, 76, 46, 12, 39, 30, 22, 9, 32, 40, 64, 36, 24, 50, 42, 2, 56, 59, 28, 19, 36, 54, 14},
                                {36, 62, 31, 76, 46, 12, 39, 30, 22, 9, 32, 40, 64, 36, 24, 50, 42, 2, 56, 59, 28, 19, 36, 54, 15},
                             {58, 54, 42, 78, 56, 42, 46, 51, 32, 40, 49, 62, 75, 38, 46, 50, 42, 35, 53, 72, 50, 46, 56, 57, 35},
                             {43, 50, 41, 69, 52, 38, 51, 54, 43, 47, 54, 51, 70, 58, 44, 54, 52, 32, 42, 70, 50, 49, 56, 59, 38},
                             {36, 46, 40, 66, 56, 38, 44, 42, 28, 30, 37, 40, 66, 62, 55, 52, 38, 22, 40, 66, 42, 40, 54, 62, 20},
                             {37, 52, 29, 81, 40, 28, 51, 42, 22, 24, 52, 49, 63, 62, 49, 51, 50, 16, 32, 62, 63, 30, 52, 58, 20},
                             {36, 46, 40, 66, 56, 38, 54, 52, 28, 30, 37, 40, 66, 62, 55, 52, 38, 22, 40, 66, 42, 40, 54, 62, 29},
                             {37, 52, 29, 81, 40, 28, 41, 32, 22, 24, 52, 49, 63, 62, 49, 51, 50, 16, 32, 62, 63, 30, 32, 68, 29}};
        PCA testclass = new PCA();
        double[][] principalComponents = testclass.principalComponents(testscores);
        double[] variance = testclass.variance(testscores);
        for(int i=0; i<principalComponents.length; i++){
            System.out.println(principalComponents[0][i]+"\t"+principalComponents[1][i]+"\n");
            Vector line = new Vector(1);
            line.addElement(Double.toString(principalComponents[0][i]));
            line.addElement(Double.toString(principalComponents[1][i]));
        hashSample.put("C:" + i, line);
        }
    System.out.println("hashSample:"+hashSample);
    expressionProfilePicture expressionProfilePicture1
        = new expressionProfilePicture(hashSample, "Just A Test");
    String[] abc = {"Line:2"};
    expressionProfilePicture1.label.repaint();
    expressionProfilePicture1.setLocation(50, 50);
    expressionProfilePicture1.setSize(500, 700);
    expressionProfilePicture1.setVisible(true);
  }
}
