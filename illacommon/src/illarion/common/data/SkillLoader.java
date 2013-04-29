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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class takes care for loading the skills from the XML file as required.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
class SkillLoader {
    /**
     * This value is turned true once the loading is started.
     */
    private static boolean loadingStarted;

    /**
     * Load the skills from the XML file.
     */
    static synchronized void load() {
        if (loadingStarted) {
            return;
        }
        loadingStarted = true;

        final InputStream skillXmlStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("skills.xml");

        if (skillXmlStream == null) {
            throw new IllegalStateException("Skill XML was not found.");
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(skillXmlStream);
            doc.getDocumentElement().normalize();

            findGroup(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search the group nodes in the document.
     *
     * @param currentNode the node that is the root of the search
     */
    private static void findGroup(@Nonnull final Node currentNode) {
        if ("group".equals(currentNode.getNodeName())) {
            final NamedNodeMap attributes = currentNode.getAttributes();
            final String german = attributes.getNamedItem("german").getNodeValue();
            final String english = attributes.getNamedItem("english").getNodeValue();

            final SkillGroup group = new SkillGroup(german, english);

            final NodeList childNodes = currentNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                parseSkill(group, childNodes.item(i));
            }

            SkillGroups.getInstance().addSkillGroup(group);
        }

        final NodeList childNodes = currentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            findGroup(childNodes.item(i));
        }
    }

    /**
     * Parse the skill values from a specified node.
     *
     * @param parentGroup the group this skills belong to
     * @param parsedNode  the node that is parsed
     */
    private static void parseSkill(@Nonnull final SkillGroup parentGroup, @Nonnull final Node parsedNode) {
        if ("skill".equals(parsedNode.getNodeName())) {
            final NamedNodeMap attributes = parsedNode.getAttributes();
            final int id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
            final String name = attributes.getNamedItem("name").getNodeValue();
            final String german = attributes.getNamedItem("german").getNodeValue();
            final String english = attributes.getNamedItem("english").getNodeValue();

            final Skill skill = new Skill(id, name, german, english, parentGroup);
            Skills.getInstance().addSkill(skill);
        }
    }

    /**
     * Private constructor to block the creation of a instance.
     */
    private SkillLoader() {
        // nothing
    }
}
