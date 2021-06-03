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
public class FloatScalar extends AbstractVectorBenchmark {
    static final int INVOC_COUNT = 1; // To align with vector benchmarks.

    @Param("1024")
    int size;

    float[] fill(IntFunction<Float> f) {
        float[] array = new float[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = f.apply(i);
        }
        return array;
    }

    static int bits(float e) {
        return Float.floatToIntBits(e);
    }

    float[] as, bs, cs, rs;
    boolean[] ms, mt, rms;
    int[] ss;

    @Setup
    public void init() {
        as = fill(i -> (float)(2*i));
        bs = fill(i -> (float)(i+1));
        cs = fill(i -> (float)(i+5));
        rs = fill(i -> (float)0);
        ms = fillMask(size, i -> (i % 2) == 0);
        mt = fillMask(size, i -> true);
        rms = fillMask(size, i -> false);

        ss = fillInt(size, i -> RANDOM.nextInt(Math.max(i,1)));
    }

    final IntFunction<float[]> fa = vl -> as;
    final IntFunction<float[]> fb = vl -> bs;
    final IntFunction<float[]> fc = vl -> cs;
    final IntFunction<float[]> fr = vl -> rs;
    final IntFunction<boolean[]> fm = vl -> ms;
    final IntFunction<boolean[]> fmt = vl -> mt;
    final IntFunction<boolean[]> fmr = vl -> rms;
    final IntFunction<int[]> fs = vl -> ss;

    static boolean eq(float a, float b) {
        return a == b;
    }

    static boolean neq(float a, float b) {
        return a != b;
    }

    static boolean lt(float a, float b) {
        return a < b;
    }

    static boolean le(float a, float b) {
        return a <= b;
    }

    static boolean gt(float a, float b) {
        return a > b;
    }

    static boolean ge(float a, float b) {
        return a >= b;
    }


