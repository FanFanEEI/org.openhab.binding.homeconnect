/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.handler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.homeconnect.client.AuthenticationException;
//import org.openhab.binding.homeconnect.client.AuthenticationException;
import org.openhab.binding.homeconnect.client.HomeconnectClient;
import org.openhab.binding.homeconnect.client.HomeconnectSupportedDevice;
//import org.openhab.binding.homeconnect.client.HomeconnectSupportedDevice;
import org.openhab.binding.homeconnect.client.IHomeconnectClient;
//import org.openhab.binding.homeconnect.client.IHomeconnectDevice;
import org.openhab.binding.homeconnect.client.IHomeconnectDevice;
import org.openhab.binding.homeconnect.internal.discovery.HomeconnectDeviceDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the base class for devices connected to your Home Connect account. Implements initialization for all
 * Home Connect devices.
 *
 * @author Stefan Foydl
 *
 */
public abstract class HomeconnectBaseThingHandler extends BaseThingHandler {
    public HomeconnectBaseThingHandler(Thing thing) {
        super(thing);
    }

    private final Logger logger = LoggerFactory.getLogger(HomeconnectBaseThingHandler.class);
    private IHomeconnectClient client = HomeconnectClient.getInstance();
    private String conn;
    private int position = 0;

    @Override
    public void initialize() {
        logger.debug("Initializing Device {}", getThing());
        HomeconnectDeviceDiscoveryService discovery = new HomeconnectDeviceDiscoveryService(this);

        this.bundleContext.registerService(DiscoveryService.class, discovery, null);
        /*
         * this.scheduler.schedule(new Runnable() {
         *
         * @Override
         * public void run() {
         * conn = getDevice().getConnected();
         * if (conn.equals("true")) {
         * updateStatus(ThingStatus.ONLINE);
         * getDeviceState(getDevice());
         * getDeviceSetting(getDevice());
         * } else if (conn.equals("false")) {
         * updateStatus(ThingStatus.OFFLINE);
         * }
         * }
         * }, 30, TimeUnit.SECONDS);
         */
        this.scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                conn = getDevice().getConnected();
                if (conn.equals("true")) {
                    updateStatus(ThingStatus.ONLINE);
                    getDeviceState(getDevice());
                    getDeviceSetting(getDevice());
                } else if (conn.equals("false")) {
                    updateStatus(ThingStatus.OFFLINE);
                }
            }
        }, 30, 5, TimeUnit.SECONDS);

        if (getThing().getConfiguration().get("haId") == null) { // Wenn haId leer ist
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "haId must be specified in Config");
        } else {
            try {
                conn = getDevice().getConnected();
                logger.debug("getDevice().getCurrentState().get(\"connected\") = {}", conn);
                if (conn.equals("true")) {
                    updateStatus(ThingStatus.ONLINE);
                    // getDeviceState(getDevice());
                    // updateDeviceState(getDevice());

                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Device Not Connected");
                }
            } catch (AuthenticationException e) {
                logger.error("Unable to initialize device: {}", e.getMessage());
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            } catch (RuntimeException e) {
                logger.error("Unable to initialize device: {}", e.getMessage());
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }
        }
        super.initialize();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            handleHomeconnectCommand(channelUID, command);
        } catch (RuntimeException e) {
            logger.error("Unable to process command: {}", e.getMessage());
        }
    }

    /**
     * Sub-implementation of ThingHandler handleCommand to deal with exception handling more cleanly
     *
     * @param channelUID
     * @param command
     */
    protected abstract void handleHomeconnectCommand(ChannelUID channelUID, Command command);

    @Override
    public void channelLinked(ChannelUID channelUID) {
        List<Channel> channellist = getThing().getChannels();
        int size = channellist.size();
        try {
            for (Channel channel : channellist) {
                if (channelUID.equals(channel.getUID())) {
                    // updateDeviceState(getDevice());
                    // do nothing
                    logger.debug("Channel {} Linked", channelUID.getId());
                    position++;
                    // break;
                }
            }
        } catch (AuthenticationException e) {
            logger.error("Unable to process channel link: {}", e.getMessage());
        }
        if (conn.equals("true") && position == size) {
            getDeviceState(getDevice());
            getDeviceSetting(getDevice());
        } else if (conn.equals("false") && position == size) {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    /**
     * Subclasses must define the correct Home Connect supported device type
     *
     * @return Enum from HomeconnectSupportedDevice for this device
     */
    protected abstract HomeconnectSupportedDevice getDeviceType();

    /**
     * Retrieves the device configuration from the API
     *
     * @return
     */
    protected IHomeconnectDevice getDevice() {

        /**
         * Retrieve a specified device from the Home Connect API
         *
         * @param deviceType The type of device to retrieve
         * @param haId The unique identifier for the device
         * @return The device
         */
        logger.debug("Getting device through handler {}", getThing().getConfiguration().get("haId").toString());
        HomeconnectSupportedDevice devtype = getDeviceType();
        logger.debug("getDeviceType() = {}", devtype);
        // return Daten des client.getdevice(): deviceType & haId als String
        return client.getDevice(getDeviceType(), getThing().getConfiguration().get("haId").toString());
    }

    /**
     * Subclasses must implement this method to perform the mapping between the properties and state
     * retrieved from the API and how that state is represented in OpenHab.
     *
     * @param device
     */
    protected abstract void updateDeviceState(IHomeconnectDevice device);

    /**
     * Handles state change events from the api
     */
    protected abstract void getDeviceState(IHomeconnectDevice device);

    /**
     * Reads settings from the api
     */
    protected void getDeviceSetting(IHomeconnectDevice device) {

    }

}