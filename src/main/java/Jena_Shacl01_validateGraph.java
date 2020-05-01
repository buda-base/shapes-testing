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
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Load shapes and data, validate and print report */
public class Jena_Shacl01_validateGraph {
    
    public static Logger logger = LoggerFactory.getLogger(Jena_Shacl01_validateGraph.class);
    
    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "PersonShapes_BASE.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "_augmented2";
    static final String DATA = REZ_NM+DATA_VER+".ttl";

    public static void main(String ...args) {

        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        Graph dataGraph = RDFDataMgr.loadGraph(DATA);

        logger.info("PARSING {}", SHAPES);
        Shapes shapes = Shapes.parse(shapesGraph);
        
        ShaclValidator sv = ShaclValidator.get();

//        logger.info("VALIDATING ALL in {}", DATA);
//        ValidationReport report = sv.validate(shapes, dataGraph);
        
        Resource rez = ResourceFactory.createResource(BDR + "NMC2A097019ABA499F");
//        Resource rez = ResourceFactory.createResource(BDR + REZ_NM);
        logger.info("Validating Node {} in {}", rez.getLocalName(), DATA);
        ValidationReport report = sv.validate(shapes, dataGraph, rez.asNode());

        logger.info("PRINTING report.getModel().ttl");
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}
