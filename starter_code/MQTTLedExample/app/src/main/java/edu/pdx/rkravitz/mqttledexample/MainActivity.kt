package edu.pdx.rkravitz.mqttledexample

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.pdx.rkravitz.mqttledexample.databinding.ActivityMainBinding
import org.eclipse.paho.client.mqttv3.*

@Suppress("PrivatePropertyName")
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var binding: ActivityMainBinding
    private lateinit var mqttClient : MQTTClient
    private lateinit var mqttClientID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if Internet connection is available
        // exit if it is not
        if (!isConnected()) {
            Log.d(TAG, "Internet connection NOT available")
            Toast.makeText(this, "Internet connection NOT available", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Log.d(TAG, "Connected to the Internet")
            Toast.makeText(this, "Connected to the Internet", Toast.LENGTH_LONG).show()
        }

        // open mQTT Broker communication
        mqttClientID = MqttClient.generateClientId()
        mqttClient = MQTTClient(this, MQTT_SERVER_URI, mqttClientID)

        // set initial state of the buttons
        binding.connectBtn.isEnabled = true
        binding.disconnectBtn.isEnabled = false
        binding.liteonBtn.isEnabled = false
        binding.liteoffBtn.isEnabled = false

        // put some text in the LED status
        binding.stsTextview.text = "LED is ???"

        // Connect and Disconnect listeners
        binding.connectBtn.setOnClickListener {
            // Connect to MQTT Broker and subscribe to the status topic
            mqttClient.connect(
                MQTT_USERNAME,
                MQTT_PWD,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(TAG, "Connection success")

                        val successMsg = "MQTT Connection to $MQTT_SERVER_URI Established"
                        Toast.makeText(this@MainActivity, successMsg, Toast.LENGTH_LONG).show()
                        binding.connectBtn.isEnabled = false
                        binding.disconnectBtn.isEnabled = true

                        // disable the LED buttons
                        binding.liteonBtn.isEnabled = true
                        binding.liteoffBtn.isEnabled = true

                        // subscribe to the status topics
                        subscribeToStatus()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Connection failure: ${exception.toString()}")
                        val failureMsg =
                            "MQTT Connection to $MQTT_SERVER_URI failed: ${exception?.toString()}"
                        Toast.makeText(this@MainActivity, failureMsg, Toast.LENGTH_LONG).show()
                        exception?.printStackTrace()
                    }
                },

                object : MqttCallback {
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        val msg = "Received message: ${message.toString()} from topic: $topic"
                        Log.d(TAG, msg)

                        // update the status textview
                        // since a message arrived I'm assuming that the topic string is not null
                        if (topic!! == STS_TOPIC) {
                            binding.stsTextview.text = message.toString()
                        }
                    }

                    override fun connectionLost(cause: Throwable?) {
                        Log.d(TAG, "Connection lost ${cause.toString()}")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d(TAG, "Delivery complete")
                    }
                })
        }

        binding.disconnectBtn.setOnClickListener {
            // Disconnect from MQTT Broker if connected
            if (mqttClient.isConnected()) {
                mqttClient.disconnect(object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(TAG, "Disconnected from $$MQTT_SERVER_URI")
                        Toast.makeText(this@MainActivity, "MQTT Disconnection success", Toast.LENGTH_LONG).show()
                        binding.connectBtn.isEnabled = true
                        binding.disconnectBtn.isEnabled = false

                        // disable the LED buttons
                        binding.liteonBtn.isEnabled = false
                        binding.liteoffBtn.isEnabled = false
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to disconnect exception: ${exception.toString()}")
                    }
                })
            } else {
                Log.d(TAG, "Impossible to disconnect, no server connected")
            }
        }

        // LED change listeners
        binding.liteonBtn.setOnClickListener {
            // Publish the LiteOn message
            changeLEDState(LITEON_MESSAGE, false, true)
        }

        binding.liteoffBtn.setOnClickListener {
            // Publish the LiteOff message
            changeLEDState(LITEOFF_MESSAGE, true, false)
        }
    }

    // helper functions
    private fun isConnected(): Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (capabilities != null) {
            result = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        }
        return result
    }

     private fun subscribeToStatus() {
           // subscribe to status topic only if connected to broker
           if (mqttClient.isConnected()) {
               mqttClient.subscribe(
                   topic = STS_TOPIC,
                   qos = 1,
                   object : IMqttActionListener {
                       override fun onSuccess(asyncActionToken: IMqttToken?) {
                           val msg = "Subscribed to: $STS_TOPIC"
                           Log.d(TAG, msg)
                           Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                       }

                       override fun onFailure(
                           asyncActionToken: IMqttToken?,
                           exception: Throwable?
                       ) {
                           Log.d(
                               TAG, "Failed to subscribe: $STS_TOPIC exception: ${exception.toString()}"
                           )
                       }
                   })
           } else {
               Log.d(TAG, "Cannot subscribe to $STS_TOPIC: Not connected to server")
           }
       }

    private fun changeLEDState(message: String, enableLiteOnBtn: Boolean, enableLiteOffBtn: Boolean) {
        if (mqttClient.isConnected()) {
            val topic = LITE_TOPIC
            mqttClient.publish(
                topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Successfully published message: $message to topic: $topic"
                        Log.d(TAG, msg)

                        binding.liteonBtn.isEnabled = enableLiteOnBtn
                        binding.liteoffBtn.isEnabled = enableLiteOffBtn
                    }

                    override fun onFailure(
                        asyncActionToken: IMqttToken?,
                        exception: Throwable?
                    ) {
                        val msg =
                            "Failed to publish: $message to topic: $topic exception: ${exception.toString()}"
                        Log.d(TAG, msg)
                    }
                })
        } else {
            Log.d(TAG, "Impossible to publish, no server connected")
        }
    }
}