package itraqgui;

import java.io.BufferedOutputStream;
import java.util.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.rowset.CachedRowSet;
import com.sun.rowset.CachedRowSetImpl;
public class Method {
public static int[] readPhenoFile(String fileName){
    int[] dataPheno = new int[Data.type];
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String[] line=br.readLine().split("\t");
        for(int i=0; i<Data.type; i++){
            dataPheno[i]=Integer.parseInt(line[i]);
        }
    }catch(Exception eFile){
        System.out.println("File format wrong:"+eFile.getMessage());
    }
    Data.phenotype = dataPheno;
    return dataPheno;
}
//read the data
public static Vector<HashMap> readMatrixFile(String fileName, int traqNum){
    HashMap<String, Vector<String>> mapping=new HashMap<String, Vector<String>>(1);
    HashMap<String, double[]> data=new HashMap<String, double[]>(1);
    HashMap<String, String> mappingPeptide=new HashMap<String, String>(1);
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String line=br.readLine();//header
        while ((line = br.readLine()) != null) {
            Data.rowNum++;
            String[] lineItem=line.split("\t");
            double[] intensity=new double[traqNum];
            for(int i=1; i<=traqNum; i++){
                intensity[i-1]=Double.parseDouble(lineItem[i+1]);
            }
            data.put(lineItem[0], intensity);
            //check if this protein has been recorded
            if(mapping.containsKey(lineItem[1])){
                Vector<String> peptides=mapping.get(lineItem[1]);
                peptides.add(lineItem[0]);
                mapping.put(lineItem[1], peptides);
            }else{
                Vector<String> peptides=new Vector<String>(1);
                peptides.add(lineItem[0]);
                mapping.put(lineItem[1], peptides);
            }
            mappingPeptide.put(lineItem[0], lineItem[1]);
        }
    }catch(Exception e){
        System.out.println("File Format Wrong:" + e.getMessage());
    }
    Data.dataAndMapping.add(mapping);
    Data.dataAndMapping.add(data);
    Data.dataAndMapping.add(mappingPeptide);
    return Data.dataAndMapping;
}
//normalize data
public static HashMap<String, double[]>  normalizeMatrixFile(){
    HashMap<String, double[]> dataNormalized =new HashMap<String, double[]>(1);
    //Add the expression to a matrix
    Iterator iter= Data.data.get(1).keySet().iterator();
    Vector<ArrayList<Double>> vtExpression = new Vector<ArrayList<Double>>(1);
    //initial
    for(int i=0; i<Data.type; i++){
        ArrayList<Double> list = new ArrayList<Double>(1);
        vtExpression.add(list);
    }
    while(iter.hasNext()){
        double[] expressionIter = (double[])(Data.data.get(1).get(iter.next()));
        for(int i=0; i<Data.type; i++){
            ArrayList<Double> list=vtExpression.get(i);
            list.add(expressionIter[i]);
            vtExpression.set(i, list);
        }
    }
    //System.out.println(vtExpression.get(1));
    double[] meanChannel = new double[Data.type];
    double[] stdChannel = new double[Data.type];
    for(int i=0; i<Data.type; i++){
         ArrayList<Double> list=vtExpression.get(i);
         Collections.sort(list);
         int len=list.size();
         //System.out.println("peptiede number:" + len);
         int len_20 = (int)(len*0.2);
         int len_80 = (int)(len*0.8);
         double[] exp = new double[len_80-len_20];
         for(int j=0; j<exp.length; j++){
             exp[j]=list.get(j+len_20);
         }
         //System.out.println("expression:" + exp[0] + "\t" + exp[1] +"\t"+exp[2]);
         //System.out.println(Method.getLogMean(exp));
         //System.out.println(Method.getLogStd(exp));
         meanChannel[i] = Method.getLogMean(exp);
         stdChannel[i] = Method.getLogStd(exp);
    }
    double meanMeanChannel = Method.getMean(meanChannel);//We want to normalized all to this value
    iter= Data.data.get(1).keySet().iterator();
    while(iter.hasNext()){
        String peptide = (String)iter.next();
        double[] exp=(double[])(Data.data.get(1).get(peptide));
        double[] expNorm = new double[8];
        //start  to normalize
        for(int i=0; i<Data.type; i++){
            expNorm[i]=(Math.log10(exp[i])-meanChannel[i]+meanMeanChannel)/stdChannel[i];
        }
        dataNormalized.put(peptide, expNorm);
    }
    System.out.println(dataNormalized);
    Data.dataAndMapping.add(dataNormalized);
    return dataNormalized;
}
public static double getMean(double[] value){
    double valueMean=0;
    for(int i=0; i<value.length; i++){
        valueMean = valueMean + value[i]/value.length;
    }
    return valueMean;
}

