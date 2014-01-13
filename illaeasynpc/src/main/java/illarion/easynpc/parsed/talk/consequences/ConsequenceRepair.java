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
public class ConsequenceRepair implements TalkConsequence {
    @Nullable
    @Override
    public String getLuaModule() {
        return BASE_LUA_MODULE + "repair";
    }

    @Override
    public void writeEasyNpc(@Nonnull Writer target) throws IOException {
        target.write("repair");
    }

    @Override
    public void writeLua(@Nonnull Writer target) throws IOException {
        target.write("talkEntry:addConsequence(" + getLuaModule() + ".repair());");
        target.write(LuaWriter.NL);
    }
}
