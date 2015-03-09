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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.mebigfatguy.fbaas.downloader.Downloader;

public class PomHandler {

	private static Logger LOGGER = LoggerFactory.getLogger(PomHandler.class);
	
	private static final String MAVEN_CENTRAL_POM_URL = "http://repo1.maven.org/maven2/%s/%s/%s/%s-%s.pom";
	private static final String MAVEN_CENTRAL_JAR_URL = "http://repo1.maven.org/maven2/%s/%s/%s/%s-%s.jar";
	
	private final Artifact jarArtifact;
	private final Path jarDirectory;
	
	public PomHandler(Artifact artifact, Path jarDir) {
		jarArtifact = artifact;
		jarDirectory = jarDir;
	}
	
	public void processPom() throws IOException {
		parsePom(jarArtifact.getGroupId(), jarArtifact.getArtifactId(), jarArtifact.getVersion());
		downloadJar(jarArtifact.getGroupId(), jarArtifact.getArtifactId(), jarArtifact.getVersion());
	}
	
	private void parsePom(String groupId, String artifactId, String version) throws IOException {
		URL pomUrl = new URL(String.format(MAVEN_CENTRAL_POM_URL, groupId.replaceAll("\\.",  "/"), artifactId, version, artifactId, version));
		try (BufferedInputStream bis = new BufferedInputStream(pomUrl.openStream())) {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			SAXHandler handler = new SAXHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(bis));
		} catch (SAXException e) {
			LOGGER.error("Failed downloading pom for {} {} {}", groupId, artifactId, version, e);
		}
	}
	
	private void downloadJar(String groupId, String artifactId, String version) throws MalformedURLException {
		try {
			URL jarURL = new URL(String.format(MAVEN_CENTRAL_JAR_URL, groupId.replaceAll("\\.",  "/"), artifactId, version, artifactId, version));
			Path jarPath = Paths.get(jarDirectory.toString(), artifactId + '-' + version + ".jar");
			if (!Files.exists(jarPath)) {
				Downloader dl = new Downloader(jarURL, jarPath);
				Thread th = new Thread(dl);
				th.start();
		
				th.join();
			}
		} catch (InterruptedException e) {
			LOGGER.info("Download of jar {} {} {} was interrupted", groupId, artifactId, version);
		}
	}
	
	class SAXHandler extends DefaultHandler {

		private final List<String> openTags;
		private final StringBuilder text;
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
					downloadJar(groupId, artifactId, version);
				} else if (innerTag.equalsIgnoreCase("parent")) {
					Artifact parentArtifact = new Artifact(groupId, artifactId, version);
					PomHandler handler = new PomHandler(parentArtifact, jarDirectory);
					handler.processPom();
				}
			} catch (IOException e) {
				throw new SAXException("Failed downloading inner pom: " + groupId + '/' + artifactId + '/' + version, e);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			text.append(new String(ch, start, length));
		}
	}
}
