package itraqgui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;

public class volcanoLabel extends JLabel {
  Hashtable hashPictureData;
  Vector vtPictureData;
  Vector vtGeneName;
  String pictureDescription;
  String[] lineSpots;

  public volcanoLabel(Hashtable hash, String description, String[] linespots){
    lineSpots = linespots;
    hashPictureData = hash;
    vtPictureData = new Vector(1);
    Iterator iter = hash.keySet().iterator();
    while(iter.hasNext()){
        Vector line = (Vector) hash.get(iter.next());
        vtPictureData.add(line);
    }
    pictureDescription = description;
  }

  public void paint(Graphics g) {
      System.out.println(vtPictureData);
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
            System.out.println(vtLine.get(1)+"\t"+vtLine.get(0));
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
        System.out.println(vtLine.get(1)+"\t"+vtLine.get(0));
        int intYStart = new Double(200 *((Double.parseDouble( (String) vtLine.get(1))) - doubleYmin) / (doubleYmax - doubleYmin)).intValue();
        int intXStart = new Double(200 *((Double.parseDouble( (String) vtLine.get(0))) - doubleXmin) / (doubleXmax - doubleXmin)).intValue();
        if(Double.parseDouble( (String) vtLine.get(1))<-2)
            g.setColor(Color.red);
        else
            g.setColor(Color.black);
        g.fillOval(28 + intXStart, 278 - intYStart,3,3);
    }
    g.setColor(Color.black);
    g.setFont(new java.awt.Font("Dialog", 0, 14));
    g.drawString(pictureDescription, 60, 370);
  }

public void setLineSelect(String[] spot){
    lineSpots = spot;
  }
}

