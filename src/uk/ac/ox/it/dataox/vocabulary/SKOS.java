package uk.ac.ox.it.dataox.vocabulary;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class SKOS {
	static final OntModel model = ModelFactory.createOntologyModel();
	
	public static final Property prefLabel = model.getProperty("http://www.w3.org/2004/02/skos/core#prefLabel");

}
