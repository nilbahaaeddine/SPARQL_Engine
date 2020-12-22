
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
	public static long start = 0;
	public static long end = 0;
	public static long jenaTime = 0;
	
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
			start = System.currentTimeMillis();
			ResultSet results = qexec.execSelect();
			end = System.currentTimeMillis();

			jenaTime += end - start;
			
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