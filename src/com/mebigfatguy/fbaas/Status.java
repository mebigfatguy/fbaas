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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

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
    
    static {
        FBAAS_DIR.mkdirs();
        PROCESSING_DIR.mkdirs();
        REPORT_DIR.mkdirs();
    }
    
    private Status() {
    }
    
    public static boolean isProcessing(FBJob job) {
        File procFile = new File(PROCESSING_DIR, job.fileName());
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
            File procFile = new File(PROCESSING_DIR, job.fileName());
            procFile.delete();
            procFile.createNewFile();
            procFile.deleteOnExit();
        } catch (IOException e) {
            LOGGER.error("Failed to create processing file for {}", job, e);
        }
    }
    
    public static String getProcessingFailed(Locale locale, FBJob job) {
        File procFile = new File(PROCESSING_DIR, job.fileName());
        if (procFile.length() == 0) {
            return null;
        }
        
        try (BufferedReader r = new BufferedReader(new FileReader(procFile))) {
            return Bundle.getString(locale, Bundle.Failure, r.readLine());
        } catch (IOException e) {
            LOGGER.error("Failed reading failure message in processing file for {}", job, e);
            return Bundle.getString(locale, Bundle.Failure, job);
        }   
    }
    
    public static void setProcessingFailed(FBJob job, Exception e) {
        File procFile = new File(PROCESSING_DIR, job.fileName());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(procFile))) {
            bw.write(e.getMessage());
        } catch (IOException ioe) {
            LOGGER.error("Failed writing failure message to processing file for {}", job, ioe);
        }
    }
    
    public static File getReportDir() {
        return REPORT_DIR;
    }
    
    public static boolean hasReport(FBJob job) {
        File reportFile = new File(REPORT_DIR, job.fileName());
        return (reportFile.exists() && reportFile.isFile());
    }
}
