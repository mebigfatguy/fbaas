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
package com.mebigfatguy.fbaas.rest;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebigfatguy.fbaas.Artifact;
import com.mebigfatguy.fbaas.FindBugsProcessor;
import com.mebigfatguy.fbaas.FindBugsResultsProcessor;
import com.mebigfatguy.fbaas.FindBugsSecurityManager;

public class WebAppContextListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebAppContextListener.class);
	
	private Thread processor;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
		    ServletContext context = event.getServletContext();
		    installPlugins(context);
		    
			System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
			System.setSecurityManager(new FindBugsSecurityManager());
			
			FindBugsResultsProcessor resultsProcessor = new FindBugsResultsProcessor();
			context.setAttribute("results", resultsProcessor);
			
			BlockingQueue<Artifact> queue = new ArrayBlockingQueue<Artifact>(10000);
			context.setAttribute("queue", queue);
			
			processor = new Thread(new FindBugsProcessor(queue));
			processor.start();
		} catch (Exception e) {
			LOGGER.error("Failed to initialize fbaas service", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			processor.interrupt();
			processor.join();
		} catch (InterruptedException e) {
			LOGGER.error("Failed to destroy fbaas service", e);
		}
	}
	
	private File installPlugins(ServletContext context) {
	    File path = new File(context.getRealPath("/findbugshome"));
	    
	    path.mkdirs();
	    File pluginDir = new File(path, "plugin");
	    pluginDir.mkdirs();
	    
	    context.setAttribute("findbugs.home",  path);
	    return path;
	}
}
