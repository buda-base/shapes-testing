
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

public class OntTestLoading4 {

    public static Logger logger = LoggerFactory.getLogger(OntTestLoading4.class);

//    private static final String ONT_POLICY = "https://raw.githubusercontent.com/buda-base/editor-templates/master/ont-policy-local.rdf";
//    private static final String ONT_POLICY = "/Users/chris/git/editor-templates/ont-policy.rdf";
//    private static final String ONT_POLICY = "/Users/chris/git/editor-templates/ont-policy-local.rdf";
//    private static final String ONT_POLICY = "/Users/chris/git/owl-schema/ont-policy.rdf";
    
    private static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";
    private static final String PERSON_SHAPES = "http://purl.bdrc.io/shapes/core/PersonShapes/";
    private static final String PERSON_LOCAL_SHAPES = "http://purl.bdrc.io/shapes/core/PersonLocalShapes/";
    private static final String PERSON_UI_SHAPES = "http://purl.bdrc.io/shapes/core/PersonUIShapes/";
    
//    private static final String ROOT = "file://Users/chris/git/owl-schema";
//    private static final String ONT_POLICY = "/Users/chris/git/owl-schema/ont-policy-local.rdf";
    
    private static final String ROOT = "file://Users/chris/git/editor-templates";
    private static final String ONT_POLICY = "/Users/chris/git/editor-templates/ont-policy-local.rdf";
    
    private static OntModelSpec oms;
    private static OntDocumentManager odm;
    
    private static void initOdm() {
        FileManager fm = FileManager.get().clone(); // the global FileManager
        fm.addLocatorFile(ROOT);
        oms = new OntModelSpec(OntModelSpec.OWL_MEM);        
        odm = new OntDocumentManager(fm, ONT_POLICY);        
        oms.setDocumentManager(odm);
        writeTtl(fm.getLocationMapper().toModel(), "LOCATOR_SHAPES_LOCAL09");
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
    
    private static void doTest(String ontUri, boolean processImports, String graphLocalName) {
        logger.info("processing {} imports {}", graphLocalName, processImports);
        
        odm.setProcessImports(processImports);
        
        // fetch OntModel using the value of processImports - overrides the ont-policy.rdf
        OntModel om = odm.getOntology(ontUri, oms);
        
        // write out the model at the ontUri independent of the imports
        // writing the om does not include the sub-models from imports if any
        System.out.println("\n\ngetOntology("+graphLocalName+").getModelSize() == "+om.size());
        writeTtl(om, graphLocalName+"_ONT_MOD_GET_ONT");
        
        // write out the entire model including imports if any
        Graph g = om.getGraph();
        Model m4g = ModelFactory.createModelForGraph(g);
        writeTtl(m4g, graphLocalName+"_M4G");
    }
    
    public static void main(String[] args){
        
        initOdm();
        doAll("ALL_SHAPES_09");
//        doTest(PERSON_LOCAL_SHAPES, true, "PersonLocalShapes_ALL07");
//        doTest(PERSON_SHAPES, true, "PersonShapes_ALL07");
//        doTest(PERSON_UI_SHAPES, true, "PersonUIShapes_ALL07");
    }
}
