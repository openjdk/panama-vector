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
 * @summary converted from VM Testbase nsk/jdi/VirtualMachine/redefineClasses/redefineclasses004.
 * VM Testbase keywords: [quick, jpda, jdi, redefine]
 * VM Testbase readme:
 * DESCRIPTION:
 *     The test against the method com.sun.jdi.VirtualMachine.redefineClasses()
 *     and checks up the following assertion:
 *        "If canUnrestrictedlyRedefineClasses() is false, changing the schema
 *        (the fields) will throw UnsupportedOperationException exception"
 *     The test consists of the following files:
 *         redefineclasses004.java             - debugger
 *         redefineclasses004a.java            - initial debuggee
 *         newclassXX/redefineclasses004a.java - redefining debuggees
 *     This test performs the following cases:
 *       1. newclass01 - adding the fields
 *       2. newclass02 - deleting the fields
 *       3. newclass03 - changing field modifiers (adding static)
 *       4. newclass04 - changing field modifiers (adding final)
 *       5. newclass05 - changing field modifiers (adding transient)
 *       6. newclass06 - changing field modifiers (adding volatile)
 *       7. newclass07 - changing field modifiers (changing public to protected)
 *       8. newclass08 - changing field modifiers (changing protected to private)
 *       9. newclass09 - changing field modifiers (changing protected to public)
 *      10. newclass10 - changing field modifiers (changing private to public)
 *     The test checks two different cases for suspended debugee and not
 *     suspended one.
 * COMMENTS:
 *
 * @library /vmTestbase
 *          /test/lib
 * @build nsk.jdi.VirtualMachine.redefineClasses.redefineclasses004
 *        nsk.jdi.VirtualMachine.redefineClasses.redefineclasses004a
 *
 * @comment compile newclassXX to bin/newclassXX
 *          with full debug info
 * @run driver nsk.share.ExtraClassesBuilder
 *      -g:lines,source,vars
 *      newclass01 newclass02 newclass03 newclass04 newclass05 newclass06 newclass07 newclass08 newclass09 newclass10
 *
 * @run driver
 *      nsk.jdi.VirtualMachine.redefineClasses.redefineclasses004
 *      ./bin
 *      -verbose
 *      -arch=${os.family}-${os.simpleArch}
 *      -waittime=5
 *      -debugee.vmkind=java
 *      -transport.address=dynamic
 *      -debugee.vmkeys="${test.vm.opts} ${test.java.opts}"
 */

