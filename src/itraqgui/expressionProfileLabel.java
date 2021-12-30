package itraqgui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;

public class expressionProfileLabel extends JLabel {
  Hashtable hashPictureData;
  Vector vtPictureData;
  Vector vtChanelName;
  String pictureDescription;
  String[] lineSpots;

  public expressionProfileLabel(Hashtable hash, String description, String[] linespots){
    lineSpots = linespots;
    hashPictureData = hash;
    vtPictureData = new Vector(1);
    vtChanelName = new Vector(1);
    Iterator<String> iter = hash.keySet().iterator();
    while(iter.hasNext()){
        String name = iter.next();
        Vector line = (Vector) hash.get(name);
        vtPictureData.add(line);
        vtChanelName.add(name);
    }
    pictureDescription = description;
  }

  public void paint(Graphics g) {
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
            if (doubleYmax < Double.parseDouble( (String) vtLine.get(1)))
              doubleYmax = Double.parseDouble( (String) vtLine.get(1));
            if (doubleYmin > Double.parseDouble( (String) vtLine.get(1)))
              doubleYmin = Double.parseDouble( (String) vtLine.get(1));
            if (doubleXmax < Double.parseDouble( (String) vtLine.get(0)))
              doubleXmax = Double.parseDouble( (String) vtLine.get(0));
            if (doubleXmin > Double.parseDouble( (String) vtLine.get(0)))
              doubleXmin = Double.parseDouble( (String) vtLine.get(0));
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
        int intYStart = new Double(200 *((Double.parseDouble( (String) vtLine.get(1))) - doubleYmin) / (doubleYmax - doubleYmin)).intValue();
        int intXStart = new Double(200 *((Double.parseDouble( (String) vtLine.get(0))) - doubleXmin) / (doubleXmax - doubleXmin)).intValue();
        //g.fillOval(,5,5);
        g.drawString((String)vtChanelName.get(i), 28 + intXStart, 278 - intYStart);
    }
    g.setColor(Color.black);
    g.setFont(new java.awt.Font("Dialog", 0, 14));
    g.drawString(pictureDescription, 60, 370);
  }

public void setLineSelect(String[] spot){
    lineSpots = spot;
  }
}

