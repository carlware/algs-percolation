
/**
 *  
 */

enum SiteState { BLOCKED, OPEN }

public class Percolation {
    
    private WeightedQuickUnionUF grid;
    private Out     fileout;
    private SiteState[] state;
    private int     N;

    // create N-by-N grid, with all sites blocked
    public Percolation(int N) {

        int site_count = N * N;
        this.N = N;

        // index 0 and N^2+1 are reserved for virtual top and bottom sites
        grid    = new WeightedQuickUnionUF(site_count + 2);
        state   = new SiteState[site_count + 2];
        fileout = new Out("test.txt");

        // Initilize all sites to be blocked.
        for (int i = 1; i <= site_count; i++)
            state[i] = SiteState.BLOCKED;
        // Initilize virtual top and bottom site with open state
        state[0] = state[site_count+1] = SiteState.OPEN;
        
        fileout.println(this.N);
    }

    // return array index of given row i and column j
    private int xyToIndex(int i, int j) {
        // Attention: i and j are of range 1 ~ N, NOT 0 ~ N-1.
        // Throw IndexOutOfBoundsException if i or j is not valid
        if (i <= 0 || i > N) 
            throw new IndexOutOfBoundsException("row " + i + " out of bound");
        if (j <= 0 || j > N) 
            throw new IndexOutOfBoundsException("column " + j + " out of bound");

        return (i - 1) * N + j;
    }

    private boolean isTopSite(int index) {
        return index <= N;
    }

    private boolean isBottomSite(int index) {
        return index >= (N - 1) * N + 1;
    }

    // open site (row i, column j) if it is not already
    public void open(int i, int j) {
        // All input sites are blocked at first. 
        // Check the state of site before invoking this method.
        int idx = xyToIndex(i, j);
        fileout.printf("%d %d\n", i, j);
        state[idx] = SiteState.OPEN;

        // Traverse surrounding sites, connect all open ones. 
        // Make sure we do not index sites out of bounds.
        if (i != 1 && isOpen(i-1, j)) grid.union(idx, xyToIndex(i-1, j));
        if (i != N && isOpen(i+1, j)) grid.union(idx, xyToIndex(i+1, j));
        if (j != 1 && isOpen(i, j-1)) grid.union(idx, xyToIndex(i, j-1));
        if (j != N && isOpen(i, j+1)) grid.union(idx, xyToIndex(i, j+1));
        // if site is on top or bottom, connect to corresponding virtual site.
        if (isTopSite(idx))     grid.union(0, idx);
        if (isBottomSite(idx) && isFull(i, j))  grid.union(state.length-1, idx);
    }

    // is site (row i, column j) open?
    public boolean isOpen(int i, int j) {
        int idx = xyToIndex(i, j);
        return state[idx] == SiteState.OPEN;
    }

    // is site (row i, column j) full?
    public boolean isFull(int i, int j) {
        // Check if this site is connected to virtual top site
        int idx = xyToIndex(i, j);
        return grid.connected(0, idx);
    }

    // does the system percolate?
    public boolean percolates() {
        // Check whether virtual top and bottom sites are connected
        return grid.connected(0, state.length-1);
    }

    public static void main(String[] args) {
        Percolation pl = new Percolation(Integer.parseInt(args[0]));
        int row, column;

        do {
            // generate random row and column numbers between 1~N
            row     = StdRandom.uniform(1, pl.N+1);
            column  = StdRandom.uniform(1, pl.N+1);
            if(pl.isOpen(row, column))
                continue;
            pl.open(row, column);
        } while (!pl.percolates());
    }
}
