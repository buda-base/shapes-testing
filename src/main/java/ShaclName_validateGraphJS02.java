import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShaclName_validateGraphJS02 {
    
    public static Logger logger = LoggerFactory.getLogger(ShaclName_validateGraphJS02.class);
    
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "ShapeName_02.ttl";
    static final String DATA = "DATA_P707.ttl";
    
    static ShaclValidator sv;
    static Graph dataGraph;
    static Graph shapesGraph;
    static Shapes shapes;
    
    static void initSV() {
        sv = ShaclValidator.get();
    }

    static {
        dataGraph = RDFDataMgr.loadGraph(DATA);
        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        shapes = Shapes.parse(shapesGraph);        
        shapes.forEach(shape -> { shape.print(System.out); });
        
        initSV();
    }
    
    static void process() {
        logger.info("Validating Node with {}", SHAPES);
        ValidationReport report = sv.validate(shapes, dataGraph);
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
    
    public static void main(String ...args) {
        process();        
    }
}
