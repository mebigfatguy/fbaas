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

import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebigfatguy.fbaas.FBJob;
import com.mebigfatguy.fbaas.FindBugsProcessor;

public class WebAppContextListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebAppContextListener.class);
	
	private ArrayBlockingQueue<FBJob> queue;
	private Thread processor;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			queue = new ArrayBlockingQueue<FBJob>(10000);
			event.getServletContext().setAttribute("queue", queue);
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
		}
	}
}
