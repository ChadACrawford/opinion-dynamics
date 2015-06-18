package stats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sim.Agent;
import sim.SimNetwork;
import sim.Simulator;
import sim.Network;

public class NetTopology extends StatModule {
	private List<Agent> agents = new ArrayList<Agent>();
	private int trial = 0;

	public NetTopology(Statistics parent) {
		super(parent);
	}

	@Override
	public void hookTrialEnd(Simulator sim) {
		trial++;
		agents = sim.getAgents();
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();

			// create main node
			Element gefx = doc.createElementNS("http://www.gexf.net/1.2draft",
					"gexf");
			gefx.setAttributeNS("viz", "http://www.gexf.net/1.1draft/viz");
			gefx.setAttribute("version", "1.2");
			doc.appendChild(gefx);

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Element metadata = doc.createElement("meta");
			metadata.setAttribute("lastmodifieddate",
					dateFormat.format(new Date()));
			gefx.appendChild(metadata);

			Element creator = doc.createElement("creator");
			creator.appendChild(doc.createTextNode(System
					.getProperty("user.name")));
			metadata.appendChild(creator);

			Element description = doc.createElement("description");
			description
					.appendChild(doc
							.createTextNode("A graph showing how the agents cluster together and their opinions."));
			metadata.appendChild(description);

			// enter actual graph
			Element graph = doc.createElement("graph");
			graph.setAttribute("mode", "static");
			graph.setAttribute("defaultedgetype", "directed");
			gefx.appendChild(graph);

			graph.appendChild(getNodes(agents, doc));
			graph.appendChild(getEdges(agents, doc,
					((SimNetwork) sim).getNetwork()));

			// write to file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File f = new File(stats.getDataFolder() + "/NetTopology/Trial "
					+ trial + ".gexf");
			if (!f.getParentFile().isDirectory())
				f.getParentFile().mkdir();
			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	private Element getNodes(List<Agent> agents, Document doc) {
		Element nodes = doc.createElement("nodes");
		for (Agent agent : agents) {
			Element node = doc.createElement("node");
			node.setAttribute("id", "" + agent.id);
			Element nodeColor = doc.createElement("viz:color");
			nodeColor.setAttribute("r", "" + (agent.getOpinion() * 255));
			nodeColor.setAttribute("g", "0");
			nodeColor.setAttribute("b", "" + ((1 - agent.getOpinion()) * 255));
			node.appendChild(nodeColor);
			nodes.appendChild(node);
		}
		return nodes;
	}

	private Element getEdges(List<Agent> agents, Document doc, Network N) {
		int i = 0;
		Element edges = doc.createElement("edges");

		for (Agent a : agents) {
			for (Agent agent : N.neighbors(a)) {
				Element edge = doc.createElement("edge");
				edge.setAttribute("id", "" + i);
				edge.setAttribute("source", "" + a.id);
				edge.setAttribute("target", "" + agent.id);
				edges.appendChild(edge);
				i++;
			}
		}
		return edges;
	}
}
