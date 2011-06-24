/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Dialog base class. This class implements OK and Cancel buttons and standard
 * closing behavior. It is also Translating.
 * 
 * @author Nop
 * @since 0.92
 * @version 0.92
 */
public abstract class AbstractDialog extends JDialog {

    /**
     * Identifier for the cancle button.
     */
    public static final int BUTTON_CANCEL = 2;

    /**
     * Identifier for the okay button.
     */
    public static final int BUTTON_OK = 1;
    /**
     * Class Version identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The button panel of the dialog.
     */
    private JPanel buttons;

    /**
     * The Cancle-Button of the dialog.
     */
    private JButton cancelButton;

    /**
     * The OK-Button of the dialog.
     */
    private JButton okButton;

    /**
     * Constructor for a Dialoge using a JDialog as owner.
     * 
     * @param owner Owner of this dialog, has to be a JFrame
     * @param titel Title of the dialog
     * @param modal true for a modal dialog, false for one that allows other
     *            dialogs to be in focus at the same time.
     * @see javax.swing.JDialog#JDialog(java.awt.Frame, String, boolean)
     */
    public AbstractDialog(final Frame owner, final String titel,
        final boolean modal) {
        super(owner, titel, modal);
        init();
    }

    /**
     * Constructor for a Dialoge using a JDialog as owner.
     * 
     * @param parent Owner of this dialog, has to be a JDialog
     * @param titel Title of the dialog
     * @param modal true for a modal dialog, false for one that allows other
     *            dialogs to be in focus at the same time.
     * @see javax.swing.JDialog#JDialog(java.awt.Dialog, String, boolean)
     */
    public AbstractDialog(final JDialog parent, final String titel,
        final boolean modal) {
        super(parent, titel, modal);
        init();
    }

    /**
     * Implement this method with code to be executed when the dialog is
     * cancelled with the cancel button, the escape key or the close box.
     */
    public abstract void actionCancel();

    /**
     * Implement this method with code to be executed when the dialog is
     * confirmed with the OK button.
     */
    public abstract void actionOK();

    /**
     * Add additional button to button bar of dialog.
     * 
     * @param comp the Component that shall be added the the button pane
     */
    public final void addButton(final JComponent comp) {
        buttons.add(comp);
    }

    /**
     * Center the dialog relative to its parent.
     */
    public final void center() {
        pack();

        Point dadpos;
        Dimension dadsize;
        if (getOwner().isVisible()) {
            dadpos = getOwner().getLocationOnScreen();
            dadsize = getOwner().getSize();
        } else {
            dadpos = new Point(0, 0);
            dadsize = Toolkit.getDefaultToolkit().getScreenSize();
        }
        final Dimension size = getSize();

        int x = dadpos.x + ((dadsize.width - size.width) / 2);
        int y = dadpos.y + ((dadsize.height - size.height) / 2);
        x = Math.max(x, 0);
        y = Math.max(y, 0);
        setLocation(x, y);
    }

    /**
     * Init the frame and set up all default values such as buttons and button
     * listeners.
     */
    @SuppressWarnings("nls")
    protected final void init() {
        // add buttons
        buttons = new JPanel(new FlowLayout());
        okButton = new JButton(Lang.getMsg("button.save"));
        cancelButton = new JButton(Lang.getMsg("button.cancel"));
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.PLAIN,
            16.f));
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.setFont(okButton.getFont().deriveFont(Font.BOLD, 16.f));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        buttons.add(okButton);
        buttons.add(cancelButton);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        // button actions
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent ae) {
                actionCancel();
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent ae) {
                actionOK();
            }
        });

        // window keyboard actions
        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent ae) {
                actionCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                actionCancel();
            }
        });
    }

    /**
     * Check if a button is enabled.
     * 
     * @param button Select the button that shall be checked. Possible values
     *            are {@link #BUTTON_OK} and {@link #BUTTON_CANCEL}
     * @return <code>true</code> in case the button is enabled
     */
    @SuppressWarnings("nls")
    protected final boolean isButtonEnabled(final int button) {
        if (button == BUTTON_OK) {
            return okButton.isEnabled();
        } else if (button == BUTTON_CANCEL) {
            return cancelButton.isEnabled();
        } else {
            throw new IllegalArgumentException("Undefined button identifier");
        }
    }

    /**
     * Change the enabled status of one of the buttons created by default.
     * 
     * @param button Select the button that shall be changed. Possible values
     *            are {@link #BUTTON_OK} and {@link #BUTTON_CANCEL}
     * @param enabled the new enabled status
     */
    @SuppressWarnings("nls")
    protected final void setButtonEnabled(final int button,
        final boolean enabled) {
        if (button == BUTTON_OK) {
            okButton.setEnabled(enabled);
        } else if (button == BUTTON_CANCEL) {
            cancelButton.setEnabled(enabled);
        } else {
            throw new IllegalArgumentException("Undefined button identifier");
        }
    }

    /**
     * Change the name of one of the buttons created by default.
     * 
     * @param button Select the button that shall be changed. Possible values
     *            are {@link #BUTTON_OK} and {@link #BUTTON_CANCEL}
     * @param name the new text written on the button
     */
    @SuppressWarnings("nls")
    protected final void setButtonName(final int button, final String name) {
        if (button == BUTTON_OK) {
            okButton.setText(name);
        } else if (button == BUTTON_CANCEL) {
            cancelButton.setText(name);
        } else {
            throw new IllegalArgumentException("Undefined button identifier");
        }
    }

    /**
     * Set one of the default created buttons as the default button of the root
     * pane.
     * 
     * @param button Select the button that shall be changed. Possible values
     *            are {@link #BUTTON_OK} and {@link #BUTTON_CANCEL}
     */
    @SuppressWarnings("nls")
    protected final void setDefaultButton(final int button) {
        if (button == BUTTON_OK) {
            getRootPane().setDefaultButton(okButton);
        } else if (button == BUTTON_CANCEL) {
            getRootPane().setDefaultButton(cancelButton);
        } else {
            throw new IllegalArgumentException("Undefined button identifier");
        }
    }

}
