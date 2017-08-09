/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.http.functional.requester;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED;
import static org.glassfish.grizzly.http.server.Constants.CLOSE;
import static org.glassfish.grizzly.http.server.Constants.CONNECTION;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.http.api.HttpHeaders.Names.PROXY_AUTHENTICATE;
import static org.mule.runtime.http.api.HttpHeaders.Names.PROXY_AUTHORIZATION;
import static org.mule.test.http.AllureConstants.HttpFeature.HTTP_EXTENSION;
import static org.mule.test.http.AllureConstants.HttpFeature.HttpStory.NTLM;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Test;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.runtime.api.message.Message;
import org.mule.test.http.functional.matcher.HttpMessageAttributesMatchers;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;

@Feature(HTTP_EXTENSION)
@Story(NTLM)
@DisplayName("HTTPS server behind NTLM HTTP proxy. Authentication is required.")
public class HttpsRequestNtlmProxyTestCase extends AbstractNtlmTestCase {

  private static final String TARGET_RESPONSE = "Response";

  @Override
  protected AbstractHandler createHandler(Server server) {
    return new ConnectHandler() {

      boolean authenticated = false;

      @Override
      public void handle(String target, Request baseRequest, HttpServletRequest request,
                         HttpServletResponse response)
          throws ServletException, IOException {
        if (request.isSecure()) {
          // if the request is secure, the response
          // from the https target is being handled
          simpleResponseFromTarget(response);
        } else {
          super.handle(target, baseRequest, request, response);
        }
      }

      @Override
      protected void handleConnect(org.eclipse.jetty.server.Request baseRequest, HttpServletRequest request,
                                   HttpServletResponse response, String serverAddress) {
        super.handleConnect(baseRequest, request, response, serverAddress);
        if (!authenticated) {
          try {
            response.getOutputStream().flush();
          } catch (IOException e) {
            // Do nothing
          }
        }
      }

      @Override
      protected boolean handleAuthentication(HttpServletRequest request, HttpServletResponse response,
                                             String address) {
        try {
          authenticated = authorizeRequest(address, request, response, false);
          return authenticated;
        } catch (IOException e) {
          return false;
        }
      }
    };
  }

  public HttpsRequestNtlmProxyTestCase() {
    super(PROXY_AUTHORIZATION, PROXY_AUTHENTICATE, SC_PROXY_AUTHENTICATION_REQUIRED);
  }

  @Override
  protected String getConfigFile() {
    return "https-request-ntlm-proxy-config.xml";
  }

  @Test
  @Description("Verifies that HTTP CONNECT is established for TLS tunnelling through a NTLM Proxy with mandatory auth.")
  public void validNtlmAuth() throws Exception {
    Message response = runFlow(getFlowName()).getMessage();

    assertThat((HttpResponseAttributes) response.getAttributes().getValue(), HttpMessageAttributesMatchers.hasStatusCode(SC_OK));
    assertThat(getPayloadAsString(response), equalTo(TARGET_RESPONSE));
  }

  private void simpleResponseFromTarget(HttpServletResponse response) throws IOException {
    response.setHeader(CONNECTION, CLOSE);
    response.getOutputStream().print(TARGET_RESPONSE);
    response.setStatus(SC_OK);
    response.getOutputStream().flush();
    response.getOutputStream().close();
  }

  protected boolean enableHttps() {
    return true;
  }
}