package illarion.easynpc.parsed;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * This is the parsed text that is spoken by a NPC guard upon specific events.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedGuardText implements ParsedData {
    /**
     * This enumerator contains the different types of texts that are allowed to be parsed.
     */
    public enum TextType {
        /**
         * This text will be spoken in case the NPC warps away a monster.
         */
        WarpedMonster,

        /**
         * This text will be spoken in case the NPC warps away a player.
         */
        WarpedPlayer,

        /**
         * This text will be spoken in case the NPC attacks a player.
         */
        HitPlayer
    }

    /**
     * The type of this parsed text instance.
     */
    @Nonnull
    private final TextType type;

    /**
     * The german text stored in this text instance.
     */
    @Nonnull
    private final String german;

    /**
     * The english text stored in this text instance.
     */
    @Nonnull
    private final String english;

    /**
     * Initialize a new parsed text instance that contains the text read from the easyNPC script.
     *
     * @param type the type of the parsed text
     * @param german the german text
     * @param english the english text
     */
    public ParsedGuardText(@Nonnull final TextType type, @Nonnull final String german, @Nonnull final String english) {
        this.type = type;
        this.german = german;
        this.english = english;
    }

    @Override
    public boolean effectsEasyNpcStage(@Nonnull EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.guarding;
    }

    @Override
    public void writeEasyNpc(@Nonnull Writer target, @Nonnull EasyNpcWriter.WritingStage stage) throws IOException {
        if (stage != EasyNpcWriter.WritingStage.guarding) {
            throw new IllegalArgumentException("This function did not request a call for a stage but guarding.");
        }

        switch (type) {
            case WarpedMonster:
                target.write("warpedMonsterMsg");
                break;
            case WarpedPlayer:
                target.write("warpedPlayerMsg");
                break;
            case HitPlayer:
                target.write("hitPlayerMsg");
                break;
            default:
                throw new IllegalStateException("Illegal value for type: " + String.valueOf(type));
        }
        target.write(" \"");
        target.write(german);
        target.write("\", \"");
        target.write(english);
        target.write('"');
        target.write(EasyNpcWriter.NL);
    }

    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Guarding;
    }

    @Nullable
    @Override
    public String[] getRequiredModules() {
        return new String[]{"npc.base.guard"};
    }

    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {
        if (stage != LuaWriter.WritingStage.Guarding) {
            throw new IllegalArgumentException("This function did not request a call for a stage but guarding.");
        }
        target.write("guardNPC:");
        switch (type) {
            case WarpedMonster:
                target.write("addWarpedMonsterText");
                break;
            case WarpedPlayer:
                target.write("addWarpedPlayerText");
                break;
            case HitPlayer:
                target.write("addHitPlayerText");
                break;
            default:
                throw new IllegalStateException("Illegal value for type: " + String.valueOf(type));
        }
        target.write("(\"");
        target.write(german);
        target.write("\", \"");
        target.write(english);
        target.write("\");");
        target.write(LuaWriter.NL);
    }
}
