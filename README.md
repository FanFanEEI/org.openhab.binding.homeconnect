# Home Connect Binding

This binding supports household devices with Home Connect service of the BSH group.
For more details visit http://www.home-connect.com/global

At time of writing this binding supports the coffee machine, dishwasher, dryer, ridgefreezer combination, oven and washer.

## Overview

The Home Connect binding represents "Devices" as things connected to the Home Connect Server.

Since the binding works only with the Home Connect API, in order to use it you must obtain a Home Connect API key.

To obtain a API key You need an developer account at the Home Connect developer portal: https://developer.home-connect.com/
Your account should have at least the level advanced!

### Devices

When devices are connected to the Home Connect Server they become discoverable by the binding through the Home Connect API.

Different devices will support different channels depending on the capabilities of the device.

## Supported Things

* OVEN
* DISHWASHER
* FRIDGEFREEZER
* WASHER
* DRYER
* COFFEEMAKER

## Discovery

You can search automatically for Home Connect Devices from the Inbox and the binding will add all devices connected to your Home Connect account to the inbox.

## Binding Configuration

In order for the binding to work, you must add a homeconnect.cfg file (must be named homeconnect.cfg) to the services directory. This will allow the binding to obtain a new access token whenever it needs one.

The file must contain four parameters:
 
```
client_id = {homeconnect_api_key}
redirect_uri = {redirect_uri}
code = redirect_authorization_code
refresh_token = refresh_token
```
{homeconnect_api_key} has to be replaced with your API Key and {redirect_uri} with the Redirect URL from your Home Connect developer account.
You can obtain these values at https://developer.home-connect.com/?q=user/my_apps
For the simulator the Redirect URL is: https://apiclient.home-connect.com/o2c.html
The last both code and refresh_token are wildcards for the values obtained automatically during the authorization flow.


## Thing Configuration

Discovery will automatically do this for you, but if you want to do it manually, you can do this at the paperUI.
Then you need choose the device type (oven, washer, etc.) and the haId of the device. To find the device pleas look in  the manual.   

## Channels

Depending on the device being configured, there are different channels available:

| Thing |               Channel               |               Description               | Visability | read only? |
|:-----:|:-----------------------------------:|:---------------------------------------:|:----------:|:----------:|
| OVEN  | status_oven_remotecontrolactivation |       Is remote control activated?      |   hidden   |    true    |
| OVEN  |  status_oven_remotecontrolallowance |        Is remote control allowed?       |   hidden   |    true    |
| OVEN  |       status_oven_localcontrol      | Is device controlled local by the user? |   hidden   |    true    |
| OVEN  |      status_oven_operationstate     |  Describes the actual operation state.  |   visable  |    true    |
| OVEN  |        status_oven_doorstate        | Describes the actual state of the door. |   visable  |    true    |


Your items file will look like:

```
Item Switch MyLightSwitch "My Light Switch"  {channel="wink:light_bulb:MyLight:lightstate"}
Item Dimmer MyLightDimmer "My Light Dimmer"  {channel="wink:light_bulb:MyLight:lightlevel"}
```

## For Developers

The directory src/main/java/org/openhab/binding/homeconnect contains three folders and the file homeconnectBindingConstants.java

In this file all the global constants are stored:
```
// REST URI constants
    public static final String HOMECONNECT_URI = "https://simulator.home-connect.com/";
    public static final String HOMECONNECT_DEVICES_REQUEST_PATH = "api/homeappliances";
    public static final String REDIRECT_URI = "https://apiclient.home-connect.com/o2c.html";
    public static final String HOMECONNECT_ACCESS_TOKEN = "access_token";
    public static final String HOMECONNECT_REFRESH_TOKEN = "refresh_token";
```
Furthermore there are the ThingTypeUIDs, which should look like:
```
public static final ThingTypeUID THING_TYPE_OVEN = new ThingTypeUID(BINDING_ID, "oven");
```
...and the list of all the channels. For example:
```
 public static final String CHANNEL_EXAMPLE = "example";
```

# directory internal

|                   File                  |                       Description                       |
|:---------------------------------------:|:-------------------------------------------------------:|
| homeconnectHandlerFactory.java          |       Creates the several handlers for each thing       |
| AuthenticationConfigurationService.java | Reads homeconnect.cfg and starts authentication service |
| **directory**                           |                      **discovery**                      |
| HomeconnectDeviceDiscoveryService.java  |       Starts a scan service to auto detect devices      |

# directory handler

|                  File                  |                                               Description                                               |
|:--------------------------------------:|:-------------------------------------------------------------------------------------------------------:|
| HomeconnectBaseThingHandler.java       |                               Creates the several handlers for each thing                               |
| *Device*Handler.java                   | Special handler for each device. The *Device* is replaced with the device type (e. g. OvenHandler.java) |

# directory client

In this directory plays all the magic. The complete authentication and communication with the Home Connect server is in this directory.

|                       File                      |                                       Description                                       |
|:-----------------------------------------------:|:---------------------------------------------------------------------------------------:|
| AuthenticationException.java                    |         Exception that is thrown when unable to authenticate to Home Connect API        |
| CloudOauthHomeconnectAuthenticationService.java |                             Does the complete OAUTH2.0 flow.                            |
| CloudRestfulHomeconnectClient.java              |              Does the complete communication with the Home Connect Server.              |
| HomeconnectAuthenticationService.java           |              singleton instance of the authentication service to get tokens             |
| HomeconnectClient.java                          | singleton instance of a Home Connect client for communicating with the Home Connect API |
| HomeconnectSupportedDevice.java                 | Enumeration of all the devices used by HomeconnectThingHandler                          |
| IHomeconnectAuthenticationService.java          | interface for the Authentication                                                        |
| IHomeconnectClient.java                         | interface to the Home Connect API                                                       |
| IHomeconnectDevice.java                         | interface that represents all the connected devices                                     |
| JsonHomeconnectDevice.java                      | parses json from the homeconnect api IHomeconnectDevice                                 |

 
