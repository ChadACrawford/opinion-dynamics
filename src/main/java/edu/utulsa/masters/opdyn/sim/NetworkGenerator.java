package edu.utulsa.masters.opdyn.sim;

import edu.utulsa.masters.cutil.CList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Generates random networks of varying topologies.
 *
 * I did some crappy method of having subclasses implement the NetworkGenerator class. Not good design, but it feels a
 * bit clever I guess.
 */
public abstract class NetworkGenerator implements IndepedentVariable {
    private static Random rand;

    public NetworkGenerator() {
        rand = new Random();
    }

    public NetworkGenerator(long seed) {
        rand = new Random(seed);
    }

    public abstract Network generate(int M);

    /**
     * Random network generated using Erdos-Renyi model
     * @param N Network
     * @param A List of agents
     * @param E Average (?) degree
     */
    public static void random(final Network N, List<Agent> A, int E) {
        for(int i = 0; i < E; i++) {
            Agent n1,n2;
            n1 = Collections.min(A, new Comparator<Agent>() {
                public int compare(Agent o1, Agent o2) {
                    return N.degree(o1) > N.degree(o2) ? 1:-1;
                }
            });
            if(N.degree(n1) <= 0)
                while((n2 = CList.choose(rand, A)) == n1);
            else
                while((n2 = CList.choose(rand, A)) == (n1 = CList.choose(rand, A)) || N.isEdge(n1,n2));
            N.addEdge(n1, n2);
        }
    }

    // TODO: maybe fix old comments
    /**
     * fuck self-documenting code
     * @param N thing?
     * @param A another thing
     * @param M this is important thing (spells 'nam)
     */
    public static void scalefree(Network N, List<Agent> A, int M) {
        List<Agent> init = A.subList(0,M);
        int d = M;
        random(N, init, d);
        for(int i = M; i < A.size(); i++) {
            boolean c = false;
            Agent a = A.get(i);
            for(int j = 0; j < i; j++) {
                Agent b = A.get(j);
                double p = N.degree(b)/(1.*d);
                if(rand.nextDouble() < p && !N.isEdge(a,b)) {
                    N.addEdge(a, b);
                    d++;
                    c = true;
                }
            }
            if(!c && N.degree(a)<=0) i--;
        }
        Debug.format(5, "Scale free: %d nodes, %d edges.\n", A.size(), d);
    }

    /**
     * Small world generated using watts-strogatz
     * @param N Network object.
     * @param A List of agents
     * @param K The degree of each agent
     * @param B "Likelihood" of an edge on the lattice being changed into a random edge.
     */
    public static void smallworld(Network N, List<Agent> A, int K, double B) {
        for(int i = 0; i < A.size(); i++) {
            Agent a1 = A.get(i);
            for(int j = 0; j < i; j++) {
                if((i-j)%(A.size()-K/2)<=K/2) {
                    Agent a2 = A.get(j);
                    N.addEdge(a1,a2);
                }
            }
        }

        for(int i = 0; i < A.size(); i++) {
            Agent a = A.get(i);
            List<Agent> an = N.neighbors(a);
            for(Agent b: an) {
                if(rand.nextDouble()<B) {
                    N.remEdge(a,b);
                    Agent c;
                    while(a == (c = CList.choose(rand, A)) || N.isEdge(a,c));
                    N.addEdge(a,b);
                }
            }
        }
    }

    /**
     * A particularly interesting piece of code here
     * @param N
     * @param A
     * @param K
     */
    public static void group(Network N, List<Agent> A, int K) {

    }

    /**
     * Class that generates random networks.
     */
    public static class NGRandom extends NetworkGenerator {
        private int D;
        public NGRandom(int D) {
            this.D = D;
        }

        @Override
        public Network generate(int M) {
            Network N = new Network(M, rand);
            random(N, N.agents, D*N.agents.size()/2);
            return N;
        }

        public IndepedentVariable updateValue(Independent I, double V) {
            if(I == Independent.DEGREE) return new NGRandom((int)V);
            else return new NGRandom(D);
        }
    }

    /**
     * Class that generates scale-free networks.
     */
    public static class NGScaleFree extends NetworkGenerator {
        private int M;
        public NGScaleFree(int M) {
            this.M = M;
        }

        @Override
        public Network generate(int C) {
            Debug.format(5, "Generating scale-free network with parameters: M = %d\n", M);
            Network N = new Network(C, rand);
            scalefree(N, N.agents, M);
            Debug.println(10, "Scale-free network completed.");
            return N;
        }

        public IndepedentVariable updateValue(Independent I, double V) {
            return new NGScaleFree(M);
        }
    }

    /**
     * Generates small world networks using Watts-Strogatz model
     */
    public static class NGSmallWorld extends NetworkGenerator {
        private int K;
        private double B;
        public NGSmallWorld(int K, double B) {
            this.K = K;
            this.B = B;
        }

        @Override
        public Network generate(int C) {
            Network N = new Network(C, rand);
            smallworld(N,N.agents,K,B);
            return N;
        }

        public IndepedentVariable updateValue(Independent I, double V) {
            return new NGSmallWorld(K,B);
        }
    }

    /**
     * Generates "group" networks, although the code is missing
     */
    public static class NGGroup extends NetworkGenerator {
        private int K;
        public NGGroup(int K) {
            this.K = K;
        }

        @Override
        public Network generate(int C) {
            Network N = new Network(C, rand);
            group(N, N.agents, K);
            return N;
        }

        public IndepedentVariable updateValue(Independent I, double V) {
            return new NGGroup(K);
        }
    }

    /**
     * Generates a complete (fully connected) network.
     */
    public static class NGComplete extends NetworkGenerator {
        @Override
        public Network generate(int C) {
            return new CompleteNetwork(C, rand);
        }

        public IndepedentVariable updateValue(Independent I, double V) {
            return new NGComplete();
        }
    }
}