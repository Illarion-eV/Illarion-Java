/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install.gui.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import illarion.download.install.resources.Resource;
import illarion.download.util.Lang;

/**
 * This is the application selection view. Its sole purpose is to display the
 * applications the user can start using this utility. This display only shows
 * in case the application to start is not selected explicit.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class AppSelectionSWING extends AbstractContentSWING {
    /**
     * This class is used as listener for the buttons that launch the different
     * applications.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class ApplicationButtonListener implements
        ActionListener {
        /**
         * The parent that stores the selected resource.
         */
        private final AppSelectionSWING parent;

        /**
         * The resource that is selected in case this listener is triggered.
         */
        private final Resource resource;

        /**
         * Public constructor so the parent class is able to create a object of
         * this class. Using this constructor also the required values for this
         * listener are set.
         * 
         * @param parentContent the parent that stores the selected listener
         * @param res the resource that is selected by this listener
         */
        public ApplicationButtonListener(
            final AppSelectionSWING parentContent, final Resource res) {
            parent = parentContent;
            resource = res;
        }

        /**
         * This is invoked in case the button is clicked. When this happens the
         * resource assigned to this class is marked as ready and the execution
         * of the install routine continues.
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

        final Dimension launchButtonDim = new Dimension(120, 58);

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

        final JLabel headLabel =
            new JLabel(
                Lang.getMsg("illarion.download.intall.gui.AppSelection.title"));
        contentPanel.add(headLabel, con);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.BOLD, 14.f));

        final JButton rsClient = new JButton();
        rsClient.setHorizontalTextPosition(SwingConstants.RIGHT);
        rsClient.setHorizontalAlignment(SwingConstants.LEFT);
        rsClient.setIconTextGap(40);
        rsClient.setPreferredSize(launchButtonDim);
        rsClient.setSize(launchButtonDim);
        rsClient.setText(Lang
            .getMsg("illarion.download.intall.gui.AppSelection.rsClient"));
        rsClient.setEnabled(false);

        con.gridy = line++;
        contentPanel.add(rsClient, con);
        rsClient.setFont(rsClient.getFont().deriveFont(20.f));

        final JButton tsClient = new JButton();
        tsClient.setHorizontalTextPosition(SwingConstants.RIGHT);
        tsClient.setHorizontalAlignment(SwingConstants.LEFT);
        tsClient.setIconTextGap(40);
        tsClient.setPreferredSize(launchButtonDim);
        tsClient.setSize(launchButtonDim);
        tsClient.setText(Lang
            .getMsg("illarion.download.intall.gui.AppSelection.tsClient"));
        tsClient.addActionListener(new ApplicationButtonListener(this,
            illarion.download.install.resources.dev.Client.getInstance()));

        con.gridy = line++;
        contentPanel.add(tsClient, con);
        tsClient.setFont(tsClient.getFont().deriveFont(20.f));

        final JButton easyEditor = new JButton();
        easyEditor.setHorizontalTextPosition(SwingConstants.RIGHT);
        easyEditor.setHorizontalAlignment(SwingConstants.LEFT);
        easyEditor.setIconTextGap(40);
        easyEditor.setPreferredSize(launchButtonDim);
        easyEditor.setSize(launchButtonDim);
        easyEditor.setText(Lang
            .getMsg("illarion.download.intall.gui.AppSelection.easyEditor"));
        easyEditor.addActionListener(new ApplicationButtonListener(this,
            illarion.download.install.resources.dev.EasyNpcEditor
                .getInstance()));

        con.gridy = line++;
        contentPanel.add(easyEditor, con);
        easyEditor.setFont(easyEditor.getFont().deriveFont(20.f));

        final JButton mapEditor = new JButton();
        mapEditor.setHorizontalTextPosition(SwingConstants.RIGHT);
        mapEditor.setHorizontalAlignment(SwingConstants.LEFT);
        mapEditor.setIconTextGap(40);
        mapEditor.setPreferredSize(launchButtonDim);
        mapEditor.setSize(launchButtonDim);
        mapEditor.setText(Lang
            .getMsg("illarion.download.intall.gui.AppSelection.mapEditor"));
        mapEditor.addActionListener(new ApplicationButtonListener(this,
            illarion.download.install.resources.dev.Mapeditor.getInstance()));

        con.gridy = line++;
        contentPanel.add(mapEditor, con);
        mapEditor.setFont(mapEditor.getFont().deriveFont(20.f));

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
    }

    /**
     * Get the resource that was selected in this menu or <code>null<code> in
     * case none was selected.
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
