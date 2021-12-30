/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itraqgui;

/**
 *
 * @author gaos2
 */
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.commons.math.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

public class MethodWeight {
    
public static void calculateWeights(){
    double ratio = 1;           //#Expected ratio between duplicates
    int bins = 8;            //#Number of bins for weight calculation
    int rowNum=0;
    double[][] pepIntensity=new double[1187][2];

    try (BufferedReader br = new BufferedReader(
        new FileReader(Data.pathwayFile))) {
            String line=br.readLine();//header
            while((line = br.readLine())!=null){
                String[] lineItem=line.split("\t");
                pepIntensity[rowNum][0]=Double.parseDouble(lineItem[2]);
                pepIntensity[rowNum][1]=Double.parseDouble(lineItem[3]);
                rowNum++;
            }
            Data.rowNum=rowNum;
            System.out.println("Number of Rows:" + Data.rowNum);
        }catch(Exception e){
            System.out.println("File Format Wrong:" + e.getMessage());
        }

    //##Calculate weight-matrix for first two ratio
  
    //#Extract iTRAQ channels for weight calculation
    //index.channel.1 = grep(channel.1,colnames(pep.quant))
    //index.channel.2 = grep(channel.2,colnames(pep.quant))
    //pep.quant = pep.quant[,c(index.channel.1,index.channel.2)]
  
    //#Remove missing data
    //index.na = c(which(is.na(pep.quant[,1])),which(is.na(pep.quant[,2])))
    //if (length(index.na)>0) {
    //    pep.quant.norm = pep.quant.norm[-index.na,]
    //} 
  
    //#Calculate variance as deviance from expected ratio (on log2 scale)
    double[] estimated_ratio = new double[rowNum];double[] variance = new double[rowNum];
    double[][] variance_matrix = new double[rowNum][2];
    double expected_ratio = Math.log10(ratio)/Math.log10(2);
    for(int i=0; i<rowNum; i++){
        estimated_ratio[i]=Math.log10(pepIntensity[i][0]/pepIntensity[i][1])/Math.log10(2);
        variance[i] = Math.pow(estimated_ratio[i] - expected_ratio,2);
        variance_matrix[i][0] = Math.min(Math.log10(pepIntensity[i][0])/Math.log10(2), Math.log10(pepIntensity[i][1])/Math.log10(2));
        variance_matrix[i][1] = variance[i];
    }
    //#Calculate reference intensity as minimum intensity over iTRAQ channels
    //reference = log2(apply(pep.quant,1,min))
  
    //#Create variance matrix and sort in ascending order of reference intensity 
    //variance.matrix = cbind(reference, variance) 
    //variance.matrix = variance.matrix[order(variance.matrix[,1]),]
    Arrays.sort(variance_matrix, new Comparator<double[]>() {
        @Override
            public int compare(double[] o1, double[] o2) {
                return Double.compare(o1[0], o2[0]);
            }
        });
    //#Create weight matrix (call function getWeight)
    //weight.matrix = getWeightMatrix(variance.matrix,bins)
    double[][] weight_matrix = MethodWeight.getWeightMatrix(variance_matrix, 8);
    Data.weight_matrix= weight_matrix;
  
    //#Save variance and weight matrix
    //filename = paste(folder.weights,dataset.name,"_",channel.1,channel.2,"_variance_matrix.txt",sep="")
    //write.table(variance.matrix,file=filename,row.names=FALSE,sep="\t")
    Data.variance_matrix=variance_matrix;
  
    //filename = paste(folder.weights,dataset.name,"_",channel.1,channel.2,"_weight_matrix.txt",sep="")
    //write.table(weight.matrix,file=filename,row.names=FALSE,sep="\t")
      
    //##Plot ratios versus minimum intensity
    //filename = paste(folder.weights,dataset.name,"_ratio_plot.tif",sep="")
    //tiff(file=filename)
    //par(mar=c(2.5,2.5,0.5,0.5),mgp=c(1.4,0.5,0))
    //plot(reference,estimated.ratio,main="",xlab="log2 of minimum intensity",ylab="log2 of ratio",pch=20,ylim=c(-2,2))
    //abline(a=0,b=0)
    //abline(v=weight.matrix[-nrow(weight.matrix),3],col=grey(0.8))
    //dev.off()
  
    //##Plot weights for bins
    //filename = paste(folder.weights,dataset.name,"_weight_plot.tif",sep="")
    //tiff(file=filename)
    //par(mar=c(2.5,2.5,1,0.5),mgp=c(1.4,0.5,0))
    //barplot(weight.matrix[,4],main="",names.arg="",xlab="Bins",ylab="Weights")
    //dev.off()
}

public static void calculateError(){
    String output="\tratios\tprotein.weights\tpeptides\terrors\n";
    Vector<String> peptide = new Vector<String>(1);
    HashMap<String, Vector<String>> proteinPeptide = new HashMap<String, Vector<String>>(1);
    HashMap <String, double[]>pepIntensity = new HashMap <String, double[]>(1);
//#Specify folders, full path to data and weights
//folder.data = ""
//folder.weights = ""

//#Dataset
//dataset.name = "data_IPG"

//#Parameters for calculating weighted quant and errors
//channel.1 = "113"       #iTRAQ channel for weight calculation, training data
//channel.2 = "114"       #iTRAQ channel for weight calculation, training data
//num = "113"             #iTRAQ channel numerator for protein ratios
//den = "114"             #iTRAQ channel denominator used to calculate protein ratios
//ratio = 1               #Expected ratio between duplicates
double ratio = 1;               //#Expected ratio between duplicates

//#Load raw data
//##filename = paste(folder.data,dataset.name,"_norm_median_quant.txt",sep="")
//filename = paste(folder.data,dataset.name,".txt",sep="")

//pep.quant = read.delim(filename,header=TRUE,row.names=1,sep="\t")

//#Load protein accessions
//#filename = paste(folder.data,dataset.name,"_protein_accession.txt",sep="")
//filename = paste("proteinacc_IPG",".txt",sep="")
//protein.acc = read.delim(filename,header=TRUE,row.names=1,sep="\t")
    int rowNum = Data.rowNum;
    try (BufferedReader br = new BufferedReader(
        new FileReader(Data.pathwayFile))) {
            String line=br.readLine();//header
            for(int i=0; i<rowNum; i++){
                line = br.readLine();
                String[] lineItem=line.split("\t");
                double[] pepIntensityData = new double[2];
                pepIntensityData[0]=Double.parseDouble(lineItem[2]);
                pepIntensityData[1]=Double.parseDouble(lineItem[3]);
                pepIntensity.put(lineItem[0], pepIntensityData);
                peptide.add(lineItem[0]);
                if(proteinPeptide.containsKey(lineItem[1])){
                    Vector<String> peptideList = proteinPeptide.get(lineItem[1]);
                    peptideList.add(lineItem[0]);
                    proteinPeptide.put(lineItem[1], peptideList);
                }else{
                    Vector<String> peptideList = new Vector<String>(1);
                    peptideList.add(lineItem[0]);
                    proteinPeptide.put(lineItem[1], peptideList);
                }
            }
        }catch(Exception e){
            System.out.println("File Format Wrong:" + e.getMessage());
        }
  
  
//#Get unique proteins
//proteins = protein.acc[rownames(pep.quant),]$Protein.Group.Accessions
//unique.proteins = unique(proteins)
  
//#Remove empty protein accession string
//unique.proteins = unique.proteins[!is.na(unique.proteins)]
//unique.proteins = unique.proteins[unique.proteins!=""]

//no.proteins = length(unique.proteins)
  int no_proteins = proteinPeptide.size();
//##Calculate weighted protein quantities for 113/114 ratio
        
//#Load weight matrix
//filename = paste(folder.weights,dataset.name,"_",channel.1,channel.2,"_weight_matrix.txt",sep="")
//weight.matrix = read.delim(filename,header=TRUE,sep="\t")
//double[][] weight_matrix={{148,6.49200182569244,10.566112811209,7.05771389353873},
//                            {148,10.5664367739823,12.0063087233348,17.739081621797},
//                            {148,12.0067182391166,12.9876915855997,23.2789906083273},
//                            {148,12.9994941816322,13.8996891818333,44.541265590365},
//                            {148,13.9024661587182,14.819633057437,60.8188796794333},
//                            {148,14.8225179542945,15.6357177742456,53.6819960545462},
//                            {148,15.6363034721968,16.6961805441239,86.4443605870525},
//                            {151,16.7012473495986,21.2607559805726,100}};
double[][] weight_matrix = Data.weight_matrix; //12-20-2015
//#Select iTRAQ channels to calculate protein quant for
//index.den = grep(den,colnames(pep.quant))
  int index_den = 0;
//index.num = grep(num,colnames(pep.quant))
  int index_num = 1;
//quant.num = pep.quant[,index.num]
//quant.den = pep.quant[,index.den]

//#Calculate weighted protein quant and error for each protein
//weight.results = data.frame(ratios=rep(0,no.proteins),protein.weights=rep(0,no.proteins),
//peptides=rep(0,no.proteins),errors=rep(0,no.proteins),row.names=unique.proteins)
  HashMap<String, Vector<Double>> weight_results = new HashMap<String, Vector<Double>>(1);

//for (protein.i in 1:no.proteins) {
  Iterator<String> proteins= proteinPeptide.keySet().iterator();
  while(proteins.hasNext()) {
      String protein = proteins.next();
      //Get expression
      Vector<String> peptideList = proteinPeptide.get(protein);
  //quant.num.protein = quant.num[proteins==unique.proteins[protein.i]]
  //quant.den.protein = quant.den[proteins==unique.proteins[protein.i]]
      double[][] exp = new double[peptideList.size()][2];
         
  //#Get weights for peptide ratio, use minimum intensity as reference
  //reference = log2(apply(cbind(quant.num.protein,quant.den.protein),1,min,na.rm=TRUE))
      double[] reference = new double[peptideList.size()];
      double[] pep_ratio = new double[peptideList.size()];
      for(int i=0; i<reference.length; i++){
          double[] expData = pepIntensity.get(peptideList.get(i));
          exp[i][0] = expData[0];
          exp[i][1] = expData[1];
          reference[i]=Math.min(Math.log10(expData[0])/Math.log10(2), Math.log10(expData[1])/Math.log10(2));
          pep_ratio[i]=Math.log10(expData[0])/Math.log10(2)- Math.log10(expData[1])/Math.log10(2);
      }
  //#Call function getWeight
  //ratio.weights = getWeight(reference,weight.matrix[,3],weight.matrix[,4])
  //weight.factor = ratio.weights[,2]
      double[] weight_matrix_3 = new double[weight_matrix.length];
      double[] weight_matrix_4 = new double[weight_matrix.length];
      for(int i=0; i<weight_matrix.length; i++){
          weight_matrix_3[i] = weight_matrix[i][2];
          weight_matrix_4[i] = weight_matrix[i][3];
      }
    double[][] ratio_weights = MethodWeight.getWeight(reference, weight_matrix_3, weight_matrix_4);
    double[] weight_factor = new double[ratio_weights.length];
    for(int i=0; i<ratio_weights.length; i++){
        weight_factor[i] = ratio_weights[i][1];
    }
  //#Calculate peptide ratios and remove empty
  //pep.ratio = log2(quant.num.protein/quant.den.protein)
  //index.na = which(is.na(pep.ratio))
  //if (length(index.na)>0) {
    //pep.ratio = pep.ratio[-index.na]
    //weight.factor = weight.factor[-index.na]
  //} 
          
  //#Calculate total protein weight as average over peptide weights
  //weight.results$protein.weights[protein.i] = mean(weight.factor)
    double protein_weights = Method.getMean(weight_factor);
  //#Use Hmisc function to calculate weighted average (protein quantity)
  //protein.ratio.w = wtd.mean(pep.ratio,weight.factor) 
    double protein_ratio_w = Method.getWeightedMean(weight_factor, pep_ratio);
  //#Move back from log space to regular ratio
  //weight.results$ratios[protein.i] = 2^protein.ratio.w
    double ratios = Math.pow(2, protein_ratio_w);
  //#Calculate relative error for weighted protein quant
  //error = abs(ratio-weight.results$ratios[protein.i])
  //weight.results$errors[protein.i] = (error/ratio)*100
    double error = Math.abs(ratio-ratios)/ratio*100;
          
  //#Number of peptides for protein
  //weight.results$peptides[protein.i] = length(pep.ratio)
    double peptides = pep_ratio.length;
    //prepare weight.result
    Vector<Double> valueWeightResult=new Vector<Double>(1);
    valueWeightResult.add(ratios);valueWeightResult.add(protein_weights);valueWeightResult.add(peptides);valueWeightResult.add(error); 
    output=output+protein + "\t" + ratios + "\t" + protein_weights + "\t" + peptides + "\t" + error + "\n";
    weight_results.put(protein, valueWeightResult);
//}
}//end while(proteins.hasNext()) {
//  System.out.println("weight.results:"+output);
  System.out.println("weight.results:"+weight_results);
  Data.weight_results = weight_results;
//#Save weight results
//filename = paste(folder.weights,dataset.name,"_",channel.1,channel.2,"_weight_results.txt",sep="")
//write.table(weight.results,file=filename,col.names=NA,sep="\t")


}
public static double[][] generateOutput(){
    Vector<String> peptide = new Vector<String>(1);
    HashMap<String, Vector<String>> proteinPeptide = new HashMap<String, Vector<String>>(1);
    HashMap <String, double[]>pepIntensity = new HashMap <String, double[]>(1);
//#Specify folders, full path to data and weights
//folder.data = ""
//folder.weights = ""

//#Dataset
//dataset.name = "data_IPG"

//#Parameters for calculating weighted quant and errors
//channel.1 = "113"       #iTRAQ channel for weight calculation, training data
//channel.2 = "114"       #iTRAQ channel for weight calculation, training data
//num = "113"             #iTRAQ channel numerator for protein ratios
//den = "114"             #iTRAQ channel denominator used to calculate protein ratios
//ratio = 1               #Expected ratio between duplicates
double ratio = 1;               //#Expected ratio between duplicates

//#Load raw data
//##filename = paste(folder.data,dataset.name,"_norm_median_quant.txt",sep="")
//filename = paste(folder.data,dataset.name,".txt",sep="")

//pep.quant = read.delim(filename,header=TRUE,row.names=1,sep="\t")

//#Load protein accessions
//#filename = paste(folder.data,dataset.name,"_protein_accession.txt",sep="")
//filename = paste("proteinacc_IPG",".txt",sep="")
//protein.acc = read.delim(filename,header=TRUE,row.names=1,sep="\t")
    int rowNum = Data.rowNum;
    try (BufferedReader br = new BufferedReader(
        //new FileReader("/Users/gaos2/Desktop/Literature_on_iTRAQ_statistics/henrik/data_IPG_protein.txt"))) {
        new FileReader(Data.pathwayFile))) {
           String line=br.readLine();//header
            for(int i=0; i<rowNum; i++){
                line = br.readLine();
                String[] lineItem=line.split("\t");
                double[] pepIntensityData = new double[Data.type];
                for(int j=0; j<Data.type; j++)
                    pepIntensityData[j]=Double.parseDouble(lineItem[2+j]);
                pepIntensity.put(lineItem[0], pepIntensityData);
                peptide.add(lineItem[0]);
                if(proteinPeptide.containsKey(lineItem[1])){
                    Vector<String> peptideList = proteinPeptide.get(lineItem[1]);
                    peptideList.add(lineItem[0]);
                    proteinPeptide.put(lineItem[1], peptideList);
                }else{
                    Vector<String> peptideList = new Vector<String>(1);
                    peptideList.add(lineItem[0]);
                    proteinPeptide.put(lineItem[1], peptideList);
                }
            }
        }catch(Exception e){
            System.out.println("File Format Wrong:" + e.getMessage());
        }
  
  
//#Get unique proteins
//proteins = protein.acc[rownames(pep.quant),]$Protein.Group.Accessions
//unique.proteins = unique(proteins)
  
//#Remove empty protein accession string
//unique.proteins = unique.proteins[!is.na(unique.proteins)]
//unique.proteins = unique.proteins[unique.proteins!=""]

//no.proteins = length(unique.proteins)
  int no_proteins = proteinPeptide.size();
  Iterator<String> proteins = proteinPeptide.keySet().iterator();
  Vector<String> proteinList=new Vector<String>(1);
  while(proteins.hasNext())
      proteinList.add(proteins.next());
  System.out.println("Number of proteins:"+proteinList.size()+":"+no_proteins);
//##Calculate weighted protein quantities for 113/114 ratio
        

//##Calculate weighted protein ratios (all channels)
      
//#Load weight matrix
//filename = paste(folder.weights,dataset.name,"_",channel.1,channel.2,"_weight_matrix.txt",sep="")
//weight.matrix = read.delim(filename,header=TRUE,sep="\t")
//double[][] weight_matrix={{148,6.49200182569244,10.566112811209,7.05771389353873},
//                            {148,10.5664367739823,12.0063087233348,17.739081621797},
//                            {148,12.0067182391166,12.9876915855997,23.2789906083273},
//                            {148,12.9994941816322,13.8996891818333,44.541265590365},
//                            {148,13.9024661587182,14.819633057437,60.8188796794333},
//                            {148,14.8225179542945,15.6357177742456,53.6819960545462},
//                            {148,15.6363034721968,16.6961805441239,86.4443605870525},
//                            {151,16.7012473495986,21.2607559805726,100}};
double[][] weight_matrix = Data.weight_matrix; //12-20-2015
  //weight.factor = ratio.weights[,2]
      double[] weight_matrix_3 = new double[weight_matrix.length];
      double[] weight_matrix_4 = new double[weight_matrix.length];
      for(int i=0; i<weight_matrix.length; i++){
          weight_matrix_3[i] = weight_matrix[i][2];
          weight_matrix_4[i] = weight_matrix[i][3];
      }


//protein.weights = matrix(data=NA,nrow=no.proteins,ncol=length(num.list))
//protein.ratios = matrix(data=NA,nrow=no.proteins,ncol=length(num.list))
//protein.peptides = matrix(data=NA,nrow=no.proteins,ncol=length(num.list))
double[][] protein_weights = new double[no_proteins][Data.type];
double[][] protein_ratios = new double[no_proteins][Data.type];
double[][] protein_peptides = new double[no_proteins][Data.type];

//for (num in num.list) {
  for(int num=0; num<Data.type; num++){
//  ratio.list[ratio.i] = paste(num,den,sep="/")
  
//  #Select iTRAQ channels to calculate protein quant for
//  index.den.1 = grep(den.1,colnames(pep.quant))
//  index.den.2 = grep(den.2,colnames(pep.quant))
//  index.num = grep(num,colnames(pep.quant))
//  quant.num = pep.quant[,index.num]
//  quant.den = apply(cbind(pep.quant[,index.den.1],pep.quant[,index.den.2]),1,mean,na.rm=TRUE)
        
//  #Get peptides belonging to each protein
//  for (protein.i in 1:no.proteins) {
      for(int protein_i=0; protein_i<proteinList.size(); protein_i++){
//    quant.num.protein = quant.num[proteins==unique.proteins[protein.i]]
//    quant.den.protein = quant.den[proteins==unique.proteins[protein.i]]
          //Get the peptides 
          Vector<String> peptides = proteinPeptide.get(proteinList.get(protein_i));
          double[] quant_den_protein = new double[peptides.size()];
          double[] quant_num_protein = new double[peptides.size()];
          double[] reference = new double[peptides.size()];
          double[] pep_ratio = new double[peptides.size()];
//    #Get weights for peptide ratio, use minimum intensity as reference
//    reference = log2(apply(cbind(quant.num.protein,quant.den.protein),1,min,na.rm=TRUE))
          for(int j=0; j<peptides.size(); j++){
              double[] exp = pepIntensity.get(peptides.get(j));
              quant_den_protein[j] = exp[0]/2+exp[1]/2;
              quant_num_protein[j] = exp[num];
              reference[j] = Math.log10(Math.min(quant_num_protein[j], quant_den_protein[j]))/Math.log10(2);
              pep_ratio[j] = Math.log10(quant_num_protein[j]/quant_den_protein[j])/Math.log10(2);
          }
//    #Call function getWeight
//    ratio.weights = getWeight(reference,weight.matrix[,3],weight.matrix[,4])
        double[][] ratio_weights = MethodWeight.getWeight(reference, weight_matrix_3, weight_matrix_4);
        double[] weight_factor = new double[ratio_weights.length];
        for(int i=0; i<ratio_weights.length; i++){
            weight_factor[i] = ratio_weights[i][1];
        }
//    weight.factor = ratio.weights[,2]
  
//    #Calculate peptide ratios and remove empty
//    pep.ratio = log2(quant.num.protein/quant.den.protein)
//    index.na = which(is.na(pep.ratio))
//    if (length(index.na)>0) {
//      pep.ratio = pep.ratio[-index.na]
//      weight.factor = weight.factor[-index.na]
//    } 
          
//    #Calculate total protein weight as average over peptide weights
//    protein.weights[protein.i,ratio.i] = mean(weight.factor)
       protein_weights[protein_i][num] = Method.getMean(weight_factor);
//    #Use Hmisc function to calculate weighted average (protein quantity)
//    protein.ratio.w = wtd.mean(pep.ratio,weight.factor) 
      double protein_ratio_w = Method.getWeightedMean(weight_factor, pep_ratio);
//    #Move back from log space to regular ratio
//    protein.ratios[protein.i,ratio.i] = 2^protein.ratio.w
      protein_ratios[protein_i][num] = Math.pow(2, protein_ratio_w);    
//    #Number of peptides for protein
//    protein.peptides[protein.i,ratio.i] = length(pep.ratio)
      protein_peptides[protein_i][num] = pep_ratio.length;
  }
//  ratio.i = ratio.i+1
}
//colnames(protein.weights) = ratio.list
//rownames(protein.weights) = unique.proteins
//colnames(protein.ratios) = ratio.list
//rownames(protein.ratios) = unique.proteins
//colnames(protein.peptides) = ratio.list
//rownames(protein.peptides) = unique.proteins
//write.csv(protein.weights, file="proteinWeights.csv")  
//write.csv(protein.ratios, file="proteinratios.csv")
//write.csv(protein.peptides, file="proteinpeptides.csv")    
//#Load data for 113/114 training set

//#Load weight results
//filename = paste(folder.weights,dataset.name,"_",channel.1,channel.2,"_weight_results.txt",sep="")
//weight.results = read.delim(filename,row.names=1,header=TRUE,sep="\t")
HashMap<String, Vector<double[]>> petides_no_data = new HashMap<String, Vector<double[]>> (1);
    /*
    try (BufferedReader br = new BufferedReader(
        new FileReader(Data.pathwayFile + "_113114_weight_results.txt"))) {
            String line=br.readLine();//header
            while((line=br.readLine())!=null){
                line = br.readLine();
                String[] lineItem = line.split("\t");
                //"IPI:IPI00012759.1"	0.978219878870715	39.2789806506152	2	2.17801211292846
                double[] weight_error = new double[2];
                weight_error[0] = Double.parseDouble(lineItem[2]);
                weight_error[1] = Double.parseDouble(lineItem[4]);
                //no.peptides = c("1","2","3","4","5","5-10",">10")
                String pepNum="";
                if(Double.parseDouble(lineItem[3])<6){
                    pepNum=lineItem[3];
                }else if (Double.parseDouble(lineItem[3])>10){
                    pepNum=">10";
                }else{
                    pepNum="6_10";
                }
                if(petides_no_data.containsKey(pepNum)){
                    Vector<double[]> vtExp = petides_no_data.get(pepNum);
                    vtExp.add(weight_error);
                    petides_no_data.put(pepNum, vtExp);
                }else{
                    Vector<double[]> vtExp = new Vector<double[]>(1);
                    vtExp.add(weight_error);
                    petides_no_data.put(pepNum, vtExp);
                 }
            }
            br.close();
    }catch(Exception e){
        System.out.println("petides_no_data Wrong:" + e.getMessage());
    }
    */
    //replace the reading file with hashMap  
        HashMap<String, Vector<Double>> weight_results = Data.weight_results;
       Iterator<String> iter1 = weight_results.keySet().iterator();
       while(iter1.hasNext()){
           String protein = iter1.next();
           Vector<Double> vtResult = weight_results.get(protein);
                double[] weight_error = new double[2];
                weight_error[0] = vtResult.get(1);
                weight_error[1] = vtResult.get(3);
                //no.peptides = c("1","2","3","4","5","5-10",">10")
                String pepNum="";
                if(vtResult.get(2)<6){
                    pepNum=String.valueOf(vtResult.get(2).intValue());
                }else if (vtResult.get(2)>10){
                    pepNum=">10";
                }else{
                    pepNum="5_10";
                }
                if(petides_no_data.containsKey(pepNum)){
                    Vector<double[]> vtExp = petides_no_data.get(pepNum);
                    vtExp.add(weight_error);
                    petides_no_data.put(pepNum, vtExp);
                }else{
                    Vector<double[]> vtExp = new Vector<double[]>(1);
                    vtExp.add(weight_error);
                    petides_no_data.put(pepNum, vtExp);
                 }
    }

//#Get loess function for error versus average weight of proteins (95%) - for 113/114

//#Plot error versus average weight of proteins (95% loess smoothers)
//filename = paste(folder.results,dataset.name,"_protein_error_weights.tif",sep="")
//tiff(file=filename)
//par(mar=c(3,3,0.5,0.5),mgp=c(1.5,0.5,0))

//#For each number of peptides
//protein.weights.ref = c()
//protein.errors.ref = c()
//loess.list = list()
  Vector<String> no_peptides = new Vector<String>(1);
  Iterator<String> iter = petides_no_data.keySet().iterator();
  while(iter.hasNext())
      no_peptides.add(iter.next());
//for (i in 1:length(no.peptides)) {
 HashMap<String, PolynomialSplineFunction> hashLoess = new HashMap<String, PolynomialSplineFunction>(1);
String text="";
for (int i=0; i<no_peptides.size(); i++) {
    System.out.print(no_peptides.get(i) + ":");
//  if (i<6) {
//    protein.list = rownames(weight.results[weight.results$peptides==no.peptides.num[i],])
//  } else if (i==6) {
//    protein.list = rownames(weight.results[weight.results$peptides>5&weight.results$peptides<=10,])
//  } else {
//    protein.list = rownames(weight.results[weight.results$peptides>10,])
//  }
    Vector<double[]> vtExp = petides_no_data.get(no_peptides.get(i));
//  protein.weights.ref = weight.results[protein.list,]$protein.weights
//  protein.errors.ref = weight.results[protein.list,]$errors
    double[][] protein_weights_errors_ref = new double[vtExp.size()][2];
    double[] protein_weights_ref = new double[vtExp.size()];
    double[] protein_errors_ref = new double[vtExp.size()];
    HashMap<Double, Vector<Double>> protein_weights_errors_hash = new HashMap<Double, Vector<Double>> (1);
    for(int j=0; j<vtExp.size(); j++){
        double[] weight_error = vtExp.get(j);
        if(protein_weights_errors_hash.containsKey(weight_error[0])){
            Vector<Double> vtEx=protein_weights_errors_hash.get(weight_error[0]);
            vtEx.add(weight_error[1]);
            protein_weights_errors_hash.put(weight_error[0], vtEx);
        }else{
            Vector<Double> vtEx=new Vector<Double>(1);
            vtEx.add(weight_error[1]);
            protein_weights_errors_hash.put(weight_error[0], vtEx);
        }
        protein_weights_errors_ref[j][0] = weight_error[0];
        protein_weights_errors_ref[j][1] = weight_error[1];
    }
//  #Order the errors in order of the weights
//  weights.order = order(protein.weights.ref)
    Arrays.sort(protein_weights_errors_ref, new Comparator<double[]>() {
        @Override
            public int compare(double[] o1, double[] o2) {
                return Double.compare(o1[0], o2[0]);
            }
        });

  //protein.weights.ref = protein.weights.ref[weights.order]
  //protein.errors.ref = protein.errors.ref[weights.order]
    for(int j=0; j<vtExp.size(); j++){
        protein_weights_ref[j] = protein_weights_errors_ref[j][0]; 
        protein_errors_ref[j] = protein_weights_errors_ref[j][1]; 
    }
//  #Calculate 95% upper limit of error
//  protein.errors.95 = c()
//  if (i==1) {
    double[] weight_breaks;
    double[] protein_errors_runmed={1.0,1.0};
    if(no_peptides.get(i).equals("1")){
//    weights.unique = unique(protein.weights.ref)
        double[] weights_unique = new double[protein_weights_errors_hash.size()];
        Iterator<Double> iterWeight = protein_weights_errors_hash.keySet().iterator();
        for(int j=0; j<protein_weights_errors_hash.size(); j++){
            //Get unique weights
            weights_unique[j] = iterWeight.next();
        }
        Arrays.sort(weights_unique);
        double[] protein_errors_95 = new double[protein_weights_errors_hash.size()];
        for(int j=0; j<protein_weights_errors_hash.size(); j++){
            //Get the quantile of errors
            Vector<Double> vt_protein_errors_sel = protein_weights_errors_hash.get(weights_unique[j]);
            double[] protein_errors_sel = new double[vt_protein_errors_sel.size()];
            for(int k=0; k<vt_protein_errors_sel.size(); k++)
                protein_errors_sel[k] = vt_protein_errors_sel.get(k);
            Arrays.sort(protein_errors_sel);
            Percentile test = new Percentile(1);
            test.setData(protein_errors_sel);
            protein_errors_95[j] = test.evaluate(95);
        }
//    for (weight.unique in weights.unique) {
//      protein.errors.sel = protein.errors.ref[which(protein.weights.ref==weight.unique)]
//      protein.errors.95 = c(protein.errors.95,quantile(protein.errors.sel,probs=seq(0,1,0.05),na.rm=TRUE)[20])
//    }
//    protein.errors.runmed = protein.errors.95
//    weight.breaks = weights.unique
      weight_breaks = weights_unique;
      protein_errors_runmed = protein_errors_95;
//  } else if (i==2)  {
    } else if (no_peptides.get(i).equals("2"))  {
        double[] weights_unique = new double[protein_weights_errors_hash.size()];
        double[] protein_errors_95 = new double[protein_weights_errors_hash.size()];
        Iterator<Double> iterWeight = protein_weights_errors_hash.keySet().iterator();
        for(int j=0; j<protein_weights_errors_hash.size(); j++){
            //Get unique weights
            weights_unique[j] = iterWeight.next();
        }
        Arrays.sort(weights_unique);
        for(int j=0; j<protein_weights_errors_hash.size(); j++){
            //Get the quantile of errors
            Vector<Double> vt_protein_errors_sel = protein_weights_errors_hash.get(weights_unique[j]);
            double[] protein_errors_sel = new double[vt_protein_errors_sel.size()];
            for(int k=0; k<vt_protein_errors_sel.size(); k++)
                protein_errors_sel[k] = vt_protein_errors_sel.get(k);
            Arrays.sort(protein_errors_sel);
            Percentile test = new Percentile(1);
            test.setData(protein_errors_sel);
            protein_errors_95[j] = test.evaluate(95);
        }
//    for (weight.unique in weights.unique) {
//      protein.errors.sel = protein.errors.ref[which(protein.weights.ref==weight.unique)]
//      protein.errors.95 = c(protein.errors.95,quantile(protein.errors.sel,probs=seq(0,1,0.05),na.rm=TRUE)[20])
//    }
//    protein.errors.runmed = runmed(protein.errors.95,31)
//    weight.breaks = weights.unique
      weight_breaks = weights_unique; 
      protein_errors_runmed = protein_errors_95; //runmed(protein_errors_95);
  } else {
//    weight.breaks = quantile(protein.weights.ref,probs=seq(0,1,0.02))
      Set<Double> set_weight_breaks = new LinkedHashSet<Double>(1);
      Percentile test = new Percentile(1);
      test.setData(protein_weights_ref);
      for(int j=0; j<50; j++){
          set_weight_breaks.add(test.evaluate(2*j+2));
      }
      weight_breaks = new double[set_weight_breaks.size()];//50 is too many?
      double[] protein_errors_95 = new double[set_weight_breaks.size()];
      Iterator<Double> iterWeight = set_weight_breaks.iterator();
      for(int j=0; j<set_weight_breaks.size(); j++){
          weight_breaks[j]=iterWeight.next();
      }
      //do not know the reason the 0-th element is the same as the the 1-th element. This is the bug need to be fixed
//      System.out.println("I am here");
//    for (j in 1:(length(weight.breaks)-1)) {
      for(int j=0; j<set_weight_breaks.size(); j++){
          Vector<Double> error_interval = new Vector<Double>(1);
          //The first one
          if(j==0){
              for(int k=0; k<protein_weights_ref.length; k++){
                  if(protein_weights_ref[k]<weight_breaks[j])
                      error_interval.add(protein_errors_ref[k]);
              }
          }else if(j==(set_weight_breaks.size()-1)){//The last one
              for(int k=0; k<protein_weights_ref.length; k++){
                  if(protein_weights_ref[k]>weight_breaks[j])
                      error_interval.add(protein_errors_ref[k]);
              }
          }else{
              for(int k=0; k<protein_weights_ref.length; k++){
                  if(protein_weights_ref[k]>weight_breaks[j] & protein_weights_ref[k]>weight_breaks[j+1])
                      error_interval.add(protein_errors_ref[k]);
              }          
          }
//      if (j<length(weight.breaks)-1) {
//        error.interval = protein.errors.ref[protein.weights.ref>=weight.breaks[j]&protein.weights.ref<weight.breaks[j+1]]
//      } else {
//        error.interval = protein.errors.ref[protein.weights.ref>=weight.breaks[j]&protein.weights.ref<=weight.breaks[j+1]]
//      }
            if(error_interval.size()>=1){//At least two values
                double[] protein_errors_sel = new double[error_interval.size()];
                for(int k=0; k<error_interval.size(); k++)
                 protein_errors_sel[k] = error_interval.get(k);
                Arrays.sort(protein_errors_sel);
                Percentile testError = new Percentile(1);
                testError.setData(protein_errors_sel);
                protein_errors_95[j] = test.evaluate(95);
            }else{//With one or zero values, can not estimate
                protein_errors_95[j] = 0;
            }
//      protein.errors.95[j] = quantile(error.interval,probs=seq(0,1,0.05),na.rm=TRUE)[20]
    }//end for(j=
//    #Fill upp empty values
//    index.na = which(is.na(protein.errors.95))
//    while (length(index.na)>0) {
//      protein.errors.95[index.na] = protein.errors.95[index.na+1]
//      index.na = which(is.na(protein.errors.95))
//    }
  for(int j=0; j<set_weight_breaks.size(); j++){
      if(j==0){//Assign the fist one with the first non zero
          int k=0;
          while(protein_errors_95[k]==0)
              k++;
          protein_errors_95[0] = protein_errors_95[k];
      }
      if (protein_errors_95[j]==0){
          protein_errors_95[j]=protein_errors_95[j-1];
      }
  }
//    protein.errors.runmed = runmed(protein.errors.95,31)
//    weight.breaks = weight.breaks[-length(weight.breaks)]
      protein_errors_runmed = protein_errors_95; //runmed(protein_errors_95);
  }
     System.out.println(weight_breaks.length);
  //assign NaN
  if(Double.isNaN(protein_errors_runmed[0]))
      protein_errors_runmed[0]=protein_errors_runmed[1];
//  #Fit a loess curve to error
//  data.errors = data.frame(x=weight.breaks,y=protein.errors.runmed)
//  lo.errors = loess(y~x,data.errors,span=0.5,degree=1)
//  loess.list[i] = list(lo.errors)
    try{
        LoessInterpolator loessInterpolator=new LoessInterpolator(0.75,2);
        hashLoess.put(no_peptides.get(i), loessInterpolator.interpolate(weight_breaks, protein_errors_runmed));
        //output the errors
        text = text + "no peptides"+no_peptides.get(i)+":\n";
        System.out.println("no peptides"+no_peptides.get(i)+":");
        text = text + "weights";
        for(int ii=0; ii<weight_breaks.length; ii++){
            System.out.print("\t"+weight_breaks[ii]);
            text = text + "\t"+weight_breaks[ii];
        }
        System.out.println();
        text = text +"\nerror:";
        for(int ii=0; ii<weight_breaks.length; ii++){
            System.out.print("\t"+protein_errors_runmed[ii]);
            text = text + "\t"+protein_errors_runmed[ii];
        }
        System.out.println();
        text = text +"\n";
    }catch (Exception e){
        System.out.println(no_peptides.get(i) + ":" + e.getMessage());
    }
//  if (i==1) {
//    plot(data.errors$x,predict(lo.errors,data.errors$x),xlab="Protein weight",ylab="Relative error (%)",type="l",col=i,ylim=c(0,max(protein.errors.runmed)))  
//  } else {
//    lines(data.errors$x,predict(lo.errors,data.errors$x),col=i)
//  }
//  legend("topright",no.peptides,col=c(1:7),lty=1)
}//end for (int i=0
//dev.off()
        JFrame f = new TextAreaFrame(text);
        f.show();
//#Create matrix to save relative error estimated by loess
//error.matrix = matrix(NA,nrow=nrow(protein.ratios),ncol=ncol(protein.ratios),byrow=FALSE,dimnames=list(rownames(protein.ratios)))
//Shouguo
//double[][] protein_weights = new double[no_proteins][Data.type];
//double[][] protein_ratios = new double[no_proteins][Data.type];
//double[][] protein_peptides = new double[no_proteins][Data.type];
//proteinList
double[][]error_matrix=new double[no_proteins][Data.type];
//#Get relative error for all ratios
//for (i in 1:nrow(protein.weights)) {
//  for (j in 1:ncol(protein.weights)) {
Vector vector_test = new Vector(1);
   
for (int i=0; i<proteinList.size(); i++) {
  System.out.print(proteinList.get(i));
  String line=proteinList.get(i);
  for (int j=0; j<Data.type; j++) {
//    weight = protein.weights[i,j]
//    peptides = protein.peptides[i,j]
      double weight = protein_weights[i][j];
      double peptides = protein_peptides[i][j];
      String pepNum="";
//    if (peptides<6) {
      if(peptides<6){
//      peptides.index = peptides
        pepNum = ""+(int)peptides;  
//   } else if (peptides>5 & peptides<11) {
//      peptides.index = 6
      }else if(peptides>10){
          pepNum = ">10";
    } else {
//      peptides.index = 7
        pepNum = "6_10";
      }
//    error.matrix[i,j] = predict(loess.list[[peptides.index]],weight)
      //we need to adjust the weight, because sometime the weight is out of the fitted ranges
      if(weight>60){
          while(error_matrix[i][j]==0){
              try{
                  error_matrix[i][j] = hashLoess.get(pepNum).value(weight);
            }catch(Exception e){
                  //System.out.println("Loess error" + e.getMessage());
            }
            weight=weight-1;
            if (weight<35)
                break;
          }
      }else if(weight<30){
          while(error_matrix[i][j]==0){
              try{
                  error_matrix[i][j] = hashLoess.get(pepNum).value(weight);
            }catch(Exception e){
                  //System.out.println("Loess error" + e.getMessage());
            }
            weight=weight+1;
            if(weight>40)
                break;
          }
      }else{
              try{
                  error_matrix[i][j] = hashLoess.get(pepNum).value(weight);
            }catch(Exception e){
                  //System.out.println("Loess error" + e.getMessage());
            }      
      }
      //output the result
      //System.out.print("\t"+protein_weights[i][j]+"\t"+protein_ratios[i][j]+"\t"+protein_peptides[i][j]+"\t"+error_matrix[i][j]);
      line = line+"---"+protein_weights[i][j]+"---"+protein_ratios[i][j]+"---"+protein_peptides[i][j]+"---"+error_matrix[i][j];
//    if (is.na(error.matrix[i,j])) {
//      if (weight>60) {
//        while (is.na(error.matrix[i,j])) {
//          weight = weight-1
//          error.matrix[i,j] = predict(loess.list[[peptides.index]],weight)
//        }
//      } else if (weight<30) {
//        while (is.na(error.matrix[i,j])) {
//          weight = weight+1
//          error.matrix[i,j] = predict(loess.list[[peptides.index]],weight)
//        }
//      } else {
//      }
//    }
  }//end for (int j=0; j<Data.type; j++)
  //System.out.println();
  vector_test.add(line);
}//end for (int i=0; i<proteinList
System.out.println(vector_test);
TableSorterMatrixWeighted expTable = new TableSorterMatrixWeighted(new Hashtable(1));
if(Data.type==8){
    String header[] = {"protein", "protein_weights_113", "protein_ratios_113", "protein_peptides_num_113", "error_matrix_113"
    , "protein_weights_114", "protein_ratios_114", "protein_peptides_num_114", "error_matrix_114"
    , "protein_weights_115", "protein_ratios_115", "protein_peptides_num_115", "error_matrix_115"
    , "protein_weights_116", "protein_ratios_116", "protein_peptides_num_116", "error_matrix_116"
    , "protein_weights_117", "protein_ratios_117", "protein_peptides_num_117", "error_matrix_117"
    , "protein_weights_118", "protein_ratios_118", "protein_peptides_num_118", "error_matrix_118"
    , "protein_weights_119", "protein_ratios_119", "protein_peptides_num_119", "error_matrix_119"
    , "protein_weights_121", "protein_ratios_121", "protein_peptides_num_121", "error_matrix_121"};
    expTable.createAndShowGUI(vector_test, Data.type*4+1, header);
}else{
    String header[] = {"protein", "protein_weights_113", "protein_ratios_113", "protein_peptides_num_113", "error_matrix_113"
    , "protein_weights_114", "protein_ratios_114", "protein_peptides_num_114", "error_matrix_114"
    , "protein_weights_115", "protein_ratios_115", "protein_peptides_num_115", "error_matrix_115"
    , "protein_weights_116", "protein_ratios_116", "protein_peptides_num_116", "error_matrix_116"};
    expTable.createAndShowGUI(vector_test, Data.type*4+1, header);
}

//#Round error and weight
//error.matrix = round(error.matrix)
//protein.weights = round(protein.weights)

//#Save table
//quant.table = cbind(protein.ratios,protein.peptides,protein.weights,error.matrix)
//ratios = c("113","114","115","116","117","118","119","121")
//column.names = c(ratios,paste(ratios,"no.peptides",sep="."),paste(ratios,"weight",sep="."),paste(ratios,"rel.error",sep=".")) 
//colnames(quant.table) = column.names

//filename = paste(folder.results,dataset.name,"_protein_quant_table.txt",sep="")
//write.table(quant.table,file=filename,col.names=NA,sep="\t")
 return error_matrix;    

}
public static double[][] getWeight(double[] weight_ref, double[] weight_up, double[] weight_value){//weight_ref, weight_up, weight_value
    //ref_len = length(weight_ref)
  //weight_len = length(weight_up)
  //val_1 = rep(0,ref_len)
  //val_2 = rep(0,ref_len)
    int ref_len = weight_ref.length;
    int weight_len = weight_up.length;
  //val_1 = rep(0,ref_len)
  //val_2 = rep(0,ref_len)
    double[][] val = new double[ref_len][2];
  //for(i in 1:ref_len){
    //if(weight_ref[i] >= weight_up[weight_len]){
      //val_1[i] = weight_ref[i]
     // val_2[i] = weight_value[weight_len]
    //}else{
      //pos_val = min(weight_up[weight_ref[i] <= weight_up])
      //val_1[i] = weight_ref[i]
      //val_2[i] = weight_value[weight_up == pos_val]
    //}
  //}
  for(int i=0; i<ref_len;i++){
    if(weight_ref[i] >= weight_up[weight_len-1]){
      val[i][0] = weight_ref[i];
      val[i][1] = weight_value[weight_len-1];
    }else{
        int j=0;
        while(j<weight_value.length){
            if(weight_ref[i] < weight_up[j])
                break;
            val[i][0] = weight_ref[i];
            val[i][1] = weight_value[j];
            j++;
        }
    }
  }
  //return(cbind(val_1, val_2))   
        
        return val;
}

