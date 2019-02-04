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

package com.mebigfatguy.fbaas;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public enum Bundle {

    Title, Description, GroupId, ArtifactId, Version, Category, Type, Message, FieldMethod, Location, Starting, Processing, Complete, Failure;

    public static String getString(Locale locale, Bundle key, Object... parms) {

        ResourceBundle bundle = ResourceBundle.getBundle("com/mebigfatguy/fbaas/bundle", locale);
        String fmt = bundle.getString(key.name());
        if ((parms == null) || (parms.length == 0)) {
            return fmt;
        }

        return MessageFormat.format(fmt, parms);
    }
}
