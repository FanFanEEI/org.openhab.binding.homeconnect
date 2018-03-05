/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link HomeconnectBinding} class defines common constants, which are used across the whole binding.
 *
 * @author Stefan Foydl - Initial contribution
 */
public class homeconnectBindingConstants {

    public static final String BINDING_ID = "homeconnect";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_OVEN = new ThingTypeUID(BINDING_ID, "oven");
    public static final ThingTypeUID THING_TYPE_DISHWASHER = new ThingTypeUID(BINDING_ID, "dishwasher");
    public static final ThingTypeUID THING_TYPE_FRIDGEFREEZER = new ThingTypeUID(BINDING_ID, "fridgefreezer");
    public static final ThingTypeUID THING_TYPE_WASHER = new ThingTypeUID(BINDING_ID, "washer");
    public static final ThingTypeUID THING_TYPE_DRYER = new ThingTypeUID(BINDING_ID, "dryer");
    public static final ThingTypeUID THING_TYPE_COFFEEMAKER = new ThingTypeUID(BINDING_ID, "coffeemaker");
    public static final ThingTypeUID THING_TYPE_COOKTOP = new ThingTypeUID(BINDING_ID, "cooktop");
    public static final ThingTypeUID THING_TYPE_HOOD = new ThingTypeUID(BINDING_ID, "hood");

    // List of all Channel ids for an oven
    public static final String CHANNEL_STATUS_OVEN_REMOTECONTROLACTIVATION = "status_oven_remotecontrolactivation";
    public static final String CHANNEL_STATUS_OVEN_REMOTESTARTALLOWANCE = "status_oven_remotestartallowance";
    public static final String CHANNEL_STATUS_OVEN_OPERATIONSTATE = "status_oven_operationstate";
    public static final String CHANNEL_STATUS_OVEN_DOORSTATE = "status_oven_doorstate";
    public static final String CHANNEL_STATUS_OVEN_CURRCAVTEMPSTATE = "status_oven_currcavtempstate";
    public static final String CHANNEL_SETTING_OVEN_POWERSTATE = "setting_oven_powerstate";

    // List of all Channel ids for an dishwasher
    public static final String CHANNEL_SWITCHSTATE_DISHWASHER = "switchstate_dishwasher";

    // REST URI constants
    public static final String HOMECONNECT_URI = "https://simulator.home-connect.com/";
    public static final String HOMECONNECT_DEVICES_REQUEST_PATH = "api/homeappliances";
    public static final String REDIRECT_URI = "https://apiclient.home-connect.com/o2c.html";
    public static final String HOMECONNECT_ACCESS_TOKEN = "access_token";
    public static final String HOMECONNECT_REFRESH_TOKEN = "refresh_token";
}