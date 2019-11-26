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
package com.manorrock.piranha.test.authorization.exousia;

import static cloud.piranha.authorization.exousia.AuthorizationPreInitializer.AUTHZ_FACTORY_CLASS;
import static cloud.piranha.authorization.exousia.AuthorizationPreInitializer.AUTHZ_POLICY_CLASS;
import static cloud.piranha.authorization.exousia.AuthorizationPreInitializer.UNCHECKED_PERMISSIONS;
import static com.manorrock.piranha.builder.WebApplicationBuilder.newWebApplication;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.security.jacc.WebUserDataPermission;

import org.junit.Test;
import org.omnifaces.exousia.modules.def.DefaultPolicy;
import org.omnifaces.exousia.modules.def.DefaultPolicyConfigurationFactory;

import com.manorrock.piranha.api.WebApplication;
import com.manorrock.piranha.test.utils.TestHttpServletRequest;
import com.manorrock.piranha.test.utils.TestHttpServletResponse;

import cloud.piranha.authorization.exousia.AuthorizationPreInitializer;
import cloud.piranha.security.jakarta.JakartaSecurityInitializer;

/**
 * The JUnit tests for the basic connection test
 *
 * @author Arjan Tijms (arjan.tijms@gmail.com)
 */
public class BasicConnectionTest {

    /**
     * Test basic connection permission using a non-secure (http) connection.
     *
     * @throws Exception
     */
    @Test
    public void testNonSecureConnection() throws Exception {
        WebApplication webApp = 
            newWebApplication()
                .addAttribute(AUTHZ_FACTORY_CLASS, DefaultPolicyConfigurationFactory.class)
                .addAttribute(AUTHZ_POLICY_CLASS, DefaultPolicy.class)
                .addAttribute(UNCHECKED_PERMISSIONS, asList(
                    new WebUserDataPermission("/*", "!GET"),
                    new WebUserDataPermission("/*", "GET:CONFIDENTIAL")))
                .addInitializer(AuthorizationPreInitializer.class)
                
                .addInitializer(JakartaSecurityInitializer.class)
                
                .addServlet(PublicServlet.class, "/public/servlet")
                .start();
        
        TestHttpServletRequest request = new TestHttpServletRequest(webApp, "", "/public/servlet");
        TestHttpServletResponse response = new TestHttpServletResponse();

        webApp.service(request, response);

        assertFalse(response.getResponseBodyAsString().contains("Hello, from Servlet!"));
    }
    
    /**
     * Test basic connection permission using a secure (https) connection.
     *
     * @throws Exception
     */
    @Test
    public void testSecureConnection() throws Exception {
        WebApplication webApp = 
            newWebApplication()
                .addAttribute(AUTHZ_FACTORY_CLASS, DefaultPolicyConfigurationFactory.class)
                .addAttribute(AUTHZ_POLICY_CLASS, DefaultPolicy.class)
                .addAttribute(UNCHECKED_PERMISSIONS, asList(
                    new WebUserDataPermission("/*", "!GET"),
                    new WebUserDataPermission("/*", "GET:CONFIDENTIAL")))
                .addInitializer(AuthorizationPreInitializer.class)
                
                .addServlet(PublicServlet.class, "/public/servlet")
                .start();
        
        TestHttpServletRequest request = new TestHttpServletRequest(webApp, "", "/public/servlet");
        request.setScheme("https");
        
        TestHttpServletResponse response = new TestHttpServletResponse();

        webApp.service(request, response);

        assertEquals(200, response.getStatus());
        assertTrue(response.getResponseBodyAsString().contains("Hello, from Servlet!"));
    }
    
    @Test
    public void testSecureConnectionExactMapping() throws Exception {
        WebApplication webApp = 
            newWebApplication()
                .addAttribute(AUTHZ_FACTORY_CLASS, DefaultPolicyConfigurationFactory.class)
                .addAttribute(AUTHZ_POLICY_CLASS, DefaultPolicy.class)
                .addAttribute(UNCHECKED_PERMISSIONS, asList(
                    new WebUserDataPermission("/public/servlet", "!GET"),
                    new WebUserDataPermission("/public/servlet", "GET:CONFIDENTIAL")))
                .addInitializer(AuthorizationPreInitializer.class)
                
                .addServlet(PublicServlet.class, "/public/servlet")
                .start();
        
        TestHttpServletRequest request = new TestHttpServletRequest(webApp, "", "/public/servlet");
        request.setScheme("https");
        
        TestHttpServletResponse response = new TestHttpServletResponse();

        webApp.service(request, response);

        assertEquals(200, response.getStatus());
        assertTrue(response.getResponseBodyAsString().contains("Hello, from Servlet!"));
    }
}
