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

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.shacl.validation.ShaclSimpleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jena_EditServ_SSV {
    
    public static Logger logger = LoggerFactory.getLogger(Jena_EditServ_SSV.class);
    
    static final String BDG = "http://purl.bdrc.io/graph/";
    static final String BDR = "http://purl.bdrc.io/resource/";
    static final String SHAPES = "PersonShapes_BASE.ttl";
    static final String REZ_NM = "P707";
    static final String DATA = REZ_NM+".ttl";

    static Graph testGraph;
    static Model testModel;
    
    static Graph shapesGraph;
    static Shapes shapes;
    
    static {
        testGraph = RDFDataMgr.loadGraph(DATA);
        testModel = ModelFactory.createModelForGraph(testGraph);
        
        shapesGraph = RDFDataMgr.loadGraph(SHAPES);
    }

    public static void main(String[] args) throws IOException {
        
        ShaclSimpleValidator ssv = new ShaclSimpleValidator();
        
        System.out.println("\n\nCalling Shapes.parseAll( "+SHAPES+" )");
        shapes = Shapes.parseAll(shapesGraph);
        System.out.println("Shapes.parseAll Completed");

        System.out.println("\n\nCalling ShaclSimpleValidator.conforms( "+SHAPES+", "+DATA+" )");
        boolean conforms = ssv.conforms(shapes, testGraph);
        System.out.println("ShaclSimpleValidator conforms "+conforms);
        
        
        Resource rez = ResourceFactory.createResource(BDR + REZ_NM);
        
        System.out.println("\n\nCalling ShaclSimpleValidator.conforms( "+SHAPES+", "+DATA+", "+rez.asNode()+" )");
        conforms = ssv.conforms(shapes, testGraph, rez.asNode());
        System.out.println("ShaclSimpleValidator Node conforms "+conforms);
        
        
        ValidationReport report = ssv.validate(shapes, testGraph, rez.asNode());
        
        System.out.println("\n\nPRINTING VALIDATION REPORT.ttl\n");
        ShLib.printReport(report);

        System.out.println("\n\nPRINTING report.getModel().ttl\n");
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}
