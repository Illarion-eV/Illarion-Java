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
package illarion.client.net.server;

import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * The standard interface that is used for a reply that was send by the server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ServerReply {
    /**
     * The decoding function. This function is executed to allow the reply to read the data from the network interface.
     *
     * @param reader the reader from the network interface
     * @throws IOException throw in case reading fails for some reason
     */
    void decode(@Nonnull NetCommReader reader) throws IOException;

    /**
     * Execute the task.
     *
     * @return the result of the execution
     */
    @Nonnull
    ServerReplyResult execute();
}
