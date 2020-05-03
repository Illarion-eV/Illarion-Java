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
package illarion.easynpc.docu;

import illarion.easynpc.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fredrik K
 */
public class DocuRoot implements DocuEntry {
    @Nonnull
    private static final DocuRoot INSTANCE = new DocuRoot();
    @Nonnull
    private final List<DocuEntry> types;

    private DocuRoot() {
        types = new ArrayList<>();
        DocuEntry comment = new DocuLeaf("NpcComment");
        types.add(comment);

        DocuNode basics = new DocuNode("NpcBasics");
        basics.addChild("Name");
        basics.addChild("Sex");
        basics.addChild("Race");
        basics.addChild("Position");
        basics.addChild("Direction");
        basics.addChild("Author");
        basics.addChild("Job");
        basics.addChild("Affiliation");
        basics.addChild("Language");
        basics.addChild("DefaultLang");
        basics.addChild("Lookat");
        basics.addChild("Use");
        basics.addChild("Confused");
        basics.addChild("AutoIntro");
        types.add(basics);

        DocuNode color = new DocuNode("NpcColors");
        color.addChild("Skin");
        color.addChild("Hair");
        types.add(color);

        DocuNode hair = new DocuNode("NpcHair");
        hair.addChild("Beard");
        hair.addChild("Hair");
        types.add(hair);

        DocuNode equipment = new DocuNode("NpcEquipment");
        equipment.addChild("Head");
        equipment.addChild("Chest");
        equipment.addChild("Coat");
        equipment.addChild("MainHand");
        equipment.addChild("SecHand");
        equipment.addChild("Hands");
        equipment.addChild("Trousers");
        equipment.addChild("Shoes");
        types.add(equipment);

        DocuNode talk = new DocuNode("NpcTalk", true);
        DocuNode conditions = new DocuNode("talk.TalkingLine.Conditions");
        conditions.addChild(new DocuLeaf("talk.conditions.Admin"));
        conditions.addChild(new DocuLeaf("talk.conditions.Attribute"));
        conditions.addChild(new DocuLeaf("talk.conditions.Chance"));
        conditions.addChild(new DocuLeaf("talk.conditions.Item"));
        conditions.addChild(new DocuLeaf("talk.conditions.Language"));
        conditions.addChild(new DocuLeaf("talk.conditions.Money"));
        conditions.addChild(new DocuLeaf("talk.conditions.Rank"));
        conditions.addChild(new DocuLeaf("talk.conditions.Number"));
        conditions.addChild(new DocuLeaf("talk.conditions.Queststatus"));
        conditions.addChild(new DocuLeaf("talk.conditions.Race"));
        conditions.addChild(new DocuLeaf("talk.conditions.Sex"));
        conditions.addChild(new DocuLeaf("talk.conditions.Skill"));
        conditions.addChild(new DocuLeaf("talk.conditions.State"));
        conditions.addChild(new DocuLeaf("talk.conditions.TalkMode"));
        conditions.addChild(new DocuLeaf("talk.conditions.Talkstate"));
        conditions.addChild(new DocuLeaf("talk.conditions.Town"));
        conditions.addChild(new DocuLeaf("talk.conditions.Trigger"));
        talk.addChild(conditions);

        DocuNode consequences = new DocuNode("talk.TalkingLine.Consequence");
        consequences.addChild(new DocuLeaf("talk.consequences.Arena"));
        consequences.addChild(new DocuLeaf("talk.consequences.Answer"));
        consequences.addChild(new DocuLeaf("talk.consequences.Attribute"));
        consequences.addChild(new DocuLeaf("talk.consequences.DeleteItem"));
        consequences.addChild(new DocuLeaf("talk.consequences.Inform"));
        consequences.addChild(new DocuLeaf("talk.consequences.Introduce"));
        consequences.addChild(new DocuLeaf("talk.consequences.Item"));
        consequences.addChild(new DocuLeaf("talk.consequences.Money"));
        consequences.addChild(new DocuLeaf("talk.consequences.Queststatus"));
        consequences.addChild(new DocuLeaf("talk.consequences.Rankpoints"));
        consequences.addChild(new DocuLeaf("talk.consequences.Repair"));
        consequences.addChild(new DocuLeaf("talk.consequences.Town"));
        consequences.addChild(new DocuLeaf("talk.consequences.Rune"));
        consequences.addChild(new DocuLeaf("talk.consequences.Skill"));
        consequences.addChild(new DocuLeaf("talk.consequences.State"));
        consequences.addChild(new DocuLeaf("talk.consequences.Talkstate"));
        consequences.addChild(new DocuLeaf("talk.consequences.Trade"));
        consequences.addChild(new DocuLeaf("talk.consequences.Gemcraft"));
        consequences.addChild(new DocuLeaf("talk.consequences.Treasure"));
        consequences.addChild(new DocuLeaf("talk.consequences.Warp"));
        talk.addChild(consequences);
        types.add(talk);

        DocuEntry empty = new DocuNode("NpcEmpty");
        types.add(empty);

        DocuEntry cycleText = new DocuLeaf("NpcCycleText");
        types.add(cycleText);

        DocuEntry walk = new DocuLeaf("NpcWalk");
        types.add(walk);

        DocuNode tradeComplex = new DocuNode("NpcTradeComplex");
        tradeComplex.addChild("Sell");
        tradeComplex.addChild("Buy.Primary");
        tradeComplex.addChild("Buy.Secondary");
        types.add(tradeComplex);

        DocuNode tradeSimple = new DocuNode("NpcTradeSimple");
        tradeSimple.addChild("Sell");
        tradeSimple.addChild("Buy.Primary");
        tradeSimple.addChild("Buy.Secondary");
        types.add(tradeSimple);

        DocuEntry tradeText = new DocuLeaf("NpcTradeText");
        types.add(tradeText);

        DocuEntry guardRange = new DocuLeaf("NpcGuardRange");
        types.add(guardRange);

        DocuEntry guardWarp = new DocuLeaf("NpcGuardWarpTarget");
        types.add(guardWarp);

        DocuEntry guardText = new DocuLeaf("NpcGuardText");
        types.add(guardText);
    }

    /**
     * Get the singleton instance of this parser.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static DocuRoot getInstance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public DocuEntry getChild(int index) {
        if ((index >= 0) && (index < types.size())) {
            return types.get(index);
        }
        throw new IndexOutOfBoundsException("Index is out of range.");
    }

    @Override
    public int getChildCount() {
        return types.size();
    }

    @Override
    public String getDescription() {
        return Lang.getMsg("illarion.easynpc.Parser.Docu.description");
    }

    @Nullable
    @Override
    public String getExample() {
        return null;
    }

    @Nullable
    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getTitle() {
        return Lang.getMsg("illarion.easynpc.Parser.Docu.title");
    }
}
