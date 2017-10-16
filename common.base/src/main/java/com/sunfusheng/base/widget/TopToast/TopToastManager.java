package com.sunfusheng.base.widget.TopToast;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Manages {@link TopToast}s.
 */
class TopToastManager {

    private static final int MSG_TIMEOUT = 0;

    private static final int SHORT_DURATION_MS = 1000;
    private static final int LONG_DURATION_MS = 2750;

    private static TopToastManager sTopToastManager;

    static TopToastManager getInstance() {
        if (sTopToastManager == null) {
            sTopToastManager = new TopToastManager();
        }
        return sTopToastManager;
    }

    private final Object mLock;
    private final Handler mHandler;

    private TopToastRecord mCurrentTopToast;
    private TopToastRecord mNextTopToast;

    private TopToastManager() {
        mLock = new Object();
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_TIMEOUT:
                        handleTimeout((TopToastRecord) message.obj);
                        return true;
                }
                return false;
            }
        });
    }

    interface Callback {
        void show();
        void dismiss(int event);
    }

    public void show(int duration, Callback callback) {
//        synchronized (mLock) {
            if (isCurrentTopToastLocked(callback)) {
                // Means that the callback is already in the queue. We'll just update the duration
                mCurrentTopToast.duration = duration;

                // If this is the TopToast currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentTopToast);
                scheduleTimeoutLocked(mCurrentTopToast);
                return;
            } else if (isNextTopToastLocked(callback)) {
                // We'll just update the duration
                mNextTopToast.duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextTopToast = new TopToastRecord(duration, callback);
            }

//            if (mCurrentTopToast != null && cancelTopToastLocked(mCurrentTopToast,
//                    TopToast.Callback.DISMISS_EVENT_CONSECUTIVE)) {
//                // If we currently have a TopToast, try and cancel it and wait in line
//                return;
//            } else {
//                // Clear out the current TopToast
//                mCurrentTopToast = null;
//                // Otherwise, just show it now
//                showNextTopToastLocked();
//            }
        if (mCurrentTopToast != null) {
            cancelTopToastLocked(mCurrentTopToast,
                    TopToast.Callback.DISMISS_EVENT_CONSECUTIVE);
        }  // Clear out the current TopToast
        mCurrentTopToast = null;
        // Otherwise, just show it now
        showNextTopToastLocked();
//        }
    }

    public void dismiss(Callback callback, int event) {
        synchronized (mLock) {
            if (isCurrentTopToastLocked(callback)) {
                cancelTopToastLocked(mCurrentTopToast, event);
            } else if (isNextTopToastLocked(callback)) {
                cancelTopToastLocked(mNextTopToast, event);
            }
        }
    }

    /**
     * Should be called when a TopToast is no longer displayed. This is after any exit
     * animation has finished.
     */
    public void onDismissed(Callback callback) {
        synchronized (mLock) {
            if (isCurrentTopToastLocked(callback)) {
                // If the callback is from a TopToast currently show, remove it and show a new one
                mCurrentTopToast = null;
                if (mNextTopToast != null) {
                    showNextTopToastLocked();
                }
            }
        }
    }

    /**
     * Should be called when a TopToast is being shown. This is after any entrance animation has
     * finished.
     */
    public void onShown(Callback callback) {
        synchronized (mLock) {
            if (isCurrentTopToastLocked(callback)) {
                scheduleTimeoutLocked(mCurrentTopToast);
            }
        }
    }

    public void cancelTimeout(Callback callback) {
        synchronized (mLock) {
            if (isCurrentTopToastLocked(callback)) {
                mHandler.removeCallbacksAndMessages(mCurrentTopToast);
            }
        }
    }

    public void restoreTimeout(Callback callback) {
        synchronized (mLock) {
            if (isCurrentTopToastLocked(callback)) {
                scheduleTimeoutLocked(mCurrentTopToast);
            }
        }
    }

    public boolean isCurrent(Callback callback) {
        synchronized (mLock) {
            return isCurrentTopToastLocked(callback);
        }
    }

    public boolean isCurrentOrNext(Callback callback) {
        synchronized (mLock) {
            return isCurrentTopToastLocked(callback) || isNextTopToastLocked(callback);
        }
    }

    private static class TopToastRecord {
        private final WeakReference<Callback> callback;
        private int duration;

        TopToastRecord(int duration, Callback callback) {
            this.callback = new WeakReference<>(callback);
            this.duration = duration;
        }

        boolean isTopToast(Callback callback) {
            return callback != null && this.callback.get() == callback;
        }
    }

    private void showNextTopToastLocked() {
        if (mNextTopToast != null) {
            mCurrentTopToast = mNextTopToast;
            mNextTopToast = null;

            final Callback callback = mCurrentTopToast.callback.get();
            if (callback != null) {
                callback.show();
            } else {
                // The callback doesn't exist any more, clear out the TopToast
                mCurrentTopToast = null;
            }
        }
    }

    private boolean cancelTopToastLocked(TopToastRecord record, int event) {
        final Callback callback = record.callback.get();
        if (callback != null) {
            callback.dismiss(event);
            return true;
        }
        return false;
    }

    private boolean isCurrentTopToastLocked(Callback callback) {
        return mCurrentTopToast != null && mCurrentTopToast.isTopToast(callback);
    }

    private boolean isNextTopToastLocked(Callback callback) {
        return mNextTopToast != null && mNextTopToast.isTopToast(callback);
    }

    private void scheduleTimeoutLocked(TopToastRecord r) {
        if (r.duration == TopToast.LENGTH_INDEFINITE) {
            // If we're set to indefinite, we don't want to set a timeout
            return;
        }

        int durationMs = LONG_DURATION_MS;
        if (r.duration > 0) {
            durationMs = r.duration;
        } else if (r.duration == TopToast.LENGTH_SHORT) {
            durationMs = SHORT_DURATION_MS;
        }
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs);
    }

    private void handleTimeout(TopToastRecord record) {
        synchronized (mLock) {
            if (mCurrentTopToast == record || mNextTopToast == record) {
                cancelTopToastLocked(record, TopToast.Callback.DISMISS_EVENT_TIMEOUT);
            }
        }
    }
}
