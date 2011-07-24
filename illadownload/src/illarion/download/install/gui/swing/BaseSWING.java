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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;

import illarion.download.install.resources.ResourceManager;
import illarion.download.util.Lang;

/**
 * This is the basic frame of the GUI that holds all other content.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class BaseSWING extends JFrame {
    /**
     * This is the action listener in general used for cancel buttons. It will
     * display a confirm message and shutdown the application in case this
     * message is confirmed.
     * 
     * @author Martin Karing
     * @version 1.00
     * @since 1.00
     */
    private static final class CancelActionListener implements ActionListener {
        /**
         * The parent component. The confirm dialog will be modal to this one
         */
        private final Component parent;

        /**
         * Create this cancel option.
         * 
         * @param parentComp the component that is the triggering parent of this
         *            listener
         */
        public CancelActionListener(final Component parentComp) {
            parent = parentComp;
        }

        /**
         * Execute this listener.
         */
        @SuppressWarnings("nls")
        @Override
        public void actionPerformed(final ActionEvent e) {
            if (JOptionPane
                .showConfirmDialog(
                    parent,
                    Lang.getMsg("illarion.download.install.gui.cancel.confirm.text"),
                    Lang.getMsg("illarion.download.install.gui.cancel.confirm.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    /**
     * This is the action listener in general used for exit buttons. It will
     * display a confirm message and shutdown the application in case this
     * message is confirmed.
     * 
     * @author Martin Karing
     * @version 1.00
     * @since 1.00
     */
    private static final class ExitActionListener implements ActionListener {
        /**
         * The parent component. The confirm dialog will be modal to this one
         */
        private final Component parent;

        /**
         * Create this cancel option.
         * 
         * @param parentComp the component that is the triggering parent of this
         *            listener
         */
        public ExitActionListener(final Component parentComp) {
            parent = parentComp;
        }

        /**
         * Execute this listener.
         */
        @SuppressWarnings("nls")
        @Override
        public void actionPerformed(final ActionEvent e) {
            if (JOptionPane
                .showConfirmDialog(
                    parent,
                    Lang.getMsg("illarion.download.install.gui.exit.confirm.text"),
                    Lang.getMsg("illarion.download.install.gui.exit.confirm.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                ResourceManager.getInstance().saveResourceDatabase();
                System.exit(0);
            }
        }
    }

    /**
     * This task to show the new content within the swing context.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class ShowContentTask implements Runnable {
        /**
         * The base that displays the content.
         */
        private final BaseSWING base;

        /**
         * The content to show.
         */
        private final AbstractContentSWING content;

        /**
         * Constructor for this content showing task.
         * 
         * @param baseFrame the frame that is to show the content
         * @param displayContent the content that will be shown
         */
        public ShowContentTask(final BaseSWING baseFrame,
            final AbstractContentSWING displayContent) {
            content = displayContent;
            base = baseFrame;
        }

        @Override
        public void run() {
            base.showImpl(content);
        }

    }

    /**
     * The height of the frame in pixels.
     */
    public static final int WINDOW_HEIGHT = 545;

    /**
     * The width of the frame in pixels.
     */
    public static final int WINDOW_WIDTH = 600;

    /**
     * The serialization UID of this base frame.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The panel that holds the buttons of the GUI.
     */
    private final JPanel buttonPanel;

    /**
     * The main panel that fills the center of the GUI.
     */
    private final JPanel mainPanel;

    /**
     * The next image ID to be used.
     */
    private int nextImageID;

    /**
     * This value is set true while this class is preparing the display.
     */
    private boolean preparingDisplay = false;

    /**
     * This is the synchronization object for the showing variable
     * {@link #preparingDisplay}.
     */
    private final Object showingLock = new Object();

    /**
     * The label that displays the title of the installer.
     */
    private final JLabel title;

    /**
     * The tracker ID of the title image.
     */
    private final int titleID;

    /**
     * The media tracker that tracks the graphics required for this GUI.
     */
    private final MediaTracker tracker;

    /**
     * Prepares the base display.
     */
    @SuppressWarnings("nls")
    public BaseSWING() {
        super(Lang.getMsg("illarion.download.gui.Base.Title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            setIconImage(ImageIO.read(BaseSWING.class.getClassLoader()
                .getResource("download.png")));
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tracker = new MediaTracker(this);
        nextImageID = 0;

        titleID = trackImage("title.png");

        final JPanel content = new JPanel(new BorderLayout());
        setContentPane(content);

        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(title, BorderLayout.NORTH);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        content.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setBorder(BorderFactory
            .createEtchedBorder(EtchedBorder.LOWERED));

        mainPanel = new JPanel();
        content.add(mainPanel, BorderLayout.CENTER);

        setResizable(false);
    }

    /**
     * Update the look and feel settings so the application looks nice.
     */
    @SuppressWarnings("nls")
    public static void updateLookAndFeel() {
        try {
            final Color background = new Color(159, 138, 91);
            final Color green = new Color(81, 111, 17);
            UIManager.put("desktop", background);
            UIManager.put("activeCaption", background);
            UIManager.put("inactiveCaption", background.brighter());
            UIManager.put("textHighlight", background.brighter());
            UIManager.put("control", background);
            UIManager.put("nimbusBase", background);
            UIManager.put("nimbusBlueGrey", background);
            UIManager.put("nimbusLightBackground", background);
            UIManager.put("nimbusBorder", background.darker());
            UIManager.put("nimbusSelection", background.darker());
            UIManager.put("nimbusInfoBlue", background.darker());
            UIManager.put("nimbusFocus", background.brighter());
            UIManager.put("nimbusOrange", green);
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

    /**
     * Create the default cancel button for the bottom Panel.
     * 
     * @return the default cancel button
     */
    @SuppressWarnings("nls")
    public JButton getCancelButton() {
        final JButton returnButton = getPanelButton();
        returnButton.setText(Lang
            .getMsg("illarion.download.install.gui.cancel.text"));
        returnButton.setToolTipText(Lang
            .getMsg("illarion.download.install.gui.cancel.tooltip"));
        returnButton.addActionListener(new CancelActionListener(this));
        return returnButton;
    }

    /**
     * Create the default button to continue to the next page.
     * 
     * @return the default continue button
     */
    @SuppressWarnings("nls")
    public JButton getContinueButton() {
        final JButton returnButton = getPanelButton();
        returnButton.setText(Lang
            .getMsg("illarion.download.install.gui.continue.text"));
        returnButton.setToolTipText(Lang
            .getMsg("illarion.download.install.gui.continue.tooltip"));
        return returnButton;
    }

    /**
     * Create the default exit button for the bottom Panel.
     * 
     * @return the default exit button
     */
    @SuppressWarnings("nls")
    public JButton getExitButton() {
        final JButton returnButton = getPanelButton();
        returnButton.setText(Lang
            .getMsg("illarion.download.install.gui.exit.text"));
        returnButton.setToolTipText(Lang
            .getMsg("illarion.download.install.gui.exit.tooltip"));
        returnButton.addActionListener(new ExitActionListener(this));
        return returnButton;
    }

    /**
     * Get the image with a specified name. This function allows to share the
     * image resources so even calling it multiple times for the same image will
     * load the image only once.
     * 
     * @param name the name of the image
     * @return the image
     */
    public Image getImage(final String name) {
        final URL imageURL =
            BaseSWING.class.getClassLoader().getResource(name);
        return getToolkit().getImage(imageURL);
    }

    /**
     * Create a button that is designed for the bottom Panel.
     * 
     * @return the button for the button panel
     */
    public JButton getPanelButton() {
        final JButton returnButton = new JButton();
        final Dimension size = new Dimension(120, 40);
        returnButton.setSize(size);
        returnButton.setPreferredSize(size);

        return returnButton;
    }

    /**
     * Show one GUI on the screen.
     * 
     * @param content the content that is now displayed.
     */
    public void show(final AbstractContentSWING content) {
        synchronized (showingLock) {
            preparingDisplay = true;
        }
        SwingUtilities.invokeLater(new ShowContentTask(this, content));
    }

    /**
     * Add a image to the tracker.
     * 
     * @param name the name of the image to track
     * @return the ID of the image in the tracker
     */
    public int trackImage(final String name) {
        tracker.addImage(getImage(name), nextImageID++);
        return (nextImageID - 1);
    }

    /**
     * This function waits until this class is ready showing the new content.
     * 
     * @throws InterruptedException in case the waiting time is interrupted
     */
    public void waitForFinishShowing() throws InterruptedException {
        synchronized (showingLock) {
            while (preparingDisplay) {
                showingLock.wait();
            }
        }
    }

    /**
     * Wait until the tracker load a image properly.
     * 
     * @param id the id of the image
     * @return <code>true</code> in case loading the image went well and its now
     *         ready to be used
     */
    public boolean waitForImage(final int id) {
        while (!tracker.checkID(id)) {
            try {
                tracker.waitForID(id);
            } catch (final InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * This is the implementation of the show function that is used to execute
     * is properly within the SWING context.
     * 
     * @param content the content to show
     */
    @SuppressWarnings("nls")
    protected void showImpl(final AbstractContentSWING content) {
        mainPanel.removeAll();
        buttonPanel.removeAll();
        content.fillContent(this, mainPanel);
        content.fillButtons(this, buttonPanel);

        if (waitForImage(titleID)) {
            if (title.getIcon() == null) {
                title.setIcon(new ImageIcon(getImage("title.png")));
            }
        }

        mainPanel.invalidate();
        buttonPanel.invalidate();
        invalidate();

        if (!isVisible()) {
            final Dimension prefDim =
                new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
            setPreferredSize(prefDim);
            setSize(prefDim);
            pack();
            setLocationRelativeTo(null);
        }

        content.prepareDisplay(this);
        reportShowingDone();
    }

    /**
     * Send out the notification that the display is now prepared.
     */
    private void reportShowingDone() {
        synchronized (showingLock) {
            preparingDisplay = false;
            showingLock.notify();
        }
    }
}