    public static double[][] getWeightMatrix(double[][] mat, int bins){//weight_ref, weight_up, weight_value
        //rows = length(mat[,1])
        int rows = mat.length;
        //size_bins = floor(rows/bins)
        int size_bins = rows/bins;
        //sum_x = 0; sum_v = 0; x_points = c(); v_points = c(); v_points_median = c(); 
        double sum_x=0; double sum_v=0; HashMap x_points=new HashMap(1); 
        HashMap v_points = new HashMap(1); HashMap v_points_median = new HashMap(1);
        //w_points = c(); w_points_median = c(); x_low = c(); x_high = c(); v_values = c()
        HashMap w_points = new HashMap(1); HashMap w_points_median = new HashMap(1); 
        //Add two values to record the maximum value Shouguo
        double w_points_max=0; double w_points_median_max=0;
        //Shouguo
        HashMap x_low = new HashMap(1); HashMap x_high = new HashMap(1); Vector v_values = new Vector(1);
        //counter = 1; bins_filled = 0; bins_minus_one = bins - 1;
        int counter=1; int bins_filled=0; int bins_minus_one = bins-1; 
        //num_points = 1; num_points_vector = c();
        int num_points = 1; HashMap num_points_vector = new HashMap(1);
//      for(i in 1:rows){
        for(int i=1; i<=rows; i++){
            //if((i %% size_bins == 0) && (bins_filled < bins_minus_one)){
            if((i % size_bins == 0) && (bins_filled < bins_minus_one)){
                //sum_x = sum_x + mat[i,1]
                sum_x=sum_x + mat[i-1][0];
                //sum_v = sum_v + mat[i,2]
                sum_v= sum_v + mat[i-1][1];
                //x = (sum_x/num_points)
                //v = (sum_v/num_points)  #The mean variance of the bin
                double x=sum_x/num_points;
                double v = (sum_v/num_points);
                //bin_weight = 1/v        #Weight of the bin (mean)
                double bin_weight = 1/v;
                //v_points[counter] = v
                v_points.put(counter, v);
                //w_points[counter] = bin_weight
                w_points.put(counter, bin_weight);
                //x_high[counter] = mat[i,1];
                x_high.put(counter, mat[i-1][0]);
                //v_points_median[counter] = median(v_values)       #The median variance of the bin
                v_points_median.put(counter, Method.Median(v_values));      // #The median variance of the bin
                //w_points_median[counter] = 1/(median(v_values))   #Weight of the bin (median)
                w_points_median.put(counter, 1/(Method.Median(v_values))); //   #Weight of the bin (median)
                //num_points_vector[counter] = num_points
                num_points_vector.put(counter, num_points);
		//sum_x = 0; sum_v = 0     #Update
		sum_x = 0; sum_v = 0;
		//bins_filled = bins_filled + 1
		bins_filled = bins_filled + 1;
		//counter = counter + 1
		counter = counter + 1;
		//num_points = 1
		num_points = 1;
		//v_values = c()
		v_values = new Vector(1);
            }else{
                if (num_points==1) {
                    //x_low[counter] = mat[i,1]
                    x_low.put(counter, mat[i-1][0]);
                  }
                if (bins_filled==bins_minus_one) {
                    //x_high[counter] = mat[i,1]
                    //num_points_vector[counter] = num_points
                    x_high.put(counter, mat[i-1][0]);
                    num_points_vector.put(counter, num_points);
                }
                //sum_x = sum_x + mat[i,1]
                //sum_v = sum_v + mat[i,2]
		//num_points = num_points + 1
		//v_values = c(v_values,mat[i,2])
                sum_x = sum_x + mat[i-1][0];
                sum_v = sum_v + mat[i-1][1];
		num_points = num_points + 1;
		v_values.add(mat[i-1][1]);
            }
        }
        //#Fill the last bin which may contain up to (2*bins_size - 1) points
        //x = (sum_x/(num_points-1))
        //v = (sum_v/(num_points-1))    #The mean variance of the bin
        //bin_weight <- 1/v             #Weight of the bin (mean)
        //v_points[counter] = v
        //w_points[counter] = bin_weight
        double x = (sum_x/(num_points-1));
        double v = (sum_v/(num_points-1));//    #The mean variance of the bin
        double bin_weight;
        if(v!=0){
            bin_weight= 1/v;//             #Weight of the bin (mean)
            if(bin_weight>w_points_max)
                w_points_max=bin_weight;
        }else{
            bin_weight=1000000;
        }
        v_points.put(counter, v);
        w_points.put(counter, bin_weight);
        //v_points_median[counter] = median(v_values)     #The median variance of the bin
        //w_points_median[counter] = 1/(median(v_values)) #The weight of the bin (median)
        v_points_median.put(counter, Method.Median(v_values));//     #The median variance of the bin
        if(Method.Median(v_values)!=0){
            w_points_median.put(counter, 1/(Method.Median(v_values)));// #The weight of the bin (median)
            if(1/(Method.Median(v_values))>w_points_median_max)
                w_points_median_max=1/(Method.Median(v_values));
        }else
            w_points_median.put(counter, 1000000);
        //#If any of the points had expected value == actual value, the variance will be 0. If by some chance all the points
        //#in one bin end up having all points with variance == 0, the weight will be 1/0 = Inf. The steps below change
        //#this value from Inf to the next biggest value
        //w_points[w_points == Inf] <- max(w_points[w_points != Inf])
        //w_points_median[w_points_median == Inf] <- max(w_points_median[w_points_median != Inf])
        //Shouguo replace the 10000000 with maximum
        Iterator iter = w_points.keySet().iterator();
        while(iter.hasNext()){
            int key = (int) iter.next();
            if((double)w_points.get(key)==10000000)
                w_points.put(key, w_points_max);
        }
        iter = w_points_median.keySet().iterator();
        while(iter.hasNext()){
            int key = (int) iter.next();
            if((double)w_points_median.get(key)==10000000)
                w_points_median.put(key, w_points_median_max);
        }
        //end Shouguo
        //#Scale weight to have max weight = 100
        //w_points = (w_points/max(w_points))*100
        //w_points_median = (w_points_median/max(w_points_median))*100
        iter = w_points.keySet().iterator();
        while(iter.hasNext()){
            int key = (int) iter.next();
//            System.out.println((double)(w_points.get(key))/Method.Max(w_points)*100);
            w_points.put(key, ((double)(w_points.get(key))/w_points_max*100));
        }
        System.out.println("w_points_median_original" + w_points_median);
        iter = w_points_median.keySet().iterator();
        while(iter.hasNext()){
            int key = (int) iter.next();
            w_points_median.put(key, ((double)(w_points_median.get(key))/w_points_median_max*100));
        }
        //#Return for each bin: number of data points, the lower and upper intensity limits of the bin and the weight
        //#NOTE: All x values after the last x, get the value of the last bin
        //ans = cbind(num_points_vector, x_low, x_high, w_points_median)
        System.out.println("num_points_vector:" + num_points_vector);
        System.out.println("x_low"+x_low);
        System.out.println("x_high"+x_high);
        System.out.println("w_points_median" + w_points_median);
        double[][] ans = new double[counter][4];
        for(int i=0; i<counter; i++){
            ans[i][0] = Double.parseDouble(num_points_vector.get(i+1).toString());
            ans[i][1] = Double.parseDouble(x_low.get(i+1).toString());
            ans[i][2] = Double.parseDouble(x_high.get(i+1).toString());
            ans[i][3] = Double.parseDouble(w_points_median.get(i+1).toString());
        }
        return ans;  
    }
    
      public static void main(String[] args){
        //Percentile test = new Percentile(1);
        //double[] values = {1.0, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        //test.setData(values);
        //System.out.println(test.evaluate(77));
        //JFileChooser chooser = new JFileChooser();
        //chooser.showOpenDialog(null);
        //Data.pathwayFile=chooser.getSelectedFile().getPath();
        MethodWeight.calculateWeights();
        MethodWeight.calculateError();
        MethodWeight.generateOutput();
        /*
        int num=100;
        Random rand=new Random(0L);
        double x[]=new double[num];
        double y[]=new double[x.length];
        for(int i=0;i< x.length;++i){
            x[i]=rand.nextDouble()+(i>0?x[i-1]:0);
            y[i]=x[i]*(rand.nextDouble()*0.3+1);
        }
        try{LoessInterpolator loessInterpolator=new LoessInterpolator(0.75,2);
            for(int i=0; i<x.length; i++)
                System.out.println(x[i] + "\t" + y[i] + "\t" + loessInterpolator.interpolate(x, y).value(x[i]));       
            }catch(Exception e){}
                */
      }
}
