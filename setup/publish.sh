#!/bin/bash

rsync "${WORKSPACE}"/setup/*.illares /home/nitram/public_html/downloads
rsync "${WORKSPACE}"/setup/*.jar /home/nitram/public_html/downloads
rsync "${WORKSPACE}"/setup/*.jar.pack.gz /home/nitram/public_html/downloads
rsync "${WORKSPACE}"/setup/lib/*.illares /var/www/illarion/media/java
rsync "${WORKSPACE}"/bin/illabuild.jar /var/www/illarion/media/java