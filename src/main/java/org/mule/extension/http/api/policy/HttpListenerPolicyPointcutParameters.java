/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api.policy;

import org.mule.runtime.api.component.Component;
import org.mule.runtime.http.policy.api.HttpPolicyPointcutParameters;
import org.mule.runtime.policy.api.PolicyPointcutParameters;

/**
 * Specific implementation of {@link PolicyPointcutParameters} for http:listener operation.
 * 
 * @since 1.0
 */
public class HttpListenerPolicyPointcutParameters extends HttpPolicyPointcutParameters {

  /**
   * Creates a new {@link PolicyPointcutParameters}
   *
   * @param component the component where the policy is being applied.
   * @param path the target path of the incoming request
   * @param method the HTTP method of the incoming request
   */
  public HttpListenerPolicyPointcutParameters(Component component, String path, String method) {
    super(component, path, method);
  }

}
