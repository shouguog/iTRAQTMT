
/*
 * This Program is used to save pathway analysis results
 * TableSorter.java is needed, because we can sort results
 */
package itraqgui;
import flanagan.analysis.Regression;
import flanagan.analysis.*;
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
import javastat.inference.nonparametric.RankSumTest;
import javastat.inference.twosamples.TwoSampMeansTTest;
import javastat.multivariate.PCA;
import javastat.util.DataManager;

public class TableSorterMatrixNorm extends JPanel {
    private boolean DEBUG = false;
    JButton buttonWeighted;                          
    JButton buttonPCA;                         
    JButton buttonCauthy;                          
    JButton buttonRankSum;                         
    JButton buttonTtest;                         
    JButton buttonSave;                         
    Hashtable hashExpression_normal = new Hashtable();
    JFileChooser chooser = new JFileChooser();
    String fileName = "";
	double[] phenoData;                               //used to record normalized data
	
	double[] getPhenoData(){
		return phenoData;
	}
	void setPhenoData(double[] phenoData){
		this.phenoData = phenoData;
	}
	
	
    public TableSorterMatrixNorm(Hashtable hashExpression){
		hashExpression_normal = hashExpression;
    }

    public TableSorterMatrixNorm(Vector vt_test, int columnNum, String[] columnName, Hashtable hashExpression) {
        super(new GridLayout(1,0));
		hashExpression_normal = hashExpression;
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

        //Add the scroll pane to this panel.
        add(BorderLayout.CENTER, scrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPCA = new JButton("PCA");
        buttonPanel.add(buttonPCA);
        buttonWeighted = new JButton("Weighted method");
        buttonPanel.add(buttonWeighted);
        buttonCauthy = new JButton("Cauthy Fitting");
        buttonPanel.add(buttonCauthy);
        buttonRankSum = new JButton("Rank Sum");
        buttonPanel.add(buttonRankSum);
        buttonTtest = new JButton("Ttest");
        buttonPanel.add(buttonTtest);
        buttonSave = new JButton("Save the Result");
        buttonPanel.add(buttonSave);
        add(BorderLayout.SOUTH, buttonPanel);

        //Add Action listener to buttonPCA
        buttonPCA.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            //prepare the data
            HashMap<String, double[]> pepData = (HashMap<String, double[]>)Data.data.get(3);
            double[][] testscores = new double[Data.type][pepData.size()];
            Vector<String> tracks = new Vector<String>(1);
            if(Data.type==8){
                tracks.add("113");tracks.add("114");tracks.add("115");tracks.add("116");
                tracks.add("117");tracks.add("118");tracks.add("119");tracks.add("121");
            }else{
                tracks.add("113");tracks.add("114");tracks.add("115");tracks.add("116");
            }
            Iterator<String> iter= pepData.keySet().iterator();
            int index=0;
            while(iter.hasNext()){
                //deal with expression data
                double[] expData = pepData.get(iter.next());
                for(int j=0; j<Data.type; j++)
                    testscores[j][index]=expData[j];
                index++;
            }
            //Start the PCA analysis
            DataManager dm = new DataManager();
            PCA testclass2 = new PCA();
            double[][] principalComponents = testclass2.principalComponents(testscores);
            double[] variance = testclass2.variance(testscores);
            Hashtable hashPCA = new Hashtable();
            for(int i=0; i<principalComponents.length; i++){
                System.out.println(principalComponents[0][i]+"\t"+principalComponents[1][i]+"\n");
                Vector line = new Vector(1);
                line.addElement(Double.toString(principalComponents[0][i]));
                line.addElement(Double.toString(principalComponents[1][i]));
                hashPCA.put(tracks.get(i), line);
            }
                //System.out.println("hashSample:"+hashSample);
                expressionProfilePicture expressionProfilePicture1 = new expressionProfilePicture(hashPCA, "PCA result");
                expressionProfilePicture1.label.repaint();
                expressionProfilePicture1.setLocation(50, 50);
                expressionProfilePicture1.setSize(500, 700);
                expressionProfilePicture1.setVisible(true);
              return;
           }
          });

        //Add Action listener to buttonCauthy
        buttonCauthy.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             //read the phenotype data
            Vector<Integer> vtGroup1 = new Vector<Integer>(1);
            Vector<Integer> vtGroup2 = new Vector<Integer>(1);
            for(int i=0; i<Data.type; i++){
                if(Data.phenotype[i]==0)
                    vtGroup1.add(i);
                else
                    vtGroup2.add(i);
            }
            //We firstly check peptide result
            //Fit Cauchy            
            //Get ratios
            double[] ratios = new double[Data.rowNum];
            System.out.println("ratio number"+ratios.length);
            Vector vector_test1 = new Vector(1);
            String header1[] = {"peptides #","ratio","pvalue"};
            int numCol1 = 3;
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
                ratios[index]=meandata1-meandata2;
                System.out.println("index:"+index);
                index++;
            }
            double[] coeff=Regression.fitCauchy(ratios);
            //calculate p values with Cauchy model
            HashMap<String, double[]> pepResult = new HashMap<String, double[]>(1);
            iter= pepData.keySet().iterator();
            index=0;
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
                ratios[index]=meandata1-meandata2;
                double[] resultTest = new double[2];
                resultTest[0]=Stat.lorentzianCDF(coeff[0], coeff[1], -10, ratios[index]);
                resultTest[1]=ratios[index];
                index++;
                vector_test1.addElement(peptide+"---"+resultTest[0]+"---"+resultTest[1]);
                pepResult.put(peptide, resultTest);
            }
            //Let us combined into protein level results
            //We used the median of all resuts of peptides for each proteins
            Vector vector_test = new Vector(1);
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
                vector_test.addElement(protein + "---"+set2.size()+"---"+me2.getValue()+"---"+me2.getKey());
            }
            TableSorterResultBinarySplit expTable = new TableSorterResultBinarySplit ("cauchy");
            expTable.createAndShowGUI(vector_test, numCol, header, vector_test1, numCol1, header1);
            return;
            }
          });

        //Add Action listener to buttonWeighted
        buttonWeighted.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            MethodWeight.calculateWeights();
            MethodWeight.calculateError();
            MethodWeight.generateOutput();
            return;
            }
          });


        //Add Action Listener buttonRankSum
        buttonRankSum.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            //read the phenotype data
            Vector<Integer> vtGroup1 = new Vector<Integer>(1);
            Vector<Integer> vtGroup2 = new Vector<Integer>(1);
            for(int i=0; i<Data.type; i++){
                if(Data.phenotype[i]==0)
                    vtGroup1.add(i);
                else
                    vtGroup2.add(i);
            }
            //We firstly check peptide result
            Vector vector_test1 = new Vector(1);
            String header1[] = {"peptides #","value","pvalue"};
            int numCol1 = 3;
            HashMap<String, double[]> pepData = (HashMap<String, double[]>)Data.data.get(3);
            HashMap<String, double[]> pepResult = new HashMap<String, double[]>(1);
            Iterator<String> iter= pepData.keySet().iterator();
            while(iter.hasNext()){
                String peptide = iter.next();
                //deal with expression data
                double[] expData = pepData.get(peptide);
                double [] testdata1 = new double[vtGroup1.size()]; 
                double [] testdata2 = new double[vtGroup2.size()];
                for(int i=0; i<vtGroup1.size(); i++){
                    testdata1[i]=expData[vtGroup1.get(i)];
                }
                for(int i=0; i<vtGroup2.size(); i++){
                    testdata2[i]=expData[vtGroup2.get(i)];
                }
                // Non-null constructor: 
        RankSumTest testclass2 = new RankSumTest(0.05, "equal", testdata1, testdata2); 
                double[] resultTest = new double[2];
                resultTest[0] = testclass2.testStatistic; 
                resultTest[1] = testclass2.pValue; 
                vector_test1.addElement(peptide+"---"+resultTest[0]+"---"+resultTest[1]);
                pepResult.put(peptide, resultTest);
            }
            //Let us combined into protein level results
            //We used the median of all resuts of peptides for each proteins
            Vector vector_test = new Vector(1);
            String header[] = {"protein","peptides #","Value","pvalue"};
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
                vector_test.addElement(protein + "---"+set2.size()+"---"+me2.getValue()+"---"+me2.getKey());
            }
            TableSorterResultBinarySplit expTable = new TableSorterResultBinarySplit ("ranksum");
            expTable.createAndShowGUI(vector_test, numCol, header, vector_test1, numCol1, header1); 
            return;
           }
         });

        //Add Action Listener buttonTtest
        buttonTtest.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            //read the phenotype data
            Vector<Integer> vtGroup1 = new Vector<Integer>(1);
            Vector<Integer> vtGroup2 = new Vector<Integer>(1);
            for(int i=0; i<Data.type; i++){
                if(Data.phenotype[i]==0)
                    vtGroup1.add(i);
                else
                    vtGroup2.add(i);
            }
            //We firstly check peptide result
            Vector vector_test1 = new Vector(1);
            String header1[] = {"peptides #","t value","pvalue"};
            int numCol1 = 3;
            HashMap<String, double[]> pepData = (HashMap<String, double[]>)Data.data.get(3);
            HashMap<String, double[]> pepResult = new HashMap<String, double[]>(1);
            Iterator<String> iter= pepData.keySet().iterator();
            while(iter.hasNext()){
                String peptide = iter.next();
                //deal with expression data
                double[] expData = pepData.get(peptide);
                double [] testdata1 = new double[vtGroup1.size()]; 
                double [] testdata2 = new double[vtGroup2.size()];
                for(int i=0; i<vtGroup1.size(); i++){
                    testdata1[i]=expData[vtGroup1.get(i)];
                }
                for(int i=0; i<vtGroup2.size(); i++){
                    testdata2[i]=expData[vtGroup2.get(i)];
                }
                // Non-null constructor: 
                TwoSampMeansTTest testclass1 = new TwoSampMeansTTest(0.05, 0, "equal", testdata1, testdata2); 
                double[] resultTest = new double[2];
                resultTest[0] = testclass1.testStatistic; 
                resultTest[1] = testclass1.pValue; 
                vector_test1.addElement(peptide+"---"+resultTest[0]+"---"+resultTest[1]);
                pepResult.put(peptide, resultTest);
            }
            //Let us combined into protein level results
            //We used the median of all resuts of peptides for each proteins
            Vector vector_test = new Vector(1);
            String header[] = {"protein","peptides #","t value","pvalue"};
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
                vector_test.addElement(protein + "---"+set2.size()+"---"+me2.getValue()+"---"+me2.getKey());
            }
            TableSorterResultBinarySplit expTable = new TableSorterResultBinarySplit ("ttest");
            expTable.createAndShowGUI(vector_test, numCol, header, vector_test1, numCol1, header1);
            return;
           }
         });


 
         //Add Action Listener buttonSave
         buttonSave.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             chooser.setSelectedFile(new File("Normalizeddata.txt"));
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
    public void createAndShowGUI(Vector vt_test, int columnNum, String[] columnName) {
      //Make sure we have nice window decorations.
      JFrame.setDefaultLookAndFeelDecorated(true);

      //Create and set up the window.we use file name as frame title
      //Because users may want to compare results from different datasets
      JFrame frame = new JFrame("Normalized iTRAQ Data");

      //Create and set up the content pane.
      TableSorterMatrixNorm newContentPane = new TableSorterMatrixNorm(vt_test, columnNum, columnName, this.hashExpression_normal);
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
        //
        vector_test.addElement("mmu00010---hsjsjs---0.111---0.0032---0.4666");
        vector_test.addElement("mmu00020---sjsjdjsdk---0.222---0.0022---0.6666");
        vector_test.addElement("mmu00030---dsdsds---0.333---0.0011---0.5666");
        
        //
        TableSorterMatrixNorm expTable = new TableSorterMatrixNorm(new Hashtable(1));
        expTable.createAndShowGUI(vector_test, numCol, header);
    }
  }
