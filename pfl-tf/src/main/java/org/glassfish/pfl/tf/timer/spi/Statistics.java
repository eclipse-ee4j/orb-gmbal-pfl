/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi ;

/** A simple read-only holder for accumulated statistics.
 */
public class Statistics {
    private final long count ;
    private final double min ;
    private final double max ;
    private final double average ;
    private final double standardDeviation ;

    public Statistics( long count, double min, double max,
	double average, double standardDeviation ) {

	this.count = count ;
	this.min = min ;
	this.max = max ;
	this.average = average ;
	this.standardDeviation = standardDeviation ;
    }

    /** Return the number of data points recorded.
     */
    public long count() { return count ; }

    /** Return the minimum value of call data points records.
     */
    public double min() { return min ; }

    /** Return the maximum value of call data points records.
     */
    public double max() { return max ; }

    /** Return the current average of the data, or -1 if there is no
     * data.
     */
    public double average() { return average ; }

    /** Return the standard deviation of the data, or -1 if there is
     * no data.
     */
    public double standardDeviation() { return standardDeviation ; }

    @Override
    public boolean equals( Object obj ) {
	if (obj == this) {
            return true;
        }

	if (!(obj instanceof Statistics)) {
            return false;
        }

	Statistics other = Statistics.class.cast( obj ) ;
	return (count==other.count()) &&
	    (min==other.min()) &&
	    (max==other.max()) &&
	    (average==other.average()) &&
	    (standardDeviation==other.standardDeviation()) ;
    }

    @Override
    public int hashCode() {
	double sum = min+max+average+standardDeviation ;
	sum += count ;
	Double dsum = Double.valueOf( sum ) ;
	return dsum.hashCode() ;
    }
}
