package com.sunfusheng.base.db;

import java.util.ArrayList;
import java.util.List;

import io.realm.Sort;

public class DbQueryParams {

    public int limit;
    public int offset;
    public List<SelectionArgs> selectionArgs;
    public SortArgs sortArgs;
    public Class clazz;

    public DbQueryParams(Class clazz) {
        this.clazz = clazz;
        this.limit = -1;
        this.offset = 0;
        this.selectionArgs = new ArrayList<>();
    }

    public void appendSelection(SelectionArgs args) {
        selectionArgs.add(args);
    }

    public enum SelectionType {
        LESS(0),
        EQUAL(1),
        GREATER(2),
        ISNULL(3),
        IN(4);
        private final int value;

        SelectionType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class SelectionArgs {
        SelectionType type;
        String filedName;
        Object filedValue;

        public SelectionArgs(SelectionType type, String filedName, Object filedValue) {
            this.filedName = filedName;
            this.type = type;
            this.filedValue = filedValue;
        }
    }

    public static class SortArgs {
        String filedName;
        Sort sort;

        public SortArgs(String filedName, Sort sort) {
            this.filedName = filedName;
            this.sort = sort;
        }

        public SortArgs(String filedName) {
            this(filedName, Sort.ASCENDING);
        }
    }
}
