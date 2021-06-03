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
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package benchmark.jdk.incubator.vector;

// -- This file was mechanically generated: Do not edit! -- //

import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class ShortScalar extends AbstractVectorBenchmark {
    static final int INVOC_COUNT = 1; // To align with vector benchmarks.

    @Param("1024")
    int size;

    short[] fill(IntFunction<Short> f) {
        short[] array = new short[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = f.apply(i);
        }
        return array;
    }

    static short bits(short e) {
        return e;
    }

    short[] as, bs, cs, rs;
    boolean[] ms, mt, rms;
    int[] ss;

    @Setup
    public void init() {
        as = fill(i -> (short)(2*i));
        bs = fill(i -> (short)(i+1));
        cs = fill(i -> (short)(i+5));
        rs = fill(i -> (short)0);
        ms = fillMask(size, i -> (i % 2) == 0);
        mt = fillMask(size, i -> true);
        rms = fillMask(size, i -> false);

        ss = fillInt(size, i -> RANDOM.nextInt(Math.max(i,1)));
    }

    final IntFunction<short[]> fa = vl -> as;
    final IntFunction<short[]> fb = vl -> bs;
    final IntFunction<short[]> fc = vl -> cs;
    final IntFunction<short[]> fr = vl -> rs;
    final IntFunction<boolean[]> fm = vl -> ms;
    final IntFunction<boolean[]> fmt = vl -> mt;
    final IntFunction<boolean[]> fmr = vl -> rms;
    final IntFunction<int[]> fs = vl -> ss;

    static boolean eq(short a, short b) {
        return a == b;
    }

    static boolean neq(short a, short b) {
        return a != b;
    }

    static boolean lt(short a, short b) {
        return a < b;
    }

    static boolean le(short a, short b) {
        return a <= b;
    }

    static boolean gt(short a, short b) {
        return a > b;
    }

    static boolean ge(short a, short b) {
        return a >= b;
    }

    static boolean ult(short a, short b) {
        return Short.compareUnsigned(a, b) < 0;
    }

    static boolean ule(short a, short b) {
        return Short.compareUnsigned(a, b) <= 0;
    }

    static boolean ugt(short a, short b) {
        return Short.compareUnsigned(a, b) > 0;
    }

    static boolean uge(short a, short b) {
        return Short.compareUnsigned(a, b) >= 0;
    }

    @Benchmark
    public void ADD(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(a + b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void ADDMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(a + b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }

    @Benchmark
    public void SUB(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(a - b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void SUBMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(a - b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }

    @Benchmark
    public void MUL(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(a * b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void MULMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(a * b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }



    @Benchmark
    public void FIRST_NONZERO(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)((a)!=0?a:b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void FIRST_NONZEROMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)((a)!=0?a:b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }


    @Benchmark
    public void AND(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(a & b);
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ANDMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(a & b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }



    @Benchmark
    public void AND_NOT(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(a & ~b);
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void AND_NOTMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(a & ~b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }



    @Benchmark
    public void OR(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(a | b);
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ORMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(a | b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }



    @Benchmark
    public void XOR(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(a ^ b);
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void XORMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(a ^ b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }







    @Benchmark
    public void LSHL(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)((a << (b & 0xF)));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void LSHLMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)((a << (b & 0xF)));
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }







    @Benchmark
    public void ASHR(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)((a >> (b & 0xF)));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ASHRMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)((a >> (b & 0xF)));
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }







    @Benchmark
    public void LSHR(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(((a & 0xFFFF) >>> (b & 0xF)));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void LSHRMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(((a & 0xFFFF) >>> (b & 0xF)));
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }







    @Benchmark
    public void LSHLShift(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)((a << (b & 15)));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void LSHLMaskedShift(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)((a << (b & 15))) : a);
            }
        }

        bh.consume(rs);
    }







    @Benchmark
    public void LSHRShift(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(((a & 0xFFFF) >>> (b & 15)));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void LSHRMaskedShift(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)(((a & 0xFFFF) >>> (b & 15))) : a);
            }
        }

        bh.consume(rs);
    }







    @Benchmark
    public void ASHRShift(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)((a >> (b & 15)));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ASHRMaskedShift(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)((a >> (b & 15))) : a);
            }
        }

        bh.consume(rs);
    }


    @Benchmark
    public void MIN(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(Math.min(a, b));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void MAX(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(Math.max(a, b));
            }
        }

        bh.consume(rs);
    }


    @Benchmark
    public void ANDLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        short r = -1;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = -1;
            for (int i = 0; i < as.length; i++) {
                r &= as[i];
            }
        }
        bh.consume(r);
    }



    @Benchmark
    public void ANDMaskedLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        short r = -1;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = -1;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r &= as[i];
            }
        }
        bh.consume(r);
    }



    @Benchmark
    public void ORLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        short r = 0;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 0;
            for (int i = 0; i < as.length; i++) {
                r |= as[i];
            }
        }
        bh.consume(r);
    }



    @Benchmark
    public void ORMaskedLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        short r = 0;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 0;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r |= as[i];
            }
        }
        bh.consume(r);
    }



    @Benchmark
    public void XORLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        short r = 0;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 0;
            for (int i = 0; i < as.length; i++) {
                r ^= as[i];
            }
        }
        bh.consume(r);
    }



    @Benchmark
    public void XORMaskedLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        short r = 0;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 0;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r ^= as[i];
            }
        }
        bh.consume(r);
    }


    @Benchmark
    public void ADDLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        short r = 0;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 0;
            for (int i = 0; i < as.length; i++) {
                r += as[i];
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void ADDMaskedLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        short r = 0;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 0;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r += as[i];
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MULLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        short r = 1;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 1;
            for (int i = 0; i < as.length; i++) {
                r *= as[i];
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MULMaskedLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        short r = 1;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = 1;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r *= as[i];
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MINLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        short r = Short.MAX_VALUE;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Short.MAX_VALUE;
            for (int i = 0; i < as.length; i++) {
                r = (short)Math.min(r, as[i]);
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MINMaskedLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        short r = Short.MAX_VALUE;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Short.MAX_VALUE;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r = (short)Math.min(r, as[i]);
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MAXLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        short r = Short.MIN_VALUE;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Short.MIN_VALUE;
            for (int i = 0; i < as.length; i++) {
                r = (short)Math.max(r, as[i]);
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MAXMaskedLanes(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        short r = Short.MIN_VALUE;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Short.MIN_VALUE;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r = (short)Math.max(r, as[i]);
            }
        }
        bh.consume(r);
    }


    @Benchmark
    public void anyTrue(Blackhole bh) {
        boolean[] ms = fm.apply(size);
        boolean r = false;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = false;
            for (int i = 0; i < ms.length; i++) {
                r |= ms[i];
            }
        }
        bh.consume(r);
    }



    @Benchmark
    public void allTrue(Blackhole bh) {
        boolean[] ms = fm.apply(size);
        boolean r = true;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = true;
            for (int i = 0; i < ms.length; i++) {
                r &= ms[i];
            }
        }
        bh.consume(r);
    }


    @Benchmark
    public void IS_DEFAULT(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                r &= (bits(a)==0); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }

    @Benchmark
    public void IS_NEGATIVE(Blackhole bh) {
        short[] as = fa.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                r &= (bits(a)<0); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }




    @Benchmark
    public void LT(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= lt(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }

    @Benchmark
    public void GT(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= gt(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }

    @Benchmark
    public void EQ(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= eq(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }

    @Benchmark
    public void NE(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= neq(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }

    @Benchmark
    public void LE(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= le(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }

    @Benchmark
    public void GE(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= ge(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }


    @Benchmark
    public void UNSIGNED_LT(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= ult(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }



    @Benchmark
    public void UNSIGNED_GT(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= ugt(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }



    @Benchmark
    public void UNSIGNED_LE(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= ule(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }



    @Benchmark
    public void UNSIGNED_GE(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= uge(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }


    @Benchmark
    public void blend(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? b : a);
            }
        }

        bh.consume(rs);
    }
    void rearrangeShared(int window, Blackhole bh) {
        short[] as = fa.apply(size);
        int[] order = fs.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i += window) {
                for (int j = 0; j < window; j++) {
                    short a = as[i+j];
                    int pos = order[j];
                    rs[i + pos] = a;
                }
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void rearrange064(Blackhole bh) {
        int window = 64 / Short.SIZE;
        rearrangeShared(window, bh);
    }

    @Benchmark
    public void rearrange128(Blackhole bh) {
        int window = 128 / Short.SIZE;
        rearrangeShared(window, bh);
    }

    @Benchmark
    public void rearrange256(Blackhole bh) {
        int window = 256 / Short.SIZE;
        rearrangeShared(window, bh);
    }

    @Benchmark
    public void rearrange512(Blackhole bh) {
        int window = 512 / Short.SIZE;
        rearrangeShared(window, bh);
    }
    void broadcastShared(int window, Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i += window) {
                int idx = i;
                for (int j = 0; j < window; j++) {
                    rs[j] = as[idx];
                }
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void broadcast064(Blackhole bh) {
        int window = 64 / Short.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void broadcast128(Blackhole bh) {
        int window = 128 / Short.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void broadcast256(Blackhole bh) {
        int window = 256 / Short.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void broadcast512(Blackhole bh) {
        int window = 512 / Short.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void zero(Blackhole bh) {
        short[] as = fa.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                as[i] = (short)0;
            }
        }

        bh.consume(as);
    }






















    @Benchmark
    public void BITWISE_BLEND(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] cs = fc.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                short c = cs[i];
                rs[i] = (short)((a&~(c))|(b&c));
            }
        }

        bh.consume(rs);
    }




    @Benchmark
    public void BITWISE_BLENDMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] cs = fc.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                short c = cs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)((a&~(c))|(b&c));
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }


    @Benchmark
    public void NEG(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                rs[i] = (short)(-((short)a));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void NEGMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)(-((short)a)) : a);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void ABS(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                rs[i] = (short)(Math.abs((short)a));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void ABSMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)(Math.abs((short)a)) : a);
            }
        }

        bh.consume(rs);
    }


    @Benchmark
    public void NOT(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                rs[i] = (short)(~((short)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void NOTMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)(~((short)a)) : a);
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ZOMO(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                rs[i] = (short)((a==0?0:-1));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ZOMOMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)((a==0?0:-1)) : a);
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void gatherBase0(Blackhole bh) {
        short[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                int ix = 0 + is[i];
                rs[i] = as[ix];
            }
        }

        bh.consume(rs);
    }


    void gather(int window, Blackhole bh) {
        short[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i += window) {
                for (int j = 0; j < window; j++) {
                    int ix = is[i + j];
                    rs[i + j] = as[ix];
                }
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void gather064(Blackhole bh) {
        int window = 64 / Short.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void gather128(Blackhole bh) {
        int window = 128 / Short.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void gather256(Blackhole bh) {
        int window = 256 / Short.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void gather512(Blackhole bh) {
        int window = 512 / Short.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void scatterBase0(Blackhole bh) {
        short[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                int ix = 0 + is[i];
                rs[ix] = as[i];
            }
        }

        bh.consume(rs);
    }

    void scatter(int window, Blackhole bh) {
        short[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i += window) {
                for (int j = 0; j < window; j++) {
                    int ix = is[i + j];
                    rs[ix] = as[i + j];
                }
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void scatter064(Blackhole bh) {
        int window = 64 / Short.SIZE;
        scatter(window, bh);
    }

    @Benchmark
    public void scatter128(Blackhole bh) {
        int window = 128 / Short.SIZE;
        scatter(window, bh);
    }

    @Benchmark
    public void scatter256(Blackhole bh) {
        int window = 256 / Short.SIZE;
        scatter(window, bh);
    }

    @Benchmark
    public void scatter512(Blackhole bh) {
        int window = 512 / Short.SIZE;
        scatter(window, bh);
    }
}

