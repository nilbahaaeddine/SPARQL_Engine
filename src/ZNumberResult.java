
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class ZNumberResult {
	public Model createModel(String pathData) {
		Model model = ModelFactory.createDefaultModel();
		String dataset = pathData;
		InputStream in = FileManager.get().open(dataset);
		model.read(in, null, "RDF/XML");

		return model;
	}

	public ResultSet selectJena(Query query, Model model) {
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		
		try {
			ResultSet results = qexec.execSelect();
			results = ResultSetFactory.copyResults(results);
			return results;
		} finally {
			qexec.close();
		}
	}

	public boolean askJena(Query query, Model model) {
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		
		try {
			boolean result = qexec.execAsk();
			return result;
		} finally {
			qexec.close();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String pathData = "Data/100K.rdfxml";
		String theQuery = "SELECT * WHERE { ?v0 ?v1 ?v2 . }";

		Query query = QueryFactory.create(theQuery);

		Jena jena = new Jena();
		
		Model model = jena.createModel(pathData);
		
		ResultSet resultSelect = jena.selectJena(query, model);
		
		ArrayList<String> tab = new ArrayList<String>();
		
		while(resultSelect.hasNext()) {
			String temp = resultSelect.next().toString();
			System.out.println(temp);
			temp = temp.replaceAll("\\s+", "").replaceAll("\\)\\(", ";").replaceAll("\\(", "").replaceAll("\\)", "").replace("?", "");
			tab.add(temp);
		}
		
		//System.out.println(tab.size());
		
		//tab.forEach((value) -> {System.out.println(value);});

		//ResultSetFormatter.out(System.out, rsSelect, query);
		//System.out.println(rsSelect.getRowNumber());
		
		//boolean rsAsk = jena.askJena(query, pathData);
		//System.out.println("Ask result = " + rsAsk);
	}
}