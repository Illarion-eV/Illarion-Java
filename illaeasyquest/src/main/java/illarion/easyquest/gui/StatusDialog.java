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
package illarion.easyquest.gui;

import illarion.easyquest.Lang;
import illarion.easyquest.quest.Handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StatusDialog extends JDialog {

    @Nonnull
    private final JTextField name;
    @Nonnull
    private final JCheckBox start;
    private final Box handlerPanels;
    @Nonnull
    private final JButton okay;
    @Nonnull
    private final JButton cancel;

    public StatusDialog(Frame owner) {
        super(owner);
        setTitle(Lang.getMsg(getClass(), "title"));

        final JPanel main = new JPanel();
        handlerPanels = Box.createVerticalBox();
        final Box buttons = Box.createHorizontalBox();
        final JLabel label = new JLabel(Lang.getMsg(getClass(), "name") + ":");
        name = new JTextField(15);
        start = new JCheckBox(Lang.getMsg(getClass(), "start"));
        okay = new JButton(Lang.getMsg(getClass(), "ok"));
        cancel = new JButton(Lang.getMsg(getClass(), "cancel"));

        setResizable(false);

        buttons.add(Box.createHorizontalGlue());
        buttons.add(okay);
        buttons.add(Box.createHorizontalStrut(5));
        buttons.add(cancel);
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));

        main.add(label);
        main.add(name);
        main.add(start);
        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        handlerPanels.setBorder(BorderFactory.createTitledBorder(Lang.getMsg(getClass(), "handlers")));

        getRootPane().setDefaultButton(okay);

        add(main, BorderLayout.NORTH);
        add(handlerPanels, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        pack();
    }

    public String getName() {
        return name.getText();
    }

    public void setName(@Nonnull String value) {
        name.setText(value);
    }

    public boolean isStart() {
        return start.isSelected();
    }

    public void setStart(boolean value) {
        start.setSelected(value);
    }

    @Nonnull
    public Handler[] getHandlers() {
        int count = (handlerPanels.getComponentCount() + 1) / 2;
        List<Handler> handlers = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            HandlerPanel hp = (HandlerPanel) handlerPanels.getComponent(2 * i);
            Handler h = hp.getHandler();
            if (h != null) {
                handlers.add(h);
            }
        }
        return handlers.toArray(new Handler[handlers.size()]);
    }

    public void setHandlers(@Nullable Handler[] handlers) {
        handlerPanels.removeAll();

        if (handlers != null && handlers.length > 0) {
            handlerPanels.add(new HandlerPanel(this, handlers[0]));
            for (int i = 1; i < handlers.length; ++i) {
                handlerPanels.add(new JSeparator());
                handlerPanels.add(new HandlerPanel(this, handlers[i]));
            }
        } else {
            handlerPanels.add(new HandlerPanel(this, null));
        }

        pack();
        validate();
    }

    public void addHandler() {
        handlerPanels.add(new JSeparator());
        handlerPanels.add(new HandlerPanel(this, null));
        pack();
        validate();
    }

    public void removeHandler(HandlerPanel handler) {
        if (handlerPanels.getComponentCount() > 1) {
            int z = handlerPanels.getComponentZOrder(handler);
            if (z != 0) {
                handlerPanels.remove(z - 1);
            } else {
                handlerPanels.remove(z + 1);
            }
            handlerPanels.remove(handler);
        } else {
            ((HandlerPanel) handlerPanels.getComponent(0)).clearSelection();
        }

        pack();
        validate();
    }

    public void addOkayListener(ActionListener listener) {
        okay.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
}