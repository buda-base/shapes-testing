
import java.io.FileWriter;

import org.apache.jena.graph.Graph;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntTestLoading2 {

    public static Logger logger = LoggerFactory.getLogger(OntTestLoading2.class);

    private static final String ONT_POLICY = "https://raw.githubusercontent.com/buda-base/editor-templates/master/ont-policy.rdf";
    private static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";
    private static final String DS = "http://buda1.bdrc.io:13180/fuseki/newcorerw/";
    
    private static RDFConnection fuConn;
    
    private static final String BDG = "http://purl.bdrc.io/graph/";
    private static final String PERSON_SHAPES = "http://purl.bdrc.io/shapes/core/PersonShapes/";
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
//    
//    private static void writeTtl(OntModel m, String nm) {
//        try {
//            m.writeAll(new FileWriter(OUT+nm+".ttl"), "TTL");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }        
//    }
    
    private static void doTest(String ontUri, boolean processImports, String graphLocalName) {
        doTest(oms, odm, ontUri, processImports, graphLocalName, true);
    }
    
    private static void doTest(String ontUri, boolean processImports, String graphLocalName, boolean rt) {
        doTest(oms, odm, ontUri, processImports, graphLocalName, rt);
    }
    
    private static void doTest(OntModelSpec oms, OntDocumentManager odm, String ontUri, boolean processImports, String graphLocalName, boolean rt) {
        logger.info("processing {} imports {}", graphLocalName, processImports);
        
        odm.setProcessImports(processImports);
        
        // fetch OntModel
        OntModel om = odm.getOntology(ontUri, oms);
        writeTtl(om, graphLocalName+"_ONT_MOD_GET_ONT");
        
        Graph g = om.getGraph();
        Model m4g = ModelFactory.createModelForGraph(g);
        writeTtl(m4g, graphLocalName+"_M4G");
        
        OntModel unionOM = ModelFactory.createOntologyModel();
        unionOM.add(om.getBaseModel());
        ExtendedIterator<OntModel> it = om.listSubModels();
        while (it.hasNext()) {
            OntModel sm = it.next();
            unionOM.add(sm);
        }
        writeTtl(m4g, graphLocalName+"_UNION_OM");
        
        if (rt) {
            // send OntModel to Fuseki and retrieve Model by graph name
            String graphName = BDG+graphLocalName;
            fuConn.put(graphName, om);
            Model mFmFu = fuConn.fetch(graphName);
            writeTtl(mFmFu, graphLocalName+"_MODEL_FM_FUSEKI");
        }
    }
    
    public static void main(String[] args){
        
        fuConn = RDFConnectionFuseki.create().destination(DS).build();        

//        // PROCESS IMPORTS FALSE
//        initOdm();
//        doTest(PERSON_SHAPES, false, "PersonShapes_NI13");        
//
//        // PROCESS IMPORTS TRUE
//        initOdm();        
//        doTest(PERSON_SHAPES, true, "PersonShapes_WI13");
//
//        // PROCESS IMPORTS FALSE
//        initOdm();
//        doTest(PERSON_UI_SHAPES, false, "PersonUIhapes_NI13");        
//
//        // PROCESS IMPORTS TRUE
//        initOdm();        
//        doTest(PERSON_UI_SHAPES, true, "PersonUIShapes_WI13");

        // PROCESS IMPORTS TRUE
        initOdm();        
        doTest(PERSON_UI_SHAPES, true, "PersonUIShapes_BASE", false);
    }
}
