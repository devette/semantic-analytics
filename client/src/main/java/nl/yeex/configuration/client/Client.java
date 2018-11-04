package nl.yeex.configuration.client;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class Client {

    public void executeQuery(final String host, final int port)  {
        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
                .destination("http://" + host + ":" + port + "/sampleconfig/query");

       // Query query = QueryFactory.create("SELECT * { BIND('Hello'as ?text) }");
        Query query1 = QueryFactory.create(getQueryForSwitch("useNewIPO"));

        // In this variation, a connection is built each time.
        try (RDFConnectionFuseki connection = (RDFConnectionFuseki) builder.build() ) {
            connection.queryResultSet(query1, ResultSetFormatter::out);
        }

        // Query query = QueryFactory.create("SELECT * { BIND('Hello'as ?text) }");
        Query query2 = QueryFactory.create(getAccounts("friendsAndFamily"));

        // In this variation, a connection is built each time.
        try (RDFConnectionFuseki connection = (RDFConnectionFuseki) builder.build() ) {
            connection.queryResultSet(query2, ResultSetFormatter::out);
        }

    }

    public void executeDbPediaQuery()  {
        String queryStr = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                "SELECT ?country\n" +
                "WHERE {\n" +
                " ?country a dbo:Country\n" +
                "}";
        Query query = QueryFactory.create(queryStr);

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            ResultSetFormatter.out(System.out, rs, query);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getDbPediaQuery()  {
        return  "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                "PREFIX : <http://dbpedia.org/resource/>\n" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>\n"+
                "PREFIX dbpedia2: <http://dbpedia.org/property/>\n" +
                "PREFIX dbpedia: <http://dbpedia.org/>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
                "SELECT ?name ?birth ?description ?person WHERE {\n" +
                "      ?person a dbo:MusicalArtist .\n" +
                "      ?person dbo:birthPlace :Berlin .\n" +
                "      ?person dbo:birthDate ?birth .\n" +
                "      ?person foaf:name ?name .\n" +
                "      ?person rdfs:comment ?description .\n" +
                "      FILTER (LANG(?description) = 'en') . \n" +
                "} ORDER BY ?name";
    }


    public void getModel(final String host, final int port, final String dataset)  {
        DatasetAccessor datasetAccessor = DatasetAccessorFactory.createHTTP("http://" + host + ":" + port + "/" + dataset + "/data");
        Model model = datasetAccessor.getModel();
        model.write(System.out);
    }

    public String getQueryForSwitch(final String switchName)  {
        return "PREFIX config: <http://vocabulary.yeex.nl/configuration/v1#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "SELECT ?effectiveFrom ?effectiveUntil\n" +
                "WHERE {\n" +
                "  ?switch rdf:type config:Switch.\n" +
                "  ?switch config:name \"" + switchName + "\".\n" +
                "  ?switch config:hasRuleSet ?ruleset.\n" +
                "  ?ruleset config:effectiveFrom ?effectiveFrom.\n" +
                "  ?ruleset config:effectiveUntil ?effectiveUntil.\n" +
                "}";

    }

    public String getAccounts(final String accountGroupName)  {
       return "PREFIX config: <http://vocabulary.yeex.nl/configuration/v1#>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "SELECT ?iban ?bic\n" +
            "WHERE {\n" +
            "  ?accountgroup rdf:type config:AccountGroup.\n" +
            "  ?accountgroup config:name \""+ accountGroupName +"\".\n" +
            "  ?accountgroup config:hasAccount ?account.\n" +
            "  ?account config:iban ?iban.\n" +
            "  ?account config:bic ?bic\n" +
            "}";
    }

}
