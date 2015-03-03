package com.cmurphy;

import info.aduna.iteration.Iterations;

import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;

public class App {
    public static void main(String[] args) throws RepositoryException, RDFHandlerException {
        Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();

        ValueFactory factory = repository.getValueFactory();
        String namespace = "http://localhost/";

        URI john = factory.createURI(namespace, "john");

        RepositoryConnection connection = repository.getConnection();

        try {
            connection.add(john, RDF.TYPE, FOAF.PERSON);
            connection.add(john, RDFS.LABEL, factory.createLiteral("John", XMLSchema.STRING));

            RepositoryResult<Statement> statements = connection.getStatements(null, null, null, true);

            Model model = Iterations.addAll(statements, new LinkedHashModel());
            model.setNamespace("rdf", RDF.NAMESPACE);
            model.setNamespace("rdfs", RDFS.NAMESPACE);
            model.setNamespace("xsd", XMLSchema.NAMESPACE);
            model.setNamespace("foaf", FOAF.NAMESPACE);
            model.setNamespace("ex", namespace);

            Rio.write(model, System.out, RDFFormat.TURTLE);
        }
        finally {
            connection.close();
        }
    }

}
