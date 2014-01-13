package illarion.easynpc.parser.talk.consequences;

import illarion.easynpc.Lang;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.parsed.talk.consequences.ConsequenceRepair;
import illarion.easynpc.parser.talk.ConsequenceParser;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Repair extends ConsequenceParser {
    private static final Pattern STRING_FIND = Pattern.compile("\\s*repair[,\\s]*");

    @Nullable
    @Override
    public TalkConsequence extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no state set.");
        }

        final Matcher stringMatcher = STRING_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            setLine(stringMatcher.replaceFirst(""));
            return new ConsequenceRepair();
        }

        return null;
    }

    @Override
    public void enlistHighlightedWords(TokenMap map) {
        map.put("repair", Token.RESERVED_WORD);
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(Repair.class, "Docu.description"); //$NON-NLS-1$
    }

    @Override
    public String getExample() {
        return Lang.getMsg(Repair.class, "Docu.example"); //$NON-NLS-1$
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(Repair.class, "Docu.syntax"); //$NON-NLS-1$
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(Repair.class, "Docu.title"); //$NON-NLS-1$
    }
}
