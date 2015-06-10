package sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * Created by Chad on 2/14/15.
 */
public class Network {
	public final UndirectedGraph<Node, Edge> graph;
	public final HashMap<Agent, Node> amap = new HashMap<Agent, Node>();
	public final List<Agent> agents = new ArrayList<Agent>();

	public Network(int N, Random rand) {
		graph = new UndirectedSparseGraph<Node, Edge>();
		for (int i = 0; i < N; i++) {
			Agent a = new Agent(rand.nextDouble());
			agents.add(a);
			Node n = new Node(a);
			amap.put(a, n);
			graph.addVertex(n);
		}
	}

	public List<Agent> neighbors(Agent a) {
		return amap.get(a).neighbors;
	}

	public int degree(Agent a) {
		return amap.get(a).neighbors.size();
	}

	public void addEdge(Agent a, Agent b) {
		Edge e1 = new Edge(a, b);
		Node n1 = amap.get(a), n2 = amap.get(b);
		n1.neighbors.add(b);
		n2.neighbors.add(a);
		graph.addEdge(e1, n1, n2);
	}

	public void remEdge(Agent a, Agent b) {
		Node n1 = amap.get(a), n2 = amap.get(b);
		n1.neighbors.remove(b);
		n2.neighbors.remove(a);
		graph.removeEdge(graph.findEdge(n1, n2));
	}

	public boolean isEdge(Agent a, Agent b) {
		Node n1 = amap.get(a), n2 = amap.get(b);
		return graph.findEdge(n1, n2) != null;
	}

	public ArrayList<HashSet<Agent>> calcClusters(double E) {
		ArrayList<HashSet<Agent>> ngroups = new ArrayList<HashSet<Agent>>();
		for (Agent a : agents) {
			List<Agent> as = neighbors(a);
			ArrayList<HashSet<Agent>> n1gs = new ArrayList<HashSet<Agent>>();
			for (Agent b : as) {
				HashSet<Agent> n2g = null;
				for (HashSet<Agent> g : ngroups)
					if (g.contains(b)) {
						n2g = g;
						break;
					}
				// Interaction i = new IM1(constr, n.a, n2.a, op);
				// if(i.result == I.a && n2g != null) n1gs.add(n2g);
				if (Math.abs(a.getOpinion() - b.getOpinion()) < E
						&& n2g != null)
					n1gs.add(n2g);
			}
			HashSet<Agent> n1g;
			if (n1gs.size() < 1) {
				n1g = new HashSet<Agent>();
				n1g.add(a);
				ngroups.add(n1g);
			} else if (n1gs.size() > 1) {
				n1g = new HashSet<Agent>();
				n1g.add(a);
				for (HashSet<Agent> g : n1gs) {
					n1g.addAll(g);
					ngroups.remove(g);
				}
				ngroups.add(n1g);
			} else {
				n1gs.get(0).add(a);
			}
		}
		System.out.format("Number of clusters: %d\n", ngroups.size());
		for (HashSet<Agent> g : ngroups) {
			if (g.size() > 2)
				System.out.format("%d  ", g.size());
		}
		System.out.println();
		return ngroups;
	}

	public class Node {
		public final Agent a;
		private List<Agent> neighbors;

		public Node(Agent a) {
			this.a = a;
			this.neighbors = new ArrayList<Agent>();
		}
	}

	public class Edge {
		public final Agent a, b;

		public Edge(Agent a, Agent b) {
			if (a.id > b.id) {
				this.a = b;
				this.b = a;
			} else {
				this.a = a;
				this.b = b;
			}
		}
	}
}
