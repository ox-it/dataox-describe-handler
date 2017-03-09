package uk.ac.ox.it.dataox.vocabulary;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class OV {
	static final OntModel model = ModelFactory.createOntologyModel();
	
	public static final Property describes = model.getProperty("http://open.vocab.org/terms/describes");
	public static final Resource RequestedDocument = model.createClass("http://open.vocab.org/terms/RequestedDocument");

}
