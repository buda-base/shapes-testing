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

public class TopQ_EditServ_ShaclTest {
    
    public static Logger logger = LoggerFactory.getLogger(Jena_EditServ_SSV.class);

    static String personShapeUri = "https://raw.githubusercontent.com/buda-base/editor-templates/master/templates/core/person.shapes.ttl";
    static Model testMod;
    
    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "PersonShapes_BASE.ttl";
    static final String REZ_NM = "P707";
    static final String DATA = REZ_NM+".ttl";

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
        
        System.out.println("\n\nmodel size for shapes>> " + shapesModel.size());
        ShapesGraph sg = new ShapesGraph(shapesModel);
        System.out.println("Shapes graph root shapes >> " + sg.getRootShapes());
        System.out.println("Shapes graph root Model >> " + sg.getShapesModel().size());
        ValidationEngine ve = ValidationEngineFactory.get().create(DatasetFactory.create(testModel), new URI(BDG+"PersonShapes"), sg,
                null);
        System.out.println("Validation Engine config validateShapes >>" + ve.getConfiguration().getValidateShapes());
        System.out.println("Validation Engine config validateShapes >>" + ve.getConfiguration().setReportDetails(true));
        // ve.validateAll();
        ve.validateNode(rez.asNode());
        System.out.println("Shapes graph URI >> " + ve.getShapesGraphURI());
        System.out.println("Shapes model >> " + ve.getShapesModel().size());
        System.out.println(ve.getValidationReport().conforms());
        System.out.println(ve.getValidationReport().results());
    }
}
