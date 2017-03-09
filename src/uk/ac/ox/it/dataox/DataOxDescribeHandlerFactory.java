package uk.ac.ox.it.dataox;

import org.apache.jena.sparql.core.describe.DescribeHandler;
import org.apache.jena.sparql.core.describe.DescribeHandlerFactory;
import org.apache.jena.sparql.core.describe.DescribeHandlerRegistry;

public class DataOxDescribeHandlerFactory implements DescribeHandlerFactory {
	static {
		DescribeHandlerRegistry registry = DescribeHandlerRegistry.get();
		DescribeHandlerFactory factory = new DataOxDescribeHandlerFactory();
		registry.add(factory);
	}

	@Override
	public DescribeHandler create() {
		return new DataOxDescribeHandler();
	}

}
