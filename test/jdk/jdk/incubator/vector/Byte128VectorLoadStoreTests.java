/*
 * Copyright (c) 2018, 2020, Oracle and/or its affiliates. All rights reserved.
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
 * or visit www.oracle.com if you need additional information or have
 * questions.
 */

/*
 * @test
 * @modules jdk.incubator.vector
 * @run testng Byte128VectorLoadStoreTests
 *
 */

// -- This file was mechanically generated: Do not edit! -- //

import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.Vector;

import jdk.incubator.vector.ByteVector;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

@Test
public class Byte128VectorLoadStoreTests extends AbstractVectorTest {
    static final VectorSpecies<Byte> SPECIES =
                ByteVector.SPECIES_128;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 10);


    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / 128);

    static final int BUFFER_SIZE = Integer.getInteger("jdk.incubator.vector.test.buffer-size", BUFFER_REPS * (128 / 8));

    static void assertArraysEquals(byte[] a, byte[] r, boolean[] mask) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(mask[i % SPECIES.length()] ? a[i] : (byte) 0, r[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(mask[i % SPECIES.length()] ? a[i] : (byte) 0, r[i], "at index #" + i);
        }
    }

    static void assertArraysEquals(byte[] a, byte[] r, int[] im) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(a[im[i]], r[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(a[im[i]], r[i], "at index #" + i);
        }
    }

    static void assertArraysEquals(byte[] a, byte[] r, int[] im, boolean[] mask) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(mask[i % SPECIES.length()] ? a[im[i]] : (byte) 0, r[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(mask[i % SPECIES.length()] ? a[im[i]] : (byte) 0, r[i], "at index #" + i);
        }
    }


    static final List<IntFunction<byte[]>> BYTE_GENERATORS = List.of(
            withToString("byte[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (byte)(i * 5));
            }),
            withToString("byte[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((byte)(i + 1) == 0) ? 1 : (byte)(i + 1)));
            })
    );

    @DataProvider
    public Object[][] byteProvider() {
        return BYTE_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteIndexMapProvider() {
        return INDEX_GENERATORS.stream().
                flatMap(fim -> BYTE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fim};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteIndexMapMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> INDEX_GENERATORS.stream().
                    flatMap(fim -> BYTE_GENERATORS.stream().map(fa -> {
                        return new Object[] {fa, fim, fm};
                }))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteByteBufferProvider() {
        return BYTE_GENERATORS.stream().
                flatMap(fa -> BYTE_BUFFER_GENERATORS.stream().
                        flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                            return new Object[]{fa, fb, bo};
                }))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteByteBufferMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_GENERATORS.stream().
                        flatMap(fa -> BYTE_BUFFER_GENERATORS.stream().
                                flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                            return new Object[]{fa, fb, fm, bo};
                        })))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteByteArrayProvider() {
        return BYTE_GENERATORS.stream().
                flatMap(fa -> BYTE_ARRAY_GENERATORS.stream().
                        flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                    return new Object[]{fa, fb, bo};
                }))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteByteArrayMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_GENERATORS.stream().
                        flatMap(fa -> BYTE_ARRAY_GENERATORS.stream().
                                flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                            return new Object[]{fa, fb, fm, bo};
                        })))).
                toArray(Object[][]::new);
    }

    static ByteBuffer toBuffer(byte[] a, IntFunction<ByteBuffer> fb) {
        ByteBuffer bb = fb.apply(a.length * SPECIES.elementSize() / 8);
        for (byte v : a) {
            bb.put(v);
        }
        return bb.clear();
    }

    static byte[] bufferToArray(ByteBuffer bb) {
        ByteBuffer db = bb;
        byte[] d = new byte[db.capacity()];
        db.get(d);
        return d;
    }

    static byte[] toByteArray(byte[] a, IntFunction<byte[]> fb, ByteOrder bo) {
        byte[] b = fb.apply(a.length * SPECIES.elementSize() / 8);
        ByteBuffer bb = ByteBuffer.wrap(b, 0, b.length).order(bo);
        for (byte v : a) {
            bb.put(v);
        }
        return b;
    }


    interface ToByteF {
        byte apply(int i);
    }

    static byte[] fill(int s , ToByteF f) {
        return fill(new byte[s], f);
    }

    static byte[] fill(byte[] a, ToByteF f) {
        for (int i = 0; i < a.length; i++) {
            a[i] = f.apply(i);
        }
        return a;
    }

    @Test(dataProvider = "byteProvider")
    static void loadStoreArray(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(SPECIES.length());
        byte[] r = new byte[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i);
            }
        }
        Assert.assertEquals(a, r);
    }

    @Test(dataProvider = "byteMaskProvider")
    static void loadStoreMaskArray(IntFunction<byte[]> fa,
                                   IntFunction<boolean[]> fm) {
        byte[] a = fa.apply(SPECIES.length());
        byte[] r = new byte[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromArray(SPECIES, a, i, vmask);
                av.intoArray(r, i);
            }
        }
        assertArraysEquals(a, r, mask);

        r = new byte[a.length];
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, vmask);
            }
        }

        assertArraysEquals(a, r, mask);
    }

    @Test(dataProvider = "byteMaskProvider")
    static void loadStoreMask(IntFunction<byte[]> fa,
                              IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = new boolean[mask.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, i);
                vmask.intoArray(r, i);
            }
        }
        Assert.assertEquals(mask, r);
    }

    @Test(dataProvider = "byteByteBufferProvider")
    static void loadStoreByteBuffer(IntFunction<byte[]> fa,
                                    IntFunction<ByteBuffer> fb,
                                    ByteOrder bo) {
        ByteBuffer a = toBuffer(fa.apply(SPECIES.length()), fb);
        ByteBuffer r = fb.apply(a.limit());

        int l = a.limit();
        int s = SPECIES.length() * SPECIES.elementSize() / 8;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromByteBuffer(SPECIES, a, i, bo);
                av.intoByteBuffer(r, i, bo);
            }
        }
        Assert.assertEquals(a.position(), 0, "Input buffer position changed");
        Assert.assertEquals(a.limit(), l, "Input buffer limit changed");
        Assert.assertEquals(r.position(), 0, "Result buffer position changed");
        Assert.assertEquals(r.limit(), l, "Result buffer limit changed");
        Assert.assertEquals(a, r, "Buffers not equal");
    }

    @Test(dataProvider = "byteByteBufferProvider")
    static void loadReadOnlyStoreByteBuffer(IntFunction<byte[]> fa,
                                            IntFunction<ByteBuffer> fb,
                                            ByteOrder bo) {
        ByteBuffer a = toBuffer(fa.apply(SPECIES.length()), fb);
        a = a.asReadOnlyBuffer().order(a.order());
        ByteBuffer r = fb.apply(a.limit());

        int l = a.limit();
        int s = SPECIES.length() * SPECIES.elementSize() / 8;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromByteBuffer(SPECIES, a, i, bo);
                av.intoByteBuffer(r, i, bo);
            }
        }
        Assert.assertEquals(a.position(), 0, "Input buffer position changed");
        Assert.assertEquals(a.limit(), l, "Input buffer limit changed");
        Assert.assertEquals(r.position(), 0, "Result buffer position changed");
        Assert.assertEquals(r.limit(), l, "Result buffer limit changed");
        Assert.assertEquals(a, r, "Buffers not equal");
    }

    @Test(dataProvider = "byteByteBufferMaskProvider")
    static void loadStoreByteBufferMask(IntFunction<byte[]> fa,
                                        IntFunction<ByteBuffer> fb,
                                        IntFunction<boolean[]> fm,
                                        ByteOrder bo) {
        ByteBuffer a = toBuffer(fa.apply(SPECIES.length()), fb);
        ByteBuffer r = fb.apply(a.limit());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        int l = a.limit();
        int s = SPECIES.length() * SPECIES.elementSize() / 8;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromByteBuffer(SPECIES, a, i, bo, vmask);
                av.intoByteBuffer(r, i, bo);
            }
        }
        Assert.assertEquals(a.position(), 0, "Input buffer position changed");
        Assert.assertEquals(a.limit(), l, "Input buffer limit changed");
        Assert.assertEquals(r.position(), 0, "Result buffer position changed");
        Assert.assertEquals(r.limit(), l, "Result buffer limit changed");
        assertArraysEquals(bufferToArray(a), bufferToArray(r), mask);

        a = toBuffer(fa.apply(SPECIES.length()), fb);
        r = fb.apply(a.limit());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromByteBuffer(SPECIES, a, i, bo);
                av.intoByteBuffer(r, i, bo, vmask);
            }
        }
        Assert.assertEquals(a.position(), 0, "Input buffer position changed");
        Assert.assertEquals(a.limit(), l, "Input buffer limit changed");
        Assert.assertEquals(r.position(), 0, "Result buffer position changed");
        Assert.assertEquals(r.limit(), l, "Result buffer limit changed");
        assertArraysEquals(bufferToArray(a), bufferToArray(r), mask);
    }

    @Test(dataProvider = "byteByteBufferMaskProvider")
    static void loadReadOnlyStoreByteBufferMask(IntFunction<byte[]> fa,
                                                IntFunction<ByteBuffer> fb,
                                                IntFunction<boolean[]> fm,
                                                ByteOrder bo) {
        ByteBuffer a = toBuffer(fa.apply(SPECIES.length()), fb);
        a = a.asReadOnlyBuffer().order(a.order());
        ByteBuffer r = fb.apply(a.limit());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        int l = a.limit();
        int s = SPECIES.length() * SPECIES.elementSize() / 8;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromByteBuffer(SPECIES, a, i, bo, vmask);
                av.intoByteBuffer(r, i, bo);
            }
        }
        Assert.assertEquals(a.position(), 0, "Input buffer position changed");
        Assert.assertEquals(a.limit(), l, "Input buffer limit changed");
        Assert.assertEquals(r.position(), 0, "Result buffer position changed");
        Assert.assertEquals(r.limit(), l, "Result buffer limit changed");
        assertArraysEquals(bufferToArray(a), bufferToArray(r), mask);
    }

    @Test(dataProvider = "byteByteArrayProvider")
    static void loadStoreByteArray(IntFunction<byte[]> fa,
                                    IntFunction<byte[]> fb,
                                    ByteOrder bo) {
        byte[] a = toByteArray(fa.apply(SPECIES.length()), fb, bo);
        byte[] r = fb.apply(a.length);

        int s = SPECIES.length() * SPECIES.elementSize() / 8;
        int l = a.length;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromByteArray(SPECIES, a, i, bo);
                av.intoByteArray(r, i, bo);
            }
        }
        Assert.assertEquals(a, r, "Byte arrays not equal");
    }

    @Test(dataProvider = "byteByteArrayMaskProvider")
    static void loadByteArrayMask(IntFunction<byte[]> fa,
                                  IntFunction<byte[]> fb,
                                  IntFunction<boolean[]> fm,
                                  ByteOrder bo) {
          byte[] a = toByteArray(fa.apply(SPECIES.length()), fb, bo);
          byte[] r = fb.apply(a.length);
          boolean[] mask = fm.apply(SPECIES.length());
          VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

          int s = SPECIES.length() * SPECIES.elementSize() / 8;
          int l = a.length;

          for (int ic = 0; ic < INVOC_COUNT; ic++) {
              for (int i = 0; i < l; i += s) {
                  ByteVector av = ByteVector.fromByteArray(SPECIES, a, i, bo, vmask);
                  av.intoByteArray(r, i, bo);
              }
          }
          assertArraysEquals(a, r, mask);
    }

    @Test(dataProvider = "byteByteArrayMaskProvider")
    static void storeByteArrayMask(IntFunction<byte[]> fa,
                                   IntFunction<byte[]> fb,
                                   IntFunction<boolean[]> fm,
                                   ByteOrder bo) {
        byte[] a = toByteArray(fa.apply(SPECIES.length()), fb, bo);
        byte[] r = fb.apply(a.length);
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        int s = SPECIES.length() * SPECIES.elementSize() / 8;
        int l = a.length;

        a = toByteArray(fa.apply(SPECIES.length()), fb, bo);
        r = fb.apply(a.length);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromByteArray(SPECIES, a, i, bo);
                av.intoByteArray(r, i, bo, vmask);
            }
        }
        assertArraysEquals(a, r, mask);
    }
}
