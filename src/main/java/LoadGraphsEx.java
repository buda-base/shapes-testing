
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.writer.TriGWriter;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD4;
import org.apache.jena.vocabulary.XSD;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class LoadGraphsEx {

//    public static Logger logger = LoggerFactory.getLogger(LoadGraphsEx.class);
    
    private static final String BDO = "http://purl.bdrc.io/ontology/core/";
    private static final String BDR = "http://purl.bdrc.io/resource/";
    private static final String ADM = "http://purl.bdrc.io/ontology/admin/";
    private static final String BDA = "http://purl.bdrc.io/admindata/";
    private static final String BDG = "http://purl.bdrc.io/graph/";
    
    private static Context ctx = new Context();
    private static OutputStream out;
    private static RDFConnection fuConn;
    
    private static void init(String outPath) {
        System.err.println("Output path: "+outPath);
        Path path = Paths.get(outPath);
        try {
            out = Files.newOutputStream(path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        fuConn = RDFConnectionFuseki.create().destination("http://buda1.bdrc.io:13180/fuseki/newcorerw/").build();
    }
    
    public static PrefixMap getPrefixMap() {
        PrefixMap pm = PrefixMapFactory.create();
        pm.add("",      BDO);
        pm.add("adm",   ADM);
        pm.add("bda",   BDA);
        pm.add("bdg",   BDG);
        pm.add("bdr",   BDR);
        pm.add("owl",   OWL.getURI());
        pm.add("rdf",   RDF.getURI());
        pm.add("rdfs",  RDFS.getURI()); ;
        pm.add("skos",  SKOS.getURI());
        pm.add("vcard", VCARD4.getURI());
        pm.add("xsd",   XSD.getURI());
        return pm;
    }
    
    private static void fetchFusekiGraph(DatasetGraph dsg, String uriStr) {
        Node graphUri = NodeFactory.createURI(uriStr);
        dsg.addGraph(graphUri, fuConn.fetch(uriStr).getGraph());        
    }

    public static void main(String[] args){
        init(args[0]);
        
        Dataset ds = DatasetFactory.create();
        DatasetGraph dsg = ds.asDatasetGraph();

        System.err.println("Loading Graphs");        
        fetchFusekiGraph(dsg, BDG+"W1FPL1");
        fetchFusekiGraph(dsg, BDG+"MW1FPL1");
        fetchFusekiGraph(dsg, BDG+"P707");
        
        System.err.println("Writing " + args[0]);
        new TriGWriter().write(out, dsg, getPrefixMap(), null, ctx);
    }
}