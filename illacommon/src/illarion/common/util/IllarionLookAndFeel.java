/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This class is used to setup the look and feel of Illarion. This style only
 * applies to swing GUIs.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class IllarionLookAndFeel {
    /**
     * As this is a utility class the private constructor ensures that there
     * won't be any instances of this class.
     */
    private IllarionLookAndFeel() {
        // nothing to do
    }

    /**
     * Calling this function causes that the current look and feel is changed to
     * the Illarion style.
     */
    @SuppressWarnings("nls")
    public static void setupLookAndFeel() {
        try {
            final Color background = new Color(159, 138, 91);
            final Color green = new Color(81, 111, 17);
            final Color yellow = new Color(255, 220, 35);
            final Color red = new Color(169, 46, 34);

            // Primary Colors
            UIManager.put("control", background);
            UIManager.put("info", yellow.darker());
            UIManager.put("nimbusBase", background);
            UIManager.put("nimbusDisabledText", background.darker());
            UIManager.put("nimbusFocus", background.brighter().brighter());
            UIManager.put("nimbusGreen", green.brighter());
            UIManager.put("nimbusInfoBlue", background.darker());
            UIManager.put("nimbusLightBackground", background.brighter());
            UIManager.put("nimbusOrange", green);
            UIManager.put("nimbusRed", red);
            UIManager.put("nimbusSelectedText", Color.white);
            UIManager.put("nimbusSelectionBackground", background.darker().darker());
            UIManager.put("text", Color.black);

            UIManager.put("activeCaption", background.brighter());
            UIManager.put("background", background);
            UIManager.put("controlDkShadow", background.darker());
            UIManager.put("controlHighlight", background);
            UIManager.put("controlLHighlight", background.brighter());
            UIManager.put("controlShadow", background.darker());
            UIManager.put("controlText", Color.black);
            UIManager.put("desktop", background.darker());
            UIManager.put("inactiveCaption", background.darker());
            UIManager.put("infoText", Color.black);
            UIManager.put("menu", background.brighter());
            UIManager.put("menuText", background.darker().darker());
            UIManager.put("nimbusBlueGrey", background);
            UIManager.put("nimbusBorder", background.darker());
            UIManager.put("nimbusSelection", background.darker());
            UIManager.put("scrollbar", background.brighter().brighter());
            UIManager.put("textBackground", background.darker());
            UIManager.put("textForeground", Color.black);
            UIManager.put("textHighlight", background.brighter().brighter());
            UIManager.put("textHighlightText", background.brighter());
            UIManager.put("textInactiveText", background.darker());
            for (final LookAndFeelInfo info : UIManager
                .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (final ClassNotFoundException e) {
            // handle exception
        } catch (final InstantiationException e) {
            // handle exception
        } catch (final IllegalAccessException e) {
            // handle exception
        }
    }
}
