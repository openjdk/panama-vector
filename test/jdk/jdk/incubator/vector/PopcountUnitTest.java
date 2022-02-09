/*
 * Copyright (c) 2017, 2020, Oracle and/or its affiliates. All rights reserved.
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

/**
 * @test
 * @modules jdk.incubator.vector
 * @requires vm.compiler2.enabled
 */

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.Vector;

import java.util.Arrays;
import java.util.stream.IntStream;

public class PopcountUnitTest {
    static final VectorSpecies<Long> SPECIESL =
            LongVector.SPECIES_256;
    static final VectorSpecies<Integer> SPECIESI =
            IntVector.SPECIES_256;

    static final int SIZE = 1024;
    static int[] a = new int[SIZE];
    static int[] b = new int[SIZE];
    static long[] c = new long[SIZE];
    static long[] d = new long[SIZE];

    static {
        for (int i = 0; i < SIZE; i++) {
            a[i] = 7;
            c[i] = -63;
        }
    }

    static void workload() {
        for (int i = 0; i < a.length; i += SPECIESI.length()) {
            IntVector av = IntVector.fromArray(SPECIESI, a, i);
            av.popcnt().intoArray(b, i);
        }
         for (int i = 0; i < c.length; i += SPECIESL.length()) {
            LongVector cv = LongVector.fromArray(SPECIESL, c, i);
            cv.popcnt().intoArray(d, i);
        }
    }

    public static void main(String args[]) {
        for (int i = 0; i < 30_0000; i++) {
            workload();
        }
        for (int i = 0; i < a.length; i++) {
            if (b[i] != Integer.bitCount(a[i]))
                throw new AssertionError();
        }
        for (int i = 0; i < c.length; i++) {
            if (d[i] != Long.bitCount(c[i]))
                throw new AssertionError();
        }
        System.out.println("b[0] = " + b[0]);
        System.out.println("d[0] = " + d[0]);
    }
}
