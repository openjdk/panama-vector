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

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class BooleanArrayCheck {

  @Param("1024")
  int ARRAY_LENGTH;

  static final VectorSpecies<Byte> SPECIES = ByteVector.SPECIES_PREFERRED;

  private boolean[] bitsArray;

  @Setup
  public void init() {
    System.out.println("SPECIES's length: " + SPECIES.length());

    bitsArray = new boolean[ARRAY_LENGTH];
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      bitsArray[i] = true;
    }
  }

  @Benchmark
  public boolean filterAll_vec() {
    int filterPos = 0;

    for (; filterPos < ARRAY_LENGTH; filterPos += SPECIES.length()) {
      VectorMask<Byte> mask = VectorMask.fromArray(SPECIES, bitsArray, filterPos);
      if (!mask.allTrue()) {
        return false;
      }
    }

    for (filterPos -= SPECIES.length(); filterPos < ARRAY_LENGTH; filterPos++) {
      if (!bitsArray[filterPos]) {
        return false;
      }
    }
    return true;
  }

  @Benchmark
  public boolean filterAll() {
    int filterPos = 0;

    for (; filterPos < ARRAY_LENGTH; filterPos++) {
      if (!bitsArray[filterPos]) {
        return false;
      }
    }
    return true;
  }

}

