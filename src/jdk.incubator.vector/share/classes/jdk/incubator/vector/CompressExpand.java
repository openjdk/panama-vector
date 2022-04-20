/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
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

package jdk.incubator.vector;

import jdk.internal.vm.annotation.ForceInline;

class CompressExpand {

    /**
     * Returns the value obtained by compressing the bits of the
     * specified {@code int} value, {@code i}, in accordance with
     * the specified bit mask.
     * <p>
     * For each one-bit value {@code mb} of the mask, from least
     * significant to most significant, the bit value of {@code i} at
     * the same bit location as {@code mb} is assigned to the compressed
     * value contiguously starting from the least significant bit location.
     * All the upper remaining bits of the compressed value are set
     * to zero.
     *
     * @apiNote
     * Consider the simple case of compressing the digits of a hexadecimal
     * value:
     * {@snippet lang="java" :
     * // Compressing drink to food
     * compress(0xCAFEBABE, 0xFF00FFF0) == 0xCABAB
     * }
     * Starting from the least significant hexadecimal digit at position 0
     * from the right, the mask {@code 0xFF00FFF0} selects hexadecimal digits
     * at positions 1, 2, 3, 6 and 7 of {@code 0xCAFEBABE}. The selected digits
     * occur in the resulting compressed value contiguously from digit position
     * 0 in the same order.
     * <p>
     * The following identities all return {@code true} and are helpful to
     * understand the behaviour of {@code compress}:
     * {@snippet lang="java" :
     * // Returns 1 if the bit at position n is one
     * compress(x, 1 << n) == (x >> n & 1)
     *
     * // Logical shift right
     * compress(x, -1 << n) == x >>> n
     *
     * // Any bits not covered by the mask are ignored
     * compress(x, m) == compress(x & m, m)
     *
     * // Compressing a value by itself
     * compress(m, m) == (m == -1 || m == 0) ? m : (1 << bitCount(m)) - 1
     *
     * // Expanding then compressing with the same mask
     * compress(expand(x, m), m) == x & compress(m, m)
     * }
     * <p>
     * The Sheep And Goats (SAG) operation (see Hacker's Delight, section 7.7)
     * can be implemented as follows:
     * {@snippet lang="java" :
     * int compressLeft(int i, int mask) {
     *     // This implementation follows the description in Hacker's Delight which
     *     // is informative. A more optimal implementation is:
     *     //   Integer.compress(i, mask) << -Integer.bitCount(mask)
     *     return Integer.reverse(
     *         Integer.compress(Integer.reverse(i), Integer.reverse(mask)));
     * }
     *
     * int sag(int i, int mask) {
     *     return compressLeft(i, mask) | Integer.compress(i, ~mask);
     * }
     *
     * // Separate the sheep from the goats
     * sag(0xCAFEBABE, 0xFF00FFF0) == 0xCABABFEE
     * }
     *
     * @param i the value whose bits are to be compressed
     * @param mask the bit mask
     * @return the compressed value
     * @see #expand
     * @since 19
     */
    static int compress(int i, int mask) {
        // See Hacker's Delight (2nd ed) section 7.4 Compress, or Generalized Extract

        i = i & mask; // Clear irrelevant bits
        int maskCount = ~mask << 1; // Count 0's to right

        for (int j = 0; j < 5; j++) {
            // Parallel prefix
            // Mask prefix identifies bits of the mask that have an odd number of 0's to the right
            int maskPrefix = parallelSuffix(maskCount);
            // Bits to move
            int maskMove = maskPrefix & mask;
            // Compress mask
            mask = (mask ^ maskMove) | (maskMove >>> (1 << j));
            // Bits of i to be moved
            int t = i & maskMove;
            // Compress i
            i = (i ^ t) | (t >>> (1 << j));
            // Adjust the mask count by identifying bits that have 0 to the right
            maskCount = maskCount & ~maskPrefix;
        }
        return i;
    }

