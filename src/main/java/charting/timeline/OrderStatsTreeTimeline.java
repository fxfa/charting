package charting.timeline;

import charting.util.Preconditions;

import java.time.Instant;
import java.util.*;

/**
 * A {@link Timeline} implementation based on an order statistic tree.
 * Provides O(log n) time complexity for both accessing, updating and ranking of elements.
 */
public final class OrderStatsTreeTimeline<T> extends NotificationBaseTimeline<T> {
    private Entry<T> root;

    private int modCount;

    public void put(Instant instant, T value) {
        modCount++;
        root = put(root, instant, value);
        onUpdate(instant, value);
    }

    @Override
    public Timestamped<T> first() {
        return extract(getFirst(root));
    }

    @Override
    public Timestamped<T> last() {
        return extract(getLast(root));
    }

    @Override
    public Timestamped<T> get(Instant instant) {
        return extract(get(root, instant));
    }

    @Override
    public Timestamped<T> floor(Instant instant) {
        return extract(lower(root, instant, true));
    }

    @Override
    public Timestamped<T> ceiling(Instant instant) {
        return extract(higher(root, instant, true));
    }

    @Override
    public Timestamped<T> lower(Instant instant) {
        return extract(lower(root, instant, false));
    }

    @Override
    public Timestamped<T> higher(Instant instant) {
        return extract(higher(root, instant, false));
    }

    @Override
    public Timestamped<T> get(int i) {
        Preconditions.checkArgument(i >= 0);
        return extract(select(root, i));
    }

    @Override
    public ListIterator<Timestamped<T>> listIterator(int i) {
        Preconditions.checkIndex(i, size() + 1);
        return new ListIteratorImpl(i);
    }

    @Override
    public ListIterator<Timestamped<T>> listIterator(Instant instant) {
        return listIterator(indexOf(instant));
    }

    @Override
    public int indexOf(Instant instant) {
        return rank(root, instant);
    }

    @Override
    public int size() {
        return getSize(root);
    }

    private Timestamped<T> extract(Entry<T> e) {
        return (e == null) ? null : new Timestamped<>(e.getKey(), e.getValue());
    }

    private Entry<T> put(Entry<T> root, Instant key, T value) {
        if (root == null) {
            return new Entry<>(key, value);
        }

        int c = key.compareTo(root.key);
        if (c < 0) {
            int s = getSize(root.left);
            Entry<T> e = put(root.left, key, value);
            if (e.size != s) {
                root.left = e;
                root.left.parent = root;

                root.size++;
                updateHeight(root);
                return balance(root);
            }
        } else if (c > 0) {
            int s = getSize(root.right);
            Entry<T> e = put(root.right, key, value);
            if (e.size != s) {
                root.right = e;
                root.right.parent = root;

                root.size++;
                updateHeight(root);
                return balance(root);
            }
        } else {
            root.value = value;
        }

        return root;
    }

