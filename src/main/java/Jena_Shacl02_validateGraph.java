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

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Load shapes and data, validate and print report */
public class Jena_Shacl02_validateGraph {
    
    public static Logger logger = LoggerFactory.getLogger(Jena_Shacl01_validateGraph.class);
    
    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String BDS = "http://purl.bdrc.io/ontology/shapes/core/";
    static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    static final String SH = "http://www.w3.org/ns/shacl#";
    static final String SHAPES = "PersonShapes_BASE_minimal8.ttl";
    static final String ONT_GRAPH = "http://ldspdi-dev.bdrc.io/graph/ontologySchema.ttl";
//    static final String SHAPES_REPO = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/";
//    static final String SHAPES = SHAPES_REPO+"person.shapes.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "";
    static final String DATA = REZ_NM+DATA_VER+".ttl";
//    static final String DATA = "http://purl.bdrc.io/graph/"+REZ_NM;
    
    static final Resource EXT_SHAPE = ResourceFactory.createResource(BDS+"ExternalShape");
    static final Resource IGN_SHAPE = ResourceFactory.createResource(BDS+"IgnoreShape");
    static final Resource SH_VREPORT = ResourceFactory.createResource(SH+"ValidationReport");
    static final Resource SH_VRESULT = ResourceFactory.createResource(SH+"ValidationResult");
    
    static final Property RDF_TYPE = ResourceFactory.createProperty(RDF+"type");
    static final Property NODE_SHAPE_TYPE = ResourceFactory.createProperty(BDS+"nodeShapeType");
    static final Property PROP_SHAPE_TYPE = ResourceFactory.createProperty(BDS+"propertyShapeType");
    static final Property SH_CONFORMS = ResourceFactory.createProperty(SH+"conforms");
    static final Property SH_PATH = ResourceFactory.createProperty(SH+"path");
    static final Property SH_RESULT = ResourceFactory.createProperty(SH+"result");
    static final Property SH_RESULT_PATH = ResourceFactory.createProperty(SH+"resultPath");

    /*
     * Find the set of property paths to be ignored
     */
    private static Set<Resource> getIgnorePaths(Graph sg) {
        Model sm = ModelFactory.createModelForGraph(sg);       
        
        Set<Resource> pathSet = new HashSet<Resource>();
        
        ResIterator resItr = sm.listResourcesWithProperty(PROP_SHAPE_TYPE, EXT_SHAPE);
        ExtendedIterator<Resource> allItr = resItr.andThen(sm.listResourcesWithProperty(PROP_SHAPE_TYPE, IGN_SHAPE));
        logger.info("were there any propertyShapeType triples {}", allItr.hasNext());
        while (allItr.hasNext()) {
            Resource res = (Resource) allItr.next();
            Statement stmt = sm.getProperty(res, SH_PATH);
            if (stmt != null) {
                pathSet.add((Resource) stmt.getObject());
            }
        }
        
        logger.info("Ignored paths: {}", pathSet);
        return pathSet;
    }

    /*
     * Update the validation report to contain only violations internal to the graph, if any
     */
    private static Model updateReport(Model report, Graph sg) {
        // find the paths to ignore
        Set<Resource> ignPaths = getIgnorePaths(sg);
        
        // get the top-level subject of the report (a blank node)
        ResIterator resItr = report.listResourcesWithProperty(RDF_TYPE, SH_VREPORT);
        Resource reportSubj = resItr.hasNext() ? resItr.next() : null;
        if (reportSubj == null) return report;
        
        // check each result in the report for whether it is ignored or external
        // leaving only the results that are internal or facet results
        StmtIterator stmtItr = report.listStatements((Resource) null, SH_RESULT_PATH, (RDFNode) null);
        while (stmtItr.hasNext()) {
            Statement stmt = stmtItr.removeNext();
            Resource path = (Resource) stmt.getObject();
            if (ignPaths.contains(path)) {
                Resource result = stmt.getSubject();
                StmtIterator resultItr = report.listStatements(result, (Property) null, (RDFNode) null);
                report.remove(reportSubj, SH_RESULT, result); // remove the sh:result triple
                report.remove(resultItr); // remove all the ValidationResuilt triples
            }
        }
        
        // update sh:conforms
        resItr = report.listResourcesWithProperty(RDF_TYPE, SH_VRESULT);
        Statement conformStmt = report.getProperty(reportSubj, SH_CONFORMS);
        if (conformStmt != null) {
            conformStmt.changeLiteralObject(!resItr.hasNext());
        } else {
            report.addLiteral(reportSubj, SH_CONFORMS, !resItr.hasNext());
        }

        return report;
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
        
        ShaclValidator sv = ShaclValidator.get();

        Resource rez = ResourceFactory.createResource(BDR + REZ_NM);
        logger.info("Validating Node {} with {}", rez.getLocalName(), SHAPES);
        ValidationReport report = sv.validate(shapes, dataGraph, rez.asNode());
        Model reportModel = updateReport(report.getModel(), shapesGraph);

        logger.info("PRINTING report.getModel().ttl");
        RDFDataMgr.write(System.out, reportModel, Lang.TTL);
    }
}
