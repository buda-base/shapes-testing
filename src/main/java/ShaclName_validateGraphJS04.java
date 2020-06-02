import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShaclName_validateGraphJS04 {
    
    public static Logger logger = LoggerFactory.getLogger(ShaclName_validateGraphJS04.class);
    
    static boolean printShapes = false;
    
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String ONT_GRAPH = "http://purl.bdrc.io/graph/ontologySchema.ttl";
    static final String SHAPES = "PersonLocalShapes_ALL04.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "_nameErrs_noRoot";
    static final String DATA = REZ_NM+DATA_VER+".ttl";
    
    static ShaclValidator sv;
    static Graph ontGraph;
    static Graph dataGraph;
    static Graph shapesGraph;
    static Shapes shapes;
    
    static void initSV() {
        sv = ShaclValidator.get();
    }

    static {
        ontGraph = RDFDataMgr.loadGraph(ONT_GRAPH);
        Graph targetGraph = RDFDataMgr.loadGraph(DATA);
        Model datasetModel = ModelFactory.createModelForGraph(ontGraph);
        datasetModel.add(ModelFactory.createModelForGraph(targetGraph));
        dataGraph = datasetModel.getGraph();

        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        shapes = Shapes.parse(shapesGraph);
        if (printShapes) {
            shapes.forEach(shape -> { shape.print(System.out); });
        }

        initSV();
    }

    static void process(String rid) {
        Resource rez = ResourceFactory.createResource(BDR+rid);
        logger.info("Validating {} with {}", rid, SHAPES);
        ValidationReport report = sv.validate(shapes, dataGraph, rez.asNode());
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
    
    public static void main(String ...args) {
        process(REZ_NM);        
    }
}
