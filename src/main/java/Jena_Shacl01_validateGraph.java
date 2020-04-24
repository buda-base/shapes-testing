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

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;

/** Load shapes and data, validate and print report */
public class Jena_Shacl01_validateGraph {
    static { LogCtl.setLog4j(); }

    public static void main(String ...args) {
        String SHAPES = "PersonShapes_BASE.ttl";
        String DATA = "P707.ttl";

        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        Graph dataGraph = RDFDataMgr.loadGraph(DATA);

        System.out.println("\n\nPARSING PersonUIShapes_ALL.ttl\n");
        Shapes shapes = Shapes.parse(shapesGraph);

        System.out.println("\n\nVALIDATING P707.ttl\n");
        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
        
        System.out.println("\n\nPRINTING VALIDATION REPORT.ttl\n");
        ShLib.printReport(report);

        System.out.println("\n\nPRINTING report.getModel().ttl\n");
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
        
        System.out.println("\n\n");
    }
}
