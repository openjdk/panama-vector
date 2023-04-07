/*
 * Copyright (c) 2018, 2023, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @test
 * @enablePreview
 * @modules jdk.incubator.vector java.base/jdk.internal.vm.annotation
 * @run testng/othervm --add-opens jdk.incubator.vector/jdk.incubator.vector=ALL-UNNAMED
 *      -XX:-TieredCompilation ByteMaxVectorLoadStoreTests
 *
 */

// -- This file was mechanically generated: Do not edit! -- //

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;
import jdk.incubator.vector.*;
import jdk.internal.vm.annotation.DontInline;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteOrder;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

@Test
public class ByteMaxVectorLoadStoreTests extends AbstractVectorLoadStoreTest {
    static final VectorSpecies<Byte> SPECIES =
                ByteVector.SPECIES_MAX;

    static final VectorSpecies<Integer> SAME_LENGTH_INT_SPECIES;
    static final VectorSpecies<Long> SAME_LENGTH_LONG_SPECIES;

    static {
        VectorSpecies<Integer> intSpecies;
        try {
            intSpecies = VectorSpecies.of(
                    int.class,
                    VectorShape.forBitSize(SPECIES.length() * Integer.SIZE)
            );
            if (intSpecies.length() != SPECIES.length()) {
                intSpecies = null;
            }
        } catch (Throwable e) {
            intSpecies = null;
        }
        SAME_LENGTH_INT_SPECIES = intSpecies;

        VectorSpecies<Long> longSpecies;
        try {
            longSpecies = VectorSpecies.of(
                    long.class,
                    VectorShape.forBitSize(SPECIES.length() * Long.SIZE)
            );
            if (longSpecies.length() != SPECIES.length()) {
                longSpecies = null;
            }
        } catch (Throwable e) {
            longSpecies = null;
        }
        SAME_LENGTH_LONG_SPECIES = longSpecies;
    }

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 100);

    static final ValueLayout.OfByte ELEMENT_LAYOUT = ValueLayout.JAVA_BYTE.withBitAlignment(8);

    static VectorShape getMaxBit() {
        return VectorShape.S_Max_BIT;
    }

    private static final int Max = 256;  // juts so we can do N/Max

    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / Max);

    static void assertArraysEquals(byte[] r, byte[] a, boolean[] mask) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? a[i] : (byte) 0);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? a[i] : (byte) 0, "at index #" + i);
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

    // Relative to array.length
    static final List<IntFunction<Integer>> INDEX_GENERATORS = List.of(
            withToString("-1", (int l) -> {
                return -1;
            }),
            withToString("l", (int l) -> {
                return l;
            }),
            withToString("l - 1", (int l) -> {
                return l - 1;
            }),
            withToString("l + 1", (int l) -> {
                return l + 1;
            }),
            withToString("l - speciesl + 1", (int l) -> {
                return l - SPECIES.length() + 1;
            }),
            withToString("l + speciesl - 1", (int l) -> {
                return l + SPECIES.length() - 1;
            }),
            withToString("l + speciesl", (int l) -> {
                return l + SPECIES.length();
            }),
            withToString("l + speciesl + 1", (int l) -> {
                return l + SPECIES.length() + 1;
            })
    );

    // Relative to byte[] array.length or MemorySegment.byteSize()
    static final List<IntFunction<Integer>> BYTE_INDEX_GENERATORS = List.of(
            withToString("-1", (int l) -> {
                return -1;
            }),
            withToString("l", (int l) -> {
                return l;
            }),
            withToString("l - 1", (int l) -> {
                return l - 1;
            }),
            withToString("l + 1", (int l) -> {
                return l + 1;
            }),
            withToString("l - speciesl*ebsize + 1", (int l) -> {
                return l - SPECIES.vectorByteSize() + 1;
            }),
            withToString("l + speciesl*ebsize - 1", (int l) -> {
                return l + SPECIES.vectorByteSize() - 1;
            }),
            withToString("l + speciesl*ebsize", (int l) -> {
                return l + SPECIES.vectorByteSize();
            }),
            withToString("l + speciesl*ebsize + 1", (int l) -> {
                return l + SPECIES.vectorByteSize() + 1;
            })
    );

    @DataProvider
    public Object[][] byteProvider() {
        return BYTE_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] maskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteProviderForIOOBE() {
        var f = BYTE_GENERATORS.get(0);
        return INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi};
                }).
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
    public Object[][] byteMaskProviderForIOOBE() {
        var f = BYTE_GENERATORS.get(0);
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteMemorySegmentProvider() {
        return BYTE_GENERATORS.stream().
                flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                        flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                            return new Object[]{fa, fb, bo};
                        }))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteMemorySegmentMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_GENERATORS.stream().
                        flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                                flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                            return new Object[]{fa, fb, fm, bo};
                        })))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteByteProviderForIOOBE() {
        var f = BYTE_GENERATORS.get(0);
        return BYTE_INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi};
                }).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] byteByteMaskProviderForIOOBE() {
        var f = BYTE_GENERATORS.get(0);
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi, fm};
                })).
                toArray(Object[][]::new);
    }

    static MemorySegment toSegment(byte[] a, LongFunction<MemorySegment> fb) {
        MemorySegment ms = fb.apply(a.length * SPECIES.elementSize() / 8);
        for (int i = 0; i < a.length; i++) {
            ms.set(ELEMENT_LAYOUT, i * SPECIES.elementSize() / 8 , a[i]);
        }
        return ms;
    }

    static byte[] segmentToArray(MemorySegment ms) {
        return ms.toArray(ELEMENT_LAYOUT);
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

    @DontInline
    static ByteVector fromArray(byte[] a, int i) {
        return ByteVector.fromArray(SPECIES, a, i);
    }

    @DontInline
    static ByteVector fromArray(byte[] a, int i, VectorMask<Byte> m) {
        return ByteVector.fromArray(SPECIES, a, i, m);
    }

    @DontInline
    static void intoArray(ByteVector v, byte[] a, int i) {
        v.intoArray(a, i);
    }

    @DontInline
    static void intoArray(ByteVector v, byte[] a, int i, VectorMask<Byte> m) {
        v.intoArray(a, i, m);
    }

    @DontInline
    static ByteVector fromMemorySegment(MemorySegment a, int i, ByteOrder bo) {
        return ByteVector.fromMemorySegment(SPECIES, a, i, bo);
    }

    @DontInline
    static ByteVector fromMemorySegment(MemorySegment a, int i, ByteOrder bo, VectorMask<Byte> m) {
        return ByteVector.fromMemorySegment(SPECIES, a, i, bo, m);
    }

    @DontInline
    static void intoMemorySegment(ByteVector v, MemorySegment a, int i, ByteOrder bo) {
        v.intoMemorySegment(a, i, bo);
    }

    @DontInline
    static void intoMemorySegment(ByteVector v, MemorySegment a, int i, ByteOrder bo, VectorMask<Byte> m) {
        v.intoMemorySegment(a, i, bo, m);
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
        Assert.assertEquals(r, a);
    }

    @Test(dataProvider = "byteProviderForIOOBE")
    static void loadArrayIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi) {
        byte[] a = fa.apply(SPECIES.length());
        byte[] r = new byte[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = fromArray(a, i);
                av.intoArray(r, i);
            }
        }

        int index = fi.apply(a.length);
        boolean shouldFail = isIndexOutOfBounds(SPECIES.length(), index, a.length);
        try {
            fromArray(a, index);
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
    }

    @Test(dataProvider = "byteProviderForIOOBE")
    static void storeArrayIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi) {
        byte[] a = fa.apply(SPECIES.length());
        byte[] r = new byte[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                intoArray(av, r, i);
            }
        }

        int index = fi.apply(a.length);
        boolean shouldFail = isIndexOutOfBounds(SPECIES.length(), index, a.length);
        try {
            ByteVector av = ByteVector.fromArray(SPECIES, a, 0);
            intoArray(av, r, index);
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
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
        assertArraysEquals(r, a, mask);


        r = new byte[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, vmask);
            }
        }
        assertArraysEquals(r, a, mask);
    }

    @Test(dataProvider = "byteMaskProviderForIOOBE")
    static void loadArrayMaskIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        byte[] a = fa.apply(SPECIES.length());
        byte[] r = new byte[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = fromArray(a, i, vmask);
                av.intoArray(r, i);
            }
        }

        int index = fi.apply(a.length);
        boolean shouldFail = isIndexOutOfBoundsForMask(mask, index, a.length);
        try {
            fromArray(a, index, vmask);
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
    }

    @Test(dataProvider = "byteMaskProviderForIOOBE")
    static void storeArrayMaskIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        byte[] a = fa.apply(SPECIES.length());
        byte[] r = new byte[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                intoArray(av, r, i, vmask);
            }
        }

        int index = fi.apply(a.length);
        boolean shouldFail = isIndexOutOfBoundsForMask(mask, index, a.length);
        try {
            ByteVector av = ByteVector.fromArray(SPECIES, a, 0);
            intoArray(av, a, index, vmask);
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
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
        Assert.assertEquals(r, mask);
    }


    @Test(dataProvider = "byteMemorySegmentProvider")
    static void loadStoreMemorySegment(IntFunction<byte[]> fa,
                                       LongFunction<MemorySegment> fb,
                                       ByteOrder bo) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), fb);
        MemorySegment r = fb.apply((int) a.byteSize());

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromMemorySegment(SPECIES, a, i, bo);
                av.intoMemorySegment(r, i, bo);
            }
        }
        long m = r.mismatch(a);
        Assert.assertEquals(m, -1, "Segments not equal");
    }

    @Test(dataProvider = "byteByteProviderForIOOBE")
    static void loadMemorySegmentIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), i -> MemorySegment.allocateNative(i, Byte.SIZE, SegmentScope.auto()));
        MemorySegment r = MemorySegment.allocateNative(a.byteSize(), Byte.SIZE, SegmentScope.auto());

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = fromMemorySegment(a, i, ByteOrder.nativeOrder());
                av.intoMemorySegment(r, i, ByteOrder.nativeOrder());
            }
        }

        int index = fi.apply((int) a.byteSize());
        boolean shouldFail = isIndexOutOfBounds(SPECIES.vectorByteSize(), index, (int) a.byteSize());
        try {
            fromMemorySegment(a, index, ByteOrder.nativeOrder());
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
    }

    @Test(dataProvider = "byteByteProviderForIOOBE")
    static void storeMemorySegmentIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), i -> MemorySegment.allocateNative(i, Byte.SIZE, SegmentScope.auto()));
        MemorySegment r = MemorySegment.allocateNative(a.byteSize(), Byte.SIZE, SegmentScope.auto());

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromMemorySegment(SPECIES, a, i, ByteOrder.nativeOrder());
                intoMemorySegment(av, r, i, ByteOrder.nativeOrder());
            }
        }

        int index = fi.apply((int) a.byteSize());
        boolean shouldFail = isIndexOutOfBounds(SPECIES.vectorByteSize(), index, (int) a.byteSize());
        try {
            ByteVector av = ByteVector.fromMemorySegment(SPECIES, a, 0, ByteOrder.nativeOrder());
            intoMemorySegment(av, r, index, ByteOrder.nativeOrder());
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
    }

    @Test(dataProvider = "byteMemorySegmentMaskProvider")
    static void loadStoreMemorySegmentMask(IntFunction<byte[]> fa,
                                           LongFunction<MemorySegment> fb,
                                           IntFunction<boolean[]> fm,
                                           ByteOrder bo) {
        byte[] _a = fa.apply(SPECIES.length());
        MemorySegment a = toSegment(_a, fb);
        MemorySegment r = fb.apply((int) a.byteSize());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromMemorySegment(SPECIES, a, i, bo, vmask);
                av.intoMemorySegment(r, i, bo);
            }
        }
        assertArraysEquals(segmentToArray(r), _a, mask);


        r = fb.apply((int) a.byteSize());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromMemorySegment(SPECIES, a, i, bo);
                av.intoMemorySegment(r, i, bo, vmask);
            }
        }
        assertArraysEquals(segmentToArray(r), _a, mask);
    }

    @Test(dataProvider = "byteByteMaskProviderForIOOBE")
    static void loadMemorySegmentMaskIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), i -> MemorySegment.allocateNative(i, Byte.SIZE, SegmentScope.auto()));
        MemorySegment r = MemorySegment.allocateNative(a.byteSize(), Byte.SIZE, SegmentScope.auto());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = fromMemorySegment(a, i, ByteOrder.nativeOrder(), vmask);
                av.intoMemorySegment(r, i, ByteOrder.nativeOrder());
            }
        }

        int index = fi.apply((int) a.byteSize());
        boolean shouldFail = isIndexOutOfBoundsForMask(mask, index, (int) a.byteSize(), SPECIES.elementSize() / 8);
        try {
            fromMemorySegment(a, index, ByteOrder.nativeOrder(), vmask);
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
    }

    @Test(dataProvider = "byteByteMaskProviderForIOOBE")
    static void storeMemorySegmentMaskIOOBE(IntFunction<byte[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), i -> MemorySegment.allocateNative(i, Byte.SIZE, SegmentScope.auto()));
        MemorySegment r = MemorySegment.allocateNative(a.byteSize(), Byte.SIZE, SegmentScope.auto());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                ByteVector av = ByteVector.fromMemorySegment(SPECIES, a, i, ByteOrder.nativeOrder());
                intoMemorySegment(av, r, i, ByteOrder.nativeOrder(), vmask);
            }
        }

        int index = fi.apply((int) a.byteSize());
        boolean shouldFail = isIndexOutOfBoundsForMask(mask, index, (int) a.byteSize(), SPECIES.elementSize() / 8);
        try {
            ByteVector av = ByteVector.fromMemorySegment(SPECIES, a, 0, ByteOrder.nativeOrder());
            intoMemorySegment(av, a, index, ByteOrder.nativeOrder(), vmask);
            if (shouldFail) {
                Assert.fail("Failed to throw IndexOutOfBoundsException");
            }
        } catch (IndexOutOfBoundsException e) {
            if (!shouldFail) {
                Assert.fail("Unexpected IndexOutOfBoundsException");
            }
        }
    }

    @Test(dataProvider = "byteMemorySegmentProvider")
    static void loadStoreReadonlyMemorySegment(IntFunction<byte[]> fa,
                                               LongFunction<MemorySegment> fb,
                                               ByteOrder bo) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), fb).asReadOnly();

        Assert.assertThrows(
                UnsupportedOperationException.class,
                () -> SPECIES.zero().intoMemorySegment(a, 0, bo)
        );

        Assert.assertThrows(
                UnsupportedOperationException.class,
                () -> SPECIES.zero().intoMemorySegment(a, 0, bo, SPECIES.maskAll(true))
        );

        Assert.assertThrows(
                UnsupportedOperationException.class,
                () -> SPECIES.zero().intoMemorySegment(a, 0, bo, SPECIES.maskAll(false))
        );

        VectorMask<Byte> m = SPECIES.shuffleFromOp(i -> i % 2 == 0 ? 1 : -1)
                .laneIsValid();
        Assert.assertThrows(
                UnsupportedOperationException.class,
                () -> SPECIES.zero().intoMemorySegment(a, 0, bo, m)
        );
    }


    @Test(dataProvider = "maskProvider")
    static void loadStoreMask(IntFunction<boolean[]> fm) {
        boolean[] a = fm.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                VectorMask<Byte> vmask = SPECIES.loadMask(a, i);
                vmask.intoArray(r, i);
            }
        }
        Assert.assertEquals(r, a);
    }


    @Test
    static void loadStoreShuffle() {
        IntUnaryOperator fn = a -> a + 5;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            var shuffle = VectorShuffle.fromOp(SPECIES, fn);
            int [] r = shuffle.toArray();

            int [] a = expectedShuffle(SPECIES.length(), fn);
            Assert.assertEquals(r, a);
       }
    }



    static void assertArraysEquals(boolean[] r, byte[] a) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], (a[i] & 1) == 1);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], (a[i] & 1) == 1, "at index #" + i);
        }
    }

    static void assertArraysEquals(boolean[] r, boolean[] a, boolean[] mask) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], mask[i % SPECIES.length()] && a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[i % SPECIES.length()] && a[i], "at index #" + i);
        }
    }

    static boolean[] convertToBooleanArray(byte[] a) {
        boolean[] r = new boolean[a.length];

        for (int i = 0; i < a.length; i++) {
            r[i] = (a[i] & 1) == 1;
        }

        return r;
    }

    @Test(dataProvider = "byteProvider")
    static void loadByteStoreBooleanArray(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                av.intoBooleanArray(r, i);
            }
        }
        assertArraysEquals(r, a);
    }

    @Test(dataProvider = "byteProvider")
    static void loadStoreBooleanArray(IntFunction<byte[]> fa) {
        boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
        boolean[] r = new boolean[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, i);
                av.intoBooleanArray(r, i);
            }
        }
        Assert.assertEquals(r, a);
    }

    @Test(dataProvider = "byteMaskProvider")
    static void loadStoreMaskBooleanArray(IntFunction<byte[]> fa,
                                          IntFunction<boolean[]> fm) {
        boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
        boolean[] r = new boolean[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Byte> vmask = VectorMask.fromValues(SPECIES, mask);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, i, vmask);
                av.intoBooleanArray(r, i);
            }
        }
        assertArraysEquals(r, a, mask);


        r = new boolean[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, i);
                av.intoBooleanArray(r, i, vmask);
            }
        }
        assertArraysEquals(r, a, mask);
    }


    // Gather/Scatter load/store tests

    static void assertGatherArraysEquals(byte[] r, byte[] a, int[] indexMap, boolean[] mask) {
        for (int i = 0; i < r.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Assert.assertEquals(r[j], a[indexMap[j]], "at index #" + j);
                } else {
                    Assert.assertEquals(r[j], (byte) 0, "at index #" + j);
                }
            }
        }
    }

    static void assertGatherArraysEquals(byte[] r, byte[] a, long[] indexMap, boolean[] mask) {
        for (int i = 0; i < r.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Objects.checkIndex(indexMap[j], a.length);
                    Assert.assertEquals(r[j], a[(int)indexMap[j]], "at index #" + j);
                } else {
                    Assert.assertEquals(r[j], (byte) 0, "at index #" + j);
                }
            }
        }
    }

    static void assertScatterArraysEquals(byte[] r, byte[] a, int[] indexMap, boolean[] mask) {
        byte[] expected = new byte[r.length];

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    expected[indexMap[j]] = a[j];
                }
            }
        }

        Assert.assertEquals(r, expected);
    }

    static void assertScatterArraysEquals(byte[] r, byte[] a, long[] indexMap, boolean[] mask) {
        byte[] expected = new byte[r.length];

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Objects.checkIndex(indexMap[j], r.length);
                    expected[(int)indexMap[j]] = a[j];
                }
            }
        }

        Assert.assertEquals(r, expected);
    }

    @DataProvider
    public Object[][] gatherScatterIntProvider() {
        return INT_INDEX_GENERATORS.stream().
                flatMap(fs -> BYTE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] gatherScatterLongProvider() {
        return LONG_INDEX_GENERATORS.stream().
                flatMap(fs -> BYTE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] gatherScatterIntMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            BYTE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm, fs};
            }))).
            toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] gatherScatterLongMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> LONG_INDEX_GENERATORS.stream().flatMap(fm ->
            BYTE_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm, fs};
            }))).
            toArray(Object[][]::new);
    }

    @Test(dataProvider = "gatherScatterIntProvider")
    static void gatherInt(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            int[] b = fs.apply(a.length, a.length);
            byte[] r = new byte[b.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, idx);
                    av.intoArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterLongProvider")
    static void gatherLong(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            long[] b = fs.apply(a.length, (long) a.length);
            byte[] r = new byte[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, idx);
                    av.intoArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterIntMaskProvider")
    static void gatherIntMask(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            int[] b = fs.apply(a.length, a.length);
            byte[] r = new byte[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, idx, vmask);
                    av.intoArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, mask);
        }
    }

    @Test(dataProvider = "gatherScatterLongMaskProvider")
    static void gatherLongMask(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            long[] b = fs.apply(a.length, (long) a.length);
            byte[] r = new byte[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, idx, vmask);
                    av.intoArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, mask);
        }
    }

    @Test(dataProvider = "gatherScatterIntProvider")
    static void scatterInt(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            int[] b = fs.apply(a.length, a.length);
            byte[] r = new byte[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoArray(r, idx);
                }
            }

            assertScatterArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterLongProvider")
    static void scatterLong(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            long[] b = fs.apply(a.length, (long) a.length);
            byte[] r = new byte[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoArray(r, idx);
                }
            }

            assertScatterArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterIntMaskProvider")
    static void scatterIntMask(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            int[] b = fs.apply(a.length, a.length);
            byte[] r = new byte[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoArray(r, idx, vmask);
                }
            }

            assertScatterArraysEquals(r, a, b, mask);
        }
    }

    @Test(dataProvider = "gatherScatterLongMaskProvider")
    static void scatterLongMask(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            long[] b = fs.apply(a.length, (long) a.length);
            byte[] r = new byte[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoArray(r, idx, vmask);
                }
            }

            assertScatterArraysEquals(r, a, b, mask);
        }
    }


    static void assertGatherArraysEquals(boolean[] r, boolean[] a, int[] indexMap, boolean[] mask) {
        for (int i = 0; i < r.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Assert.assertEquals(r[j], a[indexMap[j]], "at index #" + j);
                } else {
                    Assert.assertEquals(r[j], false, "at index #" + j);
                }
            }
        }
    }

    static void assertGatherArraysEquals(boolean[] r, boolean[] a, long[] indexMap, boolean[] mask) {
        for (int i = 0; i < r.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Objects.checkIndex(indexMap[j], a.length);
                    Assert.assertEquals(r[j], a[(int)indexMap[j]], "at index #" + j);
                } else {
                    Assert.assertEquals(r[j], false, "at index #" + j);
                }
            }
        }
    }

    static void assertScatterArraysEquals(boolean[] r, boolean[] a, int[] indexMap, boolean[] mask) {
        boolean[] expected = new boolean[r.length];

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    expected[indexMap[j]] = a[j];
                }
            }
        }

        Assert.assertEquals(r, expected);
    }

    static void assertScatterArraysEquals(boolean[] r, boolean[] a, long[] indexMap, boolean[] mask) {
        boolean[] expected = new boolean[r.length];

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Objects.checkIndex(indexMap[j], r.length);
                    expected[(int)indexMap[j]] = a[j];
                }
            }
        }

        Assert.assertEquals(r, expected);
    }

    @Test(dataProvider = "gatherScatterIntProvider")
    static void booleanGatherInt(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            int[] b = fs.apply(a.length, a.length);
            boolean[] r = new boolean[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, idx);
                    av.intoBooleanArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterLongProvider")
    static void booleanGatherLong(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            long[] b = fs.apply(a.length, (long) a.length);
            boolean[] r = new boolean[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, idx);
                    av.intoBooleanArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterIntMaskProvider")
    static void booleanGatherIntMask(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            int[] b = fs.apply(a.length, a.length);
            boolean[] r = new boolean[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, idx, vmask);
                    av.intoBooleanArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, mask);
        }
    }

    @Test(dataProvider = "gatherScatterLongMaskProvider")
    static void booleanGatherLongMask(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            long[] b = fs.apply(a.length, (long) a.length);
            boolean[] r = new boolean[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, idx, vmask);
                    av.intoBooleanArray(r, i);
                }
            }

            assertGatherArraysEquals(r, a, b, mask);
        }
    }

    @Test(dataProvider = "gatherScatterIntProvider")
    static void booleanScatterInt(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            int[] b = fs.apply(a.length, a.length);
            boolean[] r = new boolean[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, i);
                    av.intoBooleanArray(r, idx);
                }
            }

            assertScatterArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterLongProvider")
    static void booleanScatterLong(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            long[] b = fs.apply(a.length, (long) a.length);
            boolean[] r = new boolean[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, i);
                    av.intoBooleanArray(r, idx);
                }
            }

            assertScatterArraysEquals(r, a, b, null);
        }
    }

    @Test(dataProvider = "gatherScatterIntMaskProvider")
    static void booleanScatterIntMask(IntFunction<byte[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            int[] b = fs.apply(a.length, a.length);
            boolean[] r = new boolean[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, i);
                    av.intoBooleanArray(r, idx, vmask);
                }
            }

            assertScatterArraysEquals(r, a, b, mask);
        }
    }

    @Test(dataProvider = "gatherScatterLongMaskProvider")
    static void booleanScatterLongMask(IntFunction<byte[]> fa, BiFunction<Integer,Long,long[]> fs, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            boolean[] a = convertToBooleanArray(fa.apply(SPECIES.length()));
            long[] b = fs.apply(a.length, (long) a.length);
            boolean[] r = new boolean[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromBooleanArray(SPECIES, a, i);
                    av.intoBooleanArray(r, idx, vmask);
                }
            }

            assertScatterArraysEquals(r, a, b, mask);
        }
    }

    static void assertGatherMemorySegmentsEquals(byte[] r, MemorySegment ms, int[] indexMap, ByteOrder bo, boolean[] mask) {
        for (int i = 0; i < r.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Assert.assertEquals(r[j], ms.get(ELEMENT_LAYOUT.withOrder(bo), indexMap[j]), "at index #" + j);
                } else {
                    Assert.assertEquals(r[j], (byte) 0, "at index #" + j);
                }
            }
        }
    }

    static void assertGatherMemorySegmentsEquals(byte[] r, MemorySegment ms, long[] indexMap, ByteOrder bo, boolean[] mask) {
        for (int i = 0; i < r.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    Assert.assertEquals(r[j], ms.get(ELEMENT_LAYOUT.withOrder(bo), indexMap[j]), "at index #" + j);
                } else {
                    Assert.assertEquals(r[j], (byte) 0, "at index #" + j);
                }
            }
        }
    }

    static void assertScatterMemorySegmentsEquals(MemorySegment ms, byte[] a, int[] indexMap, ByteOrder bo, boolean[] mask) {
        MemorySegment expected = MEMORY_SEGMENT_GENERATORS.get(0).apply(ms.byteSize());

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    expected.set(ELEMENT_LAYOUT.withOrder(bo), indexMap[j], a[j]);
                }
            }
        }

        Assert.assertEquals(expected.mismatch(ms), -1);
    }

    static void assertScatterMemorySegmentsEquals(MemorySegment ms, byte[] a, long[] indexMap, ByteOrder bo, boolean[] mask) {
        MemorySegment expected = MEMORY_SEGMENT_GENERATORS.get(0).apply(ms.byteSize());

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask == null || mask[j % SPECIES.length()]) {
                    expected.set(ELEMENT_LAYOUT.withOrder(bo), indexMap[j], a[j]);
                }
            }
        }

        Assert.assertEquals(expected.mismatch(ms), -1);
    }

    @DataProvider
    public Object[][] msGatherScatterIntProvider() {
        return BYTE_GENERATORS.stream().
                flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                        flatMap(fb -> BYTE_ORDER_VALUES.stream().
                                flatMap(bo -> INT_INDEX_GENERATORS.stream().map(fs -> {
                                    return new Object[]{fa, fb, fs, bo};
                                })))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] msGatherScatterLongProvider() {
        return BYTE_GENERATORS.stream().
                flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                        flatMap(fb -> BYTE_ORDER_VALUES.stream().
                                flatMap(bo -> LONG_INDEX_GENERATORS.stream().map(fs -> {
                                    return new Object[]{fa, fb, fs, bo};
                                })))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] msGatherScatterIntMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_GENERATORS.stream().
                        flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                                flatMap(fb -> BYTE_ORDER_VALUES.stream().
                                        flatMap(bo -> INT_INDEX_GENERATORS.stream().map(fs -> {
                                            return new Object[]{fa, fb, fs, bo, fm};
                                        }))))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] msGatherScatterLongMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_GENERATORS.stream().
                        flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                                flatMap(fb -> BYTE_ORDER_VALUES.stream().
                                        flatMap(bo -> LONG_INDEX_GENERATORS.stream().map(fs -> {
                                            return new Object[]{fa, fb, fs, bo, fm};
                                        }))))).
                toArray(Object[][]::new);
    }

    @Test(dataProvider = "msGatherScatterIntProvider")
    static void msGatherInt(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                            BiFunction<Integer,Integer,int[]> fs, ByteOrder bo) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = toSegment(a, fb);
            int[] b = fs.apply(a.length, (int) ms.byteSize() - Byte.BYTES + 1);
            byte[] r = new byte[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromMemorySegment(SPECIES, ms, idx, bo);
                    av.intoArray(r, i);
                }
            }

            assertGatherMemorySegmentsEquals(r, ms, b, bo, null);
        }
    }

    @Test(dataProvider = "msGatherScatterLongProvider")
    static void msGatherLong(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                             BiFunction<Integer,Long,long[]> fs, ByteOrder bo) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = toSegment(a, fb);
            long[] b = fs.apply(a.length, ms.byteSize() - Byte.BYTES + 1);
            byte[] r = new byte[a.length];

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromMemorySegment(SPECIES, ms, idx, bo);
                    av.intoArray(r, i);
                }
            }

            assertGatherMemorySegmentsEquals(r, ms, b, bo, null);
        }
    }

    @Test(dataProvider = "msGatherScatterIntMaskProvider")
    static void msGatherIntMask(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                                BiFunction<Integer,Integer,int[]> fs, ByteOrder bo, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = toSegment(a, fb);
            int[] b = fs.apply(a.length, (int) ms.byteSize() - Byte.BYTES + 1);
            byte[] r = new byte[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromMemorySegment(SPECIES, ms, idx, bo, vmask);
                    av.intoArray(r, i);
                }
            }

            assertGatherMemorySegmentsEquals(r, ms, b, bo, mask);
        }
    }

    @Test(dataProvider = "msGatherScatterLongMaskProvider")
    static void msGatherLongMask(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                                 BiFunction<Integer,Long,long[]> fs, ByteOrder bo, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = toSegment(a, fb);
            long[] b = fs.apply(a.length, ms.byteSize() - Byte.BYTES + 1);
            byte[] r = new byte[a.length];
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < r.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromMemorySegment(SPECIES, ms, idx, bo, vmask);
                    av.intoArray(r, i);
                }
            }

            assertGatherMemorySegmentsEquals(r, ms, b, bo, mask);
        }
    }

    @Test(dataProvider = "msGatherScatterIntProvider")
    static void msScatterInt(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                             BiFunction<Integer,Integer,int[]> fs, ByteOrder bo) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = fb.apply(a.length * Byte.BYTES);
            int[] b = fs.apply(a.length, (int) ms.byteSize() - Byte.BYTES + 1);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoMemorySegment(ms, idx, bo);
                }
            }

            assertScatterMemorySegmentsEquals(ms, a, b, bo, null);
        }
    }

    @Test(dataProvider = "msGatherScatterLongProvider")
    static void msScatterLong(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                              BiFunction<Integer,Long,long[]> fs, ByteOrder bo) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = fb.apply(a.length * Byte.BYTES);
            long[] b = fs.apply(a.length, ms.byteSize() - Byte.BYTES + 1);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoMemorySegment(ms, idx, bo);
                }
            }

            assertScatterMemorySegmentsEquals(ms, a, b, bo, null);
        }
    }

    @Test(dataProvider = "msGatherScatterIntMaskProvider")
    static void msScatterIntMask(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                                 BiFunction<Integer,Integer,int[]> fs, ByteOrder bo, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_INT_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = fb.apply(a.length * Byte.BYTES);
            int[] b = fs.apply(a.length, (int) ms.byteSize() - Byte.BYTES + 1);
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    IntVector idx = IntVector.fromArray(SAME_LENGTH_INT_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoMemorySegment(ms, idx, bo, vmask);
                }
            }

            assertScatterMemorySegmentsEquals(ms, a, b, bo, mask);
        }
    }

    @Test(dataProvider = "msGatherScatterLongMaskProvider")
    static void msScatterLongMask(IntFunction<byte[]> fa, LongFunction<MemorySegment> fb,
                                  BiFunction<Integer,Long,long[]> fs, ByteOrder bo, IntFunction<boolean[]> fm) {
        if (SAME_LENGTH_LONG_SPECIES != null) {
            byte[] a = fa.apply(SPECIES.length());
            MemorySegment ms = fb.apply(a.length * Byte.BYTES);
            long[] b = fs.apply(a.length, ms.byteSize() - Byte.BYTES + 1);
            boolean[] mask = fm.apply(SPECIES.length());
            VectorMask<Byte> vmask = VectorMask.fromArray(SPECIES, mask, 0);

            for (int ic = 0; ic < INVOC_COUNT; ic++) {
                for (int i = 0; i < a.length; i += SPECIES.length()) {
                    LongVector idx = LongVector.fromArray(SAME_LENGTH_LONG_SPECIES, b, i);
                    ByteVector av = ByteVector.fromArray(SPECIES, a, i);
                    av.intoMemorySegment(ms, idx, bo, vmask);
                }
            }

            assertScatterMemorySegmentsEquals(ms, a, b, bo, mask);
        }
    }
}
