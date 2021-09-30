/*
 * Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
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

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;

/*
 * @test
 * @bug 8274533
 * @summary Test intrinsic for VectorMask.toLong operation
 * @modules jdk.incubator.vector
 * @run main VectorMaskToLong -XX:CompileCommand=compileonly,VectorMaskToLong::maskFromToLongInt128VectorTest
 */

public class VectorMaskToLong {
    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_128;
    static long maskFromToLongInt128VectorTest(long inputLong) {
        var vmask = VectorMask.fromLong(SPECIES, inputLong);
        return vmask.toLong();
    }

    static long expected(long inputLong) {
        return inputLong & (((1L << (SPECIES.length() - 1)) << 1) - 1);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++) {
            long res = maskFromToLongInt128VectorTest(-1);
            long exp = expected(-1);
            if (res != exp) {
                System.err.println("Failure: res = " + res + " exp = " + exp);
                System.exit(-1);
            }
        }
    }
}
