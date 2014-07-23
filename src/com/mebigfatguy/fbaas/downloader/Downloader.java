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
package com.mebigfatguy.fbaas.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Downloader implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Downloader.class);
	private static final int DEFAULT_BUFFER_SIZE = 8192;

	private URL srcURL;
	private Path dstPath;
	
	public Downloader(URL src, Path dst) {
		
		srcURL = src;
		dstPath = dst;
	}
	
	public void run() {
		try (BufferedInputStream bis = new BufferedInputStream(srcURL.openStream());
			 BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(dstPath))) {
			
			Deque<TransferBuffer> dq = new ArrayDeque<TransferBuffer>();
			Thread r = new Thread(new BufferReader(bis, dq, DEFAULT_BUFFER_SIZE));
			r.start();
			
			Thread w = new Thread(new BufferWriter(bos, dq));
			w.start();
			
			r.join();
			w.join();
			
		} catch (IOException | InterruptedException e) {
			LOGGER.error("Failed downloading {} to {}", srcURL, dstPath, e);
		}
	}
}
