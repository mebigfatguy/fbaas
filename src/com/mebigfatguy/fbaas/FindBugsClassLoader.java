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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class FindBugsClassLoader extends ClassLoader {

	private URLClassLoader loader;
	
	public FindBugsClassLoader() {
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loader.loadClass(name);
	}
	
	@Override
	public URL getResource(String name) {
		return loader.getResource(name);
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return loader.getResources(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		return loader.getResourceAsStream(name);
	}
}
