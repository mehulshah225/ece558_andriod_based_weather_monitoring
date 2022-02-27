import paho.mqtt.client as paho
import RPi.GPIO as GPIO 
import time
from threading import Thread
import adafruit_ahtx0
import os.path

broker = "broker.hivemq.com"
client = paho.Client("P1")
client.connect(broker)


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
        time.sleep(4)
        
def LED():
    GPIO.setwarnings(False) # Ignore warning for now
    GPIO.setmode(GPIO.BCM) # Use physical pin numbering
    GPIO.setup(23, GPIO.OUT, initial=GPIO.LOW) # Set pin 8 to be an output pin and set initial value to low (off)


def on_subscribe(client, userdata, mid, granted_qos):
    print("LED".format(granted_qos[0]))
    
def on_message(client, userdata, msg):
    s = str(msg.payload, encoding="UTF_8")
    print("retrieved message: " + s)
    if s == "true":
        GPIO.output(23, GPIO.HIGH)  
    elif s == "false":
        GPIO.output(23, GPIO.LOW)
    else:
        print("Invalid payload received")

client.on_message = on_message
        
if __name__ == '__main__':
    global flag
    flag = 0
    Thread(target = push_button, args=[flag]).start()
    Thread(target = aht20).start()
    Thread(target = LED).start()
    
    