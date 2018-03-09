/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.homeconnect.client.CloudOauthHomeconnectAuthenticationService;
import org.openhab.binding.homeconnect.client.HomeconnectAuthenticationService;
import org.openhab.binding.homeconnect.client.IHomeconnectAuthenticationService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service reads configuration from the services/homeconnect.cfg file and uses it to instantiate the
 * authentication service needed by the binding in order to retrieve and refresh access_tokens
 *
 * @author Shawn Crosby
 *         adopted to Home Connect:
 * @author Stefan Foydl (Institute for Factory Automation and Production Systems Friedrich-Alexander-University
 *         Erlangen-Nürnberg)
 *
 */
public class AuthenticationConfigurationService implements ManagedService {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationConfigurationService.class);

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (!properties.isEmpty() && properties.get("client_id") != null) {
            configure(properties);
        }
    }

    private void configure(Dictionary<String, ?> properties) {
        IHomeconnectAuthenticationService service = null;

        // if client_id exists, then write values in map
        if (properties.get("client_id") != null) {
            Map<String, String> props = new HashMap<String, String>();
            props.put("client_id", (String) properties.get("client_id"));
            props.put("client_secret", (String) properties.get("client_secret"));
            props.put("refresh_token", (String) properties.get("refresh_token"));
            logger.debug("Configuring Homeconnect Authentication Service {}", props);
            service = new CloudOauthHomeconnectAuthenticationService(props);
        }

        HomeconnectAuthenticationService.setInstance(service);
    }

    /**
     * Called by the framework when the services is activated. Reads the config and configures the
     * service
     *
     * @param context The context of the component as defined by the framework
     * @throws Exception
     */
    public void activate(ComponentContext context) throws Exception {
        Dictionary<String, Object> properties = context.getProperties();
        if (!properties.isEmpty() && properties.get("client_id") != null) {
            logger.debug("Configuring auth service with found properties {}", properties);
            configure(properties);
        }
    }

    /**
     * Called by the framework when the service is deactivated.
     *
     * @param context The context of the component as defined by the framework
     * @throws Exception
     */
    public void deactivate(ComponentContext context) throws Exception {
        logger.debug("Deactivating AuthConfigService");
    }

}