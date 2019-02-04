/** fbaas - FindBugs as a Service.
 * Copyright 2014-2019 MeBigFatGuy.com
 * Copyright 2014-2019 Dave Brosius
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Deque;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferWriter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferWriter.class);

    private final OutputStream outputStream;
    private final Deque<TransferBuffer> deque;
    private IOException exception;

    public BufferWriter(final OutputStream os, Deque<TransferBuffer> dq) {
        deque = dq;
        outputStream = os;
        exception = null;
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
        } catch (InterruptedException e) {
            LOGGER.error("Failed writing stream into queue - interrupted");
            exception = new IOException("Failed writing stream into queue - interrupted", e);
        } catch (IOException e) {
            LOGGER.error("Failed writing stream into queue");
            exception = e;
        }
    }

    public void checkSuccess() throws IOException {
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
