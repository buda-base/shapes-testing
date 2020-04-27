
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.graph.Graph;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.engine.ShapesGraph;
import org.topbraid.shacl.validation.ValidationEngine;
import org.topbraid.shacl.validation.ValidationEngineFactory;
import org.topbraid.shacl.validation.ValidationReport;

public class TopQ_EditServ_ShaclTest {

    public static Logger logger = LoggerFactory.getLogger(TopQ_EditServ_ShaclTest.class);

    static Model testMod;

    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "PersonShapes_BASE.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "";
    static final String DATA = REZ_NM + DATA_VER + ".ttl";

    static Graph testGraph;
    static Model testModel;

    static Graph shapesGraph;
    static Model shapesModel;

    static {
        testGraph = RDFDataMgr.loadGraph(DATA);
        testModel = ModelFactory.createModelForGraph(testGraph);

        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        Resource rez = ResourceFactory.createResource(BDR + REZ_NM);

        logger.info("testModel.size() = {} ", testModel.size());

        logger.info("shapesModel.size() = {} ", shapesModel.size());
        ShapesGraph sg = new ShapesGraph(shapesModel);
        logger.info("sg.getRootShapes(): {}", sg.getRootShapes());

        Dataset ds = DatasetFactory.create(testModel);
        Resource shapesGraphNm = ResourceFactory.createResource(BDG + "PersonShapes");
        ds.asDatasetGraph().addGraph(shapesGraphNm.asNode(), shapesGraph);

        URI shapesGraphUri = new URI(BDG + "PersonShapes");
        logger.info("Creating ValidationEngine for {}", shapesGraphUri);
        ValidationEngine ve = ValidationEngineFactory.get().create(ds, shapesGraphUri, sg, null);
        logger.info("Should the ValidationEngine validateShapes: {}", ve.getConfiguration().getValidateShapes());
        logger.info("Setting ValidationEngine report to include sh:details");
        ve.getConfiguration().setReportDetails(true);

        logger.info("ValidationEngine Shapes graph {}", ve.getShapesGraphURI());
        logger.info("ValidationEngine .getShapesModel().size() = {}", ve.getShapesModel().size());

//        logger.info("Validating ALL in {}", DATA);
//        ve.validateAll();

        logger.info("Validating Node {} in {}", rez.getLocalName(), DATA);
        ve.validateNode(rez.asNode());

        ValidationReport report = ve.getValidationReport();
        logger.info("report.conforms {}", report.conforms());
        logger.info("report.results(): {}", report.results());
    }
}
