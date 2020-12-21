
import java.io.InputStream;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class Jena {
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
}