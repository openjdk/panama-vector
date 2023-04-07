/*
 * Copyright (c) 2022, 2023, Arm Limited. All rights reserved.
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

import compiler.lib.ir_framework.*;

import java.util.Arrays;
import java.util.Random;

import jdk.incubator.vector.*;

import jdk.test.lib.Asserts;
import jdk.test.lib.Utils;

/**
 * @test
 * @bug 8288397 8287289
 * @key randomness
 * @library /test/lib /
 * @summary Test vector gather, scatter intrinsics
 * @modules jdk.incubator.vector
 *
 * @run driver compiler.vectorapi.VectorGatherScatterTest
 */

public class VectorGatherScatterTest {
    private static final VectorSpecies<Double> D_SPECIES = DoubleVector.SPECIES_MAX;
    private static final VectorSpecies<Long> L_SPECIES = LongVector.SPECIES_MAX;
    private static final VectorSpecies<Integer> I_SPECIES =
        VectorSpecies.of(int.class, VectorShape.forBitSize(L_SPECIES.vectorBitSize() / 2));

    private static final VectorSpecies<Float> F_SPECIES =
            VectorSpecies.of(float.class, VectorShape.forBitSize(L_SPECIES.vectorBitSize() / 2));

    private static int LENGTH = 128;
    private static final Random RD = Utils.getRandomInstance();

    private static int[] ia;
    private static int[] ir;
    private static long[] la;
    private static long[] lr;
    private static float[] fa;
    private static float[] fr;
    private static double[] da;
    private static double[] dr;
    private static boolean[] m;

    static {
        ia = new int[LENGTH];
        ir = new int[LENGTH];
        la = new long[LENGTH];
        lr = new long[LENGTH];
        fa = new float[LENGTH];
        fr = new float[LENGTH];
        da = new double[LENGTH];
        dr = new double[LENGTH];
        m = new boolean[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            ia[i] = RD.nextInt(100);
            la[i] = RD.nextLong(100);
            da[i] = RD.nextDouble(100);
            m[i] = i % 2 == 0;
        }
    }

    @DontInline
    public static void verifyGather(boolean intIndex, boolean masked) {
        for (int i = 0; i < I_SPECIES.length(); i++) {
            int index = intIndex ? ia[i] : (int) la[i];
            if (!masked || m[i]) {
                Asserts.assertEquals(ir[i], ia[index]);
                Asserts.assertEquals(lr[i], la[index]);
                Asserts.assertEquals(fr[i], fa[index]);
                Asserts.assertEquals(dr[i], da[index]);
            } else {
                Asserts.assertEquals(ir[i], 0);
                Asserts.assertEquals(lr[i], 0L);
                Asserts.assertEquals(fr[i], 0F);
                Asserts.assertEquals(dr[i], 0D);
            }
        }
    }

