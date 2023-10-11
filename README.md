# ECE 558 Project #2 -- Build a internet-connected device using MQTT
## <b>This assignment is worth 100 points.  Deliverables due to GitHub and Canvas on Sat, 26-Feb by 10:00 PM.  No late assignments after 3:00 PM on Wed, 03-Mar. </b>

### <i> We will be using GitHub classroom for this assignment.

Android Studio (IntelliJ) has good support for GitHub integration.  It also advisable to use GitHub for your MQTT clients running on the RPi.  Several popular Python development IDE's support integration with GitHub.  One of those is Microsoft VS Code (https://code.visualstudio.com/docs/editor/github) which also has good Python support through plug-ins.

Submit your assignment before the deadline by pushing your final Android Studio and Python projects and other deliverables to your GitHub repository for the assignment. We'd also like you to submit a .zip archive of your repository to your Project #2 dropbox on Canvas</i>

## After completing this assignment students should have:
- Practiced object-oriented programming in Python and Java
- Gained experience building a network-connected  device
- Gained experience w/ publish/subscribe via MQTT

### Introduction

Although ECE 558 is entitled <i>Embedded Systems Programming</i> all we have produced so far has been software running on an Android device or the Kotlin console. That’s about to change. Project #2 has  significant hardware and software components to it and provides a chance for you to gain experience building an application for the Internet of Things (IoT). We will be using a Raspberry PI 3, 3B or 4 (RPi) or an Adafruit Huzzah32 to host your MQTT clients, a low cost temperature sensor (ex: Adafruit AHT20 breakout board) and some discrete components and jumper wires to add an LED and pushbutton to the mix. You will use your Android device to run an app that uses MQTT to exchange commands and responses with an MQTT client running on the RPi or Huzzah32. The Android app should be programmed in Kotlin and we suggest using Python for the MQTT clients.  If you are using the Huzzah32 consider using the Arduino support for the board and an MQTT C++ client (there are several).  Your MQTT broker can be a public cloud-based MQTT broker like the HiveMQ public broker (broker.hivemq.com/1883.  If you are running your client on the RPI instead of the Huzzah32 you can start a Mosquitto broker service on the RPi. 

We will be using GitHub classroom for this assignment. We will enable the Individual project capability for the project.   After accepting the assignment you will be given a private repository for you work.  Use it throughout the development/implementation phase of the project.

## GitHub Deliverables
Push your deliverables to your private GitHub repository for the assignment.  The repository should include:
- A video demo of your working network-connected devices.
- A short Theory of operations/design report (.pdf preferred).
- A final version of the source code you wrote. We will use these to grade your effort.  “Neatness counts” for this project - we will grade the quality of your code.  Your code should be well structured, indented consistently (The IDE's help with that) and you should include comments describing what long sections of your code do.    Comments should be descriptive rather than explain the obvious (ex:  //set a to b when the actual code says a = b; does not provide any value-added).  Your use of Javadoc and one of the Python documentation applications to document your API's is encouraged.

## Canvas Deliverables
We would also like you to submit an archive of your Canvas repository to your Project #2 dropbox.  This is easy to do.  Click on the <i>Clone or Download</i> button for your GitHub repository and <i>Download ZIP</i>.  There is a chance that your .zip file will be too large to upload to Canvas - if that happens, thanks for trying.
