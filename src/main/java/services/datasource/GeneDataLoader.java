package services.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.GeneFeature;
import services.util.ProgressTracker;
import services.util.Util;
import uk.ac.roslin.ensembl.config.DBConnection;
import uk.ac.roslin.ensembl.dao.database.DBRegistry;
import uk.ac.roslin.ensembl.dao.database.DBSpecies;
import uk.ac.roslin.ensembl.datasourceaware.core.DAChromosome;
import uk.ac.roslin.ensembl.datasourceaware.core.DAGene;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 21/03/2013
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
public class GeneDataLoader {

    private final static Logger log = LoggerFactory.getLogger(GeneDataLoader.class);

    private final static String[] chrNames = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                                 "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                                 "X", "Y", "MT" };

    public static HashMap<String, List<DAGene>> loadDB() {

        HashMap<String, List<DAGene>> chrGenes = new HashMap<String, List<DAGene>>();

        try {
            DBRegistry eReg = new DBRegistry(DBConnection.DataSource.ENSEMBLDB);
            DBSpecies sp = eReg.getSpeciesByAlias("human");

            for (String chrName : chrNames) {
                DAChromosome chr = sp.getChromosomeByName(chrName);

                log.info("getting genes for chr " + chrName);

                List<DAGene> list = chr.getGenesOnRegion(0, chr.getLength());

                log.info("got list.size()=" + list.size());

                chrGenes.put(chrName, list);
            }

        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        return chrGenes;
    }

    public static void dumpData(HashMap<String, List<DAGene>> chrGenes, String fname){

        try{
            // Create file
            FileWriter fstream = new FileWriter(fname);
            BufferedWriter out = new BufferedWriter(fstream);

            for (String key : chrGenes.keySet()) {
                List<DAGene> list = chrGenes.get(key);

                for (DAGene g : list) {
                    out.write(g.getDisplayName() + "\t" +
                            g.getStableID() + "\t" +
                            g.getBiotype() + "\t" +
                            g.getDescription() + "\t" +
                            key + "\t" +
                            g.getChromosomeMapping().getTargetCoordinates().getStart().toString() + "\t" +
                            g.getChromosomeMapping().getTargetCoordinates().getEnd().toString() + "\t" +
                            "\n");
                }
            }

            //Close the output stream
            out.close();
        } catch (Exception ex){
            log.error("Error!", ex);
        }
    }

    public static HashMap<String, List<GeneFeature>> loadData(InputStream stream) {

        HashMap<String, List<GeneFeature>> listMap = new HashMap<String, List<GeneFeature>>();

        try{
            Scanner scanner = new Scanner(stream);

            log.info("size = " + Util.humanizeSize(stream.available()));

            ProgressTracker tracker = new ProgressTracker(stream.available());

            try {
       			//first use a Scanner to get each line
                while ( scanner.hasNextLine() ) {
                    String line = scanner.nextLine();

                    if (line.length() > 0) {

                        /*
                            AC008993.1	ENSG00000225373	protein_coding	null	19	65917	70966
                            MIR1302-10	ENSG00000220978	miRNA	microRNA 1302-10 [Source:HGNC Symbol;Acc:38233]	19	71973	72110
                            FAM138F	ENSG00000233630	protein_coding	family with sequence similarity 138, member F [Source:HGNC Symbol;Acc:33581]	19	76222	77686
                        */

                        String[] parts = line.split("\t");

                        String displayName = parts[0]; // displayName
                        String ensemblId   = parts[1]; // ensemblId
                        String biocType    = parts[2]; // bioctype
                        String description = parts[3]; // description
                        String chr         = parts[4]; // chr
                        int    start       = Integer.parseInt(parts[5]); // start
                        int    end         = Integer.parseInt(parts[6]); // end

                        List<GeneFeature> list = listMap.get(chr);

                        // check
                        if (list == null) {
                            list = new ArrayList<GeneFeature>();
                            listMap.put(chr, list);
                        }

                        list.add(new GeneFeature(displayName, ensemblId, biocType, description, chr, start, end));
                    }

                    if (tracker.track(line.length())) {
                        log.info(tracker.report());
                    }
       			}
       		} finally {
       			//ensure the underlying stream is always closed
       			scanner.close();
                System.gc();
       		}

        }catch (Exception ex){
            log.error("Error!", ex);
        }

        return listMap;
    }


    public static void main(String[] args){
        log.info("loading the db");

        HashMap<String, List<DAGene>> chrGenes = loadDB();

        String fname = System.getProperty("gene.fname");

        log.info("dumping the data");

        dumpData(chrGenes, fname);

        log.info("validation");

        log.info(" -- DB -- ");

        int total_loaded_from_db = 0;

        for (String key : chrGenes.keySet()) {
            total_loaded_from_db += chrGenes.get(key).size();

            log.info(key + " -> " + chrGenes.get(key).size() + " loaded");
        }

        log.info("total loaded from DB = " + total_loaded_from_db);


        log.info(" -- FILE -- ");


        try {
            HashMap<String, List<GeneFeature>> chrGenesLoaded = loadData(new FileInputStream(fname));

            int total_loaded_from_file = 0;

            for (String key : chrGenesLoaded.keySet()) {
                total_loaded_from_file += chrGenesLoaded.get(key).size();

                log.info(key + " -> " + chrGenesLoaded.get(key).size() + " loaded");
            }

            log.info("total loaded from DB = " + total_loaded_from_file);

        } catch (Exception ex) {
            log.info("Error!", ex);
        }


    }

}
