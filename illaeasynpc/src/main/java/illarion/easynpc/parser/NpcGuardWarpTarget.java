package illarion.easynpc.parser;

import illarion.common.types.Location;
import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedGuardWarpTarget;
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
public class NpcGuardWarpTarget implements NpcType {
    private static final Pattern GUARD_WARP_TARGET_PATTERN = Pattern
            .compile("^\\s*(guardWarpTarget)\\s*=\\s*(-*[0-9]+)[,\\s]+(-*[0-9]+)[,\\s]+(-*[0-9]+)\\s*$");

    @Override
    public boolean canParseLine(@Nonnull EasyNpcScript.Line line) {
        return GUARD_WARP_TARGET_PATTERN.matcher(line.getLine()).matches();
    }

    @Override
    public void parseLine(@Nonnull EasyNpcScript.Line line, @Nonnull ParsedNpc npc) {
        Matcher matcher = GUARD_WARP_TARGET_PATTERN.matcher(line.getLine());
        if (matcher.find()) {
            try {
                final int x = Integer.parseInt(matcher.group(2));
                final int y = Integer.parseInt(matcher.group(3));
                final int z = Integer.parseInt(matcher.group(4));

                npc.addNpcData(new ParsedGuardWarpTarget(new Location(x, y, z)));
            } catch (NumberFormatException e) {
                npc.addError(line, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void enlistHighlightedWords(@Nonnull TokenMap map) {
        map.put("guardWarpTarget", Token.RESERVED_WORD);
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
        return Lang.getMsg(NpcGuardWarpTarget.class, "Docu.description");
    }

    @Override
    public String getExample() {
        return Lang.getMsg(NpcGuardWarpTarget.class, "Docu.example");
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(NpcGuardWarpTarget.class, "Docu.syntax");
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(NpcGuardWarpTarget.class, "Docu.title");
    }
}