    @Benchmark
    public void ADD(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(a + b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void ADDMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (float)(a + b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }

    @Benchmark
    public void SUB(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(a - b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void SUBMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (float)(a - b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }

    @Benchmark
    public void MUL(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(a * b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void MULMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (float)(a * b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }


    @Benchmark
    public void DIV(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(a / b);
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void DIVMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (float)(a / b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }


    @Benchmark
    public void FIRST_NONZERO(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(Double.doubleToLongBits(a)!=0?a:b);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void FIRST_NONZEROMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (float)(Double.doubleToLongBits(a)!=0?a:b);
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }













































    @Benchmark
    public void MIN(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(Math.min(a, b));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void MAX(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(Math.max(a, b));
            }
        }

        bh.consume(rs);
    }







    @Benchmark
    public void ADDLanes(Blackhole bh) {
        float[] as = fa.apply(size);
        float r = 0;
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
        float[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        float r = 0;
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
        float[] as = fa.apply(size);
        float r = 1;
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
        float[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        float r = 1;
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
        float[] as = fa.apply(size);
        float r = Float.POSITIVE_INFINITY;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Float.POSITIVE_INFINITY;
            for (int i = 0; i < as.length; i++) {
                r = (float)Math.min(r, as[i]);
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MINMaskedLanes(Blackhole bh) {
        float[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        float r = Float.POSITIVE_INFINITY;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Float.POSITIVE_INFINITY;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r = (float)Math.min(r, as[i]);
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MAXLanes(Blackhole bh) {
        float[] as = fa.apply(size);
        float r = Float.NEGATIVE_INFINITY;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < as.length; i++) {
                r = (float)Math.max(r, as[i]);
            }
        }
        bh.consume(r);
    }

    @Benchmark
    public void MAXMaskedLanes(Blackhole bh) {
        float[] as = fa.apply(size);
        boolean[] ms = fm.apply(size);
        float r = Float.NEGATIVE_INFINITY;
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            r = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < as.length; i++) {
                if (ms[i % ms.length])
                    r = (float)Math.max(r, as[i]);
            }
        }
        bh.consume(r);
    }



    @Benchmark
    public void IS_DEFAULT(Blackhole bh) {
        float[] as = fa.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                r &= (bits(a)==0); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }

    @Benchmark
    public void IS_NEGATIVE(Blackhole bh) {
        float[] as = fa.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                r &= (bits(a)<0); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }


    @Benchmark
    public void IS_FINITE(Blackhole bh) {
        float[] as = fa.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                r &= (Float.isFinite(a)); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }



    @Benchmark
    public void IS_NAN(Blackhole bh) {
        float[] as = fa.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                r &= (Float.isNaN(a)); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }



    @Benchmark
    public void IS_INFINITE(Blackhole bh) {
        float[] as = fa.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                r &= (Float.isInfinite(a)); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }


    @Benchmark
    public void LT(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
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
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
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
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
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
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
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
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
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
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        boolean r = true;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                r &= ge(as[i], bs[i]); // accumulate so JIT can't eliminate the computation
            }
        }

        bh.consume(r);
    }





    @Benchmark
    public void blend(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? b : a);
            }
        }

        bh.consume(rs);
    }
    void rearrangeShared(int window, Blackhole bh) {
        float[] as = fa.apply(size);
        int[] order = fs.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i += window) {
                for (int j = 0; j < window; j++) {
                    float a = as[i+j];
                    int pos = order[j];
                    rs[i + pos] = a;
                }
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void rearrange064(Blackhole bh) {
        int window = 64 / Float.SIZE;
        rearrangeShared(window, bh);
    }

    @Benchmark
    public void rearrange128(Blackhole bh) {
        int window = 128 / Float.SIZE;
        rearrangeShared(window, bh);
    }

    @Benchmark
    public void rearrange256(Blackhole bh) {
        int window = 256 / Float.SIZE;
        rearrangeShared(window, bh);
    }

    @Benchmark
    public void rearrange512(Blackhole bh) {
        int window = 512 / Float.SIZE;
        rearrangeShared(window, bh);
    }
    void broadcastShared(int window, Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

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
        int window = 64 / Float.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void broadcast128(Blackhole bh) {
        int window = 128 / Float.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void broadcast256(Blackhole bh) {
        int window = 256 / Float.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void broadcast512(Blackhole bh) {
        int window = 512 / Float.SIZE;
        broadcastShared(window, bh);
    }

    @Benchmark
    public void zero(Blackhole bh) {
        float[] as = fa.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                as[i] = (float)0;
            }
        }

        bh.consume(as);
    }


    @Benchmark
    public void SIN(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.sin((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void EXP(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.exp((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void LOG1P(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.log1p((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void LOG(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.log((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void LOG10(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.log10((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void EXPM1(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.expm1((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void COS(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.cos((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void TAN(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.tan((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void SINH(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.sinh((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void COSH(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.cosh((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void TANH(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.tanh((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ASIN(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.asin((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ACOS(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.acos((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ATAN(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.atan((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void CBRT(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.cbrt((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void HYPOT(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(Math.hypot((double)a, (double)b));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void POW(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(Math.pow((double)a, (double)b));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void ATAN2(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                rs[i] = (float)(Math.atan2((double)a, (double)b));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void FMA(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] cs = fc.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                float c = cs[i];
                rs[i] = (float)(Math.fma(a, b, c));
            }
        }

        bh.consume(rs);
    }




    @Benchmark
    public void FMAMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] bs = fb.apply(size);
        float[] cs = fc.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                float b = bs[i];
                float c = cs[i];
                if (ms[i % ms.length]) {
                    rs[i] = (float)(Math.fma(a, b, c));
                } else {
                    rs[i] = a;
                }
            }
        }
        bh.consume(rs);
    }




    @Benchmark
    public void NEG(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(-((float)a));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void NEGMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (float)(-((float)a)) : a);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void ABS(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.abs((float)a));
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void ABSMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (float)(Math.abs((float)a)) : a);
            }
        }

        bh.consume(rs);
    }






    @Benchmark
    public void SQRT(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                rs[i] = (float)(Math.sqrt((double)a));
            }
        }

        bh.consume(rs);
    }



    @Benchmark
    public void SQRTMasked(Blackhole bh) {
        float[] as = fa.apply(size);
        float[] rs = fr.apply(size);
        boolean[] ms = fm.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                float a = as[i];
                boolean m = ms[i % ms.length];
                rs[i] = (m ? (float)(Math.sqrt((double)a)) : a);
            }
        }

        bh.consume(rs);
    }

    @Benchmark
    public void gatherBase0(Blackhole bh) {
        float[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                int ix = 0 + is[i];
                rs[i] = as[ix];
            }
        }

        bh.consume(rs);
    }


    void gather(int window, Blackhole bh) {
        float[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        float[] rs = fr.apply(size);

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
        int window = 64 / Float.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void gather128(Blackhole bh) {
        int window = 128 / Float.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void gather256(Blackhole bh) {
        int window = 256 / Float.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void gather512(Blackhole bh) {
        int window = 512 / Float.SIZE;
        gather(window, bh);
    }

    @Benchmark
    public void scatterBase0(Blackhole bh) {
        float[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        float[] rs = fr.apply(size);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < as.length; i++) {
                int ix = 0 + is[i];
                rs[ix] = as[i];
            }
        }

        bh.consume(rs);
    }

    void scatter(int window, Blackhole bh) {
        float[] as = fa.apply(size);
        int[] is    = fs.apply(size);
        float[] rs = fr.apply(size);

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
        int window = 64 / Float.SIZE;
        scatter(window, bh);
    }

    @Benchmark
    public void scatter128(Blackhole bh) {
        int window = 128 / Float.SIZE;
        scatter(window, bh);
    }

    @Benchmark
    public void scatter256(Blackhole bh) {
        int window = 256 / Float.SIZE;
        scatter(window, bh);
    }

    @Benchmark
    public void scatter512(Blackhole bh) {
        int window = 512 / Float.SIZE;
        scatter(window, bh);
    }
}

