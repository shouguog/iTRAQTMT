import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDBI {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        String filename="L:\\Lab-Wang\\Projects\\Proteomics Core\\Literature on iTRAQ statistics\\Young_vs_Old_mouse_hearts_itraq.msf";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
        Statement stat = conn.createStatement();
        //
        ResultSet rs = null;
        System.out.println("################ReporterIonQuanResultsSearchSpectra###################");
        rs=stat.executeQuery("PRAGMA table_info(ReporterIonQuanResultsSearchSpectra);"); 
        while(rs.next()){
            System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4));
        }
        rs.close();
       System.out.println("################ReporterIonQuanResults###################");
        rs=stat.executeQuery("PRAGMA table_info(ReporterIonQuanResults);"); 
        while(rs.next()){
            System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4));
        }
        rs.close();//        AS riqrss ON riqr.SpectrumID=riqrss.SpectrumID;");
       System.out.println("################PeptidesProteins###################");
        rs=stat.executeQuery("PRAGMA table_info(PeptidesProteins);"); 
        while(rs.next()){
            System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4));
        }
        rs.close();//        AS riqrss ON riqr.SpectrumID=riqrss.SpectrumID;");
       System.out.println("################Peptides###################");
        rs=stat.executeQuery("PRAGMA table_info(Peptides);"); 
        while(rs.next()){
            System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4));
        }
        rs.close();//        AS riqrss ON riqr.SpectrumID=riqrss.SpectrumID;");
       System.out.println("################ProteinAnnotations###################");
        rs=stat.executeQuery("PRAGMA table_info(ProteinAnnotations);"); 
        while(rs.next()){
            System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4));
        }
        rs.close();//        AS riqrss ON riqr.SpectrumID=riqrss.SpectrumID;");
        String rsString="SELECT * FROM ProteinAnnotations;";
        System.out.println(rsString);
        rs=stat.executeQuery(rsString);
        for(int i=0; i<20; i++){
            rs.next();
            System.out.print(rs.getString(1));
            for(int j=2; j<5; j++){
                System.out.print("\t"+rs.getString(j));
            }
            System.out.println();
        }
        rs.close(); 
        rsString="SELECT ReporterIonQuanResults.QuanChannelID, ReporterIonQuanResults.SpectrumID, ReporterIonQuanResults.Mass, ReporterIonQuanResults.Height, Peptides.PeptideID, Peptides.SpectrumID FROM ReporterIonQuanResults, Peptides WHERE ReporterIonQuanResults.SpectrumID=Peptides.SpectrumID;";
        System.out.println(rsString);
        rs=stat.executeQuery(rsString);
        for(int i=0; i<20; i++){
            rs.next();
            System.out.print(rs.getString(1));
            for(int j=2; j<5; j++){
                System.out.print("\t"+rs.getString(j));
            }
            System.out.println();
        }
        rs.close(); 

        /*
        rs=stat.executeQuery("SELECT riqr.ProcessingNodeNumber, riqr.QuanChannelID,riqr.SpectrumID,riqr.Mass,riqr.Height,riqrss.SearchSpectrumID FROM ReporterIonQuanResults AS riqr INNER JOIN ReporterIonQuanResultsSearchSpectra AS riqrss ON riqr.SpectrumID=riqrss.SpectrumID;");
        for(int i=0; i<20; i++){
            //rs.next();
            //System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+rs.getString(6));
        }
        //rs.close();
        //
        int confidence=1;
        String rsString="SELECT p.PeptideID,p.ProcessingNodeNumber,p.Sequence,p.SpectrumID,p.TotalIonsCount,p.MatchedIonsCount,p.ConfidenceLevel,p.SearchEngineRank,p.Annotation,"                   
                +"p.UniquePeptideSequenceID,p.MissedCleavages,sh.ScanNumbers,sh.RetentionTime,sh.Mass,sh.Charge,sh.MassPeakID,mp.Intensity,mp.PercentIsolationInterference,"
            +"mp.IonInjectTime,mp.Mass AS MZ,se.MSLevel,se.ActivationType,fi.FileName FROM Peptides AS p, SpectrumHeaders AS sh, MassPeaks AS mp, ScanEvents AS se, FileInfos AS fi, SpectrumScores AS ss "
            +"WHERE p.SpectrumID=sh.SpectrumID AND p.ConfidenceLevel >=" + confidence + " "
            +"AND sh.MassPeakID=mp.MassPeakID AND sh.ScanEventID=se.ScanEventID AND mp.FileID=fi.FileID "
            //AND p.SpectrumID = ss.SpectrumID\n",
            +"GROUP BY p.PeptideID;";
        System.out.println(rsString);
        rs=stat.executeQuery(rsString);
        System.out.println("p.PeptideID\tp.ProcessingNodeNumber\tp.Sequence\tp.SpectrumID\tp.TotalIonsCount\tp.MatchedIonsCount\tp.ConfidenceLevel\tp.SearchEngineRank\tp.Annotation\tp.UniquePeptideSequenceID\tp.MissedCleavages\tsh.ScanNumbers\tsh.RetentionTime\tsh.Mass\tsh.Charge\tsh.MassPeakID\tmp.Intensity\tmp.PercentIsolationInterference\tmp.IonInjectTime\tmp.Mass\tse.MSLevel\tse.ActivationType\tfi.FileName");
        for(int i=0; i<20; i++){
            rs.next();
            System.out.print(rs.getString(1));
            for(int j=2; j<20; j++){
                System.out.print("\t"+rs.getString(j));
            }
            System.out.println();
        }
        rs.close(); 
        //
        rsString="SELECT p.Sequence AS PeptideSequence, pprot.PeptideID, pprot.ProteinID, prot.Sequence,prot.IsMasterProtein,protAnnot.Description, pscores.ProteinScore FROM PeptidesProteins pprot INNER JOIN Peptides p ON pprot.PeptideID=p.PeptideID "
                +"INNER JOIN Proteins prot ON pprot.ProteinID=prot.ProteinID INNER JOIN ProteinAnnotations protAnnot ON pprot.ProteinID = protAnnot.ProteinID INNER JOIN ProteinScores pscores ON pprot.ProteinID = pscores.ProteinID;\"";
        System.out.println(rsString);
        rs=stat.executeQuery(rsString);
        System.out.println("p.Sequence\tpprot.PeptideID\tpprot.ProteinID\tprot.Sequence\tprot.IsMasterProtein\tprotAnnot.Description\tpscores.ProteinScore");
        for(int i=0; i<20; i++){
            rs.next();
            System.out.print(rs.getString(1));
            for(int j=2; j<8; j++){
                System.out.print("\t"+rs.getString(j));
            }
            System.out.println();
        }
        rs.close(); 
        */
        //
        
        conn.close();
    }
  }