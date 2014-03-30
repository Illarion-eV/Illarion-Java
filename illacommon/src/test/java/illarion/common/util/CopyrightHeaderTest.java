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
package illarion.common.util;

import org.testng.annotations.Test;

import java.io.StringWriter;

import static org.testng.Assert.*;

public class CopyrightHeaderTest {
    @Test
    public void testWriteTo() throws Exception {
        CopyrightHeader header = new CopyrightHeader(80, null, null, null, null);
        StringWriter writer = new StringWriter();
        header.writeTo(writer);

        String[] lines = writer.toString().split("\n");
        for (String line : lines) {
            if (line.length() > 80) {
                fail();
            }
        }
    }

    @Test
    public void testIsLicenseText() throws Exception {
        CopyrightHeader header1 = new CopyrightHeader(80, null, null, null, null);
        CopyrightHeader header2 = new CopyrightHeader(160, null, null, null, null);
        CopyrightHeader header3 = new CopyrightHeader(80, null, null, "#", null);

        StringWriter writer = new StringWriter();
        header1.writeTo(writer);
        String headerString1 = writer.toString();
        writer.getBuffer().setLength(0);

        header2.writeTo(writer);
        String headerString2 = writer.toString();
        writer.getBuffer().setLength(0);

        header3.writeTo(writer);
        String headerString3 = writer.toString();
        writer.getBuffer().setLength(0);

        assertTrue(header1.isLicenseText(headerString1));
        assertTrue(header1.isLicenseText(headerString2));
        assertFalse(header1.isLicenseText(headerString3));

        assertTrue(header2.isLicenseText(headerString1));
        assertTrue(header2.isLicenseText(headerString2));
        assertFalse(header2.isLicenseText(headerString3));

        assertFalse(header3.isLicenseText(headerString1));
        assertFalse(header3.isLicenseText(headerString2));
        assertTrue(header3.isLicenseText(headerString3));
    }
}
