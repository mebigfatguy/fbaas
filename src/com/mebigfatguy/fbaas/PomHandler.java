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

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PomHandler {

	private FBJob job;
	private Path jarDirectory;
	private List<String> processedJars;
	
	public PomHandler(FBJob fbJob, Path jarDir) {
		job = fbJob;
		jarDirectory = jarDir;
		processedJars = new ArrayList<>();
	}
	
	public void processPom() {
		
		Path pomFile = downloadPom(job.getGroupId(), job.getArtifactId(), job.getVersion());
		parsePom(pomFile);
	}
	
	private Path downloadPom(String groupId, String artifactId, String version) {
		return null;
	}
	
	private void parsePom(Path pomFile) {
		
	}
	
	private void downloadURL(URL sourceURL, URL destURL) {
		
	}
}
