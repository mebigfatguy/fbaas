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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class FindBugsResultsProcessor {

    public Results getResults(Locale locale, Artifact job) {
        if (Status.isProcessing(job)) {
            String failure = Status.getProcessingFailed(locale, job);
            if (failure != null) {
                return new Results(failure, null);
            }
            
            String status = Bundle.getString(locale, Bundle.Processing, job);
            return new Results(status, null);
        }
        if (!Status.hasReport(job)) {
            String status = Bundle.getString(locale, Bundle.Starting, job);
            return new Results(status, null);
        }
        
        String status = Bundle.getString(locale, Bundle.Complete, job);
        List<Bug> bugs = processBugs(job);
        return new Results(status, bugs);
    }
    
    private List<Bug> processBugs(Artifact job) {
        File reportFile = Status.getReportFile(job);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(reportFile))) {
            List<Bug> bugs = new ArrayList<>();
            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(new BugsHandler(bugs));
            r.parse(new InputSource(bis));
            
            return bugs;
        } catch (SAXException | IOException e) {
            return Collections.emptyList();
        }
    }
    
    private static class BugsHandler extends DefaultHandler {
        
        private List<Bug> bugReport;
        Bug bug;
        
        public BugsHandler(List<Bug> bugs) {
            bugReport = bugs;
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            switch (localName) {
            case "BugInstance":
                bug = new Bug();
                bug.setCategory(attributes.getValue("category"));
                bug.setType(attributes.getValue("type"));
                break;
                
            case "Class":
                bug.setClassName(attributes.getValue("classname"));
                break;
                
            case "Field":
            case "Method":
                bug.setMethodName(attributes.getValue("name"));
                break;
                
            case "SourceLine":
                try {
                    bug.setLineStart(Integer.parseInt(attributes.getValue("start")));
                    bug.setLineEnd(Integer.parseInt(attributes.getValue("end")));
                } catch (Exception e) {
                    //ignore
                }
                break;
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("BugInstance".equals(localName)) {
                bugReport.add(bug);
                bug = null;
            }
        }
    }
}
