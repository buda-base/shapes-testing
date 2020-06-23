
import java.io.FileWriter;
import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.Locator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntTestLoading5_SHAPES_GH {

    public static Logger logger = LoggerFactory.getLogger(OntTestLoading5_SHAPES_GH.class);

    
    private static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";
    
    private static final String ONT_POLICY = "https://raw.githubusercontent.com/buda-base/editor-templates/master/ont-policy.rdf";
        
    private static OntModelSpec oms;
    private static OntDocumentManager odm;
    
    private static void initOdm() {
        oms = new OntModelSpec(OntModelSpec.OWL_MEM);        
        odm = new OntDocumentManager(ONT_POLICY);
        oms.setDocumentManager(odm);
        FileManager odFm = odm.getFileManager();
        writeTtl(odFm.getLocationMapper().toModel(), "LOCATOR_SHAPES_GH_05");
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
        Model allModel = ModelFactory.createDefaultModel();
        while (allItr.hasNext()) {
            String uri = allItr.next();
            Model m = odm.getOntology(uri, oms);
            logger.info("got model of size {} for uri {}", (m != null ? m.size() : 0), uri);
            if (m != null) {
                allModel.add(m);
            }
        }

        System.out.println("\n\ndoAll("+name+").getModelSize() == "+allModel.size());
        writeTtl(allModel, name);
    }
    
    public static void main(String[] args){
        
        initOdm();
        doAll("ALL_SHAPES_GH_05");
    }
}
