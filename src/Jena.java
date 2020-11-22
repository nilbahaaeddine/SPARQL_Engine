/*	Jena File from the semantic web course, #TP2
 *
 *	By : - NIL Bahaa Eddine (bahaa-eddine.nil@etu.umontpellier.fr)
 *
 *	Work submitted to Moodle on 20 February 2020
 */

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class Jena {
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String theQuery = "SELECT ?v0 WHERE { ?v0 <http://schema.org/eligibleRegion> <http://db.uwaterloo.ca/~galuc/wsdbm/Country137> . }";
		String pathData = "Data/100K.rdfxml";

		//Create a data model and load file
		Model model = ModelFactory.createDefaultModel();
		String dataset = pathData;
		InputStream in = FileManager.get().open(dataset);
		model.read(in, null, "RDF/XML"); 

		//Create a query object
		Query query = QueryFactory.create(theQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		//Execute query and print result
		try {
			//SELECT
			ResultSet rs = qexec.execSelect();
			ResultSetFormatter.out(System.out, rs, query);
			//ASK
			//boolean rs = qexec.execAsk();
			//System.out.println("Ask result = " + rs);
		} finally {
			qexec.close();
		}
	}
}