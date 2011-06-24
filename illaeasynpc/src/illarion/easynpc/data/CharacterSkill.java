/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.data;

/**
 * This enumerator contains all skills usable with the easyNPC language and all
 * required values to translate them as needed to a LUA script.
 * 
 * @author Martin Karing
 * @since 1.00
 */
@SuppressWarnings("nls")
public enum CharacterSkill {
    /**
     * Skill: alchemy - Group: druid
     */
    alchemy(6, "alchemy"),
    /**
     * Skill: ancient language - Group: languages
     */
    ancientLanguage(1, "ancient language"),
    /**
     * Skill: baking - Group: crafting
     */
    baking(2, "baking"),
    /**
     * Skill: carpentry - Group: crafting
     */
    carpentry(2, "carpentry"),
    /**
     * Skill: common language - Group: languages
     */
    commonLanguage(1, "common language"),
    /**
     * Skill: commotio - Group: magic
     */
    commotio(3, "commotio"),
    /**
     * Skill: concussion weapons - Group: fighting
     */
    concussionWeapons(5, "concussion weapons"),
    /**
     * Skill: desicio - Group: magic
     */
    desicio(3, "desicio"),
    /**
     * Skill: distance weapons - Group: fighting
     */
    distanceWeapons(5, "distance weapons"),
    /**
     * Skill: dodge - Group: fighting
     */
    dodge(5, "dodge"),
    /**
     * Skill: dwarf language - Group: languages
     */
    dwarfLanguage(1, "dwarf language"),
    /**
     * Skill: elf language - Group: languages
     */
    elfLanguage(1, "elf language"),
    /**
     * Skill: fairy language - Group: languages
     */
    fairyLanguage(1, "fairy language"),
    /**
     * Skill: fireing bricks - Group: crafting
     */
    fireingBricks(2, "fireing bricks"),
    /**
     * Skill: fishing - Group: crafting
     */
    fishing(2, "fishing"),
    /**
     * Skill: flute - Group: bard
     */
    flute(8, "flute"),
    /**
     * Skill: gemcutting - Group: crafting
     */
    gemcutting(2, "gemcutting"),
    /**
     * Skill: glass blowing - Group: crafting
     */
    glassBlowing(2, "glass blowing"),
    /**
     * Skill: gnome language - Group: languages
     */
    gnomeLanguage(1, "gnome language"),
    /**
     * Skill: goblin language - Group: languages
     */
    gobalinLanguage(1, "goblin language"),
    /**
     * Skill: goldsmithing - Group: crafting
     */
    goldsmithing(2, "goldsmithing"),
    /**
     * Skill: halfling language - Group: languages
     */
    halflingLanuage(1, "halfling language"),
    /**
     * Skill: harp - Group: bard
     */
    harp(8, "harp"),
    /**
     * Skill: herb lore - Group: crafting
     */
    herbLore(2, "herb lore"),
    /**
     * Skill: horn - Group: bard
     */
    horn(8, "horn"),
    /**
     * Skill: human language - Group: languages
     */
    humanLanguage(1, "human language"),
    /**
     * Skill: library research - Group: magic
     */
    libraryResearch(3, "library research"),
    /**
     * Skill: lizard language - Group: languages
     */
    lizardLanguage(1, "lizard language"),
    /**
     * Skill: lumberjacking - Group: crafting
     */
    lumberjacking(2, "lumberjacking"),
    /**
     * Skill: lute - Group: bard
     */
    lute(8, "lute"),
    /**
     * Skill: magic resistance - Group: magic
     */
    magicResistance(3, "magic resistance"),
    /**
     * Skill: mining - Group: crafting
     */
    mining(2, "mining"),
    /**
     * Skill: orc language - Group: languages
     */
    orcLanguage(1, "orc language"),
    /**
     * Skill: parry - Group: fighting
     */
    parry(5, "parry"),
    /**
     * Skill: peasantry - Group: crafting
     */
    peasantry(2, "peasantry"),
    /**
     * Skill: pervestigatio - Group: magic
     */
    pervestigatio(3, "pervestigatio"),
    /**
     * Skill: poisoning - Group: fighting
     */
    poisoning(5, "poisoning"),
    /**
     * Skill: puncture weapons - Group: fighting
     */
    punctureWeapons(5, "puncture weapons"),
    /**
     * Skill: slashing weapons - Group: fighting
     */
    slashingWeapons(5, "slashing weapons"),
    /**
     * Skill: smithing - Group: crafting
     */
    smithing(2, "smithing"),
    /**
     * Skill: tactics - Group: fighting
     */
    tactics(5, "tactics"),
    /**
     * Skill: tailoring - Group: crafting
     */
    tailoring(2, "tailoring"),
    /**
     * Skill: transformo - Group: magic
     */
    transformo(3, "transformo"),
    /**
     * Skill: transfreto - Group: magic
     */
    transfreto(3, "transfreto"),
    /**
     * Skill: wrestling - Group: fighting
     */
    wrestling(5, "wrestling");

    /**
     * The group index of this skill.
     */
    private final int skillGroup;

    /**
     * The name of this skill to be used in the LUA and the easyNPC script.
     */
    private final String skillName;

    /**
     * Enumerator constructor, that stores the required data.
     * 
     * @param group the group index of this skill
     * @param name the name of this skill
     */
    private CharacterSkill(final int group, final String name) {
        skillGroup = group;
        skillName = name;
    }

    /**
     * Get the group index of this skill.
     * 
     * @return the group index of this skill
     */
    public int getSkillGroup() {
        return skillGroup;
    }

    /**
     * Get the name of this skill required for the LUA and the easyNPC script.
     * 
     * @return the name of this skill
     */
    public String getSkillName() {
        return skillName;
    }
}
