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

// Class for BigClam algorithm
public class BigClam {
    // Constants
    public static final double EPSILON = 1e-8; // Small value for convergence
    public static final double ETA = 0.01; // Learning rate
    public static final double LAMBDA = 0.1; // Regularization parameter
    public static final double THRESHOLD = Math.sqrt(1 - Math.log(1 - EPSILON)); // Threshold for community assignment

    // Fields
    private int n; // Number of nodes
    private int k; // Number of communities
    private double[][] F; // Factor matrix of size n x k
    private List<Integer>[] adj; // Adjacency list of the graph
    private Random rand; // Random number generator

    // Constructor
    public BigClam(int n, int k, List<Integer>[] adj) {
        this.n = n;
        this.k = k;
        this.adj = adj;
        this.F = new double[n][k];
        this.rand = new Random();
        initializeF(); // Initialize the factor matrix with random values
    }

    public BigClam() {

    }

    // Method to initialize the factor matrix with random values
    private void initializeF() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                F[i][j] = rand.nextDouble();
            }
        }
    }

    // Method to optimize the factor matrix using gradient ascent
    public void optimizeF() {
        boolean converged = false; // Flag to check convergence
        while (!converged) {
            converged = true;
            for (int i = 0; i < n; i++) { // For each node
                for (int j = 0; j < k; j++) { // For each community
                    double gradient = computeGradient(i, j); // Compute the gradient
                    F[i][j] += ETA * gradient; // Update the factor value
                    F[i][j] = Math.max(F[i][j], 0); // Project to non-negative space
                    if (Math.abs(gradient) > EPSILON) { // Check convergence
                        converged = false;
                    }
                }
            }
        }
    }

    // Method to compute the gradient of the log-likelihood function
    private double computeGradient(int i, int j) {
        double gradient = -LAMBDA * F[i][j]; // Regularization term
        for (int u : adj[i]) { // For each neighbor of node i
            double p = 1 - Math.exp(-dotProduct(F[i], F[u])); // Probability of edge (i, u)
            gradient += F[u][j] / p; // Positive term
        }
        for (int v = 0; v < n; v++) { // For each non-neighbor of node i
            if (v == i || adj[i].contains(v)) continue; // Skip if v is i or a neighbor of i
            double q = 1 - Math.exp(-dotProduct(F[i], F[v])); // Probability of edge (i, v)
            gradient -= F[v][j] / (1 - q); // Negative term
        }
        return gradient;
    }

    // Method to compute the dot product of two vectors
    private double dotProduct(double @NotNull [] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    // Method to assign nodes to communities based on the factor matrix
    public List<Integer>[] assignCommunities() {
        List<Integer>[] communities = new List[k]; // List of communities
        for (int i = 0; i < k; i++) {
            communities[i] = new ArrayList<>(); // Initialize each community
        }
        for (int i = 0; i < n; i++) { // For each node
            for (int j = 0; j < k; j++) { // For each community
                if (F[i][j] > THRESHOLD) { // If the factor value is above the threshold
                    communities[j].add(i); // Assign node i to community j
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
        int n = sc.nextInt(); // Number of nodes
        int m = sc.nextInt(); // Number of edges
        int k = sc.nextInt(); // Number of communities
        List<Integer>[] adj = new List[n]; // Adjacency list of the graph
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>(); // Initialize each list
        }
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt(); // Source node
            int v = sc.nextInt(); // Destination node
            adj[u].add(v); // Add edge (u, v)
            adj[v].add(u); // Add edge (v, u)
        }
        sc.close();

        // Create an instance of BigClam algorithm
        BigClam bigClam = new BigClam(n, k, adj);

        // Optimize the factor matrix
        bigClam.optimizeF();

        // Assign nodes to communities
        List<Integer>[] communities = bigClam.assignCommunities();

        // Print the communities
        bigClam.printCommunities(communities);
    }
}
