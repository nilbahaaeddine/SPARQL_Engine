
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

import com.opencsv.CSVWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public final class Main {
	static Jena jena = new Jena();
	public static Dictionary myDictionary;
	public static Index myIndex;

	public static NumberFormat formatter = new DecimalFormat("#0.00");

	public static Date date = new Date();
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public static long start = 0;
	public static long end = 0;
	public static long allQueriesTimeEngine = 0;
	public static long thisQueryTimeEngine = 0;
	public static long allQueriesTimeJena = 0;
	public static long oneQueryTimeJena = 0;
	public static long creatingDictionaryIndexTime = 0;
	public static long parsingQueriesTime = 0;
	public static long startProgramTime = 0;
	public static long endProgramTime = 0;

	public static String pathOutput;
	public static String pathData;
	public static String pathQueries;
	public static boolean verbose = false;

	public static boolean jenaVerification = true;

	public static String[] header = {"Nom du fichier de données", "Nom du dossier des requêtes", "Nombre de triplets RDF", "Nombre de requêtes", "Temps de lecture des requêtes (ms)", "Temps création dico (ms)", "Nombre d’index", "Temps total création des index (ms)", "Temps d’évaluation du workload (ms)", "Temps pris par l’optimisation (ms)", "Temps total (du début à la fin du programme) (ms)"};
	public static ArrayList<String> dataOfCSV;
	public static String[] myList = new String[11];

	public static int numberOfCurrentQuery = 0;
	public static int queriesWithoutAnswer = 0;
	public static int numberOfStarQueries = 0;
	public static int currentQuery = 0;

	public static ArrayList<String> wrongQueryList = new ArrayList<String>();

	public static Model model;

	private static class RDFListener extends RDFHandlerBase {
		@Override
		public void handleStatement(Statement st) {
			myDictionary.creatDicitonary(st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
			myIndex.creatIndex(st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
		}
	};

	public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
		return new HashSet<>(list1).equals(new HashSet<>(list2));
	}

	public static List<String> sortVariables(List<String> list) {
		ArrayList<String> result = new ArrayList<String>();
		for( String s : list ) {
			String enfin = "";
			String[] temp = s.split(";");
			Arrays.sort(temp);
			enfin += temp[0];
			for( int i = 1 ; i < temp.length ; i++ ) {
				enfin += ";" + temp[i];
			}
			result.add(enfin);
		}
		return result;
	}

	public static String getIndividual(Object value) {
		return Dictionary.getInstance().dictionaryInverse.get(value);
	}

	public static String getVariable(Integer value) {
		return Dictionary.getInstance().dictionaryInverseOfVariables.get(value);
	}

	public static String[] readQueries(String path) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(path));

		ArrayList<String> temp = new ArrayList<String>();
		String line = "";

		while((line = in.readLine()) != null)
			temp.add(line);
		in.close();

		String[] queryList = new String[temp.size()];
		queryList = temp.toArray(queryList);

		return queryList;
	}
	
	public static String[] readQueriesBis(String path) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(path));

		String lines = "";
		String line = "";

		while((line = in.readLine()) != null)
			lines += line;
		in.close();

		String[] queryList = lines.split("(?=SELECT)");

		return queryList;
	}

	public static String isNnull(Var var) {
		if(var.getValue()==null)
			return var.getName();
		else
			return var.getValue().toString();
	}

	public static Boolean isStarQuery(List<StatementPattern> patterns) {
		ArrayList<String> list_Index_pattern = new ArrayList<String>();

		if(patterns.get(0).getSubjectVar().getValue()==null)
			list_Index_pattern.add(patterns.get(0).getSubjectVar().getName().toString());
		if(patterns.get(0).getPredicateVar().getValue()==null)
			list_Index_pattern.add(patterns.get(0).getPredicateVar().getName().toString());
		if(patterns.get(0).getObjectVar().getValue()==null)
			list_Index_pattern.add(patterns.get(0).getObjectVar().getName().toString());
		int i=1, j=0;

		while(i!=patterns.size() && j<list_Index_pattern.size()) {
			if(list_Index_pattern.get(j).equals(isNnull(patterns.get(i).getSubjectVar()))) {
				i++;
			} else if (list_Index_pattern.get(j).equals(isNnull(patterns.get(i).getPredicateVar()))){
				i++;
			} else if (list_Index_pattern.get(j).equals(isNnull(patterns.get(i).getObjectVar()))) {
				i++;
			} else if(j==list_Index_pattern.size()-1){
				return false;
			} else {
				j++;
				i=1;
			}
		}
		return true;
	}

	public static List<ParsedQuery> selectRandomStringlements(List<ParsedQuery> parsedQueries, int amount) {
		List<ParsedQuery> selected = new ArrayList<>();
		Random random = new Random();
		int listSize = parsedQueries.size();

		// Avoid a deadlock
		if(amount >= listSize) {
			return parsedQueries;
		}

		// Get a random item until we got the requested amount
		while(selected.size() < amount) {
			selected.add(parsedQueries.get(random.nextInt(listSize)));
		}

		return selected;
	}

	public static void main(String args[]) throws IOException, ParseException {
		startProgramTime = System.currentTimeMillis();
		org.openrdf.rio.RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
		SPARQLParser sparqlParser = new SPARQLParser();
		rdfParser.setRDFHandler(new RDFListener());

		myDictionary = Dictionary.getInstance();
		myIndex = Index.getInstance();
		Engine moteur = new Engine();

		pathData = "Data/100K.rdfxml";
		pathQueries = "Queries/10K.queryset";
		pathOutput =  "Output/";

		Options options = new Options();

		options
		.addOption("queries", true,"Merge files request.")
		.addOption("data", true,"Copy files from file.")
		.addOption("output", true, "display current time")
		.addOption("verbose", false, "display current size")
		.addOption("export_query_stats", false, "display current size")
		.addOption("export_query_results", false, "display current size")
		.addOption("workload_time", false, "display current size")
		.addOption("jena", false, "display current size")
		.addOption("shuffle", false, "display current size")
		.addOption("warm", true, "display current size")
		.addOption("optim_none", false, "display current size")
		.addOption("star_queries", false, "display current size");

		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			if(cmd.hasOption("optim_none") && cmd.hasOption("star_queries")) {
				System.out.println("Impossible de choisir les deux options suivantes ensemble : star_queries & optim_none\n");
				System.exit(0);
			}

			if(cmd.hasOption("queries")) {
				pathQueries = "Queries/" + cmd.getOptionValue("queries");
			}

			if(cmd.hasOption("data")) {
				pathData = "Data/" + cmd.getOptionValue("data");
			}

			if(cmd.hasOption("output")) {
				pathOutput = cmd.getOptionValue("output") + "/";
			}

			if(cmd.hasOption("verbose")) {
				verbose = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Dictionary & index
		System.out.println("Creation du dictionnaire et des index...\n");
		try {
			Reader reader = new FileReader(pathData);

			start = System.currentTimeMillis();
			rdfParser.parse(reader, "");
			end = System.currentTimeMillis();

			creatingDictionaryIndexTime = end - start;

			reader.close();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		// Reading queries
		System.out.println("Lecture des requetes...\n");
		String[] queryList = readQueriesBis(pathQueries);
		
		
		// Parsing queries
		System.out.println("Parsing des requetes...\n");
		List<ParsedQuery> parsedQueries = new ArrayList<ParsedQuery>();
		for(String query : queryList) {
			try {
				start = System.currentTimeMillis();
				ParsedQuery pq = sparqlParser.parseQuery(query, null);
				parsedQueries.add(pq);
				end = System.currentTimeMillis();

				parsingQueriesTime += end - start;
			} catch(MalformedQueryException e) {
				e.printStackTrace();
			}
		}

		// Warm option
		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("warm")) {
				System.out.println("Echauffement du moteur...\n");
				String percentageWarm = cmd.getOptionValue("warm");

				List<ParsedQuery> randomQueriesList = selectRandomStringlements(parsedQueries, ((Integer.parseInt(percentageWarm) * parsedQueries.size()) / 100));

				// Warm up the engine
				for(ParsedQuery query : randomQueriesList) {
					List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

					boolean isStarQuery = isStarQuery(patterns);

					if(isStarQuery) {
						numberOfStarQueries++;
					}

					query.getTupleExpr().visit(new QueryModelVisitorBase<RuntimeException>() {
						public void meet(Projection projection) {
							try {
								ArrayList<ArrayList<Integer>> resultAsArrayOfArraysEngine = null;
								CommandLine cmd = parser.parse(options, args);
								if(cmd.hasOption("star_queries")) {
									if(isStarQuery) {//Star
										start = System.currentTimeMillis();
										resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
										end = System.currentTimeMillis();
									}
								} else {
									if(cmd.hasOption("optim_none")) {//NonOpt
										start = System.currentTimeMillis();
										resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
										end = System.currentTimeMillis();
									} else {
										if(!isStarQuery) {//Query
											start = System.currentTimeMillis();
											resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
											end = System.currentTimeMillis();
										} else {//Star
											start = System.currentTimeMillis();
											resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
											end = System.currentTimeMillis();
										}
									}
								}
								resultAsArrayOfArraysEngine.clear();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Shuffle option
		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("shuffle")) {
				Collections.shuffle(Arrays.asList(queryList), new Random());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Creating Jena Model
		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("jena")) {
				model = jena.createModel(pathData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Traiter requete par requete
		System.out.println("Traitement de " + parsedQueries.size() + " requetes...\n");
		for (ParsedQuery query : parsedQueries) {
			//System.out.println(currentQuery+1);
			start = System.currentTimeMillis();
			//ParsedQuery pq = sparqlParser.parseQuery(query, null);
			end = System.currentTimeMillis();

			parsingQueriesTime += end - start;

			List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

			boolean isStarQuery = isStarQuery(patterns);

			if(isStarQuery) {
				numberOfStarQueries++;
			}

			query.getTupleExpr().visit(new QueryModelVisitorBase<RuntimeException>() {
				public void meet(Projection projection) {
					long start = 0;
					long end = 0;

					ArrayList<ArrayList<Integer>> resultAsArrayOfArraysEngine = null;
					ArrayList<String> resultAsArrayEngine = new ArrayList<String>();
					ArrayList<String> resultAsArrayEngineWithOnlyProjectedVariable = new ArrayList<String>();

					try {
						CommandLine cmd = parser.parse(options, args);
						if(cmd.hasOption("star_queries")) {
							if(isStarQuery) {//Star
								start = System.currentTimeMillis();
								resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
								end = System.currentTimeMillis();
							} else {
								System.out.println("La requete " + queryList[currentQuery].replaceAll("\\s+", " ") + " n'est pas une requete en etoile\n");
							}
						} else {
							if(cmd.hasOption("optim_none")) {//NonOpt
								start = System.currentTimeMillis();
								resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
								end = System.currentTimeMillis();
							} else {
								if(!isStarQuery) { //Query
									start = System.currentTimeMillis();
									resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
									end = System.currentTimeMillis();
								} else { //Star
									start = System.currentTimeMillis();
									resultAsArrayOfArraysEngine = moteur.query(patterns, projection);
									end = System.currentTimeMillis();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					allQueriesTimeEngine += end - start;
					thisQueryTimeEngine = end - start;

					// Creating our new result
					for(ArrayList<Integer> element : resultAsArrayOfArraysEngine) {
						String s = "";
						for(int i = 0 ; i < element.size() ; i += 2) {
							s += getVariable(element.get(i)) + "=" + getIndividual(element.get(i+1)) + ";";
						}
						s = s.substring(0, s.length() - 1);
						resultAsArrayEngine.add(s.replaceAll("\\s+", ""));
					}

					if(resultAsArrayOfArraysEngine.size() == 0) {
						queriesWithoutAnswer++;
					}

					// Sorting our engine result
					resultAsArrayEngine = (ArrayList<String>) sortVariables(resultAsArrayEngine);

					// Taking the select variables
					for(String element : resultAsArrayEngine) {
						String[] temp = element.split(";");
						String finalResultHere = "";
						for(int i = 0 ; i < temp.length ; i++) {
							String[] autreTemp = temp[i].split("=");
							if(projection.getProjectionElemList().getTargetNames().contains(autreTemp[0])) {
								finalResultHere += temp[i] + ";";
							}
						}
						resultAsArrayEngineWithOnlyProjectedVariable.add(finalResultHere.substring(0, finalResultHere.length()-1));
					}

					try {
						CommandLine cmd = parser.parse(options, args);

						if(cmd.hasOption("jena")) {
							ArrayList<String> resultJena = new ArrayList<String>();
							Query jenaQuery = QueryFactory.create(queryList[currentQuery]);

							start = System.currentTimeMillis();
							ResultSet resultSelect = jena.selectJena(jenaQuery, model);
							end = System.currentTimeMillis();

							allQueriesTimeJena += end - start;
							oneQueryTimeJena = end - start;

							// Creating jena results
							while(resultSelect.hasNext()) {
								String temp = resultSelect.next().toString();
								temp = temp.replaceAll("\\s+", "").replaceAll("\\)\\(", ";").replaceAll("\\(", "").replaceAll("\\)", "").replace("?", "").replace(">", "").replace("<", "").replace("\"", "");
								resultJena.add(temp);
							}

							// Sorting jena result
							resultJena = (ArrayList<String>) sortVariables(resultJena);

							// Checking if our engine is giving the same result as Jena
							if(resultAsArrayEngineWithOnlyProjectedVariable.size() == resultJena.size()) {
								if(!listEqualsIgnoreOrder(resultAsArrayEngineWithOnlyProjectedVariable, resultJena)) {
									jenaVerification = false;
									wrongQueryList.add(queryList[currentQuery]);
								}
							}
							else {
								jenaVerification = false;
								wrongQueryList.add(queryList[currentQuery]);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Ecriture dans le fichier
					try(FileWriter fw = new FileWriter(pathOutput + dateFormat.format(date) + ".txt", true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw)) {
						out.write(queryList[currentQuery] + "\n");
						if(resultAsArrayOfArraysEngine.isEmpty())
							out.write("\tPas de solution\n");
						else {
							try {
								CommandLine cmd = parser.parse(options, args);
								if(cmd.hasOption("export_query_stats")) {
									out.write(resultAsArrayEngineWithOnlyProjectedVariable.size() + " solutions trouvées en : " + formatter.format(thisQueryTimeEngine / 1000d) + "s, en utilisant l'index : " + moteur.whichIndex + "\n");
									resultAsArrayEngine.forEach((value) -> {
										out.write("\t" + value + "\n");
									});
								} else {
									resultAsArrayEngineWithOnlyProjectedVariable.forEach((value) -> {
										out.write("\t" + value + "\n");
									});
								}
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
						out.write("\n-----------------------------------------------------------------------------------------------------------------------\n\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						CommandLine cmd = parser.parse(options, args);
						if(cmd.hasOption("export_query_results")) {
							File file = new File(pathOutput + "export_query_results_" + dateFormat.format(date) + ".csv");
							try {
								// create FileWriter object with file as parameter 
								FileWriter outputfile = new FileWriter(file, true); 

								// create CSVWriter object filewriter object as parameter  
								CSVWriter writer = new CSVWriter(outputfile, ';', 
										CSVWriter.NO_QUOTE_CHARACTER, 
										CSVWriter.DEFAULT_ESCAPE_CHARACTER, 
										CSVWriter.DEFAULT_LINE_END);

								String[] queryToArray = new String[1];
								String[] noSolution = {"Aucune solution"};
								String[] newLine = {""};

								queryToArray[0] = queryList[currentQuery];

								writer.writeNext(queryToArray);

								if(resultAsArrayEngine.size() == 0)
									writer.writeNext(noSolution);
								else {
									for( String tuple : resultAsArrayEngineWithOnlyProjectedVariable ) {
										String[] temp = new String[2];
										temp[1] = tuple;
										writer.writeNext(temp);
									}
								}

								writer.writeNext(newLine);

								writer.close();
							} 
							catch (IOException e) {
								e.printStackTrace();
							} 
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			currentQuery++;
		}

		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("workload_time")) {
				System.out.println("Toutes les requetes ont ete traitees en : " + formatter.format(allQueriesTimeEngine / 1000d) + " secondes\n");
			}
		} catch(Exception e) {

		}

		// Verbose option
		if(verbose) {
			myList[0] = pathData.split("/")[1];
			myList[1] = pathQueries.split("/")[0];
			myList[2] = String.valueOf(myDictionary.dictionary.size());
			myList[3] = String.valueOf(queryList.length);
			myList[4] = String.valueOf(parsingQueriesTime);
			myList[5] = String.valueOf(myDictionary.dictionaryTime);
			myList[6] = "6";
			myList[7] = String.valueOf(myIndex.indexTime);
			myList[8] = String.valueOf(allQueriesTimeEngine);
			try {
				CommandLine cmd = parser.parse(options, args);
				if(cmd.hasOption("optim_none"))
					myList[9] = "NON_DISPONIBLE";
				else
					myList[9] = "TODO";
			} catch (Exception e) {
				e.printStackTrace(); 
			}
			myList[10] = String.valueOf(System.currentTimeMillis() - startProgramTime);

			File file = new File(pathOutput + "verbose_" + dateFormat.format(date) + ".csv");
			try {
				// create FileWriter object with file as parameter 
				FileWriter outputfile = new FileWriter(file); 

				// create CSVWriter object filewriter object as parameter  
				CSVWriter writer = new CSVWriter(outputfile, ';', 
						CSVWriter.NO_QUOTE_CHARACTER, 
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, 
						CSVWriter.DEFAULT_LINE_END);

				writer.writeNext(header);
				writer.writeNext(myList);
				writer.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
		}

		endProgramTime = System.currentTimeMillis();
		long allProgramTime = endProgramTime - startProgramTime;

		// Affichage
		System.out.println("Dictionnaire cree en : " + myDictionary.dictionaryTime + " ms\n");
		System.out.println("Index crees en : " + myIndex.indexTime + " ms\n");
		System.out.println("Le temps de notre moteur : " + formatter.format(allQueriesTimeEngine / 1000d) + " secondes\n");
		try {
			CommandLine cmd = parser.parse(options, args);

			if(cmd.hasOption("jena")) {
				System.out.println("Jena : " + formatter.format(allQueriesTimeJena / 1000d) + " secondes");

				if(jenaVerification)
					System.out.println("Le resultat est similaire a celui de Jena\n");
				else {
					System.out.println("Le resultat n'est pas similaire a celui de Jena");
					System.out.println("Il y a " + wrongQueryList.size() + " requetes avec reponses fausses\n");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		System.out.println("Il y a " + queriesWithoutAnswer + " requetes avec 0 reponses\n");
		System.out.println("Il y a " + numberOfStarQueries + " requetes en etoile et " + (queryList.length - numberOfStarQueries) + " requetes generales\n");
		
		System.out.println("Le temps de tout le programme : " + formatter.format(allProgramTime / 1000d) + " secondes");
	}
}