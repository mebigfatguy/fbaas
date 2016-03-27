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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
/**
 * a findbugs bug to be sent back to the client
 */
public class Bug {
    private String category;
    private String type;
    private String className;
    private String methodName;
    private String fieldName;
    private int lineStart;
    private int lineEnd;

    public String getCategory() {
        return category;
    }

    public void setCategory(String bugCategory) {
        category = bugCategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String bugType) {
        type = bugType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String clsName) {
        className = clsName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methName) {
        methodName = methName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fldName) {
        fieldName = fldName;
    }

    public int getLineStart() {
        return lineStart;
    }

    public void setLineStart(int lnStart) {
        lineStart = lnStart;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(int lnEnd) {
        lineEnd = lnEnd;
    }
}
