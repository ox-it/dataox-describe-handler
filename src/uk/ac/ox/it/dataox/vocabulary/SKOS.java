package uk.ac.ox.it.dataox.vocabulary;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class SKOS {
	static final OntModel model = ModelFactory.createOntologyModel();
	
	public static final Property prefLabel = model.getProperty("http://www.w3.org/2004/02/skos/core#prefLabel");

}
