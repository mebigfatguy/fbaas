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

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * FindBugs foolishly calls System.exit when done. We can not allow this, so
 * this security manager prevents this.
 */
public class FindBugsSecurityManager extends SecurityManager {

	@Override
	public void checkPermission(Permission perm) {
	}

	@Override
	public void checkPermission(Permission perm, Object context) {
	}

	@Override
	public void checkCreateClassLoader() {
	}

	@Override
	public void checkAccess(Thread t) {
	}

	@Override
	public void checkAccess(ThreadGroup g) {

	}

	@Override
	public void checkExec(String cmd) {
	}

	@Override
	public void checkLink(String lib) {
	}

	@Override
	public void checkRead(FileDescriptor fd) {
	}

	@Override
	public void checkRead(String file) {
	}

	@Override
	public void checkRead(String file, Object context) {
	}

	@Override
	public void checkWrite(FileDescriptor fd) {
	}

	@Override
	public void checkWrite(String file) {
	}

	@Override
	public void checkDelete(String file) {
	}

	@Override
	public void checkConnect(String host, int port) {
	}

	@Override
	public void checkConnect(String host, int port, Object context) {
	}

	@Override
	public void checkListen(int port) {
	}

	@Override
	public void checkAccept(String host, int port) {
	}

	@Override
	public void checkMulticast(InetAddress maddr) {
	}

	@Override
	@Deprecated
	public void checkMulticast(InetAddress maddr, byte ttl) {
	}

	@Override
	public void checkPropertiesAccess() {
	}

	@Override
	public void checkPropertyAccess(String key) {
	}

	@Override
	@Deprecated
	public boolean checkTopLevelWindow(Object window) {
		return true;
	}

	@Override
	public void checkPrintJobAccess() {
	}

	@Override
	@Deprecated
	public void checkSystemClipboardAccess() {
	}

	@Override
	@Deprecated
	public void checkAwtEventQueueAccess() {
	}

	@Override
	public void checkPackageAccess(String pkg) {
	}

	@Override
	public void checkPackageDefinition(String pkg) {
	}

	@Override
	public void checkSetFactory() {
	}

	@Override
	@Deprecated
	public void checkMemberAccess(Class<?> clazz, int which) {
	}

	@Override
	public void checkSecurityAccess(String target) {
	}

	@Override
	public void checkExit(int status) {
		throw new SecurityException("Invalid: Status: " + status);
	}
}
