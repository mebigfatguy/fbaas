package com.mebigfatguy.fbaas.downloader;

import java.io.InputStream;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferReader implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(BufferReader.class);
	
	private InputStream inputStream;
	private Deque<TransferBuffer> deque;
	private int bufferSize;
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
