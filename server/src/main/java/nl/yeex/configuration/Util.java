package nl.yeex.configuration;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.io.PrintStream;

public class Util {

    public static void printAllStatements(Model model, PrintStream out) {
        // list the statements in the graph
        StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement statement      = iter.nextStatement();         // get next statement
            Resource subject   = statement.getSubject();   // get the subject
            Property predicate = statement.getPredicate(); // get the predicate
            RDFNode object    = statement.getObject();    // get the object

            out.print(subject.toString());
            out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                out.print(object.toString());
            } else {
                // object is a literal
                out.print(" \"" + object.toString() + "\"");
            }
            out.println(" .");
        }
    }
}
