package itraqgui;

import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.*;


public class MainFrame extends JFrame {
	  JButton jButtonRun = new JButton("Run");
	  JButton jButtonExit = new JButton("Exit");
	  JButton jButtonAbout = new JButton("About us");
	  JButton jButtonBrowserExpression = new JButton("Choose iTRAQ File");
	  JButton jButtonBrowserPheno = new JButton("Choose Pheno Data");
	  JTextField jTextFieldExpression = new JTextField("");
	  JTextField jTextFieldPheno = new JTextField("");
	  
	  JRadioButton jRadioButtonMatrixData   = new JRadioButton("MatrixData",true);
	  JRadioButton jRadioButtonMatrixDataNORM   = new JRadioButton("MatrixDataNorm",false);
	  JRadioButton jRadioButtonMSF    = new JRadioButton("msf",false);
	  ButtonGroup bgroup = new ButtonGroup();

	  JRadioButton jRadioButtoniTraq4   = new JRadioButton("iTraq4",true);
	  JRadioButton jRadioButtoniTraq8   = new JRadioButton("iTraq8",false);

	  ButtonGroup bgroupOrganism = new ButtonGroup();

	  
	  public MainFrame() {
		  super("proteomics/iTRAQ analysis in NLLBI");
		  this.getContentPane().setLayout(null);
		  this.setSize(600,450);
		  this.setLocation(100,100);
		  //
		  Data.expressionFileName="";
		  Data.phenoFileName="";
		  
		  //Expression file
		  jTextFieldExpression.setBounds(100,50,270,30);
		  jTextFieldExpression.setEditable(false);
		  this.getContentPane().add(jTextFieldExpression);
		  jButtonBrowserExpression.setBounds(400,50,170,30);
		  this.getContentPane().add(jButtonBrowserExpression);
		  jButtonBrowserExpression.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  chooseExpressioFile();
	        	  return;
	          }
	          });		  
		  //PhenoTyepe
		  jTextFieldPheno.setBounds(100,150,270,30);
		  jTextFieldPheno.setEditable(false);
		  this.getContentPane().add(jTextFieldPheno);
		  jButtonBrowserPheno.setBounds(400,150,170,30);
		  this.getContentPane().add(jButtonBrowserPheno);
		  jButtonBrowserPheno.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  choosePhenoFile();
	        	return;
	          }
	          });
		  //fileType
		  bgroup.add(jRadioButtonMatrixData);
		  bgroup.add(jRadioButtonMatrixDataNORM);
		  bgroup.add(jRadioButtonMSF);
		  JPanel radioPanel = new JPanel();
		  radioPanel.setLayout(new GridLayout(1, 2));
		  radioPanel.add(jRadioButtonMatrixData);
		  radioPanel.add(jRadioButtonMatrixDataNORM);
		  radioPanel.add(jRadioButtonMSF);
		  radioPanel.setBounds(100,200,450,30);
		  this.getContentPane().add(radioPanel);
		  
		  //Human or Mouse
		  bgroupOrganism.add(jRadioButtoniTraq4);
		  bgroupOrganism.add(jRadioButtoniTraq8);
		  JPanel radioPaneOrganism = new JPanel();
		  radioPaneOrganism.setLayout(new GridLayout(1, 2));
		  radioPaneOrganism.add(jRadioButtoniTraq4);
		  radioPaneOrganism.add(jRadioButtoniTraq8);
		  radioPaneOrganism.setBounds(100,260,300,30);
		  this.getContentPane().add(radioPaneOrganism);
		  
		  
		  //Add Button Run and Action listener
		  jButtonRun.setBounds(100,330,70,30);
		  this.getContentPane().add(jButtonRun);
		  jButtonRun.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  if(Data.expressionFileName.equals("") || Data.phenoFileName.equals("")){
	        	       JOptionPane.showMessageDialog(null,"You must provide Expression and Pheno file","NHLBI/NIH",JOptionPane.ERROR_MESSAGE);
	        	  }else{
	        		  //organism
	        		  if(jRadioButtoniTraq4.isSelected())
	        			  Data.type = 4;
	        		  else
	        			  Data.type=8;
                                    //read the phenotype data
                                    Method.readPhenoFile(Data.phenoFileName);

                                  //binary or quantitative
	        		  if(jRadioButtonMatrixData.isSelected()){
                                        Data.fileType = "matrix";
	        			if(Data.type==4){
                                            Vector vector_test = new Vector(1);
                                            String header[] = {"peptide", "113","114","115","116","protein"};
                                            int numCol = 6;
                                            Data.data=Method.readMatrixFile(jTextFieldExpression.getText(), 4);
                                            HashMap<String, double[]> data = Data.data.get(1);//The first one is data
                                            Iterator <String>it = data.keySet().iterator();
                                            while(it.hasNext()){
                                                String line=it.next();
                                                String pep=line;
                                                double[] lineData = data.get(line);
                                                for(int i=0; i<4; i++){
                                                    line=line+"---"+lineData[i];
                                                }
                                                line=line+"---"+Data.data.get(2).get(pep);
                                                vector_test.addElement(line);
                                                //System.out.println(line);
                                            }                                                  
                                            TableSorterMatrix expTable = new TableSorterMatrix(new Hashtable(1));
                                            expTable.createAndShowGUI(vector_test, numCol, header);
                                            System.out.println("Data.data:"+Data.data);
                                        }else{
                                            Vector vector_test = new Vector(1);
                                            String header[] = {"peptide", "113","114","115","116", "117", "118", "119", "121","protein"};
                                            int numCol = 10;
                                            Data.data=Method.readMatrixFile(jTextFieldExpression.getText(), 8);
                                            HashMap<String, double[]> data = Data.data.get(1);//The first one is data
                                            Iterator <String>it = data.keySet().iterator();
                                            while(it.hasNext()){
                                                String line=it.next();
                                                String pep=line;
                                                double[] lineData = data.get(line);
                                                for(int i=0; i<8; i++){
                                                    line=line+"---"+lineData[i];
                                                }
                                                line=line+"---"+Data.data.get(2).get(pep);
                                                vector_test.addElement(line);
                                            }                                            
                                            TableSorterMatrix expTable = new TableSorterMatrix(new Hashtable(1));
                                            expTable.createAndShowGUI(vector_test, numCol, header);
                                            System.out.println("Data.data:"+Data.data);                                            
                                        }
                                  }else if (jRadioButtonMatrixDataNORM.isSelected()){
                                        Data.fileType = "matrixNorm";
	        			if(Data.type==4){
                                            Vector vector_test = new Vector(1);
                                            String header[] = {"peptide", "113","114","115","116","protein"};
                                            int numCol = 6;
                                            Data.data=Method.readMatrixFile(jTextFieldExpression.getText(), 4);
                                            HashMap<String, double[]> data = Data.data.get(1);//The first one is data
                                            //We need to add normzalized data again to keep consistency
                                            Data.data.add(data);//The third are normalized
                                            Iterator <String>it = data.keySet().iterator();
                                            while(it.hasNext()){
                                                String line=it.next();
                                                String pep=line;
                                                double[] lineData = data.get(line);
                                                for(int i=0; i<4; i++){
                                                    line=line+"---"+lineData[i];
                                                }
                                                line=line+"---"+Data.data.get(2).get(pep);
                                                vector_test.addElement(line);
                                                //System.out.println(line);
                                            }                                                  
                                            TableSorterMatrixNorm expTable = new TableSorterMatrixNorm(new Hashtable(1));
                                            expTable.createAndShowGUI(vector_test, numCol, header);
                                            System.out.println("Data.data:"+Data.data);
                                        }else{
                                            Vector vector_test = new Vector(1);
                                            String header[] = {"peptide", "113","114","115","116", "117", "118", "119", "121","protein"};
                                            int numCol = 10;
                                            Data.data=Method.readMatrixFile(jTextFieldExpression.getText(), 8);
                                            HashMap<String, double[]> data = Data.data.get(1);//The first one is data
                                            Data.data.add(data);
                                            Iterator <String>it = data.keySet().iterator();
                                            while(it.hasNext()){
                                                String line=it.next();
                                                String pep=line;
                                                double[] lineData = data.get(line);
                                                for(int i=0; i<8; i++){
                                                    line=line+"---"+lineData[i];
                                                }
                                                line=line+"---"+Data.data.get(2).get(pep);
                                                vector_test.addElement(line);
                                            }                                            
                                            TableSorterMatrixNorm expTable = new TableSorterMatrixNorm(new Hashtable(1));
                                            expTable.createAndShowGUI(vector_test, numCol, header);
                                            System.out.println("Data.data:"+Data.data);
                                        }
	        		  }else{
                                        Data.fileType="MSF";
                                        try{
                                            Object[] buttons = {"OK"};
                                            JOptionPane.showMessageDialog(null, "Parse MSF file may need several hours to finish");
                                            Method.parseMSF(jTextFieldExpression.getText());
                                        }catch(Exception eMSF){
                                        }
                                  }
	        	  }
	        	return;
	          }
	          });

		  //Add ButtonExit and Action listener
		  jButtonExit.setBounds(250,330,70,30);
		  this.getContentPane().add(jButtonExit);
		  jButtonExit.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  System.exit(0);
	        	  return;
	          }
	          });
		  //Add ButtonAbout and Action listener
		  jButtonAbout.setBounds(400,330,130,30);
		  this.getContentPane().add(jButtonAbout);
		  jButtonAbout.addActionListener(new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	      		AboutFrame aboutFrame = new AboutFrame(new Frame("MCW"));
	    		aboutFrame.setSize(500, 200);
	    		aboutFrame.setLocation(200,200);
	    		aboutFrame.show();
	    		return;
	          }
	          });
	  }
	  
	  void chooseExpressioFile(){
		    JFileChooser chooser = new JFileChooser();
		    chooser.showOpenDialog(null);
		    try{
		      File file = chooser.getSelectedFile();
		      String stringFileName = file.getPath();
		      jTextFieldExpression.setText(stringFileName);
		      Data.expressionFileName = stringFileName;
		    }catch(Exception e1){
		      System.out.println("You select nothing");
		    }
		  }

	  void choosePhenoFile(){
		    JFileChooser chooser = new JFileChooser();
		    chooser.showOpenDialog(null);
		    try{
		      File file = chooser.getSelectedFile();
		      String stringFileName = file.getPath();
		      jTextFieldPheno.setText(stringFileName);
		      Data.phenoFileName=stringFileName;
		    }catch(Exception e1){
		      System.out.println("You select nothing");
		    }
		  }

	  
	  public static void main(String[] args) {
	    MainFrame mainFrame = new MainFrame();
	    mainFrame.setVisible(true);
	  }
}
