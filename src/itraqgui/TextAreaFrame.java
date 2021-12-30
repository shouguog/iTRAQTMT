/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itraqgui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextAreaFrame extends JFrame{
  private JButton saveButton = new JButton("Save the model");
  private JButton drawButton = new JButton("draw the model");
  private JTextArea textArea = new JTextArea(8, 40);
  private JTextArea textArea1 = new JTextArea(8, 40);
  private JScrollPane scrollPane = new JScrollPane(textArea);
  private JScrollPane scrollPane1 = new JScrollPane(textArea1);

  JFileChooser chooser = new JFileChooser();
public TextAreaFrame(String text) {
    JPanel p = new JPanel();
    textArea.setLineWrap(true);
    textArea.setText(text);
    textArea1.setLineWrap(true);
    textArea1.setText("We will draw the figure of error-intensity");
    p.add(saveButton);
    p.add(drawButton);
    getContentPane().add(p, "South");
    JPanel center = new JPanel();
    center.setLayout(new GridLayout(1,2));
    center.add(scrollPane); center.add(scrollPane1);
    getContentPane().add(center, "Center");
    setTitle("TextAreaTest");
    setSize(300, 300);   
    saveButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             int state = chooser.showSaveDialog(null);
            chooser.setSelectedFile(new File((Data.expressionFileName).replaceAll(".txt", "") + "_weighted.txt"));
             File file = chooser.getSelectedFile();
             String s = "CANCELED";
             if(state == JFileChooser.APPROVE_OPTION)
               s = file.getPath();
               //    JOptionPane.showMessageDialog(null, s);
             try{
               BufferedOutputStream bufOut=
                   new BufferedOutputStream(new FileOutputStream(s));
               DataOutputStream dataOut = new DataOutputStream(bufOut);

               dataOut.writeBytes(text);
               bufOut.close();
               dataOut.close();}
             catch(Exception e1){
               System.out.println("can not create file" + e1.getMessage());
             }
            return;
           }
         });
 
    drawButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            return;
           }
         });

  }
  public static void main(String[] args) {
    JFrame f = new TextAreaFrame("The quick brown fox jumps over the lazy dog. \n The quick brown fox jumps over the lazy dog.\n");
    f.show();
  }
}
