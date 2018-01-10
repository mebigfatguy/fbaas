/** fbaas - FindBugs as a Service. 
 * Copyright 2014-2018 MeBigFatGuy.com 
 * Copyright 2014-2018 Dave Brosius 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations 
 * under the License. 
 */
package com.mebigfatguy.fbaas.fbp;

import java.nio.file.Path;
import java.util.List;

import org.apache.xalan.extensions.ExpressionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AuxBean {

    public NodeList getAuxPaths(final ExpressionContext ec, final List<Path> auxPaths) {
        return new NodeList() {

            @Override
            public Node item(int index) {
                Node contextNode = ec.getContextNode();
                Document doc = (contextNode instanceof Document) ? (Document) contextNode : contextNode.getOwnerDocument();
                return doc.createTextNode(auxPaths.get(index).toString());
            }

            @Override
            public int getLength() {
                return auxPaths.size();
            }

        };
    }
}
