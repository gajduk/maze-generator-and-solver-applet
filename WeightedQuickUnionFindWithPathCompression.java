import java.util.Arrays;


public class WeightedQuickUnionFindWithPathCompression {

	private int parent_ids[];
	private int weight[];
	private int count;

	public WeightedQuickUnionFindWithPathCompression( int n ) {
		this.parent_ids = new int[n];
		this.weight = new int[n];
		Arrays.fill(this.weight, 1);
		for ( int i = 0 ; i < n ; ++i ) this.parent_ids[i] = i;
		this.count = n;
	}

	public void union(int a, int b) {
		int p = find(a);
		int q = find(b);
		if ( p == q ) return;
		--count;
		if ( weight[q] > weight[p] ) {
			weight[q] += weight[p];
			parent_ids[p] = q;
		}
		else {
			weight[p] += weight[q];
			parent_ids[q] = p;
		}
	}

	public boolean isConneted(int a, int b) {
		return find(a)==find(b);
	}

	public int find(int x) {
		while ( parent_ids[x] != x ) {
			int t = x;
			x = parent_ids[x];
			parent_ids[t] = parent_ids[x];
		}
		return x;
	}

	public int count() {
		return count;
	}
	
	public int[] get_ids() { return parent_ids; }
	
}
