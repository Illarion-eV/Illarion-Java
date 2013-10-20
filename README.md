Illarion Java Applications
==========================

[![Build Status](https://illarion.org:8080/job/Illarion%20Applications/badge/icon)](https://illarion.org:8080/job/Illarion%20Applications/)

Objectives
----------

Illarion is the online multiplayer roleplaying game that is developed and
maintained by the Illarion e.V. This repository contains all client
applications that get published.

Details
-------

The applications in this repository are the Illarion Client, the easyNPC
editor, the easyQuest editor and the map editor. Also there is a small utility
to download and update the applications on the players computers.

Contributing
------------

The team of Illarion is always looking for new members who want to support our
project. This project is created developed only by volunteers, who don't get
paid for their work. How ever there is much experience to be gained by joining
into the development of this project.

For further information check out our homepage: http://illarion.org

Any changes to the applications can be applied using pull requests.

Build
-----

Illarion is using Gradle and the Gradle wrapper to build the application. To
build the application from command line simply be entering:

```Batchfile
gradlew build
```

The Gradle wrapper will take care for downloading Gradle, all dependencies and
will perform the build. Make sure you got a JDK >= 6 installed.

IDE integration
---------------
### IntelliJ IDEA

To integrate the build properly into the IntelliJ IDEA IDE you should import the
project when opening it and select the gradle.build file in the root directory.

After selecting this make sure that Settings->Gradle is set to "Use gradle wapper"
and the gradle.build file in the root directory of the project is selected.

Once this is done the JetGradle task will will allow you to control the build
operations.