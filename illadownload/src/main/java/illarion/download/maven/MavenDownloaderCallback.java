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

import illarion.common.util.ProgressMonitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;

/**
 * This is the callback interface for the maven downloader. It can be used to keep track of the operations performed
 * by the maven downloader.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface MavenDownloaderCallback {
    /**
     * This enumeration stores the different states of the maven downloading progress.
     */
    enum State {
        /**
         * This state is active while the downloader is resolving the current version of the main application.
         */
        SearchingNewVersion,

        /**
         * This state is active while the downloader is resolving the required dependencies of the application.
         */
        ResolvingDependencies,

        /**
         * This state is active while the downloader is mapping the artifacts to local files and downloads the files
         * if required.
         */
        ResolvingArtifacts
    }

    /**
     * Report a new active state.
     *
     * @param state the state that is active from now on
     * @param progress the progress of this state, this may be {@code null} in case the progress is not determined
     */
    void reportNewState(@Nonnull State state, @Nullable ProgressMonitor progress);

    /**
     * Report that the resolving is done.
     *
     * @param classpath the resolved classpath, this may be {@code null} in case the resolving failed
     */
    void resolvingDone(@Nonnull Collection<File> classpath);

    /**
     * Report that the resolving has failed and forward the exception that was caused in response.
     *
     * @param ex the resolving exception
     */
    void resolvingFailed(@Nonnull Exception ex);
}
