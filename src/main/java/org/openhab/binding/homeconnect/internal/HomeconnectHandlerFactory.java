/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homeconnect.internal;

import static org.openhab.binding.homeconnect.homeconnectBindingConstants.*;

import java.util.Set;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.homeconnect.handler.DishwasherHandler;
import org.openhab.binding.homeconnect.handler.OvenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link HomeconnectHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Stefan Foydl - Initial contribution
 */
public class HomeconnectHandlerFactory extends BaseThingHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(HomeconnectHandlerFactory.class);

    public static final Set<ThingTypeUID> DISCOVERABLE_DEVICE_TYPES_UIDS = ImmutableSet.of(THING_TYPE_OVEN,
            THING_TYPE_DISHWASHER, THING_TYPE_FRIDGEFREEZER, THING_TYPE_WASHER, THING_TYPE_DRYER,
            THING_TYPE_COFFEEMAKER, THING_TYPE_COOKTOP, THING_TYPE_HOOD);

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_OVEN,
            THING_TYPE_DISHWASHER, THING_TYPE_FRIDGEFREEZER, THING_TYPE_WASHER, THING_TYPE_DRYER,
            THING_TYPE_COFFEEMAKER, THING_TYPE_COOKTOP, THING_TYPE_HOOD);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        logger.debug("Checking if the factory supports {}", thingTypeUID.toString());

        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)
                || DISCOVERABLE_DEVICE_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_OVEN)) {
            return new OvenHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_DISHWASHER)) {
            return new DishwasherHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_FRIDGEFREEZER)) {
            // return new FridgefreezerHandler(thing);
            return null;
        } else if (thingTypeUID.equals(THING_TYPE_WASHER)) {
            // return new WasherHandler(thing);
            return null;
        } else if (thingTypeUID.equals(THING_TYPE_DRYER)) {
            // return new DryerHandler(thing);
            return null;
        } else if (thingTypeUID.equals(THING_TYPE_COFFEEMAKER)) {
            // return new CoffeemakerHandler(thing);
            return null;
        } else if (thingTypeUID.equals(THING_TYPE_COOKTOP)) {
            // return new CoffeemakerHandler(thing);
            return null;
        } else if (thingTypeUID.equals(THING_TYPE_HOOD)) {
            // return new CoffeemakerHandler(thing);
            return null;
        }

        return null;
    }
}