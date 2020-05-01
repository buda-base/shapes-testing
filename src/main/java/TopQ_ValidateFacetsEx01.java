
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
import java.util.List;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationEngine;
import org.topbraid.shacl.validation.ValidationEngineConfiguration;
import org.topbraid.shacl.validation.ValidationUtil;

/**
 * 
 * Example of validating all occurrence of a facet of some subject. 
 *
 */
public class TopQ_ValidateFacetsEx01 {

    public static Logger logger = LoggerFactory.getLogger(TopQ_ValidateFacetsEx01.class);

    static Model testMod;

    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDO = "http://purl.bdrc.io/ontology/core/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String BDS = "http://purl.bdrc.io/ontology/shapes/core/";
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

        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);
    }
    
    public static Resource validateFacets(Model dataModel, Model shapesModel, Resource shape, List<RDFNode> focusNodes) {
        
        ValidationEngineConfiguration configuration = new ValidationEngineConfiguration().setValidateShapes(true);
        
        ValidationEngine engine = ValidationUtil.createValidationEngine(dataModel, shapesModel, configuration);
        engine.setConfiguration(configuration);
        
        logger.info("ValidationEngine Shapes graph {}", engine.getShapesGraphURI());
        logger.info("ValidationEngine .getShapesModel().size() = {}", engine.getShapesModel().size());
        
        try {
            engine.applyEntailments();
            Resource report = engine.validateNodesAgainstShape(focusNodes, shape.asNode());
            return report;
        }
        catch(InterruptedException ex) {
            return null;
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        logger.info("dataModel.size() = {} ", dataModel.size());
        logger.info("shapesModel.size() = {} ", shapesModel.size());
        
        Resource focus = ResourceFactory.createResource(BDR + REZ_NM);
        
        Property prop = ResourceFactory.createProperty(BDO+"personName");
        NodeIterator facetItr = dataModel.listObjectsOfProperty(focus, prop);
        List<RDFNode> facets = facetItr.toList();
        
        Resource shape = ResourceFactory.createResource(BDS+"PersonNameShape");
        
        logger.info("validating facets: {} against shape: {}", facets, shape);
        Resource report = validateFacets(dataModel, shapesModel, shape, facets);

        logger.info("PRINTING report.getModel()");
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}
