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
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class Jena {
	public QueryExecution checkJena(Query query, String pathData) {
		//Create a data model and load file
		Model model = ModelFactory.createDefaultModel();
		String dataset = pathData;
		InputStream in = FileManager.get().open(dataset);
		model.read(in, null, "RDF/XML"); 

		return QueryExecutionFactory.create(query, model);
	}

	public ResultSet selectJena(Query query, String pathData) {
		QueryExecution qexec = checkJena(query, pathData);
		try {
			ResultSet results = qexec.execSelect();
			results = ResultSetFactory.copyResults(results);
			return results;
		} finally {
			qexec.close();
		}
	}

	public boolean askJena(Query query, String pathData) {
		QueryExecution qexec = checkJena(query, pathData);
		try {
			boolean result = qexec.execAsk();
			return result;
		} finally {
			qexec.close();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String theQuery = "SELECT * WHERE {  <http://db.uwaterloo.ca/~galuc/wsdbm/SubGenre54> <http://ogp.me/ns#tag> ?v1 .  ?v0 <http://ogp.me/ns#tag> ?v1 .  ?v0 <http://schema.org/description> ?v3 .  ?v0 <http://schema.org/expires> ?v4 .  }";
		String pathData = "Data/1M.rdfxml.rdf";

		Query query = QueryFactory.create(theQuery);

		Jena jena = new Jena();

		ResultSet rsSelect = jena.selectJena(query, pathData);
		ResultSetFormatter.out(System.out, rsSelect, query);
		
		boolean rsAsk = jena.askJena(query, pathData);
		System.out.println("Ask result = " + rsAsk);
	}
}