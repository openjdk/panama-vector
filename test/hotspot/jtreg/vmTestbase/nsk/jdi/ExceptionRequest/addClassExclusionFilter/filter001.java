/*
 * Copyright (c) 2001, 2024, Oracle and/or its affiliates. All rights reserved.
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

package nsk.jdi.ExceptionRequest.addClassExclusionFilter;

import nsk.share.*;
import nsk.share.jpda.*;
import nsk.share.jdi.*;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import java.util.*;
import java.io.*;

/**
 * The test for the implementation of an object of the type
 * ExceptionRequest.
 *
 * The test checks that results of the method
 * <code>com.sun.jdi.ExceptionRequest.addClassExclusionFilter()</code>
 * complies with its spec.
 *
 * The test checks up on the following assertion:
 *    Restricts the events generated by this request to those
 *    whose location is in a class whose name does not match a
 *    restricted regular expression.
 * The cases to check include both a pattern that begins with '*' and
 * one that end with '*'.
 *
 * The test works as follows.
 * - The debugger
 *   - sets up two ExceptionRequests,
 *   - restricts the Requests using patterns that begins with '*' and
 *     ends with *, so that, events will be filtered only from thread1,
 *   - resumes the debuggee, and
 *   - waits for expected ExceptionEvents.
 * - The debuggee creates and starts two threads, thread1 and thread2,
 *   that being run, throw NullPointerExceptions used
 *   to generate Events and to test the filters.
 * - Upon getting the events, the debugger performs checks required.
 */

public class filter001 extends TestDebuggerType1 {

    public static void main (String argv[]) {
        int result = run(argv,System.out);
        if (result != 0) {
            throw new RuntimeException("TEST FAILED with result " + result);
        }
    }

    public static int run (String argv[], PrintStream out) {
        debuggeeName = "nsk.jdi.ExceptionRequest.addClassExclusionFilter.filter001a";
        return new filter001().runThis(argv, out);
    }

    private String testedClassName1 = "TestClass11";
    private String testedClassName2 = "nsk.jdi.ExceptionRequest.addClassExclusionFilter.Thread2filter001a";

    protected void testRun() {

        String property1 = "ExceptionRequest1";
        String property2 = "ExceptionRequest2";

        Event newEvent = null;

        for (int i = 0; ; i++) {

            if (!shouldRunAfterBreakpoint()) {
                vm.resume();
                break;
            }

            display(":::::: case: # " + i);

            switch (i) {

                case 0:
                final EventRequest eventRequest1 = setting23ExceptionRequest( null,
                                                           "*" + testedClassName1,
                                                           EventRequest.SUSPEND_NONE,
                                                           property1);
                eventRequest1.enable();
                eventHandler.addListener(
                     new EventHandler.EventListener() {
                         public boolean eventReceived(Event event) {
                            if (event instanceof ExceptionEvent && event.request().equals(eventRequest1)) {
                                String str = ((ExceptionEvent)event).location().declaringType().name();
                                if (str.endsWith(testedClassName1)) {
                                    setFailedStatus("eventRequest1: Received unexpected ExceptionEvent for excluded class:" + str);
                                } else {
                                    display("eventRequest1: Received expected ExceptionEvent for " + str);
                                }
                                return true;
                            }
                            return false;
                         }
                     }
                );

                display("......waiting1 for ExceptionEvent in expected thread");
                vm.resume();
                break;

                case 1:
                final EventRequest eventRequest2 = setting23ExceptionRequest( null,
                                                           testedClassName2 + "*",
                                                           EventRequest.SUSPEND_NONE,
                                                           property2);
                eventRequest2.enable();
                eventHandler.addListener(
                     new EventHandler.EventListener() {
                         public boolean eventReceived(Event event) {
                            if (event instanceof ExceptionEvent && event.request().equals(eventRequest2)) {
                                String str = ((ExceptionEvent)event).location().declaringType().name();
                                if (str.endsWith(testedClassName2)) {
                                    setFailedStatus("eventRequest2: Received ExceptionEvent for excluded class:" + str);
                                } else {
                                    display("eventRequest2: Received expected ExceptionEvent for " + str);
                                }
                                return true;
                            }
                            return false;
                         }
                     }
                );

                display("......waiting for ExceptionEvent in expected thread");
                vm.resume();
                break;

                default:
                throw new Failure("** default case 2 **");
            }
        }
        return;
    }

    private ExceptionRequest setting23ExceptionRequest ( ThreadReference thread,
                                                         String          testedClass,
                                                         int             suspendPolicy,
                                                         String          property       ) {
        try {
            display("......setting up ExceptionRequest:");
            display("       thread: " + thread + "; class exclude filter: " + testedClass + "; property: " + property);

            ExceptionRequest
            excr = eventRManager.createExceptionRequest(null, true, true);
            excr.putProperty("number", property);
            if (thread != null)
                excr.addThreadFilter(thread);
            excr.addClassExclusionFilter(testedClass);
            excr.setSuspendPolicy(suspendPolicy);

            display("      ExceptionRequest has been set up");
            return excr;
        } catch ( Exception e ) {
            throw new Failure("** FAILURE to set up ExceptionRequest **");
        }
    }
}
