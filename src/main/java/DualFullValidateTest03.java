import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;

public class DualFullValidateTest03 {

    static final String DATA = "P707_CL03.ttl";

    static Graph testGraph;
    static Model testModel;

    static {
        testGraph = RDFDataMgr.loadGraph(DATA);
        testModel = ModelFactory.createModelForGraph(testGraph);
    }

    public static void main(String... args) {
        try {
            ShaclValidator sv = ShaclValidator.get();
            Shapes shapes = Shapes.parse(testGraph);
            ValidationReport report = sv.validate(shapes, testGraph);
            if (report != null) {
                report.getModel().write(System.out, "TURTLE");
            } else {
                System.out.println("Jena validation is null !");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
