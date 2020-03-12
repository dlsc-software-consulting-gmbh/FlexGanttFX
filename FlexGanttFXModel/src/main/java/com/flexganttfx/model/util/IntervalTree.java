/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * An interval tree implementation to store activities based on their start and
 * end time.
 *
 * @see IntervalTreeActivityRepository
 *
 * @param <A>
 *            the activity type
 */
public class IntervalTree<A extends Activity> {

    private Entry<A> root;
    private int treeSize;

    /**
     * Returns the earliest time used by all activities currently stored inside
     * the tree.
     *
     * @return the earliest time used
     */
    public final Instant getEarliestTimeUsed() {
        if (root != null) {
            return Instant.ofEpochMilli(getEarliestTimeUsed(root));
        }

        return null;
    }

    private long getEarliestTimeUsed(Entry<A> entry) {
        if (entry.getLeft() != null) {
            return getEarliestTimeUsed(entry.getLeft());
        }

        return entry.low;
    }

    /**
     * Returns the latest time used by all activities currently stored inside
     * the tree.
     *
     * @return the latest time used
     */
    public final Instant getLatestTimeUsed() {
        if (root != null) {
            return Instant.ofEpochMilli(getLatestTimeUsed(root));
        }

        return null;
    }

    private long getLatestTimeUsed(Entry<A> entry) {
        if (entry.getRight() != null) {
            return getLatestTimeUsed(entry.getRight());
        }

        return entry.high;
    }

    /**
     * Adds an activity to the tree.
     *
     * @param activity
     *            the activity to add
     * @return true if the activity could be added
     */
    public final boolean add(A activity) {
        Entry<A> entry = addEntry(activity);
        return entry != null;
    }

    /**
     * Method to remove period/key object from tree. Entry to delete will be
     * found by period and key values of given activity (not by given object
     * reference).
     *
     * @param activity
     *            the activity to remove
     * @return true if the activity was a member of this tree
     */
    public final boolean remove(A activity) {

        Entry<A> entry = getEntry(activity);

        if (entry == null) {
            return false;
        } else {
            deleteEntry(entry);
        }

        return true;
    }

    public final boolean removeIf(Predicate<A> predicate) {
        // TODO: implement
        return true;
    }

    /**
     * Removes all activities found within the given time interval.
     *
     * @param interval
     *            the time interval
     * @return the removed activities
     */
    public final Collection<A> removePeriod(TimeInterval interval) {
        Collection<A> result = getIntersectingObjects(interval);

        for (A activity : result) {
            deleteEntry(getEntry(activity));
        }

        return result;
    }

    /**
     * Returns all activities found within the given time interval.
     *
     * @param interval
     *            the time interval
     * @return the found activities
     */
    public final Collection<A> getIntersectingObjects(TimeInterval interval) {
        return getIntersectingObjects(interval.getStartTime().toEpochMilli(), interval.getEndTime().toEpochMilli());
    }

    /**
     * Returns all activities found within the given time interval.
     *
     * @param start
     *            the time interval start
     * @param end
     *            the time interval end
     * @return the found activities
     */
    public final Collection<A> getIntersectingObjects(long start, long end) {
        Collection<A> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        searchIntersecting(root, start, end, result);

        return result;
    }

    private void searchIntersecting(Entry<A> entry, long start, long end, Collection<A> result) {
        // Don't search nodes that don't exist
        if (entry == null) {
            return;
        }

        long pLow = start;
        long pHigh = end;

        // If p is to the right of the rightmost point of any interval
        // in this node and all children, there won't be any matches.
        if (entry.maxHigh < pLow) {
            return;
        }

        // Search left children
        if (entry.left != null) {
            searchIntersecting(entry.left, start, end, result);
        }

        // Check this node
        if (checkPLow(entry, pLow) || checkPHigh(entry, pHigh) || (pLow <= entry.low && entry.high <= pHigh)) {
            result.add(entry.value);
        }

        // If p is to the left of the start of this interval,
        // then it can't be in any child to the right.
        if (pHigh < entry.low) {
            return;
        }

        // Otherwise, search right children
        if (entry.right != null) {
            searchIntersecting(entry.right, start, end, result);
        }
    }

    private boolean checkPLow(Entry<A> n, long pLow) {
        return n.low <= pLow && n.high > pLow;
    }

    private boolean checkPHigh(Entry<A> n, long pHigh) {
        return n.low < pHigh && n.high >= pHigh;
    }

    /**
     * Returns the number of activities stored inside the tree.
     *
     * @return the tree size
     */
    public final long size() {
        return treeSize;
    }

    /**
     * Removes all activities from the tree.
     */
    public final void clear() {
        treeSize = 0;
        root = null;
    }

