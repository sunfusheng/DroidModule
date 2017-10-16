package com.sunfusheng.utils.actions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sunfusheng on 2017/5/23.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ActionDispatcher<T> {
    List<T> actionList = new CopyOnWriteArrayList<>();

    public void registerNotify(T notify) {
        if (actionList.contains(notify)) {
            return;
        }

        actionList.add(notify);
    }

    public void unregisterNotify(T notify) {
        if (!actionList.contains(notify)) {
            return;
        }

        actionList.remove(notify);
    }

    public void dispatchAction(Action1<T> action) {
        dispatchAction(actionList, action);
    }

    public <P> void dispatchAction(Action2<T, P> action, P parameter) {
        dispatchAction(actionList, action, parameter);
    }

    public <P1, P2> void dispatchAction(Action3<T, P1, P2> action, P1 parameter1, P2 parameter2) {
        dispatchAction(actionList, action, parameter1, parameter2);
    }

    public <P1, P2, P3> void dispatchAction(Action4<T, P1, P2, P3> action, P1 parameter1, P2 parameter2, P3 parameter3) {
        dispatchAction(actionList, action, parameter1, parameter2, parameter3);
    }

    public <P1, P2, P3, P4> void dispatchAction(Action5<T, P1, P2, P3, P4> action, P1 parameter1, P2 parameter2, P3 parameter3, P4 parameter4) {
        dispatchAction(actionList, action, parameter1, parameter2, parameter3, parameter4);
    }

    static public <T> void dispatchAction(List<T> actionList, Action1<T> action) {
        for (int i = actionList.size() - 1; i >= 0; i--) {
            try {
                T item = actionList.get(i);
                if (item == null) {
                    continue;
                }

                action.call(item);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static public <T, P> void dispatchAction(List<T> actionList, Action2<T, P> action, P parameter) {
        for (int i = actionList.size() - 1; i >= 0; i--) {
            try {
                T item = actionList.get(i);
                if (item == null) {
                    continue;
                }

                action.call(item, parameter);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static public <T, P1, P2> void dispatchAction(List<T> actionList, Action3<T, P1, P2> action, P1 parameter1, P2 parameter2) {
        for (int i = actionList.size() - 1; i >= 0; i--) {
            try {
                T item = actionList.get(i);
                if (item == null) {
                    continue;
                }

                action.call(item, parameter1, parameter2);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static public <T, P1, P2, P3> void dispatchAction(List<T> actionList, Action4<T, P1, P2, P3> action, P1 parameter1, P2 parameter2, P3 parameter3) {
        for (int i = actionList.size() - 1; i >= 0; i--) {
            try {
                T item = actionList.get(i);
                if (item == null) {
                    continue;
                }

                action.call(item, parameter1, parameter2, parameter3);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static public <T, P1, P2, P3, P4> void dispatchAction(List<T> actionList, Action5<T, P1, P2, P3, P4> action, P1 parameter1, P2 parameter2, P3 parameter3, P4 parameter4) {
        for (int i = actionList.size() - 1; i >= 0; i--) {
            try {
                T item = actionList.get(i);
                if (item == null) {
                    continue;
                }

                action.call(item, parameter1, parameter2, parameter3, parameter4);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
