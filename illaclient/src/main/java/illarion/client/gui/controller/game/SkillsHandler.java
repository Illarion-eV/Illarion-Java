/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
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
import illarion.client.net.server.events.LoginFinishedEvent;
import illarion.client.util.Lang;
import illarion.client.util.UpdateTask;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.data.Skill;
import illarion.common.data.SkillGroup;
import illarion.common.data.SkillGroups;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.GameContainer;

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
    private Window skillWindow;

    /**
     * This flag is set {@code true} once the login to the server is done.
     */
    private boolean loginDone;

    @Override
    public void bind(final Nifty nifty, @Nonnull final Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        skillWindow = screen.findNiftyControl("characterInformation", Window.class);

        skillWindow.getElement().setConstraintX(new SizeValue(IllaClient.getCfg().getString("skillWindowPosX")));
        skillWindow.getElement().setConstraintY(new SizeValue(IllaClient.getCfg().getString("skillWindowPosY")));
        skillWindow.getElement().getParent().layoutElements();

        createSkillEntries();
    }

    @Override
    public void onStartScreen() {
        nifty.subscribeAnnotations(this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void onEndScreen() {
        nifty.unsubscribeAnnotations(this);
        AnnotationProcessor.unprocess(this);

        IllaClient.getCfg().set("skillWindowPosX", Integer.toString(skillWindow.getElement().getX()) + "px");
        IllaClient.getCfg().set("skillWindowPosY", Integer.toString(skillWindow.getElement().getY()) + "px");
    }

    @Override
    public void showSkillWindow() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
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
            public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
                if (skillWindow != null) {
                    skillWindow.getElement().hide();
                }
            }
        });
    }

    @Override
    public void toggleSkillWindow() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
                if (skillWindow != null) {
                    if (skillWindow.getElement().isVisible()) {
                        hideSkillWindow();
                    } else {
                        showSkillWindow();
                    }
                }
            }
        });
    }

    /**
     * This function creates the entries for every single skill.
     */
    private void createSkillEntries() {
        final Element content = skillWindow.getElement().findElementById("#textContent");

        int groupCnt = 0;
        for (final SkillGroup group : SkillGroups.getInstance().getSkillGroups()) {
            final String groupId = content.getId() + "#group" + Integer.toString(groupCnt++);
            final PanelBuilder groupPanel = new PanelBuilder(groupId);
            groupPanel.childLayoutVertical();
            groupPanel.height("0px");
            groupPanel.alignCenter();
            groupPanel.valignTop();

            final LabelBuilder headline = new LabelBuilder(groupId + "#headline");
            headline.font("menuFont");

            if (Lang.getInstance().isGerman()) {
                headline.label(group.getNameGerman());
            } else {
                headline.label(group.getNameEnglish());
            }
            headline.width("*");
            headline.height("0px");
            groupPanel.control(headline);

            for (final Skill skill : group.getSkills()) {
                final String skillId = groupId + "#skill" + Integer.toString(skill.getId());
                final PanelBuilder skillPanel = new PanelBuilder(skillId);
                skillPanel.childLayoutCenter();
                skillPanel.width(content.getConstraintWidth().toString());

                final LabelBuilder skillName = new LabelBuilder(skillId + "#name");
                if (Lang.getInstance().isGerman()) {
                    skillName.label(skill.getNameGerman());
                } else {
                    skillName.label(skill.getNameEnglish());
                }
                skillName.font("textFont");
                skillName.width(content.getConstraintWidth().toString());
                skillName.height("0px");
                skillName.alignLeft();
                skillName.textHAlignLeft();
                skillPanel.control(skillName);

                final LabelBuilder skillValue = new LabelBuilder(skillId + "#value");
                skillValue.label("0");
                skillValue.width(content.getConstraintWidth().toString());
                skillValue.font("textFont");
                skillValue.height("0px");
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
            public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
                internalUpdateSkill(skill, value);
            }
        });
    }

    /**
     * This function will update the data of a single skill.
     *
     * @param skill the skill that receives the update
     * @param value the new value of the skill
     */
    private void internalUpdateSkill(@Nonnull final Skill skill, final int value) {
        @Nullable final Element skillPanel = skillWindow.getElement().findElementById("#skill" + skill.getId());
        int SkillHeight = 18;
        if (skillPanel == null) {
            return;
        }

        boolean skillChanged = false;
        if (value == 0) {
            SkillHeight = 0;
        }
        @Nullable final Element skillPanelWindowContent = skillPanel.getParent();
        if (skillPanelWindowContent != null) {
            @Nullable final Element skillPanelWindow = skillPanelWindowContent.getParent();
            if (skillPanelWindow != null) {
                skillPanelWindow.setConstraintHeight(null);
            }
            skillPanelWindowContent.setConstraintHeight(null);
            skillPanelWindowContent.setMarginBottom(SizeValue.px(5));
            skillPanelWindowContent.findElementById("#headline").setConstraintHeight(SizeValue.px(24));
        }

        skillPanel.setConstraintHeight(SizeValue.px(SkillHeight));

        final Element valueLabel = skillPanel.findElementById("#value");
        final TextRenderer valueTextRenderer = valueLabel.getRenderer(TextRenderer.class);

        final String newValue = Integer.toString(value);
        skillChanged = !valueTextRenderer.getOriginalText().equals(newValue);
        valueTextRenderer.setText(newValue);
        valueLabel.setConstraintHeight(SizeValue.px(SkillHeight));

        final Element nameLabel = skillPanel.findElementById("#name");
        nameLabel.setConstraintHeight(SizeValue.px(SkillHeight));
        nameLabel.setMarginLeft(SizeValue.px(5));

        if (loginDone && skillChanged) {
            screen.findElementById("openSkillsBtn").startEffect(EffectEventId.onCustom, null, "pulse");
            final MapTile playerTile = World.getMap().getMapAt(World.getPlayer().getLocation());
            if (playerTile == null) {
                LOGGER.error("Tile below the player is NULL?!");
            } else {
                playerTile.showEffect(41);
            }
        }

        layoutDirty = true;

        if (skillWindow.getElement().isVisible()) {
            toggleSkillWindow();
            toggleSkillWindow();
        }
    }

    private static final Logger LOGGER = Logger.getLogger(SkillsHandler.class);

    private void updateVisibility() {
        final Element content = skillWindow.getElement().findElementById("#textContent");
        updateVisibilityOfElement(content);
    }

    private static void updateVisibilityOfElement(@Nonnull final Element target) {
        if ((target.getConstraintHeight() != null) && "0px".equals(target.getConstraintHeight().toString())) {
            target.setVisible(false);
        } else {
            for (final Element child : target.getChildren()) {
                updateVisibilityOfElement(child);
            }
        }
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        if (layoutDirty) {
            layoutDirty = false;

            updateVisibility();
            skillWindow.getElement().layoutElements();
        }
    }

    /**
     * This event handler waits for the login done event.
     *
     * @param data the event data
     */
    @EventSubscriber
    public void onLoginDoneReceived(final LoginFinishedEvent data) {
        loginDone = true;
    }

    /**
     * The event handler for clicks on the skill window button.
     *
     * @param topic the topic of the event
     * @param event the event data
     */
    @NiftyEventSubscriber(id = "openSkillsBtn")
    public void onSkillWindowButtonClickedEvent(final String topic, final ButtonClickedEvent event) {
        toggleSkillWindow();
    }

    @NiftyEventSubscriber(id = "characterInformation")
    public void onSkillWindowShowEvent(final String topic, final ElementShowEvent event) {
        updateVisibility();
    }
}
