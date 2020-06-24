import java.io.FileWriter;
import java.util.Iterator;

import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntTestLoading5_ONTS_RES_CT {

    public static Logger logger = LoggerFactory.getLogger(OntTestLoading5_ONTS_RES_CT.class);

    
    private static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";
    
    private static final String ONT_POLICY = "owl-schema/ont-policy.rdf";
        
    private static OntModelSpec oms;
    private static OntDocumentManager odm;
    
    private static void initOdm() {
        oms = new OntModelSpec(OntModelSpec.OWL_MEM);        
        odm = new OntDocumentManager(ONT_POLICY);
        oms.setDocumentManager(odm);
        FileManager odFm = odm.getFileManager();
        writeTtl(odFm.getLocationMapper().toModel(), "LOCATOR_ONT_RES_CT_05");
        System.exit(0);
    }    
    
    private static void writeTtl(Model m, String nm) {
        try {
            m.write(new FileWriter(OUT+nm+".ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }
    
    private static void doAll(String name) {
        logger.info("processing ALL from {}", name);

        odm.setProcessImports(true);
        odm.setCacheModels(true);

        Iterator<String> allItr = odm.listDocuments();
        OntModel allModel = null;
        while (allItr.hasNext()) {
            String uri = allItr.next();
            OntModel m = odm.getOntology(uri, oms);
            logger.info("got model of size {} for uri {}", (m != null ? m.size() : 0), uri);
            if (m != null) {
                if (allModel == null)
                    allModel = m;
                else
                    allModel.add(m);
            }
        }

        System.out.println("\n\ndoAll("+name+").getModelSize() == "+allModel.size());
        writeTtl(allModel, name);
    }
    
    public static void main(String[] args){
        
        initOdm();
        doAll("ALL_ONTS_RES_CT_05");
    }
}
