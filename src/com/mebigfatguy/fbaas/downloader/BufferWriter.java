package com.mebigfatguy.fbaas.downloader;

import java.io.OutputStream;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BufferWriter implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(BufferWriter.class);
	
	private OutputStream outputStream;
	private Deque<TransferBuffer> deque;
	private boolean success;

	public BufferWriter(final OutputStream os, Deque<TransferBuffer> dq) {
		deque = dq;
		outputStream = os;
		success = false;
	}

	@Override
	public void run() {
        try {
            TransferBuffer buffer = null;
            int size = 0;

            while (size >= 0) {
                synchronized (deque) {
                    while (deque.size() == 0) {
                    	deque.wait();
                    }
                    buffer = deque.removeFirst();
                }

                size = buffer.getSize();
                if (size > 0) {
                    outputStream.write(buffer.getBuffer(), 0, size);
                }
            }
            success = true;
        } catch (Exception e) {
        	LOGGER.error("Failed writing stream into queue");
        }
	}
}
