/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api.request.authentication;

import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;

/**
 * An object that authenticates an HTTP request.
 *
 * @since 1.0
 */
public interface HttpRequestAuthentication {

  /**
   * Adds authentication information to the request. This method will be executed before creating and sending the request.
   * Implementations will usually add some authentication header, but there is no restriction on this.
   *
   * @param builder The builder that is being used to create the HTTP request.
   */
  void authenticate(HttpRequestBuilder builder) throws MuleException;

  /**
   * Detects if there was an authentication failure in the response. After sending an HTTP request and creating a result with the
   * response, this method will be executed. If it returns false, the flow continues executing. If it returns true, the requester
   * will try to send the request again.
   *
   * @param firstAttemptResult The result with the response of the request.
   * @return True if the request should be sent again, false otherwise.
   */
  boolean shouldRetry(Result<Object, HttpResponseAttributes> firstAttemptResult) throws MuleException;

}
