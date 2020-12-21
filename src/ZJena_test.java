
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;

public class ZJena_test {
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

	public static String[] parseQueries(String path) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(path));

		String lines = "";
		String line = "";

		while((line = in.readLine()) != null)
			lines += line;
		in.close();

		String[] queryList = lines.split("(?=SELECT)");

		return queryList;
	}
	
	public static String is_it_null(Var var) {
		if(var.getValue()==null)
			return var.getName();
		else
			return var.getValue().toString();
	}

	public static Boolean is_it_starQuery(List<StatementPattern> patterns) {

		ArrayList<String> list_Index_pattern = new ArrayList<String>();

		if(patterns.get(0).getSubjectVar().getValue()==null)
			list_Index_pattern.add(patterns.get(0).getSubjectVar().getName().toString());
		if(patterns.get(0).getPredicateVar().getValue()==null)
			list_Index_pattern.add(patterns.get(0).getPredicateVar().getName().toString());
		if(patterns.get(0).getObjectVar().getValue()==null)
			list_Index_pattern.add(patterns.get(0).getObjectVar().getName().toString());
		int i=1, j=0;
		//System.out.println("list_Index_pattern : "+ list_Index_pattern);

		while(i!=patterns.size() && j<list_Index_pattern.size()) {

			if(list_Index_pattern.get(j).equals(is_it_null(patterns.get(i).getSubjectVar()))) {
				i++;}
			else if (list_Index_pattern.get(j).equals(is_it_null(patterns.get(i).getPredicateVar()))){
				i++;}
			else if (list_Index_pattern.get(j).equals(is_it_null(patterns.get(i).getObjectVar()))) {
				i++;}
			else if(j==list_Index_pattern.size()-1){
				return false;
			}
			else {
				j++;
				i=1;
			}
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		int cpt = 0;

		String pathData = "Data/watdiv/test_2M.rdf";

		File file = new File("Queries/watdiv/testMerged.queryset");

		Jena jena = new Jena();

		Model model = jena.createModel(pathData);

		System.out.println("Model cree");

		if(file.isFile()) {
			System.out.println(file.getName());
			String[] queryList = parseQueries("Queries/watdiv/testMerged.queryset");
			System.out.println("Requetes parsees");
			cpt = 1;
			for(String theQuery : queryList ) {
				//System.out.println(cpt + "/" + queryList.length);
				Query query = QueryFactory.create(theQuery);

				ResultSet resultSelect = jena.selectJena(query, model);

				if(!resultSelect.hasNext()) {
					cpt++;
					//System.out.println(resultSelect.nextSolution());
					// Ecriture dans le fichier
					try(FileWriter fw = new FileWriter("Output/NO.queryset", true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw)) {
						out.write(theQuery + "\n");

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println(cpt);
		}
	}
}