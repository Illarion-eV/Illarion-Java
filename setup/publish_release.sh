#!/bin/bash

rsync --update --times "${WORKSPACE}"/setup/*.illares /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/setup/*.jar /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/setup/*.jar.pack.gz /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/bin/illabuild.jar /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/illadownload/release/JNLP-INF/*.jnlp /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/illadownload/jnlp_ico/*.png /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/illadownload/jnlp_ico/*.icns /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/illadownload/jnlp_ico/*.ico /var/www/illarion/media/java
