package uk.ac.ox.it.dataox.vocabulary;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class OV {
	static final OntModel model = ModelFactory.createOntologyModel();
	
	public static final Property describes = model.getProperty("http://open.vocab.org/terms/describes");
	public static final Resource RequestedDocument = model.createClass("http://open.vocab.org/terms/RequestedDocument");

}
