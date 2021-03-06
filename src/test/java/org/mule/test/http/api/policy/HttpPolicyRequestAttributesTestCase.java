/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.http.api.policy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;
import org.mule.runtime.api.util.MultiMap;
import org.mule.test.http.api.AbstractHttpAttributesTestCase;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.junit.Test;

public class HttpPolicyRequestAttributesTestCase extends AbstractHttpAttributesTestCase {

  private static final String TO_STRING_COMPLETE = "org.mule.extension.http.api.policy.HttpPolicyRequestAttributes\n" +
      "{\n" +
      "   Request path=/request/path\n" +
      "   Headers=[\n" +
      "      header2=headerValue2\n" +
      "      header1=headerValue1\n" +
      "   ]\n" +
      "   Query Parameters=[\n" +
      "      queryParam1=queryParamValue1\n" +
      "      queryParam2=queryParamValue2\n" +
      "   ]\n" +
      "   URI Parameters=[\n" +
      "      uriParam1=uriParamValue1\n" +
      "      uriParam2=uriParamValue2\n" +
      "   ]\n" +
      "}";

  private static final String TO_STRING_EMPTY = "org.mule.extension.http.api.policy.HttpPolicyRequestAttributes\n" +
      "{\n" +
      "   Request path=null\n" +
      "   Headers=[]\n" +
      "   Query Parameters=[]\n" +
      "   URI Parameters=[]\n" +
      "}";

  private static final String TO_STRING_QUERY_PARAMS = "org.mule.extension.http.api.policy.HttpPolicyRequestAttributes\n" +
      "{\n" +
      "   Request path=null\n" +
      "   Headers=[]\n" +
      "   Query Parameters=[\n" +
      "      queryParam1=queryParamValue1\n" +
      "      queryParam2=queryParamValue2\n" +
      "   ]\n" +
      "   URI Parameters=[]\n" +
      "}";

  private static final String TO_STRING_URI_PARAMS = "org.mule.extension.http.api.policy.HttpPolicyRequestAttributes\n" +
      "{\n" +
      "   Request path=null\n" +
      "   Headers=[]\n" +
      "   Query Parameters=[]\n" +
      "   URI Parameters=[\n" +
      "      uriParam1=uriParamValue1\n" +
      "      uriParam2=uriParamValue2\n" +
      "   ]\n" +
      "}";

  private static final String TO_STRING_OBFUSCATED = "org.mule.extension.http.api.policy.HttpPolicyRequestAttributes\n" +
      "{\n" +
      "   Request path=/request/path\n" +
      "   Headers=[\n" +
      "      password=****\n" +
      "      pass=****\n" +
      "      client_secret=****\n" +
      "      regular=show me\n" +
      "   ]\n" +
      "   Query Parameters=[\n" +
      "      password=****\n" +
      "      pass=****\n" +
      "      client_secret=****\n" +
      "      regular=show me\n" +
      "   ]\n" +
      "   URI Parameters=[\n" +
      "      password=****\n" +
      "      pass=****\n" +
      "      client_secret=****\n" +
      "      regular=show me\n" +
      "   ]\n" +
      "}";

  private Object requestAttributes;

  @Test
  public void completeToString() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    requestAttributes = new HttpPolicyRequestAttributes(getHeaders(), getQueryParams(), getUriParams(), "/request/path");
    assertThat(TO_STRING_COMPLETE, is(requestAttributes.toString()));
  }

  @Test
  public void emptyToString() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    requestAttributes = new HttpPolicyRequestAttributes(new MultiMap<>(), new MultiMap<>(), new HashMap<>(), null);
    assertThat(TO_STRING_EMPTY, is(requestAttributes.toString()));
  }

  @Test
  public void onlyQueryParamToString() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    requestAttributes = new HttpPolicyRequestAttributes(new MultiMap<>(), getQueryParams(), new HashMap<>(), null);
    assertThat(TO_STRING_QUERY_PARAMS, is(requestAttributes.toString()));
  }

  @Test
  public void onlyUriParamToString() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    requestAttributes = new HttpPolicyRequestAttributes(new MultiMap<>(), new MultiMap<>(), getUriParams(), null);
    assertThat(TO_STRING_URI_PARAMS, is(requestAttributes.toString()));
  }

  @Test
  public void sensitiveContentIsHidden() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    MultiMap<String, String> sensitiveDataMultiMap = prepareSensitiveDataMap(new MultiMap<>());
    requestAttributes = new HttpPolicyRequestAttributes(sensitiveDataMultiMap, sensitiveDataMultiMap,
                                                        prepareSensitiveDataMap(new HashMap<>()), "/request/path");
    assertThat(TO_STRING_OBFUSCATED, is(requestAttributes.toString()));
  }

}
