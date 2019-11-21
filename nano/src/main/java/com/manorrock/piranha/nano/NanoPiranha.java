/*
 * Copyright (c) 2002-2019 Manorrock.com. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *   3. Neither the name of the copyright holder nor the names of its 
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.piranha.nano;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * The nano version of Piranha.
 *
 * <p>
 * This version of Piranha allows you to define your filters and your servlet
 * completely programmatically and it will service a request and response by
 * means of an input and output stream.
 * </p>
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class NanoPiranha {

    /**
     * Stores the filters.
     */
    private final LinkedList<Filter> filters = new LinkedList<>();

    /**
     * Stores the servlet.
     */
    private Servlet servlet;

    /**
     * Add a filter.
     *
     * @param filter the filter.
     */
    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    /**
     * Service.
     *
     * @param inputStream the input stream.
     * @param outputStream the output stream.
     * @throws IOException when an I/O error occurs.
     * @throws ServletException when a Servlet error occurs.
     */
    public void service(InputStream inputStream, OutputStream outputStream)
            throws IOException, ServletException {
        Iterator<Filter> iterator = filters.descendingIterator();
        NanoFilterChain chain = new NanoFilterChain(servlet);
        while (iterator.hasNext()) {
            Filter filter = iterator.next();
            chain = new NanoFilterChain(filter, chain);
        }
        NanoHttpServletResponse response = new NanoHttpServletResponse(outputStream);
        chain.doFilter(new NanoHttpServletRequest(inputStream), response);
        response.flushBuffer();
    }

    /**
     * Set the servlet.
     *
     * @param servlet the servlet.
     */
    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }
}