public static double getWeightedMean(double weight[], double[] value){
    double valueMean=0;
    double valueWeight=0;
    for(int i=0; i<value.length; i++){
        valueMean = valueMean + value[i]*weight[i];
        valueWeight = valueWeight + weight[i];
    }
    return valueMean/valueWeight;
}

public static double getStd(double[]value){
    double valueMean= Method.getMean(value);
    double valueSD=0;
    for(int i=0; i<value.length; i++){
        valueSD = valueSD + Math.pow(value[i]-valueMean, 2);
    }
    return Math.sqrt(valueSD/(value.length-1));
}

public static double getLogMean(double[]value){
    double valueMean=0;
    for(int i=0; i<value.length; i++){
        valueMean = valueMean + Math.log10(value[i])/value.length;
    }
    return valueMean;
}

public static double getLogStd(double[]value){
    double valueMean= Method.getLogMean(value);
    double valueSD=0;
    for(int i=0; i<value.length; i++){
        valueSD = valueSD + Math.pow(Math.log10(value[i])-valueMean, 2);
    }
    return Math.sqrt(valueSD/(value.length-1));
}



public static HashMap<String, double[]> getWeightMatrix(HashMap<String, double[]> data, int bins){
return data;
}
public static void getWeight(){//weight_ref, weight_up, weight_value
}

public static double Median(double[] invalue){
    //Median calculation
    int num_value = invalue.length;
    double median = 0;
    double mid=0;
    if(num_value%2 == 0){
        int temp=(num_value/2)-1;
        for(int i=0;i<num_value;i++){
            if(temp==i || (temp+1)==i){
                mid=mid+invalue[i];
            }
        }
        mid=mid/2;
    }else{
        int temp=(num_value/2);
        for(int i=0;i<num_value;i++){
            if(temp==i){
                mid=invalue[i];
            }
        }
    }
    return mid;
}

public static double Median(Vector<Double> vtValue){
    //Median calculation
    Collections.sort(vtValue);
    if(vtValue.size()==0){
        return 0;
    }
    if(vtValue.size()%2==1){
        return vtValue.get(vtValue.size()/2);
    }else{
        return vtValue.get(vtValue.size()/2-1)/2+vtValue.get(vtValue.size()/2)/2;
    }
}
public static double Median(HashMap<String, Double> hashValue){
    //Median calculation
    List<Double> values = (List)hashValue.values();
    Collections.sort(values);
    if(values.size()%2==1){
        return values.get(values.size()%2);
    }else{
        return values.get(values.size()/2-1)/2+values.get(values.size()/2)/2;
    }
}
public static double Max(Vector<Double> vtValue){
    //Median calculation
    Collections.sort(vtValue);
    return vtValue.get(vtValue.size()-1);
}
public static double Max(HashMap<Integer, Double> hashValue){
    //Median calculation
    Iterator<Integer> keys = hashValue.keySet().iterator();
    Vector<Double> values=new Vector(1);
    while(keys.hasNext()){
        int key = keys.next();
        values.add(hashValue.get(key));
    }
    Collections.sort(values);
    return values.get(values.size()-1);
}

//used to parse MSF file
public static void parseMSF(String fileName)throws Exception{
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+fileName);
        Statement stat = conn.createStatement();
        stat.setFetchSize(1000);
        //
        ResultSet rs = null;
        rs=stat.executeQuery("select Peptides.PeptideID, Peptides.Sequence, ReporterIonQuanResults.Mass, ReporterIonQuanResults.Height, PeptidesProteins.ProteinID "+
                            "from Peptides, ReporterIonQuanResults, ReporterIonQuanResultsSearchSpectra, PeptidesProteins " +
                            "where Peptides.SpectrumID = ReporterIonQuanResultsSearchSpectra.SearchSpectrumID AND " +
                            "ReporterIonQuanResultsSearchSpectra.SpectrumID = ReporterIonQuanResults.SpectrumID AND " +
                            "PeptidesProteins.PeptideID=Peptides.PeptideID LIMIT 30000;"); 
