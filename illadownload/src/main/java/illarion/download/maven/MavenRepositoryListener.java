/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static illarion.download.maven.MavenDownloaderCallback.State.ResolvingDependencies;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MavenRepositoryListener implements RepositoryListener {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MavenRepositoryListener.class);

    private boolean offline;
    @Nullable
    private MavenDownloaderCallback callback;



    @Override
    public void artifactDescriptorInvalid(@Nonnull RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void artifactDescriptorMissing(@Nonnull RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void metadataInvalid(@Nonnull RepositoryEvent event) {
        log.warn(event.toString());
    }

    @Override
    public void artifactResolving(@Nonnull RepositoryEvent event) {
        if ((callback != null) && "pom".equals(event.getArtifact().getExtension())) {
            callback.reportNewState(ResolvingDependencies, null, offline,
                                    event.getArtifact().getGroupId() + ':' + event.getArtifact().getArtifactId());
        }
        log.info(event.toString());
    }

    @Override
    public void artifactResolved(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataResolving(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataResolved(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDownloading(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDownloaded(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDownloading(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDownloaded(@Nonnull RepositoryEvent event) {

    }

    @Override
    public void artifactInstalling(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactInstalled(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataInstalling(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataInstalled(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDeploying(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void artifactDeployed(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDeploying(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    @Override
    public void metadataDeployed(@Nonnull RepositoryEvent event) {
        log.info(event.toString());
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public void setCallback(@Nullable MavenDownloaderCallback callback) {
        this.callback = callback;
    }
}
