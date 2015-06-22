package stats;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.FilterController;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.ranking.api.RankingController;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.Agent;
import sim.Network;
import sim.SimNetwork;
import sim.Simulator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetTopology extends StatModule {
    private List<Agent> agents = new ArrayList<Agent>();
    private int trial = 0;

    public NetTopology(Statistics parent) {
        super(parent);
    }

    @Override
    public void hookSimulationEnd() {
        System.out.print("Converting GEXF to PNG... ");
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);

        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
        RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);

        ForceAtlas2 forceAtlas2 = new ForceAtlas2(new ForceAtlas2Builder());
        forceAtlas2.setGraphModel(graphModel);
        forceAtlas2.setScalingRatio(75.00);
        forceAtlas2.setAdjustSizes(false);
        forceAtlas2.setOutboundAttractionDistribution(true);

        System.out.println("Began traversing through files.");
        File directory = new File(stats.getDataFolder() + "NetTopology/GEXF");
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                pc.newProject();
                workspace = pc.getCurrentWorkspace();

                //Import file
                Container container;
                try {
                    container = importController.importFile(child);
                    container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTD
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                importController.process(container, new DefaultProcessor(), workspace);

                forceAtlas2.initAlgo();
                for (int i = 0; i < 100 && forceAtlas2.canAlgo(); i++) {
                    forceAtlas2.goAlgo();
                }
                forceAtlas2.endAlgo();

                ExportController ec = Lookup.getDefault().lookup(ExportController.class);
                try {
                    File f = new File(stats.getDataFolder() + "NetTopology/SVG/" + child.getName() + ".png");
                    String name = f.getName();
                    int pos = name.lastIndexOf(".");
                    if (pos > 0) {
                        name = name.substring(0, pos);
                    }
                    if (!f.getParentFile().isDirectory()) {
                        f.getParentFile().mkdirs();
                    }
                    ec.exportFile(f);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
        System.out.println("Done!");
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
            gefx.setAttribute("xmlns:viz", "http://www.gexf.net/1.1draft/viz");
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
            File f = new File(stats.getDataFolder() + "/NetTopology/GEXF/Trial "
                    + trial + ".gexf");
            if (!f.getParentFile().isDirectory()) {
                f.getParentFile().mkdirs();
            }
            StreamResult result = new StreamResult(f.getPath());
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
            nodeColor.setAttribute("r", "" + Math.round((agent.getOpinion() * 255)));
            nodeColor.setAttribute("g", "0");
            nodeColor.setAttribute("b", "" + Math.round(((1 - agent.getOpinion()) * 255)));
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
