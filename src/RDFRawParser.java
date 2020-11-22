import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class RDFRawParser {
	public static Dictionary myDictionary;

	public static NumberFormat formatter = new DecimalFormat("#0.0000");

	public static Date date = new Date();
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	private static class RDFListener extends RDFHandlerBase {
		@Override
		public void handleStatement(Statement st) {
			myDictionary.creatDicitonaryAndIndex(st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
		}
	};

	public static void main(String args[]) throws IOException, ParseException {
		Options options = new Options();

		options.addOption( "VERBOSE", false,"Merge files request.")
		.addOption("JENA", false,"Copy files from file.")
		.addOption("t", false, "display current time")
		.addOption("s", false, "display current size");

		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("t")) {
			    System.out.println("HUHU");
			}
			
			if(cmd.hasOption("VERBOSE")) {
				throw new IllegalArgumentException("Must specify an input file 1.");
			}

			if(cmd.hasOption("JENA")) {
				throw new IllegalArgumentException("Must specify an input file 2.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		myDictionary = Dictionary.getInstance();

		Reader reader = new FileReader("Data/100K.rdfxml");
		org.openrdf.rio.RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);

		SPARQLRawParser moteur = new SPARQLRawParser();
		SPARQLParser sparqlParser = new SPARQLParser();
		String[] queryList = moteur.parseQueries("Queries/query.queryset");

		rdfParser.setRDFHandler(new RDFListener());

		try {
			// Start time
			long start = System.currentTimeMillis();
			// Parsing rdf
			rdfParser.parse(reader, "");
			// End time
			long end = System.currentTimeMillis();

			// Printing time
			System.out.println("Temps de la creation du dictionnaire et des indexes : " + formatter.format((end - start) / 1000d) + " secondes");

		} catch (Exception e) {

		}

		try {
			reader.close();
		} catch (IOException e) {

		}

		for (String query : queryList) {
			try {
				ParsedQuery pq = sparqlParser.parseQuery(query, null);

				List<StatementPattern> patterns = StatementPatternCollector.process(pq.getTupleExpr());

				System.out.println("\nTraitement de la requete : " + query);

				pq.getTupleExpr().visit(new QueryModelVisitorBase<RuntimeException>() {
					public void meet(Projection projection) {
						// Start time
						long start = System.currentTimeMillis();
						// Engine
						List<String> result = moteur.query(patterns, projection);
						// End time
						long end = System.currentTimeMillis();

						//Printing time & results
						System.out.println("Resultats (temps d'execution : " + formatter.format((end - start) / 1000d) + " secondes" + ") :");



						try(FileWriter fw = new FileWriter("Output/" + dateFormat.format(date) + ".txt", true); BufferedWriter bw = new BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {
							out.write(query + "\n");
							if(result.isEmpty())
								out.write("\tPas de solution\n");
							else {
								out.write("\tSolution(s) :\n");
								result.forEach((value) -> {out.write("\t\t" + projection.getProjectionElemList().getElements().get(0).getSourceName() + " : " + value + "\n");});
							}
							out.write("\n-----------------------------------------------------------------------------------------------------------------------\n\n");
						} catch (IOException e) {
							//exception handling left as an exercise for the reader
						}

						if(result.isEmpty())
							System.out.println("\tPas de solution");
						else {
							result.forEach((value) -> {System.out.println("\t" + projection.getProjectionElemList().getElements().get(0).getSourceName() + " : " + value);});
							System.out.println("Nbr de solution : " + result.size());
						}

					}
				});

			} catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {

			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("s")) {
			    System.out.println("Ramzi");
			}
			
			if(cmd.hasOption("VERBOSE")) {
				throw new IllegalArgumentException("Must specify an input file 1.");
			}

			if(cmd.hasOption("JENA")) {
				throw new IllegalArgumentException("Must specify an input file 2.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}