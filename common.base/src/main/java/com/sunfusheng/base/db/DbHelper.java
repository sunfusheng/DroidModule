package com.sunfusheng.base.db;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DbHelper<T extends RealmObject> {

    private static final Object lock = new Object();

    private void equalTo(RealmQuery where, String filed, Object value) {
        if (value instanceof String) {
            where.equalTo(filed, (String) value);
        } else if (value instanceof Boolean) {
            where.equalTo(filed, (Boolean) value);
        } else if (value instanceof Integer) {
            where.equalTo(filed, (Integer) value);
        } else if (value instanceof Long) {
            where.equalTo(filed, (Long) value);
        }
    }

    private void lessThan(RealmQuery where, String filed, Object value) {
        if (value instanceof Long) {
            where.lessThan(filed, (long) value);
        } else if (value instanceof Integer) {
            where.lessThan(filed, (int) value);
        }
    }

    private void greaterThan(RealmQuery where, String filed, Object value) {
        if (value instanceof Long) {
            where.greaterThan(filed, (long) value);
        } else if (value instanceof Integer) {
            where.greaterThan(filed, (int) value);
        }
    }

    private void in(RealmQuery where, String filed, Object value) {
        if (value instanceof String[]) {
            where.in(filed, (String[]) value);
        } else if (value instanceof Integer[]) {
            where.in(filed, (Integer[]) value);
        }
    }

    private List<T> getListOffsetAndLimit(List<T> rawList, int offset, int limit) {
        List<T> list = new ArrayList<T>();
        if (rawList == null) {
            return list;
        }
        if (offset <= 0) {
            offset = 0;
        }
        if (limit <= 0) {
            limit = rawList.size();
        }
        int size = rawList.size();
        if (size <= offset) {
            return list;
        }
        if ((offset + limit) > size) {
            limit = size - offset;
        }
        for (int i = offset; i < limit + offset; i++) {
            list.add(rawList.get(i));
        }
        return list;
    }

    private void querySelections(RealmQuery query, List<DbQueryParams.SelectionArgs> selectionArgs) {
        for (DbQueryParams.SelectionArgs sel : selectionArgs) {
            if (sel.type.getValue() == DbQueryParams.SelectionType.EQUAL.getValue()) {
                equalTo(query, sel.filedName, sel.filedValue);
            } else if (sel.type.getValue() == DbQueryParams.SelectionType.LESS.getValue()) {
                lessThan(query, sel.filedName, sel.filedValue);
            } else if (sel.type.getValue() == DbQueryParams.SelectionType.GREATER.getValue()) {
                greaterThan(query, sel.filedName, sel.filedValue);
            } else if (sel.type.getValue() == DbQueryParams.SelectionType.ISNULL.getValue()) {
                query.isNull(sel.filedName);
            } else if (sel.type.getValue() == DbQueryParams.SelectionType.IN.getValue()) {
                in(query, sel.filedName, sel.filedValue);
            }
        }
    }

    protected void insertOrUpdate(T model) {
        synchronized (lock) {
            Realm realm = null;
            try {
                if (model == null) {
                    return;
                }
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.insertOrUpdate(model);
                realm.commitTransaction();
            } catch (Throwable e) {
                if (realm != null) {
                    realm.cancelTransaction();
                }
                e.printStackTrace();
            } finally {
                if (realm != null) realm.close();
            }
        }
    }

    protected void insertOrUpdate(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        synchronized (lock) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.insertOrUpdate(list);
                realm.commitTransaction();
            } catch (Throwable e) {
                if (realm != null) {
                    realm.cancelTransaction();
                }
                e.printStackTrace();
            } finally {
                if (realm != null) realm.close();
            }
        }
    }

    protected void deleteAll(DbQueryParams params) {
        synchronized (lock) {
            if (params == null || params.clazz == null) {
                return;
            }
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                RealmQuery query = realm.where(params.clazz);
                querySelections(query, params.selectionArgs);
                RealmResults<T> result = getResult(params, realm);
                List<T> list = getListOffsetAndLimit(result, params.offset, params.limit);
                for (T t : list) {
                    t.deleteFromRealm();
                }
                realm.commitTransaction();
            } catch (Throwable t) {
                if (realm != null) {
                    realm.cancelTransaction();
                }
                t.printStackTrace();
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }
    }

    protected List<T> queryAll(DbQueryParams params) {
        List<T> list = new ArrayList<>();
        if (params == null || params.clazz == null || params.selectionArgs == null ||
                params.selectionArgs.size() == 0) {
            return list;
        }
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<T> result = getResult(params, realm);
            list.addAll(result);
            list = getListOffsetAndLimit(list, params.offset, params.limit);
            list = realm.copyFromRealm(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    protected T queryFirst(DbQueryParams params) {
        if (params == null || params.clazz == null || params.selectionArgs == null ||
                params.selectionArgs.size() == 0) {
            return null;
        }
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<T> result = getResult(params, realm);
            if (result.size() == 0) {
                return null;
            }
            return realm.copyFromRealm(result.first());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    protected long count(DbQueryParams params) {
        if (params == null || params.clazz == null) {
            return 0L;
        }
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<T> result = getResult(params, realm);
            int size = result.size();
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @NonNull
    private RealmResults<T> getResult(DbQueryParams params, Realm realm) {
        RealmQuery<T> query = realm.where(params.clazz);
        querySelections(query, params.selectionArgs);
        RealmResults<T> result = query.findAll();
        if (params.sortArgs != null) {
            result = result.sort(params.sortArgs.filedName, params.sortArgs.sort);
        }
        return result;
    }
}
