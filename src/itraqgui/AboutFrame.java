package itraqgui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class AboutFrame extends JDialog implements ActionListener {

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JButton button1 = new JButton();
  ImageIcon image1 = new ImageIcon();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  public AboutFrame(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
        image1 = new ImageIcon("Logo.gif");
        this.getContentPane().setBackground(Color.white);
        this.setTitle("About");
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        button1.setText("Ok");
        button1.addActionListener(this);
        insetsPanel1.setBackground(Color.white);
        jLabel1.setBackground(Color.white);
        jLabel1.setIcon(image1);
        jLabel1.setAlignmentX(JLabel.CENTER);
        panel1.setBackground(Color.white);
        panel2.setBackground(Color.white);
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 17));
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setText("Develpped by Shouguo Gao and Xujing Wang");
        jLabel3.setText("gaos2@nih.gov, wangx21@nih.gov");
        this.getContentPane().add(panel1, null);
        insetsPanel1.add(button1, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        panel1.add(panel2, BorderLayout.NORTH);
        panel2.add(jLabel1, BorderLayout.NORTH);
        panel2.add(jLabel2, BorderLayout.CENTER);
        panel2.add(jLabel3, BorderLayout.SOUTH);
        setResizable(true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  //Component initialization
 
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  //Close the dialog
  void cancel() {
    dispose();
  }
  //Close the dialog on a button event
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }
}

