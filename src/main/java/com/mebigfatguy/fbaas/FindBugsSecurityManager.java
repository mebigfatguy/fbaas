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

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * FindBugs foolishly calls System.exit when done. We can not allow this, so this security manager prevents this.
 */
public class FindBugsSecurityManager extends SecurityManager {

    @Override
    public void checkPermission(Permission perm) {
        // allow
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        // allow
    }

    @Override
    public void checkCreateClassLoader() {
        // allow
    }

    @Override
    public void checkAccess(Thread t) {
        // allow
    }

    @Override
    public void checkAccess(ThreadGroup g) {
        // allow
    }

    @Override
    public void checkExec(String cmd) {
        // allow
    }

    @Override
    public void checkLink(String lib) {
        // allow
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        // allow
    }

    @Override
    public void checkRead(String file) {
        // allow
    }

    @Override
    public void checkRead(String file, Object context) {
        // allow
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        // allow
    }

    @Override
    public void checkWrite(String file) {
        // allow
    }

    @Override
    public void checkDelete(String file) {
        // allow
    }

    @Override
    public void checkConnect(String host, int port) {
        // allow
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        // allow
    }

    @Override
    public void checkListen(int port) {
        // allow
    }

    @Override
    public void checkAccept(String host, int port) {
        // allow
    }

    @Override
    public void checkMulticast(InetAddress maddr) {
        // allow
    }

    @Override
    @Deprecated
    public void checkMulticast(InetAddress maddr, byte ttl) {
        // allow
    }

    @Override
    public void checkPropertiesAccess() {
        // allow
    }

    @Override
    public void checkPropertyAccess(String key) {
        // allow
    }

    @Override
    @Deprecated
    public boolean checkTopLevelWindow(Object window) {
        return true;
    }

    @Override
    public void checkPrintJobAccess() {
        // allow
    }

    @Override
    @Deprecated
    public void checkSystemClipboardAccess() {
        // allow
    }

    @Override
    @Deprecated
    public void checkAwtEventQueueAccess() {
        // allow
    }

    @Override
    public void checkPackageAccess(String pkg) {
        // allow
    }

    @Override
    public void checkPackageDefinition(String pkg) {
        // allow
    }

    @Override
    public void checkSetFactory() {
        // allow
    }

    @Override
    @Deprecated
    public void checkMemberAccess(Class<?> clazz, int which) {
        // allow
    }

    @Override
    public void checkSecurityAccess(String target) {
        // allow
    }

    @Override
    public void checkExit(int status) {
        throw new SecurityException("Invalid: Status: " + status);
    }
}
