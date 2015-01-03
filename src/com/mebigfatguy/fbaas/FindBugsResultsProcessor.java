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

import java.util.Locale;

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
        //process bugs
        return new Results(status, null);
    }
}