    private long getLow(Activity obj) {
        return obj.getStartTime() == null ? Long.MIN_VALUE
                : obj.getStartTime().toEpochMilli();
    }

    private long getHigh(Activity obj) {
        return obj.getEndTime() == null ? Long.MAX_VALUE
                : obj.getEndTime().toEpochMilli();
    }

    private void fixUpMaxHigh(Entry<A> x) {
        while (x != null) {
            x.maxHigh = Math.max(x.high,
                    Math.max(x.left != null ? x.left.maxHigh
                            : Long.MIN_VALUE,
                            x.right != null ? x.right.maxHigh
                                    : Long.MIN_VALUE));
            x = x.parent;
        }
    }

    /**
     * Method to find entry by period. Period start, period end and object key
     * are used to identify each entry.
     *
     * @param activity
     *            the activity for which to find the entry
     * @return appropriate entry, or null if not found
     */
    private Entry<A> getEntry(Activity activity) {
        Entry<A> t = root;
        while (t != null) {
            int cmp = compareLongs(getLow(activity), t.low);
            if (cmp == 0)
                cmp = compareLongs(getHigh(activity), t.high);
            if (cmp == 0)
                cmp = activity.hashCode() - t.value.hashCode();

            if (cmp < 0) {
                t = t.left;
            } else if (cmp > 0) {
                t = t.right;
            } else {
                return t;
            }
        }

        return null;
    }

    private Entry<A> addEntry(A activity) {
        if (activity == null) throw new IllegalArgumentException("null element is not supported");

        Entry<A> t = root;
        if (t == null) {
            root = new Entry<>(getLow(activity), getHigh(activity), activity,
                    null);
            treeSize = 1;
            return root;
        }
        long cmp;
        Entry<A> parent;

        do {
            parent = t;
            cmp = compareLongs(getLow(activity), t.low);
            if (cmp == 0) {
                cmp = compareLongs(getHigh(activity), t.high);
                if (cmp == 0)
                    cmp = activity.hashCode() - t.value.hashCode();
            }

            if (cmp < 0) {
                t = t.left;
            } else if (cmp > 0) {
                t = t.right;
            } else {
                return null;
            }
        } while (t != null);

        Entry<A> e = new Entry<>(getLow(activity), getHigh(activity), activity,
                parent);
        if (cmp < 0) {
            parent.left = e;
        } else {
            parent.right = e;
        }

        fixAfterInsertion(e);
        treeSize++;
        return e;
    }

    private int compareLongs(long val1, long val2) {
        return val1 < val2 ? -1 : (val1 == val2 ? 0 : 1);
    }

    // This part of code was copied from java.util.TreeMap

    // Red-black mechanics

    private final boolean RED = false;
    private final boolean BLACK = true;

    /**
     * Internal Entry class.
     *
     * @author koop
     *
     * @param <V>
     */
    private final class Entry<V> {
        private long low;
        private long high;
        private V value;
        private long maxHigh = Long.MIN_VALUE;
        private Entry<V> left = null;
        private Entry<V> right = null;
        private Entry<V> parent;
        private boolean color = BLACK;

        /**
         * Make a new cell with given key, value, and parent, and with
         * <tt>null</tt> child links, and BLACK color.
         */
        Entry(long low, long high, V value, Entry<V> parent) {
            this.low = low;
            this.high = high;
            this.value = value;
            this.parent = parent;
            this.maxHigh = high;
        }

        @Override
        public String toString() {
            return "[" + Instant.ofEpochMilli(low) + " - "
                    + Instant.ofEpochMilli(high) + "]=" + value;
        }

        public Entry<V> getLeft() {
            return left;
        }

        public Entry<V> getRight() {
            return right;
        }
    }

