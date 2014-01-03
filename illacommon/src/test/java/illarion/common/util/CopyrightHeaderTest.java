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
