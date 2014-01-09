package illarion.compile.impl;

import com.mxgraph.model.mxIGraphModel;
import illarion.easyquest.QuestIO;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EasyQuestCompile extends AbstractCompile {
    @Override
    public int compileFile(@Nonnull final Path file) {
        try {
            ensureTargetDir();
            mxIGraphModel model = QuestIO.loadGraphModel(file);
            String fileName = file.getFileName().toString();
            String questName = fileName.replace(".quest", "");
            QuestIO.exportQuest(model, getTargetDir().resolve(questName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
