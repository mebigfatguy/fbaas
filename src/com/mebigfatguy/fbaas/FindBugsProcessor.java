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

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindBugsProcessor implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(FindBugsProcessor.class);
	private ArrayBlockingQueue<FBJob> queue;
	
	public FindBugsProcessor(ArrayBlockingQueue<FBJob> q) {
		queue = q;
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				FBJob job = queue.take();
				
				try {
					ClassLoader l = new FindBugsClassLoader();
					Class<?> cls = l.loadClass("edu.umd.cs.findbugs.FindBugs2");
					Method m = cls.getMethod("main",  String[].class);
					m.invoke(null);
				} catch (Exception e) {
					LOGGER.error("Failed running findbugs on job {}", job);
					
				}
				
				
			}
		} catch (InterruptedException e) {
		}
	}
}
