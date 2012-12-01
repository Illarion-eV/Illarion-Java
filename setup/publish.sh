#!/bin/bash

rsync --update --times "${WORKSPACE}"/setup/*.illares /home/nitram/public_html/downloads
rsync --update --times "${WORKSPACE}"/setup/*.jar /home/nitram/public_html/downloads
rsync --update --times "${WORKSPACE}"/setup/*.jar.pack.gz /home/nitram/public_html/downloads
rsync --update --times "${WORKSPACE}"/setup/lib/*.illares /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/bin/illabuild.jar /var/www/illarion/media/java
rsync --update --times "${WORKSPACE}"/illadownload/JNLP-INF/*.jnlp /home/nitram/public_html/downloads
rsync --update --times "${WORKSPACE}"/illadownload/jnlp_ico/*.png /home/nitram/public_html/downloads/jnlp_ico
rsync --update --times "${WORKSPACE}"/illadownload/jnlp_ico/*.icns /home/nitram/public_html/downloads/jnlp_ico
rsync --update --times "${WORKSPACE}"/illadownload/jnlp_ico/*.ico /home/nitram/public_html/downloads/jnlp_ico
