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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsEventHandlerTest {
    private static final int WARMUP_ITERATIONS = 10000;
    private static final int ITERATIONS = 1000;
    private static final int NUM_TIMERS = 15;
    private static final String tfName = "SETF";
    private static final String tfDescription = "The TimerFactorySuite TimerFactory";

    private TimerFactory tf;
    private List<Timer> timers;
    private TimerEventController controller;

    // Coefficients of a 4th degree polynomial with min/max at 3, 7, 10
    // and all values positive for x>0.  Used to calculate a range of delay times
    // for the timers in this test.
    private static int[] coefficients = {3, -80, 726, -2520, 3000};

    private static long eval(int x) {
        long res = 0;
        for (int c : coefficients) {
            res *= x;
            res += c;
        }

        return res;
    }

    private static long delayTime(int timerIndex) {
        return eval(timerIndex) / 100;
    }

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
	{ 1, 4 },
	    { 1, 13 },
	    { 0, 13 },
	    { 1, 5 },
		{ 1, 3 },
		{ 0, 3 },
		{ 1, 12 },
		    { 1, 14 },
			{ 1, 9 },
			    { 1, 8 },
				{ 1, 7 },
				    { 1, 2 },
					{ 1, 2 },
					    { 1, 2 },
					    { 0, 2 },
					{ 0, 2 },
				    { 0, 2 },
				    { 1, 2 },
					{ 1, 1 },
					    { 1, 0 },
					    { 0, 0 },
					{ 0, 1 },
				    { 0, 2 },
				    { 1, 2 },
				    { 0, 2 },
				{ 0, 7 },
			    { 0, 8 },
			{ 0, 9 },
		    { 0, 14 },
		{ 0, 12 },
	    { 0, 5 },
	    { 1, 12 },
		{ 1, 11 },
		    { 1, 10 },
		    { 0, 10 },
		{ 0, 11 },
	    { 0, 12 },
	{ 0, 4 },
    } ;

    private int numEvents = 0;
    private long eventTime = 0;
    private long start;

    private void startCall() {
        start = System.nanoTime();
    }

    private void endCall() {
        numEvents++;
        eventTime += (System.nanoTime() - start);
    }

    private void callTimers() {
        for (int[] op : timerCallData) {
            long start = 0;
            long end = 0;
            int kind = op[0];
            int timerIndex = op[1];
            Timer timer = timers.get(timerIndex);
            if (kind == 1) {
                startCall();
                controller.enter(timer);
                endCall();
        /*
		long delay = delayTime( timerIndex ) ;
		try {
		    Thread.sleep( delay ) ;
		} catch (InterruptedException exc) {
		}
		*/
            } else {
                startCall();
                controller.exit(timer);
                endCall();
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

    @Test
    public void singleThreadedTest() {
        StatsEventHandler seh = tf.makeStatsEventHandler("STSEH");
        controller.register(seh);
        for (int ctr = 0; ctr < WARMUP_ITERATIONS; ctr++) {
            callTimers();
        }

        enableTimers();
        for (int ctr = 0; ctr < ITERATIONS; ctr++) {
            callTimers();
        }
        disableTimers();

        Map<Timer, Statistics> map = seh.stats();
    }

    @Test
    public void multiThreadedTest() {
        StatsEventHandler seh = tf.makeMultiThreadedStatsEventHandler("SEH");
        controller.register(seh);
    }
}
