data.ox.ac.uk describe handler
==============================

Adds graph information to describe results (for each described resource, we add
?graph ov:describes ?resource, for all ?graph where ?resource appears as a
subject), add labels and rdf:type properties for every resource appearing in
the model.

Usage
-----

Add the following to your Fuseki config file::

   [] ja:loadClass "uk.ac.ox.it.dataox.DataOxDescribeHandlerFactory" .

