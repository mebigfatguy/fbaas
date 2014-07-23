/** fbaas - FindBugs as a Service. 
 * Copyright 2014 MeBigFatGuy.com 
 * Copyright 2014 Dave Brosius 
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
package com.mebigfatguy.fbaas;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class PomHandler {

	private static final String MAVEN_CENTRAL_ROOT_URL = "http://repo1.maven.org/maven2/%s/%s/%s/%s-%s.pom";
	
	private FBJob job;
	private Path jarDirectory;
	private List<String> processedJars;

	
	public PomHandler(FBJob fbJob, Path jarDir) {
		job = fbJob;
		jarDirectory = jarDir;
		processedJars = new ArrayList<>();
	}
	
	public void processPom() throws IOException {
		parsePom(job.getGroupId(), job.getArtifactId(), job.getVersion());
	}
	
	private void parsePom(String groupId, String artifactId, String version) throws IOException {
		URL u = new URL(String.format(MAVEN_CENTRAL_ROOT_URL, groupId.replaceAll("\\.",  "/"), artifactId, version, artifactId, version));

		try (BufferedInputStream bis = new BufferedInputStream(u.openStream())) {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			SAXHandler handler = new SAXHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(bis));
		} catch (SAXException e) {
			throw new IOException(String.format("Failed parsing pom: %s %s %s", groupId, artifactId, version), e);
		}
	}
	
	class SAXHandler extends DefaultHandler {

		private List<String> openTags;
		private StringBuilder text;
		private String groupId;
		private String artifactId;
		private String version;
		
		public SAXHandler() {
			openTags = new ArrayList<>();
			text = new StringBuilder();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			openTags.add(localName);
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			try {
				String innerTag = openTags.remove(openTags.size() - 1);
				
				if (innerTag.equalsIgnoreCase("groupid")) {
					groupId = text.toString();
				} else if (innerTag.equalsIgnoreCase("artifactid")) {
					artifactId = text.toString();
				} else if (innerTag.equalsIgnoreCase("version")) {
					version = text.toString();
				} else if (innerTag.equalsIgnoreCase("dependency")) {
					parsePom(groupId, artifactId, version);
				}
			} catch (IOException e) {
				throw new SAXException("Failed downloading inner pom", e);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			text.append(new String(ch, start, length));
		}
	}
}
