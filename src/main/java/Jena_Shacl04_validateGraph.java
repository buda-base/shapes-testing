/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Load shapes and data, validate and print report */
public class Jena_Shacl04_validateGraph {
    
    public static Logger logger = LoggerFactory.getLogger(Jena_Shacl01_validateGraph.class);
    
    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String BDS = "http://purl.bdrc.io/ontology/shapes/core/";
    static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    static final String SH = "http://www.w3.org/ns/shacl#";
    
    static final Property SH_CONFORMS = ResourceFactory.createProperty(SH+"conforms");
    static final Property SH_RESULT = ResourceFactory.createProperty(SH+"result");
    static final Property SH_VALUE = ResourceFactory.createProperty(SH+"value");
    
    static final Literal FALSE = ModelFactory.createDefaultModel().createTypedLiteral(false);
    static final Literal TRUE = ModelFactory.createDefaultModel().createTypedLiteral(true);

//    static final String SHAPES = "PersonLocalShapes_BASE.ttl";
//    static final String SHAPES = "PersonLocalShapes_ALL.ttl";
//    static final String SHAPES = "PersonShapes_BASE_minimal9.ttl";
//    static final String SHAPES = "PersonShapes_BASE_minimal10.ttl";
    static final String SHAPES = "PersonShapes_BASE_minimal11.ttl";
//    static final String SHAPES_REPO = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/";
//    static final String SHAPES = SHAPES_REPO+"person.shapes.ttl";
    
    static final String ONT_GRAPH = "http://purl.bdrc.io/graph/ontologySchema.ttl";

    static final String REZ_NM = "P707";
    static final String DATA_VER = "_nameErrs";
    static final String DATA = REZ_NM+DATA_VER+".ttl";
    
    static ShaclValidator sv = ShaclValidator.get();
    
    static Model process(Shapes shapes, Graph dataGraph, Resource rez) {

        logger.info("Validating Node {} with {}", rez.getLocalName(), SHAPES);
        ValidationReport report = sv.validate(shapes, dataGraph, rez.asNode());
        Model reportModel = report.getModel();
        
        return reportModel;
    }

    static Model completeReport(Shapes shapes, Graph dataGraph, Model top) {
        Model complete = ModelFactory.createDefaultModel();
        complete.add(top);

        if (top.contains((Resource) null, SH_CONFORMS, FALSE)) {
            StmtIterator valItr = top.listStatements((Resource) null, SH_VALUE, (RDFNode) null);
            while (valItr.hasNext()) {
                Statement valStmt = valItr.removeNext();
                RDFNode valNode = valStmt.getObject();

                if (valNode.isResource()) {
                    logger.info("completeReport, valStmt: {},  val: {}", valStmt, valNode);
                    Model subReport = process(shapes, dataGraph, (Resource) valNode);
                    //                subReport.remove(subReport.listStatements(null, SH_CONFORMS, (RDFNode) null));
                    complete.add(subReport);
                }
            }
        }

        return complete;
    }
    
    public static void main(String ...args) {

        Graph ontGraph = RDFDataMgr.loadGraph(ONT_GRAPH);
        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        Graph targetGraph = RDFDataMgr.loadGraph(DATA);
        
        Model datasetModel = ModelFactory.createModelForGraph(ontGraph);
        datasetModel.add(ModelFactory.createModelForGraph(targetGraph));
        Graph dataGraph = datasetModel.getGraph();

        logger.info("PARSING {}", SHAPES);
        Shapes shapes = Shapes.parse(shapesGraph);
        
        Resource rez = ResourceFactory.createResource(BDR+REZ_NM);
        Model topReport = process(shapes, dataGraph, rez);
//        logger.info("PRINTING topReport");
//        RDFDataMgr.write(System.out, topReport, Lang.TTL);

        Model finalReport = completeReport(shapes, dataGraph, topReport);
        logger.info("PRINTING finalReport");
        RDFDataMgr.write(System.out, finalReport, Lang.TTL);
    }
}
