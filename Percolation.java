import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final WeightedQuickUnionUF grid;
    private final WeightedQuickUnionUF full;
    private final int N;
    private final int top;
    private final int bottom;
    private int openSites;
    private boolean[] openNodes;

    /**
     * Initialises an N * N WeightedQuickUnionUF object plus two extra nodes for the virtual top and virtual bottom
     * nodes. Creates an internal boolean array to keep track of whether a node is considered open or not.
     *
     * Also initialises a second N * N WeightedQuickUnionUF object plus one extra node as a second collection to check
     * for fullness and avoid the backwash issue.
     *
     * @param N The dimensions of the grid
     */
    public Percolation(int N) {
        if (N <= 0) {
            throw new java.lang.IllegalArgumentException("Woah, N must be greater than zero");
        }

        grid = new WeightedQuickUnionUF(N * N + 2);
        full = new WeightedQuickUnionUF(N * N + 1);
        this.N = N;

        top = getSingleArrayIdx(N, N) + 1;
        bottom = getSingleArrayIdx(N, N) + 2;
        openSites = 0;
        openNodes = new boolean[N * N];
    }

    /**
     * Converts an index for a 0-based array from two grid coordinates which are 1-based. First checks to see if the
     * coordinates are out of bounds.
     *
     * @param i Node row
     * @param j Node column
     * @return
     */
    private int getSingleArrayIdx(int i, int j) {
        doOutOfBoundsCheck(i, j);

        return (N * (i - 1) + j) - 1;
    }

    /**
     * Checks to see if two given coordinates are valid. I.e - a coodinate is valid if it is greater than 0
     * and smaller than the dimensions of the parent grid
     *
     * @param i Node row
     * @param j Node column
     * @return
     */
    private boolean isValid(int i, int j) {
        return i > 0
                && j > 0
                && i <= N
                && j <= N;
    }

    /**
     * Throws an error if the given coordinates are valid (the valid state comes from the `isValid` function
     * @param i Node row
     * @param j Node column
     */
    private void doOutOfBoundsCheck(int i, int j) {
        if (!isValid(i, j)) {
            throw new IllegalArgumentException("Boo! Values are out of bounds");
        }
    }

    /**
     * Sets a given node coordinates to be open (if it isn't open already). First is sets the appropriate index of the
     * `openNodes` array to be true and then attempts to union with all adjacent open nodes (if any).
     *
     * If the node is in the first row then it will union with the virtual top node. If the node is in the last row
     * then it will union with the virtual bottom row.
     *
     * This does connections both for the internal `grid` WeightedQuickUnionUF as well as the `full` WeightedQuickUnionUF,
     * but checks to make sure that the nodes in `full` never connect to the virtual bottom node.
     *
     * @param i Node row
     * @param j Node column
     */
    public void open(int i, int j) {
        doOutOfBoundsCheck(i, j);

        if (isOpen(i, j)) {
            // No need to open this again as it's already open
            return;
        }

        int idx = getSingleArrayIdx(i, j);
        openNodes[idx] = true;
        openSites++;

        // Node is in the top row. Union node in `grid` and `full` to the virtual top row.
        if (i == 1) {
            grid.union(top, idx);
            full.union(top, idx);
        }

        // Node is in the bottom row. Only union the node in `grid` to avoid backwash issue.
        if (i == N) {
            grid.union(bottom, idx);
        }

        // Union with the node above the given node if it is already open
        if (isValid(i - 1, j) && isOpen(i - 1, j)) {
            grid.union(getSingleArrayIdx(i - 1, j), idx);
            full.union(getSingleArrayIdx(i - 1, j), idx);
        }

        // Union with the node to the right of the given node if it is already open
        if (isValid(i, j + 1) && isOpen(i, j + 1)) {
            grid.union(getSingleArrayIdx(i, j + 1), idx);
            full.union(getSingleArrayIdx(i, j + 1), idx);
        }

        // Union with the node below the given node if it is already open
        if (isValid(i + 1, j) && isOpen(i + 1, j)) {
            grid.union(getSingleArrayIdx(i + 1, j), idx);
            full.union(getSingleArrayIdx(i + 1, j), idx);

        }

        // Union with the node to the left of the given node if it is already open
        if (isValid(i, j - 1) && isOpen(i, j - 1)) {
            grid.union(getSingleArrayIdx(i, j - 1), idx);
            full.union(getSingleArrayIdx(i, j - 1), idx);
        }
    }

    /**
     * Whether this node id open. This is checked against the internal `openNodes` array.
     *
     * @param i Node row
     * @param j Node column
     * @return
     */
    public boolean isOpen(int i, int j) {
        doOutOfBoundsCheck(i, j);

        return openNodes[getSingleArrayIdx(i, j)];
    }

    /**
     * Checks if a given node if 'full'. A node is considered full if it connects to the virtual top node.
     * Note that this check is against the full WeightedQuickUnionUF object which is not connected to the virtual
     * bottom node so that we don't get affected by backwash.
     *
     * @param i Node row
     * @param j Node column
     * @return
     */
    public boolean isFull(int i, int j) {
        int idx = getSingleArrayIdx(i, j);
        return full.connected(idx, top);
    }
    
    // returns the number of open sites
    public int numberOfOpenSites() {
    	return openSites;
    }

    /**
     * Does this grid percolate? It percolates if the virtual top node connects to the virtual bottom node
     *
     * @return
     */
    public boolean percolates() {
        return grid.connected(top , bottom);
    }
}
