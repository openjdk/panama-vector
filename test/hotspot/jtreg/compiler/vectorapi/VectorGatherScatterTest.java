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

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;
import jdk.incubator.vector.*;
import jdk.test.lib.Asserts;
import jdk.test.lib.Utils;
import static java.lang.foreign.ValueLayout.*;

/**
 * @test
 * @bug 8288397 8287289
 * @key randomness
 * @library /test/lib /
 * @summary Test vector gather, scatter intrinsics
 * @enablePreview
 * @modules jdk.incubator.vector
 *
 * @run main compiler.vectorapi.VectorGatherScatterTest
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
    private static MemorySegment mia;
    private static MemorySegment mir;
    private static MemorySegment mla;
    private static MemorySegment mlr;
    private static MemorySegment mfa;
    private static MemorySegment mfr;
    private static MemorySegment mda;
    private static MemorySegment mdr;
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
        mia = MemorySegment.allocateNative(LENGTH * Integer.BYTES, SegmentScope.auto());
        mir = MemorySegment.allocateNative(LENGTH * Integer.BYTES, SegmentScope.auto());
        mla = MemorySegment.allocateNative(LENGTH * Long.BYTES, SegmentScope.auto());
        mlr = MemorySegment.allocateNative(LENGTH * Long.BYTES, SegmentScope.auto());
        mfa = MemorySegment.allocateNative(LENGTH * Float.BYTES, SegmentScope.auto());
        mfr = MemorySegment.allocateNative(LENGTH * Float.BYTES, SegmentScope.auto());
        mda = MemorySegment.allocateNative(LENGTH * Double.BYTES, SegmentScope.auto());
        mdr = MemorySegment.allocateNative(LENGTH * Double.BYTES, SegmentScope.auto());
        m = new boolean[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            ia[i] = RD.nextInt(100);
            la[i] = RD.nextLong(100);
            fa[i] = RD.nextFloat();
            da[i] = RD.nextDouble();
            mia.setAtIndex(JAVA_INT_UNALIGNED, i, RD.nextInt());
            mla.setAtIndex(JAVA_LONG_UNALIGNED, i, RD.nextLong());
            mfa.setAtIndex(JAVA_FLOAT_UNALIGNED, i, RD.nextFloat());
            mda.setAtIndex(JAVA_DOUBLE_UNALIGNED, i, RD.nextDouble());
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
                Asserts.assertEquals(mir.getAtIndex(JAVA_INT_UNALIGNED, i),
                        mia.get(JAVA_INT_UNALIGNED, index));
                Asserts.assertEquals(mlr.getAtIndex(JAVA_LONG_UNALIGNED, i),
                        mla.get(JAVA_LONG_UNALIGNED, index));
                Asserts.assertEquals(mfr.getAtIndex(JAVA_FLOAT_UNALIGNED, i),
                        mfa.get(JAVA_FLOAT_UNALIGNED, index));
                Asserts.assertEquals(mdr.getAtIndex(JAVA_DOUBLE_UNALIGNED, i),
                        mda.get(JAVA_DOUBLE_UNALIGNED, index));
            } else {
                Asserts.assertEquals(ir[i], 0);
                Asserts.assertEquals(lr[i], 0L);
                Asserts.assertEquals(fr[i], 0F);
                Asserts.assertEquals(dr[i], 0D);
                Asserts.assertEquals(mir.getAtIndex(JAVA_INT_UNALIGNED, i), 0);
                Asserts.assertEquals(mlr.getAtIndex(JAVA_LONG_UNALIGNED, i), 0L);
                Asserts.assertEquals(mfr.getAtIndex(JAVA_FLOAT_UNALIGNED, i), 0F);
                Asserts.assertEquals(mdr.getAtIndex(JAVA_DOUBLE_UNALIGNED, i), 0D);
            }
        }
    }

    @DontInline
    public static void verifyScatter(boolean intIndex, boolean masked,
                                     int[] ie, long[] le, float[] fe, double[] de,
                                     MemorySegment mie, MemorySegment mle,
                                     MemorySegment mfe, MemorySegment mde) {
        for (int i = 0; i < I_SPECIES.length(); i++) {
            int index = intIndex ? ia[i] : (int) la[i];
            if (!masked || m[i]) {
                ie[index] = ia[i];
                le[index] = la[i];
                fe[index] = fa[i];
                de[index] = da[i];
                mie.set(JAVA_INT_UNALIGNED, index, mia.getAtIndex(JAVA_INT_UNALIGNED, i));
                mle.set(JAVA_LONG_UNALIGNED, index, mla.getAtIndex(JAVA_LONG_UNALIGNED, i));
                mfe.set(JAVA_FLOAT_UNALIGNED, index, mfa.getAtIndex(JAVA_FLOAT_UNALIGNED, i));
                mde.set(JAVA_DOUBLE_UNALIGNED, index, mda.getAtIndex(JAVA_DOUBLE_UNALIGNED, i));
            }
        }
        Asserts.assertTrue(Arrays.equals(ie, ir));
        Asserts.assertTrue(Arrays.equals(le, lr));
        Asserts.assertTrue(Arrays.equals(fe, fr));
        Asserts.assertTrue(Arrays.equals(de, dr));
        Asserts.assertTrue(mie.mismatch(mir) == -1);
        Asserts.assertTrue(mle.mismatch(mlr) == -1);
        Asserts.assertTrue(mfe.mismatch(mfr) == -1);
        Asserts.assertTrue(mde.mismatch(mdr) == -1);
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_INT, "8"}, applyIfCPUFeatureOr = {"avx2", "true", "sve", "true"})
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
        DoubleVector.fromMemorySegment(D_SPECIES, mda, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mdr, 0, ByteOrder.nativeOrder());
        FloatVector.fromMemorySegment(F_SPECIES, mfa, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mfr, 0, ByteOrder.nativeOrder());
        LongVector.fromMemorySegment(L_SPECIES, mla, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mlr, 0, ByteOrder.nativeOrder());
        IntVector.fromMemorySegment(I_SPECIES, mia, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mir, 0, ByteOrder.nativeOrder());

        verifyGather(true, false);
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_LONG, "8"}, applyIfCPUFeatureOr = {"avx2", "true", "sve", "true"})
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
        DoubleVector.fromMemorySegment(D_SPECIES, mda, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mdr, 0, ByteOrder.nativeOrder());
        FloatVector.fromMemorySegment(F_SPECIES, mfa, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mfr, 0, ByteOrder.nativeOrder());
        LongVector.fromMemorySegment(L_SPECIES, mla, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mlr, 0, ByteOrder.nativeOrder());
        IntVector.fromMemorySegment(I_SPECIES, mia, idx, ByteOrder.nativeOrder())
                .intoMemorySegment(mir, 0, ByteOrder.nativeOrder());

        verifyGather(false, false);
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_INT_MASKED, "8"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
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
        DoubleVector.fromMemorySegment(D_SPECIES, mda, idx, ByteOrder.nativeOrder(), mask.cast(D_SPECIES))
                .intoMemorySegment(mdr, 0, ByteOrder.nativeOrder());
        FloatVector.fromMemorySegment(F_SPECIES, mfa, idx, ByteOrder.nativeOrder(), mask.cast(F_SPECIES))
                .intoMemorySegment(mfr, 0, ByteOrder.nativeOrder());
        LongVector.fromMemorySegment(L_SPECIES, mla, idx, ByteOrder.nativeOrder(), mask.cast(L_SPECIES))
                .intoMemorySegment(mlr, 0, ByteOrder.nativeOrder());
        IntVector.fromMemorySegment(I_SPECIES, mia, idx, ByteOrder.nativeOrder(), mask.cast(I_SPECIES))
                .intoMemorySegment(mir, 0, ByteOrder.nativeOrder());

        verifyGather(true, true);
    }

    @Test
    @IR(counts = {IRNode.LOAD_GATHER_LONG_MASKED, "8"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
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
        DoubleVector.fromMemorySegment(D_SPECIES, mda, idx, ByteOrder.nativeOrder(), mask.cast(D_SPECIES))
                .intoMemorySegment(mdr, 0, ByteOrder.nativeOrder());
        FloatVector.fromMemorySegment(F_SPECIES, mfa, idx, ByteOrder.nativeOrder(), mask.cast(F_SPECIES))
                .intoMemorySegment(mfr, 0, ByteOrder.nativeOrder());
        LongVector.fromMemorySegment(L_SPECIES, mla, idx, ByteOrder.nativeOrder(), mask.cast(L_SPECIES))
                .intoMemorySegment(mlr, 0, ByteOrder.nativeOrder());
        IntVector.fromMemorySegment(I_SPECIES, mia, idx, ByteOrder.nativeOrder(), mask.cast(I_SPECIES))
                .intoMemorySegment(mir, 0, ByteOrder.nativeOrder());

        verifyGather(false, true);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_INT, "8"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterInt() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        MemorySegment mie = MemorySegment.allocateNative(mir.byteSize(), SegmentScope.auto());
        mie.copyFrom(mir);
        MemorySegment mle = MemorySegment.allocateNative(mlr.byteSize(), SegmentScope.auto());
        mle.copyFrom(mlr);
        MemorySegment mfe = MemorySegment.allocateNative(mfr.byteSize(), SegmentScope.auto());
        mfe.copyFrom(mfr);
        MemorySegment mde = MemorySegment.allocateNative(mdr.byteSize(), SegmentScope.auto());
        mde.copyFrom(mdr);

        var idx = IntVector.fromArray(I_SPECIES, ia, 0);
        DoubleVector.fromArray(D_SPECIES, da, 0)
                .intoArray(dr, idx);
        FloatVector.fromArray(F_SPECIES, fa, 0)
                .intoArray(fr, idx);
        LongVector.fromArray(L_SPECIES, la, 0)
                .intoArray(lr, idx);
        IntVector.fromArray(I_SPECIES, ia, 0)
                .intoArray(ir, idx);
        DoubleVector.fromMemorySegment(D_SPECIES, mda, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mdr, idx, ByteOrder.nativeOrder());
        FloatVector.fromMemorySegment(F_SPECIES, mfa, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mfr, idx, ByteOrder.nativeOrder());
        LongVector.fromMemorySegment(L_SPECIES, mla, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mlr, idx, ByteOrder.nativeOrder());
        IntVector.fromMemorySegment(I_SPECIES, mia, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mir, idx, ByteOrder.nativeOrder());

        verifyScatter(true, false, ie, le, fe, de, mie, mle, mfe, mde);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_LONG, "8"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterLong() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        MemorySegment mie = MemorySegment.allocateNative(mir.byteSize(), SegmentScope.auto());
        mie.copyFrom(mir);
        MemorySegment mle = MemorySegment.allocateNative(mlr.byteSize(), SegmentScope.auto());
        mle.copyFrom(mlr);
        MemorySegment mfe = MemorySegment.allocateNative(mfr.byteSize(), SegmentScope.auto());
        mfe.copyFrom(mfr);
        MemorySegment mde = MemorySegment.allocateNative(mdr.byteSize(), SegmentScope.auto());
        mde.copyFrom(mdr);

        var idx = LongVector.fromArray(L_SPECIES, la, 0);
        DoubleVector.fromArray(D_SPECIES, da, 0)
                .intoArray(dr, idx);
        FloatVector.fromArray(F_SPECIES, fa, 0)
                .intoArray(fr, idx);
        LongVector.fromArray(L_SPECIES, la, 0)
                .intoArray(lr, idx);
        IntVector.fromArray(I_SPECIES, ia, 0)
                .intoArray(ir, idx);
        DoubleVector.fromMemorySegment(D_SPECIES, mda, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mdr, idx, ByteOrder.nativeOrder());
        FloatVector.fromMemorySegment(F_SPECIES, mfa, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mfr, idx, ByteOrder.nativeOrder());
        LongVector.fromMemorySegment(L_SPECIES, mla, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mlr, idx, ByteOrder.nativeOrder());
        IntVector.fromMemorySegment(I_SPECIES, mia, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mir, idx, ByteOrder.nativeOrder());

        verifyScatter(false, false, ie, le, fe, de, mie, mle, mfe, mde);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_INT_MASKED, "8"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterIntMasked() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        MemorySegment mie = MemorySegment.allocateNative(mir.byteSize(), SegmentScope.auto());
        mie.copyFrom(mir);
        MemorySegment mle = MemorySegment.allocateNative(mlr.byteSize(), SegmentScope.auto());
        mle.copyFrom(mlr);
        MemorySegment mfe = MemorySegment.allocateNative(mfr.byteSize(), SegmentScope.auto());
        mfe.copyFrom(mfr);
        MemorySegment mde = MemorySegment.allocateNative(mdr.byteSize(), SegmentScope.auto());
        mde.copyFrom(mdr);

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
        DoubleVector.fromMemorySegment(D_SPECIES, mda, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mdr, idx, ByteOrder.nativeOrder(), mask.cast(D_SPECIES));
        FloatVector.fromMemorySegment(F_SPECIES, mfa, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mfr, idx, ByteOrder.nativeOrder(), mask.cast(F_SPECIES));
        LongVector.fromMemorySegment(L_SPECIES, mla, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mlr, idx, ByteOrder.nativeOrder(), mask.cast(L_SPECIES));
        IntVector.fromMemorySegment(I_SPECIES, mia, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mir, idx, ByteOrder.nativeOrder(), mask.cast(I_SPECIES));

        verifyScatter(true, true, ie, le, fe, de, mie, mle, mfe, mde);
    }

    @Test
    @IR(counts = {IRNode.STORE_SCATTER_LONG_MASKED, "8"}, applyIfCPUFeatureOr = {"avx512f", "true", "sve", "true"})
    public static void testScatterLongMasked() {
        int[] ie = ir.clone();
        long[] le = lr.clone();
        float[] fe = fr.clone();
        double[] de = dr.clone();
        MemorySegment mie = MemorySegment.allocateNative(mir.byteSize(), SegmentScope.auto());
        mie.copyFrom(mir);
        MemorySegment mle = MemorySegment.allocateNative(mlr.byteSize(), SegmentScope.auto());
        mle.copyFrom(mlr);
        MemorySegment mfe = MemorySegment.allocateNative(mfr.byteSize(), SegmentScope.auto());
        mfe.copyFrom(mfr);
        MemorySegment mde = MemorySegment.allocateNative(mdr.byteSize(), SegmentScope.auto());
        mde.copyFrom(mdr);

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
        DoubleVector.fromMemorySegment(D_SPECIES, mda, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mdr, idx, ByteOrder.nativeOrder(), mask.cast(D_SPECIES));
        FloatVector.fromMemorySegment(F_SPECIES, mfa, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mfr, idx, ByteOrder.nativeOrder(), mask.cast(F_SPECIES));
        LongVector.fromMemorySegment(L_SPECIES, mla, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mlr, idx, ByteOrder.nativeOrder(), mask.cast(L_SPECIES));
        IntVector.fromMemorySegment(I_SPECIES, mia, 0, ByteOrder.nativeOrder())
                .intoMemorySegment(mir, idx, ByteOrder.nativeOrder(), mask.cast(I_SPECIES));

        verifyScatter(false, true, ie, le, fe, de, mie, mle, mfe, mde);
    }

    public static void main(String[] args) {
        TestFramework inst = new TestFramework();
        inst.setDefaultWarmup(10000)
                .addFlags("--add-modules=jdk.incubator.vector", "--enable-preview")
                .start();
    }
}
