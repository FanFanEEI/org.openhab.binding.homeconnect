/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * This class parses json from the Home Connect API IHomeconnectDevice
 *
 * @author Shawn Crosby (for WINK)
 *         adapted to Home Connect: Stefan Foydl
 *
 */
public class JsonHomeconnectDevice implements IHomeconnectDevice {
    private JsonObject json;

    public JsonHomeconnectDevice(JsonObject element) {
        this.json = element;
    }

    @Override
    public String getId() {
        return json.get("haId").getAsString();
    }

    @Override
    public String getName() {
        return json.get("name").getAsString();
    }

    @Override
    public Map<String, String> getMap() {
        return toMap(this.json);
    }

    @Override
    public HomeconnectSupportedDevice getDeviceType() {
        if (json.get("type").getAsString().equals("Oven")) {
            return HomeconnectSupportedDevice.OVEN;
        } else if (json.get("type").getAsString().equals("Dishwasher")) {
            return HomeconnectSupportedDevice.DISHWASHER;
        } else if (json.get("type").getAsString().equals("FridgeFreezer")) {
            return HomeconnectSupportedDevice.FRIDGEFREEZER;
        } else if (json.get("type").getAsString().equals("Washer")) {
            return HomeconnectSupportedDevice.WASHER;
        } else if (json.get("type").getAsString().equals("Dryer")) {
            return HomeconnectSupportedDevice.DRYER;
        } else if (json.get("type").getAsString().equals("Coffeemaker")) {
            return HomeconnectSupportedDevice.COFFEEMAKER;
        } else if (json.get("type").getAsString().equals("Cooktop")) {
            return HomeconnectSupportedDevice.COOKTOP;
        } else {
            return HomeconnectSupportedDevice.HOOD;
        }
    }

    @Override
    public String getProperty(String property) {
        return json.get(property).getAsString();
    }

    @Override
    public String getConnected() {
        String data = json.get("connected").getAsString();
        return data;
    }

    @Override
    public Map<String, String> getDesiredState() {
        // original line of the wink binding:
        // JsonObject data = json.get("desired_state").getAsJsonObject();
        JsonObject data = null;
        return toMap(data);
    }

    private Map<String, String> toMap(JsonObject json) {
        return new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {
        }.getType());
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append(this.getDeviceType() + " ");
        ret.append("Device: (" + this.getId() + ") ");
        ret.append(this.getName());

        return ret.toString();
    }

    // TODO: An homeconnect anpassen --> evtl. in noch zu erschaffendes IHomeconnctDeviceState Objekt verschieben.
    @Override
    public Map<String, String> getCurrentState() {
        JsonObject data = json.get("desired_state").getAsJsonObject();
        return toMap(data);
    }

}