package er.plugin;

import java.util.*;
import java.io.*;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.jetbrains.annotations.NotNull;
import org.openide.util.Lookup;

//@author Ebrahim Razmdideh

public class BigClam {
    // Constants
    public static final double EPSILON = 1e-8;
    public static final double ETA = 0.01;
    public static final double LAMBDA = 0.1;
    public static final double THRESHOLD = Math.sqrt(1 - Math.log(1 - EPSILON));

    private int n;
    private int k;
    private double[][] F;
    private List<Integer>[] adj;
    private Random rand;

    public BigClam(int n, int k, List<Integer>[] adj) {
        this.n = n;
        this.k = k;
        this.adj = adj;
        this.F = new double[n][k];
        this.rand = new Random();
        initializeF();
    }

    public BigClam() {

    }

    private void initializeF() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                F[i][j] = rand.nextDouble();
            }
        }
    }

    public void optimizeF() {
        boolean converged = false;
        while (!converged) {
            converged = true;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k; j++) {
                    double gradient = computeGradient(i, j);
                    F[i][j] += ETA * gradient;
                    F[i][j] = Math.max(F[i][j], 0);
                    if (Math.abs(gradient) > EPSILON) {
                        converged = false;
                    }
                }
            }
        }
    }

    private double computeGradient(int i, int j) {
        double gradient = -LAMBDA * F[i][j];
        for (int u : adj[i]) {
            double p = 1 - Math.exp(-dotProduct(F[i], F[u]));
            gradient += F[u][j] / p;
        }
        for (int v = 0; v < n; v++) {
            if (v == i || adj[i].contains(v)) continue;
            double q = 1 - Math.exp(-dotProduct(F[i], F[v]));
            gradient -= F[v][j] / (1 - q);
        }
        return gradient;
    }
    private double dotProduct(double @NotNull [] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public List<Integer>[] assignCommunities() {
        List<Integer>[] communities = new List[k];
        for (int i = 0; i < k; i++) {
            communities[i] = new ArrayList<>();
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                if (F[i][j] > THRESHOLD) {
                    communities[j].add(i);
                }
            }
        }
        return communities;
    }

    // Method to print the communities
    public void printCommunities(List<Integer>[] communities) {
        for (int i = 0; i < k; i++) {
            System.out.println("Community " + (i + 1) + ": " + communities[i]);
        }
    }
    public static void main(String[] args) throws IOException {
        // Read the graph from a file
        Scanner sc = new Scanner(new File("graph.txt"));
        int n = sc.nextInt();
        int m = sc.nextInt();
        int k = sc.nextInt();
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            adj[u].add(v);
            adj[v].add(u);
        }
        sc.close();


        BigClam bigClam = new BigClam(n, k, adj);


        bigClam.optimizeF();


        List<Integer>[] communities = bigClam.assignCommunities();


        bigClam.printCommunities(communities);
    }
}
