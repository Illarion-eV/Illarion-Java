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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXLabel;

import illarion.common.util.DirectoryManager;

import illarion.download.util.Lang;

/**
 * This is the display for the application directory. This displayed form allows
 * to set this directory.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class AppDirectorySWING extends AbstractContentSWING {
    /**
     * This is the continue button listener. It will invoke when the continue
     * button is clicked. It will check if the entered path is valid and
     * continue the application then.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class ContinueButtonListener implements
        ActionListener {
        /**
         * This is the base component that is used to align the error window in
         * case one is needed.
         */
        private final BaseSWING baseComponent;

        /**
         * The reference to the parent of this listener.
         */
        private final AppDirectorySWING parent;

        /**
         * The text component that contains the path that is supposed to be
         * forwarded.
         */
        private final JTextComponent pathSource;

        /**
         * Constructor of the button listener. It will store the references to
         * the components required for this to work properly.
         * 
         * @param base the base component that is used to align a error window
         *            in case one is needed
         * @param parentObject this is the parent of the object this listener is
         *            assigned to
         * @param source the source of the text to the path
         */
        public ContinueButtonListener(final BaseSWING base,
            final AppDirectorySWING parentObject, final JTextComponent source) {
            pathSource = source;
            baseComponent = base;
            parent = parentObject;
        }

        @SuppressWarnings("nls")
        @Override
        public void actionPerformed(final ActionEvent e) {
            String path;
            try {
                path = pathSource.getText();
                if ((path == null) || (path.length() < 2)) {
                    path = null;
                }
            } catch (final NullPointerException ex) {
                path = null;
            }

            if (path != null) {
                DirectoryManager.getInstance()
                    .setDataDirectory(new File(path));
                if (DirectoryManager.getInstance().hasDataDirectory()) {
                    parent.reportContinue();
                    return;
                }
            }

            JOptionPane
                .showMessageDialog(
                    baseComponent,
                    Lang.getMsg("illarion.download.intall.gui.Directory.DirError.message"),
                    Lang.getMsg("illarion.download.intall.gui.Directory.DirError.title"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This is the listener that is applied to the search button. It will open a
     * dialog that allows selecting a folder and put the path to the selected
     * folder to the text component that is set by the constructor.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class SearchButtonListener implements ActionListener {
        /**
         * This is the base component the newly opened selection window will be
         * aligned to.
         */
        private final BaseSWING baseComponent;

        /**
         * This test component will receive the selected path.
         */
        private final JTextComponent pathReceiver;

        /**
         * The search button listener. This listener enables the button its
         * applied on to search for a path.
         * 
         * @param base the base component that is used to align the opened
         *            dialog
         * @param receiver the text component that will receive the text
         */
        public SearchButtonListener(final BaseSWING base,
            final JTextComponent receiver) {
            pathReceiver = receiver;
            baseComponent = base;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            String path;
            try {
                path = pathReceiver.getText();
                if ((path == null) || (path.length() < 2)) {
                    path = null;
                }
            } catch (final NullPointerException ex) {
                path = null;
            }
            final JFileChooser chooser = new JFileChooser(path);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (chooser.showOpenDialog(baseComponent) == JFileChooser.APPROVE_OPTION) {
                pathReceiver.setText(chooser.getSelectedFile()
                    .getAbsolutePath());
            }
        }
    }

    /**
     * This flag is used to ensure that the components are not created more then
     * once.
     */
    private boolean componentsCreated = false;

    /**
     * The button that is used to continue to the next step.
     */
    private JButton continueButton;

    /**
     * The text field used to display the selected path.
     */
    private JTextField pathArea;

    /**
     * The button used to search for the path to store the application data.
     */
    private JButton searchPathButton;

    @Override
    public void fillButtons(final BaseSWING base, final JPanel buttonPanel) {
        createComponents(base);
        buttonPanel.add(base.getCancelButton());
        buttonPanel.add(continueButton);
    }

    @SuppressWarnings("nls")
    @Override
    public void fillContent(final BaseSWING base, final JPanel contentPanel) {
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        int line = 0;
        final GridBagConstraints con = new GridBagConstraints();
        con.anchor = GridBagConstraints.WEST;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridheight = 1;
        con.gridwidth = 2;
        con.gridx = 0;
        con.gridy = line++;
        con.weightx = 1.0;
        con.weighty = 0.0;
        con.insets.set(0, 0, 10, 0);

        final JLabel headLabel =
            new JLabel(
                Lang.getMsg("illarion.download.intall.gui.AppDirectory.title"));
        contentPanel.add(headLabel, con);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.BOLD, 14.f));

        con.gridy = line++;
        JXLabel textField = new JXLabel();
        textField.setCursor(null);
        textField.setOpaque(false);
        textField.setFocusable(false);
        textField.setLineWrap(true);
        textField.setText(Lang
            .getMsg("illarion.download.intall.gui.AppDirectory.content"));
        contentPanel.add(textField, con);

        con.gridy = line++;
        textField = new JXLabel();
        textField.setCursor(null);
        textField.setOpaque(false);
        textField.setFocusable(false);
        textField.setLineWrap(true);
        textField.setText(Lang
            .getMsg("illarion.download.intall.gui.Directory.enterPathDesc"));
        contentPanel.add(textField, con);

        createComponents(base);

        con.gridy = line++;
        con.gridwidth = 1;
        con.insets.set(0, 0, 0, 10);

        contentPanel.add(pathArea, con);

        con.gridx = 1;
        con.weightx = 0.0;
        con.insets.set(0, 0, 0, 0);
        contentPanel.add(searchPathButton, con);

        con.gridy = line++;
        con.gridx = 0;
        con.gridwidth = 2;
        con.weightx = 1.0;
        con.weighty = 1.0;
        contentPanel.add(new JLabel(), con);
    }

    @Override
    public void prepareDisplay(final BaseSWING base) {
        base.setVisible(true);
    }

    /**
     * Create the components. This is only used for interactive components to
     * ensure that they are created properly.
     * 
     * @param base the base element the components are created for
     */
    @SuppressWarnings("nls")
    private void createComponents(final BaseSWING base) {
        if (componentsCreated) {
            return;
        }
        componentsCreated = true;

        pathArea = new JTextField(30);
        pathArea.setText(System.getProperty("user.home") + File.separator
            + "Illarion" + File.separator + "bin");
        searchPathButton =
            new JButton(
                Lang.getMsg("illarion.download.intall.gui.Directory.search"));
        searchPathButton.addActionListener(new SearchButtonListener(base,
            pathArea));
        continueButton = base.getContinueButton();
        continueButton.addActionListener(new ContinueButtonListener(base,
            this, pathArea));
    }

}
