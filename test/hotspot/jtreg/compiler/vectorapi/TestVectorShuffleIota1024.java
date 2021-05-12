/*
 * Copyright (c) 2021, Huawei Technologies Co. Ltd. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package compiler.vectorapi;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorShuffle;

import org.testng.Assert;
import org.testng.annotations.Test;


/*
 * @test
 * @bug 8266720
 * @modules jdk.incubator.vector
 * @run testng/othervm compiler.vectorapi.TestVectorShuffleIotaByte1024
 */

@Test
public class TestVectorShuffleIotaByte1024 {
    static final VectorSpecies<Byte> SPECIESb_1024 = ByteVector.SPECIES_MAX;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 50000);

    static final byte[] ab_1024 = {50, 49, 47, 53, 47, 49, 50, 48, 50, 32, 46, 116, 105, 32, 115,
                                   110, 101, 104, 116, 103, 110, 101, 114, 116, 115, 32, 101,
                                   99, 110, 101, 115, 101, 114, 112, 44, 101, 118, 111, 108,
                                   32, 115, 110, 101, 112, 114, 97, 104, 115, 32, 101, 99, 110,
                                   101, 115, 98, 65, 46, 117, 111, 121, 32, 101, 118, 111, 108,
                                   32, 73, 46, 103, 110, 97, 117, 72, 32, 71, 78, 65, 87, 45, 45,
                                   33, 117, 111, 121, 32, 103, 110, 105, 115, 115, 105, 77, 46, 117,
                                   111, 121, 32, 111, 116, 32, 114, 101, 116, 116, 101, 108, 32,
                                   104, 116, 52, 32, 121, 109, 32, 115, 105, 32, 115, 105, 104, 116,
                                   44, 121, 116, 101, 101, 119, 83};

    static final byte[] expected_1024 = {0, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 48,
                                         51, 54, 57, 60, 63, 66, 69, 72, 75, 78, 81, 84, 87, 90, 93, 96,
                                         99, 102, 105, 108, 111, 114, 117, 120, 123, 126, -127, -124,
                                         -121, -118, -115, -112, -109, -106, -103, -100, -97, -94, -91,
                                         -88, -85, -82, -79, -76, -73, -70, -67, -64, -61, -58, -55, -52,
                                         -49, -46, -43, -40, -37, -34, -31, -28, -25, -22, -19, -16, -13,
                                         -10, -7, -4, -1, -126, -123, -120, -117, -114, -111, -108, -105,
                                         -102, -99, -96, -93, -90, -87, -84, -81, -78, -75, -72, -69, -66,
                                         -63, -60, -57, -54, -51, -48, -45, -42, -39, -36, -33, -30, -27,
                                         -24, -21, -18, -15, -12, -9, -6, -3};

    static void testShuffleIota_1024() {
        ByteVector bv = (ByteVector) VectorShuffle.iota(SPECIESb_1024, 0, 3, false).toVector();
        bv4.intoArray(ab_1024, 0);
    }

    static void testIota_1024() {
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            testShuffleIota_1024();
        }
        Assert.assertEquals(ab_1024, expected_1024);
    }

    @Test
    static void testIota() {
        if (SPECESb_1024.length() == 1024) {
            testIota_1024();
        }
    }
}
