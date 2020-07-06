
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
import java.net.URISyntaxException;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationEngine;
import org.topbraid.shacl.validation.ValidationEngineConfiguration;
import org.topbraid.shacl.validation.ValidationUtil;

public class TopQ_ValidateNode_MA01 {

    public static Logger logger = LoggerFactory.getLogger(TopQ_ValidateNode.class);

    static Model testMod;

    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "PersonShapes_BASE.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "_augmented2";
    static final String DATA = REZ_NM + DATA_VER + ".ttl";

    static final boolean validateShapes = true;

    static Graph dataGraph;
    static Model dataModel;

    static Graph shapesGraph;
    static Model shapesModel;

    static {
        dataGraph = RDFDataMgr.loadGraph(DATA);
        dataModel = ModelFactory.createModelForGraph(dataGraph);

        shapesGraph = RDFDataMgr.loadGraph("http://purl.bdrc.io/graph/PersonShapes.ttl");
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);
    }

    public static Resource validateNode(Model dataModel, Model shapesModel, Resource focus, boolean validateShapes) {
        return validateNode(dataModel, shapesModel, focus, new ValidationEngineConfiguration().setValidateShapes(validateShapes));
    }

    public static Resource validateNode(Model dataModel, Model shapesModel, Resource focus, ValidationEngineConfiguration configuration) {

        ValidationEngine engine = ValidationUtil.createValidationEngine(dataModel, shapesModel, configuration);
        engine.setConfiguration(configuration);
        logger.info("ValidationEngine Shapes graph {}", engine.getShapesGraphURI());
        logger.info("ValidationEngine .getShapesModel().size() = {}", engine.getShapesModel().size());
        try {
            engine.applyEntailments();
            return engine.validateNode(focus.asNode());
        } catch (InterruptedException ex) {
            return null;
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        logger.info("dataModel.size() = {} ", dataModel.size());
        logger.info("shapesModel.size() = {} ", shapesModel.size());

//        logger.info("Validating ALL in {}", DATA);
//        Resource report = ValidationUtil.validateModel(dataModel, shapesModel, true);

//        Resource rez = ResourceFactory.createResource(BDR + "NMC2A097019ABA499F");
        Resource rez = ResourceFactory.createResource(BDR + REZ_NM);
        logger.info("Validating Node {} in {}", rez.getLocalName(), DATA);
        Resource report = validateNode(dataModel, shapesModel, rez, true);

        logger.info("PRINTING report.getModel()");
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}
