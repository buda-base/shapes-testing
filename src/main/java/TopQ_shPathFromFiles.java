import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.engine.Shape;
import org.topbraid.shacl.engine.ShapesGraph;
import org.topbraid.shacl.model.SHPropertyShape;
import org.topbraid.shacl.model.SHShape;

public class TopQ_shPathFromFiles {

    static String personShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/person.shapes.ttl";
    static String personEventShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/person.event.shapes.ttl";
    static String coreShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/core.shapes.ttl";
    static String corporationShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/corporation.shapes.ttl";
    static String identifierShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/identifier.shapes.ttl";
    static String instancesShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/instance.shapes.ttl";
    static String itemShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/item.shapes.ttl";
    static String personUIShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/person.ui.shapes.ttl";
    static String workShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/work.shapes.ttl";
    static ArrayList<String> uris;

    static {
        uris = new ArrayList<>();
        uris.add(personShapeUri);
        uris.add(personEventShapeUri);
        uris.add(coreShapeUri);
        uris.add(corporationShapeUri);
        uris.add(identifierShapeUri);
        uris.add(instancesShapeUri);
        uris.add(itemShapeUri);
        // uris.add(personUIShapeUri);
        uris.add(workShapeUri);
    }

    public static boolean checkAreAllPathsOK() {
        boolean test = true;
        StringBuffer buff = new StringBuffer();
        for (String uri : uris) {
            Model shapes = ModelFactory.createDefaultModel();
            shapes.read(uri, null, "TTL");
            ShapesGraph sg = new ShapesGraph(shapes);
            for (Shape shape : sg.getRootShapes()) {
                SHShape shs = shape.getShapeResource();
                System.out.println("Shape : " + shape.getShapeResource());
                for (SHPropertyShape shp : shs.getPropertyShapes()) {
                    Resource path = shp.getPath();
                    if (path == null) {
                        System.out.println(">>>>>  Path is null for propertyShape: " + shp.getURI() + " in graph : " + uri + System.lineSeparator());
                        buff.append("Path is null for propertyShape: " + shp.getURI() + " in graph : " + uri + System.lineSeparator());
                        test = false;
                    } else {
                        System.out.println("Path is " + path + " for  " + shp.getURI() + " in graph : " + uri + System.lineSeparator());
                    }
                }
            }
        }
        System.out.println("checkAreAllPathsOK() returns " + test);
        System.out.println(buff.toString());
        return test;
    }

    public static void main(String[] args) {
        TopQ_shPathFromFiles.checkAreAllPathsOK();
    }

}
