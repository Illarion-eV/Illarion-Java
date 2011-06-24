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
package illarion.easynpc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.io.IOException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import illarion.easynpc.Lang;
import illarion.easynpc.Parser;
import illarion.easynpc.docu.DocuEntry;

import illarion.common.util.ArrayEnumeration;

/**
 * This dialog is the help browser used to display the embedded documentation of
 * the editor.
 * 
 * @author Martin Karing
 * @since 1.01
 */
public final class DocuBrowser extends JDialog {
    private final class DocuTreeNode implements TreeNode {
        private final DocuTreeNode[] children;
        private final DocuEntry nodeEntry;
        private final DocuTreeNode parentNode;
        private final String title;

        public DocuTreeNode(final DocuEntry entry) {
            this(entry, null);
        }

        public DocuTreeNode(final DocuEntry entry, final DocuTreeNode parent) {
            nodeEntry = entry;
            parentNode = parent;
            title = entry.getTitle();

            if (entry.getChildCount() == 0) {
                children = null;
            } else {
                children = new DocuTreeNode[entry.getChildCount()];

                for (int i = 0; i < children.length; i++) {
                    children[i] = new DocuTreeNode(entry.getChild(i), this);
                }
            }
        }

        @Override
        public Enumeration<DocuTreeNode> children() {
            return new ArrayEnumeration<DocuTreeNode>(children);
        }

