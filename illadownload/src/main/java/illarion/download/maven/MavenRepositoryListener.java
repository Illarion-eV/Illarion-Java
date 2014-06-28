/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.download.maven;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MavenRepositoryListener implements RepositoryListener {
    private static final Logger log = LoggerFactory.getLogger(MavenRepositoryListener.class);

    private boolean offline;
    @Nullable
    private MavenDownloaderCallback callback;



    @Override
    public void artifactDescriptorInvalid(RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void artifactDescriptorMissing(RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void metadataInvalid(RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void artifactResolving(RepositoryEvent event) {
        if ((callback != null) && "pom".equals(event.getArtifact().getExtension())) {
            callback.reportNewState(MavenDownloaderCallback.State.ResolvingDependencies, null, offline,
                                    event.getArtifact().getGroupId() + ':' + event.getArtifact().getArtifactId());
        }
        log.info(event.toString());
    }

    @Override
    public void artifactResolved(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataResolving(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataResolved(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDownloading(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDownloaded(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDownloading(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDownloaded(RepositoryEvent event) {

    }

    @Override
    public void artifactInstalling(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactInstalled(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataInstalling(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataInstalled(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDeploying(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDeployed(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDeploying(RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDeployed(RepositoryEvent event) {
        log.info(event.toString());
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public void setCallback(@Nullable MavenDownloaderCallback callback) {
        this.callback = callback;
    }
}
