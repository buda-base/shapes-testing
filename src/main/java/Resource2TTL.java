
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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resource2TTL {

    public static Logger logger = LoggerFactory.getLogger(Resource2TTL.class);

    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String BDS = "http://purl.bdrc.io/ontology/shapes/core/";
    static final String SHAPES = "PersonShapes_BASE.ttl";

    static Graph shapesGraph;
    static Model shapesModel;

    static {
        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        logger.info("shapesModel.size() = {} ", shapesModel.size());

        Resource shape = ResourceFactory.createResource(BDS+"PersonShape");
        StmtIterator itr = shapesModel.listStatements(shape, (Property) null, (RDFNode) null);
        
        Model shapeModel = ModelFactory.createDefaultModel().add(itr);
        shapeModel.setNsPrefixes(shapesModel.getNsPrefixMap());
        logger.info("PRINTING report.getModel()");
        RDFDataMgr.write(System.out, shapeModel, Lang.TTL);
    }
}
