
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This HomeconnectAuthenticationService requires that the user receives his own
 * API Key at his Home Connect developer account.
 *
 * In order to work, you must put a homeconnect.cfg file in your $openhab/conf/services folder with the
 * following parameters defined
 *
 * ---homeconnect.cfg---
 * client_id = homeconnect_api_key
 * redirect_uri = redirect_uri
 * code = redirect_authorization_code (evtl. gar nicht nortwendig!?)
 * refresh_token = refresh_token (evtl. gar nicht notwendig!?)
 * ---end---
 *
 * @author Shawn Crosby (sacrosby@gmail.com)
 *         adapted to homeconnect:
 * @author Stefan Foydl (Institute for Factory Automation and Production Systems Friedrich-Alexander-University
 *         Erlangen-Nürnberg)
 *
 */
public class CloudOauthHomeconnectAuthenticationService implements IHomeconnectAuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(CloudOauthHomeconnectAuthenticationService.class);

    private String token;
    private String client_id;
    private String scope;
    private String code;
    private String refresh_token;
    private String state;
    private String redirect_uri;

    public CloudOauthHomeconnectAuthenticationService(Map<String, String> properties) {
        client_id = properties.get("client_id");
        scope = properties.get("scope");
        redirect_uri = properties.get("redirect_uri");

        ClientConfig configuration = new ClientConfig();
        configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 1000 * 15);
        configuration = configuration.property(ClientProperties.READ_TIMEOUT, 1000 * 15);
        Client client = ClientBuilder.newBuilder().withConfig(configuration).build();

        // build and send authorization link
        // The authorization link has the following pattern:
        // https://simulator.home-connect.com/security/oauth/authorize?response_type=code&redirect_uri=YYYYYYYYYYYYY&client_id=XXXXXXXXXXXXXX&scope=IdentifyAppliance&state=homeconnect_auth

        String path = HOMECONNECT_URI + "security/oauth/authorize?response_type=code&redirect_uri=" + redirect_uri
                + "&client_id=" + client_id + "&scope=" + scope + "&state=homeconnect_auth";

        // String path = HOMECONNECT_URI + "security/oauth/authorize?response_type=code&redirect_uri=" + REDIRECT_URI
        // + "&client_id=" + client_id + "&scope=" + scope + "&state=homeconnect_auth";
        /*
         * // open browser
         * // Funktioniert anscheinend nicht in jedem System!
         * try {
         * Desktop.getDesktop().browse(new URI("http://192.168.178.27:8080/paperui/index.html#/configuration/things"));
         * } catch (IOException e) {
         * e.printStackTrace();
         * } catch (URISyntaxException e) {
         * e.printStackTrace();
         * }
         */

        // TODO: Hier eigentlich auf Benutzeringabe warten bzw. gleichbedeutend mit einer automatischen Änderung der URL
        // zur redirect_uri

        URLConnection con = null;
        try {
            con = new URL(path).openConnection();
            con.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        java.io.InputStream is = null;

        try {
            is = con.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = con.getURL().toString();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read code and state from redirected URL
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
            for (NameValuePair param : params) {
                String name = param.getName();
                // String value = param.getValue();
                if (name.equals("code")) {
                    code = param.getValue();
                } else if (name.equals("state")) {
                    state = param.getValue();
                }
                logger.debug("Name {}", name);
                logger.debug("Wert {}", param.getValue());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Form payload = new Form(); // "Matrix" fuer Sendedaten anlegen
        // Matrix beschreiben mit den zu sendenden Daten. (Vgl. homeconnect Dokumentation)
        payload.param("client_id", client_id);
        // payload.param("redirect_uri", REDIRECT_URI);
        payload.param("redirect_uri", redirect_uri);
        payload.param("grant_type", "authorization_code");
        payload.param("code", code);
        // payload.param("state", "xxx");
        /*
         * WebTarget tokenPath = client.target(
         * "https://developer.home-connect.com/security/oauth/token?client_id=CFD3410FDCE6FA8DD8041B0F495AF9F2A3513907D59A08538340E57C25C8E886&redirect_uri=https://apiclient.home-connect.com/o2c.html&grant_type=authorization_code&code="
         * + code);
         */
        String token_path = HOMECONNECT_URI + "security/oauth/token";
        WebTarget tokenPath = client.target(token_path);
        Response response = tokenPath.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.form(payload));
        logger.debug("Access Token status: {}" + response.getStatus());

        JsonObject json = getResultAsJson(response);
        logger.debug("Access Token Response: {}", json);
        if (json.getAsJsonObject().get("access_token") != null) {
            token = json.getAsJsonObject().get("access_token").getAsString();
            refresh_token = json.getAsJsonObject().get("refresh_token").getAsString();
        }
        client.close();

    }

    @Override
    public String getAuthToken() {
        logger.debug("getAuthToken() for client id {}", client_id);
        logger.debug("getAuthToken(): TOKEN = {}", token);
        return token;
    }

    @Override
    public String refreshToken() throws AuthenticationException {
        logger.debug("Refreshing token for client id {}", client_id);
        Client homeconnectClient = ClientBuilder.newClient();
        WebTarget tokenTarget = homeconnectClient.target(HOMECONNECT_URI).path("security/oauth/token");

        Form payload = new Form(); // "Matrix" fuer Sendedaten anlegen
        // Matrix beschreiben mit den zu sendenden Daten. (Vgl. homeconnect Dokumentation)
        payload.param("grant_type", "refresh_token");
        payload.param("refresh_token", refresh_token);

        /*
         * //payload.put("client_id", clientId);
         * //payload.put("redirect_uri", redirect_uri);
         * payload.put("grant_type", "refresh_token");
         * payload.put("code", code);
         */

        Response response = tokenTarget.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.form(payload));
        JsonObject responseJson = getResultAsJson(response);

        if (response.getStatus() == 200) {
            token = responseJson.getAsJsonObject().get("access_token").getAsString();
            logger.debug("New Access Token: {}", token);
        } else {
            logger.debug("Got status: {} refreshing token", response.getStatus());
            logger.trace("Error Response: {}", responseJson.get("errors").getAsString());
            throw new AuthenticationException("Invalid refresh token or app key and secret");
        }

        homeconnectClient.close();
        return token;
    }

    private JsonObject getResultAsJson(Response response) {
        String result = response.readEntity(String.class);
        logger.debug("getResultAsJson: result = {}", result);
        JsonParser parser = new JsonParser();
        JsonObject resultJson = parser.parse(result).getAsJsonObject();
        return resultJson;
    }
}