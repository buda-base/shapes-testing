import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;

public class OntTestLoading6_LOCAL {

    static Model TEST_MOD = null;

    static String ONT_POLICIES = "owl-schema/ont-policy.rdf";

    public OntTestLoading6_LOCAL() {
        TEST_MOD = ModelFactory.createDefaultModel();
        InputStream in = OntTestLoading6_LOCAL.class.getClassLoader().getResourceAsStream(ONT_POLICIES);
        TEST_MOD.read(in, RDFLanguages.strLangRDFXML);
    }

    public void readModel() {
        ResIterator it1 = TEST_MOD.listResourcesWithProperty(RDF.type,
                ResourceFactory.createResource("http://jena.hpl.hp.com/schemas/2003/03/ont-manager#OntologySpec"));
        while (it1.hasNext()) {
            Resource r = it1.next();
            Statement st = r.getProperty(ResourceFactory.createProperty("http://jena.hpl.hp.com/schemas/2003/03/ont-manager#altURL"));
            // Displaying altUrl where /owl-schema/ is replaced by /RDF/
            System.out.println("altUrl >>" + st.getObject().toString());
        }
    }

    public static void main(String... arg) {
        OntTestLoading6_LOCAL test = new OntTestLoading6_LOCAL();
        test.readModel();
    }

}
