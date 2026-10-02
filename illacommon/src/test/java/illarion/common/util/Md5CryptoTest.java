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
package illarion.common.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class Md5CryptoTest {
    @DataProvider(name = "utf8ReferenceVectors")
    public Object[][] utf8ReferenceVectors() {
        // Generated with OpenSSL 3.5.7: passwd -1 -salt illarion -stdin, supplying UTF-8 bytes.
        // These fixed vectors require no external executable when running the tests.
        return new Object[][] {
            {
                "",
                "$1$illarion$YROf5r80Cnkw.a9itKUoU."
            },
            {
                "password",
                "$1$illarion$9b6RKOLo687hFmPD9Q/Ny."
            },
            {
                "123456789012345",
                "$1$illarion$OOaQ4Cm/f/sLDV.ylSGcM1"
            },
            {
                "1234567890123456",
                "$1$illarion$NdJ7PTq/sHKaOSCJ4JR0X."
            },
            {
                "12345678901234567",
                "$1$illarion$Uk8YToilqiEnOyMMxelqn/"
            },
            {
                "p\u00e4ssword",
                "$1$illarion$eSUzk/eMeOAnsKMZNEAmP/"
            },
            {
                "p\u20acssword",
                "$1$illarion$sJAplPnuaQUH8OUe9xZ791"
            },
            {
                "\u4e2d\u6587",
                "$1$illarion$IJpGMRg9BY5hAL2vGE.oO."
            },
            {
                "test\ud83d\ude00",
                "$1$illarion$zoaKH8fRgwNLVpaZXohQw0"
            },
            {
                "12345678901234\u00e4",
                "$1$illarion$jQj.rxKr7b2yF.xcY1xlX."
            },
            {
                "123456789012345\u00e4",
                "$1$illarion$Zek0TyPS9GbDyIpVqKKuS1"
            },
            {
                "\u00e4\u00f6\u00fc\u00df",
                "$1$illarion$xHwawW7HqlOPDD5TpRFJU/"
            },
            {
                "\u00bb\u00f8\u00a6\u00ff\u00dc\u00bf\u00f6\u00cf\u00e0\u00e4" +
                    "\u00ff\u00ed\u00a3\u00ca\u00b7\u00cc\u00c3\u00b5\u00be\u00b5",
                "$1$illarion$JDkUWndLcOijsPgbus0TE1"
            }
        };
    }

    @Test(dataProvider = "utf8ReferenceVectors")
    public void testCryptMatchesUtf8Reference(String password, String expectedHash) {
        assertEquals(new Md5Crypto().crypt(password, "illarion"), expectedHash);
    }
}
