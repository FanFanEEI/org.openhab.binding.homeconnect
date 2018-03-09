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
 * HomeconnectThingHandler uses this enum to describe devices that it handles.
 *
 * @author Shawn Crosby
 *         adapted to Home Connect:
 * @author Stefan Foydl (Institute for Factory Automation and Production Systems Friedrich-Alexander-University
 *         Erlangen-Nürnberg)
 */

public enum HomeconnectSupportedDevice {
    OVEN("oven"),
    DISHWASHER("dishwasher"),
    FRIDGEFREEZER("fridgefreezer"),
    WASHER("washer"),
    DRYER("dryer"),
    COFFEEMAKER("coffeemaker"),
    COOKTOP("cooktop"),
    HOOD("hood");

    private String device_type;

    HomeconnectSupportedDevice(String device_type) {
        this.device_type = device_type;
    }

    /**
     * Retrieves the object_type defined by the homeconnect api
     *
     * @return
     */

    public String getDeviceType() {
        String ret = this.device_type;
        return ret;
    }

    /**
     * Looks up the appropriate enum value based on the object_type defined by the wink api
     *
     * @param device_type
     * @return
     */
    public static HomeconnectSupportedDevice lookup(String device_type) {
        for (HomeconnectSupportedDevice dType : values()) {
            if (dType.getDeviceType().equals(device_type)) {
                return dType;
            }
        }
        return null;
    }
}