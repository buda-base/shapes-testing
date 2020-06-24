import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntTestLoading5_ONTS_RES {

    public static Logger logger = LoggerFactory.getLogger(OntTestLoading5_ONTS_RES.class);

    private static final String OUT = "/tmp/";

    private static final String ONT_POLICY = "owl-schema/ont-policy.rdf";

    private static OntModelSpec oms;
    private static OntDocumentManager odm;
    private static Model policies;
    static HashMap<String, OntPolicy> map;

    private static void initOdm() throws IOException {
        map = new HashMap<>();
        policies = ModelFactory.createDefaultModel();
        InputStream in = OntTestLoading5_ONTS_RES.class.getClassLoader().getResourceAsStream(ONT_POLICY);
        policies.read(in, RDFLanguages.strLangRDFXML);
        ResIterator it1 = policies.listResourcesWithProperty(RDF.type,
                ResourceFactory.createResource("http://jena.hpl.hp.com/schemas/2003/03/ont-manager#OntologySpec"));
        while (it1.hasNext()) {
            Resource r = it1.next();
            OntPolicy op = loadPolicy(r, FileManager.get().clone(), "core");
            map.put(op.getBaseUri() + "/", op);
            logger.info("loaded OntPolicy for uri {} >> {} ", op.getBaseUri(), op);
        }
        in.close();
    }

    private static void writeTtl(Model m, String nm) {
        try {
            m.write(new FileWriter(OUT + nm + ".ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void doAll(String name) throws IOException {
        logger.info("processing ALL from {}", name);
        Model allModel = null;
        InputStream in = null;
        for (String uri : map.keySet()) {
            // String uri = allItr.next();
            logger.info("URI {}", uri);
            if (map.keySet().contains(uri)) {
                Model m = ModelFactory.createDefaultModel();
                in = OntTestLoading5_ONTS_RES.class.getClassLoader().getResourceAsStream(getJarLoc(uri));
                System.out.println("jarLOC >>" + getJarLoc(uri) + " in:" + in);
                m.read(in, "", "TTL");
                logger.info("got model of size {} for uri {}", (m != null ? m.size() : 0), uri);
                if (m != null) {
                    if (allModel == null)
                        allModel = m;
                    else
                        allModel.add(m);
                }
            }
        }
        in.close();
        System.out.println("\n\ndoAll(" + name + ").getModelSize() == " + allModel.size());
        OntModel ontm = ModelFactory.createOntologyModel();
        ontm.addSubModel(allModel);
        System.out.println("\n\ndoAll(" + name + ").getModelSize() == " + ontm.size());
        writeTtl(allModel, name);
    }

    private static OntPolicy loadPolicy(Resource r, FileManager fm, String type) {
        Statement st = r.getProperty(ResourceFactory.createProperty("http://jena.hpl.hp.com/schemas/2003/03/ont-manager#publicURI"));
        String baseUri = st.getObject().asResource().getURI();
        String graph = null;
        st = r.getProperty(ResourceFactory.createProperty("http://purl.bdrc.io/ontology/admin/ontGraph"));
        if (st != null) {
            graph = st.getObject().asResource().getURI();
        }
        boolean visible = false;
        st = r.getProperty(ResourceFactory.createProperty("http://purl.bdrc.io/ontology/admin/ontVisible"));
        if (st != null) {
            visible = st.getObject().asLiteral().getBoolean();
        }
        st = r.getProperty(ResourceFactory.createProperty("http://jena.hpl.hp.com/schemas/2003/03/ont-manager#altURL"));
        String file = "";
        if (st != null) {
            file = st.getObject().toString();
        }
        OntPolicy op = new OntPolicy(baseUri, graph, fm.mapURI(baseUri), file, visible);
        return op;
    }

    public static String getJarLoc(String uri) {
        OntPolicy op = map.get(uri);
        logger.info("Getting jarLoc for {} and policy {}", uri, op);
        String tmp = op.getFile();
        tmp = "owl-schema/" + tmp.substring(tmp.lastIndexOf("RDF/") + 4);
        return tmp;
    }

    public static void main(String[] args) throws IOException {
        initOdm();
        doAll("ALL_ONTS_RES_05");
    }
}
