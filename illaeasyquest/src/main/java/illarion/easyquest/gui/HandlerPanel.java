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

import illarion.easyquest.quest.Handler;
import illarion.easyquest.quest.HandlerTemplate;
import illarion.easyquest.quest.HandlerTemplates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

@SuppressWarnings("serial")
public class HandlerPanel extends JPanel {
    @Nonnull
    private final JComboBox handlerType;
    @Nonnull
    private final JPanel parameterPanels;
    @Nonnull
    private final JButton addHandler;
    @Nonnull
    private final JButton removeHandler;
    private final StatusDialog owner;

    public HandlerPanel(StatusDialog owner, @Nullable Handler handler) {
        super();

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        this.owner = owner;

        handlerType = new JComboBox(HandlerTemplates.getInstance().getTemplates());
        parameterPanels = new JPanel(new GridLayout(0, 1, 0, 5));
        addHandler = new JButton("+");
        removeHandler = new JButton("-");

        final JPanel header = new JPanel();
        header.add(handlerType);
        header.add(removeHandler);
        header.add(addHandler);

        add(header);
        add(parameterPanels);

        final StatusDialog dialog = this.owner;
        handlerType.addItemListener(new ItemListener() {
            public void itemStateChanged(@Nonnull ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    parameterPanels.removeAll();
                    HandlerTemplate template = (HandlerTemplate) e.getItem();
                    for (int i = 0; i < template.size(); ++i) {
                        ParameterPanel parameter = new ParameterPanel(template.getParameter(i));

                        parameterPanels.add(parameter);
                    }
                    dialog.pack();
                    dialog.validate();
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    parameterPanels.removeAll();
                    dialog.pack();
                    dialog.validate();
                }
            }
        });

        handlerType.setSelectedIndex(-1);
        if (handler != null) {
            handlerType.setSelectedItem(HandlerTemplates.getInstance().getTemplate(handler.getType()));
            Object[] parameters = handler.getParameters();
            for (int i = 0; i < parameters.length; ++i) {
                ParameterPanel panel = (ParameterPanel) parameterPanels.getComponent(i);
                panel.setParameter(parameters[i]);
            }
        }

        addHandler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.addHandler();
            }
        });

        final HandlerPanel handlerPanel = this;
        removeHandler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.removeHandler(handlerPanel);
            }
        });
    }

    public void clearSelection() {
        handlerType.setSelectedIndex(-1);
    }

    @Nullable
    public Handler getHandler() {
        Handler handler = null;
        HandlerTemplate template = (HandlerTemplate) handlerType.getSelectedItem();
        if (template != null) {
            int count = parameterPanels.getComponentCount();
            Object[] parameters = new Object[count];
            for (int i = 0; i < count; ++i) {
                ParameterPanel p = (ParameterPanel) parameterPanels.getComponent(i);
                parameters[i] = p.getParameter();
            }

            handler = new Handler(template.getName(), parameters);
        }
        return handler;
    }
}