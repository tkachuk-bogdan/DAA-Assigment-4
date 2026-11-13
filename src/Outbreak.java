import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        int[] parent = new int[n];
        int[] order = rootAtZero(n, nb, parent);

        long[] tested = new long[n];
        long[] notTested = new long[n];
        for (int idx = n - 1; idx >= 0; idx--) {
            int v = order[idx];
            tested[v] = cost[v];
            notTested[v] = 0;
            for (int u : nb[v]) {
                if (u == parent[v]) {
                    continue;
                }
                tested[v] += Math.min(tested[u], notTested[u]);
                notTested[v] += tested[u];
            }
        }

        boolean[] chosen = new boolean[n];
        for (int idx = 0; idx < n; idx++) {
            int v = order[idx];
            if (v == 0) {
                chosen[v] = tested[v] <= notTested[v];
            } else if (!chosen[parent[v]]) {
                chosen[v] = true;
            } else {
                chosen[v] = tested[v] <= notTested[v];
            }
        }
        return chosen;
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        int[] parent = new int[n];
        int[] order = rootAtZero(n, nb, parent);

        int[] down = new int[n];
        int best = 0;
        for (int idx = n - 1; idx >= 0; idx--) {
            int v = order[idx];
            int first = 0;
            int second = 0;
            for (int u : nb[v]) {
                if (u == parent[v]) {
                    continue;
                }
                int way = down[u] + 1;
                if (way > first) {
                    second = first;
                    first = way;
                } else if (way > second) {
                    second = way;
                }
            }
            down[v] = first;
            best = Math.max(best, first + second);
        }
        return best;
    }

    private static int[] rootAtZero(int n, int[][] nb, int[] parent) {
        int[] order = new int[n];
        boolean[] seen = new boolean[n];
        int head = 0;
        int tail = 0;
        order[tail++] = 0;
        seen[0] = true;
        parent[0] = -1;
        while (head < tail) {
            int v = order[head++];
            for (int u : nb[v]) {
                if (!seen[u]) {
                    seen[u] = true;
                    parent[u] = v;
                    order[tail++] = u;
                }
            }
        }
        return order;
    }
}
