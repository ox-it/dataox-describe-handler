package uk.ac.ox.it.dataox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ox.it.dataox.vocabulary.VOID;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.core.describe.DescribeHandler;
import com.hp.hpl.jena.sparql.util.Closure;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DataOxDescribeHandler implements DescribeHandler {
	protected Model acc;
	protected Dataset dataset;

	protected static Query query = QueryFactory.create("SELECT DISTINCT ?g { GRAPH ?g { ?s ?p ?o } }") ;

	protected static List<Property> labelProperties = new ArrayList<Property>();
	static {
		OntModel ont = ModelFactory.createOntologyModel();
		labelProperties.add(RDF.type);
		labelProperties.add(RDFS.label);
		labelProperties.add(DC.title);
		labelProperties.add(DCTerms.title);
		labelProperties.add(FOAF.name);
		labelProperties.add(RDF.value);
		labelProperties.add(ont.createProperty("http://www.w3.org/2004/02/skos/core#prefLabel"));
	}

	@Override
	public void start(Model acc, Context context) {
		this.acc = acc;
        this.dataset = (Dataset) context.get(ARQConstants.sysCurrentDataset) ;
	}

	@Override
	public void describe(Resource resource) {
		// Default model.
		Closure.closure(otherModel(resource, dataset.getDefaultModel()), false, acc) ;

		// Find all the named graphs in which this resource
		// occurs as a subject.  Faster than iterating in the
		// names of graphs in the case of very large numbers
		// of graphs, few of which contain the resource, in
		// some kind of persistent storage.

		QuerySolutionMap qsm = new QuerySolutionMap() ;
		qsm.add("s", resource) ;
		QueryExecution qExec = QueryExecutionFactory.create(query, dataset, qsm) ;
		ResultSet rs = qExec.execSelect();
		for ( ; rs.hasNext() ; ) {
			QuerySolution qs = rs.next();
			Resource g = qs.getResource("g");
			String gName = g.getURI() ;
			Model model =  dataset.getNamedModel(gName) ;
			Resource g2 = otherModel(g, model);
			Resource r2 = otherModel(resource, model) ;
			Closure.closure(r2, false, acc) ;
			acc.add(g2, model.createProperty("http://open.vocab.org/terms/describes"), r2);
			acc.add(g2.listProperties(DCTerms.license));
			acc.add(g2.listProperties(VOID.inDataset));
		}

		//        // Named graphs
		//        for ( Iterator<String> iter = dataset.listNames() ; iter.hasNext() ; )
		//        {
		//            String name = iter.next();
		//            Model model =  dataset.getNamedModel(name) ;
		//            Resource r2 = otherModel(r, model) ;
		//            Closure.closure(r2, false, acc) ;
		//        }

		Closure.closure(resource, false, acc) ;	}

	@Override
	public void finish() {
		// Add labels
		Model model = dataset.getNamedModel("urn:x-arq:UnionGraph");
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
}
