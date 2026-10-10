/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2026 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import illarion.client.IllaClient;
import illarion.common.config.ConfigSystem;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.graphic.GraphicResolution;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.testng.Assert.*;

public class OwnAvatarOptionTest {
    @DataProvider
    public Object[][] languages() {
        return new Object[][]{{Locale.ENGLISH}, {Locale.GERMAN}};
    }

    @Test(dataProvider = "languages")
    public void optionCanBeSavedReopenedAndCancelled(Locale locale) throws Exception {
        Path directory = Files.createTempDirectory("illarion-own-avatar-option");
        Path configFile = directory.resolve("client.xcfgz");
        ConfigSystem config = new ConfigSystem(configFile);
        Object client = IllaClient.getInstance();
        Field configField = IllaClient.class.getDeclaredField("cfg");
        Field containerField = IllaClient.class.getDeclaredField("gameContainer");
        configField.setAccessible(true);
        containerField.setAccessible(true);
        Object previousConfig = configField.get(client);
        Object previousContainer = containerField.get(client);
        Nifty nifty = null;

        try {
            configField.set(client, config);
            containerField.set(client, stub(DesktopGameContainer.class, (proxy, method, arguments) -> {
                if ("getFullScreenResolutions".equals(method.getName())) {
                    return new GraphicResolution[]{new GraphicResolution(800, 600, 32, 60)};
                }

                throw new AssertionError(method.getName());
            }));
            nifty = createNifty(locale);
            nifty.addScreen("login", new ScreenBuilder("login", new DefaultScreenController()).build(nifty));
            nifty.fromXml("illarion/client/gui/xml/options.xml", "options");
            Screen screen = nifty.getScreen("options");
            OptionScreenController controller = (OptionScreenController) screen.getScreenController();
            CheckBox option = screen.findNiftyControl("showOwnAvatarTag", CheckBox.class);
            assertNotNull(option);
            assertFalse(option.isChecked(), "Existing configurations must start with this option disabled");

            screen.layoutLayers();
            assertLayout(screen, option, locale);
            option.setChecked(true);
            controller.onSaveButtonClickedEvent("saveButton", null);
            assertTrue(new ConfigSystem(configFile).getBoolean("showOwnAvatarTag"));

            controller.onStartScreen();
            assertTrue(option.isChecked(), "Reopening the options must restore the saved choice");
            option.setChecked(false);
            controller.onCancelButtonClickedEvent("cancelButton", null);
            assertTrue(config.getBoolean("showOwnAvatarTag"));
            assertTrue(new ConfigSystem(configFile).getBoolean("showOwnAvatarTag"));
            controller.onStartScreen();
            assertTrue(option.isChecked(), "Cancelling must discard the checkbox change");

            option.setChecked(false);
            controller.onSaveButtonClickedEvent("saveButton", null);
            assertFalse(new ConfigSystem(configFile).getBoolean("showOwnAvatarTag"));
            controller.onStartScreen();
            assertFalse(option.isChecked());
        } finally {
            if (nifty != null) {
                nifty.exit();
            }

            configField.set(client, previousConfig);
            containerField.set(client, previousContainer);
            Files.deleteIfExists(configFile);
            Files.deleteIfExists(directory);
        }
    }

    private static void assertLayout(Screen screen, CheckBox option, Locale locale) {
        Element checkbox = option.getElement();
        Element row = checkbox.getParent();
        Element label = row.getChildren().get(0);
        String expected = ResourceBundle.getBundle("options", locale).getString("showOwnAvatarTag");
        assertEquals(label.getRenderer(TextRenderer.class).getOriginalText(), expected);
        assertTrue(checkbox.getWidth() > 0);
        assertTrue(checkbox.getHeight() > 0);
        assertTrue(label.getX() + label.getWidth() <= checkbox.getX(), "The label must not cover the checkbox");
        Element save = screen.findElementById("saveButton");
        assertTrue(row.getY() + row.getHeight() <= save.getY(), "The extra row must fit above the buttons");
    }

    private static Nifty createNifty(Locale locale) {
        RenderFont font = stub(RenderFont.class, (proxy, method, arguments) -> {
            switch (method.getName()) {
                case "getHeight":
                    return 16;
                case "getWidth":
                    return ((String) arguments[0]).length() * 8;
                case "getCharacterAdvance":
                    return 8;
                default:
                    return null;
            }
        });
        RenderImage image = stub(RenderImage.class, (proxy, method, arguments) -> {
            if ("getWidth".equals(method.getName()) || "getHeight".equals(method.getName())) {
                return 64;
            }

            return null;
        });
        RenderDevice render = stub(RenderDevice.class, (proxy, method, arguments) -> {
            switch (method.getName()) {
                case "getWidth":
                    return 800;
                case "getHeight":
                    return 600;
                case "createFont":
                    return font;
                case "createImage":
                    return image;
                default:
                    return null;
            }
        });
        SoundDevice sound = stub(SoundDevice.class, (proxy, method, arguments) -> null);
        InputSystem input = stub(InputSystem.class, (proxy, method, arguments) -> {
            return method.getReturnType() == boolean.class ? false : null;
        });
        Nifty nifty = new Nifty(render, sound, input, () -> 1000L);
        nifty.setLocale(locale);
        nifty.loadStyleFile("nifty-illarion-style.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        return nifty;
    }

    private static <T> T stub(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, arguments) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        switch (method.getName()) {
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == arguments[0];
                            case "toString":
                                return type.getSimpleName();
                            default:
                                throw new AssertionError(method.getName());
                        }
                    }

                    return handler.invoke(proxy, method, arguments);
                }));
    }
}
