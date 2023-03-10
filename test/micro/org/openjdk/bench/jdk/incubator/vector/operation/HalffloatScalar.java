/*
 * Copyright (c) 2018, 2022, Oracle and/or its affiliates. All rights reserved.
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

package org.openjdk.bench.jdk.incubator.vector.operation;

// -- This file was mechanically generated: Do not edit! -- //

import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import jdk.incubator.vector.Halffloat;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class HalffloatScalar extends AbstractVectorBenchmark {
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
        return Halffloat.shortToShortBits(e);
    }

    short[] as, bs, cs, rs;
    boolean[] ms, mt, rms;
    int[] ss;

    static short genValue(int i) {
        return Halffloat.valueOf(i);
    }

    @Setup
    public void init() {
        as = fill(i -> genValue(2*i));
        bs = fill(i -> genValue(i+1));
        cs = fill(i -> genValue(i+5));
        rs = fill(i -> genValue(0));
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



    @Benchmark
    public void ADD(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) + Float.float16ToFloat(b)));
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
                    rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) + Float.float16ToFloat(b)));
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
                rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) - Float.float16ToFloat(b)));
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
                    rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) - Float.float16ToFloat(b)));
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
                rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) * Float.float16ToFloat(b)));
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
                    rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) * Float.float16ToFloat(b)));
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }

    @Benchmark
    public void DIV(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) / Float.float16ToFloat(b)));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void DIVMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(Float.floatToFloat16(Float.float16ToFloat(a) / Float.float16ToFloat(b)));
                } else {
                    rs[i] = a;
                }
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
                rs[i] = (short)(Float.floatToFloat16(Math.max(Float.float16ToFloat(a), Float.float16ToFloat(b))));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void MAXMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(Float.floatToFloat16(Math.max(Float.float16ToFloat(a), Float.float16ToFloat(b))));
                } else {
                    rs[i] = a;
                }
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
                rs[i] = (short)(Halffloat.valueOf(Math.min(Float.float16ToFloat(a), Float.float16ToFloat(b))));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void MINMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (short)(Halffloat.valueOf(Math.min(Float.float16ToFloat(a), Float.float16ToFloat(b))));
                } else {
                    rs[i] = a;
                }
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
                rs[i] = (short)(Math.abs(a));
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
                rs[i] = (m ? (short)(Math.abs(a)) : a);
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
                rs[i] = (short)(-a);
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
                rs[i] = (m ? (short)(-a) : a);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void FMA(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] bs = fb.apply(size);
        short[] cs = fc.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                short b = bs[i];
                short c = cs[i];
                rs[i] = (short)(Float.floatToFloat16(Math.fma(Float.float16ToFloat(a), Float.float16ToFloat(b), Float.float16ToFloat(c))));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void FMAMasked(Blackhole bh) {
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
                    rs[i] = (short)(Float.floatToFloat16(Math.fma(Float.float16ToFloat(a), Float.float16ToFloat(b), Float.float16ToFloat(c))));
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }
    @Benchmark
    public void SQRT(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                rs[i] = (short)(Halffloat.valueOf((float) Math.sqrt(Float.float16ToFloat(a))));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void SQRTMasked(Blackhole bh) {
        short[] as = fa.apply(size);
        short[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                short a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (short)(Halffloat.valueOf((float) Math.sqrt(Float.float16ToFloat(a)))) : a);
            }
        }

        bh.consume(rs);
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
}
