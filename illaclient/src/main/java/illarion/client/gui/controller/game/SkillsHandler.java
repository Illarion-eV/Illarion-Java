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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.ElementShowEvent;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.gui.SkillGui;
import illarion.client.util.Lang;
import illarion.client.util.UpdateTask;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.data.Skill;
import illarion.common.data.SkillGroup;
import illarion.common.data.SkillGroups;
import org.illarion.engine.GameContainer;
import org.illarion.nifty.controls.Progress;
import org.illarion.nifty.controls.progress.builder.ProgressBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This handler controls the skill window.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SkillsHandler implements SkillGui, ScreenController, UpdatableHandler {
    /**
     * The Nifty-GUI instance this handler is bound to.
     */
    private Nifty nifty;

    /**
     * The screen instance this handler is bound to.
     */
    private Screen screen;

    /**
     * The window control that contains the skill information.
     */
    @Nullable
    private Window skillWindow;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        skillWindow = screen.findNiftyControl("characterInformation", Window.class);

        skillWindow.getElement().setConstraintX(new SizeValue(IllaClient.getCfg().getString("skillWindowPosX")));
        skillWindow.getElement().setConstraintY(new SizeValue(IllaClient.getCfg().getString("skillWindowPosY")));
        //skillWindow.getElement().getParent().layoutElements();

        createSkillEntries();
    }

    @Override
    public void onStartScreen() {
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        nifty.unsubscribeAnnotations(this);

        IllaClient.getCfg().set("skillWindowPosX", Integer.toString(skillWindow.getElement().getX()) + "px");
        IllaClient.getCfg().set("skillWindowPosY", Integer.toString(skillWindow.getElement().getY()) + "px");
    }

    @Override
    public void showSkillWindow() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (skillWindow != null) {
                    skillWindow.getElement().show(new EndNotify() {
                        @Override
                        public void perform() {
                            skillWindow.moveToFront();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void hideSkillWindow() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (skillWindow != null) {
                    skillWindow.getElement().hide();
                }
            }
        });
    }

    @Override
    public void toggleSkillWindow() {
        if (skillWindow != null) {
            if (skillWindow.getElement().isVisible()) {
                hideSkillWindow();
            } else {
                showSkillWindow();
            }
        }
    }

    /**
     * This function creates the entries for every single skill.
     */
    private void createSkillEntries() {
        Element content = skillWindow.getElement().findElementById("#textContent");

        for (SkillGroup group : SkillGroups.getInstance().getSkillGroups()) {
            String groupId = content.getId() + "#group" + Integer.toString(group.getGroupId());
            PanelBuilder groupPanel = new PanelBuilder(groupId);
            groupPanel.childLayoutVertical();
            groupPanel.height(SizeValue.px(0));
            groupPanel.alignCenter();
            groupPanel.valignTop();

            LabelBuilder headline = new LabelBuilder(groupId + "#headline");
            headline.font("menuFont");

            if (Lang.getInstance().isGerman()) {
                headline.label(group.getNameGerman());
            } else {
                headline.label(group.getNameEnglish());
            }
            headline.width(SizeValue.wildcard());
            headline.height(SizeValue.px(0));
            groupPanel.control(headline);

            for (Skill skill : group.getSkills()) {
                String skillId = groupId + "#skill" + Integer.toString(skill.getId());
                PanelBuilder skillPanel = new PanelBuilder(skillId);
                skillPanel.childLayoutCenter();
                skillPanel.width(content.getConstraintWidth().toString());

                LabelBuilder skillName = new LabelBuilder(skillId + "#name");
                if (Lang.getInstance().isGerman()) {
                    skillName.label(skill.getNameGerman());
                } else {
                    skillName.label(skill.getNameEnglish());
                }
                skillName.font("textFont");
                skillName.width(content.getConstraintWidth().toString());
                skillName.height(SizeValue.px(0));
                skillName.alignLeft();
                skillName.textHAlignLeft();
                skillPanel.control(skillName);

                ProgressBuilder progress = new ProgressBuilder(skillId + "#progress");
                progress.width(SizeValue.px(130));
                progress.height(SizeValue.px(0));
                progress.alignRight();
                progress.valignBottom();
                skillPanel.control(progress);

                LabelBuilder skillValue = new LabelBuilder(skillId + "#value");
                skillValue.label("0");
                skillValue.width(content.getConstraintWidth().toString());
                skillValue.font("textFont");
                skillValue.height(SizeValue.px(0));
                skillValue.alignRight();
                skillValue.textHAlignRight();
                skillPanel.control(skillValue);

                groupPanel.panel(skillPanel);
            }

            groupPanel.build(nifty, screen, content);
        }
    }

    /**
     * This value is set {@code true} in case the layout needs to be updated.
     */
    private boolean layoutDirty;

    @Override
    public void updateSkill(@Nonnull final Skill skill, final int value, final int minor) {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                internalUpdateSkill(skill, value, minor);
            }
        });
    }

    @Nullable
    private Element getSkillPanel(@Nonnull Skill skill) {
        if (skillWindow == null) {
            return null;
        }
        @Nullable Element windowElement = skillWindow.getElement();
        if (windowElement == null) {
            return null;
        }
        return windowElement.findElementById("#skill" + skill.getId());
    }

    /**
     * This function will update the data of a single skill.
     *
     * @param skill the skill that receives the update
     * @param value the new value of the skill
     */
    private void internalUpdateSkill(@Nonnull Skill skill, int value, int minor) {
        if (!World.getNet().isLoginDone()) {
            return;
        }
        @Nullable Element skillPanel = skillWindow.getElement().findElementById("#skill" + skill.getId());
        int SkillHeight = 22;
        if (skillPanel == null) {
            return;
        }

        if (value == 0) {
            SkillHeight = 0;
        }
        @Nullable Element skillPanelWindowContent = skillPanel.getParent();
        @Nullable Element skillPanelWindow = skillPanelWindowContent.getParent();
        skillPanelWindow.setConstraintHeight(SizeValue.def());
        skillPanelWindowContent.setConstraintHeight(SizeValue.def());
        skillPanelWindowContent.setMarginBottom(SizeValue.px(5));
        skillPanelWindowContent.findElementById("#headline").setConstraintHeight(SizeValue.px(28));

        skillPanel.setConstraintHeight(SizeValue.px(SkillHeight));

        Element valueLabel = skillPanel.findElementById("#value");
        TextRenderer valueTextRenderer = valueLabel.getRenderer(TextRenderer.class);

        String newValue = Integer.toString(value);
        boolean skillChanged = !valueTextRenderer.getOriginalText().equals(newValue);
        valueTextRenderer.setText(newValue);
        valueLabel.setConstraintHeight(SizeValue.px(SkillHeight));

        Element progressBar = skillPanel.findElementById("#progress");
        float progress = (value == 100) ? 1.f : (minor / 10000.f);
        progressBar.getNiftyControl(Progress.class).setProgress(progress);
        progressBar.setConstraintHeight(SizeValue.px(Math.max(SkillHeight - 3, 0)));
        progressBar.setMarginRight(SizeValue.px(40));

        Element nameLabel = skillPanel.findElementById("#name");
        nameLabel.setConstraintHeight(SizeValue.px(SkillHeight));
        nameLabel.setMarginLeft(SizeValue.px(5));

        if (World.getNet().isLoginDone() && skillChanged) {
            screen.findElementById("openSkillsBtn").startEffect(EffectEventId.onCustom, null, "pulse");
            MapTile playerTile = World.getMap().getMapAt(World.getPlayer().getLocation());
            if (playerTile == null) {
                LOGGER.error("Tile below the player is NULL?!");
            } else {
                playerTile.showEffect(41);
            }
        }

        layoutDirty = true;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillsHandler.class);

    private void updateVisibility() {
        Element content = skillWindow.getElement().findElementById("#textContent");
        updateVisibilityOfElement(content);
    }

    private static void updateVisibilityOfElement(@Nonnull Element target) {
        if ("0px".equals(target.getConstraintHeight().toString())) {
            target.setVisible(false);
        } else {
            for (Element child : target.getChildren()) {
                updateVisibilityOfElement(child);
            }
        }
    }

    @Override
    public void update(GameContainer container, int delta) {
        if (layoutDirty) {
            layoutDirty = false;

            updateVisibility();
            skillWindow.getElement().resetLayout();
            skillWindow.getElement().layoutElements();
        }
    }

    /**
     * The event handler for clicks on the skill window button.
     *
     * @param topic the topic of the event
     * @param event the event data
     */
    @NiftyEventSubscriber(id = "openSkillsBtn")
    public void onSkillWindowButtonClickedEvent(String topic, ButtonClickedEvent event) {
        toggleSkillWindow();
    }

    @NiftyEventSubscriber(id = "characterInformation")
    public void onSkillWindowShowEvent(String topic, ElementShowEvent event) {
        updateVisibility();
    }
}
