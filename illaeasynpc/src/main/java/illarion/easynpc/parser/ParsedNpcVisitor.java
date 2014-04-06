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
import illarion.common.data.SkillLoader;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.data.Color;
import illarion.easynpc.data.EquipmentSlots;
import illarion.easynpc.data.Items;
import illarion.easynpc.grammar.EasyNpcBaseVisitor;
import illarion.easynpc.parsed.*;
import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.parsed.talk.conditions.*;
import illarion.easynpc.parsed.talk.consequences.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static illarion.easynpc.grammar.EasyNpcParser.*;
import static illarion.easynpc.parsed.ParsedGuardText.TextType.*;
import static illarion.easynpc.parsed.ParsedTradeText.TradeTextType.*;
import static illarion.easynpc.parser.Utils.*;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedNpcVisitor extends EasyNpcBaseVisitor<ParsedNpcVisitor> {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsedNpcVisitor.class);
    @Nonnull
    private final ParsedNpc npc = new ParsedNpc();

    static {
        SkillLoader.load();
    }

    @Override
    public ParsedNpcVisitor visitBasicConfiguration(@NotNull BasicConfigurationContext ctx) {
        Token startToken = ctx.getStart();
        switch (startToken.getText()) {
            case "affiliation":
                npc.setAffiliation(getTown(ctx.getRuleContext(TownContext.class, 0)));
                break;
            case "author":
                npc.addAuthor(getString(ctx.STRING()));
                break;
            case "autointroduce":
                npc.setAutoIntroduce(getBoolean(ctx.BOOLEAN()));
                break;
            case "defaultLanguage":
                npc.setDefaultLanguage(getCharacterLanguage(ctx.charLanguage()));
                break;
            case "direction":
                npc.setNpcDir(getDirection(ctx.direction()));
                break;
            case "job":
                npc.setJob(getString(ctx.STRING()));
                break;
            case "language":
                npc.addLanguage(getCharacterLanguage(ctx.charLanguage()));
                break;
            case "lookatDE":
                npc.setGermanLookAt(getString(ctx.STRING()));
                break;
            case "lookatUS":
                npc.setEnglishLookAt(getString(ctx.STRING()));
                break;
            case "name":
                npc.setNpcName(getString(ctx.STRING()));
                break;
            case "position":
                npc.setNpcPos(getLocation(ctx.location()));
                break;
            case "race":
                npc.setNpcRace(getRace(ctx.race()));
                break;
            case "sex":
                npc.setNpcSex(getSex(ctx.gender()));
                break;
            case "useMsgDE":
                npc.setGermanUse(getString(ctx.STRING()));
                break;
            case "useMsgUS":
                npc.setEnglishUse(getString(ctx.STRING()));
                break;
            case "wrongLangDE":
                npc.setGermanWrongLang(getString(ctx.STRING()));
                break;
            case "wrongLangUS":
                npc.setEnglishWrongLang(getString(ctx.STRING()));
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown basic configuration key: {}", startToken.getText());
        }
        return super.visitBasicConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitColorConfiguration(@NotNull ColorConfigurationContext ctx) {
        Token startToken = ctx.getStart();

        Color color = getColor(ctx.color());
        switch (startToken.getText()) {
            case "colorHair":
                npc.addNpcData(new ParsedColors(ParsedColors.ColorTarget.Hair, color));
                break;
            case "colorSkin":
                npc.addNpcData(new ParsedColors(ParsedColors.ColorTarget.Skin, color));
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown color configuration key: {}", startToken.getText());
        }
        return super.visitColorConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitEquipmentConfiguration(@NotNull EquipmentConfigurationContext ctx) {
        Token startToken = ctx.getStart();

        Items item = getItem(ctx.itemId());
        if (item == null) {
            LOGGER.warn("Failed to match item id for equipment slot: {}", startToken.getText());
            return defaultResult();
        }
        switch (startToken.getText()) {
            case "itemChest":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.chest, item));
                break;
            case "itemCoat":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.coat, item));
                break;
            case "itemHands":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.hands, item));
                break;
            case "itemHead":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.head, item));
                break;
            case "itemMainHand":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.mainHand, item));
                break;
            case "itemSecondHand":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.secondHand, item));
                break;
            case "itemShoes":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.feet, item));
                break;
            case "itemTrousers":
                npc.addNpcData(new ParsedEquipment(EquipmentSlots.trousers, item));
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown equipment configuration key: {}", startToken.getText());
        }
        return super.visitEquipmentConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitGuardConfiguration(@NotNull GuardConfigurationContext ctx) {
        Token startToken = ctx.getStart();

        switch (startToken.getText()) {
            case "guardRange":
                int north = getInteger(ctx.INT(0));
                int south = getInteger(ctx.INT(1));
                int west = getInteger(ctx.INT(2));
                int east = getInteger(ctx.INT(3));
                npc.addNpcData(new ParsedGuardRange(north, south, west, east));
                break;
            case "guardWarpTarget":
                npc.addNpcData(new ParsedGuardWarpTarget(getLocation(ctx.location())));
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown guard configuration key: {}", startToken.getText());
        }
        return super.visitGuardConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitHairConfiguration(@NotNull HairConfigurationContext ctx) {
        Token startToken = ctx.getStart();

        int id = getInteger(ctx.INT());
        switch (startToken.getText()) {
            case "hairID":
                npc.addNpcData(new ParsedHair(ParsedHair.HairType.Hair, id));
                break;
            case "beardID":
                npc.addNpcData(new ParsedHair(ParsedHair.HairType.Beard, id));
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown hair configuration key: {}", startToken.getText());
        }
        return super.visitHairConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitTraderComplexConfiguration(@NotNull TraderComplexConfigurationContext ctx) {
        Token startToken = ctx.getStart();
        AbstractParsedTrade.TradeMode tradeMode;
        switch (startToken.getText()) {
            case "sellItem":
                tradeMode = AbstractParsedTrade.TradeMode.selling;
                break;
            case "buyPrimaryItem":
                tradeMode = AbstractParsedTrade.TradeMode.buyingPrimary;
                break;
            case "buySecondaryItem":
                tradeMode = AbstractParsedTrade.TradeMode.buyingSecondary;
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown complex trade configuration key: {}", startToken.getText());
                return super.visitTraderComplexConfiguration(ctx);
        }

        Map<String, String> data = new HashMap<>();

        Items item = getItem(ctx.traderComplexItemId());
        if (item == null) {
            ctx.addErrorNode(ctx.getStart());
            LOGGER.warn("Failed to match item id for complex trade entry.");
            return super.visitTraderComplexConfiguration(ctx);
        }
        int itemId = item.getItemId();

        String textDe = null;
        String textEn = null;
        int price = 0;
        int stackSize = 0;
        int quality = 0;
        for (TraderComplexEntryContext entry : ctx.traderComplexEntry()) {
            switch (entry.getStart().getText()) {
                case "de":
                    textDe = getString(entry.STRING());
                    break;
                case "en":
                    textEn = getString(entry.STRING());
                    break;
                case "price":
                    price = getInteger(entry.INT());
                    break;
                case "stack":
                    stackSize = getInteger(entry.INT());
                    break;
                case "quality":
                    quality = getItemQuality(entry.itemQuality());
                    break;
                case "data":
                    data.putAll(getItemData(entry.itemDataList()));
                    break;
                default:
                    ctx.addErrorNode(entry.getStart());
                    LOGGER.warn("Unknown key for complex item entry: {}", entry.getStart().getText());
                    break;
            }
        }

        npc.addNpcData(new ParsedTradeComplex(tradeMode, itemId, textDe, textEn, price, stackSize, quality,
                                              new ParsedItemData(data)));

        return super.visitTraderComplexConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitTraderSimpleConfiguration(
            @NotNull TraderSimpleConfigurationContext ctx) {
        Token startToken = ctx.getStart();
        AbstractParsedTrade.TradeMode tradeMode;
        switch (startToken.getText()) {
            case "sellItems":
                tradeMode = AbstractParsedTrade.TradeMode.selling;
                break;
            case "buyPrimaryItems":
                tradeMode = AbstractParsedTrade.TradeMode.buyingPrimary;
                break;
            case "buySecondaryItems":
                tradeMode = AbstractParsedTrade.TradeMode.buyingSecondary;
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown simple trade configuration key: {}", startToken.getText());
                return super.visitTraderSimpleConfiguration(ctx);
        }

        List<ItemIdContext> itemIds = ctx.itemId();
        List<Integer> ids = new ArrayList<>(itemIds.size());
        for (ItemIdContext itemId : itemIds) {
            Items item = getItem(itemId);
            if (item == null) {
                ctx.addErrorNode(itemId.getStart());
            } else {
                ids.add(item.getItemId());
            }
        }
        npc.addNpcData(new ParsedTradeSimple(tradeMode, ids));
        return super.visitTraderSimpleConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitWalkConfiguration(@NotNull WalkConfigurationContext ctx) {
        Token startToken = ctx.getStart();
        switch (startToken.getText()) {
            case "radius":
                npc.addNpcData(new ParsedWalkingRadius(getInteger(ctx.INT())));
                break;
            default:
                ctx.addErrorNode(startToken);
                LOGGER.warn("Unknown walking configuration key: {}", startToken.getText());
        }
        return super.visitWalkConfiguration(ctx);
    }

    @Nullable
    private ParsedTalk currentTalkingLine;

    @Override
    public ParsedNpcVisitor visitTalkCommand(@NotNull TalkCommandContext ctx) {
        currentTalkingLine = new ParsedTalk();
        ParsedNpcVisitor result = super.visitTalkCommand(ctx);
        npc.addNpcData(currentTalkingLine);
        currentTalkingLine = null;
        return result;
    }

    @Override
    public ParsedNpcVisitor visitTrigger(@NotNull TriggerContext ctx) {
        if (currentTalkingLine == null) {
            LOGGER.error("Visiting trigger while there is no active talking line.");
        } else {
            currentTalkingLine.addCondition(new ConditionTrigger(getString(ctx.STRING())));
        }
        return super.visitTrigger(ctx);
    }

    @Override
    public ParsedNpcVisitor visitCondition(@NotNull ConditionContext ctx) {
        if (currentTalkingLine == null) {
            LOGGER.error("Visiting condition while there is no active talking line.");
            return super.visitCondition(ctx);
        }
        switch (ctx.getStart().getText()) {
            case "isAdmin":
                currentTalkingLine.addCondition(new ConditionAdmin());
                break;
            case "attrib":
                currentTalkingLine.addCondition(
                        new ConditionAttrib(getAttribute(ctx.attribute()), getOperator(ctx.compare()),
                                            getAdvancedNumber(ctx.advancedNumber()))
                );
                break;
            case "chance":
                TerminalNode intNode = ctx.INT();
                if (intNode != null) {
                    currentTalkingLine.addCondition(new ConditionChance(getInteger(intNode)));
                } else {
                    currentTalkingLine.addCondition(new ConditionChance(getFloat(ctx.FLOAT())));
                }
                break;
            case "item":
                Items item = getItem(ctx.itemId());
                if (item != null) {
                    currentTalkingLine.addCondition(
                            new ConditionItem(item, getItemPosition(ctx.itemPos()), getOperator(ctx.compare()),
                                              getAdvancedNumber(ctx.advancedNumber()),
                                              new ParsedItemData(getItemDataOpt(ctx.itemDataList())))
                    );
                }
                break;
            case "magictype":
                currentTalkingLine.addCondition(new ConditionMagicType(getMagicType(ctx.magictype())));
                break;
            case "money":
                currentTalkingLine.addCondition(
                        new ConditionMoney(getOperator(ctx.compare()), getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "%NUMBER":
                currentTalkingLine.addCondition(new ConditionNumber(getOperator(ctx.compare()), getInteger(ctx.INT())));
                break;
            case "queststatus":
                currentTalkingLine.addCondition(
                        new ConditionQueststatus(getQuestId(ctx.questId()), getOperator(ctx.compare()),
                                                 getAdvancedNumber(ctx.advancedNumber()))
                );
                break;
            case "race":
                currentTalkingLine.addCondition(new ConditionRace(getRace(ctx.race())));
                break;
            case "rank":
                currentTalkingLine.addCondition(
                        new ConditionRank(getOperator(ctx.compare()), getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "sex":
                currentTalkingLine.addCondition(new ConditionSex(getSex(ctx.gender())));
                break;
            case "skill":
                Skill skill = getSkill(ctx.skill());
                if (skill != null) {
                    currentTalkingLine.addCondition(new ConditionSkill(skill, getOperator(ctx.compare()),
                                                                       getAdvancedNumber(ctx.advancedNumber())));
                }
                break;
            case "state":
                currentTalkingLine.addCondition(
                        new ConditionState(getOperator(ctx.compare()), getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "talkMode":
                currentTalkingLine.addCondition(new ConditionTalkMode(getTalkMode(ctx.talkMode())));
                break;
            case "town":
                currentTalkingLine.addCondition(new ConditionTown(getTown(ctx.town())));
                break;
            default:
                return super.visitCondition(ctx);
        }
        return super.visitCondition(ctx);
    }

    @Override
    public ParsedNpcVisitor visitLanguage(@NotNull LanguageContext ctx) {
        if (currentTalkingLine != null) {
            currentTalkingLine.addCondition(new ConditionLanguage(getPlayerLanguage(ctx)));
        }
        return super.visitLanguage(ctx);
    }

    @Override
    public ParsedNpcVisitor visitTalkstateGet(@NotNull TalkstateGetContext ctx) {
        if (currentTalkingLine == null) {
            LOGGER.error("Visiting talk state get while there is no active talking line.");
        } else {
            currentTalkingLine.addCondition(new ConditionTalkstate(getTalkState(ctx)));
        }
        return super.visitTalkstateGet(ctx);
    }

    @Override
    public ParsedNpcVisitor visitAnswer(@NotNull AnswerContext ctx) {
        if (currentTalkingLine == null) {
            LOGGER.error("Visiting consequence while there is no active talking line.");
        } else {
            currentTalkingLine.addConsequence(new ConsequenceAnswer(getString(ctx.STRING())));
        }
        return super.visitAnswer(ctx);
    }

    @Override
    public ParsedNpcVisitor visitConsequence(@NotNull ConsequenceContext ctx) {
        if (currentTalkingLine == null) {
            LOGGER.error("Visiting consequence while there is no active talking line.");
            return defaultResult();
        }
        switch (ctx.getStart().getText()) {
            case "arena":
                currentTalkingLine.addConsequence(new ConsequenceArena(getArenaTask(ctx.arenaTask())));
                break;
            case "attrib":
                currentTalkingLine.addConsequence(
                        new ConsequenceAttribute(getAttribute(ctx.attribute()), getOperator(ctx.set()),
                                                 getAdvancedNumber(ctx.advancedNumber()))
                );
                break;
            case "deleteItem":
                Items item = getItem(ctx.itemId());
                if (item != null) {
                    currentTalkingLine.addConsequence(
                            new ConsequenceDeleteItem(item, getAdvancedNumber(ctx.advancedNumber()),
                                                      new ParsedItemData(getItemDataOpt(ctx.itemDataList())))
                    );
                } else {
                    ctx.addErrorNode(ctx.itemId().getStart());
                }
                break;
            case "gemcraft":
                currentTalkingLine.addConsequence(new ConsequenceGemcraft());
                break;
            case "inform":
                currentTalkingLine.addConsequence(new ConsequenceInform(getString(ctx.STRING())));
                break;
            case "introduce":
                currentTalkingLine.addConsequence(new ConsequenceIntroduce());
                break;
            case "item":
                item = getItem(ctx.itemId());
                if (item != null) {
                    currentTalkingLine.addConsequence(new ConsequenceItem(item, getAdvancedNumber(ctx.advancedNumber()),
                                                                          getItemQualityOpt(ctx.itemQuality()),
                                                                          new ParsedItemData(
                                                                                  getItemDataOpt(ctx.itemDataList()))
                    ));
                } else {
                    ctx.addErrorNode(ctx.itemId().getStart());
                }
                break;
            case "money":
                currentTalkingLine.addConsequence(
                        new ConsequenceMoney(getOperator(ctx.set()), getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "queststatus":
                currentTalkingLine.addConsequence(
                        new ConsequenceQueststatus(getQuestId(ctx.questId()), getOperator(ctx.set()),
                                                   getAdvancedNumber(ctx.advancedNumber()))
                );
                break;
            case "rankpoints":
                currentTalkingLine.addConsequence(
                        new ConsequenceRankpoints(getOperator(ctx.set()), getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "repair":
                currentTalkingLine.addConsequence(new ConsequenceRepair());
                break;
            case "rune":
                currentTalkingLine.addConsequence(
                        new ConsequenceRune(getMagicType(ctx.magictypeWithRunes()), getInteger(ctx.INT())));
                break;
            case "skill":
                currentTalkingLine.addConsequence(new ConsequenceSkill(getSkill(ctx.skill()), getOperator(ctx.set()),
                                                                       getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "state":
                currentTalkingLine.addConsequence(
                        new ConsequenceState(getOperator(ctx.set()), getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "town":
                currentTalkingLine.addConsequence(new ConsequenceTown(getTown(ctx.town())));
                break;
            case "trade":
                currentTalkingLine.addConsequence(new ConsequenceTrade());
                break;
            case "treasure":
                currentTalkingLine.addConsequence(new ConsequenceTreasure(getAdvancedNumber(ctx.advancedNumber())));
                break;
            case "warp":
                currentTalkingLine.addConsequence(new ConsequenceWarp(getLocation(ctx.location())));
                break;
            default:
                return super.visitConsequence(ctx);
        }
        return super.visitConsequence(ctx);
    }

    @Override
    public ParsedNpcVisitor visitTalkstateSet(@NotNull TalkstateSetContext ctx) {
        if (currentTalkingLine == null) {
            LOGGER.error("Visiting consequence while there is no active talking line.");
            return defaultResult();
        }

        currentTalkingLine.addConsequence(new ConsequenceTalkstate(getTalkState(ctx)));
        return defaultResult();
    }

    @Override
    public ParsedNpcVisitor visitTextConfiguration(@NotNull TextConfigurationContext ctx) {
        TextKeyContext textKeyContext = ctx.textKey();
        if (textKeyContext == null) {
            ctx.addErrorNode(ctx.getStart());
            LOGGER.warn("Missing text key for text configuration.");
            return defaultResult();
        }

        String german = getString(ctx.STRING(0));
        String english = getString(ctx.STRING(1));
        switch (textKeyContext.getStart().getText()) {
            case "cycletext":
                npc.addNpcData(new ParsedCycleText(german, english));
                break;
            case "hitPlayerMsg":
                npc.addNpcData(new ParsedGuardText(HitPlayer, german, english));
                break;
            case "tradeFinishedMsg":
                npc.addNpcData(new ParsedTradeText(TradingCanceled, german, english));
                break;
            case "tradeFinishedWithoutTradingMsg":
                npc.addNpcData(new ParsedTradeText(TradingCanceledWithoutTrade, german, english));
                break;
            case "tradeNotEnoughMoneyMsg":
                npc.addNpcData(new ParsedTradeText(NoMoney, german, english));
                break;
            case "tradeWrongItemMsg":
                npc.addNpcData(new ParsedTradeText(WrongItem, german, english));
                break;
            case "warpedMonsterMsg":
                npc.addNpcData(new ParsedGuardText(WarpedMonster, german, english));
                break;
            case "warpedPlayerMsg":
                npc.addNpcData(new ParsedGuardText(WarpedPlayer, german, english));
                break;
            default:
                ctx.addErrorNode(textKeyContext.getStart());
                LOGGER.warn("Unknown basic text key: {}", textKeyContext.getText());
        }
        return super.visitTextConfiguration(ctx);
    }

    @Override
    public ParsedNpcVisitor visitErrorNode(@NotNull ErrorNode node) {
        npc.addError(node.getSymbol().getLine(), node.getSymbol().getCharPositionInLine(), node.getText());
        return defaultResult();
    }

    @Nonnull
    public ParsedNpc getParsedNpc() {
        return npc;
    }
}
