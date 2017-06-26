/** fbaas - FindBugs as a Service.
 * Copyright 2014-2017 MeBigFatGuy.com
 * Copyright 2014-2017 Dave Brosius
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.w3c.dom.Document;

public class GenerateFBP {
    private static final String FBP_XSL_PATH = "/com/mebigfatguy/fbaas/fbp/fbp.xsl";

    private static final String FBP_NAME = "fbp_name";
    private static final String FBP_JAR = "fbp_jar";
    private static final String FBP_SRC = "fbp_src";
    private static final String FBP_AUX = "fbp_aux";

    private final Path jar;
    private final Path src;
    private final List<Path> auxList;

    public GenerateFBP(Path jarPath, Path srcPath, List<Path> auxJars) {
        jar = jarPath;
        src = srcPath;
        auxList = auxJars;
    }

    public void generate(Path fbpOutputPath) throws ParserConfigurationException, TransformerException, IOException {

        try (InputStream is = GenerateFBP.class.getResourceAsStream(FBP_XSL_PATH);
                BufferedWriter bw = Files.newBufferedWriter(fbpOutputPath, Charset.forName("UTF-8"))) {

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(new StreamSource(is));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setExpandEntityReferences(false);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();

            t.setParameter(FBP_NAME, jar.getFileName().toString());
            t.setParameter(FBP_JAR, jar.toString());
            t.setParameter(FBP_SRC, src.toString());
            t.setParameter(FBP_AUX, auxList);
            t.transform(new DOMSource(d), new StreamResult(bw));
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
