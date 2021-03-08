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

package benchmark.bigdata;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class ValueRangeCheckAndCastL2I {

  @Param("1024")
  int ARRAY_LENGTH;

  private long min = Integer.MIN_VALUE;
  private long max = Integer.MAX_VALUE;

  private static final VectorSpecies<Long> LONG_SPECIES =
    LongVector.SPECIES_PREFERRED;
  private static final VectorSpecies<Integer> INT_SPECIES =
    VectorSpecies.of(int.class, VectorShape.forBitSize(LONG_SPECIES.vectorBitSize() / 2));

  private int[] intResult;
  private long[] longArray;

  @Setup
  public void init() {
    System.out.println("LONG_SPECIES's length: " + LONG_SPECIES.length());
    System.out.println("INT_SPECIES's length: " + INT_SPECIES.length());
    System.out.println("Min is: " + min + ". Max is: " + max);

    longArray = new long[ARRAY_LENGTH];
    intResult = new int[ARRAY_LENGTH];

    Random rand = new Random();
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      intResult[i] = 0;
      longArray[i] = rand.nextInt(Integer.MAX_VALUE);
    }
  }

  @Benchmark
  public boolean castL2I() {
    for (int i = 0; i < longArray.length; i++) {
      if (longArray[i] >= min && longArray[i] <= max) {
        intResult[i] = (int)(longArray[i]);
      } else {
        return false;
      }
    }
    return true;
  }

  @Benchmark
  public boolean castL2I_vec() {
    for (int i = 0; i < longArray.length; i += LONG_SPECIES.length()) {
      LongVector av = LongVector.fromArray(LONG_SPECIES, longArray, i);
      if (av.compare(VectorOperators.GE, min).and(av.compare(VectorOperators.LE, max)).allTrue()) {
        ((IntVector) av.castShape(INT_SPECIES, 0)).intoArray(intResult, i);
      } else {
        return false;
      }
    }
    return true;
  }

}