        /**
         * Update the details display for this node.
         */
        public void displayNode() {
            updateDetails(nodeEntry);
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public TreeNode getChildAt(final int childIndex) {
            if (children == null) {
                return null;
            }
            if ((childIndex < 0) || (childIndex >= children.length)) {
                return null;
            }
            return children[childIndex];
        }

        @Override
        public int getChildCount() {
            if (children == null) {
                return 0;
            }
            return children.length;
        }

        @Override
        public int getIndex(final TreeNode node) {
            if (children == null) {
                return -1;
            }
            for (int i = 0; i < children.length; i++) {
                if (children[i].equals(node)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public TreeNode getParent() {
            return parentNode;
        }

        @Override
        public boolean isLeaf() {
            return (children == null);
        }

        @Override
        public String toString() {
            return title;
        }
    }

    /**
     * The instance of the documentation browser once it was created.
     */
    private static DocuBrowser instance;

    /**
     * The serialization UID of the dialog.
     */
    private static final long serialVersionUID = 1L;

    private final JTextArea descriptionContent;

    private final JLabel descriptionTitle;
    private final JTextArea exampleContent;
    private final JLabel exampleTitle;

    private final JTextArea syntaxContent;
    private final JLabel syntaxTitle;

    private final JLabel titleLabel;

    /**
     * The default constructor creating this documentation display.
     */
    @SuppressWarnings("nls")
    public DocuBrowser() {
        super(MainFrame.getInstance(),
            Lang.getMsg(DocuBrowser.class, "title"), false);

        final JSplitPane splitPane =
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        try {
            setIconImage(ImageIO.read(getClass().getClassLoader()
                .getResourceAsStream("easynpc16.png")));
        } catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        final JTree contentTree =
            new JTree(new DocuTreeNode(Parser.getInstance()));
        final JScrollPane contentScroll = new JScrollPane(contentTree);
        contentScroll.setMinimumSize(new Dimension(350, 400));
        contentScroll.setPreferredSize(contentScroll.getMinimumSize());

        final JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        final JScrollPane detailsScroll = new JScrollPane(detailsPanel);
        detailsScroll.setMinimumSize(new Dimension(500, 400));
        detailsScroll.setPreferredSize(detailsScroll.getMinimumSize());

        splitPane.add(contentScroll, JSplitPane.LEFT);
        splitPane.add(detailsScroll, JSplitPane.RIGHT);

        contentTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                ((DocuTreeNode) e.getPath().getLastPathComponent())
                    .displayNode();
            }
        });

        getContentPane().add(splitPane);

        titleLabel = new JLabel();
        descriptionTitle = new JLabel(Lang.getMsg(getClass(), "description"));
        descriptionContent = new JTextArea();
        descriptionContent.setFont(titleLabel.getFont());

        syntaxTitle = new JLabel(Lang.getMsg(getClass(), "syntax"));
        syntaxContent = new JTextArea();
        syntaxContent.setFont(titleLabel.getFont());

        exampleTitle = new JLabel(Lang.getMsg(getClass(), "example"));
        exampleContent = new JTextArea();
        exampleContent.setFont(titleLabel.getFont());

        final Font headlineFont =
            new Font(titleLabel.getFont().getName(), Font.BOLD, 20);
        final Font subheadlineFont =
            new Font(titleLabel.getFont().getName(), Font.BOLD, 18);
        final Font textsubheadlineFont =
            new Font(titleLabel.getFont().getName(), titleLabel.getFont()
                .getStyle(), titleLabel.getFont().getSize());
        titleLabel.setFont(headlineFont);
        descriptionTitle.setFont(subheadlineFont);
        descriptionTitle.setVisible(false);
        descriptionContent.setEditable(false);
        descriptionContent.setFont(textsubheadlineFont);
        descriptionContent.setVisible(false);
        descriptionContent.setLineWrap(true);
        descriptionContent.setWrapStyleWord(true);
        descriptionContent.setBackground(titleLabel.getBackground());
        descriptionContent.setForeground(titleLabel.getForeground());

        final JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(descriptionTitle, BorderLayout.NORTH);
        descriptionPanel.add(descriptionContent, BorderLayout.CENTER);
        descriptionPanel.add(Box.createRigidArea(new Dimension(20, 1)),
            BorderLayout.WEST);

        syntaxTitle.setFont(subheadlineFont);
        syntaxTitle.setVisible(false);
        syntaxContent.setEditable(false);
        syntaxContent.setFont(textsubheadlineFont);
        syntaxContent.setVisible(false);
        syntaxContent.setLineWrap(true);
        syntaxContent.setWrapStyleWord(true);
        syntaxContent.setBackground(titleLabel.getBackground());
        syntaxContent.setForeground(titleLabel.getForeground());

        final JPanel syntaxPanel = new JPanel(new BorderLayout());
        syntaxPanel.add(syntaxTitle, BorderLayout.NORTH);
        syntaxPanel.add(syntaxContent, BorderLayout.CENTER);
        syntaxPanel.add(Box.createRigidArea(new Dimension(20, 1)),
            BorderLayout.WEST);

        exampleTitle.setFont(subheadlineFont);
        exampleTitle.setVisible(false);
        exampleContent.setEditable(false);
        exampleContent.setFont(textsubheadlineFont);
        exampleContent.setVisible(false);
        exampleContent.setLineWrap(true);
        exampleContent.setWrapStyleWord(true);
        exampleContent.setBackground(titleLabel.getBackground());
        exampleContent.setForeground(titleLabel.getForeground());

        final JPanel examplePanel = new JPanel(new BorderLayout());
        examplePanel.add(exampleTitle, BorderLayout.NORTH);
        examplePanel.add(exampleContent, BorderLayout.CENTER);
        examplePanel.add(Box.createRigidArea(new Dimension(20, 1)),
            BorderLayout.WEST);

        final JPanel titlePanel =
            new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        titlePanel.add(titleLabel);
        detailsPanel.add(titlePanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        detailsPanel.add(descriptionPanel);
        detailsPanel.add(syntaxPanel);
        detailsPanel.add(examplePanel);

        validate();
        pack();

        final Dimension parentDim = getOwner().getSize();
        final Point parentPos = getOwner().getLocation();

        setLocation(((parentDim.width - getWidth()) / 2) + parentPos.x,
            ((parentDim.height - getHeight()) / 2) + parentPos.y);
    }

    /**
     * Display the help browser and create it in case that was not done yet.
     */
    public static void showDocuBrowser() {
        synchronized (DocuBrowser.class) {
            if (instance == null) {
                instance = new DocuBrowser();
            }
        }

        instance.setVisible(true);
    }

    /**
     * Update the details view of the browser. This displays all the details
     * needed for each entry.
     * 
     * @param entry the entry the details shall be displayed from
     */
    @SuppressWarnings("nls")
    void updateDetails(final DocuEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be NULL");
        }

        invalidate();

        final String titleText = entry.getTitle();
        if (titleText != null) {
            titleLabel.setText(titleText);
        } else {
            titleLabel.setText(null);
        }

        final String descriptionText = entry.getDescription();
        if (descriptionText != null) {
            descriptionTitle.setVisible(true);
            descriptionContent.setText(descriptionText);
            descriptionContent.setVisible(true);
        } else {
            descriptionTitle.setVisible(false);
            descriptionContent.setVisible(false);
        }

        final String syntaxText = entry.getSyntax();
        if (syntaxText != null) {
            syntaxTitle.setVisible(true);
            syntaxContent.setText(syntaxText);
            syntaxContent.setVisible(true);
        } else {
            syntaxTitle.setVisible(false);
            syntaxContent.setVisible(false);
        }

        final String exampleText = entry.getExample();
        if (exampleText != null) {
            exampleTitle.setVisible(true);
            exampleContent.setText(exampleText);
            exampleContent.setVisible(true);
        } else {
            exampleTitle.setVisible(false);
            exampleContent.setVisible(false);
        }

        validate();
    }
}
