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
package illarion.compile.impl;

import com.mxgraph.model.mxIGraphModel;
import illarion.easyquest.QuestIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EasyQuestCompile extends AbstractCompile {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyQuestCompile.class);

    @Override
    public int compileFile(@Nonnull final Path file) {
        try {
            ensureTargetDir();
            mxIGraphModel model = QuestIO.loadGraphModel(file);
            String fileName = file.getFileName().toString();
            String questName = fileName.replace(".quest", "");
            QuestIO.exportQuest(model, getTargetDir().resolve(questName));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            return -1;
        }
        return 0;
    }

    @Override
    public int compileStream(@Nonnull InputStream in, @Nonnull OutputStream out) {
        try {
            QuestIO.loadGraphModel(new InputStreamReader(in, QuestIO.CHARSET));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            return -1;
        }
        return 0;
    }
}
