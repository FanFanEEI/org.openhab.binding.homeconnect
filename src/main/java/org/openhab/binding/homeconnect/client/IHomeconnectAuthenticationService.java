/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.client;

/**
 * This Interface abstracts away the token fetching method because getting an app key to get
 * tokens is a bit clunky. For now, the configuration requires that the service is provided with
 * an application id (API Key), a redirect URL, two wildcards for the redirected code and refresh token in order to
 * function.
 *
 * The service provides a method for retrieving and refreshing an access token.
 *
 * @author scrosby
 *         adapted to homeconnect: Stefan Foydl
 *
 */
public interface IHomeconnectAuthenticationService {
    /**
     * Returns the current access token persisted in the service
     *
     * @return String access token
     */
    public String getAuthToken();

    /**
     * Returns a refreshed access token and hopefully persists the token for the next call
     * to the getAuthToken() method.
     *
     * @return String newly refreshed access token
     */
    public String refreshToken() throws AuthenticationException;
}