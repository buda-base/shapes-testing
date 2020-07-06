import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationUtil;

public class DualFullValidateTest01 {

    public static Logger logger = LoggerFactory.getLogger(TopQ_ValidationTest_MA01.class);

    static Model testMod;

    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "http://purl.bdrc.io/graph/PersonShapes.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "_augmented1";
    static final String DATA = REZ_NM + DATA_VER + ".ttl";
    static final String ONT_GRAPH = "http://purl.bdrc.io/graph/ontologySchema.ttl";

    static Graph testGraph;
    static Model testModel;

    static Graph shapesGraph;
    static Model shapesModel;

    static Shapes shapes;
    static Graph ontGraph;
    static Graph dataGraph;

    static {

        testGraph = RDFDataMgr.loadGraph(DATA);
        testModel = ModelFactory.createModelForGraph(testGraph);

        shapesGraph = RDFDataMgr.loadGraph("http://purl.bdrc.io/graph/PersonShapes.ttl");
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);

        ontGraph = RDFDataMgr.loadGraph(ONT_GRAPH);
        Model datasetModel = ModelFactory.createModelForGraph(ontGraph);
        datasetModel.add(ModelFactory.createModelForGraph(testGraph));
        dataGraph = datasetModel.getGraph();
    }

    static ValidationReport processJenaValidation() {
        try {
            ShaclValidator sv = ShaclValidator.get();
            logger.info("Validating {} with {}", SHAPES);
            shapes = Shapes.parse(shapesGraph);
            ValidationReport report = sv.validate(shapes, dataGraph);
            RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
            return report;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void main(String... args) {
        logger.info(">>>>>>>>>>>>>>>>> TQ report >>>>>>>>>>>>>>>>>>>>>");
        Resource report = ValidationUtil.validateModel(testModel, shapesModel, true);
        if (report != null) {
            report.getModel().write(System.out, "TURTLE");
        } else {
            logger.info("TQ report is null !");
        }
        logger.info(">>>>>>>>>>>>>>>>> Jena report >>>>>>>>>>>>>>>>>>>>>");
        ValidationReport reportJena = processJenaValidation();
        if (reportJena != null) {
            reportJena.getModel().write(System.out, "TURTLE");
        } else {
            logger.info("Jena validation is null !");
        }

    }

}
