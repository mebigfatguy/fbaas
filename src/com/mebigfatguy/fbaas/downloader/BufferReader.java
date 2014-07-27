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

import java.io.InputStream;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferReader implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(BufferReader.class);
	
	private final InputStream inputStream;
	private final Deque<TransferBuffer> deque;
	private final int bufferSize;
	private boolean success;
	
	public BufferReader(final InputStream is, Deque<TransferBuffer> dq, int bufSize) {
		inputStream = is;
		deque = dq;
		bufferSize = bufSize;
		success = false;
	}

	@Override
	public void run() {
        try {
            byte[] buffer = new byte[bufferSize];
            int size = inputStream.read(buffer);
            while (size >= 0) {
                if (size > 0) {
                    TransferBuffer queueBuffer = new TransferBuffer(buffer, size);
                    synchronized (deque) {
                    	deque.addLast(queueBuffer);
                    	deque.notifyAll();
                    }
                    buffer = new byte[bufferSize];
                    size = inputStream.read(buffer);
                }
            }
            success = true;
        } catch (Exception e) {
        	LOGGER.error("Failed populating queue from inputstream");
        } finally {
            TransferBuffer queueBuffer = new TransferBuffer(null, -1);
            synchronized (deque) {
            	deque.addLast(queueBuffer);
            	deque.notifyAll();
            }
        }	
    }
}
