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

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationUtil;

public class TopQ_ValidationTest_MA02 {

    public static Logger logger = LoggerFactory.getLogger(TopQ_ValidationTest_MA02.class);

    static Model testMod;
    
    static final String OUT = "/Users/chris/BUDA/TEST_OUTPUT/";

    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "PersonShapes_BASE.ttl";
    static final String REZ_NM = "P707";
    static final String DATA_VER = "_augmented1";
    static final String DATA = REZ_NM + DATA_VER + ".ttl";

    static Graph testGraph;
    static Model testModel;

    static Graph shapesGraph;
    static Model shapesModel;

    static {
        testGraph = RDFDataMgr.loadGraph(DATA);
        testModel = ModelFactory.createModelForGraph(testGraph);

        shapesGraph = RDFDataMgr.loadGraph("http://purl.bdrc.io/graph/PersonShapes.ttl");
        shapesModel = ModelFactory.createModelForGraph(shapesGraph);
    }
    
    private static void writeTtl(Model m, String nm) {
        try {
            m.write(new FileWriter(OUT+nm+".ttl"), "TTL");
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        logger.info("dataModel.size() = {} ", testModel.size());
        logger.info("shapesModel.size() = {} ", shapesModel.size());

        writeTtl(shapesModel, "PersonShapes-from_TopQ_ValidationTest_MA02");
        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Resource report = ValidationUtil.validateModel(testModel, shapesModel, true);

        logger.info("PRINTING report.getModel()");
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}