//        rs=stat.executeQuery("select Peptides.PeptideID, Peptides.Sequence, ReporterIonQuanResults.Mass, ReporterIonQuanResults.Height, PeptidesProteins.ProteinID, ProteinAnnotations.Description " +
//                            "from Peptides, ReporterIonQuanResults, ReporterIonQuanResultsSearchSpectra, PeptidesProteins, ProteinAnnotations " +
//                            "where Peptides.SpectrumID = ReporterIonQuanResultsSearchSpectra.SearchSpectrumID AND " +
//                            "ReporterIonQuanResultsSearchSpectra.SpectrumID = ReporterIonQuanResults.SpectrumID AND " +
//                            "PeptidesProteins.PeptideID=Peptides.PeptideID AND " +
//                            "PeptidesProteins.ProteinID=ProteinAnnotations.ProteinID;"); 
//       rs=stat.executeQuery("select Peptides.PeptideID, Peptides.Sequence from Peptides;");
        System.out.println("Finished Query");
        CachedRowSetImpl crs = new CachedRowSetImpl();
        crs.populate(rs);
        System.out.println("Finished Cached");
        //run:
        //Finished Query
        //Finished Cached
        //Peptides.PeptideID	Peptides.Sequence	ReporterIonQuanResults.Mass	ReporterIonQuanResults.Height	PeptidesProteins.ProteinID	ProteinAnnotations.Description
        //BUILD SUCCESSFUL (total time: 65 minutes 43 seconds)
        BufferedOutputStream bufOut=new BufferedOutputStream(new FileOutputStream("C:\\Users\\gaos2\\Desktop\\singlecellRNA-seq\\SQLiteDatabaseBrowserPortable\\usefulTable.txt"));
        DataOutputStream dataOut = new DataOutputStream(bufOut);        
        System.out.print("Peptides.PeptideID\tPeptides.Sequence\tReporterIonQuanResults.Mass\tReporterIonQuanResults.Height\tPeptidesProteins.ProteinID\tProteinAnnotations.Description\n");
//        dataOut.writeBytes("Peptides.PeptideID\tPeptides.Sequence\tReporterIonQuanResults.Mass\tReporterIonQuanResults.Height\tPeptidesProteins.ProteinID\tProteinAnnotations.Description\n");
        dataOut.writeBytes("Peptides.PeptideID\tPeptides.Sequence\tReporterIonQuanResults.Mass\tReporterIonQuanResults.Height\tPeptidesProteins.ProteinID\n");
        System.out.println("LOOP RS");
        //while(rs.next()){
//        for(int i=0; i<2000; i++){
//            rs.next();
//            System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getString(6));
        //    System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5));
//            dataOut.writeBytes(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getString(6)+"\n");
            //dataOut.writeBytes(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\n");
        //}
        rs.close();
        System.out.println("LOOP CRS");
        while(crs.next()){
            System.out.println(crs.getString(1)+"\t"+crs.getString(2)+"\t"+crs.getString(3)+"\t"+crs.getString(4)+"\t"+crs.getString(5));
        }
        dataOut.close();
        bufOut.close();
}
public static void main(String args[]){
//Method.getWeight();
    //Data.data=Method.readMatrixFile("/Users/gaos2/Desktop/Literature_on_iTRAQ_statistics/henrik/data_IPG_protein.txt", 8);
    //Method.normalizeMatrixFile();
    //System.out.println(Method.Median(new double[]{2,4,3,2,6}));
    try{
        Method.parseMSF("L:\\Lab-Wang\\Projects\\Proteomics Core\\Literature on iTRAQ statistics\\Young_vs_Old_mouse_hearts_itraq.msf");
    }catch(Exception e){
        System.out.println("MSF file problem:" + e.getMessage());
    }
//System.out.println(Data.test);
}


}

//https://code.google.com/p/six11utils/downloads/list
//http://www.ee.ucl.ac.uk/~mflanaga/java/Regression.html

 