/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itraqgui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
 *
 * @author gaos2
 */
public class MethodSAM {
    public static void calculateWeights(){
    double ratio = 1;           //#Expected ratio between duplicates
    int bins = 8;            //#Number of bins for weight calculation
    int rowNum=1187;
    double[][] pepIntensity=new double[1187][2];
    try (BufferedReader br = new BufferedReader(
        new FileReader(Data.pathwayFile + "_protein.txt"))) {
            String line=br.readLine();//header
            for(int i=0; i<rowNum; i++){
                line = br.readLine();
                String[] lineItem=line.split("\t");
                pepIntensity[i][0]=Double.parseDouble(lineItem[2]);
                pepIntensity[i][1]=Double.parseDouble(lineItem[3]);
            }
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
    double[][] mat = variance_matrix;
    int rows = mat.length;
    int size_bins = rows/bins;
        HashMap x_low = new HashMap(1); HashMap x_high = new HashMap(1); Vector v_values = new Vector(1);
        //counter = 1; bins_filled = 0; bins_minus_one = bins - 1;
        int counter=1; int bins_filled=0; int bins_minus_one = bins-1; 
        //num_points = 1; num_points_vector = c();
        int num_points = 1; 
//      for(i in 1:rows){
        for(int i=1; i<=rows; i++){
            //if((i %% size_bins == 0) && (bins_filled < bins_minus_one)){
            if((i % size_bins == 0) && (bins_filled < bins_minus_one)){
                x_high.put(counter, mat[i-1][0]);
                counter++;
       		bins_filled = bins_filled + 1;
     		num_points = 1;
            }else{
                if (num_points==1) {
                    //x_low[counter] = mat[i,1]
                    x_low.put(counter, mat[i-1][0]);
                  }
                if (bins_filled==bins_minus_one) {
                    x_high.put(counter, mat[i-1][0]);
                }
   		num_points = num_points + 1;
            }
        }
        System.out.println(x_high);
        System.out.println(x_low);
    }
         public static void main(String[] args){
            MethodSAM.calculateWeights();
         }
}
