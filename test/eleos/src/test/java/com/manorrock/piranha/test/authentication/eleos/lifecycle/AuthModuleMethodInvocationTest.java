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
package com.manorrock.piranha.test.authentication.eleos.lifecycle;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.manorrock.piranha.test.utils.TestWebApp;

/**
 * This tests that the two main methods of a SAM, {@link ServerAuthModule#validateRequest} and
 * {@link ServerAuthModule#secureResponse} are called at the right time, which is resp. before and after the resource (e.g. a
 * Servlet) is invoked.
 * 
 * @author Arjan Tijms
 * 
 */
public class AuthModuleMethodInvocationTest {

    TestWebApp webApp;
    
    @Before
    public void testProtected() throws Exception {
        webApp = Application.get();    
    }
    
    protected TestWebApp getWebApp() {
        return webApp;
    }

    /**
     * Test that the main SAM methods are called and are called in the correct order.
     * 
     * The rule seems simple:
     * <ul>
     * <li>First call validateRequest() in the SAM.
     * <li>Then invoke the requested resource (e.g. a Servlet or JSP page)
     * <li>Finally call secureResponse() in the SAM
     * </ul>
     */
    @Test
    public void testBasicSAMMethodsCalled() throws IOException, SAXException {

        String response = getWebApp().getFromServerPath("protected/servlet");

        // First test if individual methods are called
        assertTrue("SAM method validateRequest not called, but should have been.",
            response.contains("validateRequest invoked"));
        assertTrue("Resource (Servlet) not invoked, but should have been.", response.contains("Resource invoked"));

        // The previous two methods are rare to not be called, but secureResponse is more likely to fail. Seemingly it's hard
        // to understand what this method should do exactly.
        assertTrue("SAM method secureResponse not called, but should have been.",
            response.contains("secureResponse invoked"));

        int validateRequestIndex = response.indexOf("validateRequest invoked");
        int resourceIndex = response.indexOf("Resource invoked");
        int secureResponseIndex = response.indexOf("secureResponse invoked");
        
        // Finally the order should be correct. More than a few implementations call secureResponse before the resource is
        // invoked.
        assertTrue("SAM methods called in wrong order",
            validateRequestIndex < resourceIndex && resourceIndex <  secureResponseIndex);
    }

    /**
     * Test that the SAM's cleanSubject method is called following a call to {@link HttpServletRequest#logout()}.
     * <p>
     * Although occasionally a JASPIC 1.0 implementation indeed does this, it's only mandated that this happens in JASPIC 1.1
     */
    @Test
    public void testLogout() throws IOException, SAXException {

        // Note that we don't explicitly log-in; the test SAM uses for this test does that automatically before the resource
        // (servlet)
        // is invoked. Once we reach the Servlet we should be logged-in and can proceed to logout.
        String response = getWebApp().getFromServerPath("protected/servlet?doLogout=true");

        assertTrue("SAM method cleanSubject not called, but should have been.",
            response.contains("cleanSubject invoked"));
    }

}