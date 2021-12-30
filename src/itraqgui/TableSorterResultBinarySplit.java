
/*
 * This Program is used to save the binary results
 * TableSorter.java is needed, because we can sort results
 */
package itraqgui;
import flanagan.analysis.Stat;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.awt.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.math3.distribution.TDistribution;


public class TableSorterResultBinarySplit extends JPanel {
    private boolean DEBUG = false;
    JButton buttonVolcanoProtein;                         //buttonShow used to show selected pathway diagram
    JButton buttonSaveProtein;                         //buttonSave used to save the results
    JButton buttonVolcanoPeptide;                         //buttonShow used to show selected pathway diagram
    JButton buttonSavePeptide;                         //buttonSave used to save the results
//    Hashtable hashExpression_normal = new Hashtable();
    JFileChooser chooser = new JFileChooser();
    String fileName = "";
    String resultType;
//	double[] phenoData;                               //used to record normalized data
	
//	double[] getPhenoData(){
//		return phenoData;
//	}
//	void setPhenoData(double[] phenoData){
//		this.phenoData = phenoData;
//	}
	
	
    //public TableSorterResultBinarySplit(Hashtable hashExpression){
//		hashExpression_normal = hashExpression;
    //}
    public TableSorterResultBinarySplit(String resultType){
		this.resultType = resultType;
    }

    public TableSorterResultBinarySplit(Vector vt_test, int columnNum, String[] columnName, Vector vt_test1, int columnNum1, String[] columnName1) {
        super(new GridLayout(1,0));
	//	hashExpression_normal = hashExpression;
        TableSorter sorter = new TableSorter(new MyTableModel(vt_test, columnNum, columnName)); 
        final JTable table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        JList list = new JList();
        //Set up tool tips for column headers.
        table.getTableHeader().setToolTipText(
                "Click to specify sorting; Control-Click to specify secondary sorting");

        this.setLayout(new BorderLayout());
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        TableSorter sorter1 = new TableSorter(new MyTableModel(vt_test1, columnNum1, columnName1)); 
        final JTable table1 = new JTable(sorter1);
        sorter1.setTableHeader(table.getTableHeader());
        table1.setPreferredScrollableViewportSize(new Dimension(500, 70));
        //Set up tool tips for column headers.
        table1.getTableHeader().setToolTipText(
                "Click to specify sorting; Control-Click to specify secondary sorting");

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane1 = new JScrollPane(table1);
        
        //Create a Pane for center
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new GridLayout(2,1));
        dataPanel.add(scrollPane);
        dataPanel.add(scrollPane1);
        //Add the data pane to this panel.
        add(BorderLayout.CENTER, dataPanel);

        JPanel buttonPanel = new JPanel();
        buttonVolcanoProtein = new JButton("ProteinVolcano");
        buttonPanel.add(buttonVolcanoProtein);
        buttonSaveProtein = new JButton("Save the Protein Result");
        buttonPanel.add(buttonSaveProtein);
        buttonVolcanoPeptide = new JButton("PeptideVolcano");
        buttonPanel.add(buttonVolcanoPeptide);
        buttonSavePeptide = new JButton("Save the Peptide Result");
        buttonPanel.add(buttonSavePeptide);
        add(BorderLayout.SOUTH, buttonPanel);

