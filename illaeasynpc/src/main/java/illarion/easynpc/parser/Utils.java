/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import illarion.common.types.ServerCoordinate;
import illarion.easynpc.data.*;
import illarion.easynpc.grammar.EasyNpcParser.*;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.consequences.ConsequenceArena.Task;
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
    @Nonnull
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

    @Nonnull
    static Task getArenaTask(@Nullable ArenaTaskContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for arena task not found.");
            return Task.RequestMonster;
        }

        switch (node.getStart().getText()) {
            case "requestMonster":
                return Task.RequestMonster;
            case "getStats":
                return Task.ShowStatistics;
            case "getRanking":
                return Task.ShowRanking;
            default:
                LOGGER.warn("Failed to resolve {} to arena task.", node.getStart().getText());
                return Task.RequestMonster;
        }
    }

    @Nonnull
    static CharacterRace getRace(@Nullable RaceContext node) {
        return getEnumValue(node, CharacterRace.class, CharacterRace.human);
    }

    @Nonnull
    static CharacterSex getSex(@Nullable GenderContext node) {
        return getEnumValue(node, CharacterSex.class, CharacterSex.male);
    }

    @Nonnull
    static CharacterLanguage getCharacterLanguage(@Nullable CharLanguageContext node) {
        return getEnumValue(node, CharacterLanguage.class, CharacterLanguage.common);
    }

    @Nonnull
    static PlayerLanguage getPlayerLanguage(@Nullable LanguageContext node) {
        return getEnumValue(node, PlayerLanguage.class, PlayerLanguage.english);
    }

    @Nonnull
    static NpcBaseState getTalkState(@Nullable TalkstateGetContext node) {
        return getEnumValue(node, NpcBaseState.class, NpcBaseState.idle);
    }

    @Nonnull
    static NpcBaseStateToggle getTalkState(@Nullable TalkstateSetContext node) {
        return getEnumValue(node, NpcBaseStateToggle.class, NpcBaseStateToggle.begin);
    }

    @Nonnull
    static CharacterDirection getDirection(@Nullable DirectionContext node) {
        return getEnumValue(node, CharacterDirection.class, CharacterDirection.north);
    }

    @Nonnull
    static Towns getTown(@Nullable TownContext node) {
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
    static Color getColor(@Nullable ColorContext node) {
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
    static CharacterAttribute getAttribute(@Nullable AttributeContext node) {
        return getEnumValue(node, CharacterAttribute.class, CharacterAttribute.strength);
    }

    @Nonnull
    static CompareOperators getOperator(@Nullable CompareContext node) {
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
    static CalculationOperators getOperator(@Nullable SetContext node) {
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
    static AdvancedNumber getAdvancedNumber(@Nullable AdvancedNumberContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for advanced number not found.");
            return new AdvancedNumber(0);
        }
        AdvancedNumberExpressionContext expressionCtx = node.advancedNumberExpression();
        if (expressionCtx != null) {
            AdvancedNumberExpressionBodyContext bodyCtx = expressionCtx.advancedNumberExpressionBody();
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
    static Items getItem(@Nullable TraderComplexItemIdContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item id not found.");
            return null;
        }

        return getItem(node.itemId());
    }

    @Nullable
    static Items getItem(@Nullable ItemIdContext node) {
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
    static CharacterMagicType getMagicType(@Nullable MagictypeContext node) {
        return getEnumValue(node, CharacterMagicType.class, CharacterMagicType.nomagic);
    }

    @Nonnull
    static CharacterMagicType getMagicType(@Nullable MagictypeWithRunesContext node) {
        return getEnumValue(node, CharacterMagicType.class, CharacterMagicType.nomagic);
    }

    static int getQuestId(@Nullable QuestIdContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for quest id not found.");
            return 0;
        }
        return getInteger(node.INT());
    }

    static int getMonsterId(@Nullable MonsterIdContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item id not found.");
            return 0;
        }

        return getInteger(node.INT());
    }

    static int getMonsterCount(@Nullable MonsterCountContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item id not found.");
            return 0;
        }

        return getInteger(node.INT());
    }

    static int getRadius(@Nullable RadiusContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item id not found.");
            return 0;
        }

        return getInteger(node.INT());
    }

    @Nullable
    static Skill getSkill(@Nullable SkillContext node) {
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

    @Nonnull
    static Map<String, String> getItemDataOpt(@Nullable ItemDataListContext node) {
        if (node == null) {
            return Collections.emptyMap();
        }
        return getItemData(node);
    }

    @Nonnull
    static Map<String, String> getItemData(@Nullable ItemDataListContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item data not found.");
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();
        List<ItemDataContext> dataValues = node.itemData();
        for (ItemDataContext entry : dataValues) {
            getItemDataEntry(entry, result);
        }
        return result;
    }

    private static void getItemDataEntry(
            @Nullable ItemDataContext node, @Nonnull Map<String, String> storage) {
        if (node == null) {
            LOGGER.warn("Expected node for item data entry not found.");
            return;
        }
        String key = getString(node.STRING(0));
        String value = getString(node.STRING(1));
        storage.put(key, value);
    }

    static int getItemQualityOpt(@Nullable ItemQualityContext node) {
        if (node == null) {
            return 333;
        }
        return getInteger(node.INT());
    }

    static int getItemQuality(@Nullable ItemQualityContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for item quality entry not found.");
            return 333;
        }
        return getInteger(node.INT());
    }

    @Nonnull
    static ItemPositions getItemPosition(@Nullable ItemPosContext node) {
        return getEnumValue(node, ItemPositions.class, ItemPositions.all);
    }

    private static int getColorComponent(@Nullable ColorComponentContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for color component not found.");
            return 0;
        }

        return getInteger(node.INT());
    }

    @Nonnull
    static ServerCoordinate getLocation(@Nullable LocationContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for location not found.");
            return new ServerCoordinate(0, 0, 0);
        }
        int x = getLocationComponent(node.locationComponent(0));
        int y = getLocationComponent(node.locationComponent(1));
        int z = getLocationComponent(node.locationComponent(2));
        return new ServerCoordinate(x, y, z);
    }

    private static int getLocationComponent(@Nullable LocationComponentContext node) {
        if (node == null) {
            LOGGER.warn("Expected node for location component not found.");
            return 0;
        }

        UnopContext unaryOperator = node.unop();

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
