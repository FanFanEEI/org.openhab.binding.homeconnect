/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.client;

import java.util.Map;

/**
 * This object represents a device connected to the Home Connect account. Currently abstracts away the json bits
 *
 * @author scrosby
 *         adapted to homeconnect: Stefan Foydl
 *
 */
public interface IHomeconnectDevice {
    /**
     * The HAID of the device
     *
     * @return String unique device ID
     */
    public String getId();

    /**
     * The Name of the device
     *
     * @return String the name of the device
     */
    public String getName();

    /**
     * The Device Type
     *
     * @return Enum that represents the type of device this is
     */
    public HomeconnectSupportedDevice getDeviceType();

    /**
     * The current state of the device
     *
     * @return A Map of state parameters and values for the device
     */
    public String getConnected();

    /**
     * The desired state which is transitional
     *
     * @return A Map of state parameters which have been requested to be applied to a device
     */
    public Map<String, String> getDesiredState();

    /**
     * Generic top level property access
     *
     * @param property The name of the top level property required
     * @return The associated value of the property selected.
     */
    public String getProperty(String property);

    /**
     * The current state of the device
     *
     * @return A Map of state parameters and values for the device
     */
    public Map<String, String> getCurrentState();

    /**
     * The state of the device
     *
     * @return A Map of key and value of the state
     */
    public Map<String, String> getMap();
}