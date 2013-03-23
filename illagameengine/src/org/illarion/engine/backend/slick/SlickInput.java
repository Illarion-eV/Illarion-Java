/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.slick;

import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.InputListener;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class SlickInput implements Input, org.newdawn.slick.InputListener {
    @Override
    public void controllerLeftPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerLeftReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerRightPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerRightReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerUpPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerUpReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerDownPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerDownReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerButtonPressed(final int i, final int i2) {
        // controller input is ignored
    }

    @Override
    public void controllerButtonReleased(final int i, final int i2) {
        // controller input is ignored
    }

    @Override
    public void keyPressed(final int i, final char c) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void keyReleased(final int i, final char c) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseWheelMoved(final int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseClicked(final int i, final int i2, final int i3, final int i4) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mousePressed(final int i, final int i2, final int i3) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseReleased(final int i, final int i2, final int i3) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseMoved(final int i, final int i2, final int i3, final int i4) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseDragged(final int i, final int i2, final int i3, final int i4) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void poll() {

    }

    @Override
    public void setInput(final org.newdawn.slick.Input input) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isAcceptingInput() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void inputEnded() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void inputStarted() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setListener(@Nonnull final InputListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isButtonDown(@Nonnull final Button button) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isKeyDown(@Nonnull final Key key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMouseX() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMouseY() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
