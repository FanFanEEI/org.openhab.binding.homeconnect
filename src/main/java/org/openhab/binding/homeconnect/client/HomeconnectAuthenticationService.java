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
 * This is a singleton instance of the authentication service to get tokens from the Home Connect rest api
 *
 * @author Shawn Crosby
 *         adapted to homeconnect:
 * @author Stefan Foydl (Institute for Factory Automation and Production Systems Friedrich-Alexander-University
 *         Erlangen-Nürnberg)
 *
 */
public class HomeconnectAuthenticationService {
    private static IHomeconnectAuthenticationService instance;

    /**
     * Returns a singleton instance of the current authentication service
     *
     * @return
     */
    public static synchronized IHomeconnectAuthenticationService getInstance() {
        if (instance == null) {
            if (instance == null) {
                instance = new DummyService();
            }
        }
        return instance;
    }

    /**
     * Creates a new singleton authentication service
     *
     * @param service
     */
    public static synchronized void setInstance(IHomeconnectAuthenticationService service) {
        instance = service;
    }

    private static class DummyService implements IHomeconnectAuthenticationService {

        @Override
        public String getAuthToken() {
            return null;
        }

        @Override
        public String refreshToken() throws AuthenticationException {
            return null;
        }

    }
}