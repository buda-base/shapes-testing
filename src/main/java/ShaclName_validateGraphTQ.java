import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationEngine;
import org.topbraid.shacl.validation.ValidationEngineConfiguration;
import org.topbraid.shacl.validation.ValidationUtil;

public class ShaclName_validateGraphTQ {

    public static Logger logger = LoggerFactory.getLogger(ShaclName_validateGraphTQ.class);

    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "ShapeName_01.ttl";
    static final String DATA = "DATA_P707.ttl";

    static ValidationEngine engine;
    static Graph dataGraph;
    static Model dataModel;
    static Graph shapesGraph;
    static Model shapesModel;
    
    static void initEngine() {
        try {
            ValidationEngineConfiguration config = new ValidationEngineConfiguration().setValidateShapes(true);
            engine = ValidationUtil.createValidationEngine(dataModel, shapesModel, config);
            engine.applyEntailments();
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }    
    }

    static {
        dataGraph = RDFDataMgr.loadGraph(DATA);
        dataModel = ModelFactory.createModelForGraph(dataGraph);
        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);
        initEngine();
    }

    public static void process(Resource focus) throws InterruptedException {
        logger.info("Validating Node {} with {}", focus.getLocalName(), SHAPES);
        Resource report = engine.validateNode(focus.asNode());
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }

    public static void main(String ...args) {
        try {
            process(ResourceFactory.createResource(BDR+"P707"));
            initEngine();
            process(ResourceFactory.createResource(BDR+"NMC2A097019ABA499F"));
            initEngine();
            process(ResourceFactory.createResource(BDR+"NM0895CB6787E8AC6E"));
            initEngine();
            process(ResourceFactory.createResource(BDR+"NM2463D933BA1F9A38"));
            initEngine();
            process(ResourceFactory.createResource(BDR+"NMEA2B380AF0BBFB1B"));        
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }    
    }
}
