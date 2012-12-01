#!/bin/bash

rsync "${WORKSPACE}"/setup/*.illares /home/nitram/public_html/downloads
rsync "${WORKSPACE}"/setup/*.jar /home/nitram/public_html/downloads
rsync "${WORKSPACE}"/setup/*.jar.pack.gz /home/nitram/public_html/downloads
rsync "${WORKSPACE}"/setup/lib/*.illares /var/www/illarion/media/java
rsync "${WORKSPACE}"/bin/illabuild.jar /var/www/illarion/media/java
rsync "${WORKSPACE}"/illadownload/JNLP-INF/*.jnlp /home/nitram/public_html/downloads
rsync "${WORKSPACE}"/illadownload/jnlp_ico/*.png /home/nitram/public_html/downloads/jnlp_ico
rsync "${WORKSPACE}"/illadownload/jnlp_ico/*.icns /home/nitram/public_html/downloads/jnlp_ico
rsync "${WORKSPACE}"/illadownload/jnlp_ico/*.ico /home/nitram/public_html/downloads/jnlp_ico
