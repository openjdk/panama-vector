/*
 * Copyright (c) 2018, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
package benchmark.jdk.incubator.vector;

import jdk.incubator.vector.*;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Population count algorithms from "Faster Population Counts Using AVX2 Instructions", 2018 by Mula, Kurz, Lemire
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class PopulationCount extends AbstractVectorBenchmark {
    @Param({"64", "1024", "65536"})
    int size;

    private long[] data;

    @Setup
    public void init() {
        data = fillLong(size, i -> RANDOM.nextLong());
//        data = fillLong(size, i -> 0L);
//        data = fillLong(size, i -> -1L);

        checkConsistency();
    }

    @TearDown
    public void tearDown() {
        checkConsistency();
    }

    void checkConsistency() {
        long popCount = longBitCount();
        assertEquals(popCount, treeOfAdders());
        assertEquals(popCount, WilkesWheelerGill());
        assertEquals(popCount, Wegner());
        assertEquals(popCount, Lauradoux());
        assertEquals(popCount, HarleySeal());
        assertEquals(popCount, Mula128());
        assertEquals(popCount, Mula256());
        assertEquals(popCount, HarleySeal256());
    }

    long tail(int upper) {
        long acc = 0;
        for (int i = upper; i < data.length; i++) {
            acc += Long.bitCount(data[i]);
        }
        return acc;
    }

    @Benchmark
    public long longBitCount() {
        long acc = 0;
        for (int i = 0; i < data.length; i++) {
            acc += Long.bitCount(data[i]);
        }
        return acc;
    }

    /* ============================================================================================================== */

    // FIGURE 4. The Wegner function in C

    long popcntWegner(long x) {
        int v = 0;
        while (x != 0) {
            x &= x - 1;
            v++;
        }
        return v;
    }

    @Benchmark
    public long Wegner() {
        long acc = 0;
        for (int i = 0; i < data.length; i++) {
            acc += popcntWegner(data[i]);
        }
        return acc;
    }

    /* ============================================================================================================== */

    // FIGURE 2. A naive tree-of-adders function in C

    static long popcntTree(long x) {
        long c1  = 0x5555555555555555L;
        long c2  = 0x3333333333333333L;
        long c4  = 0x0F0F0F0F0F0F0F0FL;
        long c8  = 0x00FF00FF00FF00FFL;
        long c16 = 0x0000FFFF0000FFFFL;
        long c32 = 0x00000000FFFFFFFFL;

        x = (x & c1)  + ((x >>> 1)  & c1);
        x = (x & c2)  + ((x >>> 2)  & c2);
        x = (x & c4)  + ((x >>> 4)  & c4);
        x = (x & c8)  + ((x >>> 8)  & c8);
        x = (x & c16) + ((x >>> 16) & c16);
        x = (x & c32) + ((x >>> 32) & c32);
        return x;
    }

    @Benchmark
    public long treeOfAdders() {
        long acc = 0;
        for (int i = 0; i < data.length; i++) {
            acc += popcntTree(data[i]);
        }
        return acc;
    }

    /* ============================================================================================================== */

    // FIGURE 3. The Wilkes-Wheeler-Gill function in C

    static long popcntWWG(long x) {
        long c1 = 0x5555555555555555L;
        long c2 = 0x3333333333333333L;
        long c4 = 0x0F0F0F0F0F0F0F0FL;

        x -= (x >>> 1) & c1;
        x = (( x >>> 2) & c2) + (x & c2) ;
        x = ( x + (x >>> 4) ) & c4;
        x *= 0x0101010101010101L;
        x = x >>> 56;
        return x;
    }

    @Benchmark
    public long WilkesWheelerGill() {
        long acc = 0;
        for (int i = 0; i < data.length; i++) {
            acc += popcntWWG(data[i]);
        }
        return acc;
    }

    /* ============================================================================================================== */

    // FIGURE 5. The Lauradoux population count in C for sets of 12 words.

    static long parallelPopcnt(long count1, long count2, long count3) {
        long m1  = 0x5555555555555555L;
        long m2  = 0x3333333333333333L;
        long m4  = 0x0F0F0F0F0F0F0F0FL;

        long half1 = (count3      ) & m1;
        long half2 = (count3 >>> 1) & m1;

        count1 -= (count1 >>> 1) & m1;
        count2 -= (count2 >>> 1) & m1;
        count1 += half1;
        count2 += half2;
        count1  = (count1 & m2) + (( count1 >>> 2) & m2);
        count1 += (count2 & m2) + (( count2 >>> 2) & m2);
        return (count1 & m4) + (( count1 >>> 4) & m4);
    }

    static long reduce(long acc) {
        long m8  = 0x00FF00FF00FF00FFL;
        long m16 = 0x0000FFFF0000FFFFL;
        long m32 = 0x00000000FFFFFFFFL;

        acc = (acc & m8) + (( acc >>> 8) & m8);
        acc = (acc + (acc >>> 16) ) & m16;
        acc = (acc & m32) + (acc >>> 32);
        return acc;
    }

    static long popcntLauradoux(long[] xs, int off) {
        long acc = 0;
        for (int j = off; j < off+12; j += 3) {
            acc += parallelPopcnt(xs[j+0], xs[j+1], xs[j+2]);
        }
        return reduce(acc);
    }

    @Benchmark
    public long Lauradoux() {
        long acc = 0;
        int upper = data.length - (data.length % 12);
        for (int i = 0; i < upper; i += 12) {
            acc += popcntLauradoux(data, i);
        }
        return acc + tail(upper);
    }

    /* ============================================================================================================== */

    // FIGURE 6. A C function implementing a bitwise parallel carry-save adder (CSA). Given three input words a, b, c, it
    // generates two new words h, l in which each bit represents the high and low bits in the bitwise sum of the bits from a,
    // b, and c.

    static long csaLow(long a, long b, long c) {
        long u = a ^ b;
        long lo = u ^ c;
        return lo;
    }

    static long csaHigh(long a, long b, long c) {
        long u = a ^ b;
        long hi = (a & b) | (u & c) ;
        return hi;
    }

    // FIGURE 8. A C function implementing the Harley-Seal
    // population count over an array of 64-bit words. The count
    // function could be the Wilkes-Wheeler-Gill function.
    @Benchmark
    public long HarleySeal() {
        long total = 0, ones = 0, twos = 0, fours = 0, eights = 0, sixteens = 0;
        long twosA   = 0, twosB   = 0;
        long foursA  = 0, foursB  = 0;
        long eightsA = 0, eightsB = 0;

        int step = 16;
        int upper = data.length - (data.length % step);
        for (int i = 0; i < upper; i += step) {
            // CSA(&twosA, &ones, ones, d[i+0], d[i +1]);
            twosA = csaHigh(ones, data[i+0], data[i+1]);
            ones  = csaLow(ones, data[i+0], data[i+1]);

            // CSA(&twosB, &ones, ones, d[i+2], d[i+3]);
            twosB = csaHigh(ones, data[i+2], data[i+3]);
            ones  = csaLow(ones, data[i+2], data[i+3]);

            // CSA(&foursA, &twos, twos, twosA, twosB);
            foursA = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // ====================================

            // CSA(&twosA, &ones, ones, d[i+4], d[i+5]);
            twosA = csaHigh(ones, data[i+4], data[i+5]);
            ones  = csaLow(ones, data[i+4], data[i+5]);

            // CSA(&twosB, &ones, ones, d[i+6], d[i+7]);
            twosB = csaHigh(ones, data[i+6], data[i+7]);
            ones  = csaLow(ones, data[i+6], data[i+7]);

            // CSA(&foursB, &twos, twos, twosA, twosB);
            foursB = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // ====================================

            // CSA(&eightsA, &fours, fours, foursA, foursB);
            eightsA = csaHigh(fours, foursA, foursB);
            fours   = csaLow(fours, foursA, foursB);

            // ====================================

            // CSA(&twosA, &ones, ones, d[i+8], d[i+9]);
            twosA = csaHigh(ones, data[i+8], data[i+9]);
            ones  = csaLow(ones, data[i+8], data[i+9]);

            // CSA(&twosB, &ones, ones, d[i+10],d[i+11]);
            twosB = csaHigh(ones, data[i+10], data[i+11]);
            ones  = csaLow(ones, data[i+10], data[i+11]);

            // CSA(&foursA, &twos, twos, twosA, twosB);
            foursA = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // ====================================

            // CSA(&twosA, &ones, ones, d[i+12], d[i +13]);
            twosA = csaHigh(ones, data[i+12], data[i+13]);
            ones  = csaLow(ones, data[i+12], data[i+13]);

            // CSA(&twosB, &ones, ones, d[i+14], d[i +15]);
            twosB = csaHigh(ones, data[i+14], data[i+15]);
            ones  = csaLow(ones, data[i+14], data[i+15]);

            // ====================================

            // CSA(&foursB, &twos, twos, twosA, twosB);
            foursB = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // CSA(&eightsB, &fours, fours, foursA, foursB);
            eightsB = csaHigh(fours, foursA, foursB);
            fours   = csaLow(fours, foursA, foursB);

            // ====================================

            // CSA(&sixteens, &eights, eights, eightsA, eightsB);
            sixteens = csaHigh(eights, eightsA, eightsB);
            eights   = csaLow(eights, eightsA, eightsB);

            total += Long.bitCount(sixteens);
        }
        total = 16 * total
                + 8 * Long.bitCount(eights)
                + 4 * Long.bitCount(fours)
                + 2 * Long.bitCount(twos)
                + 1 * Long.bitCount(ones);

        return total + tail(upper);
    }

    /* ============================================================================================================== */

    // FIGURE 9. A C function using SSE intrinsics implementing Mula’s algorithm to compute sixteen population counts,
    // corresponding to sixteen input bytes.

    static final ByteVector MULA128_LOOKUP = IntVector.fromArray(I128,
            new int[]{
                    0x02_01_01_00, // 0, 1, 1, 2,
                    0x03_02_02_01, // 1, 2, 2, 3,
                    0x03_02_02_01, // 1, 2, 2, 3,
                    0x04_03_03_02  // 2, 3, 3, 4
            },
            0
            ).reinterpretAsBytes();

    ByteVector popcntB128(ByteVector v) {
        var low_mask = ByteVector.broadcast(B128, (byte)0x0f);

        var lo = v          .and(low_mask);
        var hi = v.lanewise(VectorOperators.LSHR, 4).and(low_mask);

        var cnt1 = MULA128_LOOKUP.rearrange(lo.toShuffle());
        var cnt2 = MULA128_LOOKUP.rearrange(hi.toShuffle());

        return cnt1.add(cnt2);
    }

    @Benchmark
    public long Mula128() {
        var acc = LongVector.zero(L128); // IntVector
        int step = 32; // % B128.length() == 0!
        int upper = data.length - (data.length % step);
        for (int i = 0; i < upper; i += step) {
            var bacc = ByteVector.zero(B128);
            for (int j = 0; j < step; j += L128.length()) {
                var v1 = LongVector.fromArray(L128, data, i + j);
                var v2 = v1.reinterpretAsBytes();
                var v3 = popcntB128(v2);
                bacc = bacc.add(v3);
            }
            acc = acc.add(sumUnsignedBytes(bacc));
        }
        var r = acc.reduceLanes(VectorOperators.ADD) + tail(upper);
        return r;
    }

    /* ============================================================================================================== */

    // FIGURE 10. A C function using AVX2 intrinsics implementing Mula’s algorithm to compute the four population counts
    // of the four 64-bit words in a 256-bit vector. The 32 B output vector should be interpreted as four separate
    // 64-bit counts that need to be summed to obtain the final population count.

    static final ByteVector MULA256_LOOKUP = 
            join(I128, I256, MULA128_LOOKUP.reinterpretAsInts(), MULA128_LOOKUP.reinterpretAsInts()).reinterpretAsBytes();

    ByteVector popcntB256(ByteVector v) {
        var low_mask = ByteVector.broadcast(B256, (byte)0x0F);

        var lo = v          .and(low_mask);
        var hi = v.lanewise(VectorOperators.LSHR, 4).and(low_mask);

        var cnt1 = MULA256_LOOKUP.rearrange(lo.toShuffle());
        var cnt2 = MULA256_LOOKUP.rearrange(hi.toShuffle());
        var cnt = cnt1.add(cnt2);

        return cnt;
    }

    // Horizontally sum each consecutive 8 differences to produce four unsigned 16-bit integers,
    // and pack these unsigned 16-bit integers in the low 16 bits of 64-bit elements in dst:
    //   _mm256_sad_epu8(total, _mm256_setzero_si256())
    LongVector sumUnsignedBytes(ByteVector vb) {
        return sumUnsignedBytesShapes(vb);
//        return sumUnsignedBytesShifts(vb);
    }

    LongVector sumUnsignedBytesShapes(ByteVector vb) {
        VectorSpecies<Short> shortSpecies = VectorSpecies.of(short.class, vb.shape());
        VectorSpecies<Integer> intSpecies = VectorSpecies.of(int.class, vb.shape());
        VectorSpecies<Long> longSpecies = VectorSpecies.of(long.class, vb.shape());

        var low_short_mask = ShortVector.broadcast(shortSpecies, (short) 0xFF);
        var low_int_mask = IntVector.broadcast(intSpecies, 0xFFFF);
        var low_long_mask = LongVector.broadcast(longSpecies, 0xFFFFFFFFL);

        var vs = vb.reinterpretAsShorts(); // 16-bit
        var vs0 = vs.and(low_short_mask);
        var vs1 = vs.lanewise(VectorOperators.LSHR, 8).and(low_short_mask);
        var vs01 = vs0.add(vs1);

        var vi = vs01.reinterpretAsInts(); // 32-bit
        var vi0 = vi.and(low_int_mask);
        var vi1 = vi.lanewise(VectorOperators.LSHR, 16).and(low_int_mask);
        var vi01 = vi0.add(vi1);

        var vl = vi01.reinterpretAsLongs(); // 64-bit
        var vl0 = vl.and(low_long_mask);
        var vl1 = vl.lanewise(VectorOperators.LSHR, 32).and(low_long_mask);
        var vl01 = vl0.add(vl1);

        return vl01;
    }

    LongVector sumUnsignedBytesShifts(ByteVector vb) {
        VectorSpecies<Long> to = VectorSpecies.of(long.class, vb.shape());

        var low_mask = LongVector.broadcast(to, 0xFF);

        var vl = vb.reinterpretAsLongs();

        var v0 = vl           .and(low_mask); // 8-bit
        var v1 = vl.lanewise(VectorOperators.LSHR, 8).and(low_mask);  // 8-bit
        var v2 = vl.lanewise(VectorOperators.LSHR, 16).and(low_mask); // 8-bit
        var v3 = vl.lanewise(VectorOperators.LSHR, 24).and(low_mask); // 8-bit
        var v4 = vl.lanewise(VectorOperators.LSHR, 32).and(low_mask); // 8-bit
        var v5 = vl.lanewise(VectorOperators.LSHR, 40).and(low_mask); // 8-bit
        var v6 = vl.lanewise(VectorOperators.LSHR, 48).and(low_mask); // 8-bit
        var v7 = vl.lanewise(VectorOperators.LSHR, 56).and(low_mask); // 8-bit

        var v01 = v0.add(v1);
        var v23 = v2.add(v3);
        var v45 = v4.add(v5);
        var v67 = v6.add(v7);

        var v03 = v01.add(v23);
        var v47 = v45.add(v67);

        var sum = v03.add(v47); // 64-bit
        return sum;
    }

    @Benchmark
    public long Mula256() {
        var acc = LongVector.zero(L256);
        int step = 32; // % B256.length() == 0!
        int upper = data.length - (data.length % step);
        for (int i = 0; i < upper; i += step) {
            var bacc = ByteVector.zero(B256);
            for (int j = 0; j < step; j += L256.length()) {
                var v1 = LongVector.fromArray(L256, data, i + j);
                var v2 = popcntB256(v1.reinterpretAsBytes());
                bacc = bacc.add(v2);
            }
            acc = acc.add(sumUnsignedBytes(bacc));
        }
        return acc.reduceLanes(VectorOperators.ADD) + tail(upper);
    }


    /* ============================================================================================================== */

    // FIGURE 11. A C function using AVX2 intrinsics implementing a bitwise parallel carry-save adder (CSA).

    LongVector csaLow(LongVector a, LongVector b, LongVector c) {
        var u = a.lanewise(VectorOperators.XOR, b);
        var r = u.lanewise(VectorOperators.XOR, c);
        return r;
    }

    LongVector csaHigh(LongVector a, LongVector b, LongVector c) {
        var u  = a.lanewise(VectorOperators.XOR, b);
        var ab = a.and(b);
        var uc = u.and(c);
        var r  = ab.or(uc); // (a & b) | ((a ^ b) & c)
        return r;
    }

    LongVector popcntL256(LongVector v) {
        var vb1 = v.reinterpretAsBytes();
        var vb2 = popcntB256(vb1);
        return sumUnsignedBytes(vb2);
    }

    // FIGURE 12. A C function using AVX2 intrinsics implementing Harley-Seal’s algorithm. It assumes, for
    // simplicity, that the input size in 256-bit vectors is divisible by 16. See Fig. 10 for the count function.

    @Benchmark
    public long HarleySeal256() {
        LongVector ones, twos, fours, eights, sixteens, vtotal, twosA, twosB, foursA, foursB, eightsA, eightsB;
        ones = twos = fours = eights = sixteens = twosA = twosB = foursA = foursB = eightsA = eights = vtotal = LongVector.broadcast(L256, 0);

        var vlen = L256.length();
        int step = 16 * vlen;
        int upper = data.length - (data.length % step);
        for (int i = 0; i < upper; i += step) {
            // CSA(&twosA, &ones, ones, d[i+0], d[i +1]);
            var d0 = LongVector.fromArray(L256, data, i + 0 * vlen);
            var d1 = LongVector.fromArray(L256, data, i + 1 * vlen);

            twosA = csaHigh(ones, d0, d1);
            ones  = csaLow(ones, d0, d1);

            // CSA(&twosB, &ones, ones, d[i+2], d[i+3]);
            var d2 = LongVector.fromArray(L256, data, i + 2 * vlen);
            var d3 = LongVector.fromArray(L256, data, i + 3 * vlen);
            twosB = csaHigh(ones, d2, d3);
            ones  = csaLow(ones, d2, d3);

            // CSA(&foursA, &twos, twos, twosA, twosB);
            foursA = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // ====================================

            // CSA(&twosA, &ones, ones, d[i+4], d[i+5]);
            var d4 = LongVector.fromArray(L256, data, i + 4 * vlen);
            var d5 = LongVector.fromArray(L256, data, i + 5 * vlen);
            twosA = csaHigh(ones, d4, d5);
            ones  = csaLow(ones, d4, d5);

            // CSA(&twosB, &ones, ones, d[i+6], d[i+7]);
            var d6 = LongVector.fromArray(L256, data, i + 6 * vlen);
            var d7 = LongVector.fromArray(L256, data, i + 7 * vlen);
            twosB = csaHigh(ones, d6, d7);
            ones  = csaLow(ones, d6, d7);

            // CSA(&foursB, &twos, twos, twosA, twosB);
            foursB = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // ====================================

            // CSA(&eightsA, &fours, fours, foursA, foursB);
            eightsA = csaHigh(fours, foursA, foursB);
            fours   = csaLow(fours, foursA, foursB);

            // ====================================

            // CSA(&twosA, &ones, ones, d[i+8], d[i+9]);
            var d8 = LongVector.fromArray(L256, data, i + 8 * vlen);
            var d9 = LongVector.fromArray(L256, data, i + 9 * vlen);
            twosA = csaHigh(ones, d8, d9);
            ones  = csaLow(ones, d8, d9);

            // CSA(&twosB, &ones, ones, d[i+10],d[i+11]);
            var d10 = LongVector.fromArray(L256, data, i + 10 * vlen);
            var d11 = LongVector.fromArray(L256, data, i + 11 * vlen);
            twosB = csaHigh(ones, d10, d11);
            ones  = csaLow(ones, d10, d11);

            // CSA(&foursA, &twos, twos, twosA, twosB);
            foursA = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // ====================================

            // CSA(&twosA, &ones, ones, d[i+12], d[i +13]);
            var d12 = LongVector.fromArray(L256, data, i + 12 * vlen);
            var d13 = LongVector.fromArray(L256, data, i + 13 * vlen);
            twosA = csaHigh(ones, d12, d13);
            ones  = csaLow(ones, d12, d13);

            // CSA(&twosB, &ones, ones, d[i+14], d[i +15]);
            var d14 = LongVector.fromArray(L256, data, i + 14 * vlen);
            var d15 = LongVector.fromArray(L256, data, i + 15 * vlen);
            twosB = csaHigh(ones, d14, d15);
            ones  = csaLow(ones, d14, d15);

            // ====================================

            // CSA(&foursB, &twos, twos, twosA, twosB);
            foursB = csaHigh(twos, twosA, twosB);
            twos   = csaLow(twos, twosA, twosB);

            // CSA(&eightsB, &fours, fours, foursA, foursB);
            eightsB = csaHigh(fours, foursA, foursB);
            fours   = csaLow(fours, foursA, foursB);

            // ====================================

            // CSA(&sixteens, &eights, eights, eightsA, eightsB);
            sixteens = csaHigh(eights, eightsA, eightsB);
            eights   = csaLow(eights, eightsA, eightsB);

            vtotal = vtotal.add(popcntL256(sixteens));
        }

        vtotal = vtotal.mul(16);                       // << 4
        vtotal = vtotal.add(popcntL256(eights).mul(8)); // << 3
        vtotal = vtotal.add(popcntL256(fours).mul(4));  // << 2
        vtotal = vtotal.add(popcntL256(twos).mul(2));   // << 1
        vtotal = vtotal.add(popcntL256(ones));          // << 0

        var total = vtotal.reduceLanes(VectorOperators.ADD);

        return total + tail(upper);
    }

    /* ============================================================================================================== */

//    ByteVector csaLow512(ByteVector a, ByteVector b, ByteVector c) {
//        return _mm512_ternarylogic_epi32(c, b, a, 0x96); // vpternlogd
//    }
//
//    ByteVector csaLow512(ByteVector a, ByteVector b, ByteVector c) {
//        return _mm512_ternarylogic_epi32(c, b, a, 0xe8); // vpternlogd
//    }
}
