/*
 * Copyright (c) 2022, 2025, Oracle and/or its affiliates. All rights reserved.
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

package java.lang.classfile.constantpool;

import java.lang.classfile.BootstrapMethodEntry;
import java.lang.classfile.ClassReader;
import java.lang.classfile.attribute.BootstrapMethodsAttribute;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides read access to the constant pool and the bootstrap method table of a
 * {@code class} file.
 *
 * <h2 id="index">Index in the Constant Pool</h2>
 * The constant pool entries are accessed by index.  A valid index is in the
 * range of {@link #size() [1, size())}.  It is {@linkplain PoolEntry#width()
 * unusable} if a {@link LongEntry} or {@link DoubleEntry} is at its previous
 * index.
 *
 * @see BootstrapMethodsAttribute
 * @jvms 4.4 The Constant Pool
 * @sealedGraph
 * @since 24
 */
public sealed interface ConstantPool extends Iterable<PoolEntry>
        permits ClassReader, ConstantPoolBuilder {

    /**
     * {@return the entry at the specified index}
     *
     * @apiNote
     * If only a particular type of entry is expected, use {@link #entryByIndex(
     * int, Class)}.
     *
     * @param index the index within the pool of the desired entry
     * @throws ConstantPoolException if the index is out of range of the
     *         constant pool, or is considered unusable
     */
    PoolEntry entryByIndex(int index);

    /**
     * {@return the exclusive upper bound of the valid indices of this constant
     * pool}  The actual number of entries is lower because {@code 0}, {@code
     * size()} are not valid, and a valid index may be unusable.
     *
     * @see ##index Index in the Constant Pool
     */
    int size();

    /**
     * {@return the entry of a given type at the specified index}
     *
     * @param <T> the entry type
     * @param index the index within the pool of the desired entry
     * @param cls the entry type
     * @throws ConstantPoolException if the index is out of range of the
     *         constant pool or considered unusable, or the entry is not
     *         of the given type
     */
    <T extends PoolEntry> T entryByIndex(int index, Class<T> cls);

    /**
     * {@return an iterator over pool entries}
     *
     * @apiNote
     * This skips any unusable index and is less error-prone than iterating by
     * raw index.  See <em>{@linkplain ##index Index in the Constant Pool}</em>.
     */
    @Override
    default Iterator<PoolEntry> iterator() {
        return new Iterator<>() {
            int index = 1;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public PoolEntry next() {
                if (!hasNext()) throw new NoSuchElementException();
                var e = entryByIndex(index);
                index += e.width();
                return e;
            }
        };
    }

    /**
     * {@return the {@link BootstrapMethodEntry} at the specified index within
     * the bootstrap method table}
     *
     * @param index the index within the bootstrap method table of the desired
     *              entry
     * @throws ConstantPoolException if the index is out of range of the
     *         bootstrap methods
     */
    BootstrapMethodEntry bootstrapMethodEntry(int index);

    /**
     * {@return the number of entries in the bootstrap method table}
     */
    int bootstrapMethodCount();
}
