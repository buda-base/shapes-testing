
import java.io.FileWriter;

import org.apache.jena.graph.Graph;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntTestLoading3 {

    public static Logger logger = LoggerFactory.getLogger(OntTestLoading3.class);

    private static final String ONT_POLICY = "https://raw.githubusercontent.com/buda-base/editor-templates/master/ont-policy.rdf";
    private static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";
    private static final String PERSON_SHAPES = "http://purl.bdrc.io/shapes/core/PersonShapes/";
    private static final String PERSON_LOCAL_SHAPES = "http://purl.bdrc.io/shapes/core/PersonLocalShapes/";
    private static final String PERSON_UI_SHAPES = "http://purl.bdrc.io/shapes/core/PersonUIShapes/";
    
    private static OntModelSpec oms;
    private static OntDocumentManager odm;
    
    private static void initOdm() {
        oms = new OntModelSpec(OntModelSpec.OWL_MEM);        
        odm = new OntDocumentManager(ONT_POLICY);        
        oms.setDocumentManager(odm);
    }
    
    private static void writeTtl(Model m, String nm) {
        try {
            m.write(new FileWriter(OUT+nm+".ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
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
        doTest(PERSON_LOCAL_SHAPES, true, "PersonLocalShapes_ALL05");

        initOdm();
        doTest(PERSON_SHAPES, true, "PersonShapes_ALL05");

//        initOdm();
//        doTest(PERSON_UI_SHAPES, true, "PersonUIShapes_ALL05");
    }
}
