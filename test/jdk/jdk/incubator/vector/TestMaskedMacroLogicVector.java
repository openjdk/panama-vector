/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 8273322
 * @modules jdk.incubator.vector
 * @requires vm.compiler2.enabled
 * @library /test/lib
 * @run main/othervm -XX:+IgnoreUnrecognizedVMOptions
 *           -Xbatch -XX:-TieredCompilation -XX:CICompilerCount=1 -XX:UseAVX=3
 *           -XX:CompileCommand=quiet -XX:+TraceNewVectors
 *           test.jdk.incubator.TestMaskedMacroLogicVector
 */

package test.jdk.incubator;

import java.util.Random;
import java.util.concurrent.Callable;
import jdk.incubator.vector.*;

public class TestMaskedMacroLogicVector {
    static boolean booleanFunc1(boolean a, boolean b) {
        return a & b;
    }

    static void testSubWordBoolean(boolean[] r, boolean[] a, boolean[] b) {
        for (int i = 0; i < r.length; i++) {
            r[i] = booleanFunc1(a[i], b[i]);
        }
    }
    static void verifySubWordBoolean(boolean[] r, boolean[] a, boolean[] b) {
        for (int i = 0; i < r.length; i++) {
            boolean expected = booleanFunc1(a[i], b[i]);
            if (r[i] != expected) {
                throw new AssertionError(
                        String.format("at #%d: r=%b, expected = %b = booleanFunc1(%b,%b)",
                                      i, r[i], expected, a[i], b[i]));
            }
        }
    }


    static short charFunc1(char a, char b) {
        return (short)((a & b) & 1);
    }

    static void testSubWordChar(short[] r, char[] a, char[] b) {
        for (int i = 0; i < r.length; i++) {
            r[i] = charFunc1(a[i], b[i]);
        }
    }
    static void verifySubWordChar(short[] r, char[] a, char[] b) {
        for (int i = 0; i < r.length; i++) {
            short expected = charFunc1(a[i], b[i]);
            if (r[i] != expected) {
                throw new AssertionError(
                        String.format("testSubWordChar: at #%d: r=%d, expected = %d = booleanFunc1(%d,%d)",
                                      i, r[i], expected, (int)a[i], (int)b[i]));
            }
        }
    }

    // Case 1): Unmasked expression tree.
    //        P_LOP
    //   L_LOP     R_LOP

    static int intFunc1(int a, int b, int c) {
        return (a & b) ^ (a & c);
    }

