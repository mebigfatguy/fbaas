/** fbaas - FindBugs as a Service.
 * Copyright 2014-2017 MeBigFatGuy.com
 * Copyright 2014-2017 Dave Brosius
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;
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
            installPlugin();

            System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
            System.setSecurityManager(new FindBugsSecurityManager());

            FindBugsResultsProcessor resultsProcessor = new FindBugsResultsProcessor();
            context.setAttribute("results", resultsProcessor);

            BlockingQueue<Artifact> queue = new ArrayBlockingQueue<>(10000);
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

    private void installPlugin() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(WebAppContextListener.class.getResourceAsStream("/fb.version")))) {
            String fbJarName = br.readLine();

            Path userHome = Paths.get(System.getProperty("user.home"));

            Path findbugsDir = userHome.resolve(".findbugs");
            Files.createDirectories(findbugsDir);
            Path pluginDir = findbugsDir.resolve("plugin");
            Files.createDirectories(pluginDir);

            Path jarPath = pluginDir.resolve(fbJarName);
            if (Files.isWritable(pluginDir)) {
                try (InputStream is = WebAppContextListener.class.getResourceAsStream("/" + fbJarName)) {

                    try (OutputStream os = Files.newOutputStream(jarPath)) {
                        IOUtils.copy(is, os);
                    }
                }
                jarPath.toFile().deleteOnExit();
            }
        }
    }
}
