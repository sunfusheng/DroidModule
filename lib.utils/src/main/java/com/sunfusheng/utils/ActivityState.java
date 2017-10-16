package com.sunfusheng.utils;

/**
 * A set of states that represent the last state change of an Activity.
 */
public interface ActivityState {
    /**
     * Represents Activity#onCreate().
     */
    public final int CREATED = 1;

    /**
     * Represents Activity#onStart().
     */
    public final int STARTED = 2;

    /**
     * Represents Activity#onResume().
     */
    public final int RESUMED = 3;

    /**
     * Represents Activity#onPause().
     */
    public final int PAUSED = 4;

    /**
     * Represents Activity#onStop().
     */
    public final int STOPPED = 5;

    /**
     * Represents Activity#onDestroy().  This is also used when the state of an Activity is unknown.
     */
    public final int DESTROYED = 6;
}
