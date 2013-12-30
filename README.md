Illarion Java Applications
==========================

[![Build Status](https://illarion.org:8080/job/Illarion%20Applications/badge/icon)](https://illarion.org:8080/job/Illarion%20Applications/)

Objectives
----------

Illarion is the online multiplayer roleplaying game developed and maintained by
the Illarion e.V. This repository contains all published client applications.

Details
-------

The applications in this repository are the Illarion Client, the easyNPC
editor, the easyQuest editor and the map editor. Also there is a small utility
to download and update the applications on the players' computers.

Contributing
------------

The team of Illarion is always looking for new members who want to support our
project. This project is developed only by volunteers, who don't get
paid for their work. However, there is much experience to be gained by joining
the development of this project.

For further information check out our homepage: http://illarion.org

Any changes to the applications can be applied using pull requests.

Build
-----

Illarion is using Gradle and the Gradle wrapper to build the application. To
build the application from command line simply enter:

```Batchfile
gradlew build
```

The Gradle wrapper will take care of downloading Gradle, all dependencies and
will perform the build. Make sure you have got a JDK >= 6 installed.

IDE integration
---------------
### IntelliJ IDEA

To integrate the build properly into the IntelliJ IDEA IDE you should use the
"Import Project" functionality and then select the file build.gradle in the
project root directory.
While importing make sure that "Use gradle wapper" is selected.

Afterwards the JetGradle task will allow you to control build operations.
