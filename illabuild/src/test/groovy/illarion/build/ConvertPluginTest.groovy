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
package illarion.build

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import org.hamcrest.Matchers

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.instanceOf
import static org.testng.Assert.assertTrue

/**
 * This application is used to test of the convert plugin is properly applied.
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ConvertPluginTest {
    private Project project

    @BeforeTest
    public void setUp() {
        project = ProjectBuilder.builder().build();
    }

    @Test
    public void applyTest() {
        project.plugins.apply(ConvertPlugin)

        assertTrue(project.plugins.hasPlugin(ConvertPlugin), "Convert plugin not present")
        assertThat("Resource task", project.tasks.getByName("convertResources"),
                Matchers.is(instanceOf(ResourceConverter)))
        assertThat("Converter convention",
                project.convention.plugins.get("converter"), Matchers.is(instanceOf(ConvertPluginConvention)))
    }

    @Test
    public void applyStringTest() {
        project.plugins.apply('convert')
        assertTrue(project.plugins.hasPlugin(ConvertPlugin), "Convert plugin not present")
    }
}
