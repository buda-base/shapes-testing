import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;

public class ShaclParse_JS01 {

    // static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";
    static final String BDG = "http://purl.bdrc.io/graph/";

    private static void parseGraph(String graphName, String suffix) {
        Graph shapesGraph = RDFDataMgr.loadGraph(BDG + graphName + ".ttl");
        Model shapesModel = ModelFactory.createModelForGraph(shapesGraph);
        writeTtl(shapesModel, graphName + suffix);
        try {
            System.out.println("===> Parsing " + graphName + ", with " + shapesModel.size() + " stmts");
            Shapes shapes = Shapes.parse(shapesGraph);
            System.out.println("===> SUCCESS Parsing " + graphName + ", with " + shapes.getGraph().size() + " stmts");
        } catch (Exception ex) {
            System.out.println("===> FAILED to Parse " + graphName);
            ex.printStackTrace();
        }
    }

    private static void writeTtl(Model m, String nm) {
        try {
            // m.write(new FileWriter(OUT+nm+".ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String... args) {
        parseGraph("PersonLocalShapes", "-JS_from_buda1_001");
        parseGraph("PersonShapes", "-JS_from_buda1_001");
    }
}
