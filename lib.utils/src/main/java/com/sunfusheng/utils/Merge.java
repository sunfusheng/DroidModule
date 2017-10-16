package com.sunfusheng.utils;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Merge {
    public static class Diff {
        public interface Op {
            int KEEP = 0;
            int INC = 1;
            int DEC = 2;
        }

        public int op;
        public Object obj;

        public Diff(int op, Object obj) {
            this.op = op;
            this.obj = obj;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Diff)) {
                return false;
            }

            Diff diff = (Diff) o;
            return this.obj.equals(diff.obj) && this.op == diff.op;
        }
    }

    private static int[][] lengthOfLCS(Object[] X, Object[] Y) {
        int[][] lengthTable = new int[X.length + 1][Y.length + 1];

        for (int i = 1; i <= X.length; i++) {
            for (int j = 1; j <= Y.length; j++) {
                if (X[i - 1].equals(Y[j - 1])) {
                    lengthTable[i][j] = lengthTable[i - 1][j - 1] + 1;
                } else if (lengthTable[i - 1][j] >= lengthTable[i][j - 1]) {
                    lengthTable[i][j] = lengthTable[i - 1][j];
                } else {
                    lengthTable[i][j] = lengthTable[i][j - 1];
                }
            }
        }

        return lengthTable;
    }

    static private class LCSContext {
        public int i;
        public int j;
        public int s;

        public LCSContext(int i, int j) {
            this.i = i;
            this.j = j;
            this.s = 0;
        }
    }

    public static List<Pair<Integer, Integer>> lcs(Object[] X, Object[] Y) {
        List<Pair<Integer, Integer>> result = new ArrayList<>(Math.max(X.length, Y.length));
        int[][] lcsLen = lengthOfLCS(X, Y);

        LinkedList<LCSContext> contextStack = new LinkedList<>();
        contextStack.push(new LCSContext(X.length, Y.length));

        while (contextStack.size() != 0) {
            LCSContext context = contextStack.pop();

            switch (context.s) {
                case 0:
                    if (context.i == 0 || context.j == 0) {
                        continue;
                    }

                    if (X[context.i - 1].equals(Y[context.j - 1])) {
                        context.s = 1;
                        contextStack.push(context);
                        contextStack.push(new LCSContext(context.i - 1, context.j - 1));
                    } else if (lcsLen[context.i - 1][context.j] >= lcsLen[context.i][context.j - 1]) {
                        contextStack.push(new LCSContext(context.i - 1, context.j));
                    } else {
                        contextStack.push(new LCSContext(context.i, context.j - 1));
                    }
                    break;
                case 1:
                    result.add(new Pair<>(context.i - 1, context.j - 1));
                    break;
            }
        }

        return result;
    }

    public static Object[] diff(Object[] original, Object[] modified) {
        List<Diff> diffs = new ArrayList<>(original.length + modified.length);
        List<Pair<Integer, Integer>> lcs = lcs(original, modified);
        lcs.add(new Pair<>(original.length, modified.length));

        int i = 0, j = 0;
        for (Pair<Integer, Integer> sp : lcs) {
            for (; i < sp.first; i++) {
                diffs.add(new Diff(Diff.Op.DEC, original[i]));
            }

            for (; j < sp.second; j++) {
                diffs.add(new Diff(Diff.Op.INC, modified[j]));
            }

            i = sp.first + 1;
            j = sp.second + 1;

            if (sp.first != original.length) {
                diffs.add(new Diff(Diff.Op.KEEP, original[sp.first]));
            }
        }

        return diffs.toArray();
    }

    public static Object[] reconstruct(Object[] diffs) {
        List<Object> reconstructed = new ArrayList<>(diffs.length);
        for (Object o : diffs) {
            Diff diff = (Diff) o;
            switch (diff.op) {
                case Diff.Op.INC:
                case Diff.Op.KEEP:
                    reconstructed.add(diff.obj);
                    break;
                case Diff.Op.DEC:
                    break;
            }
        }

        return reconstructed.toArray();
    }

    public static Object[] merge(Object[] original, Object[] mine, Object[] theirs) {
        Object[] d1 = diff(original, mine);
        Object[] d2 = diff(original, theirs);
        List<Object> patch = new ArrayList<>(d1.length + d2.length);

        Map<Object, Integer> d1Map = new HashMap<>(d1.length);
        for (Object o : d1) {
            Diff diff = (Diff) o;
            d1Map.put(diff.obj, diff.op);
        }
        Map<Object, Integer> d2Map = new HashMap<>(d2.length);
        for (Object o : d2) {
            Diff diff = (Diff) o;
            d2Map.put(diff.obj, diff.op);
        }

        List<Pair<Integer, Integer>> lcs = lcs(d1, d2);
        lcs.add(new Pair<>(d1.length, d2.length));

        int i = 0, j = 0;
        for (Pair<Integer, Integer> sp : lcs) {
            for (; i < sp.first; i++) {
                boolean conflict = false;
                switch (((Diff) d1[i]).op) {
                    case Diff.Op.KEEP:
                        if (!d2Map.containsKey(((Diff) d1[i]).obj))
                            break;

                        if (d2Map.get(((Diff) d1[i]).obj) != Diff.Op.KEEP)
                            conflict = true;

                        break;
                    default:
                        break;
                }

                if (!conflict) {
                    patch.add(d1[i]);
                }
            }

            for (; j < sp.second; j++) {
                boolean conflict = false;
                switch (((Diff) d2[j]).op) {
                    case Diff.Op.KEEP:
                        conflict = true;
                        break;
                    default:
                        if (!d1Map.containsKey(((Diff) d2[j]).obj))
                            break;

                        if (d1Map.get(((Diff) d2[j]).obj) != Diff.Op.KEEP)
                            conflict = true;

                        break;
                }

                if (!conflict) {
                    patch.add(d2[j]);
                }
            }

            i = sp.first + 1;
            j = sp.second + 1;

            if (sp.first != d1.length) {
                patch.add(d1[sp.first]);
            }
        }

        return reconstruct(patch.toArray());
    }
}
