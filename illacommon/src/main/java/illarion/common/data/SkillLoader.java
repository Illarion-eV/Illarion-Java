/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.data;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class takes care for loading the skills from the XML file as required. Calling this function is required
 * before the skills and skill groups are used.
 * <p/>
 * This function blocks the execution of the current thread until loading the Skill is done for sure.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public class SkillLoader {
    /**
     * This value is turned {@code true} once the loading is finished.
     */
    private static boolean loadingFinished;

    /**
     * Load the skills from the XML file.
     */
    public static void load() {
        if (loadingFinished) {
            return;
        }

        synchronized (SkillLoader.class) {
            if (loadingFinished) {
                return;
            }
            final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            final InputStream skillXmlStream = ccl.getResourceAsStream("skills.xml");

            if (skillXmlStream == null) {
                throw new IllegalStateException("Skill XML was not found.");
            }

            try {
                final XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(skillXmlStream, "UTF-8");

                int currentTag = parser.nextTag();
                @Nullable SkillGroup currentGroup = null;
                while (currentTag != XmlPullParser.END_DOCUMENT) {
                    final String tagName = parser.getName();
                    if ("group".equals(tagName)) {
                        final int attribCount = parser.getAttributeCount();
                        String germanName = null;
                        String englishName = null;
                        for (int i = 0; i < attribCount; i++) {
                            final String attribName = parser.getAttributeName(i);
                            if ("german".equals(attribName)) {
                                germanName = parser.getAttributeValue(i);
                            } else if ("english".equals(attribName)) {
                                englishName = parser.getAttributeValue(i);
                            }
                        }
                        if ((germanName != null) && (englishName != null)) {
                            currentGroup = new SkillGroup(germanName, englishName);
                            SkillGroups.getInstance().addSkillGroup(currentGroup);
                        }
                    } else if ("skill".equals(tagName) && (currentGroup != null)) {
                        final int attribCount = parser.getAttributeCount();
                        String serverName = null;
                        String germanName = null;
                        String englishName = null;
                        int serverId = -1;
                        for (int i = 0; i < attribCount; i++) {
                            final String attribName = parser.getAttributeName(i);
                            if ("name".equals(attribName)) {
                                serverName = parser.getAttributeValue(i);
                            } else if ("german".equals(attribName)) {
                                germanName = parser.getAttributeValue(i);
                            } else if ("english".equals(attribName)) {
                                englishName = parser.getAttributeValue(i);
                            } else if ("id".equals(attribName)) {
                                serverId = Integer.parseInt(parser.getAttributeValue(i));
                            }
                        }
                        if ((germanName != null) && (englishName != null) && (serverName != null) && (serverId >= 0)) {
                            final Skill skill = new Skill(serverId, serverName, germanName, englishName, currentGroup);
                            Skills.getInstance().addSkill(skill);
                        }
                    }
                    currentTag = parser.nextTag();
                }
            } catch (@Nonnull final XmlPullParserException e) {
                // nothing
            } catch (@Nonnull final IOException e) {
                // nothing
            }
            loadingFinished = true;
        }
    }

    /**
     * Private constructor to block the creation of a instance.
     */
    private SkillLoader() {
        // nothing
    }
}
