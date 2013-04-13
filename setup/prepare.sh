#!/bin/bash

if [ -f "${WORKSPACE}"/bin/illabuild.jar ]
then
	rsync "${WORKSPACE}"/bin/illabuild.jar /var/www/illarion/website/media/java
fi