        //Add Action Listener
        buttonVolcanoProtein.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
                          HashMap<String, double[]> pepResult = new HashMap<String, double[]>(1);
            Hashtable hashSample = new Hashtable();
             //read the phenotype data
            Vector<Integer> vtGroup1 = new Vector<Integer>(1);
            Vector<Integer> vtGroup2 = new Vector<Integer>(1);
            for(int i=0; i<Data.type; i++){
                if(Data.phenotype[i]==0)
                    vtGroup1.add(i);
                else
                    vtGroup2.add(i);
            }
            HashMap<String, double[]> pepData = (HashMap<String, double[]>)Data.data.get(3);
            Iterator<String> iter= pepData.keySet().iterator();
            int index=0;
           while(iter.hasNext()){
                String peptide = iter.next();
                //deal with expression data
                double[] expData = pepData.get(peptide);
                double meandata1 = 0; 
                double meandata2 = 0;
                for(int i=0; i<vtGroup1.size(); i++){
                    meandata1+=expData[vtGroup1.get(i)];
                }
                for(int i=0; i<vtGroup2.size(); i++){
                    meandata2+=expData[vtGroup2.get(i)];
                }
                double ratios=meandata1-meandata2;
                double[] resultTest = new double[2];
                resultTest[0]=0;//Stat.lorentzianCDF(coeff[0], coeff[1], -10, ratios);
                resultTest[1]=ratios;
                index++;
                pepResult.put(peptide, resultTest);
            }
            //Let us combined into protein level results
            //We used the median of all resuts of peptides for each proteins
            String header[] = {"protein","peptides #","ratio","pvalue"};
            int numCol = 4;
            //
            HashMap<String, Vector<String>> mapping = (HashMap<String, Vector<String>>)(Data.data.get(0));
            iter = mapping.keySet().iterator();
            while(iter.hasNext()){
                String protein = iter.next();
                Vector peptides = mapping.get(protein);
                //put the result data of all peptides into HashMap
                HashMap<Double, Double> resultPep = new HashMap<Double, Double> (1);
                for(int i=0; i<peptides.size(); i++){
                    double[] resultTest = pepResult.get(peptides.get(i));
                    resultPep.put(resultTest[1], resultTest[0]);
                }
                //Sort the result 
                Map<Double, Double> mapSort = new TreeMap<Double, Double>(resultPep);
                Set set2 = mapSort.entrySet();
                Iterator iterator2 = set2.iterator();
                for(int i=0; i<set2.size()/2; i++) {
                    iterator2.next();
                }
                Map.Entry me2 = (Map.Entry)iterator2.next();
                //Let us get the median one
                Vector line = new Vector(1);
                line.addElement(me2.getValue());
                line.addElement(me2.getValue());
                hashSample.put(protein, line);
            }
            System.out.println("hashSample:"+hashSample);
            volcanoPicture volcanoPicture1  = new volcanoPicture(hashSample, "Just a test", "protein");
            volcanoPicture1.label.repaint();
            volcanoPicture1.setLocation(50, 50);
            volcanoPicture1.setSize(500, 700);
            volcanoPicture1.setVisible(true);
            return;
           }
         });
         buttonVolcanoPeptide.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Hashtable hashSample = new Hashtable();
            TDistribution tDist = new TDistribution(7);
            Random fRandom = new Random(1);
            double MEAN = 0.0f; 
            double VARIANCE = 2.0f;
            for(int i= 0; i<1400; i++){
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
            return;
           }
         });   
          
         //Add Action Listener buttonSave
         buttonSaveProtein.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             chooser.setSelectedFile(new File((Data.expressionFileName).replaceAll(".txt", "")
                                              + "_protein"
                                              + ".txt"));
             int state = chooser.showSaveDialog(null);

             File file = chooser.getSelectedFile();
             String s = "CANCELED";
             if(state == JFileChooser.APPROVE_OPTION)
               s = file.getPath();
               //    JOptionPane.showMessageDialog(null, s);
             try{
               BufferedOutputStream bufOut=
                   new BufferedOutputStream(new FileOutputStream(s));
               DataOutputStream dataOut = new DataOutputStream(bufOut);
               dataOut.writeBytes(columnName[0]);
               for(int j=1; j<table.getColumnCount(); j++){
                   dataOut.writeBytes("\t" + columnName[j]);
               }
               dataOut.writeBytes("\n");
               String result = new String("");
               for(int i=0; i<table.getRowCount(); i++){
                 for(int j=0; j<table.getColumnCount()-1; j++){
                   dataOut.writeBytes((String)table.getValueAt(i,j) + "\t");
                 }
                 dataOut.writeBytes((String)table.getValueAt(i,table.getColumnCount()-1) + "\n");
               }

               bufOut.close();
               dataOut.close();}
             catch(Exception e1){
               System.out.println("can not create file" + e1.getMessage());
             }
             return;
           }
         });
        buttonSavePeptide.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             chooser.setSelectedFile(new File((Data.expressionFileName).replaceAll(".txt", "")
                                              + "_peptide"
                                              + ".txt"));
             int state = chooser.showSaveDialog(null);

             File file = chooser.getSelectedFile();
             String s = "CANCELED";
             if(state == JFileChooser.APPROVE_OPTION)
               s = file.getPath();
               //    JOptionPane.showMessageDialog(null, s);
             try{
               BufferedOutputStream bufOut=
                   new BufferedOutputStream(new FileOutputStream(s));
               DataOutputStream dataOut = new DataOutputStream(bufOut);
               dataOut.writeBytes(columnName1[0]);
               for(int j=1; j<table1.getColumnCount(); j++){
                   dataOut.writeBytes("\t" + columnName1[j]);
               }
               dataOut.writeBytes("\n");
               String result = new String("");
               for(int i=0; i<table1.getRowCount(); i++){
                 for(int j=0; j<table1.getColumnCount()-1; j++){
                   dataOut.writeBytes((String)table1.getValueAt(i,j) + "\t");
                 }
                 dataOut.writeBytes((String)table1.getValueAt(i,table1.getColumnCount()-1) + "\n");
               }

               bufOut.close();
               dataOut.close();}
             catch(Exception e1){
               System.out.println("can not create file" + e1.getMessage());
             }
              return; 
           }
        } );
       }

     //This class is used to define table, it extends from AbstractTableModel
     class MyTableModel extends AbstractTableModel {
       private String[] columnNames;
       private Object[][] data;


       public MyTableModel(Vector vt, int columnNum, String[] columnName){
         columnNames = columnName;    //used to define the table headers         
         data  = new Object[vt.size()][columnNum];  //define the data size
         //Get data from a vector and evaluate data for tables
         for(int i=0; i< vt.size(); i++){
           String[] line = ((String)vt.get(i)).split("---");
           for(int j=0; j<line.length; j++){
               data[i][j] = line[j];
           }
          }
        }

        public int getColumnCount() {
          return columnNames.length;
        }

        public int getRowCount() {
          return data.length;
        }

        public String getColumnName(int col) {
          return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
          return data[row][col];
        }


        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }

            data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }

    /**
     * Create the GUI and show it.
     */
    public void createAndShowGUI(Vector vt_test, int columnNum, String[] columnName, Vector vt_test1, int columnNum1, String[] columnName1) {
      //Make sure we have nice window decorations.
      JFrame.setDefaultLookAndFeelDecorated(true);

      //Create and set up the window.we use file name as frame title
      //Because users may want to compare results from different datasets
      JFrame frame = new JFrame("iTRAQ Result");

      //Create and set up the content pane.
      TableSorterResultBinarySplit newContentPane = new TableSorterResultBinarySplit(vt_test, columnNum, columnName, vt_test1, columnNum1, columnName1);
      newContentPane.setOpaque(true); //content panes must be opaque
      frame.setBounds(100,50,800,500);
      frame.setContentPane(newContentPane);

      //Display the window.
      frame.setVisible(true);
    }

    public static void main(String[] args) {
        Vector vector_test = new Vector(1);
        String header[] = {"Pathway","Description","Genes","All Genes in Chip","P-Value(Cluster)"};
        int numCol = 5;
        vector_test.addElement("mmu00010---hsjsjs---0.111---0.0032---0.4666");
        vector_test.addElement("mmu00020---sjsjdjsdk---0.222---0.0022---0.6666");
        vector_test.addElement("mmu00030---dsdsds---0.333---0.0011---0.5666");
        TableSorterResultBinarySplit expTable = new TableSorterResultBinarySplit ("ttest");
        expTable.createAndShowGUI(vector_test, numCol, header, vector_test, numCol, header);
    }
  }
