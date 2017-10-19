package services.ensembl;

import java.util.ArrayList;
import java.util.List;
import uk.ac.roslin.ensembl.config.DBConnection.DataSource;
import uk.ac.roslin.ensembl.config.EnsemblCoordSystemType;
import uk.ac.roslin.ensembl.dao.database.DBRegistry;
import uk.ac.roslin.ensembl.dao.database.DBSpecies;
import uk.ac.roslin.ensembl.dao.factory.DAOCoreFactory;
import uk.ac.roslin.ensembl.datasourceaware.core.DAChromosome;
import uk.ac.roslin.ensembl.datasourceaware.core.DADNASequence;
import uk.ac.roslin.ensembl.datasourceaware.core.DAGene;
import uk.ac.roslin.ensembl.model.core.DNASequence;


/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/11/2012
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public class EnsemblTest {

    /**
     *
     * @author tpaterso
     */

     //demonstrating the use of the chromosome cache
     //chromosomes for a given species/version are only instantiated once - and then cached for reuse
     //their properties are lazy loaded as needed

     //if they are wrongly instantiated as Generic DADNASequences - they can be validated
     //note code fixed and working at release 67 ( this is id dependent tests )

    public static void main(String[] args) throws Exception {

        DBRegistry eReg = new DBRegistry(DataSource.ENSEMBLDB);
        DBSpecies sp = eReg.getSpeciesByAlias("human");

        DAChromosome c1 = null;

        try {
            DAChromosome chr = sp.getChromosomeByName("20");

            List<DAGene> list = chr.getGenesOnRegion(0, chr.getLength());

            System.out.println("list.size()="+list.size());
            /*
            for (DAGene g : list) {
                g.getBiotype()

                sp.getAssembly()


                g.getDisplayName()
                list.get(0).getStableID()
                list.get(0).getChromosomeMapping().getTargetCoordinates().start
                list.get(0).getChromosomeMapping().getTargetCoordinates().getEnd()
                list.get(0).getChromosomeMapping().getTargetCoordinates().getStart()

            }
            */

            DAGene g = sp.getGeneByStableID("ENSG00000153551", "69");
            c1 = (DAChromosome) g.getLoadedMappings(EnsemblCoordSystemType.chromosome).first().getTarget();
            DAChromosome c1_2 = (DAChromosome) g.getChromosomeMapping().getTarget();
            System.out.println("first  fetch of chr: "+c1.getHashID() +" : "+c1.hashCode());
            System.out.println("second  fetch of chr: "+c1_2.getHashID() +" : "+c1_2.hashCode());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        DAChromosome c2 = sp.getChromosomeByName(c1.getChromosomeName(), "69");
        System.out.println("third fetch of chr: "+c2.getHashID() +" : "+c2.hashCode());

        DAGene g2 = sp.getGeneByStableID("ENSG00000170293", "69");
        DAChromosome c3 = (DAChromosome) g2.getLoadedMappings(EnsemblCoordSystemType.chromosome).first().getTarget();
        DAChromosome c3_2 = (DAChromosome) g2.getChromosomeMapping().getTarget();
        System.out.println("fourth  fetch of chr: "+c3.getHashID() +" : "+c3.hashCode());
        System.out.println("fifth  fetch of chr: "+c3_2.getHashID() +" : "+c3_2.hashCode());

        DAChromosome c4 = sp.getChromosomeByName("4","67");
        System.out.println("first  fetch of chr: "+c4.getHashID() +" : "+c4.hashCode());

        DAGene g3 = sp.getGeneByStableID("ENSG00000186777","67");
        DAChromosome c5 = (DAChromosome) g3.getLoadedMappings(EnsemblCoordSystemType.chromosome).first().getTarget();
        DAChromosome c5_2 = (DAChromosome) g3.getChromosomeMapping().getTarget();
        System.out.println("second fetch of chr: "+c5.getHashID() +" : "+c5.hashCode());
        System.out.println("third fetch of chr: "+c5_2.getHashID() +" : "+c5_2.hashCode());

        DADNASequence s = (DADNASequence) g3.getDaoFactory().getSequenceDAO().getSequenceByID(27524);
        System.out.println("fourth  fetch of chr: "+s.getHashID() +" : "+s.hashCode());

        DADNASequence s1 = new DADNASequence(g3.getDaoFactory());
        s1.setId(27524);
        System.out.println("s1 type before validation - should be DASequence: "
                +s1.getClass().getSimpleName() +" : "+s1.getHashID() +" : "+s1.hashCode());

        //note this changes the object referenced by 's1'
        s1 = (DADNASequence) g3.getDaoFactory().getSequenceDAO().getValidatedSequence(s1);

        System.out.println("s1 type after validation - should be DAChromosome: "
                +s1.getClass().getSimpleName() +" : "+s1.getHashID() +" : "+s1.hashCode() );

        //this will do the query but then replace the result with the cached chromosome!
        DADNASequence s2 = (DADNASequence) g3.getDaoFactory().getSequenceDAO().getSequenceByID(27524);

        System.out.println("s2 type before validation- should be a chromosome: "
                +s2.getClass().getSimpleName()+" : "+s2.getHashID() +" : "+s2.hashCode() );

        s2 = (DADNASequence) g3.getDaoFactory().getSequenceDAO().getValidatedSequence(s2);
        System.out.println("s2 type after validation- should still be a chromosome: "
                +s2.getClass().getSimpleName()+" : "+s2.getHashID() +" : "+s2.hashCode());


        //showing validation works to convert from DADNASequence
        //objects to DAAssembledSequences or DAChromosomes

        DAOCoreFactory f = g3.getDaoFactory();

        DADNASequence s4 = new DADNASequence(f);
        s4.setId(27524);
        DADNASequence s5 = new DADNASequence(f);
        s5.setId(54919);
        DADNASequence s6 = new DADNASequence(f);
        DADNASequence s8 = new DADNASequence(f);
        s8.setId(-999);

        List<DNASequence> test = new ArrayList<DNASequence>();
        test.add(s4);
        test.add(s5);
        test.add(s6);
        test.add(s8);

        System.out.println("");
        System.out.println("before");

        for (DNASequence x : test) {
            if (x == null) {
                System.out.println("null object");
            }
            System.out.println(x.getId()+" : "+x.getClass().getSimpleName()+" : "+x.getHashID()+" : "+x.hashCode());
        }

        test =  (List<DNASequence> ) f.getSequenceDAO().getValidatedSequences(test);

        System.out.println("");
        System.out.println("after");
        for (DNASequence x : test) {
            System.out.println(x.getId()+" : "+x.getClass().getSimpleName()+" : "+x.getHashID()+" : "+x.hashCode());
        }

        System.out.println("\n\n*************************\nCOMPLETED FUNCTIONAL TEST\n*************************\n");
    }


}
