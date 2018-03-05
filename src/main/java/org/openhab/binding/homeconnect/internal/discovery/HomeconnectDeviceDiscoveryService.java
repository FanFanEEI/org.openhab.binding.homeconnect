/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.internal.discovery;

import static org.openhab.binding.homeconnect.homeconnectBindingConstants.BINDING_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.homeconnect.client.HomeconnectClient;
import org.openhab.binding.homeconnect.client.IHomeconnectDevice;
import org.openhab.binding.homeconnect.handler.HomeconnectBaseThingHandler;
import org.openhab.binding.homeconnect.internal.HomeconnectHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to discover new devices associated with the Home Connect account
 *
 * @author Sebastian Marchand
 *         adopted to Home Connect:
 * @author Stefan Foydl
 */
public class HomeconnectDeviceDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(HomeconnectDeviceDiscoveryService.class);
    private HomeconnectBaseThingHandler baseHandler;

    public HomeconnectDeviceDiscoveryService(HomeconnectBaseThingHandler baseHandler) throws IllegalArgumentException {
        super(HomeconnectHandlerFactory.DISCOVERABLE_DEVICE_TYPES_UIDS, 10);

        this.baseHandler = baseHandler;
    }

    private ScheduledFuture<?> scanTask;

    @Override
    protected void startScan() {
        logger.debug("Starting Homeconnect Discovery Scan");
        if (this.scanTask == null || this.scanTask.isDone()) {
            this.scanTask = scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    List<IHomeconnectDevice> devices = HomeconnectClient.getInstance().listDevices();
                    logger.debug("Found {} connected devices", devices.size());
                    // ThingUID baseThingId = baseHandler.getThing().getUID();
                    for (IHomeconnectDevice device : devices) {
                        // HomeconnectSupportedDevice type = device.getDeviceType();
                        logger.debug("Creating Discovery result {}", device);
                        ThingUID thingId = new ThingUID(
                                new ThingTypeUID(BINDING_ID, device.getDeviceType().getDeviceType()), device.getId());
                        Map<String, Object> props = new HashMap<String, Object>();
                        props.put("haId", device.getId());

                        DiscoveryResult result = DiscoveryResultBuilder.create(thingId).withLabel(device.getName())
                                .withProperties(props).build();
                        thingDiscovered(result);
                        logger.debug("Discovered Thing: {}", thingId);
                    }
                }
            }, 0, TimeUnit.SECONDS);
        }
    }

}