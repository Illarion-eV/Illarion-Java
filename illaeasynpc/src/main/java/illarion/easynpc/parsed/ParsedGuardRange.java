package illarion.easynpc.parsed;

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * This is the parsed instance of the guard range settings.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedGuardRange implements ParsedData {
    /**
     * The guarding range of the NPC towards north.
     */
    private final int rangeNorth;
    /**
     * The guarding range of the NPC towards south.
     */
    private final int rangeSouth;
    /**
     * The guarding range of the NPC towards east.
     */
    private final int rangeEast;
    /**
     * The guarding range of the NPC towards west.
     */
    private final int rangeWest;

    /**
     * Create a new instance of the checking range.
     */
    public ParsedGuardRange(int rangeNorth, int rangeSouth, int rangeWest, int rangeEast) {
        this.rangeNorth = rangeNorth;
        this.rangeSouth = rangeSouth;
        this.rangeEast = rangeEast;
        this.rangeWest = rangeWest;
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

        target.write("guardRange = ");
        target.write(Integer.toString(rangeNorth));
        target.write(", ");
        target.write(Integer.toString(rangeSouth));
        target.write(", ");
        target.write(Integer.toString(rangeWest));
        target.write(", ");
        target.write(Integer.toString(rangeEast));
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
        target.write("guardNPC:setGuardRange(");
        target.write(Integer.toString(rangeNorth));
        target.write(',');
        target.write(Integer.toString(rangeSouth));
        target.write(',');
        target.write(Integer.toString(rangeWest));
        target.write(',');
        target.write(Integer.toString(rangeEast));
        target.write(");");
        target.write(LuaWriter.NL);
    }
}