    /**
     * Returns the value obtained by compressing the bits of the
     * specified {@code long} value, {@code i}, in accordance with
     * the specified bit mask.
     * <p>
     * For each one-bit value {@code mb} of the mask, from least
     * significant to most significant, the bit value of {@code i} at
     * the same bit location as {@code mb} is assigned to the compressed
     * value contiguously starting from the least significant bit location.
     * All the upper remaining bits of the compressed value are set
     * to zero.
     *
     * @apiNote
     * Consider the simple case of compressing the digits of a hexadecimal
     * value:
     * {@snippet lang="java" :
     * // Compressing drink to food
     * compress(0xCAFEBABE, 0xFF00FFF0) == 0xCABAB
     * }
     * Starting from the least significant hexadecimal digit at position 0
     * from the right, the mask {@code 0xFF00FFF0} selects hexadecimal digits
     * at positions 1, 2, 3, 6 and 7 of {@code 0xCAFEBABE}. The selected digits
     * occur in the resulting compressed value contiguously from digit position
     * 0 in the same order.
     * <p>
     * The following identities all return {@code true} and are helpful to
     * understand the behaviour of {@code compress}:
     * {@snippet lang="java" :
     * // Returns 1 if the bit at position n is one
     * compress(x, 1 << n) == (x >> n & 1)
     *
     * // Logical shift right
     * compress(x, -1 << n) == x >>> n
     *
     * // Any bits not covered by the mask are ignored
     * compress(x, m) == compress(x & m, m)
     *
     * // Compressing a value by itself
     * compress(m, m) == (m == -1 || m == 0) ? m : (1 << bitCount(m)) - 1
     *
     * // Expanding then compressing with the same mask
     * compress(expand(x, m), m) == x & compress(m, m)
     * }
     * <p>
     * The Sheep And Goats (SAG) operation (see Hacker's Delight, section 7.7)
     * can be implemented as follows:
     * {@snippet lang="java" :
     * long compressLeft(long i, long mask) {
     *     // This implementation follows the description in Hacker's Delight which
     *     // is informative. A more optimal implementation is:
     *     //   Long.compress(i, mask) << -Long.bitCount(mask)
     *     return Long.reverse(
     *         Long.compress(Long.reverse(i), Long.reverse(mask)));
     * }
     *
     * long sag(long i, long mask) {
     *     return compressLeft(i, mask) | Long.compress(i, ~mask);
     * }
     *
     * // Separate the sheep from the goats
     * sag(0xCAFEBABE, 0xFF00FFF0) == 0xCABABFEE
     * }
     *
     * @param i the value whose bits are to be compressed
     * @param mask the bit mask
     * @return the compressed value
     * @see #expand
     * @since 19
     */
    static long compress(long i, long mask) {
        // See Hacker's Delight (2nd ed) section 7.4 Compress, or Generalized Extract

        i = i & mask; // Clear irrelevant bits
        long maskCount = ~mask << 1; // Count 0's to right

        for (int j = 0; j < 6; j++) {
            // Parallel prefix
            // Mask prefix identifies bits of the mask that have an odd number of 0's to the right
            long maskPrefix = parallelSuffix(maskCount);
            // Bits to move
            long maskMove = maskPrefix & mask;
            // Compress mask
            mask = (mask ^ maskMove) | (maskMove >>> (1 << j));
            // Bits of i to be moved
            long t = i & maskMove;
            // Compress i
            i = (i ^ t) | (t >>> (1 << j));
            // Adjust the mask count by identifying bits that have 0 to the right
            maskCount = maskCount & ~maskPrefix;
        }
        return i;
    }


