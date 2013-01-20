package uk.ac.ox.it.dataox.vocabulary;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class VOID {
	static final OntModel model = ModelFactory.createOntologyModel();
	
	public static final Property inDataset = model.getProperty("http://rdfs.org/ns/void#inDataset");

}
