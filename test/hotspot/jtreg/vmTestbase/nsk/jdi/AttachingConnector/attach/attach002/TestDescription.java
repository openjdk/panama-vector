/*
 * Copyright (c) 2018, 2024, Oracle and/or its affiliates. All rights reserved.
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


/*
 * @test
 *
 * @summary converted from VM Testbase nsk/jdi/AttachingConnector/attach/attach002.
 * VM Testbase keywords: [quick, jpda, jdi, quarantine]
 * VM Testbase comments: 8054463
 * VM Testbase readme:
 * DESCRIPTION
 *     The test checks that debugger may establish connection with
 *     a target VM via com.sun.jdi.SharedMemoryAttach connector.
 *     The test also analyzes exit code of debugee's process.
 * COMMENT
 *     Test fixed due to bug:
 *     4463674 TEST_BUG: some JDI tests are timing dependent
 *     Test updated to support launching debuggee VM from another JDK
 *     Test was fixed according to test bug:
 *     4778296 TEST_BUG: debuggee VM intemittently hangs after resuming
 *     - debuggee wrapper is used to handle VMStartEvent
 *     - output redirectors are incapsulated in the wrapper
 *     Test was fixed to provide early redirecting of debugee output.
 *     See bug:
 *     4905551 shmemBase_listen failed: Cannot create a file when that file already exists
 *
 * @library /vmTestbase
 *          /test/lib
 * @build nsk.jdi.AttachingConnector.attach.attach002
 *        nsk.jdi.AttachingConnector.attach.attach002t
 * @run main/othervm
 *      nsk.jdi.AttachingConnector.attach.attach002
 *      -verbose
 *      -arch=${os.family}-${os.simpleArch}
 *      -waittime=5
 *      -debugee.vmkind=java
 *      -transport.address=dynamic
 *      -debugee.vmkeys="${test.vm.opts} ${test.java.opts}"
 */

