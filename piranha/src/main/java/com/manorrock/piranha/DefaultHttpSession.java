/*
 *  Copyright (c) 2002-2018, Manorrock.com. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      1. Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.piranha;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * The default HttpSession.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class DefaultHttpSession implements HttpSession {

    /**
     * Stores the attributes.
     */
    private HashMap<String, Object> attributes = new HashMap<>();

    /**
     * Stores the creation time.
     */
    private long creationTime;

    /**
     * Stores the session id.
     */
    private String id;

    /**
     * Stores the last accessed time.
     */
    private long lastAccessedTime;

    /**
     * Stores the max inactive interval.
     */
    private int maxInactiveInterval;

    /**
     * Stores if the session is new.
     */
    private boolean newFlag;

    /**
     * Stores the servlet context.
     */
    private ServletContext servletContext;

    /**
     * Stores the HTTP session manager.
     */
    private HttpSessionManager sessionManager;

    /**
     * Stores the valid flag.
     */
    private boolean valid;

    /**
     * Constructor.
     *
     * @param servletContext the servlet context.
     */
    public DefaultHttpSession(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = System.currentTimeMillis();
        this.valid = true;
    }

    /**
     * Constructor.
     *
     * @param servletContext the servlet context.
     * @param id the id.
     * @param newFlag the new flag.
     */
    public DefaultHttpSession(ServletContext servletContext, String id, boolean newFlag) {
        this.servletContext = servletContext;
        this.id = id;
        this.newFlag = newFlag;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = System.currentTimeMillis();
        this.valid = true;
    }

    /**
     * @param name
     * @return
     * @see HttpSession#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name) {
        verifyValid("getAttribute");
        return this.attributes.get(name);
    }

    /**
     * @return @see HttpSession#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        verifyValid("getAttributeNames");
        return Collections.enumeration(attributes.keySet());
    }

    /**
     * @return @see HttpSession#getCreationTime()
     */
    @Override
    public long getCreationTime() {
        verifyValid("getCreationTime");
        return this.creationTime;
    }

    /**
     * @return @see HttpSession#getId()
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * @return @see HttpSession#getLastAccessedTime()
     */
    @Override
    public long getLastAccessedTime() {
        verifyValid("getLastAccessedTime");
        return this.lastAccessedTime;
    }

    /**
     * @return @see HttpSession#getMaxInactiveInterval()
     */
    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    /**
     * @return @see HttpSession#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @return @see HttpSession#getSessionContext()
     */
    @Override
    @SuppressWarnings("deprecation")
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * @param name
     * @return
     * @see HttpSession#getValue(java.lang.String)
     */
    @Override
    @SuppressWarnings("deprecation")
    public Object getValue(String name) {
        return getAttribute(name);
    }

    /**
     * @return @see HttpSession#getValueNames()
     */
    @Override
    @SuppressWarnings("deprecation")
    public String[] getValueNames() {
        verifyValid("getValueNames");
        return this.attributes.keySet().toArray(new String[0]);
    }

    /**
     * @see HttpSession#invalidate()
     */
    @Override
    public void invalidate() {
        verifyValid("invalidate");
        this.valid = false;
    }

    /**
     * @return @see HttpSession#isNew()
     */
    @Override
    public boolean isNew() {
        verifyValid("isNew");
        return this.newFlag;
    }

    /**
     * @param name
     * @param value
     * @see HttpSession#putValue(java.lang.String, java.lang.Object)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    /**
     * @param name
     * @see HttpSession#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String name) {
        verifyValid("removeAttribute");
        this.attributes.remove(name);
    }

    /**
     * @param name
     * @see HttpSession#removeValue(java.lang.String)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void removeValue(String name) {
        removeAttribute(name);
    }

    /**
     * @param name
     * @param value
     * @see HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object value) {
        verifyValid("setAttribute");
        if (value != null) {
            boolean added = true;
            if (attributes.containsKey(name)) {
                added = false;
            }
            attributes.put(name, value);
            if (added) {
                sessionManager.attributeAdded(this, name, value);
            } else {
                sessionManager.attributeReplaced(this, name, value);
            }
        } else {
            removeAttribute(name);
        }
    }

    /**
     * Set the id.
     *
     * @param id the id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param maxInactiveInterval
     * @see HttpSession#setMaxInactiveInterval(int)
     */
    @Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /**
     * Set the new flag.
     *
     * @param newFlag the new flag.
     */
    public void setNew(boolean newFlag) {
        verifyValid("setNew");
        this.newFlag = newFlag;
    }

    /**
     * Set the HTTP session manager.
     *
     * @param sessionManager the HTTP session manager.
     */
    public void setSessionManager(HttpSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Verify if the session is valid.
     */
    private void verifyValid(String methodName) {
        if (!valid) {
            throw new IllegalStateException("Session is invalid, called by: " + methodName);
        }
    }
}