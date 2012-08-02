/**
 * Request.java
 * 
 * @author	Jose Luis Ugia
 */

package ugia.ants.engine.core;

import java.util.concurrent.Callable;

import android.util.Log;

public class AntRequest implements Callable<Double>, Comparable<AntRequest> {

    private static final int ANT_LOAD_WEIGHT_RATIO = 35;
    private static final int ANT_AVERAGE_SPEED_METERS_PER_HOUR = 300;

    private final double antWeight_Mg;
    private double foodWillCarry_Mg;
    private final int distance_M;
    private int timeTaken_H;

    private final int ant_no;

    public int priority;

    /**
     * Class constructor
     * 
     * @param c
     *            Execution context
     */
    public AntRequest(double _weight, int _distance, int _ant_no) {
	antWeight_Mg = _weight;
	distance_M = _distance;
	ant_no = _ant_no;
    }

    /**
     * Method that executes the task
     * 
     * @return Request status code
     */
    @Override
    public Double call() throws Exception {

	// Calculate the amount of food this ant will carry
	foodWillCarry_Mg = antWeight_Mg * ANT_LOAD_WEIGHT_RATIO;

	// Add +-25% error
	foodWillCarry_Mg += foodWillCarry_Mg * (Math.random() / 2 - 0.25);

	// Calculate the time taken to go back and forth and sleep the thread
	timeTaken_H = distance_M / ANT_AVERAGE_SPEED_METERS_PER_HOUR * 3600;

	// Add +-12.5% error
	timeTaken_H += timeTaken_H * (Math.random() / 4 - 0.125);

	// Seconds will be taken as milliseconds/10 for testing reasons
	Thread.sleep(timeTaken_H / 10);

	Log.v("Ant #" + (ant_no + 1) + " Extra food (mg)", Double.toString(foodWillCarry_Mg));

	return foodWillCarry_Mg;
    }

    /**
     * Compares requests priority
     * 
     * @param another
     *            Comparing request
     * @return Relative order against another
     */
    @Override
    public int compareTo(AntRequest another) {
	if (priority < another.priority)
	    return -1;
	else if (priority > another.priority)
	    return 1;
	else
	    return 0;
    }

}