    @DontInline
    public static void verifyScatter(boolean intIndex, boolean masked,
                                     int[] ie, long[] le, float[] fe, double[] de) {
        for (int i = 0; i < I_SPECIES.length(); i++) {
            int index = intIndex ? ia[i] : (int) la[i];
            if (!masked || m[i]) {
                ie[index] = ia[i];
                le[index] = la[i];
                fe[index] = fa[i];
                de[index] = da[i];
            }
        }
        Asserts.assertTrue(Arrays.equals(ie, ir));
        Asserts.assertTrue(Arrays.equals(le, lr));
        Asserts.assertTrue(Arrays.equals(fe, fr));
        Asserts.assertTrue(Arrays.equals(de, dr));
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_INT, "4"}, applyIfCPUFeatureOr = {"avx2", "true", "sve", "true"})
    public static void testGatherInt() {
        var idx = IntVector.fromArray(I_SPECIES, ia, 0);
        DoubleVector.fromArray(D_SPECIES, da, idx)
                .intoArray(dr, 0);
        FloatVector.fromArray(F_SPECIES, fa, idx)
                .intoArray(fr, 0);
        LongVector.fromArray(L_SPECIES, la, idx)
                .intoArray(lr, 0);
        IntVector.fromArray(I_SPECIES, ia, idx)
                .intoArray(ir, 0);

        verifyGather(true, false);
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_LONG, "4"}, applyIfCPUFeatureOr = {"avx2", "true", "sve", "true"})
    public static void testGatherLong() {
        var idx = LongVector.fromArray(L_SPECIES, la, 0);
        DoubleVector.fromArray(D_SPECIES, da, idx)
                .intoArray(dr, 0);
        FloatVector.fromArray(F_SPECIES, fa, idx)
                .intoArray(fr, 0);
        LongVector.fromArray(L_SPECIES, la, idx)
                .intoArray(lr, 0);
        IntVector.fromArray(I_SPECIES, ia, idx)
                .intoArray(ir, 0);

        verifyGather(false, false);
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_INT_MASKED, "4"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testGatherIntMasked() {
        var idx = IntVector.fromArray(I_SPECIES, ia, 0);
        var mask = VectorMask.fromArray(I_SPECIES, m, 0);
        DoubleVector.fromArray(D_SPECIES, da, idx, mask.cast(D_SPECIES))
                .intoArray(dr, 0);
        FloatVector.fromArray(F_SPECIES, fa, idx, mask.cast(F_SPECIES))
                .intoArray(fr, 0);
        LongVector.fromArray(L_SPECIES, la, idx, mask.cast(L_SPECIES))
                .intoArray(lr, 0);
        IntVector.fromArray(I_SPECIES, ia, idx, mask.cast(I_SPECIES))
                .intoArray(ir, 0);

        verifyGather(true, true);
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_LONG_MASKED, "4"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testGatherLongMasked() {
        var idx = LongVector.fromArray(L_SPECIES, la, 0);
        var mask = VectorMask.fromArray(I_SPECIES, m, 0);
        DoubleVector.fromArray(D_SPECIES, da, idx, mask.cast(D_SPECIES))
                .intoArray(dr, 0);
        FloatVector.fromArray(F_SPECIES, fa, idx, mask.cast(F_SPECIES))
                .intoArray(fr, 0);
        LongVector.fromArray(L_SPECIES, la, idx, mask.cast(L_SPECIES))
                .intoArray(lr, 0);
        IntVector.fromArray(I_SPECIES, ia, idx, mask.cast(I_SPECIES))
                .intoArray(ir, 0);

        verifyGather(false, true);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_INT, "4"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterInt() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        var idx = IntVector.fromArray(I_SPECIES, ia, 0);
        DoubleVector.fromArray(D_SPECIES, da, 0)
                .intoArray(dr, idx);
        FloatVector.fromArray(F_SPECIES, fa, 0)
                .intoArray(fr, idx);
        LongVector.fromArray(L_SPECIES, la, 0)
                .intoArray(lr, idx);
        IntVector.fromArray(I_SPECIES, ia, 0)
                .intoArray(ir, idx);

        verifyScatter(true, false, ie, le, fe, de);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_LONG, "4"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterLong() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        var idx = LongVector.fromArray(L_SPECIES, la, 0);
        DoubleVector.fromArray(D_SPECIES, da, 0)
                .intoArray(dr, idx);
        FloatVector.fromArray(F_SPECIES, fa, 0)
                .intoArray(fr, idx);
        LongVector.fromArray(L_SPECIES, la, 0)
                .intoArray(lr, idx);
        IntVector.fromArray(I_SPECIES, ia, 0)
                .intoArray(ir, idx);

        verifyScatter(false, false, ie, le, fe, de);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_INT_MASKED, "4"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterIntMasked() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        var idx = IntVector.fromArray(I_SPECIES, ia, 0);
        var mask = VectorMask.fromArray(I_SPECIES, m, 0);
        DoubleVector.fromArray(D_SPECIES, da, 0)
                .intoArray(dr, idx, mask.cast(D_SPECIES));
        FloatVector.fromArray(F_SPECIES, fa, 0)
                .intoArray(fr, idx, mask.cast(F_SPECIES));
        LongVector.fromArray(L_SPECIES, la, 0)
                .intoArray(lr, idx, mask.cast(L_SPECIES));
        IntVector.fromArray(I_SPECIES, ia, 0)
                .intoArray(ir, idx, mask.cast(I_SPECIES));

        verifyScatter(true, true, ie, le, fe, de);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_LONG_MASKED, "4"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterLongMasked() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        var idx = LongVector.fromArray(L_SPECIES, la, 0);
        var mask = VectorMask.fromArray(I_SPECIES, m, 0);
        DoubleVector.fromArray(D_SPECIES, da, 0)
                .intoArray(dr, idx, mask.cast(D_SPECIES));
        FloatVector.fromArray(F_SPECIES, fa, 0)
                .intoArray(fr, idx, mask.cast(F_SPECIES));
        LongVector.fromArray(L_SPECIES, la, 0)
                .intoArray(lr, idx, mask.cast(L_SPECIES));
        IntVector.fromArray(I_SPECIES, ia, 0)
                .intoArray(ir, idx, mask.cast(I_SPECIES));

        verifyScatter(false, true, ie, le, fe, de);
    }

    public static void main(String[] args) {
        TestFramework inst = new TestFramework();
        inst.setDefaultWarmup(10000)
                .addFlags("--add-modules=jdk.incubator.vector")
                .start();
    }
}
