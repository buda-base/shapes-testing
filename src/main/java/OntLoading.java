
import java.io.FileWriter;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD4;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntLoading {

    public static Logger logger = LoggerFactory.getLogger(OntLoading.class);
    
    private static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";
    
    private static RDFConnection fuConn;
    
    private static final String BDO = "http://purl.bdrc.io/ontology/core/";
    private static final String BDR = "http://purl.bdrc.io/resource/";
    private static final String ADM = "http://purl.bdrc.io/ontology/admin/";
    private static final String BDA = "http://purl.bdrc.io/admindata/";
    private static final String BDG = "http://purl.bdrc.io/graph/";
    
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
        
        fuConn = RDFConnectionFuseki.create().destination("http://buda1.bdrc.io:13180/fuseki/newcorerw/").build();
        
//        System.out.println("\n");
//        Model g844m = ModelFactory.createDefaultModel() ;
//        g844m.read("/Users/chris/git/xmltoldmigration/src/test/ttl/G844.ttl");
//
//        try {
//            g844m.write(new FileWriter(OUT+"G844.ttl"), "TTL");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        
////        Graph g844g = g844m.getGraph();
////        NamedGraph g844ng = new NamedGraphWrapper(NodeFactory.createURI("http://purl.bdrc.io/graph/G844"), g844g);
//        try {
//            Context ctx = new Context();
//            Path path = Paths.get(OUT+"G844.trig");
//            OutputStream out = Files.newOutputStream(path);
////            RDFWriter.create().source(g844ng).context(ctx).lang(Lang.TRIG).build().output(out);            
//            Node graphUri = NodeFactory.createURI("http://purl.bdrc.io/graph/G844");
//            DatasetGraph dsg = DatasetFactory.create().asDatasetGraph();
//            dsg.addGraph(graphUri, g844m.getGraph());
//            new TriGWriter().write(out, dsg, getPrefixMap(), "http://purl.bdrc.io/graph/G844", ctx);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        
//        System.out.println("\n");
//        
//        Model opDoc = ModelFactory.createDefaultModel() ;
//        opDoc.read("/Users/chris/git/editor-templates/ont-policy.rdf") ;
//
//        try {
//            opDoc.write(new FileWriter(OUT+"OPM.ttl"), "TTL");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        ResIterator it = opDoc.listSubjectsWithProperty(opDoc.createProperty(ADM+"ontGraph"));
//        while (it.hasNext()) {
//            Resource subj = it.next();
//            Statement pubURI = opDoc.getProperty(subj, opDoc.createProperty("http://jena.hpl.hp.com/schemas/2003/03/ont-manager#publicURI"));
//            RDFNode node = pubURI.getObject();
//            logger.info("{} has graph name: {}", subj, node);
//        }
        
        
        OntModelSpec oms = new OntModelSpec(OntModelSpec.OWL_MEM);        
//        OntDocumentManager odm = new OntDocumentManager("file:/Users/chris/git/editor-templates/ont-policy.rdf");
        OntDocumentManager odm = new OntDocumentManager("https://raw.githubusercontent.com/buda-base/editor-templates/master/ont-policy.rdf");
        
        oms.setDocumentManager(odm);
        

        //
        // PROCESS IMPORTS FALSE
        //
        odm.setProcessImports(false);
        
        OntModel omNI = odm.getOntology("http://purl.bdrc.io/shapes/core/PersonShapes/", oms);
        
        try {
            omNI.write(new FileWriter(OUT+"PS_OM_NI.ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        String graphName = BDG+"PersonShapes_NI3";

        fuConn.put(graphName, omNI);

        Model mNIFu = fuConn.fetch(graphName);

        try {
            mNIFu.write(new FileWriter(OUT+"PS_M_NI_FM_FUSEKI.ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        OntModel omNIFu = ModelFactory.createOntologyModel();
        mNIFu.add(mNIFu);


        try {
            omNIFu.write(new FileWriter(OUT+"PS_OM_NI_FM_FUSEKI.ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        

        //
        // PROCESS IMPORTS TRUE
        //
        oms = new OntModelSpec(OntModelSpec.OWL_MEM);
//        odm = new OntDocumentManager("file:/Users/chris/git/editor-templates/ont-policy.rdf");
        odm = new OntDocumentManager("https://raw.githubusercontent.com/buda-base/editor-templates/master/ont-policy.rdf");
        oms.setDocumentManager(odm);
        odm.setProcessImports(true);
        
        OntModel omWI = odm.getOntology("http://purl.bdrc.io/shapes/core/PersonShapes/", oms);
        
        try {
            omWI.write(new FileWriter(OUT+"PS_OM_WI.ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        graphName = BDG+"PersonShapes_WI3";

        fuConn.put(graphName, omWI);

        Model mWIFu = fuConn.fetch(graphName);

        try {
            mWIFu.write(new FileWriter(OUT+"PS_M_WI_FM_FUSEKI.ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        OntModel omWIFu = ModelFactory.createOntologyModel();
        omWIFu.add(mWIFu);


        try {
            omWIFu.write(new FileWriter(OUT+"PS_OM_WI_FM_FUSEKI.ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }








//        OntModelSpec ontSpecImporting = new OntModelSpec( OntModelSpec.OWL_MEM );
//        ontSpecImporting.setDocumentManager( mgrImporting );
//        
//        OntModel unk1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/core/Unknown/", ontSpecImporting);
//        logger.info("OntLoading unk1.size() = " + unk1.size());
//        
//        OntModel access1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/adm/types/Access/", ontSpecImporting);
//        logger.info("OntLoading access1.size() = " + access1.size());
//        
//        OntModel license1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/adm/types/License/", ontSpecImporting);
//        logger.info("OntLoading license1.size() = " + license1.size());
//        
//        OntModel outlineType1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/adm/types/OutlineType/", ontSpecImporting);
//        logger.info("OntLoading outlineType1.size() = " + outlineType1.size());
//        
//        OntModel status1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/adm/types/Status/", ontSpecImporting);
//        logger.info("OntLoading status1.size() = " + status1.size());
//        
//        OntModel terms1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/adm/types/TermsOfUse/", ontSpecImporting);
//        logger.info("OntLoading terms1.size() = " + terms1.size());
//
//        OntModel admin1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/admin/", ontSpecImporting);
//        logger.info("OntLoading admin1.size() = " + admin1.size());
//
//        OntModel bdo1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/core/", ontSpecImporting);
//        logger.info("OntLoading bdo1.size() = " + bdo1.size());
//
//        OntModel langScript1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/types/LangScript/", ontSpecImporting);
//        logger.info("OntLoading langScript1.size() = " + langScript1.size());
//
//        OntModel language1 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/types/Language/", ontSpecImporting);
//        logger.info("OntLoading language1.size() = " + language1.size());
//        
//        System.out.println("\n");
//        
//        
//        
//        OntDocumentManager mgrNotImporting = new OntDocumentManager("\"file:/Users/chris/git/owl-schema/ont-policy.rdf\"");
////        OntDocumentManager mgrNotImporting = new OntDocumentManager("https://raw.githubusercontent.com/buda-base/owl-schema/master/ont-policy.rdf");
//        mgrNotImporting.setProcessImports(false);
//        
//        OntModelSpec ontSpecNotImporting = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
//        ontSpecNotImporting.setDocumentManager( mgrNotImporting );
//        
//        OntModel unk2 = mgrNotImporting.getOntology("http://purl.bdrc.io/ontology/core/Unknown/", ontSpecNotImporting);
//        logger.info("OntLoading unk2.size() = " + unk2.size());
//        
//        OntModel access2 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/adm/types/Access/", ontSpecImporting);
//        logger.info("OntLoading access2.size() = " + access2.size());
//
//        OntModel admin2 = mgrNotImporting.getOntology("http://purl.bdrc.io/ontology/admin/", ontSpecNotImporting);
//        logger.info("OntLoading admin2.size() = " + admin2.size());
//
//        OntModel bdo2 = mgrNotImporting.getOntology("http://purl.bdrc.io/ontology/core/", ontSpecNotImporting);
//        logger.info("OntLoading bdo2.size() = " + bdo2.size());
//
//        OntModel langScript2 = mgrNotImporting.getOntology("http://purl.bdrc.io/ontology/types/LangScript/", ontSpecNotImporting);
//        logger.info("OntLoading langScript2.size() = " + langScript2.size());
//
//        OntModel language2 = mgrImporting.getOntology("http://purl.bdrc.io/ontology/types/Language/", ontSpecImporting);
//        logger.info("OntLoading language2.size() = " + language2.size());
//        
//        try {
//            admin1.writeAll(new FileWriter("/Users/chris/AAA_2.ttl"), "TTL");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        
//        Resource config = ModelFactory.createDefaultModel()
//                            .createResource()
//                            .addProperty(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
//        Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(config);
//        logger.info("Global model size :" + admin1.size());
//        InfModel infMod = ModelFactory.createInfModel(reasoner, admin1);
//        logger.info("Inferred Global model size :" + infMod.listStatements().toList().size());

        
        
//        try {
//            infMod.write(new FileWriter("/Users/chris/AAA_2_inf.ttl"), "TTL");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
}