/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.easynpc.parser;

import illarion.common.data.Skill;
import illarion.common.data.Skills;
import illarion.common.types.Location;
import illarion.easynpc.data.*;
import illarion.easynpc.grammar.EasyNpcParser;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.consequences.ConsequenceArena;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains a collection of utility function to process the parser tree.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    @Nonnull
    static String getString(@Nullable ParseTree node) {
        if (node == null) {
            LOGGER.warn("Node for string not found.");
            return "<null>";
        }
        return removeQuotes(node.getText());
    }

    static boolean getBoolean(@Nullable ParseTree node) {
        if (node == null) {
            LOGGER.warn("Node for boolean not found.");
            return false;
        }
        String string = node.getText();
        switch (string) {
            case "true":
            case "on":
            case "yes":
                return true;
            default:
                return false;
        }
    }

    static int getInteger(@Nullable ParseTree node) {
        if (node == null) {
            LOGGER.warn("Node for integer not found.");
            return 0;
        }
        try {
            return Integer.parseInt(node.getText());
        } catch (NumberFormatException e) {
            LOGGER.warn("Number node does not seem to contain a number.");
            return 0;
        }
    }

    static double getFloat(@Nullable ParseTree node) {
        if (node == null) {
            LOGGER.warn("Node for floating-point value not found.");
            return 0;
        }
        try {
            return Double.parseDouble(node.getText());
        } catch (NumberFormatException e) {
            LOGGER.warn("Number node does not seem to contain a number.");
            return 0;
        }
    }

    @Nonnull
    private static <T extends Enum<T>> T getEnumValue(
            @Nullable ParserRuleContext node, @Nonnull Class<T> enumClass, @Nonnull T defaultValue) {
        if (node == null) {
            LOGGER.warn("Expected node for enumerator {} not found.", enumClass.getSimpleName());
            return defaultValue;
        }
        String string = removeQuotes(node.getText());
        try {
            return Enum.valueOf(enumClass, string);
        } catch (IllegalArgumentException e) {
            node.addErrorNode(node.getStart());
            LOGGER.warn("Failed to resolve {} to enumerator {}", string, enumClass.getSimpleName());
            return defaultValue;
        }
    }

    static ConsequenceArena.Task getArenaTask(@Nullable EasyNpcParser.ArenaTaskContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for arena task not found.");
            return ConsequenceArena.Task.RequestMonster;
        }

        switch (node.getStart().getText()) {
            case "requestMonster":
                return ConsequenceArena.Task.RequestMonster;
            case "getStats":
                return ConsequenceArena.Task.ShowStatistics;
            case "getRanking":
                return ConsequenceArena.Task.ShowRanking;
            default:
                LOGGER.warn("Failed to resolve {} to arena task.", node.getStart().getText());
                return ConsequenceArena.Task.RequestMonster;
        }
    }

    @Nonnull
    static CharacterRace getRace(@Nullable EasyNpcParser.RaceContext node) {
        return getEnumValue(node, CharacterRace.class, CharacterRace.human);
    }

    @Nonnull
    static CharacterSex getSex(@Nullable EasyNpcParser.GenderContext node) {
        return getEnumValue(node, CharacterSex.class, CharacterSex.male);
    }

    @Nonnull
    static CharacterLanguage getCharacterLanguage(@Nullable EasyNpcParser.CharLanguageContext node) {
        return getEnumValue(node, CharacterLanguage.class, CharacterLanguage.common);
    }

    @Nonnull
    static PlayerLanguage getPlayerLanguage(@Nullable EasyNpcParser.LanguageContext node) {
        return getEnumValue(node, PlayerLanguage.class, PlayerLanguage.english);
    }

    @Nonnull
    static NpcBaseState getTalkState(@Nullable EasyNpcParser.TalkstateGetContext node) {
        return getEnumValue(node, NpcBaseState.class, NpcBaseState.idle);
    }

    @Nonnull
    static NpcBaseStateToggle getTalkState(@Nullable EasyNpcParser.TalkstateSetContext node) {
        return getEnumValue(node, NpcBaseStateToggle.class, NpcBaseStateToggle.begin);
    }

    @Nonnull
    static CharacterDirection getDirection(@Nullable EasyNpcParser.DirectionContext node) {
        return getEnumValue(node, CharacterDirection.class, CharacterDirection.north);
    }

    @Nonnull
    static Towns getTown(@Nullable EasyNpcParser.TownContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for town not found.");
            return Towns.None;
        }
        switch (node.getText()) {
            case "none":
                return Towns.None;
            case "free":
                return Towns.Free;
            default:
                return getEnumValue(node, Towns.class, Towns.None);
        }
    }

    @Nonnull
    static Color getColor(@Nullable EasyNpcParser.ColorContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for color not found.");
            return new Color(0, 0, 0);
        }

        int red = getColorComponent(node.colorComponent(0));
        int green = getColorComponent(node.colorComponent(1));
        int blue = getColorComponent(node.colorComponent(2));
        return new Color(red, green, blue);
    }

    @Nonnull
    static CharacterAttribute getAttribute(@Nullable EasyNpcParser.AttributeContext node) {
        return getEnumValue(node, CharacterAttribute.class, CharacterAttribute.strength);
    }

    @Nonnull
    static CompareOperators getOperator(@Nullable EasyNpcParser.CompareContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for compare not found.");
            return CompareOperators.equal;
        }
        switch (node.getStart().getText()) {
            case "=":
                return CompareOperators.equal;
            case "<":
                return CompareOperators.lesser;
            case ">":
                return CompareOperators.greater;
            case "<=":
                return CompareOperators.lesserEqual;
            case ">=":
                return CompareOperators.greaterEqual;
            case "~=":
                return CompareOperators.notEqual;
            default:
                node.addErrorNode(node.getStart());
                LOGGER.warn("Unexpected value {} for compare operator.", node.getText());
                return CompareOperators.equal;
        }
    }

    @Nonnull
    static CalculationOperators getOperator(@Nullable EasyNpcParser.SetContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for set not found.");
            return CalculationOperators.set;
        }
        switch (node.getStart().getText()) {
            case "=":
                return CalculationOperators.set;
            case "+":
                return CalculationOperators.add;
            case "-":
                return CalculationOperators.subtract;
            default:
                node.addErrorNode(node.getStart());
                LOGGER.warn("Unexpected value {} for set operator.", node.getText());
                return CalculationOperators.set;
        }
    }

    @Nonnull
    static AdvancedNumber getAdvancedNumber(@Nullable EasyNpcParser.AdvancedNumberContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for advanced number not found.");
            return new AdvancedNumber(0);
        }
        EasyNpcParser.AdvancedNumberExpressionContext expressionCtx = node.advancedNumberExpression();
        if (expressionCtx != null) {
            EasyNpcParser.AdvancedNumberExpressionBodyContext bodyCtx = expressionCtx.advancedNumberExpressionBody();
            if (bodyCtx != null) {
                return new AdvancedNumber(bodyCtx.getText());
            }
        } else {
            TerminalNode intValue = node.INT();
            if (intValue != null) {
                return new AdvancedNumber(getInteger(intValue));
            } else {
                if ("%NUMBER".equals(node.getText())) {
                    return new AdvancedNumber();
                }
            }
        }
        node.addErrorNode(node.getStart());
        LOGGER.warn("Failed to extract advanced number from the context: {}", node.getText());
        return new AdvancedNumber(0);
    }

    @Nullable
    static Items getItem(@Nullable EasyNpcParser.TraderComplexItemIdContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item id not found.");
            return null;
        }

        return getItem(node.itemId());
    }

    @Nullable
    static Items getItem(@Nullable EasyNpcParser.ItemIdContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item id not found.");
            return null;
        }

        TerminalNode terminalNode = node.INT();
        int id = getInteger(terminalNode);
        Items item = Items.valueOf(id);
        if (item == null) {
            node.addErrorNode(node.INT().getSymbol());
            LOGGER.warn("Item ID {} failed to map to a item.", Integer.toString(id));
        }
        return item;
    }

    @Nonnull
    static TalkingMode getTalkMode(@Nullable ParseTree node) {
        if (node == null) {
            LOGGER.warn("Expected node for talking mode not found.");
            return TalkingMode.Talk;
        }
        switch (node.getText()) {
            case "shout":
            case "yell":
                return TalkingMode.Shout;
            case "whisper":
                return TalkingMode.Whisper;
            default:
                return TalkingMode.Talk;
        }
    }

    @Nonnull
    static CharacterMagicType getMagicType(@Nullable EasyNpcParser.MagictypeContext node) {
        return getEnumValue(node, CharacterMagicType.class, CharacterMagicType.nomagic);
    }

    @Nonnull
    static CharacterMagicType getMagicType(@Nullable EasyNpcParser.MagictypeWithRunesContext node) {
        return getEnumValue(node, CharacterMagicType.class, CharacterMagicType.nomagic);
    }

    static int getQuestId(@Nullable EasyNpcParser.QuestIdContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for quest id not found.");
            return 0;
        }
        return getInteger(node.INT());
    }

    @Nullable
    static Skill getSkill(@Nullable EasyNpcParser.SkillContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for skill not found.");
            return null;
        }

        String skillName = node.getText();
        Skill skill = Skills.getInstance().getSkill(skillName);
        if (skill == null) {
            node.addErrorNode(node.getStart());
            LOGGER.warn("Skill name {} failed to map to a actual skill.", skillName);
        }
        return skill;
    }

    static Map<String, String> getItemDataOpt(@Nullable EasyNpcParser.ItemDataListContext node) {
        if (node == null) {
            return Collections.emptyMap();
        }
        return getItemData(node);
    }

    static Map<String, String> getItemData(@Nullable EasyNpcParser.ItemDataListContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item data not found.");
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();
        List<EasyNpcParser.ItemDataContext> dataValues = node.itemData();
        for (EasyNpcParser.ItemDataContext entry : dataValues) {
            getItemDataEntry(entry, result);
        }
        return result;
    }

    private static void getItemDataEntry(
            @Nullable EasyNpcParser.ItemDataContext node, @Nonnull Map<String, String> storage) {
        if (node == null) {
            LOGGER.warn("Expected node for item data entry not found.");
            return;
        }
        String key = getString(node.STRING(0));
        String value = getString(node.STRING(1));
        storage.put(key, value);
    }

    static int getItemQualityOpt(@Nullable EasyNpcParser.ItemQualityContext node) {
        if (node == null) {
            return 333;
        }
        return getInteger(node.INT());
    }

    static int getItemQuality(@Nullable EasyNpcParser.ItemQualityContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item quality entry not found.");
            return 333;
        }
        return getInteger(node.INT());
    }

    @Nonnull
    static ItemPositions getItemPosition(@Nullable EasyNpcParser.ItemPosContext node) {
        return getEnumValue(node, ItemPositions.class, ItemPositions.all);
    }

    private static int getColorComponent(@Nullable EasyNpcParser.ColorComponentContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for color component not found.");
            return 0;
        }

        return getInteger(node.INT());
    }

    @Nonnull
    static Location getLocation(@Nullable EasyNpcParser.LocationContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for location not found.");
            return new Location(0, 0, 0);
        }
        int x = getLocationComponent(node.locationComponent(0));
        int y = getLocationComponent(node.locationComponent(1));
        int z = getLocationComponent(node.locationComponent(2));
        return new Location(x, y, z);
    }

    private static int getLocationComponent(@Nullable EasyNpcParser.LocationComponentContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for location component not found.");
            return 0;
        }

        EasyNpcParser.UnopContext unaryOperator = node.unop();

        int value = getInteger(node.INT());
        if ((unaryOperator != null) && "-".equals(unaryOperator.getText())) {
            return -value;
        }
        return value;
    }

    @Nonnull
    private static String removeQuotes(@Nonnull String string) {
        if ((string.charAt(0) == '"') && (string.charAt(string.length() - 1) == '"')) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }
}
