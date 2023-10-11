"""
Raspberry Pi and Android paho MQTT example
Turns the Green LED on the Explorer Pro Hat on and off w/ the MQTT topics
Raspberry/liteon and Raspberry/liteoff.  Returns status (LED on or off)
in the topic Raspberry/status

Raspberry Pi code based on example from the Institute of Geomatics Engineering
Android code loosly based on HiveMQ and patho examples
"""

## imports
from config import *
import paho.mqtt.client as mqtt
import time
import explorerhat

# global variables
led_on = "LED is OFF"
client = mqtt.Client()


# MQTT callback methods
def on_connect(client, userdata, flags, rc):    
    print("Tried to connect to MQTT server: {}:{}...result: {}".format(
        mqtt_server_host,
        mqtt_server_port,
        mqtt.connack_string(rc)))

    # Check whether the result from connect is the CONNACK_ACCEPTED connack code
    # If conection was successful subscribe to the command topic
    if rc == mqtt.CONNACK_ACCEPTED:
         # Subscribe to the commands topic filter
        client.subscribe (
            "profroyk/Raspberry", 
            qos=1
        )
        time.sleep(1)

def on_subscribe(client, userdata, mid, granted_qos):
    print("Subscribed with QoS: {}".format(granted_qos[0]))


def on_message(client, userdata, msg):
    global led_on

    s = str(msg.payload, encoding="UTF_8")
    print("retrieved message: " + s)
    if s == "liteon":
        explorerhat.light.green.on()
        led_on = "LED is ON"
        publish_status()
    elif s == "liteoff":
        explorerhat.light.green.off()
        led_on = "LED is OFF"
        publish_status()
    else:
        print("Invalid payload received")

def publish_status():
    global led_on

    client.publish(
        topic = "profroyk/Android/status",
        payload = led_on 
    )


# main program
client.on_connect = on_connect
client.on_subscribe = on_subscribe
client.on_message = on_message

# connect to the MQTT server (which runs locally on the RPI)
# subscribing to the command topic is handled in on_connect()
client.connect (
    host=mqtt_server_host,
    port=mqtt_server_port,
    keepalive=mqtt_keepalive
 )

client.loop_forever()

