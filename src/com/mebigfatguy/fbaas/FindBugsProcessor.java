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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebigfatguy.fbaas.fbp.GenerateFBP;

import edu.umd.cs.findbugs.FindBugs2;

public class FindBugsProcessor implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(FindBugsProcessor.class);
	private final BlockingQueue<FBJob> queue;
	
	public FindBugsProcessor(BlockingQueue<FBJob> q) {
		queue = q;
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				FBJob job = queue.take();
				
				if (!Status.isProcessing(job) && !Status.hasReport(job)) {
				    Status.setProcessing(job);
    				Path jarDirectory = null;
    				
    				try {
    					jarDirectory = loadJars(job);
    					
    					Path fbpFile = buildProjectFile(job, jarDirectory);
    					Path out = Paths.get(Status.getReportDir().toString(), job.toString() + ".xml");
    					
    					String[] args = { "-project", fbpFile.toString(), "-xml", "-output", out.toString()};
    					FindBugs2.main(args);
    					new File(out.toString()).deleteOnExit();
    
    				} catch (Exception e) {
    					LOGGER.error("Failed running findbugs on job {}", job, e);
    				} catch (Throwable t) {
    					LOGGER.error("Failed running findbugs on job {}", job, t);
    				} finally {
    					if (jarDirectory != null) {
    						FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
    
    							@Override
    							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    								Files.delete(file);
    								return FileVisitResult.CONTINUE;
    							}			
    						};
    						
    						Files.walkFileTree(jarDirectory, fv);
    						Files.delete(jarDirectory);
    						
    					}
    				}
				}
			}
		} catch (InterruptedException | IOException e) {
		}
	}

	private static Path loadJars(FBJob job) throws IOException {
		
		Path jarDir = Files.createTempDirectory("fb");
		
		PomHandler handler = new PomHandler(job, jarDir);
		handler.processPom();
		
		return jarDir;
	}
	
	
	private static Path buildProjectFile(FBJob job, Path jarDirectory) throws IOException, TransformerException, ParserConfigurationException {
		final Path fbpFile = Paths.get(jarDirectory.toString(), job.getArtifactId() + ".fbp");
		final Path jarPath = Paths.get(jarDirectory.toString(), job.getArtifactId() + "-" + job.getVersion() + ".jar");
		final Path srcPath = Paths.get(jarDirectory.toString(), job.getArtifactId() + "-" + job.getVersion() + "-sources.jar");
		
		final List<Path> auxList = new ArrayList<Path>();
		
		FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (!file.equals(jarPath) && (!file.equals(srcPath))) {
					auxList.add(file);
				}
				return FileVisitResult.CONTINUE;
			}			
		};
		
		Files.walkFileTree(jarDirectory, fv);
		
		GenerateFBP gen = new GenerateFBP(jarPath, srcPath, auxList);
		gen.generate(fbpFile);

		return fbpFile;
	}
}
