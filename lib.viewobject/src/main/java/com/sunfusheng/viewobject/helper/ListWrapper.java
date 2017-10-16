package com.sunfusheng.viewobject.helper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListWrapper<T> implements List<T> {
    private List<T> list;
    private boolean isModified = false;

    public ListWrapper(List<T> list) {
        if (list == null) {
            throw new RuntimeException("ListWrapper: Empty list not allowed here.");
        }
        this.list = list;
    }

    private List<T> listToWrite() {
        if (isModified) {
            return this.list;
        }

        this.list = new ArrayList<>(this.list);
        this.isModified = true;
        return this.list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return listToWrite().contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return listToWrite().iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return listToWrite().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return listToWrite().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return listToWrite().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return listToWrite().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return listToWrite().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return listToWrite().retainAll(c);
    }

    @Override
    public void clear() {
        listToWrite().clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        return listToWrite().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        listToWrite().add(index, element);
    }

    @Override
    public T remove(int index) {
        return listToWrite().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listToWrite().listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return listToWrite().listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return listToWrite().subList(fromIndex, toIndex);
    }
}
