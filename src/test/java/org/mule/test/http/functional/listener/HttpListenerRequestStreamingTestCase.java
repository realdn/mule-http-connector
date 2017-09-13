/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.http.functional.listener;

import static java.lang.String.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mule.functional.api.component.FunctionalTestProcessor.getFromFlow;
import static org.mule.test.http.AllureConstants.HttpFeature.HTTP_EXTENSION;

import org.mule.runtime.api.metadata.DataType;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.test.http.functional.AbstractHttpTestCase;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.InputStreamEntity;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import io.qameta.allure.Feature;

@Feature(HTTP_EXTENSION)
public class HttpListenerRequestStreamingTestCase extends AbstractHttpTestCase {

  private static final String LARGE_MESSAGE = RandomStringUtils.randomAlphanumeric(100 * 1024);

  @Rule
  public DynamicPort listenPort = new DynamicPort("port");
  private String flowReceivedMessage;

  @Override
  protected String getConfigFile() {
    return "http-listener-request-streaming-config.xml";
  }

  @Test
  public void listenerReceivedChunkedRequest() throws Exception {
    String url = format("http://localhost:%s/", listenPort.getNumber());
    getFromFlow(muleContext, "defaultFlow")
        .setEventCallback((context, component, muleContext) -> flowReceivedMessage = muleContext.getTransformationService()
            .transform(context.getMessage(), DataType.STRING).getPayload().getValue().toString());
    testChunkedRequestContentAndResponse(url);
    // We check twice to verify that the chunked request is consumed completely. Otherwise second request would fail
    testChunkedRequestContentAndResponse(url);
  }

  private void testChunkedRequestContentAndResponse(String url) throws Exception {
    Request.Post(url).body(new InputStreamEntity(new ByteArrayInputStream(LARGE_MESSAGE.getBytes()))).connectTimeout(1000)
        .execute();
    assertThat(flowReceivedMessage, is(LARGE_MESSAGE));
  }

}