    private void updateHeight(Entry<?> node) {
        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    private int getHeight(Entry<?> node) {
        return node == null ? -1 : node.height;
    }

    private Entry<T> balance(Entry<T> node) {
        int bf = getHeight(node.right) - getHeight(node.left);

        if (bf == 2) {
            if (getHeight(node.right.right) - getHeight(node.right.left) == -1) {
                rotateRight(node.right);
            }
            return rotateLeft(node);
        } else if (bf == -2) {
            if (getHeight(node.left.right) - getHeight(node.left.left) == 1) {
                rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        return node;
    }

    private Entry<T> rotateRight(Entry<T> a) {
        Entry<T> p = a.parent;
        Entry<T> b = a.left;

        a.left = b.right;
        if (b.right != null) {
            b.right.parent = a;
        }
        b.right = a;
        a.parent = b;
        b.parent = p;

        if (p != null) {
            if (p.left == a) {
                p.left = b;
            } else {
                p.right = b;
            }
        }

        a.size = 1 + getSize(a.left) + getSize(a.right);
        updateHeight(a);
        b.size = 1 + getSize(b.left) + a.size;
        updateHeight(b);

        return b;
    }

    private Entry<T> rotateLeft(Entry<T> a) {
        Entry<T> p = a.parent;
        Entry<T> b = a.right;

        a.right = b.left;
        if (b.left != null) {
            b.left.parent = a;
        }
        b.left = a;
        a.parent = b;
        b.parent = p;

        if (p != null) {
            if (p.right == a) {
                p.right = b;
            } else {
                p.left = b;
            }
        }

        a.size = 1 + getSize(a.left) + getSize(a.right);
        updateHeight(a);
        b.size = 1 + a.size + getSize(b.right);
        updateHeight(b);

        return b;
    }

    private int getSize(Entry<?> node) {
        return node == null ? 0 : node.size;
    }

    private Entry<T> lower(Entry<T> root, Instant key, boolean inclusive) {
        if (root == null) {
            return null;
        }

        int c = root.key.compareTo(key);

        if (c == 0) {
            return inclusive ? root : getPredecessor(root);
        }

        if (c > 0) {
            return lower(root.left, key, inclusive);
        }

        Entry<T> rightFloor = lower(root.right, key, inclusive);
        return rightFloor != null && rightFloor.key.compareTo(key) <= 0 ? rightFloor : root;
    }

    private Entry<T> higher(Entry<T> root, Instant key, boolean inclusive) {
        if (root == null) {
            return null;
        }

        int c = root.key.compareTo(key);

        if (c == 0) {
            return inclusive ? root : getSuccessor(root);
        }

        if (c < 0) {
            return higher(root.right, key, inclusive);
        }

        Entry<T> leftCeil = higher(root.left, key, inclusive);
        return leftCeil != null && leftCeil.key.compareTo(key) >= 0 ? leftCeil : root;
    }

    private int rank(Entry<T> root, Instant key) {
        if (root == null) {
            return -1;
        }

        int c = key.compareTo(root.key);

        if (c < 0) {
            return rank(root.left, key);
        }

        if (c == 0) {
            return getSize(root.left);
        }

        int rightIndex = rank(root.right, key);
        return rightIndex < 0 ? rightIndex - getSize(root.left) - 1 : rightIndex + getSize(root.left) + 1;
    }

    private Entry<T> get(Entry<T> root, Instant key) {
        if (root == null) {
            return null;
        }

        int c = root.key.compareTo(key);

        if (c == 0) {
            return root;
        }

        if (c < 0) {
            return get(root.right, key);
        }

        return get(root.left, key);
    }

    private Entry<T> select(Entry<T> root, int index) {
        if (root == null) {
            return null;
        }

        int leftCount = getSize(root.left);

        if (index < leftCount) {
            return select(root.left, index);
        }

        if (index == leftCount) {
            return root;
        }

        return select(root.right, index - leftCount - 1);
    }

    private Entry<T> getSuccessor(Entry<T> node) {
        if (node == null) {
            return null;
        }

        if (node.right != null) {
            Entry<T> leftMost = node.right;
            while (leftMost.left != null) {
                leftMost = leftMost.left;
            }

            return leftMost;
        }

        Entry<T> parent = node.parent;
        Entry<T> current = node;
        while (parent != null) {
            if (parent.left == current) {
                return parent;
            }

            current = parent;
            parent = parent.parent;
        }

        return null;
    }

    private Entry<T> getPredecessor(Entry<T> node) {
        if (node == null) {
            return null;
        }

        if (node.left != null) {
            Entry<T> rightMost = node.left;
            while (rightMost.right != null) {
                rightMost = rightMost.right;
            }

            return rightMost;
        }

        Entry<T> parent = node.parent;
        Entry<T> current = node;
        while (parent != null) {
            if (parent.right == current) {
                return parent;
            }

            current = parent;
            parent = parent.parent;
        }

        return null;
    }

    private Entry<T> getLast(Entry<T> root) {
        if (root == null) {
            return null;
        }

        return root.right == null ? root : getLast(root.right);
    }

    private Entry<T> getFirst(Entry<T> root) {
        if (root == null) {
            return null;
        }

        return root.left == null ? root : getFirst(root.left);
    }

    static class Entry<T> implements Map.Entry<Instant, T> {
        Instant key;
        T value;
        Entry<T> left;
        Entry<T> right;
        Entry<T> parent;
        int size = 1;
        int height;

        Entry(Instant key, T value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Instant getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T setValue(T value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Map.Entry<?, ?> e && key.equals(e.getKey()) && value.equals(getValue());
        }

        @Override
        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private class ListIteratorImpl implements ListIterator<Timestamped<T>> {
        final int expectedModCount = modCount;

        Entry<T> previous;
        Entry<T> next;
        int nextIndex;
        int previousIndex;

        ListIteratorImpl(int index) {
            this.nextIndex = index;
            this.previousIndex = index - 1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex() < size();
        }

        @Override
        public boolean hasPrevious() {
            return previousIndex() >= 0;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return previousIndex;
        }

        @Override
        public Timestamped<T> next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (previous != null) {
                next = previous;
                previous = null;
            } else if (next != null) {
                next = getSuccessor(next);
            } else {
                next = select(root, nextIndex);
            }

            previousIndex++;
            nextIndex++;

            return extract(next);
        }

        @Override
        public Timestamped<T> previous() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }

            if (previous != null) {
                previous = getPredecessor(previous);
            } else if (next != null) {
                previous = next;
                next = null;
            } else {
                previous = select(root, previousIndex);
            }

            previousIndex--;
            nextIndex--;

            return extract(previous);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Timestamped<T> t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Timestamped<T> t) {
            throw new UnsupportedOperationException();
        }
    }
}
