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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$\\{([^\\}]*)\\}");
	
	private static final String MAVEN_CENTRAL_POM_URL = "http://repo1.maven.org/maven2/%s/%s/%s/%s-%s.pom";
	private static final String MAVEN_CENTRAL_JAR_URL = "http://repo1.maven.org/maven2/%s/%s/%s/%s-%s.jar";
	
	private final Artifact jarArtifact;
	private final Path jarDirectory;
	private final Map<String, String> properties;
	
	public PomHandler(Artifact artifact, Path jarDir) {
		jarArtifact = artifact;
		jarDirectory = jarDir;
		properties = new HashMap<>();
	}
	
	public void processPom() throws IOException {
		parsePom(jarArtifact.getGroupId(), jarArtifact.getArtifactId(), jarArtifact.getVersion());
		downloadJar(jarArtifact);
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
	
	private void downloadJar(Artifact artifact) throws MalformedURLException, IOException {
		try {
			URL jarURL = new URL(String.format(MAVEN_CENTRAL_JAR_URL, artifact.getGroupId().replaceAll("\\.",  "/"), artifact.getArtifactId(), artifact.getVersion(), artifact.getArtifactId(), artifact.getVersion()));
			Path jarPath = Paths.get(jarDirectory.toString(), artifact.getArtifactId() + '-' + artifact.getVersion() + ".jar");
			if (!Files.exists(jarPath)) {
				Downloader dl = new Downloader(jarURL, jarPath);
				Thread th = new Thread(dl);
				th.start();
		
				th.join();
				dl.checkSuccess();
			}
		} catch (InterruptedException e) {
			LOGGER.info("Download of jar {} {} {} was interrupted", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
		}
	}
	
	class SAXHandler extends DefaultHandler {

		private final List<String> openTags;
		private final StringBuilder text;
		private String groupId;
		private String artifactId;
		private String version;
		private String exclusionGroupId;
		private String exclusionArtifactId;
		private String exclusionVersion;
		private Set<Artifact> exclusions = new HashSet<>();
		
		public SAXHandler() {
			openTags = new ArrayList<>();
			text = new StringBuilder();
		}
		
		@Override
		public void startElement(String urim, String localName, String qName, Attributes attributes) throws SAXException {
			openTags.add(localName);
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			try {
				String innerTag = openTags.remove(openTags.size() - 1);
				String outerTag = (openTags.size() < 1) ? "" : openTags.get(openTags.size() - 1);
				
				if (innerTag.equalsIgnoreCase("groupid")) {
					if (outerTag.equalsIgnoreCase("dependency")) {
						groupId = text.toString();
					} else {
						exclusionGroupId = text.toString();
					}
				} else if (innerTag.equalsIgnoreCase("artifactid")) {
					if (outerTag.equalsIgnoreCase("dependency")) {
						artifactId = text.toString();
					} else {
						exclusionArtifactId = text.toString();
					}
				} else if (innerTag.equalsIgnoreCase("version")) {
					if (outerTag.equalsIgnoreCase("dependency")) {
						version = text.toString();
					} else {
						exclusionVersion = text.toString();
					}
				} else if (innerTag.equalsIgnoreCase("exclusion")) {
					Artifact artifact = new Artifact(exclusionGroupId, exclusionArtifactId, exclusionVersion);
					exclusions.add(artifact);
				} else if (innerTag.equalsIgnoreCase("dependency")) {
					Artifact artifact = new Artifact(groupId, artifactId, version);
					downloadJar(substituteProperties(artifact));
					exclusions.clear();
				} else if (innerTag.equalsIgnoreCase("parent")) {
					Artifact parentArtifact = new Artifact(groupId, artifactId, version);
					PomHandler handler = new PomHandler(parentArtifact, jarDirectory);
					handler.processPom();
					properties.putAll(handler.properties);
				} else if (outerTag.equalsIgnoreCase("properties")) {
					properties.put(localName, text.toString());
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
	
	private Artifact substituteProperties(Artifact artifact) {
		return new Artifact(substituteProperties(artifact.getGroupId()), substituteProperties(artifact.getArtifactId()), substituteProperties(artifact.getVersion()));
	}
	
	private String substituteProperties(String value) {
		
		StringBuilder convertedValue = new StringBuilder();
		
		int start = 0;
		
		Matcher m = PROPERTY_PATTERN.matcher(value);
		if (!m.find()) {
			return value;
		}

		while (m.find(start)) {
			convertedValue.append(value.substring(start, m.start()));
			
			String name = m.group(1);
			String propVal = properties.get(name);
			if (propVal != null) {
				convertedValue.append(propVal);
			} else {
				convertedValue.append(m.group(0));
			}
			start = m.end();
		}
		
		if (start < value.length()) {
			convertedValue.append(value.substring(start, value.length()));
		}
		
		return convertedValue.toString();
	}
	
	
}
