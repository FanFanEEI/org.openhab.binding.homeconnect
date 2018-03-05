/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.client;

import static org.openhab.binding.homeconnect.homeconnectBindingConstants.HOMECONNECT_URI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This implementation communicates with the homeconnect rest api.
 *
 * @author Shawn Crosby
 *         adapted to homeconnect: Stefan Foydl
 *
 *
 */
public class CloudRestfulHomeconnectClient implements IHomeconnectClient {

    private final Logger log = LoggerFactory.getLogger(CloudRestfulHomeconnectClient.class);

    @Override
    public List<IHomeconnectDevice> listDevices() {
        log.debug("Getting all devices for user");
        List<IHomeconnectDevice> ret = new ArrayList<IHomeconnectDevice>();

        Client homeconnectClient = ClientBuilder.newClient();
        WebTarget target = homeconnectClient.target(HOMECONNECT_URI).path("/api/homeappliances");
        JsonObject response = (JsonObject) executeGet(target);
        JsonElement resultJson = response.get("homeappliances");
        log.debug("resultJson = {}", resultJson);
        Iterator<JsonElement> iterator = resultJson.getAsJsonArray().iterator();

        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            if (!element.isJsonObject()) {
                continue;
            }
            ret.add(new JsonHomeconnectDevice(element.getAsJsonObject()));
        }
        homeconnectClient.close();

        return ret;
    }

    @Override
    public IHomeconnectDevice getDevice(HomeconnectSupportedDevice type, String Id) {
        log.debug("Getting Device: {}", Id);
        Client homeconnectClient = ClientBuilder.newClient();
        WebTarget target = homeconnectClient.target(HOMECONNECT_URI).path("api/homeappliances/" + Id);
        JsonElement resultJson = executeGet(target);
        log.debug("resultJson {}", resultJson);
        IHomeconnectDevice ret = new JsonHomeconnectDevice(resultJson.getAsJsonObject());

        homeconnectClient.close();

        return ret;
    }

    @Override
    public IHomeconnectDevice getDeviceState(IHomeconnectDevice device, String statuskey) {
        /*
         * Idea: first check which device it is --> device.getDeviceType()
         * Then call all the stati for this device --> see Home Connect docs
         */
        String Id = device.getId();
        log.debug("Getting Device State: {}", Id);
        Client homeconnectClient = ClientBuilder.newClient();
        WebTarget target = homeconnectClient.target(HOMECONNECT_URI)
                .path("api/homeappliances/" + Id + "/status/" + statuskey);
        JsonElement resultJson = executeGet(target);
        log.debug("getStatus: resultJson {}", resultJson);
        IHomeconnectDevice ret = new JsonHomeconnectDevice(resultJson.getAsJsonObject());
        homeconnectClient.close();

        return ret;
    }

    @Override
    // TODO: Bedeutung von desired_state herausfinden und anpassen --> wahrscheinlich erledigt!
    // desired_state entspricht dem "gewünschten" Status. --> "setting" in homeconnect
    // Dazu muss aber die Funktion komplett umgebaut werden, da bei homeconnect auch die ID benötigt wird!
    // Benötigt werden: ID & Status
    // Beispieladresse:
    // https://developer.home-connect.com/api/homeappliances/BOSCH-HNG6764B6-0000000011FF/settings/SH.Common.EnumType.PowerState.On
    public IHomeconnectDevice updateDeviceState(IHomeconnectDevice device, Map<String, String> setting) {
        Client homeconnectClient = ClientBuilder.newClient();
        WebTarget target = homeconnectClient.target(HOMECONNECT_URI)
                .path("/api/homeappliances" + "/" + device.getId() + "/settings/" + setting.get("value"));
        String desiredState = new Gson().toJson(setting);
        String wrapper = "{\"data\":{" + desiredState + "}}";
        JsonElement jsonResult = executePut(target, wrapper);

        IHomeconnectDevice ret = new JsonHomeconnectDevice(jsonResult.getAsJsonObject());
        homeconnectClient.close();

        return ret;
    }

    @Override
    public IHomeconnectDevice getDeviceSetting(IHomeconnectDevice device, String settingkey) {
        /*
         * Idea: first check which device it is --> device.getDeviceType()
         * Then call all the settings for this device --> see Home Connect docs
         */
        String Id = device.getId();
        log.debug("Getting Device Setting: {}", Id);
        Client homeconnectClient = ClientBuilder.newClient();
        WebTarget target = homeconnectClient.target(HOMECONNECT_URI)
                .path("api/homeappliances/" + Id + "/settings/" + settingkey);
        JsonElement resultJson = executeGet(target);
        log.debug("getStatus: resultJson {}", resultJson);
        IHomeconnectDevice ret = new JsonHomeconnectDevice(resultJson.getAsJsonObject());
        homeconnectClient.close();

        return ret;
    }

    private JsonElement executePut(WebTarget target, String payload) {
        String token = HomeconnectAuthenticationService.getInstance().getAuthToken();

        Response response = doPut(target, payload, token);

        if (response.getStatus() != 200) {
            log.debug("Got status {}, retrying with new token", response.getStatus());
            token = HomeconnectAuthenticationService.getInstance().refreshToken();
            response = doPut(target, payload, token);
        }

        return getResultAsJson(response);
    }

    private JsonElement executeGet(WebTarget target) {
        String token = HomeconnectAuthenticationService.getInstance().getAuthToken();
        log.debug("Token = {}", token);
        Response response = doGet(target, token);
        int status = response.getStatus();
        if (response.getStatus() != 200) {
            log.debug("Got status {}, retrying with new token", response.getStatus());
            token = HomeconnectAuthenticationService.getInstance().refreshToken();
            response = doGet(target, token);
        }

        return getResultAsJson(response);
    }

    private Response doGet(WebTarget target, String token) {
        log.debug("Doing Get: {}", target);
        Response response = target.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.ACCEPT, "application/vnd.bsh.sdk.v1+json")
                .header(HttpHeaders.ACCEPT_CHARSET, "utf-8").get();
        log.debug("Doing Get: status = {}", response.getStatus());
        log.debug("Doing Get: token = {}", token);
        return response;
    }

    private Response doPut(WebTarget target, String payload, String token) {
        log.debug("Doing Put: {}, Payload: {}", target, payload);
        Response response = target.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.ACCEPT, "application/vnd.bsh.sdk.v1+json")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.bsh.sdk.v1+json").put(Entity.json(payload));
        return response;
    }

    private JsonElement getResultAsJson(Response response) {
        JsonElement ret;
        String result = response.readEntity(String.class);
        log.debug("Doing getResultAsJson: result = {}", result);
        JsonParser parser = new JsonParser();
        JsonObject resultJson = parser.parse(result).getAsJsonObject();
        log.debug("Doing getResultAsJson: resultJson = {}", resultJson);
        if (resultJson.get("data") != null) {
            ret = resultJson.get("data");
            log.trace("Json Result: {}", ret);
            log.debug("Doing getResultAsJson: ret = {}", ret);
        } else {
            JsonElement err = resultJson.get("error");
            log.debug("Error: {}", err);
            ret = null;
        }

        return ret;
    }

}