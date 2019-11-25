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

import java.io.OutputStream;

/**
 * The Nano response builder.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class NanoResponseBuilder {
    
    /**
     * Stores if we set the output stream.
     */
    private boolean outputStreamSet;
    
    /**
     * Stores the response.
     */
    private final NanoResponse response;

    /**
     * Constructor.
     */
    public NanoResponseBuilder() {
        outputStreamSet = false;
        response = new NanoResponse();
    }
    
    /**
     * Set the body only flag.
     * 
     * @param bodyOnly if true the response will only output the body, if false 
     * the response will contain the status line and response headers.
     * @return the builder.
     */
    public NanoResponseBuilder bodyOnly(boolean bodyOnly) {
        response.setBodyOnly(bodyOnly);
        return this;
    }

    /**
     * Build the response.
     *
     * @return the response.
     */
    public NanoResponse build() {
        if (!outputStreamSet) {
            throw new RuntimeException("You need set an output stream");
        }
        return response;
    }
    
    /**
     * Set the output stream.
     * 
     * @param outputStream the output stream.
     * @return the builder.
     */
    public NanoResponseBuilder outputStream(OutputStream outputStream) {
        response.setOutputStream(outputStream);
        outputStreamSet = true;
        return this;
    }
}