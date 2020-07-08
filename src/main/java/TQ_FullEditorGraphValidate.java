import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationUtil;

public class TQ_FullEditorGraphValidate {

    public static Logger log = LoggerFactory.getLogger(TQ_FullEditorGraphValidate.class);

    static final String SHAPES = "http://purl.bdrc.io/graph/PersonShapes.ttl";
    static final String ONT = "http://purl.bdrc.io/graph/ontologySchema.ttl";
    static final String editorModel = "P707_EDITOR_GRAPH.ttl";
    static final String editorModelErr = "P707_EDITOR_missingName.ttl";

    static Model editor;
    static Model editorErr;

    static {
        Model ont = ModelFactory.createDefaultModel();
        ont.read(ONT, null, "TTL");
        editor = ModelFactory.createDefaultModel();
        editorErr = ModelFactory.createDefaultModel();
        InputStream in = TQ_FullEditorGraphValidate.class.getClassLoader().getResourceAsStream(editorModel);
        editor.read(in, null, "TTL");
        editor.add(ont);
        in = TQ_FullEditorGraphValidate.class.getClassLoader().getResourceAsStream(editorModelErr);
        editorErr.read(in, null, "TTL");
        editor.add(ont);
        try {
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Resource validateTQ(Model m_data) {
        Model shapes_mod = ModelFactory.createDefaultModel();
        shapes_mod.read(SHAPES, null, "TTL");
        // shapes_mod.write(System.out, "TURTLE");
        Resource r = ValidationUtil.validateModel(m_data, shapes_mod, true);
        return r;
    }

    public static void main(String... args) {
        System.out.println("Running validation test on graph editor with valid graph" + System.lineSeparator());
        Resource r = validateTQ(editor);
        r.getModel().write(System.out, "TURTLE");
        System.out.println("Running validation test on graph editor with missing name" + System.lineSeparator());
        r = validateTQ(editorErr);
        r.getModel().write(System.out, "TURTLE");
    }

}
