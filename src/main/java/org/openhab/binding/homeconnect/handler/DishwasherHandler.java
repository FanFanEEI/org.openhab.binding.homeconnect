/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.handler;

import static org.openhab.binding.homeconnect.homeconnectBindingConstants.CHANNEL_SWITCHSTATE_DISHWASHER;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.library.types.OnOffType;
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
 * The {@link DishwasherHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Stefan Foydl - Initial contribution
 */
public class DishwasherHandler extends HomeconnectBaseThingHandler {
    public DishwasherHandler(Thing thing) {
        super(thing);
    }

    private IHomeconnectClient client = HomeconnectClient.getInstance();
    private final Logger logger = LoggerFactory.getLogger(DishwasherHandler.class);

    @Override
    public void handleHomeconnectCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_SWITCHSTATE_DISHWASHER)) {
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
        return HomeconnectSupportedDevice.DISHWASHER;
    }

    @Override
    protected void updateDeviceState(IHomeconnectDevice device) {
        // boolean switchedState = Boolean.valueOf(device.getCurrentState().get("powered"));
        // updateState(CHANNEL_SWITCHSTATE, (switchedState ? OnOffType.ON : OnOffType.OFF));
    }

    @Override
    protected void getDeviceState(IHomeconnectDevice device) {
        /*
         * TODO Alle Statuskeys abfragen!
         * Danach alle einzelnen Statuskeys mit client.getDeviceState(device, statuskey);
         * und Ergebnisse benutzen, um die Channels/Switches zu schalten
         *
         * Benötigte Keys für Dishwasher:
         *
         */
        String statuskey = "BSH.Common.Status.DoorState";
        client.getDeviceState(device, statuskey);
        // TODO: Hier Status Schalter passend einstellen
    }

}