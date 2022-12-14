/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
 * @modules jdk.incubator.foreign jdk.incubator.vector java.base/jdk.internal.vm.annotation
 * @run testng/othervm -XX:-TieredCompilation Halffloat128VectorLoadStoreTests
 *
 */

// -- This file was mechanically generated: Do not edit! -- //

import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;
import jdk.incubator.vector.Halffloat;
import jdk.incubator.vector.HalffloatVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorShuffle;
import jdk.internal.vm.annotation.DontInline;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteOrder;
import java.util.List;
import java.util.function.*;

@Test
public class Halffloat128VectorLoadStoreTests extends AbstractVectorLoadStoreTest {
    static final VectorSpecies<Halffloat> SPECIES =
                HalffloatVector.SPECIES_128;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 100);


    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / 128);

    static void assertArraysEquals(short[] r, short[] a, boolean[] mask) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? a[i] : (short) 0);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? a[i] : (short) 0, "at index #" + i);
        }
    }

    static final List<IntFunction<short[]>> SHORT_GENERATORS = List.of(
            withToString("short[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i * 5));
            }),
            withToString("short[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((short)(i + 1) == 0) ? 1 : (short)(i + 1)));
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
    public Object[][] shortProvider() {
        return SHORT_GENERATORS.stream().
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
    public Object[][] shortProviderForIOOBE() {
        var f = SHORT_GENERATORS.get(0);
        return INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi};
                }).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortMaskProviderForIOOBE() {
        var f = SHORT_GENERATORS.get(0);
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortMemorySegmentProvider() {
        return SHORT_GENERATORS.stream().
                flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                        flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                            return new Object[]{fa, fb, bo};
                        }))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortMemorySegmentMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATORS.stream().
                        flatMap(fa -> MEMORY_SEGMENT_GENERATORS.stream().
                                flatMap(fb -> BYTE_ORDER_VALUES.stream().map(bo -> {
                            return new Object[]{fa, fb, fm, bo};
                        })))).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortByteProviderForIOOBE() {
        var f = SHORT_GENERATORS.get(0);
        return BYTE_INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi};
                }).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortByteMaskProviderForIOOBE() {
        var f = SHORT_GENERATORS.get(0);
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> BYTE_INDEX_GENERATORS.stream().map(fi -> {
                    return new Object[] {f, fi, fm};
                })).
                toArray(Object[][]::new);
    }

    static MemorySegment toSegment(short[] a, IntFunction<MemorySegment> fb) {
        MemorySegment ms = fb.apply(a.length * SPECIES.elementSize() / 8);
        for (int i = 0; i < a.length; i++) {
            ms.set(ValueLayout.JAVA_SHORT, i * SPECIES.elementSize() / 8 , a[i]);
        }
        return ms;
    }

    static short[] segmentToArray(MemorySegment ms) {
        return ms.toArray(ValueLayout.JAVA_SHORT);
    }


    interface ToShortF {
        short apply(int i);
    }

    static short[] fill(int s , ToShortF f) {
        return fill(new short[s], f);
    }

    static short[] fill(short[] a, ToShortF f) {
        for (int i = 0; i < a.length; i++) {
            a[i] = f.apply(i);
        }
        return a;
    }

    @DontInline
    static HalffloatVector fromArray(short[] a, int i) {
        return HalffloatVector.fromArray(SPECIES, a, i);
    }

    @DontInline
    static HalffloatVector fromArray(short[] a, int i, VectorMask<Halffloat> m) {
        return HalffloatVector.fromArray(SPECIES, a, i, m);
    }

    @DontInline
    static void intoArray(HalffloatVector v, short[] a, int i) {
        v.intoArray(a, i);
    }

    @DontInline
    static void intoArray(HalffloatVector v, short[] a, int i, VectorMask<Halffloat> m) {
        v.intoArray(a, i, m);
    }

    @DontInline
    static HalffloatVector fromMemorySegment(MemorySegment a, int i, ByteOrder bo) {
        return HalffloatVector.fromMemorySegment(SPECIES, a, i, bo);
    }

    @DontInline
    static HalffloatVector fromMemorySegment(MemorySegment a, int i, ByteOrder bo, VectorMask<Halffloat> m) {
        return HalffloatVector.fromMemorySegment(SPECIES, a, i, bo, m);
    }

    @DontInline
    static void intoMemorySegment(HalffloatVector v, MemorySegment a, int i, ByteOrder bo) {
        v.intoMemorySegment(a, i, bo);
    }

    @DontInline
    static void intoMemorySegment(HalffloatVector v, MemorySegment a, int i, ByteOrder bo, VectorMask<Halffloat> m) {
        v.intoMemorySegment(a, i, bo, m);
    }

    @Test(dataProvider = "shortProvider")
    static void loadStoreArray(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i);
            }
        }
        Assert.assertEquals(r, a);
    }

    @Test(dataProvider = "shortProviderForIOOBE")
    static void loadArrayIOOBE(IntFunction<short[]> fa, IntFunction<Integer> fi) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = fromArray(a, i);
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

    @Test(dataProvider = "shortProviderForIOOBE")
    static void storeArrayIOOBE(IntFunction<short[]> fa, IntFunction<Integer> fi) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i);
                intoArray(av, r, i);
            }
        }

        int index = fi.apply(a.length);
        boolean shouldFail = isIndexOutOfBounds(SPECIES.length(), index, a.length);
        try {
            HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, 0);
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


    @Test(dataProvider = "shortMaskProvider")
    static void loadStoreMaskArray(IntFunction<short[]> fa,
                                   IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Halffloat> vmask = VectorMask.fromValues(SPECIES, mask);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i, vmask);
                av.intoArray(r, i);
            }
        }
        assertArraysEquals(r, a, mask);


        r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, vmask);
            }
        }
        assertArraysEquals(r, a, mask);
    }

    @Test(dataProvider = "shortMaskProviderForIOOBE")
    static void loadArrayMaskIOOBE(IntFunction<short[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Halffloat> vmask = VectorMask.fromValues(SPECIES, mask);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = fromArray(a, i, vmask);
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

    @Test(dataProvider = "shortMaskProviderForIOOBE")
    static void storeArrayMaskIOOBE(IntFunction<short[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Halffloat> vmask = VectorMask.fromValues(SPECIES, mask);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i);
                intoArray(av, r, i, vmask);
            }
        }

        int index = fi.apply(a.length);
        boolean shouldFail = isIndexOutOfBoundsForMask(mask, index, a.length);
        try {
            HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, 0);
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


    @Test(dataProvider = "shortMaskProvider")
    static void loadStoreMask(IntFunction<short[]> fa,
                              IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = new boolean[mask.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Halffloat> vmask = VectorMask.fromArray(SPECIES, mask, i);
                vmask.intoArray(r, i);
            }
        }
        Assert.assertEquals(r, mask);
    }


    // TODO: Fix and Enable loadStoreMemorySegment, loadMemorySegmentIOOBE, storeMemorySegmentIOOBE, loadStoreMemorySegmentMask
    @Test(dataProvider = "shortByteMaskProviderForIOOBE")
    static void loadMemorySegmentMaskIOOBE(IntFunction<short[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), i -> MemorySegment.allocateNative(i, ResourceScope.newImplicitScope()));
        MemorySegment r = MemorySegment.allocateNative(a.byteSize(), ResourceScope.newImplicitScope());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Halffloat> vmask = VectorMask.fromValues(SPECIES, mask);

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                HalffloatVector av = fromMemorySegment(a, i, ByteOrder.nativeOrder(), vmask);
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

    @Test(dataProvider = "shortByteMaskProviderForIOOBE")
    static void storeMemorySegmentMaskIOOBE(IntFunction<short[]> fa, IntFunction<Integer> fi, IntFunction<boolean[]> fm) {
        MemorySegment a = toSegment(fa.apply(SPECIES.length()), i -> MemorySegment.allocateNative(i, ResourceScope.newImplicitScope()));
        MemorySegment r = MemorySegment.allocateNative(a.byteSize(), ResourceScope.newImplicitScope());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Halffloat> vmask = VectorMask.fromValues(SPECIES, mask);

        int l = (int) a.byteSize();
        int s = SPECIES.vectorByteSize();

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < l; i += s) {
                HalffloatVector av = HalffloatVector.fromMemorySegment(SPECIES, a, i, ByteOrder.nativeOrder());
                intoMemorySegment(av, r, i, ByteOrder.nativeOrder(), vmask);
            }
        }

        int index = fi.apply((int) a.byteSize());
        boolean shouldFail = isIndexOutOfBoundsForMask(mask, index, (int) a.byteSize(), SPECIES.elementSize() / 8);
        try {
            HalffloatVector av = HalffloatVector.fromMemorySegment(SPECIES, a, 0, ByteOrder.nativeOrder());
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

    @Test(dataProvider = "shortMemorySegmentProvider")
    static void loadStoreReadonlyMemorySegment(IntFunction<short[]> fa,
                                               IntFunction<MemorySegment> fb,
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

        VectorMask<Halffloat> m = SPECIES.shuffleFromOp(i -> i % 2 == 0 ? 1 : -1)
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
                VectorMask<Halffloat> vmask = SPECIES.loadMask(a, i);
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





    // Gather/Scatter load/store tests

    static void assertGatherArraysEquals(short[] r, short[] a, int[] indexMap) {
        int i = 0;
        int j = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                j = i;
                for (; j < i + SPECIES.length(); j++) {
                    Assert.assertEquals(r[j], a[i + indexMap[j]]);
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[j], a[i + indexMap[j]], "at index #" + j);
        }
    }

    static void assertGatherArraysEquals(short[] r, short[] a, int[] indexMap, boolean[] mask) {
        int i = 0;
        int j = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                j = i;
                for (; j < i + SPECIES.length(); j++) {
                    Assert.assertEquals(r[j], mask[j % SPECIES.length()] ? a[i + indexMap[j]]: (short) 0);
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[j % SPECIES.length()] ? a[i + indexMap[j]]: (short) 0, "at index #" + j);
        }
    }

    static void assertScatterArraysEquals(short[] r, short[] a, int[] indexMap, boolean[] mask) {
        short[] expected = new short[r.length];

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                if (mask[j % SPECIES.length()]) {
                    expected[i + indexMap[j]] = a[j];
                }
            }
        }

        Assert.assertEquals(r, expected);
    }

    static void assertScatterArraysEquals(short[] r, short[] a, int[] indexMap) {
        short[] expected = new short[r.length];

        // Store before checking, since the same location may be stored to more than once
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            for (int j = i; j < i + SPECIES.length(); j++) {
                expected[i + indexMap[j]] = a[j];
            }
        }

        Assert.assertEquals(r, expected);
    }

    @DataProvider
    public Object[][] gatherScatterProvider() {
        return INT_INDEX_GENERATORS.stream().
                flatMap(fs -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] gatherScatterMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm, fs};
            }))).
            toArray(Object[][]::new);
    }


    @Test(dataProvider = "gatherScatterProvider")
    static void gather(IntFunction<short[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        short[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i, b, i);
                av.intoArray(r, i);
            }
        }

        assertGatherArraysEquals(r, a, b);
    }

    @Test(dataProvider = "gatherScatterMaskProvider")
    static void gatherMask(IntFunction<short[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        short[] r = new short[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Halffloat> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i, b, i, vmask);
                av.intoArray(r, i);
            }
        }

        assertGatherArraysEquals(r, a, b, mask);
    }

    @Test(dataProvider = "gatherScatterProvider")
    static void scatter(IntFunction<short[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        short[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i);
            }
        }

        assertScatterArraysEquals(r, a, b);
    }

    @Test(dataProvider = "gatherScatterMaskProvider")
    static void scatterMask(IntFunction<short[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        short[] r = new short[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Halffloat> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                HalffloatVector av = HalffloatVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i, vmask);
            }
        }

        assertScatterArraysEquals(r, a, b, mask);
    }



}
