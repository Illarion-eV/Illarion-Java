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
package illarion.easynpc.writer;

/**
 * This class is used to build the SQL Insert statement needed to add the NPC to
 * the Database.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SQLBuilder {
    /**
     * This is the format string used to build the query.
     */
    private static final String queryFormat =
            "INSERT INTO \"npc\" (\"npc_type\", \"npc_posx\", \"npc_posy\", \"npc_posz\", \"npc_faceto\", \"npc_name\", \"npc_script\", \"npc_sex\", \"npc_hair\", \"npc_beard\", \"npc_hairred\", \"npc_hairgreen\", \"npc_hairblue\", \"npc_skinred\", \"npc_skingreen\", \"npc_skinblue\") \n" +
                    "VALUES (%1$s, %2$s, %3$s, %4$s, %5$s, '%6$s', %7$s, %8$s, %9$s, %10$s, %11$s, %12$s, %13$s, %14$s, %15$s, %16$s);";

    private int npcBeard;
    private int npcFaceTo;
    private int npcHair;
    private int npcHairBlue = 255;
    private int npcHairGreen = 255;
    private int npcHairRed = 255;
    private String npcName = "no name";
    private int npcPosX;
    private int npcPosY;
    private int npcPosZ;
    private String npcScript = "null";
    private int npcSex;
    private int npcSkinBlue = 255;
    private int npcSkinGreen = 255;
    private int npcSkinRed = 255;
    private int npcType;

    /**
     * Reducing the visibility of the default constructor.
     */
    SQLBuilder() {
        // nothing
    }

    /**
     * Set the beard ID of this NPC.
     *
     * @param newNpcBeard the new ID for the beard of this NPC
     */
    public void setNpcBeard(int newNpcBeard) {
        npcBeard = newNpcBeard;
    }

    /**
     * Set the face to value of the NPC.
     *
     * @param newNpcFaceTo the new face to value for the NPC
     */
    public void setNpcFaceTo(int newNpcFaceTo) {
        npcFaceTo = newNpcFaceTo;
    }

    /**
     * Set the hair ID of this NPC.
     *
     * @param newNpcHair the new ID for the hair of this NPC
     */
    public void setNpcHair(int newNpcHair) {
        npcHair = newNpcHair;
    }

    /**
     * Set the hair color of this NPC.
     *
     * @param red the red share of the hair color
     * @param green the green share of the hair color
     * @param blue the blue share of the hair color
     */
    public void setNpcHairColor(int red, int green, int blue) {
        npcHairRed = red;
        npcHairGreen = green;
        npcHairBlue = blue;
    }

    /**
     * Set the name of this NPC.
     *
     * @param newNpcName the new Name of this NPC
     */
    public void setNpcName(String newNpcName) {
        npcName = newNpcName;
    }

    /**
     * Set the X coordinate of the position of this NPC.
     *
     * @param newNpcPosX the x coordinate of the NPC position
     */
    public void setNpcPosX(int newNpcPosX) {
        npcPosX = newNpcPosX;
    }

    /**
     * Set the Y coordinate of the position of this NPC.
     *
     * @param newNpcPosY the y coordinate of the NPC position
     */
    public void setNpcPosY(int newNpcPosY) {
        npcPosY = newNpcPosY;
    }

    /**
     * Set the Z coordinate of the position of this NPC.
     *
     * @param newNpcPosZ the z coordinate of the NPC position
     */
    public void setNpcPosZ(int newNpcPosZ) {
        npcPosZ = newNpcPosZ;
    }

    /**
     * Set the script of this NPC.
     *
     * @param newNpcScript the new script of this NPC
     */
    public void setNpcScript(String newNpcScript) {
        npcScript = newNpcScript;
    }

    /**
     * Set the sex of this NPC.
     *
     * @param newNpcSex the new sex id of this NPC
     */
    public void setNpcSex(int newNpcSex) {
        npcSex = newNpcSex;
    }

    /**
     * Set the skin color of this NPC.
     *
     * @param red the red share of the skin color
     * @param green the green share of the skin color
     * @param blue the blue share of the skin color
     */
    public void setNpcSkinColor(int red, int green, int blue) {
        npcSkinRed = red;
        npcSkinGreen = green;
        npcSkinBlue = blue;
    }

    /**
     * Set the NPC type of this NPC.
     *
     * @param newNpcType the new type value for this NPC.
     */
    public void setNpcType(int newNpcType) {
        npcType = newNpcType;
    }

    /**
     * Generate the SQL query and return it.
     *
     * @return the generated SQL query.
     */
    String getSQL() {
        String npcScriptReal = npcScript;
        if (!"null".equals(npcScript)) {
            npcScriptReal = '\'' + npcScriptReal + '\'';
        }
        return String
                .format(queryFormat, Integer.toString(npcType), Integer.toString(npcPosX), Integer.toString(npcPosY),
                        Integer.toString(npcPosZ), Integer.toString(npcFaceTo), npcName, npcScriptReal,
                        Integer.toString(npcSex), Integer.toString(npcHair), Integer.toString(npcBeard),
                        Integer.toString(npcHairRed), Integer.toString(npcHairGreen), Integer.toString(npcHairBlue),
                        Integer.toString(npcSkinRed), Integer.toString(npcSkinGreen), Integer.toString(npcSkinBlue));
    }
}
