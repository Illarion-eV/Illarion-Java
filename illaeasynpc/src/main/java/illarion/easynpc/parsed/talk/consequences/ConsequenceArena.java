package illarion.easynpc.parsed.talk.consequences;

import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ConsequenceArena implements TalkConsequence {

    public enum Task {
        RequestMonster,
        ShowStatistics,
        ShowRanking
    }

    @Nonnull
    private final Task task;

    public ConsequenceArena(@Nonnull Task task) {
        this.task = task;
    }

    @Nullable
    @Override
    public String getLuaModule() {
        return BASE_LUA_MODULE + "arena";
    }

    @Override
    public void writeEasyNpc(@Nonnull Writer target) throws IOException {
        switch (task) {
            case RequestMonster:
                target.write("arena(requestMonster)");
                break;
            case ShowStatistics:
                target.write("arena(getStats)");
                break;
            case ShowRanking:
                target.write("arena(getRanking)");
                break;
        }
    }

    @Override
    public void writeLua(@Nonnull Writer target) throws IOException {
        target.write("talkEntry:addConsequence(");
        target.write(getLuaModule() + ".arena(\"");
        switch (task) {
            case RequestMonster:
                target.write("request");
                break;
            case ShowStatistics:
                target.write("points");
                break;
            case ShowRanking:
                target.write("list");
                break;
        }
        target.write("\"));");
        target.write(LuaWriter.NL);
    }
}
