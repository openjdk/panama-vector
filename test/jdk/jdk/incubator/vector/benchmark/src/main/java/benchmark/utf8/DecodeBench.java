/*
 * Copyright (c) 2020 Microsoft Corporation. All rights reserved.
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

package benchmark.utf8;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import jdk.incubator.vector.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 8, time = 2)
public class DecodeBench {

    @Param({"32768", "8388608"})
    private int dataSize;

    @Param({"1", "2", "3", "4"})
    private int maxBytes;

    private ByteBuffer src;
    private CharBuffer dst;
    private String in;
    private String out;

    private static final VectorSpecies<Byte>  B128 = ByteVector.SPECIES_128;
    private static final VectorSpecies<Short> S128 = ShortVector.SPECIES_128;
    private static final VectorSpecies<Short> S256 = ShortVector.SPECIES_256;

    private static final HashMap<Long, DecoderLutEntry> lutTable = new HashMap<Long, DecoderLutEntry>();

    private static class DecoderLutEntry {
        public final VectorShuffle<Byte> shufAB;   // shuffling mask to get lower two bytes of symbols
        public final VectorShuffle<Byte> shufC;    // shuffling mask to get third bytes of symbols
        public final byte srcStep;                 // number of bytes processed in input buffer
        public final byte dstStep;                 // number of symbols produced in output buffer (doubled)
        public final Vector<Byte> headerMask;      // mask of "111..10" bits required in each byte
        public final Vector<Short> zeroBits;

        public DecoderLutEntry(VectorShuffle<Byte> _shufAB, VectorShuffle<Byte> _shufC,
                               byte _srcStep, byte _dstStep,
                               Vector<Byte> _headerMask, Vector<Short> _zeroBits) {
            shufAB = _shufAB;
            shufC = _shufC;
            srcStep = _srcStep;
            dstStep = _dstStep;
            headerMask = _headerMask;
            zeroBits = _zeroBits;
        }

        // @Override
        // public String toString() {
        //     return String.format("shufAB = %s, shufC = %s, srcStep = %d, dstStep = %d, headerMask = %s, zeroBits = %s",
        //         arrayToString(shufAB), arrayToString(shufC), srcStep, dstStep, arrayToString(headerMask), arrayToString(zeroBits));
        // }
    }

    @Setup(Level.Trial)
    public void setupLutTable() {
        int[] sizes = new int[32];
        computeLutRecursive(sizes, 0, 0); //10609 entries total

        // for (var entry : lutTable.entrySet()) {
        //     System.out.println("" + entry.getKey() + " -> " + entry.getValue());
        // }
    }

    static void computeLutRecursive(int[] sizes, int num, int total) {
        if (total >= 16) {
            computeLutEntry(sizes, num);
            return;
        }
        for (int size = 1; size <= 3; size++) {
            sizes[num] = size;
            computeLutRecursive(sizes, num + 1, total + size);
        }
    }

    static void computeLutEntry(int[] sizes, int num) {
        //find maximal number of chars to decode
        int cnt = num - 1;
        int preSum = 0;
        for (int i = 0; i < cnt; i++)
            preSum += sizes[i];
        assert preSum < 16;
        // Note: generally, we can process a char only if the next byte is within XMM register
        // However, if the last char takes 3 bytes and fits the register tightly, we can take it too
        if (preSum == 13 && preSum + sizes[cnt] == 16)
            preSum += sizes[cnt++];
        //still cannot process more that 8 chars per register
        while (cnt > 8)
            preSum -= sizes[--cnt];

        //generate bitmask
        long mask = 0;
        for (int i = 0, pos = 0; i < num; i++) {
            for (int j = 0; j < sizes[i]; j++, pos++) {
                // The first byte is not represented in the mask
                if (j > 0) {
                    mask |= 1 << pos;
                }
            }
        }
        assert mask <= 0xFFFF;

        //generate shuffle masks
        byte[] shufAB = new byte[16];
        byte[] shufC  = new byte[16];
        for (int i = 0; i < 16; i++)
            shufAB[i] = shufC[i] = (byte)0xFF;
        for (int i = 0, pos = 0; i < cnt; i++) {
            int sz = sizes[i];
            for (int j = sz-1; j >= 0; j--, pos++) {
                if (j < 2)
                    shufAB[2 * i + j] = (byte)pos;
                else
                    shufC[2 * i] = (byte)pos;
            }
        }

        //generate header masks for validation
        byte[] headerMask = new byte[16];
        for (int i = 0, pos = 0; i < cnt; i++) {
            int sz = sizes[i];
            for (int j = 0; j < sz; j++, pos++) {
                int bits;
                if      (j > 0)    bits = 2;
                else if (sz == 1)  bits = 1;
                else if (sz == 2)  bits = 3;
                else   /*sz == 3*/ bits = 4;
                headerMask[pos] = (byte)-(1 << (8 - bits));
            }
        }

        //generate min symbols values for validation
        short[] zeroBits = new short[8];
        for (int i = 0; i < 8; i++) {
            int sz = i < cnt ? sizes[i] : 1;
            if      (sz == 1)  zeroBits[i] = (short)(0xFF80);
            else if (sz == 2)  zeroBits[i] = (short)(0xF800);
            else   /*sz == 3*/ zeroBits[i] = (short)(0x0000);
        }

        //store info into the lookup table
        lutTable.put(mask, new DecoderLutEntry(ByteVector.fromArray(B128, shufAB, 0).toShuffle(),
                                               ByteVector.fromArray(B128, shufC, 0).toShuffle(),
                                               (byte)preSum, (byte)cnt,
                                               ByteVector.fromArray(B128, headerMask, 0),
                                               ShortVector.fromArray(S128, zeroBits, 0)));
    }

    @Setup(Level.Trial)
    public void setup() {
        in = randomString(dataSize, maxBytes);
        src = ByteBuffer.wrap(in.getBytes());
        dst = CharBuffer.allocate(in.length());
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        src.clear();
        dst.clear();
    }

    @TearDown(Level.Invocation)
    public void tearDownInvocation() {
        out = new String(dst.array());
        if (!in.equals(out)) {
            System.out.println("in  = (" + in.length() + ") \"" + arrayToString(in.getBytes()) + "\"");
            System.out.println("out = (" + out.length() + ") \"" + arrayToString(out.getBytes()) + "\"");
            throw new RuntimeException("Incorrect result");
        }
    }

    private static final Random RANDOM = new Random(0);
    private static int randomInt(int min /* inclusive */, int max /* inclusive */) {
        return RANDOM.nextInt(max - min + 1) + min;
    }
    private static String randomString(int dataSize, int maxBytes) {
        ByteBuffer buf = ByteBuffer.allocate(dataSize);
        for (int i = 0, size = randomInt(1, maxBytes); i + size - 1 < dataSize; i += size, size = randomInt(1, maxBytes)) {
            int b1, b2, b3, b4;
            switch (size) {
            case 1: {
                b1 = randomInt(0x00, 0x7F);
                buf.put(i + 0, (byte)((0b0     << (8 - 1)) | b1));
                break;
            }
            case 2: {
                b1 = randomInt(0xC2, 0xDF);
                b2 = randomInt(0x80, 0xBF);
                buf.put(i + 0, (byte)((0b110   << (8 - 3)) | b1));
                buf.put(i + 1, (byte)((0b10    << (8 - 2)) | b2));
                break;
            }
            case 3: {
                b1 = randomInt(0xE0, 0xEF);
                switch (b1) {
                case 0xE0:
                    b2 = randomInt(0xA0, 0xBF);
                    b3 = randomInt(0x80, 0xBF);
                    break;
                default:
                    b2 = randomInt(0x80, 0xBF);
                    b3 = randomInt(0x80, 0xBF);
                    break;
                }
                buf.put(i + 0, (byte)((0b1110  << (8 - 4)) | b1));
                buf.put(i + 1, (byte)((0b10    << (8 - 2)) | b2));
                buf.put(i + 2, (byte)((0b10    << (8 - 2)) | b3));
                break;
            }
            case 4: {
                b1 = randomInt(0xF0, 0xF4);
                switch (b1) {
                case 0xF0:
                    b2 = randomInt(0x90, 0xBF);
                    b3 = randomInt(0x80, 0xBF);
                    b4 = randomInt(0x80, 0xBF);
                    break;
                case 0xF4:
                    b2 = randomInt(0x80, 0x8F);
                    b3 = randomInt(0x80, 0xBF);
                    b4 = randomInt(0x80, 0xBF);
                    break;
                default:
                    b2 = randomInt(0x80, 0xBF);
                    b3 = randomInt(0x80, 0xBF);
                    b4 = randomInt(0x80, 0xBF);
                    break;
                }
                buf.put(i + 0, (byte)((0b11110 << (8 - 5)) | b1));
                buf.put(i + 1, (byte)((0b10    << (8 - 2)) | b2));
                buf.put(i + 2, (byte)((0b10    << (8 - 2)) | b3));
                buf.put(i + 3, (byte)((0b10    << (8 - 2)) | b4));
                break;
            }
            default:
                throw new RuntimeException("not supported");
            }
        }
        return new String(buf.array(), Charset.forName("UTF-8"));
    }

    private static String arrayToString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) sb.append(",");
            sb.append(String.format("%x", (byte)array[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    @Benchmark
    public void decodeScalar() {
        decodeArrayLoop(src, dst);
    }

    @Benchmark
    public void decodeVector() {
        decodeArrayVectorized(src, dst);
        decodeArrayLoop(src, dst);
    }

    @Benchmark
    public void decodeVectorASCII() {
        decodeArrayVectorizedASCII(src, dst);
        decodeArrayLoop(src, dst);
    }

    private static void decodeArrayVectorized(ByteBuffer src, CharBuffer dst) {
        // Algorithm is largely inspired from https://dirtyhandscoding.github.io/posts/utf8lut-vectorized-utf-8-converter-introduction.html

        byte[] sa = src.array();
        int sp = src.arrayOffset() + src.position();
        int sl = src.arrayOffset() + src.limit();

        char[] da = dst.array();
        int dp = dst.arrayOffset() + dst.position();
        int dl = dst.arrayOffset() + dst.limit();

        // Vectorized loop
        while (sp + B128.length() < sl && dp + S128.length() < dl) {
            var bytes = ByteVector.fromArray(B128, sa, sp);

            /* Decode */

            var continuationByteMask = bytes.lanewise(VectorOperators.AND, (byte)0xC0).compare(VectorOperators.EQ, (byte)0x80);
            final DecoderLutEntry lookup = lutTable.get(continuationByteMask.toLong());
            if (lookup == null) {
                break;
            }
            // Shuffle the 1st and 2nd bytes
            var Rab = bytes.rearrange(lookup.shufAB, lookup.shufAB.toVector().compare(VectorOperators.NE, -1)).reinterpretAsShorts();
            // Shuffle the 3rd byte
            var Rc  = bytes.rearrange(lookup.shufC, lookup.shufC.toVector().compare(VectorOperators.NE, -1)).reinterpretAsShorts();
            // Extract the bits from each byte
            var sum = Rab.lanewise(VectorOperators.AND, (short)0x007F)
                 .add(Rab.lanewise(VectorOperators.AND, (short)0x3F00).lanewise(VectorOperators.LSHR, 2))
                 .add(Rc.lanewise(VectorOperators.LSHL, 12));

            /* Validate */

            var zeroBits = lookup.zeroBits;
            if (sum.lanewise(VectorOperators.AND, zeroBits).compare(VectorOperators.NE, 0).anyTrue()) {
                break;
            }
            // Check for surrogate code point
            if (sum.lanewise(VectorOperators.SUB, (short)0x6000).compare(VectorOperators.GT, 0x77FF).anyTrue()) {
                break;
            }
            var headerMask = lookup.headerMask;
            if (bytes.lanewise(VectorOperators.AND, headerMask).compare(VectorOperators.NE, headerMask.lanewise(VectorOperators.LSHL, 1)).anyTrue()) {
                break;
            }

            /* Advance */

            ((ShortVector)sum).intoCharArray(da, dp);
            sp += lookup.srcStep;
            dp += lookup.dstStep;
        }

        updatePositions(src, sp, dst, dp);
    }

    private static void decodeArrayVectorizedASCII(ByteBuffer src, CharBuffer dst) {
        byte[] sa = src.array();
        int sp = src.arrayOffset() + src.position();
        int sl = src.arrayOffset() + src.limit();

        char[] da = dst.array();
        int dp = dst.arrayOffset() + dst.position();
        int dl = dst.arrayOffset() + dst.limit();

        // Vectorized loop
        for (; sp <= sl - B128.length() && dp <= dl - S256.length(); sp += B128.length(), dp += S256.length()) {
            var bytes = ByteVector.fromArray(B128, sa, sp);

            if (bytes.compare(VectorOperators.LT, (byte) 0x00).anyTrue())
                break;

            ((ShortVector) bytes.convertShape(VectorOperators.B2S, S256, 0)).intoCharArray(da, dp);
        }

        updatePositions(src, sp, dst, dp);
    }

    private static CoderResult decodeArrayLoop(ByteBuffer src, CharBuffer dst) {
        // This method is optimized for ASCII input.
        byte[] sa = src.array();
        int sp = src.arrayOffset() + src.position();
        int sl = src.arrayOffset() + src.limit();

        char[] da = dst.array();
        int dp = dst.arrayOffset() + dst.position();
        int dl = dst.arrayOffset() + dst.limit();
        int dlASCII = dp + Math.min(sl - sp, dl - dp);

        // ASCII only loop
        while (dp < dlASCII && sa[sp] >= 0)
            da[dp++] = (char) sa[sp++];
        while (sp < sl) {
            int b1 = sa[sp];
            if (b1 >= 0) {
                // 1 byte, 7 bits: 0xxxxxxx
                if (dp >= dl)
                    return xflow(src, sp, sl, dst, dp, 1);
                da[dp++] = (char) b1;
                sp++;
            } else if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0) {
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                //                   [C2..DF] [80..BF]
                if (sl - sp < 2 || dp >= dl)
                    return xflow(src, sp, sl, dst, dp, 2);
                int b2 = sa[sp + 1];
                // Now we check the first byte of 2-byte sequence as
                //     if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0)
                // no longer need to check b1 against c1 & c0 for
                // malformed as we did in previous version
                //   (b1 & 0x1e) == 0x0 || (b2 & 0xc0) != 0x80;
                // only need to check the second byte b2.
                if (isNotContinuation(b2))
                    return malformedForLength(src, sp, dst, dp, 1);
                da[dp++] = (char) (((b1 << 6) ^ b2)
                                   ^
                                   (((byte) 0xC0 << 6) ^
                                    ((byte) 0x80 << 0)));
                sp += 2;
            } else if ((b1 >> 4) == -2) {
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                int srcRemaining = sl - sp;
                if (srcRemaining < 3 || dp >= dl) {
                    if (srcRemaining > 1 && isMalformed3_2(b1, sa[sp + 1]))
                        return malformedForLength(src, sp, dst, dp, 1);
                    return xflow(src, sp, sl, dst, dp, 3);
                }
                int b2 = sa[sp + 1];
                int b3 = sa[sp + 2];
                if (isMalformed3(b1, b2, b3))
                    return malformed(src, sp, dst, dp, 3);
                char c = (char)
                    ((b1 << 12) ^
                     (b2 <<  6) ^
                     (b3 ^
                      (((byte) 0xE0 << 12) ^
                       ((byte) 0x80 <<  6) ^
                       ((byte) 0x80 <<  0))));
                if (Character.isSurrogate(c))
                    return malformedForLength(src, sp, dst, dp, 3);
                da[dp++] = c;
                sp += 3;
            } else if ((b1 >> 3) == -2) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                int srcRemaining = sl - sp;
                if (srcRemaining < 4 || dl - dp < 2) {
                    b1 &= 0xff;
                    if (b1 > 0xf4 ||
                        srcRemaining > 1 && isMalformed4_2(b1, sa[sp + 1] & 0xff))
                        return malformedForLength(src, sp, dst, dp, 1);
                    if (srcRemaining > 2 && isMalformed4_3(sa[sp + 2]))
                        return malformedForLength(src, sp, dst, dp, 2);
                    return xflow(src, sp, sl, dst, dp, 4);
                }
                int b2 = sa[sp + 1];
                int b3 = sa[sp + 2];
                int b4 = sa[sp + 3];
                int uc = ((b1 << 18) ^
                          (b2 << 12) ^
                          (b3 <<  6) ^
                          (b4 ^
                           (((byte) 0xF0 << 18) ^
                            ((byte) 0x80 << 12) ^
                            ((byte) 0x80 <<  6) ^
                            ((byte) 0x80 <<  0))));
                if (isMalformed4(b2, b3, b4) ||
                    // shortest form check
                    !Character.isSupplementaryCodePoint(uc)) {
                    return malformed(src, sp, dst, dp, 4);
                }
                da[dp++] = Character.highSurrogate(uc);
                da[dp++] = Character.lowSurrogate(uc);
                sp += 4;
            } else
                return malformed(src, sp, dst, dp, 1);
        }
        return xflow(src, sp, sl, dst, dp, 0);
    }

    private static CoderResult xflow(Buffer src, int sp, int sl,
                                     Buffer dst, int dp, int nb) {
        updatePositions(src, sp, dst, dp);
        return (nb == 0 || sl - sp < nb)
               ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
    }

    private static CoderResult malformedForLength(ByteBuffer src,
                                                  int sp,
                                                  CharBuffer dst,
                                                  int dp,
                                                  int malformedNB)
    {
        updatePositions(src, sp, dst, dp);
        return CoderResult.malformedForLength(malformedNB);
    }

    private static CoderResult malformed(ByteBuffer src, int sp,
                                         CharBuffer dst, int dp,
                                         int nb)
    {
        src.position(sp - src.arrayOffset());
        CoderResult cr = malformedN(src, sp, nb);
        updatePositions(src, sp, dst, dp);
        return cr;
    }

    private static CoderResult malformedN(ByteBuffer src, int sp,
                                          int nb) {
        switch (nb) {
        case 1:
        case 2:                    // always 1
            return CoderResult.malformedForLength(1);
        case 3:
            int b1 = src.get();
            int b2 = src.get();    // no need to lookup b3
            return CoderResult.malformedForLength(
                ((b1 == (byte)0xe0 && (b2 & 0xe0) == 0x80) ||
                 isNotContinuation(b2)) ? 1 : 2);
        case 4:  // we don't care the speed here
            b1 = src.get() & 0xff;
            b2 = src.get() & 0xff;
            if (b1 > 0xf4 ||
                (b1 == 0xf0 && (b2 < 0x90 || b2 > 0xbf)) ||
                (b1 == 0xf4 && (b2 & 0xf0) != 0x80) ||
                isNotContinuation(b2))
                return CoderResult.malformedForLength(1);
            if (isNotContinuation(src.get()))
                return CoderResult.malformedForLength(2);
            return CoderResult.malformedForLength(3);
        default:
            assert false;
            return null;
        }
    }

    private static boolean isNotContinuation(int b) {
        return (b & 0xc0) != 0x80;
    }

    //  [E0]     [A0..BF] [80..BF]
    //  [E1..EF] [80..BF] [80..BF]
    private static boolean isMalformed3(int b1, int b2, int b3) {
        return (b1 == (byte)0xe0 && (b2 & 0xe0) == 0x80) ||
               (b2 & 0xc0) != 0x80 || (b3 & 0xc0) != 0x80;
    }

    // only used when there is only one byte left in src buffer
    private static boolean isMalformed3_2(int b1, int b2) {
        return (b1 == (byte)0xe0 && (b2 & 0xe0) == 0x80) ||
               (b2 & 0xc0) != 0x80;
    }

    //  [F0]     [90..BF] [80..BF] [80..BF]
    //  [F1..F3] [80..BF] [80..BF] [80..BF]
    //  [F4]     [80..8F] [80..BF] [80..BF]
    //  only check 80-be range here, the [0xf0,0x80...] and [0xf4,0x90-...]
    //  will be checked by Character.isSupplementaryCodePoint(uc)
    private static boolean isMalformed4(int b2, int b3, int b4) {
        return (b2 & 0xc0) != 0x80 || (b3 & 0xc0) != 0x80 ||
               (b4 & 0xc0) != 0x80;
    }

    // only used when there is less than 4 bytes left in src buffer.
    // both b1 and b2 should be "& 0xff" before passed in.
    private static boolean isMalformed4_2(int b1, int b2) {
        return (b1 == 0xf0 && (b2  < 0x90 || b2 > 0xbf)) ||
               (b1 == 0xf4 && (b2 & 0xf0) != 0x80) ||
               (b2 & 0xc0) != 0x80;
    }

    // tests if b1 and b2 are malformed as the first 2 bytes of a
    // legal`4-byte utf-8 byte sequence.
    // only used when there is less than 4 bytes left in src buffer,
    // after isMalformed4_2 has been invoked.
    private static boolean isMalformed4_3(int b3) {
        return (b3 & 0xc0) != 0x80;
    }

    private static void updatePositions(Buffer src, int sp,
                                        Buffer dst, int dp) {
        src.position(sp - src.arrayOffset());
        dst.position(dp - dst.arrayOffset());
    }
}
