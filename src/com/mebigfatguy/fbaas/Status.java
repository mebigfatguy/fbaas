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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

public class Status {

    private static final Logger LOGGER = LoggerFactory.getLogger(Status.class);
    
    private static final File FBAAS_DIR = new File(System.getProperty("user.home"), ".fbaas");
    private static final File PROCESSING_DIR = new File(FBAAS_DIR, "processing");
    private static final File REPORT_DIR = new File(FBAAS_DIR, "reports");
    private static final Duration MAX_PROCESSING_TIME = Duration.of(1, ChronoUnit.HOURS);
    
    private Status() {
    }
    
    public static boolean isProcessing(FBJob job) {
        File procFile = new File(PROCESSING_DIR, job.toString());
        if (!procFile.exists() || !procFile.isFile()) {
            return false;
        }
        
        Instant now = Instant.now();
        Instant fileTime = Instant.ofEpochMilli(procFile.lastModified());
        Duration d = Duration.between(fileTime, now);
        return (d.compareTo(MAX_PROCESSING_TIME) < 0);
    }
    
    public static void setProcessing(FBJob job) {
        try {
            File procFile = new File(PROCESSING_DIR, job.toString());
            procFile.delete();
            procFile.createNewFile();
            procFile.deleteOnExit();
        } catch (IOException e) {
            LOGGER.error("Failed to create processing file for {}", job, e);
        }
    }
    
    public static File getReportDir() {
        return REPORT_DIR;
    }
    
    public static boolean hasReport(FBJob job) {
        File reportFile = new File(REPORT_DIR, job.toString() + ".xml");
        return (reportFile.exists() && reportFile.isFile());
    }
}