    static void testInt1(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            va.lanewise(VectorOperators.AND, vc)
            .lanewise(VectorOperators.XOR, va.lanewise(VectorOperators.AND, vb))
            .intoArray(r, i);
        }
    }

    static void verifyInt1(int[] r, int[] a, int[] b, int[] c) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc1(a[i], b[i], c[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt1: at #%d: r=%d, expected = %d = intFunc1(%d,%d,%d)",
                                                       i, r[i], expected, a[i], b[i], c[i]));
            }
        }
    }

    // Case 2): Only right child is masked.
    //        P_LOP
    //   L_LOP    R_LOP(mask)

    static int intFunc2(int a, int b, int c, boolean mask) {
        return (a & b) ^ (mask == true ? a & c : a);
    }

    static void testInt2(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            VectorMask<Integer> vmask = VectorMask.fromArray(SPECIES, mask , i);
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            va.lanewise(VectorOperators.AND, vb)
            .lanewise(VectorOperators.XOR,
                      va.lanewise(VectorOperators.AND, vc, vmask))
           .intoArray(r, i);
        }
    }

    static void verifyInt2(int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc2(a[i], b[i], c[i], mask[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt2: at #%d: r=%d, expected = %d = intFunc2(%d,%d,%d,%b)",
                                                       i, r[i], expected, a[i], b[i], c[i], mask[i]));
            }
        }
    }

    // Case 3): Only left child is masked.
    //             P_LOP
    //   L_LOP(mask)    R_LOP

    static int intFunc3(int a, int b, int c, boolean mask) {
        return (mask == true ? a & b : a) ^ (a & c);
    }

    static void testInt3(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            VectorMask<Integer> vmask = VectorMask.fromArray(SPECIES, mask , i);
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            va.lanewise(VectorOperators.AND, vb, vmask)
            .lanewise(VectorOperators.XOR,
                      va.lanewise(VectorOperators.AND, vc))
           .intoArray(r, i);
        }
    }

    static void verifyInt3(int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc3(a[i], b[i], c[i], mask[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt3: at #%d: r=%d, expected = %d = intFunc3(%d,%d,%d,%b)",
                                                       i, r[i], expected, a[i], b[i], c[i], mask[i]));
            }
        }
    }

    // Case 4): Both child nodes are masked.
    //             P_LOP
    //   L_LOP(mask)    R_LOP(mask)

    static int intFunc4(int a, int b, int c, boolean mask) {
        return (mask == true ? b & a : b) ^ (mask == true ? c & a : c);
    }

    static void testInt4(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            VectorMask<Integer> vmask = VectorMask.fromArray(SPECIES, mask , i);
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            vb.lanewise(VectorOperators.AND, va, vmask)
            .lanewise(VectorOperators.XOR,
                      vc.lanewise(VectorOperators.AND, va, vmask))
           .intoArray(r, i);
        }
    }

    static void verifyInt4(int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc4(a[i], b[i], c[i], mask[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt4: at #%d: r=%d, expected = %d = intFunc4(%d,%d,%d,%b)",
                                                       i, r[i], expected, a[i], b[i], c[i], mask[i]));
            }
        }
    }

    // Case 5): Parent is masked with unmasked child expressions.
    //        P_LOP(mask)
    //   L_LOP     R_LOP

    static int intFunc5(int a, int b, int c, boolean mask) {
        return mask == true ? ((a & b) ^ (a & c)) : (a & b);
    }

    static void testInt5(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            VectorMask<Integer> vmask = VectorMask.fromArray(SPECIES, mask , i);
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            va.lanewise(VectorOperators.AND, vb)
            .lanewise(VectorOperators.XOR,
                      va.lanewise(VectorOperators.AND, vc), vmask)
           .intoArray(r, i);
        }
    }

    static void verifyInt5(int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc5(a[i], b[i], c[i], mask[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt5: at #%d: r=%d, expected = %d = intFunc5(%d,%d,%d,%b)",
                                                       i, r[i], expected, a[i], b[i], c[i], mask[i]));
            }
        }
    }

    // Case 6): Parent and right child are masked.
    //        P_LOP(mask)
    //   L_LOP     R_LOP(mask)

    static int intFunc6(int a, int b, int c, boolean mask) {
        return mask == true ? ((a & b) ^ (mask == true ? a & c : a)) : (a & b);
    }

    static void testInt6(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            VectorMask<Integer> vmask = VectorMask.fromArray(SPECIES, mask , i);
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            va.lanewise(VectorOperators.AND, vb)
            .lanewise(VectorOperators.XOR,
                      va.lanewise(VectorOperators.AND, vc, vmask), vmask)
           .intoArray(r, i);
        }
    }

    static void verifyInt6(int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc6(a[i], b[i], c[i], mask[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt6: at #%d: r=%d, expected = %d = intFunc6(%d,%d,%d,%b)",
                                                       i, r[i], expected, a[i], b[i], c[i], mask[i]));
            }
        }
    }

    // Case 7): Parent and left child are masked.
    //            P_LOP(mask)
    //   L_LOP(mask)       R_LOP

    static int intFunc7(int a, int b, int c, boolean mask) {
        return mask == true ? ((mask == true ? a & b : a) ^ (a & c)) : a;
    }

    static void testInt7(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            VectorMask<Integer> vmask = VectorMask.fromArray(SPECIES, mask , i);
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            va.lanewise(VectorOperators.AND, vb, vmask)
            .lanewise(VectorOperators.XOR,
                      va.lanewise(VectorOperators.AND, vc), vmask)
           .intoArray(r, i);
        }
    }

    static void verifyInt7(int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc7(a[i], b[i], c[i], mask[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt7: at #%d: r=%d, expected = %d = intFunc7(%d,%d,%d,%b)",
                                                       i, r[i], expected, a[i], b[i], c[i], mask[i]));
            }
        }
    }

    // Case 8): Parent and both child expressions are masked.
    //            P_LOP(mask)
    //   L_LOP(mask)       R_LOP (mask)

    static int intFunc8(int a, int b, int c, boolean mask) {
        return mask == true ? ((mask == true ? b & a : b) ^ (mask == true ? c & a  : c)) : b;
    }

    static void testInt8(VectorSpecies<Integer> SPECIES, int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i+=SPECIES.length()) {
            VectorMask<Integer> vmask = VectorMask.fromArray(SPECIES, mask , i);
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = IntVector.fromArray(SPECIES, c, i);
            vb.lanewise(VectorOperators.AND, va, vmask)
            .lanewise(VectorOperators.XOR,
                      vc.lanewise(VectorOperators.AND, va, vmask), vmask)
           .intoArray(r, i);
        }
    }

    static void verifyInt8(int[] r, int[] a, int[] b, int[] c, boolean [] mask) {
        for (int i = 0; i < r.length; i++) {
            int expected = intFunc8(a[i], b[i], c[i], mask[i]);
            if (r[i] != expected) {
                throw new AssertionError(String.format("testInt8: at #%d: r=%d, expected = %d = intFunc8(%d,%d,%d,%b)",
                                                       i, r[i], expected, a[i], b[i], c[i], mask[i]));
            }
        }
    }


    // ===================================================== //

    static long longFunc(long a, long b, long c) {
        long v1 = (a & b) ^ (a & c) ^ (b & c);
        long v2 = (~a & b) | (~b & c) | (~c & a);
        return v1 & v2;
    }

    static void testLong(VectorSpecies<Long> SPECIES, long[] r, long[] a, long[] b, long[] c) {
        for (int i = 0; i < SPECIES.loopBound(r.length); i += SPECIES.length()) {
            LongVector va = LongVector.fromArray(SPECIES, a, i);
            LongVector vb = LongVector.fromArray(SPECIES, b, i);
            LongVector vc = LongVector.fromArray(SPECIES, c, i);

            va.lanewise(VectorOperators.AND, vb)
            .lanewise(VectorOperators.XOR, va.lanewise(VectorOperators.AND, vc))
            .lanewise(VectorOperators.XOR, vb.lanewise(VectorOperators.AND, vc))
            .lanewise(VectorOperators.AND,
                       va.lanewise(VectorOperators.NOT).lanewise(VectorOperators.AND, vb)
                      .lanewise(VectorOperators.OR, vb.lanewise(VectorOperators.NOT).lanewise(VectorOperators.AND, vc))
                      .lanewise(VectorOperators.OR, vc.lanewise(VectorOperators.NOT).lanewise(VectorOperators.AND, va)))
            .intoArray(r, i);
        }
    }

    static void verifyLong(long[] r, long[] a, long[] b, long[] c) {
        for (int i = 0; i < r.length; i++) {
            long expected = longFunc(a[i], b[i], c[i]);
            if (r[i] != expected) {
                throw new AssertionError(
                        String.format("testLong: at #%d: r=%d, expected = %d = longFunc(%d,%d,%d)",
                                      i, r[i], expected, a[i], b[i], c[i]));
            }
        }
    }

    // ===================================================== //

    private static final Random R = new Random(1024);

    static boolean[] fillBooleanRandom(Callable<boolean[]> factory) {
        try {
            boolean[] arr = factory.call();
            for (int i = 0; i < arr.length; i++) {
                arr[i] = R.nextBoolean();
            }
            return arr;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }
    static char[] fillCharRandom(Callable<char[]> factory) {
        try {
            char[] arr = factory.call();
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (char)R.nextInt();
            }
            return arr;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }
    static int[] fillIntRandom(Callable<int[]> factory) {
        try {
            int[] arr = factory.call();
            for (int i = 0; i < arr.length; i++) {
                arr[i] = R.nextInt();
            }
            return arr;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }
    static long[] fillLongRandom(Callable<long[]> factory) {
        try {
            long[] arr = factory.call();
            for (int i = 0; i < arr.length; i++) {
                arr[i] = R.nextLong();
            }
            return arr;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    // ===================================================== //

    static final int SIZE = 512;

    public static void main(String[] args) {
        boolean[] br = new boolean[SIZE];
        boolean[] ba = fillBooleanRandom((()-> new boolean[SIZE]));
        boolean[] bb = fillBooleanRandom((()-> new boolean[SIZE]));

        short[] sr = new short[SIZE];
        char[] ca = fillCharRandom((()-> new char[SIZE]));
        char[] cb = fillCharRandom((()-> new char[SIZE]));

        int[] r = new int[SIZE];
        int[] a = fillIntRandom(()-> new int[SIZE]);
        int[] b = fillIntRandom(()-> new int[SIZE]);
        int[] c = fillIntRandom(()-> new int[SIZE]);
        int[] d = fillIntRandom(()-> new int[SIZE]);
        int[] e = fillIntRandom(()-> new int[SIZE]);
        int[] f = fillIntRandom(()-> new int[SIZE]);

        long[] rl = new long[SIZE];
        long[] al = fillLongRandom(() -> new long[SIZE]);
        long[] bl = fillLongRandom(() -> new long[SIZE]);
        long[] cl = fillLongRandom(() -> new long[SIZE]);

        boolean[] mask = fillBooleanRandom((()-> new boolean[SIZE]));

        VectorSpecies [] ispecies = {
            IntVector.SPECIES_64,
            IntVector.SPECIES_128,
            IntVector.SPECIES_256,
            IntVector.SPECIES_512
        };

        VectorSpecies [] lspecies = {
            LongVector.SPECIES_64,
            LongVector.SPECIES_128,
            LongVector.SPECIES_256,
            LongVector.SPECIES_512
        };

        for (int i = 0; i < 20_000; i++) {
            testSubWordBoolean(br, ba, bb);
            verifySubWordBoolean(br, ba, bb);

            testSubWordChar(sr, ca, cb);
            verifySubWordChar(sr, ca, cb);

            testInt1(ispecies[i & 3], r, a, b, c);
            verifyInt1(r, a, b, c);

            testInt2(ispecies[i & 3], r, a, b, c, mask);
            verifyInt2(r, a, b, c, mask);

            testInt3(ispecies[i & 3], r, a, b, c, mask);
            verifyInt3(r, a, b, c, mask);

            testInt4(ispecies[i & 3], r, a, b, c, mask);
            verifyInt4(r, a, b, c, mask);

            testInt5(ispecies[i & 3], r, a, b, c, mask);
            verifyInt5(r, a, b, c, mask);

            testInt6(ispecies[i & 3], r, a, b, c, mask);
            verifyInt6(r, a, b, c, mask);

            testInt7(ispecies[i & 3], r, a, b, c, mask);
            verifyInt7(r, a, b, c, mask);

            testInt8(ispecies[i & 3], r, a, b, c, mask);
            verifyInt8(r, a, b, c, mask);

            testLong(lspecies[i & 3], rl, al, bl, cl);
            verifyLong(rl, al, bl, cl);
        }
    }
}
