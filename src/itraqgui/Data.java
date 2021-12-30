/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itraqgui;
import java.util.*;
/**
 *
 * @author gaos2
 */
public class Data {
    public static String expressionFileName="expression.txt";
    public static String phenoFileName = "";
    static int test=1;
    static HashMap<String, Vector<Double>> weight_results;
    //We use this one to save the data.
    //the zero one is protein-peptide HashMap HashMap<String, Vector<String>>
    //the first one is the peptide inensity data HashMap<String, double[]>
    //the second one is peptide-protein HashMap HashMap<String, String>
    //The third one is normalized data HashMap<String, double[]>
    static Vector<HashMap> data = null;
    static int[] phenotype;
    static int type=8; //8 track or 4 track
    static String fileType=""; 
    static String pathwayFile="C:\\Users\\gaos2\\Documents\\data_IPG_protein_old.txt";//Users/gaos2/Desktop/data_IPG_protein.txt"; //;
    static Vector<HashMap> dataAndMapping =new Vector<HashMap>(1);
    static int rowNum=0;
    static int weightChanel1=113;
    static int weightChanel2=114;
    //The weight of weighted method
    static double[][] weight_matrix;
    //The weight of variance method
    static double[][] variance_matrix;
}
