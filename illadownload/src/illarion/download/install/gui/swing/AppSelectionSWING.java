/*
 * This file is part of the Illarion Download Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Download Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Download Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Download Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install.gui.swing;

import illarion.download.install.resources.Resource;
import illarion.download.util.Lang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the application selection view. Its sole purpose is to display the applications the user can start using
 * this
 * utility. This display only shows in case the application to start is not selected explicit.
 *
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
public final class AppSelectionSWING
        extends AbstractContentSWING {
    /**
     * This class is used as listener for the buttons that launch the different applications.
     *
     * @author Martin Karing
     * @version 1.00
     * @since 1.00
     */
    private static final class ApplicationButtonListener
            implements ActionListener {
        /**
         * The parent that stores the selected resource.
         */
        private final AppSelectionSWING parent;

        /**
         * The resource that is selected in case this listener is triggered.
         */
        private final Resource resource;

        /**
         * Public constructor so the parent class is able to create a object of this class. Using this constructor also
         * the required values for this listener are set.
         *
         * @param parentContent the parent that stores the selected listener
         * @param res           the resource that is selected by this listener
         */
        public ApplicationButtonListener(final AppSelectionSWING parentContent, final Resource res) {
            parent = parentContent;
            resource = res;
        }

        /**
         * This is invoked in case the button is clicked. When this happens the resource assigned to this class is
         * marked as ready and the execution of the install routine continues.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            parent.setSelectedResource(resource);
            parent.reportContinue();
        }

    }

    /**
     * The resource that was selected in this menu.
     */
    private Resource selectedResource;

    /**
     * Create the buttons on the root pane of the application.
     */
    @Override
    public void fillButtons(final BaseSWING base, final JPanel buttonPanel) {
        buttonPanel.add(base.getExitButton());
    }

    /**
     * Build up the selection menu for the applications available.
     */
    @SuppressWarnings("nls")
    @Override
    public void fillContent(final BaseSWING base, final JPanel contentPanel) {
        final int clientIconID = base.trackImage("client.png");
        final int mapeditIconID = base.trackImage("mapedit.png");
        final int easynpcIconID = base.trackImage("easynpc.png");
        final int easyquestIconID = base.trackImage("easyquest.png");

        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        int line = 0;
        final GridBagConstraints con = new GridBagConstraints();
        con.anchor = GridBagConstraints.WEST;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridheight = 1;
        con.gridwidth = 1;
        con.gridx = 0;
        con.gridy = line++;
        con.weightx = 1.0;
        con.weighty = 0.0;
        con.insets.set(0, 0, 7, 0);

        final JLabel headLabel = new JLabel(Lang.getMsg("illarion.download.install.gui.AppSelection.title"));
        contentPanel.add(headLabel, con);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.BOLD, 14.f));

        final JButton rsClient = createLaunchButton();
        rsClient.setText(Lang.getMsg("illarion.download.install.gui.AppSelection.rsClient"));
        rsClient.setEnabled(false);

        con.gridy = line++;
        contentPanel.add(rsClient, con);

        final JButton tsClient = createLaunchButton();
        tsClient.setText(Lang.getMsg("illarion.download.install.gui.AppSelection.tsClient"));
        tsClient.addActionListener(new ApplicationButtonListener(this, illarion.download.install.resources.dev.Client
                .getInstance()));

        con.gridy = line++;
        contentPanel.add(tsClient, con);

        final JButton easyEditor = createLaunchButton();
        easyEditor.setText(Lang.getMsg("illarion.download.install.gui.AppSelection.easyEditor"));
        easyEditor.addActionListener(new ApplicationButtonListener(this, illarion.download.install.resources.dev
                .EasyNpcEditor.getInstance()));

        con.gridy = line++;
        contentPanel.add(easyEditor, con);

        final JButton easyQuest = createLaunchButton();
        easyQuest.setText(Lang.getMsg("illarion.download.install.gui.AppSelection.easyQuest"));
        easyQuest.addActionListener(new ApplicationButtonListener(this, illarion.download.install.resources.dev
                .EasyQuestEditor.getInstance()));

        con.gridy = line++;
        contentPanel.add(easyQuest, con);

        final JButton mapEditor = createLaunchButton();
        mapEditor.setText(Lang.getMsg("illarion.download.install.gui.AppSelection.mapEditor"));
        mapEditor.addActionListener(new ApplicationButtonListener(this, illarion.download.install.resources.dev
                .Mapeditor.getInstance()));

        con.gridy = line++;
        contentPanel.add(mapEditor, con);

        con.gridy = line++;
        con.gridx = 0;
        con.gridwidth = 2;
        con.weightx = 1.0;
        con.weighty = 1.0;
        contentPanel.add(new JLabel(), con);

        if (base.waitForImage(clientIconID)) {
            rsClient.setIcon(new ImageIcon(base.getImage("client.png")));
            tsClient.setIcon(new ImageIcon(base.getImage("client.png")));
        }

        if (base.waitForImage(mapeditIconID)) {
            mapEditor.setIcon(new ImageIcon(base.getImage("mapedit.png")));
        }

        if (base.waitForImage(easynpcIconID)) {
            easyEditor.setIcon(new ImageIcon(base.getImage("easynpc.png")));
        }

        if (base.waitForImage(easyquestIconID)) {
            easyQuest.setIcon(new ImageIcon(base.getImage("easyquest.png")));
        }
    }

    private JButton createLaunchButton() {
        final Dimension launchButtonDim = new Dimension(120, 58);
        final JButton button = new JButton();
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(40);
        button.setPreferredSize(launchButtonDim);
        button.setSize(launchButtonDim);
        button.setFont(button.getFont().deriveFont(20.f));

        return button;
    }

    /**
     * Get the resource that was selected in this menu or <code>null<code> in case none was selected.
     *
     * @return the selected resource or <code>null</code>
     */
    public Resource getSelectedResource() {
        return selectedResource;
    }

    @Override
    public void prepareDisplay(final BaseSWING base) {
        base.setVisible(true);
    }

    /**
     * Set the resource that was selected in this menu.
     *
     * @param res the new selected resource
     */
    protected void setSelectedResource(final Resource res) {
        selectedResource = res;
    }
}
