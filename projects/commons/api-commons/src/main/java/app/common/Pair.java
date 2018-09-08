package app.common;

public class Pair<L, R> {

    private L left;
    private R right;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors.
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Pair() {
        super();
    }

    public Pair(final L left, final R right) {
        super();
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    public void setLeft(final L left) {
        this.left = left;
    }

    public void setRight(final R right) {
        this.right = right;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see java.lang.Object#hashCode()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public int hashCode() {
        return this.left.hashCode() ^ this.right.hashCode();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see java.lang.Object#equals(java.lang.Object)
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final Pair<L, R> pairo = (Pair<L, R>) o;
        return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
    }
}