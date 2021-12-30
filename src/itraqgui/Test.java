/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itraqgui;
import java.awt.HeadlessException;
import java.util.*;
import javastat.inference.nonparametric.RankSumTest;
import javastat.inference.twosamples.TwoSampMeansTTest;
import javastat.multivariate.PCA;
import javastat.util.DataManager;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
/**
 *
 * @author gaos2
 */
public class Test {
  public static void main(String[] args) throws HeadlessException {
    //Vector<String> test = new Vector(1);
    //test.set(1, "jsddjksj");
    //System.out.println(test);
     /*
      Percentile test = new Percentile(1);
      double[] values = {1.0, 2, 3, 4, 5, 6, 7, 8, 9, 10};
      test.setData(values);
      System.out.println(test.evaluate(77));
      HashMap<Double, String> map = new HashMap<Double, String>(1);
      map.put(0.1, "0.1");
      map.put(0.2, "0.2");
      map.put(0.5, "0.5");
      map.put(0.4, "0.4");
      map.put(0.3, "0.3");
      Map<Double, String> mapSort = new TreeMap<Double, String>(map);
      Set set2 = mapSort.entrySet();
      Iterator iterator2 = set2.iterator();
      while(iterator2.hasNext()) {
              Map.Entry me2 = (Map.Entry)iterator2.next();
              System.out.print(me2.getKey() + ": ");
              System.out.println(me2.getValue());
     }
              int num=100;
        Random rand=new Random(0L);
        double x[]=new double[num];     
        for(int i=0;i< x.length;++i){
            double a =rand.nextDouble();
            double b =rand.nextDouble();
            x[i]=a; ///b;
        }
//        Regression.fitOneOrSeveralDistributions(x);
        //double[] coeff=Regression.fitCauchy(x);
        //System.out.println(Stat.lorentzianCDF(coeff[0], coeff[1], -10, 50));
        //Regression model = new Regression();
        //model.lorentzian();
        double [] testdata1 = {1, 2, 3, 4, 5}; 
        double [] testdata2 = {6, 7, 8, 9, 10,11}; 
        // Non-null constructor: 
        TwoSampMeansTTest testclass1 = new TwoSampMeansTTest(0.05, 0, "equal", testdata1, testdata2); 
        System.out.println("t\t"+testclass1.testStatistic); 
        System.out.println("p value\t"+testclass1.pValue); 
        //R result
        //t = -5.2842, df = 8.9894, p-value = 0.0005062
        //alternative hypothesis: true difference in means is not equal to 0
        //95 percent confidence interval:
        //-7.854953 -3.145047
        // Non-null constructor 
        RankSumTest testclass2 = new RankSumTest(0.05, "equal", testdata1, testdata2); 
        System.out.println("RankSumValue\t"+testclass2.testStatistic); 
        System.out.println("RankSumt\t"+testclass2.pValue); 
        double[][] testscores = { {36, 62, 31, 76, 46, 12, 39, 30, 22, 9, 32, 40, 64, 36, 24, 50, 42, 2, 56, 59, 28, 19, 36, 54, 14},
                                {36, 62, 31, 76, 46, 12, 39, 30, 22, 9, 32, 40, 64, 36, 24, 50, 42, 2, 56, 59, 28, 19, 36, 54, 15},
                             {58, 54, 42, 78, 56, 42, 46, 51, 32, 40, 49, 62, 75, 38, 46, 50, 42, 35, 53, 72, 50, 46, 56, 57, 35},
                             {43, 50, 41, 69, 52, 38, 51, 54, 43, 47, 54, 51, 70, 58, 44, 54, 52, 32, 42, 70, 50, 49, 56, 59, 38},
                             {36, 46, 40, 66, 56, 38, 44, 42, 28, 30, 37, 40, 66, 62, 55, 52, 38, 22, 40, 66, 42, 40, 54, 62, 20},
                             {37, 52, 29, 81, 40, 28, 51, 42, 22, 24, 52, 49, 63, 62, 49, 51, 50, 16, 32, 62, 63, 30, 52, 58, 20},
                             {36, 46, 40, 66, 56, 38, 54, 52, 28, 30, 37, 40, 66, 62, 55, 52, 38, 22, 40, 66, 42, 40, 54, 62, 29},
                             {37, 52, 29, 81, 40, 28, 41, 32, 22, 24, 52, 49, 63, 62, 49, 51, 50, 16, 32, 62, 63, 30, 32, 68, 29}};
        DataManager dm = new DataManager();
        PCA testclass = new PCA();
        double[][] principalComponents = testclass.principalComponents(testscores);
        double[] variance = testclass.variance(testscores);
        for(int i=0; i<principalComponents.length; i++)
            System.out.println(principalComponents[0][i]+"\t"+principalComponents[1][i]+"\n");
        
        */
        
  }
}
