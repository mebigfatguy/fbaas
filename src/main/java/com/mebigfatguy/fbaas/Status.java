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
package com.mebigfatguy.fbaas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

public class Status {

    private static final Logger LOGGER = LoggerFactory.getLogger(Status.class);

    private static final Path FBAAS_DIR = Paths.get(System.getProperty("user.home"), ".fbaas");
    private static final Path PROCESSING_DIR = FBAAS_DIR.resolve("processing");
    private static final Path REPORT_DIR = FBAAS_DIR.resolve("reports");
    private static final Duration MAX_PROCESSING_TIME = Duration.of(1, ChronoUnit.HOURS);

    static {
        try {
            Files.createDirectories(FBAAS_DIR);
            Files.createDirectories(PROCESSING_DIR);
            Files.createDirectories(REPORT_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create basic directories: {} or {} or {}", FBAAS_DIR, PROCESSING_DIR, REPORT_DIR);
        }
    }

    private Status() {
    }

    public static boolean isProcessing(Artifact job) {
        try {
            Path procPath = PROCESSING_DIR.resolve(job.fileName());
            if (!Files.exists(procPath) || Files.isDirectory(procPath)) {
                return false;
            }

            Instant now = Instant.now();
            Instant fileTime = Instant.ofEpochMilli(Files.getLastModifiedTime(procPath).toMillis());
            Duration d = Duration.between(fileTime, now);
            return (d.compareTo(MAX_PROCESSING_TIME) < 0);
        } catch (IOException e) {
            return false;
        }
    }

    public static void setProcessing(Artifact job) {
        try {
            Path procPath = PROCESSING_DIR.resolve(job.fileName());
            if (Files.exists(procPath) && !Files.isDirectory(procPath)) {
                return;
            }
            Files.createFile(procPath);
            procPath.toFile().deleteOnExit();
        } catch (IOException e) {
            LOGGER.error("Failed to create processing file for {}", job, e);
        }
    }

    public static String getProcessingFailed(Locale locale, Artifact job) {
        try {
            Path procPath = PROCESSING_DIR.resolve(job.fileName());
            if (Files.size(procPath) == 0L) {
                return null;
            }

            try (BufferedReader r = Files.newBufferedReader(procPath, StandardCharsets.UTF_8)) {
                return Bundle.getString(locale, Bundle.Failure, r.readLine());
            } catch (IOException e) {
                LOGGER.error("Failed reading failure message in processing file for {}", job, e);
                return Bundle.getString(locale, Bundle.Failure, job);
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static void setProcessingFailed(Artifact job, Exception e) {
        Path procPath = PROCESSING_DIR.resolve(job.fileName());
        try (BufferedWriter bw = Files.newBufferedWriter(procPath, StandardCharsets.UTF_8)) {
            bw.write(e.getMessage());
        } catch (IOException ioe) {
            LOGGER.error("Failed writing failure message to processing file for {}", job, ioe);
        }
    }

    public static Path getReportFile(Artifact job) {
        return REPORT_DIR.resolve(job.fileName());
    }

    public static boolean hasReport(Artifact job) {
        Path reportPath = REPORT_DIR.resolve(job.fileName());
        return (Files.exists(reportPath) && !Files.isDirectory(reportPath));
    }

    public static void deleteProcessingFile(Artifact job) {
        try {
            Path procPath = PROCESSING_DIR.resolve(job.fileName());
            Files.delete(procPath);
        } catch (IOException e) {
            // what to do, anything
        }
    }

    public static void deleteReport(Artifact job) {
        try {
            Path reportPath = REPORT_DIR.resolve(job.fileName());
            Files.delete(reportPath);
        } catch (IOException e) {
            // what to do, anything
        }
    }
}
