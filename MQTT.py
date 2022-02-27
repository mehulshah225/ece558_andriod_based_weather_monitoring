import paho.mqtt.client as paho
import RPi.GPIO as GPIO 
import time
from threading import Thread
import adafruit_ahtx0
import os.path

broker = "broker.hivemq.com"
client = paho.Client()
client.connect(broker, 1883, 60)


def push_button(flag):
    GPIO.setwarnings(False) # Ignore warning for now
    GPIO.setmode(GPIO.BCM) # Use physical pin numbering
    GPIO.setup(22, GPIO.IN, pull_up_down=GPIO.PUD_DOWN) 
    while True: 
        if GPIO.input(22) == GPIO.HIGH:
            time.sleep(0.3)
            print("pressed")
            client.publish("mehul/pushbtn", "Button Pressed")
            flag = 1
            
        if GPIO.input(22) == GPIO.LOW and flag == 1:
            time.sleep(0.5)
            client.publish("mehul/pushbtn", "Button Released")
            
def aht20():
    import board
    i2c = board.I2C()
    sensor = adafruit_ahtx0.AHTx0(i2c)
    while True:
        #print("\nTemp: %0.2f C" % sensor.temperature)
        #print("Humidity: %0.2f %%" % sensor.relative_humidity)
        client.publish("mehul/sensor/temp",  sensor.temperature)
        client.publish("mehul/sensor/humidity", sensor.relative_humidity)
        time.sleep(interval)
        print(interval)
        
def LED():
    GPIO.setwarnings(False) # Ignore warning for now
    GPIO.setmode(GPIO.BCM) # Use physical pin numbering
    GPIO.setup(18, GPIO.OUT, initial=GPIO.LOW) # Set pin 8 to be an output pin and set initial value to low (off)

# MQTT callback methods
def on_connect(client, userdata, flags, rc):    
    print("Tried to connect to MQTT server: {}:{}...result: {}".format(
        broker,
        1883,
        paho.connack_string(rc)))

    # Check whether the result from connect is the CONNACK_ACCEPTED connack code
    # If conection was successful subscribe to the command topic
    if rc == paho.CONNACK_ACCEPTED:
        # Subscribe to the commands topic filter
        print("CONNACK")
        client.subscribe("mehul/led", qos=1)
        client.subscribe("mehul/Interval", qos=1)
        time.sleep(1)

def on_subscribe(client, userdata, mid, granted_qos):
    print("LED".format(granted_qos[0]))
    
def on_subscribe(client, userdata, mid, granted_qos):
    print("interval".format(granted_qos[0]))
    
def on_message(client, userdata, msg):
    s = str(msg.payload, encoding="UTF_8")
    print("retrieved message: " + s)
    if msg.topic == "mehul/led":
        if s == "true":
            GPIO.output(18, GPIO.HIGH)  
        elif s == "false":
            GPIO.output(18, GPIO.LOW)
        else:
            print("Invalid payload received")
    elif msg.topic == "mehul/Interval":
        interval = int(s)
        print(interval)
    else:
        print("Invalid topic")


        
if __name__ == '__main__':
    global flag
    global interval
    interval = 4
    flag = 0
    Thread(target = push_button, args=[flag]).start()
    Thread(target = aht20).start()
    Thread(target = LED).start()
    client.on_connect = on_connect
    client.on_subscribe = on_subscribe
    client.on_message = on_message
    client.loop_forever()
    