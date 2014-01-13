package illarion.easynpc.parser.talk.consequences;

import illarion.easynpc.Lang;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.parsed.talk.consequences.ConsequenceArena;
import illarion.easynpc.parser.talk.ConsequenceParser;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Arena extends ConsequenceParser {
    private static final Map<ConsequenceArena.Task, Pattern> PATTERN_MAP;

    static {
        PATTERN_MAP = new EnumMap<>(ConsequenceArena.Task.class);
        PATTERN_MAP.put(ConsequenceArena.Task.RequestMonster,
                        Pattern.compile("\\s*arena\\s*\\(\\s*requestMonster\\s*\\)[,\\s]*"));
        PATTERN_MAP.put(ConsequenceArena.Task.ShowStatistics,
                        Pattern.compile("\\s*arena\\s*\\(\\s*getStats\\s*\\)[,\\s]*"));
        PATTERN_MAP.put(ConsequenceArena.Task.ShowRanking,
                        Pattern.compile("\\s*arena\\s*\\(\\s*getRanking\\s*\\)[,\\s]*"));
    }

    @Nullable
    @Override
    public TalkConsequence extract() {
        String line = getNewLine();
        if (line == null) {
            throw new IllegalStateException("Can't extract if no state set.");
        }

        for (Map.Entry<ConsequenceArena.Task, Pattern> entry : PATTERN_MAP.entrySet()) {
            Matcher matcher = entry.getValue().matcher(line);
            if (matcher.find()) {
                setLine(matcher.replaceFirst(""));
                return new ConsequenceArena(entry.getKey());
            }
        }
        return null;
    }

    @Override
    public void enlistHighlightedWords(TokenMap map) {
        map.put("arena", Token.RESERVED_WORD);
        map.put("requestMonster", Token.RESERVED_WORD);
        map.put("getStats", Token.RESERVED_WORD);
        map.put("getRanking", Token.RESERVED_WORD);
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(Arena.class, "Docu.description");
    }

    @Override
    public String getExample() {
        return Lang.getMsg(Arena.class, "Docu.example");
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(Arena.class, "Docu.syntax");
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(Arena.class, "Docu.title");
    }
}
