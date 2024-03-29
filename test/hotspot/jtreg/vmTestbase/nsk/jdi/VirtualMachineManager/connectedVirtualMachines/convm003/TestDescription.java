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
 * @summary converted from VM Testbase nsk/jdi/VirtualMachineManager/connectedVirtualMachines/convm003.
 * VM Testbase keywords: [quick, jpda, jdi]
 * VM Testbase readme:
 * DESCRIPTION:
 *     The test for the implementation of an object of the type
 *     VirtualMachineManager.
 *     The test checks up that a result of the method
 *     com.sun.jdi.VirtualMachineManager.connectedVirtualMachines()
 *     complies with its spec:
 *     public List connectedVirtualMachines()
 *     Lists all target VMs which are connected to the debugger.
 *     The list includes VirtualMachine instances for any
 *     target VMs which initiated a connection and any target VMs to which
 *     this manager has initiated a connection.
 *     A target VM will remain in this list until the VM is disconnected.
 *     VMDisconnectEvent is placed in the event queue after the VM is removed from the list.
 *     Returns: a list of VirtualMachine objects, each mirroring a target VM.
 *     The tested case includes invalidating a gebuggee by a debugger with
 *     the method VirtualMachine.dispose().
 *     The test works as follows:
 *     The debugger program - nsk.jdi.VirtualMachineManager.connectedVirtualMachines.convm003;
 *     the debuggee program - nsk.jdi.VirtualMachineManager.connectedVirtualMachines.convm003a.
 *     Using nsk.jdi.share classes,
 *     the debugger gets the debuggee running on another JavaVM,
 *     creates the object debuggee.VM,
 *     establishes a pipe with the debuggee program, and then
 *     send to the programm commands, to which the debuggee replies
 *     via the pipe. Upon getting reply,
 *     the debugger calls to the method VirtualMachine.exit() to get debuggee's death
 *     and checks up on the list of connected VM.
 *     In case of error the test produces the return value 97 and
 *     a corresponding error message(s).
 *     Otherwise, the test is passed and produces
 *     the return value 95 and no message.
 * COMMENTS:
 *     Test was fixed according to test bug:
 *     4778296 TEST_BUG: debuggee VM intemittently hangs after resuming
 *     - waiting for VMStartEvent was removed from the debugger part of the test
 *     - waiting for debuggee VM exit was added after invocation of vm.dispose()
 *
 * @library /vmTestbase
 *          /test/lib
 * @build nsk.jdi.VirtualMachineManager.connectedVirtualMachines.convm003
 *        nsk.jdi.VirtualMachineManager.connectedVirtualMachines.convm003a
 * @run driver
 *      nsk.jdi.VirtualMachineManager.connectedVirtualMachines.convm003
 *      -verbose
 *      -arch=${os.family}-${os.simpleArch}
 *      -waittime=5
 *      -debugee.vmkind=java
 *      -transport.address=dynamic
 *      -debugee.vmkeys="${test.vm.opts} ${test.java.opts}"
 */