    /**
     * Returns the successor of the specified Entry, or null if no such.
     *
     * @param <V>
     *            the value type
     */
    private <V> Entry<V> successor(Entry<V> t) {
        if (t == null) {
            return null;
        } else if (t.right != null) {
            Entry<V> p = t.right;
            while (p.left != null) {
                p = p.left;
            }
            return p;
        } else {
            Entry<V> p = t.parent;
            Entry<V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * Balancing operations.
     *
     * Implementations of rebalancings during insertion and deletion are
     * slightly different than the CLR version. Rather than using dummy
     * nilnodes, we use a set of accessors that deal properly with null. They
     * are used to avoid messiness surrounding nullness checks in the main
     * algorithms.
     */

    private <V> boolean colorOf(Entry<V> p) {
        return p == null ? BLACK : p.color;
    }

    private <V> Entry<V> parentOf(Entry<V> p) {
        return p == null ? null : p.parent;
    }

    private <V> void setColor(Entry<V> p, boolean c) {
        if (p != null) {
            p.color = c;
        }
    }

    private <V> Entry<V> leftOf(Entry<V> p) {
        return (p == null) ? null : p.left;
    }

    private <V> Entry<V> rightOf(Entry<V> p) {
        return (p == null) ? null : p.right;
    }

    /* From CLR */
    private void rotateLeft(Entry<A> p) {
        if (p != null) {
            Entry<A> r = p.right;
            p.right = r.left;
            if (r.left != null) {
                r.left.parent = p;
            }
            r.parent = p.parent;
            if (p.parent == null) {
                root = r;
            } else if (p.parent.left == p) {
                p.parent.left = r;
            } else {
                p.parent.right = r;
            }
            r.left = p;
            p.parent = r;

            // Original C code:
            // x->maxHigh=ITMax(x->left->maxHigh,ITMax(x->right->maxHigh,x->high))
            // Original C Code:
            // y->maxHigh=ITMax(x->maxHigh,ITMax(y->right->maxHigh,y->high))
            p.maxHigh = Math.max(
                    p.left != null ? p.left.maxHigh : Long.MIN_VALUE,
                    Math.max(p.right != null ? p.right.maxHigh : Long.MIN_VALUE,
                            p.high));
            r.maxHigh = Math.max(p.maxHigh,
                    Math.max(r.right != null ? r.right.maxHigh : Long.MIN_VALUE,
                            r.high));
        }
    }

    /* From CLR */
    private void rotateRight(Entry<A> p) {
        if (p != null) {
            Entry<A> l = p.left;
            p.left = l.right;
            if (l.right != null) {
                l.right.parent = p;
            }
            l.parent = p.parent;
            if (p.parent == null) {
                root = l;
            } else if (p.parent.right == p) {
                p.parent.right = l;
            } else {
                p.parent.left = l;
            }
            l.right = p;
            p.parent = l;

            // Original C code:
            // y->maxHigh=ITMax(y->left->maxHigh,ITMax(y->right->maxHigh,y->high))
            // Original C code:
            // x->maxHigh=ITMax(x->left->maxHigh,ITMax(y->maxHigh,x->high))
            p.maxHigh = Math.max(
                    p.left != null ? p.left.maxHigh : Long.MIN_VALUE,
                    Math.max(p.right != null ? p.right.maxHigh : Long.MIN_VALUE,
                            p.high));
            l.maxHigh = Math.max(p.maxHigh, Math.max(
                    l.left != null ? l.left.maxHigh : Long.MIN_VALUE, l.high));
        }
    }

    /* From CLR */
    private void fixAfterInsertion(Entry<A> x) {

        fixUpMaxHigh(x.parent); // augmented interval tree

        x.color = RED;

        while (x != null && x != root && x.parent.color == RED) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                Entry<A> y = rightOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateRight(parentOf(parentOf(x)));
                }
            } else {
                Entry<A> y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        root.color = BLACK;
    }

    /**
     * Delete node p, and then rebalance the tree.
     */
    private void deleteEntry(Entry<A> p) {
        treeSize--;

        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        if (p.left != null && p.right != null) {
            Entry<A> s = successor(p);
            p.low = s.low;
            p.high = s.high;
            p.value = s.value;
            p.maxHigh = s.maxHigh;
            p = s;
        } // p has 2 children

        // Start fixing at replacement node, if it exists.
        Entry<A> replacement = p.left != null ? p.left : p.right;

        if (replacement != null) {
            // Link replacement to parent
            replacement.parent = p.parent;
            if (p.parent == null) {
                root = replacement;
            } else if (p == p.parent.left) {
                p.parent.left = replacement;
            } else {
                p.parent.right = replacement;
            }

            // Null out links so they are OK to use by fixAfterDeletion.
            p.left = null;
            p.right = null;
            p.parent = null;

            fixUpMaxHigh(replacement.parent); // augmented interval tree

            // Fix replacement
            if (p.color == BLACK) {
                fixAfterDeletion(replacement);
            }
        } else if (p.parent == null) { // return if we are the only node.
            root = null;
        } else { // No children. Use self as phantom replacement and unlink.
            if (p.color == BLACK) {
                fixAfterDeletion(p);
            }

            if (p.parent != null) {
                if (p == p.parent.left) {
                    p.parent.left = null;
                } else if (p == p.parent.right) {
                    p.parent.right = null;
                }

                fixUpMaxHigh(p.parent); // augmented interval tree

                p.parent = null;
            }
        }
    }

    /* From CLR */
    private void fixAfterDeletion(Entry<A> x) {
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) {
                Entry<A> sib = rightOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }

                if (colorOf(leftOf(sib)) == BLACK
                        && colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else { // symmetric
                Entry<A> sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK
                        && colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }

}
