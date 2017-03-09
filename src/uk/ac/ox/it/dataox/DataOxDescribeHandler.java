package uk.ac.ox.it.dataox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.ox.it.dataox.vocabulary.OV;
import uk.ac.ox.it.dataox.vocabulary.SKOS;
import uk.ac.ox.it.dataox.vocabulary.VOID;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.ARQConstants;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.core.describe.DescribeHandler;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class DataOxDescribeHandler implements DescribeHandler {
	protected Model acc;
	protected Dataset dataset;
	protected Resource requestedDocument;
	protected Collection<Resource> graphs = new HashSet<Resource>();

	protected static List<Property> labelProperties = new ArrayList<Property>();
	static {
		labelProperties.add(RDF.type);
		labelProperties.add(RDFS.label);
		labelProperties.add(DC.title);
		labelProperties.add(DCTerms.title);
		labelProperties.add(FOAF.name);
		labelProperties.add(RDF.value);
		labelProperties.add(SKOS.prefLabel);
		labelProperties.add(DCTerms.license);
	}

	@Override
	public void start(Model acc, Context context) {
		this.acc = acc;
		this.dataset = (Dataset) context.get(ARQConstants.sysCurrentDataset) ;

		this.requestedDocument = this.acc.createResource();
		this.requestedDocument.addProperty(RDF.type, OV.RequestedDocument);

	}

	@Override
	public void describe(Resource resource) {
		closure(dataset, resource);

		// Find all the named graphs in which this resource
		// occurs as a subject.  Faster than iterating in the
		// names of graphs in the case of very large numbers
		// of graphs, few of which contain the resource, in
		// some kind of persistent storage.



		//        // Named graphs
		//        for ( Iterator<String> iter = dataset.listNames() ; iter.hasNext() ; )
		//        {
		//            String name = iter.next();
		//            Model model =  dataset.getNamedModel(name) ;
		//            Resource r2 = otherModel(r, model) ;
		//            Closure.closure(r2, false, acc) ;
		//        }

		//Closure.closure(resource, false, acc) ;
	}

	@Override
	public void finish() {
		Model model = dataset.getNamedModel("urn:x-arq:UnionGraph");

		// Add graph metadata
		for (Resource graph : graphs) {
			Resource graphInModel = otherModel(graph, model);
			acc.add(graphInModel.listProperties(DCTerms.license));
			acc.add(graphInModel.listProperties(VOID.inDataset));
			acc.add(this.requestedDocument, DCTerms.source, graphInModel);
		}

		// Add labels
		Set<Resource> resources = new HashSet<Resource>();

		StmtIterator statements = acc.listStatements();
		for ( ; statements.hasNext(); ) {
			Statement statement = statements.next();
			resources.add(statement.getPredicate().asResource());
			RDFNode object = statement.getObject();
			if (object.isResource())
				resources.add(object.asResource());
		}

		for (Resource resource : resources) {
			resource = otherModel(resource, model);
			for (Property property : labelProperties)
				acc.add(resource.listProperties(property));
		}
	}

	protected static Resource otherModel(Resource r, Model model)
	{
		if ( r.isURIResource() )
			return model.createResource(r.getURI()) ;
		if ( r.isAnon() )
			return model.createResource(r.getId()) ;
		// Literals do not need converting.
		return r ;
	}

	protected void closure(Dataset dataset, Resource resource) {
		closure(dataset.asDatasetGraph(), resource.asNode());
	}

	protected void closure(DatasetGraph datasetGraph, Node node) {
		Collection<Node> seen = new HashSet<Node>();
		List<Node> remaining = new LinkedList<Node>();
		seen.add(node);
		remaining.add(node);

		while (!remaining.isEmpty()) {
			Node currentNode = remaining.remove(0);
			Iterator<Quad> quads = datasetGraph.find(null, currentNode, null, null);
			while (quads.hasNext()) {
				Quad quad = quads.next();
				graphs.add((Resource) acc.asRDFNode(quad.getGraph()));
				acc.add(acc.asStatement(quad.asTriple()));

				Node object = quad.getObject();
				if (object.isBlank() && seen.contains(object)) {
					seen.add(object);
					remaining.add(object);
				}
			}
		}
	}
}
