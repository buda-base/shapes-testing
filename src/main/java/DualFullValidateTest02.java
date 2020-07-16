import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationUtil;

public class DualFullValidateTest02 {

    public static Logger logger = LoggerFactory.getLogger(DualFullValidateTest02.class);

    static final String SHAPES = "http://purl.bdrc.io/graph/PersonShapes.ttl";
//    static final String SHAPES = "RLS_MIN03.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "_augmented5";
    static final String DATA = REZ_NM + DATA_VER + ".ttl";

    static Graph testGraph;
    static Model testModel;

    static Graph shapesGraph;
    static Model shapesModel;

    static {

        testGraph = RDFDataMgr.loadGraph(DATA);
        testModel = ModelFactory.createModelForGraph(testGraph);

        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);
    }

    static void processTQValidation() {
        Resource report = ValidationUtil.validateModel(testModel, shapesModel, true);
        if (report != null) {
            report.getModel().write(System.out, "TURTLE");
        } else {
            logger.info("TQ report is null !");
        }
        System.out.print("\n\n");
    }

    static void processJSValidation() {
        try {
            ShaclValidator sv = ShaclValidator.get();
            Shapes shapes = Shapes.parse(shapesGraph);
            ValidationReport report = sv.validate(shapes, testGraph);
            if (report != null) {
                report.getModel().write(System.out, "TURTLE");
            } else {
                logger.info("Jena validation is null !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        logger.info("Validating {} with {}", DATA, SHAPES);
        
        logger.info(">>>>>>>>>>>>>>>>> Validating with TQ >>>>>>>>>>>>>>>>>>>>>");
        processTQValidation();
        
        logger.info(">>>>>>>>>>>>>>>>> Validating with Jena >>>>>>>>>>>>>>>>>>>>>");
        processJSValidation();
    }
}
