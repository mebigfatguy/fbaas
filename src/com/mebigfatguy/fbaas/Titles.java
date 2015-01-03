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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

@XmlRootElement()
public class Titles {
	private String title;
	private String description;
	private String groupId;
	private String artifactId;
	private String version;
	private String category;
	private String type;
	private String message;
	private String fieldMethod;
	private String location;
	
	public Titles(Locale locale) {
		title = Bundle.getString(locale, Bundle.Title);
		description = Bundle.getString(locale, Bundle.Description);
		groupId = Bundle.getString(locale, Bundle.GroupId);
		artifactId = Bundle.getString(locale, Bundle.ArtifactId);
		version = Bundle.getString(locale, Bundle.Version);
		category = Bundle.getString(locale, Bundle.Category);
		type = Bundle.getString(locale, Bundle.Type);
		message = Bundle.getString(locale, Bundle.Message);
		fieldMethod = Bundle.getString(locale, Bundle.FieldMethod);
		location = Bundle.getString(locale, Bundle.Location);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getFieldMethod() {
        return fieldMethod;
    }

    public String getLocation() {
        return location;
    }

    @Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
