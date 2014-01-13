package illarion.easynpc.parser;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedGuardText;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser is used to read the text that is spoken by a NPC guard.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class NpcGuardText implements NpcType {
    private static final Map<ParsedGuardText.TextType, Pattern> PATTERN_MAP;

    static {
        PATTERN_MAP = new EnumMap<>(ParsedGuardText.TextType.class);
        PATTERN_MAP.put(ParsedGuardText.TextType.WarpedMonster, Pattern.compile(
                "^\\s*(warpedMonsterMsg)\\s*[\\(]*\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*[\\)]*\\s*$"));
        PATTERN_MAP.put(ParsedGuardText.TextType.WarpedPlayer, Pattern.compile(
                "^\\s*(warpedPlayerMsg)\\s*[\\(]*\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*[\\)]*\\s*$"));
        PATTERN_MAP.put(ParsedGuardText.TextType.HitPlayer, Pattern.compile(
                "^\\s*(hitPlayerMsg)\\s*[\\(]*\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*[\\)]*\\s*$"));
    }

    @Override
    public boolean canParseLine(@Nonnull EasyNpcScript.Line line) {
        for (Pattern pattern : PATTERN_MAP.values()) {
            if (pattern.matcher(line.getLine()).matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void parseLine(@Nonnull EasyNpcScript.Line line, @Nonnull ParsedNpc npc) {
        String stringLine = line.getLine();
        for (Map.Entry<ParsedGuardText.TextType, Pattern> entry : PATTERN_MAP.entrySet()) {
            Matcher matcher = entry.getValue().matcher(stringLine);
            if (matcher.find()) {
                final String germanText = matcher.group(2);
                final String englishText = matcher.group(3);
                npc.addNpcData(new ParsedGuardText(entry.getKey(), germanText, englishText));
                return;
            }
        }
    }

    @Override
    public void enlistHighlightedWords(@Nonnull TokenMap map) {
        map.put("warpedMonsterMsg", Token.RESERVED_WORD);
        map.put("warpedPlayerMsg", Token.RESERVED_WORD);
        map.put("hitPlayerMsg", Token.RESERVED_WORD);
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
        return Lang.getMsg(NpcGuardText.class, "Docu.description");
    }

    @Override
    public String getExample() {
        return Lang.getMsg(NpcGuardText.class, "Docu.example");
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(NpcGuardText.class, "Docu.syntax");
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(NpcGuardText.class, "Docu.title");
    }
}
