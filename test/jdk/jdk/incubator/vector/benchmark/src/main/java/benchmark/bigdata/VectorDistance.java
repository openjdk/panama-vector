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

import jdk.incubator.vector.*;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

import static org.junit.jupiter.api.Assertions.*;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class VectorDistance {

    static final VectorSpecies<Float> SPECIES_FLOAT_128 = FloatVector.SPECIES_128;
    static final VectorSpecies<Float> SPECIES_FLOAT_256 = FloatVector.SPECIES_256;
    static final VectorSpecies<Float> SPECIES_FLOAT_MAX = FloatVector.SPECIES_MAX;
    static final VectorSpecies<Double> SPECIES_DOUBLE_128 = DoubleVector.SPECIES_128;
    static final VectorSpecies<Double> SPECIES_DOUBLE_256 = DoubleVector.SPECIES_256;
    static final VectorSpecies<Double> SPECIES_DOUBLE_MAX = DoubleVector.SPECIES_MAX;


    static long num = 0;
    private static String x1 = "L5GSwXhHpEH05mNBHnmcQMTw3EBnagFCW1DGQHe/nUFO1B1BlJOpwCBJ9j" +
                "+RkY1BzqKeQSglN0Gy7krB5CSfQFzxB8Djn5nB2KNFwKcSRMGYzRQ7qMGWQZ0FF0FTceDAIKjxv/zhdkHFZMHB6hU4QZbo2cCAryRB+7OOQCxbfEHRtBlBxPG6P0BYSD+Pgz9BqzOLv/nVO8C9x5/BQOY/wTTIx0GfW1BBGv2lQQwdDcGCqBfB12t/QKUBoEEejIXBPN9kQWsFbEGsGcnBkqJkwKhLgr/IQZxAelAWQfcYpcFQv0HBeiGCQWExhEDrKAnBpAwBQV4bVcFpGNjAyDsNQVOc+0CSc4nBgG/ZQQGRccEXts9BKhYzQNK5+MAlU0DBzPGWwPGRCcEZC5/ADxOcv7lUkEBomM5BuqKiwV2MU8HNGHDBSB84QZRSyMB8RZlBVFdZQXSVgcBTQQBCdWa/QBQ0qkGILUW/6NA9QQnkmsG+5PPBj0UowT6nYD9cwpjAS/w5wTbX2UH8Gb5AR/HUQMTNAMJ9MN9AgHoqPbbUyUFbe47BBHANQWZJBsGBuPlBy94EQADeXsG5eOtBnA+yQCRka8EMcGLBjuoRwb4k7sAasB5Bmk/UwaI1akErp6xBq5G5wNo1E8KHa7tB3IiKQTCffcHphK1BTgJzwVY3JEEip/VAlmgXQSeKCsLEABs/n1/xwL5u58CgQY49ahUWQoAJjj1hhqBASXrrQb6nM0H2fY+/thtbQAQobMAohvXAxM3xv7xyqD+MvpDBrlDiQfBvPcGA8X5AQE4SwXhGx7+uLA1AxY8xu2mVjEE7KlFBArveQFNMtUD3N7DB12BbQcyH4cFhSw3Bu5VWQeTW0z9o03TBxtMlQctp/8E/lLVAGUtTwZsGJMKv/R5A1HKVQV6RhsC1Ji5AcXLFQJd6f0HbB+e+ZDi8wV9tQ0FwCN/B+A89v2DrU0Bcpc5BglTeQH5dT0HePS9Al4XPwdA6YEFlueXAbWKSQSBWzkBy2RnCt9Yawl9b77+xgxBC9eCqQd8f0kFoBG9BVxrkQZh2QkHNW/zBEQiawLJEocDhutTA8zEYwbIvEUIO1T9BmlOTwIhbNEDhrtlAVk9BQARQaj89NQNC6usGwDfQrkBSJrlAON7FQQ8FqsEEc/TAY3zeQYsqUEHV8QPBHJoYQQdn5kGyCiJBlDMYQBBNoUFrxbw/NlmPP3B24j6ChIdBXk2bwdxdDMFQw1rA4hybQXTchr8d9wvBuCbLQSMKmMBH4RpBQIXePa5DT8IjgvtBgAetQZgGgMEprc1BAOeSPJ5XpEEMa0NBgX4uwX7XIsG2Ie0688iqQSpJPsCAy9LBAGHkPw==";
    private static String x2 = "5R3ZwGPrxEFMKyNBLFSeQdYav0BQtDFCur7WQAgRYEGHFYC/MKZtvkiFUT+RNXfBVsGBP2KWSUCmAUTBIf+EQG57kMCtXo7BV1DuwLd98r+YzRQ7qKXNwBMSPUFNQffBPrxeQYw1t7/7JjFAKNaXP+cMSEG6GI5BuEx0wUANDMEvDqdAT9YEworQTEEiVBZBiMejQP7t67+iRwzB3HadQB1be0Ei5g5BMt+cQXvYTUHwZsLAuoy3QfrR6EFrIiHB5X8Dwc8XbUH8Yr8/AvGEwa5GkUH3F5tAP8YJQTiDyz+gKsRAFl/rwDxJuUAPyyxBvg2gQU6bjMEPEa7Bz6wYQpQy7MDF5LvB8HP+QCJdicHQDjpC6RpWQcGeY8FMK6vBoeUjQcPYmUG2QmRBBI0nwScESsGMAcxBvRmawRL2A8IByKNAgTQBQuxdDEGq8JBBHJWmQSBDfz8sLe9BE3gFwTdCPkHEaMxBhX8Xwe7BCcE/783Bt6EHwdpbpkHc5L/BCPzRwUdIQUEd/k3AoGNcQQwNmMEyuKRBtnWlwdCBAUI5Y5DBwOZYvdI+MsEu/ixBnpMrwRtYt8FECytC6JjEQW3RHcBtfn3B+sgQQcyQKcEI5ytByvw2wPZdaUH+aqLAQFQ+QPi4REBF/9lBCvJNQTdlEcIAMbzBtD+hwZWufsAEjus/YRyjwR1YuMHj0ZhBa4w+QORAhMEq9qdB/L8JQrjhyUAJBeBAKqoIQUnAq0GsLFdBkfrvQHc1zMHH6THBeggSwaJIOsAawwBBDDWqPwrAlkBYDqe/maUcQabhwsFF2VBBxY8xu5aMQUFDkHVBKhRRwHhgWsEA5jXBlh9NQVMaT0CWlhTAroaFQRyciUHQlp7BF4trQa8unsE4TfI+9XLJQDNpLcIXLZdAuX2MwShiTsFcQh5BrHMqQVI1+UBWe4fBAzi0wfe11UFAIjq9Y1iAQDxrTsEY6plB/JiXQfjFwkHkYGRBVNOhwCMxtEFbqZTA378WQeA/Sb+FrSXCqlYywtb5SsDcqlZBk1EtQZ/RREHZIxG/kcv8QekDIkHPsDXCBL4VQHN8CMGtNvvAC3YwweUuAkKkJCnANEtVQG9z/0DrwyTBQ9hnwWX3kMEdLB1CvIlKwQ0IO0HK1ErBvdRQQVpjMMCJDI/Bb4X8QYVipEGpG2nBeLGUvmBlBT7ISgRB4iGAQUunkkFDFLm/HNaqPzKTVkCITJG/XzlYwbj0XcGD60PBbpLwQbvrs8Az8RXB4ubxQXh/HEDtXLU/kONrwVBs4MGc2X1BJaHkQd0ByEAKXLJBTq7JwPPkJUGJIIRBlh57wX3FjcC2Ie060Qc6Qal5xcCfqQrCl7edQQ==";
    static float[] queryVectorFloat = parseBase64ToVector(x1);
    static float[] inputVectorFloat = parseBase64ToVector(x2);
    static double[] queryVectorDouble;
    static double[] inputVectorDouble;

    static float normQueryVectorFloat;
    static double normQueryVectorDouble;

    public static float[] parseArray(byte[] input) {
        if (input == null) {
            return null;
        }
        float[] floatArr = new float[input.length / 4];
        for (int i = 0; i < floatArr.length; i++) {
            int l;
            l = input[i << 2];
            l &= 0xff;
            l |= ((long) input[(i << 2) + 1] << 8);
            l &= 0xffff;
            l |= ((long) input[(i << 2) + 2] << 16);
            l &= 0xffffff;
            l |= ((long) input[(i << 2) + 3] << 24);
            floatArr[i] = Float.intBitsToFloat(l);
        }
        return floatArr;
    }

    public static float[] parseBase64ToVector(String vectorBase64) {
        return parseArray(Base64.getDecoder().decode(vectorBase64));
    }

    @Setup
    public static void init() {
        queryVectorDouble = new double[queryVectorFloat.length];
        inputVectorDouble = new double[inputVectorFloat.length];
        for (int i = 0; i < queryVectorFloat.length; i++) {
            queryVectorDouble[i] = (double)(queryVectorFloat[i]);
        }
        for (int i = 0; i < inputVectorFloat.length; i++) {
            inputVectorDouble[i] = (double)(inputVectorFloat[i]);
        }
        float xSquare = 0;
        for (int i = 0; i < queryVectorFloat.length; i++) {
            xSquare += (float)(queryVectorFloat[i] * queryVectorFloat[i]);
        }
        normQueryVectorFloat = xSquare;
        normQueryVectorDouble = (double)xSquare;
    }

    @Benchmark
    public static float cosinesimilOptimizedScalarFloat() {
        float dotProduct = 0.0f;
        float normInputVector = 0.0f;
        for (int i = 0; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        if (normalizedProduct == 0) {
            return Float.MIN_VALUE;
        }
        return (float) (dotProduct / (Math.sqrt(normalizedProduct)));
    }

    @Benchmark
    public static float cosinesimilOptimizedVectorFloat128() {
        FloatVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = FloatVector.zero(SPECIES_FLOAT_128);
        xSquareV = FloatVector.zero(SPECIES_FLOAT_128);
        ySquareV = FloatVector.zero(SPECIES_FLOAT_128);;
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_128.length()) <= queryVectorFloat.length; i += SPECIES_FLOAT_128.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_128, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_128, inputVectorFloat, i);
            vecSum = vecX.fma(vecY, vecSum);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        float dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        float normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        return (float)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static float cosinesimilOptimizedVectorFloat256() {
        FloatVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = FloatVector.zero(SPECIES_FLOAT_256);
        xSquareV = FloatVector.zero(SPECIES_FLOAT_256);
        ySquareV = FloatVector.zero(SPECIES_FLOAT_256);;
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_256.length()) <= queryVectorFloat.length; i += SPECIES_FLOAT_256.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_256, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_256, inputVectorFloat, i);
            vecSum = vecX.fma(vecY, vecSum);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        float dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        float normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        return (float)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static float cosinesimilOptimizedVectorFloatMax() {
        FloatVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = FloatVector.zero(SPECIES_FLOAT_MAX);
        xSquareV = FloatVector.zero(SPECIES_FLOAT_MAX);
        ySquareV = FloatVector.zero(SPECIES_FLOAT_MAX);;
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_MAX.length()) <= queryVectorFloat.length; i += SPECIES_FLOAT_MAX.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_MAX, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_MAX, inputVectorFloat, i);
            vecSum = vecX.fma(vecY, vecSum);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        float dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        float normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        return (float)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static float cosinesimilScalarFloat() {
        float dotProduct = 0.0f;
        float normQueryVectorFloat = 0.0f;
        float normInputVector = 0.0f;
        for (int i = 0; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normQueryVectorFloat += queryVectorFloat[i] * queryVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        if (normalizedProduct == 0) {
            return Float.MIN_VALUE;
        }
        return (float) (dotProduct / (Math.sqrt(normalizedProduct)));
    }

    @Benchmark
    public float cosinesimilVectorFloat128() {
        FloatVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = FloatVector.zero(SPECIES_FLOAT_128);
        xSquareV = FloatVector.zero(SPECIES_FLOAT_128);
        ySquareV = FloatVector.zero(SPECIES_FLOAT_128);;
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_128.length()) <= queryVectorFloat.length; i += SPECIES_FLOAT_128.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_128, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_128, inputVectorFloat, i);
            vecSum = vecX.fma(vecY, vecSum);
            xSquareV = vecX.fma(vecX, xSquareV);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        float dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        float normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        float normQueryVectorFloat = xSquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
            normQueryVectorFloat += queryVectorFloat[i] * queryVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        return (float)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public float cosinesimilVectorFloat256() {
        FloatVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = FloatVector.zero(SPECIES_FLOAT_256);
        xSquareV = FloatVector.zero(SPECIES_FLOAT_256);
        ySquareV = FloatVector.zero(SPECIES_FLOAT_256);;
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_256.length()) <= queryVectorFloat.length; i += SPECIES_FLOAT_256.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_256, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_256, inputVectorFloat, i);
            vecSum = vecX.fma(vecY, vecSum);
            xSquareV = vecX.fma(vecX, xSquareV);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        float dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        float normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        float normQueryVectorFloat = xSquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
            normQueryVectorFloat += queryVectorFloat[i] * queryVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        return (float)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public float cosinesimilVectorFloatMax() {
        FloatVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = FloatVector.zero(SPECIES_FLOAT_MAX);
        xSquareV = FloatVector.zero(SPECIES_FLOAT_MAX);
        ySquareV = FloatVector.zero(SPECIES_FLOAT_MAX);
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_MAX.length()) <= queryVectorFloat.length; i += SPECIES_FLOAT_MAX.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_MAX, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_MAX, inputVectorFloat, i);
            vecSum = vecX.fma(vecY, vecSum);
            xSquareV = vecX.fma(vecX, xSquareV);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        float dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        float normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        float normQueryVectorFloat = xSquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorFloat.length; i++) {
            dotProduct += queryVectorFloat[i] * inputVectorFloat[i];
            normInputVector += inputVectorFloat[i] * inputVectorFloat[i];
            normQueryVectorFloat += queryVectorFloat[i] * queryVectorFloat[i];
        }
        float normalizedProduct = normQueryVectorFloat * normInputVector;
        return (float)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static double cosinesimilOptimizedScalarDouble() {
        double dotProduct = 0.0;
        double normInputVector = 0.0;
        for (int i = 0; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return dotProduct / (Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static double cosinesimilOptimizedVectorDouble128() {
        DoubleVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_128);
        xSquareV = DoubleVector.zero(SPECIES_DOUBLE_128);
        ySquareV = DoubleVector.zero(SPECIES_DOUBLE_128);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_128.length()) <= queryVectorDouble.length; i += SPECIES_DOUBLE_128.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_128, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_128, inputVectorDouble, i);
            vecSum = vecX.fma(vecY, vecSum);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        double dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        double normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return (double)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static double cosinesimilOptimizedVectorDouble256() {
        DoubleVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_256);
        xSquareV = DoubleVector.zero(SPECIES_DOUBLE_256);
        ySquareV = DoubleVector.zero(SPECIES_DOUBLE_256);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_256.length()) <= queryVectorDouble.length; i += SPECIES_DOUBLE_256.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_256, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_256, inputVectorDouble, i);
            vecSum = vecX.fma(vecY, vecSum);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        double dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        double normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return (double)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static double cosinesimilOptimizedVectorDoubleMax() {
        DoubleVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_MAX);
        xSquareV = DoubleVector.zero(SPECIES_DOUBLE_MAX);
        ySquareV = DoubleVector.zero(SPECIES_DOUBLE_MAX);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_MAX.length()) <= queryVectorDouble.length; i += SPECIES_DOUBLE_MAX.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_MAX, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_MAX, inputVectorDouble, i);
            vecSum = vecX.fma(vecY, vecSum);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        double dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        double normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return (double)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public static double cosinesimilScalarDouble() {
        double dotProduct = 0.0f;
        double normQueryVectorDouble = 0.0f;
        double normInputVector = 0.0f;
        for (int i = 0; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normQueryVectorDouble += queryVectorDouble[i] * queryVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return (double) (dotProduct / (Math.sqrt(normalizedProduct)));
    }

    @Benchmark
    public double cosinesimilVectorDouble128() {
        DoubleVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_128);
        xSquareV = DoubleVector.zero(SPECIES_DOUBLE_128);
        ySquareV = DoubleVector.zero(SPECIES_DOUBLE_128);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_128.length()) <= queryVectorDouble.length; i += SPECIES_DOUBLE_128.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_128, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_128, inputVectorDouble, i);
            vecSum = vecX.fma(vecY, vecSum);
            xSquareV = vecX.fma(vecX, xSquareV);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        double dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        double normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        double normQueryVectorDouble = xSquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
            normQueryVectorDouble += queryVectorDouble[i] * queryVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return (double)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public double cosinesimilVectorDouble256() {
        DoubleVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_256);
        xSquareV = DoubleVector.zero(SPECIES_DOUBLE_256);
        ySquareV = DoubleVector.zero(SPECIES_DOUBLE_256);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_256.length()) <= queryVectorDouble.length; i += SPECIES_DOUBLE_256.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_256, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_256, inputVectorDouble, i);
            vecSum = vecX.fma(vecY, vecSum);
            xSquareV = vecX.fma(vecX, xSquareV);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        double dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        double normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        double normQueryVectorDouble = xSquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
            normQueryVectorDouble += queryVectorDouble[i] * queryVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return (double)(dotProduct / Math.sqrt(normalizedProduct));
    }

    @Benchmark
    public double cosinesimilVectorDoubleMax() {
        DoubleVector vecX, vecY, vecSum, xSquareV, ySquareV;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_MAX);
        xSquareV = DoubleVector.zero(SPECIES_DOUBLE_MAX);
        ySquareV = DoubleVector.zero(SPECIES_DOUBLE_MAX);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_MAX.length()) <= queryVectorDouble.length; i += SPECIES_DOUBLE_MAX.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_MAX, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_MAX, inputVectorDouble, i);
            vecSum = vecX.fma(vecY, vecSum);
            xSquareV = vecX.fma(vecX, xSquareV);
            ySquareV = vecY.fma(vecY, ySquareV);
        }
        double dotProduct = vecSum.reduceLanes(VectorOperators.ADD);
        double normInputVector = ySquareV.reduceLanes(VectorOperators.ADD);
        double normQueryVectorDouble = xSquareV.reduceLanes(VectorOperators.ADD);
        for (; i < queryVectorDouble.length; i++) {
            dotProduct += queryVectorDouble[i] * inputVectorDouble[i];
            normInputVector += inputVectorDouble[i] * inputVectorDouble[i];
            normQueryVectorDouble += queryVectorDouble[i] * queryVectorDouble[i];
        }
        double normalizedProduct = normQueryVectorDouble * normInputVector;
        return (double)(dotProduct / Math.sqrt(normalizedProduct));
    }

    // l2Squared is used to compute Euclidean distance
    @Benchmark
    public float l2SquaredVectorFloat128() {
        FloatVector vecX, vecY, vecSum, vecSquare, vecDiff;
        vecSum = FloatVector.zero(SPECIES_FLOAT_128);
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_128.length()) <= inputVectorFloat.length; i += SPECIES_FLOAT_128.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_128, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_128, inputVectorFloat, i);
            vecDiff = vecX.sub(vecY);
            vecSquare = vecDiff.mul(vecDiff);
            vecSum = vecDiff.fma(vecDiff, vecSum);
        }
        float sum = vecSum.reduceLanes(VectorOperators.ADD);
        for (; i < inputVectorFloat.length; i++) {
            float diff = queryVectorFloat[i] - inputVectorFloat[i];
            sum += diff * diff;
        }
        return sum;
    }

    @Benchmark
    public float l2SquaredVectorFloat256() {
        FloatVector vecX, vecY, vecSum, vecSquare, vecDiff;
        vecSum = FloatVector.zero(SPECIES_FLOAT_256);
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_256.length()) <= inputVectorFloat.length; i += SPECIES_FLOAT_256.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_256, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_256, inputVectorFloat, i);
            vecDiff = vecX.sub(vecY);
            vecSquare = vecDiff.mul(vecDiff);
            vecSum = vecDiff.fma(vecDiff, vecSum);
        }
        float sum = vecSum.reduceLanes(VectorOperators.ADD);
        for (; i < inputVectorFloat.length; i++) {
            float diff = queryVectorFloat[i] - inputVectorFloat[i];
            sum += diff * diff;
        }
        return sum;
    }

    @Benchmark
    public float l2SquaredVectorFloatMax() {
        FloatVector vecX, vecY, vecSum, vecSquare, vecDiff;
        vecSum = FloatVector.zero(SPECIES_FLOAT_MAX);
        int i = 0;
        for (i = 0; i + (SPECIES_FLOAT_MAX.length()) <= inputVectorFloat.length; i += SPECIES_FLOAT_MAX.length()) {
            vecX = FloatVector.fromArray(SPECIES_FLOAT_MAX, queryVectorFloat, i);
            vecY = FloatVector.fromArray(SPECIES_FLOAT_MAX, inputVectorFloat, i);
            vecDiff = vecX.sub(vecY);
            vecSquare = vecDiff.mul(vecDiff);
            vecSum = vecDiff.fma(vecDiff, vecSum);
        }
        float sum = vecSum.reduceLanes(VectorOperators.ADD);
        for (; i < inputVectorFloat.length; i++) {
            float diff = queryVectorFloat[i] - inputVectorFloat[i];
            sum += diff * diff;
        }
        return sum;
    }

    @Benchmark
    public double l2SquaredVectorDouble128() {
        DoubleVector vecX, vecY, vecSum, vecSquare, vecDiff;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_128);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_128.length()) <= inputVectorDouble.length; i += SPECIES_DOUBLE_128.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_128, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_128, inputVectorDouble, i);
            vecDiff = vecX.sub(vecY);
            vecSquare = vecDiff.mul(vecDiff);
            vecSum = vecDiff.fma(vecDiff, vecSum);
        }
        double sum = vecSum.reduceLanes(VectorOperators.ADD);
        for (; i < inputVectorDouble.length; i++) {
            double diff = queryVectorDouble[i] - inputVectorDouble[i];
            sum += diff * diff;
        }
        return sum;
    }

    @Benchmark
    public double l2SquaredVectorDouble256() {
        DoubleVector vecX, vecY, vecSum, vecSquare, vecDiff;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_256);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_256.length()) <= inputVectorDouble.length; i += SPECIES_DOUBLE_256.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_256, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_256, inputVectorDouble, i);
            vecDiff = vecX.sub(vecY);
            vecSquare = vecDiff.mul(vecDiff);
            vecSum = vecDiff.fma(vecDiff, vecSum);
        }
        double sum = vecSum.reduceLanes(VectorOperators.ADD);
        for (; i < inputVectorDouble.length; i++) {
            double diff = queryVectorDouble[i] - inputVectorDouble[i];
            sum += diff * diff;
        }
        return sum;
    }

    @Benchmark
    public double l2SquaredVectorDoubleMax() {
        DoubleVector vecX, vecY, vecSum, vecSquare, vecDiff;
        vecSum = DoubleVector.zero(SPECIES_DOUBLE_MAX);
        int i = 0;
        for (i = 0; i + (SPECIES_DOUBLE_MAX.length()) <= inputVectorDouble.length; i += SPECIES_DOUBLE_MAX.length()) {
            vecX = DoubleVector.fromArray(SPECIES_DOUBLE_MAX, queryVectorDouble, i);
            vecY = DoubleVector.fromArray(SPECIES_DOUBLE_MAX, inputVectorDouble, i);
            vecDiff = vecX.sub(vecY);
            vecSquare = vecDiff.mul(vecDiff);
            vecSum = vecDiff.fma(vecDiff, vecSum);
        }
        double sum = vecSum.reduceLanes(VectorOperators.ADD);
        for (; i < inputVectorDouble.length; i++) {
            double diff = queryVectorDouble[i] - inputVectorDouble[i];
            sum += diff * diff;
        }
        return sum;
    }

    @Benchmark
    public static float l2SquaredScalar() {
        float squaredDistance = 0;
        for (int i = 0; i < inputVectorFloat.length; i++) {
            float diff = queryVectorFloat[i] - inputVectorFloat[i];
            squaredDistance += diff * diff;
        }
        return squaredDistance;
    }

}
