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
 * This is a singleton instance of a Home Connect client for communicating with the Home Connect rest api.
 *
 * @author Shawn Crosby
 *         adapted to Home Connect: Stefan Foydl
 *
 */
public class HomeconnectClient {
    private static IHomeconnectClient instance;

    /**
     * Get a singleton instance of the Home Connect client
     *
     * @return
     */
    public static synchronized IHomeconnectClient getInstance() {
        if (instance == null) {
            instance = new CloudRestfulHomeconnectClient();
        }

        return instance;
    }

    /**
     * Allows for setting an instance of a new client. Mostly for unit tests
     *
     * @param testClient
     */
    public static synchronized void setInstance(IHomeconnectClient testClient) {
        instance = testClient;
    }
}