    /**
     * Returns the value obtained by expanding the bits of the
     * specified {@code int} value, {@code i}, in accordance with
     * the specified bit mask.
     * <p>
     * For each one-bit value {@code mb} of the mask, from least
     * significant to most significant, the next contiguous bit value
     * of {@code i} starting at the least significant bit is assigned
     * to the expanded value at the same bit location as {@code mb}.
     * All other remaining bits of the expanded value are set to zero.
     *
     * @apiNote
     * Consider the simple case of expanding the digits of a hexadecimal
     * value:
     * {@snippet lang="java" :
     * expand(0x0000CABAB, 0xFF00FFF0) == 0xCA00BAB0
     * }
     * Starting from the least significant hexadecimal digit at position 0
     * from the right, the mask {@code 0xFF00FFF0} selects the first five
     * hexadecimal digits of {@code 0x0000CABAB}. The selected digits occur
     * in the resulting expanded value in order at positions 1, 2, 3, 6, and 7.
     * <p>
     * The following identities all return {@code true} and are helpful to
     * understand the behaviour of {@code expand}:
     * {@snippet lang="java" :
     * // Logically shift right the bit at position 0
     * expand(x, 1 << n) == (x & 1) << n
     *
     * // Logically shift right
     * expand(x, -1 << n) == x << n
     *
     * // Expanding all bits returns the mask
     * expand(-1, m) == m
     *
     * // Any bits not covered by the mask are ignored
     * expand(x, m) == expand(x, m) & m
     *
     * // Compressing then expanding with the same mask
     * expand(compress(x, m), m) == x & m
     * }
     * <p>
     * The select operation for determining the position of the one-bit with
     * index {@code n} in a {@code int} value can be implemented as follows:
     * {@snippet lang="java" :
     * int select(int i, int n) {
     *     // the one-bit in i (the mask) with index n
     *     int nthBit = Integer.expand(1 << n, i);
     *     // the bit position of the one-bit with index n
     *     return Integer.numberOfTrailingZeros(nthBit);
     * }
     *
     * // The one-bit with index 0 is at bit position 1
     * select(0b10101010_10101010, 0) == 1
     * // The one-bit with index 3 is at bit position 7
     * select(0b10101010_10101010, 3) == 7
     * }
     *
     * @param i the value whose bits are to be expanded
     * @param mask the bit mask
     * @return the expanded value
     * @see #compress
     * @since 19
     */
    static int expand(int i, int mask) {
        // Save original mask
        int originalMask = mask;
        // Count 0's to right
        int maskCount = ~mask << 1;
        int maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        int maskMove1 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove1) | (maskMove1 >>> (1 << 0));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        int maskMove2 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove2) | (maskMove2 >>> (1 << 1));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        int maskMove3 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove3) | (maskMove3 >>> (1 << 2));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        int maskMove4 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove4) | (maskMove4 >>> (1 << 3));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        int maskMove5 = maskPrefix & mask;

        int t = i << (1 << 4);
        i = (i & ~maskMove5) | (t & maskMove5);
        t = i << (1 << 3);
        i = (i & ~maskMove4) | (t & maskMove4);
        t = i << (1 << 2);
        i = (i & ~maskMove3) | (t & maskMove3);
        t = i << (1 << 1);
        i = (i & ~maskMove2) | (t & maskMove2);
        t = i << (1 << 0);
        i = (i & ~maskMove1) | (t & maskMove1);

        // Clear irrelevant bits
        return i & originalMask;
    }

    /**
     * Returns the value obtained by expanding the bits of the
     * specified {@code long} value, {@code i}, in accordance with
     * the specified bit mask.
     * <p>
     * For each one-bit value {@code mb} of the mask, from least
     * significant to most significant, the next contiguous bit value
     * of {@code i} starting at the least significant bit is assigned
     * to the expanded value at the same bit location as {@code mb}.
     * All other remaining bits of the expanded value are set to zero.
     *
     * @apiNote
     * Consider the simple case of expanding the digits of a hexadecimal
     * value:
     * {@snippet lang="java" :
     * expand(0x0000CABAB, 0xFF00FFF0) == 0xCA00BAB0
     * }
     * Starting from the least significant hexadecimal digit at position 0
     * from the right, the mask {@code 0xFF00FFF0} selects the first five
     * hexadecimal digits of {@code 0x0000CABAB}. The selected digits occur
     * in the resulting expanded value in order at positions 1, 2, 3, 6, and 7.
     * <p>
     * The following identities all return {@code true} and are helpful to
     * understand the behaviour of {@code expand}:
     * {@snippet lang="java" :
     * // Logically shift right the bit at position 0
     * expand(x, 1 << n) == (x & 1) << n
     *
     * // Logically shift right
     * expand(x, -1 << n) == x << n
     *
     * // Expanding all bits returns the mask
     * expand(-1, m) == m
     *
     * // Any bits not covered by the mask are ignored
     * expand(x, m) == expand(x, m) & m
     *
     * // Compressing then expanding with the same mask
     * expand(compress(x, m), m) == x & m
     * }
     * <p>
     * The select operation for determining the position of the one-bit with
     * index {@code n} in a {@code long} value can be implemented as follows:
     * {@snippet lang="java" :
     * long select(long i, long n) {
     *     // the one-bit in i (the mask) with index n
     * The following identities all return {@code true} and are helpful to
     * understand the behaviour of {@code expand}:
     * {@snippet lang="java" :
     * // Logically shift right the bit at position 0
     * expand(x, 1 << n) == (x & 1) << n
     *
     * // Logically shift right
     * expand(x, -1 << n) == x << n
     *
     * // Expanding all bits returns the mask
     * expand(-1, m) == m
     *
     * // Any bits not covered by the mask are ignored
     * expand(x, m) == expand(x, m) & m
     *
     * // Compressing then expanding with the same mask
     * expand(compress(x, m), m) == x & m
     * }
     * <p>
     * The select operation for determining the position of the one-bit with
     * index {@code n} in a {@code long} value can be implemented as follows:
     * {@snippet lang="java" :
     * long select(long i, long n) {
     *     // the one-bit in i (the mask) with index n
     *     long nthBit = Long.expand(1 << n, i);
     *     // the bit position of the one-bit with index n
     *     return Long.numberOfTrailingZeros(nthBit);
     * }
     *
     * // The one-bit with index 0 is at bit position 1
     * select(0b10101010_10101010, 0) == 1
     * // The one-bit with index 3 is at bit position 7
     * select(0b10101010_10101010, 3) == 7
     * }
     *
     * @param i the value whose bits are to be expanded
     * @param mask the bit mask
     * @return the expanded value
     * @see #compress
     * @since 19
     */
    static long expand(long i, long mask) {
        // Save original mask
        long originalMask = mask;
        // Count 0's to right
        long maskCount = ~mask << 1;
        long maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        long maskMove1 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove1) | (maskMove1 >>> (1 << 0));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        long maskMove2 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove2) | (maskMove2 >>> (1 << 1));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        long maskMove3 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove3) | (maskMove3 >>> (1 << 2));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        long maskMove4 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove4) | (maskMove4 >>> (1 << 3));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        long maskMove5 = maskPrefix & mask;
        // Compress mask
        mask = (mask ^ maskMove5) | (maskMove5 >>> (1 << 4));
        maskCount = maskCount & ~maskPrefix;

        maskPrefix = parallelSuffix(maskCount);
        // Bits to move
        long maskMove6 = maskPrefix & mask;
        long t = i << (1 << 5);
        i = (i & ~maskMove6) | (t & maskMove6);
        t = i << (1 << 4);
        i = (i & ~maskMove5) | (t & maskMove5);
        t = i << (1 << 3);
        i = (i & ~maskMove4) | (t & maskMove4);
        t = i << (1 << 2);
        i = (i & ~maskMove3) | (t & maskMove3);
        t = i << (1 << 1);
        i = (i & ~maskMove2) | (t & maskMove2);
        t = i << (1 << 0);
        i = (i & ~maskMove1) | (t & maskMove1);

        // Clear irrelevant bits
        return i & originalMask;
    }

    @ForceInline
    private static int parallelSuffix(int maskCount) {
        int maskPrefix = maskCount ^ (maskCount << 1);
        maskPrefix = maskPrefix ^ (maskPrefix << 2);
        maskPrefix = maskPrefix ^ (maskPrefix << 4);
        maskPrefix = maskPrefix ^ (maskPrefix << 8);
        maskPrefix = maskPrefix ^ (maskPrefix << 16);
        return maskPrefix;
    }

    @ForceInline
    private static long parallelSuffix(long maskCount) {
        long maskPrefix = maskCount ^ (maskCount << 1);
        maskPrefix = maskPrefix ^ (maskPrefix << 2);
        maskPrefix = maskPrefix ^ (maskPrefix << 4);
        maskPrefix = maskPrefix ^ (maskPrefix << 8);
        maskPrefix = maskPrefix ^ (maskPrefix << 16);
        maskPrefix = maskPrefix ^ (maskPrefix << 32);
        return maskPrefix;
    }

}
