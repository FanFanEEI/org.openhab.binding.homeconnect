/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.handler;

import static org.openhab.binding.homeconnect.homeconnectBindingConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.homeconnect.client.AuthenticationException;
import org.openhab.binding.homeconnect.client.HomeconnectClient;
import org.openhab.binding.homeconnect.client.HomeconnectSupportedDevice;
import org.openhab.binding.homeconnect.client.IHomeconnectClient;
import org.openhab.binding.homeconnect.client.IHomeconnectDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OvenHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Stefan Foydl - Initial contribution
 */
public class OvenHandler extends HomeconnectBaseThingHandler {
    public OvenHandler(Thing thing) {
        super(thing);
    }

    private IHomeconnectClient client = HomeconnectClient.getInstance();
    private final Logger logger = LoggerFactory.getLogger(OvenHandler.class);

    @Override
    public void handleHomeconnectCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_STATUS_OVEN_DOORSTATE)) {
            if (command.equals(OnOffType.ON)) {
                setSwitchState(true);
            } else if (command.equals(OnOffType.OFF)) {
                setSwitchState(false);
            } else if (command instanceof RefreshType) {
                logger.debug("Refreshing state");
                updateDeviceState(getDevice());
            }
        }
    }

    public void setDesiredState(IHomeconnectDevice device, Map<String, String> updatedState) {
        logger.debug("Setting device state: {}", updatedState);
        try {
            client.updateDeviceState(device, updatedState);
        } catch (AuthenticationException e) {
            logger.error("Unable to communicate with homeconnect api: {}", e.getMessage());
        }
    }

    private void setSwitchState(boolean state) {
        IHomeconnectDevice device = getDevice();
        if (state) {
            logger.debug("Switching on Device {}", device);
            // Map<key, value> --> Key ist der Identifier, Value dessen Wert
            Map<String, String> updatedState = new HashMap<String, String>();
            updatedState.put("key", "BSH.Common.Setting.PowerState");
            updatedState.put("value", "BSH.Common.EnumType.PowerState.On");
            this.setDesiredState(device, updatedState);
        } else {
            Map<String, String> updatedState = new HashMap<String, String>();
            updatedState.put("key", "BSH.Common.Setting.PowerState");
            updatedState.put("value", "BSH.Common.EnumType.PowerState.Off");
            this.setDesiredState(device, updatedState);
        }
    }

    @Override
    protected HomeconnectSupportedDevice getDeviceType() {
        return HomeconnectSupportedDevice.OVEN;
    }

    @Override
    protected void updateDeviceState(IHomeconnectDevice device) {
        boolean switchedState = Boolean.valueOf(device.getCurrentState().get("powered"));
        updateState(CHANNEL_STATUS_OVEN_DOORSTATE, (switchedState ? OnOffType.ON : OnOffType.OFF));
    }

    @Override
    protected void getDeviceState(IHomeconnectDevice device) {
        /*
         * Needed Keys for OVEN:
         * BSH.Common.Status.RemoteControlActive
         * BSH.Common.Status.RemoteControlStartAllowed
         * BSH.Common.Status.OperationState
         * BSH.Common.Status.DoorState
         * Cooking.Oven.Status.CurrentCavityTemperature
         */
        /*
         * try {
         * Thread.sleep(2000);
         * } catch (InterruptedException e) {
         * }
         */
        // Remote control active --> BSH.Common.Status.RemoteControlActive
        String statuskey = "BSH.Common.Status.RemoteControlActive";
        IHomeconnectDevice rcactive_status = client.getDeviceState(device, statuskey);
        Map<String, String> rcactive_map = rcactive_status.getMap();
        String rcactivestate = rcactive_map.get("value");
        if (rcactivestate.equals("true")) {
            updateState(CHANNEL_STATUS_OVEN_REMOTECONTROLACTIVATION, OnOffType.ON);
        } else if (rcactivestate.equals("false")) {
            updateState(CHANNEL_STATUS_OVEN_REMOTECONTROLACTIVATION, OnOffType.OFF);
        } else {
            logger.debug("Unknown status RemoteControlActive Oven:{}", rcactivestate);
        }
        // logger.debug("Stopp");

        // Remote control allowed --> BSH.Common.Status.RemoteControlStartAllowed
        statuskey = "BSH.Common.Status.RemoteControlStartAllowed";
        IHomeconnectDevice rcsallowed_status = client.getDeviceState(device, statuskey);
        Map<String, String> rcsallowed_map = rcsallowed_status.getMap();
        String rcsallowedstate = rcsallowed_map.get("value");
        if (rcsallowedstate.equals("true")) {
            updateState(CHANNEL_STATUS_OVEN_REMOTESTARTALLOWANCE, OnOffType.ON);
        } else if (rcsallowedstate.equals("false")) {
            updateState(CHANNEL_STATUS_OVEN_REMOTESTARTALLOWANCE, OnOffType.OFF);
        } else {
            logger.debug("Unknown status RemoteControlActive Oven:{}", rcsallowedstate);
        }
        // logger.debug("Stopp");

        /*
         * Not used any more!?
         * // Local control active --> BSH.Common.Status.LocalControlActive
         * statuskey = "BSH.Common.Status.LocalControlActive";
         * IHomeconnectDevice localactive_status = client.getDeviceState(device, statuskey);
         * Map<String, String> localactive_map = localactive_status.getMap();
         * String localactivestate = localactive_map.get("value");
         * if (localactivestate.equals("true")) {
         * updateState(CHANNEL_STATUS_OVEN_LOCALCONTROL, OnOffType.ON);
         * } else if (localactivestate.equals("false")) {
         * updateState(CHANNEL_STATUS_OVEN_LOCALCONTROL, OnOffType.OFF);
         * } else {
         * logger.debug("Unknown status RemoteControlActive Oven:{}", localactivestate);
         * }
         * logger.debug("Stopp");
         */

        // operation state --> BSH.Common.Status.OperationState
        statuskey = "BSH.Common.Status.OperationState";
        IHomeconnectDevice operation_status = client.getDeviceState(device, statuskey);
        Map<String, String> operation_map = operation_status.getMap();
        String operationstate = operation_map.get("value");
        String operationString = "";
        if (operationstate.equals("BSH.Common.EnumType.OperationState.Inactive")) {
            operationString = "Inactive";
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.Ready")) {
            operationString = "Ready";
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.DelayedStart")) {
            operationString = "Delayed Start";
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.Run")) {
            operationString = "Running";
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.Pause")) {
            operationString = "Paused";
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.ActionRequired")) {
            operationString = "Action required!";
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.Finished")) {
            operationString = "Finished";
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.Error")) {
            operationString = "Error";
            logger.debug("Oven Error!");
        } else if (operationstate.equals("BSH.Common.EnumType.OperationState.Aborting")) {
            operationString = "Aborting";
        } else {
            logger.debug("Unknown operationstate Oven:{}", operationstate);
            operationString = "unknown";
        }
        updateState(CHANNEL_STATUS_OVEN_OPERATIONSTATE, new StringType(operationString));
        // logger.debug("Stopp");

        // door state --> BSH.Common.Status.DoorState
        statuskey = "BSH.Common.Status.DoorState";
        IHomeconnectDevice door_status = client.getDeviceState(device, statuskey);
        Map<String, String> status_map = door_status.getMap();
        String doorstate = status_map.get("value");
        if (doorstate.equals("BSH.Common.EnumType.DoorState.Open")) {
            updateState(CHANNEL_STATUS_OVEN_DOORSTATE, OpenClosedType.OPEN);
        } else if (doorstate.equals("BSH.Common.EnumType.DoorState.Closed")) {
            updateState(CHANNEL_STATUS_OVEN_DOORSTATE, OpenClosedType.CLOSED);
        } else if (doorstate.equals("BSH.Common.EnumType.DoorState.Locked")) {
            updateState(CHANNEL_STATUS_OVEN_DOORSTATE, OpenClosedType.CLOSED);
        } else {
            logger.debug("Unknown doorstate Oven:{}", doorstate);
        }
        // logger.debug("Stopp");

        // current cavity temp --> Cooking.Oven.Status.CurrentCavityTemperature
        statuskey = "Cooking.Oven.Status.CurrentCavityTemperature";
        IHomeconnectDevice currentCavTemp_status = client.getDeviceState(device, statuskey);
        Map<String, String> currCavTemp_map = currentCavTemp_status.getMap();
        String currCavTempvalue = currCavTemp_map.get("value");
        String currCavTempunit = currCavTemp_map.get("unit");
        String currCavTempString = currCavTempvalue + " " + currCavTempunit;
        updateState(CHANNEL_STATUS_OVEN_CURRCAVTEMPSTATE, new StringType(currCavTempString));
        // logger.debug("Stopp");
    }

    @Override
    protected void getDeviceSetting(IHomeconnectDevice device) {
        /*
         * Needed Keys for OVEN:
         * BSH.Common.Setting.PowerState
         */
        /*
         * try {
         * Thread.sleep(2000);
         * } catch (InterruptedException e) {
         * }
         */
        // Power State --> BSH.Common.Setting.PowerState
        String settingkey = "BSH.Common.Setting.PowerState";
        IHomeconnectDevice powerstate_setting = client.getDeviceSetting(device, settingkey);
        Map<String, String> powerstate_map = powerstate_setting.getMap();
        String powerstate = powerstate_map.get("value");
        if (powerstate.equals("BSH.Common.EnumType.PowerState.Off")) {
            updateState(CHANNEL_SETTING_OVEN_POWERSTATE, OnOffType.OFF);
        } else if (powerstate.equals("BSH.Common.EnumType.PowerState.On")) {
            updateState(CHANNEL_SETTING_OVEN_POWERSTATE, OnOffType.ON);
        } else if (powerstate.equals("BSH.Common.EnumType.PowerState.Standby")) {
            updateState(CHANNEL_SETTING_OVEN_POWERSTATE, OnOffType.OFF);
        } else {
            logger.debug("Unknown setting PowerState Oven:{}", powerstate);
        }
        logger.debug("Stopp");
    }

}