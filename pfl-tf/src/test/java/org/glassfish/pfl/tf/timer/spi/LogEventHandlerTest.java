/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

// import corba.framework.TimerUtils ;

public class LogEventHandlerTest {
    private static final int NUM_TIMERS = 15;
    private static final String tfName = "LEHTF";
    private static final String tfDescription = "The TimerFactorySuite TimerFactory";

    private TimerFactory tf;
    private List<Timer> timers;
    private TimerEventController controller;

    // Timer calling order for 1 test cycle
    // Data format is (flag, timerIndex) where flag 0 is exit, 1 is enter
    // 0    called  1
    // 1    called  1
    // 2    called  5
    // 3    called  1
    // 4    called  1
    // 5    called  1
    // 6    called  0
    // 7    called  1
    // 8    called  1
    // 9    called  1
    // 10   called  1
    // 11   called  1
    // 12   called  2
    // 13   called  1
    // 14   called  1
    // total 19 calls
    private static int[][] timerCallData = {
            {1, 4},
            {1, 13},
            {0, 13},
            {1, 5},
            {1, 3},
            {0, 3},
            {1, 12},
            {1, 14},
            {1, 9},
            {1, 8},
            {1, 7},
            {1, 2},
            {1, 2},
            {1, 2},
            {0, 2},
            {0, 2},
            {0, 2},
            {1, 2},
            {1, 1},
            {1, 0},
            {0, 0},
            {0, 1},
            {0, 2},
            {1, 2},
            {0, 2},
            {0, 7},
            {0, 8},
            {0, 9},
            {0, 14},
            {0, 12},
            {0, 5},
            {1, 12},
            {1, 11},
            {1, 10},
            {0, 10},
            {0, 11},
            {0, 12},
            {0, 4},
    };

    private void checkLogEventHandler(LogEventHandler leh, int size) {
        int ctr = 0;
        boolean done = false;
        for (TimerEvent te : leh) {
            Assert.assertFalse(done);
            if (ctr >= size) {
                done = true;
            }

            int[] data = timerCallData[ctr];

            if (data[0] == 0) {
                Assert.assertTrue(te.type() == TimerEvent.TimerEventType.EXIT);
            } else {
                Assert.assertTrue(te.type() == TimerEvent.TimerEventType.ENTER);
            }

            Assert.assertTrue(te.timer().equals(timers.get(data[1])));
            ctr++;
        }

        Assert.assertEquals(ctr, size);
    }

    private void callTimers() {
        for (int[] op : timerCallData) {
            int kind = op[0];
            int timerIndex = op[1];
            Timer timer = timers.get(timerIndex);
            if (kind == 1) {
                controller.enter(timer);
            } else {
                controller.exit(timer);
            }
        }
    }

    @Before
    public void setUp() {
        timers = new ArrayList<Timer>();
        tf = TimerFactoryBuilder.make(tfName, tfDescription);
        for (int ctr = 0; ctr < NUM_TIMERS; ctr++) {
            Timer timer = tf.makeTimer("t" + ctr, "Timer " + ctr);
            timers.add(timer);
        }
        controller = tf.makeController("Controller");
    }

    private void enableTimers() {
        for (Timer timer : timers) {
            timer.enable();
        }
    }

    private void disableTimers() {
        for (Timer timer : timers) {
            timer.disable();
        }
    }

    @After
    public void tearDown() {
        TimerFactoryBuilder.destroy(tf);
    }

    @Test()
    public void singleThreadedTest() {
        LogEventHandler leh = tf.makeLogEventHandler("STLEH");
        controller.register(leh);
        enableTimers();
        callTimers();
        disableTimers();

        checkLogEventHandler(leh, timerCallData.length);
        leh.clear();
        checkLogEventHandler(leh, 0);
    }
}
