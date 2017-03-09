package uk.ac.ox.it.dataox.vocabulary;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class VOID {
	static final OntModel model = ModelFactory.createOntologyModel();
	
	public static final Property inDataset = model.getProperty("http://rdfs.org/ns/void#inDataset");

}
