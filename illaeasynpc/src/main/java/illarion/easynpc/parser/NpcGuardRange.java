package illarion.easynpc.parser;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedGuardRange;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the parser that allows setting the range that is monitored by a guard NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class NpcGuardRange implements NpcType {
    private static final Pattern GUARD_RANGE_PATTERN = Pattern
            .compile("^\\s*(guardRange)\\s*=\\s*([0-9]+)[,\\s]+([0-9]+)[,\\s]+([0-9]+)[,\\s]+([0-9]+)\\s*$");

    @Override
    public boolean canParseLine(@Nonnull EasyNpcScript.Line line) {
        return GUARD_RANGE_PATTERN.matcher(line.getLine()).matches();
    }

    @Override
    public void parseLine(@Nonnull EasyNpcScript.Line line, @Nonnull ParsedNpc npc) {
        Matcher matcher = GUARD_RANGE_PATTERN.matcher(line.getLine());
        if (matcher.find()) {
            try {
                final int north = Integer.parseInt(matcher.group(2));
                final int south = Integer.parseInt(matcher.group(3));
                final int west = Integer.parseInt(matcher.group(4));
                final int east = Integer.parseInt(matcher.group(5));

                npc.addNpcData(new ParsedGuardRange(north, south, west, east));
            } catch (NumberFormatException e) {
                npc.addError(line, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void enlistHighlightedWords(@Nonnull TokenMap map) {
        map.put("guardRange", Token.RESERVED_WORD);
    }

    @Nullable
    @Override
    public DocuEntry getChild(int index) {
        throw new IllegalArgumentException("There are no children to request.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(NpcGuardRange.class, "Docu.description");
    }

    @Override
    public String getExample() {
        return Lang.getMsg(NpcGuardRange.class, "Docu.example");
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(NpcGuardRange.class, "Docu.syntax");
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(NpcGuardRange.class, "Docu.title");
    }
}
