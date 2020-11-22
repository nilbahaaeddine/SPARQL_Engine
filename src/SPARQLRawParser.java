
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;

public class SPARQLRawParser {
	public List<String> starQuery(List<StatementPattern> patterns, Projection projection) {
		ArrayList<ArrayList<String>> resultStarQuery = new ArrayList<ArrayList<String>>();
		List<String> intersect = null;

		for(int i = 0 ; i < patterns.size() ; i++) {
			ArrayList<String> resultPattern = new ArrayList<String>();
			String whichVar = "";
			String subject = "";
			String property = "";
			String object = "";

			System.out.println("\nPattern n° " + (i+1));

			// Check if subject is a variable
			if(patterns.get(i).getSubjectVar().getValue() == null)
				whichVar += "s";
			else
				subject = patterns.get(i).getSubjectVar().getValue().toString();

			//Check if property is a variable
			if(patterns.get(i).getPredicateVar().getValue() == null)
				whichVar += "p";
			else
				property = patterns.get(i).getPredicateVar().getValue().toString();

			//Check if object is a variable
			if(patterns.get(i).getObjectVar().getValue() == null)
				whichVar += "o";
			else
				object = patterns.get(i).getObjectVar().getValue().toString();

			// Check which index to use
			String whichIndex = this.getIndexQuery(whichVar);
			System.out.println("The chosen index : " + whichIndex);

			// Get ID of each element
			Integer subjectID = Dictionary.getInstance().dictionary.get(subject);
			Integer propertyID = Dictionary.getInstance().dictionary.get(property);
			Integer objectID = Dictionary.getInstance().dictionary.get(object);

			System.out.println("subjectID : " + subjectID + "\npropertyID : " + propertyID + "\nobjectID : " + objectID);
			if(whichVar.length() == 1) {
				if(Dictionary.getInstance().getStore(whichIndex).get(propertyID) != null) {
					try {
						for( Integer s : Dictionary.getInstance().getStore(whichIndex).get(propertyID).get(objectID) )
							resultPattern.add(getIndividual(s));
					} catch (Exception e) {
					}
				}
			}
			resultStarQuery.add(resultPattern);
		}

		if(resultStarQuery.size() > 1) {
			for(int i = 0 ; i < resultStarQuery.size() ; i++) {
				intersect = resultStarQuery.get(0).stream()
						.filter(resultStarQuery.get(i)::contains)
						.collect(Collectors.toList());
			}
		} else if(resultStarQuery.size() == 1){
			intersect = new ArrayList<String>(new LinkedHashSet<String>(resultStarQuery.get(0)));
		}

		return intersect;
	}

	public List<String> query(List<StatementPattern> patterns, Projection projection) {
		ArrayList<ArrayList<String>> resultStarQuery = new ArrayList<ArrayList<String>>();
		List<String> intersect = null;
		//System.out.println("HERE");
		//System.out.println(projection.getProjectionElemList().getElements().get(0).getSourceName());
		//for( Object s : projection.getProjectionElemList().getElements().get(1).getSourceName())
		//System.out.println(s);

		for(int i = 0 ; i < patterns.size() ; i++) {
			ArrayList<String> resultPattern = new ArrayList<String>();
			String whichVar = "";
			String subject = "";
			String property = "";
			String object = "";

			//System.out.println("\nTuple n° " + (i+1));

			// Check if subject is a variable
			if(patterns.get(i).getSubjectVar().getValue() == null)
				whichVar += "s";
			else
				subject = patterns.get(i).getSubjectVar().getValue().toString();

			//Check if property is a variable
			if(patterns.get(i).getPredicateVar().getValue() == null)
				whichVar += "p";
			else
				property = patterns.get(i).getPredicateVar().getValue().toString();

			//Check if object is a variable
			if(patterns.get(i).getObjectVar().getValue() == null)
				whichVar += "o";
			else
				object = patterns.get(i).getObjectVar().getValue().toString();

			// Check which index to use
			String whichIndex = this.getIndexQuery(whichVar);
			//System.out.println("The chosen index : " + whichIndex);

			// Get ID of each element
			Integer subjectID = Dictionary.getInstance().dictionary.get(subject);
			Integer propertyID = Dictionary.getInstance().dictionary.get(property);
			Integer objectID = Dictionary.getInstance().dictionary.get(object);

			//System.out.println("subjectID : " + subjectID + "\npropertyID : " + propertyID + "\nobjectID : " + objectID);
			if(whichVar.length() == 1) {
				if(whichIndex.charAt(2) == 's') {
					try {
						for( Integer s : Dictionary.getInstance().getStore(whichIndex).get(propertyID).get(objectID) )
							resultPattern.add("(" + whichIndex.charAt(2) + ";" + patterns.get(i).getSubjectVar().getName() + ")" + getIndividual(s));
					} catch (Exception e) {
					}
				} if(whichIndex.charAt(2) == 'p') {
					try {
						for( Integer p : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(objectID) )
							resultPattern.add("(" + whichIndex.charAt(2) + ";" + patterns.get(i).getPredicateVar().getName() + ")" + getIndividual(p));
					} catch (Exception e) {
					}
				} else if(whichIndex.charAt(2) == 'o') {
					try {
						for( Integer o : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(propertyID) )
							resultPattern.add("(" + whichIndex.charAt(2) + ";" + patterns.get(i).getObjectVar().getName() + ")" + getIndividual(o));
					} catch (Exception e) {
					}
				}
			} else if(whichVar.length() == 2) {
				if(whichIndex.charAt(2) == 's') {
					if(whichIndex.charAt(1) == 'p') {//object ops
						for( Object p : Dictionary.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
							for( Object s : Dictionary.getInstance().getStore(whichIndex).get(objectID).get(p) ) {
								resultPattern.add("(" + whichIndex.charAt(2)  + "=" + patterns.get(i).getSubjectVar().getName() + ";" + whichIndex.charAt(1) + "=" + patterns.get(i).getPredicateVar().getName() + ")" + getIndividual(s) + "\t" + getIndividual(p));
								//resultPattern.add("(" + whichIndex.charAt(2) + "=" + patterns.get(i).getSubjectVar().getName() + ";" + "" + ")" + getIndividual(s) + );
								//System.out.println(getIndividual(s) + "\t" + getIndividual(p));
							}
						}
					} else if(whichIndex.charAt(1) == 'o') {//property pos
						for( Object o : Dictionary.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
							for( Object s : Dictionary.getInstance().getStore(whichIndex).get(propertyID).get(o) ) {
								resultPattern.add("(" + whichIndex.charAt(2)  + "=" + patterns.get(i).getSubjectVar().getName() + ";" + whichIndex.charAt(1) + "=" + patterns.get(i).getObjectVar().getName() + ")" + getIndividual(s) + "\t" + getIndividual(o));
								//resultPattern.add("(" + whichIndex.charAt(2) + "=" + patterns.get(i).getSubjectVar().getName() + ";" + "" + ")" + getIndividual(s) + );
								//System.out.println(getIndividual(s) + "\t" + getIndividual(o));
							}
						}
					}
				}
				if(whichIndex.charAt(2) == 'p') {
					if(whichIndex.charAt(1) == 's') {//object osp
						for( Object s : Dictionary.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
							for( Object p : Dictionary.getInstance().getStore(whichIndex).get(objectID).get(s) ) {
								resultPattern.add("(" + whichIndex.charAt(2)  + "=" + patterns.get(i).getPredicateVar().getName() + ";" + whichIndex.charAt(1) + "=" + patterns.get(i).getSubjectVar().getName() + ")" + getIndividual(p) + "\t" + getIndividual(s));
								//resultPattern.add("(" + whichIndex.charAt(2) + "=" + patterns.get(i).getPredicateVar().getName() + ";" + "" + ")" + getIndividual(p) + );
								//System.out.println(getIndividual(p) + "\t" + getIndividual(s));
							}
						}
					} else if(whichIndex.charAt(1) == 'o') {//subject sop
						for( Object o : Dictionary.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
							for( Object p : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(o) ) {
								resultPattern.add("(" + whichIndex.charAt(2)  + "=" + patterns.get(i).getPredicateVar().getName() + ";" + whichIndex.charAt(1) + "=" + patterns.get(i).getObjectVar().getName() + ")" + getIndividual(p) + "\t" + getIndividual(o));
								//resultPattern.add("(" + whichIndex.charAt(2) + "=" + patterns.get(i).getPredicateVar().getName() + ";" + "" + ")" + getIndividual(p) + );
								//System.out.println(getIndividual(p) + "\t" + getIndividual(o));
							}
						}
					}
				}
				if(whichIndex.charAt(2) == 'o') {
					if(whichIndex.charAt(1) == 's') {//property pso
						for( Object s : Dictionary.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
							for( Object o : Dictionary.getInstance().getStore(whichIndex).get(propertyID).get(s) ) {
								resultPattern.add("(" + whichIndex.charAt(2)  + "=" + patterns.get(i).getObjectVar().getName() + ";" + whichIndex.charAt(1) + "=" + patterns.get(i).getSubjectVar().getName() + ")" + getIndividual(o) + "\t" + getIndividual(s));
								//resultPattern.add("(" + whichIndex.charAt(2) + "=" + patterns.get(i).getObjectVar().getName() + ";" + "" + ")" + getIndividual(o) + );
								//System.out.println(getIndividual(o) + "\t" + getIndividual(s));
							}
						}
					} else if(whichIndex.charAt(1) == 'p') {//subject spo
						for( Object p : Dictionary.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
							for( Object o : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(p) ) {
								resultPattern.add("(" + whichIndex.charAt(2)  + "=" + patterns.get(i).getObjectVar().getName() + ";" + whichIndex.charAt(1) + "=" + patterns.get(i).getPredicateVar().getName() + ")" + getIndividual(o) + "\t" + getIndividual(p));
								//resultPattern.add("(" + whichIndex.charAt(2) + "=" + patterns.get(i).getObjectVar().getName() + ";" + "" + ")" + getIndividual(o) + );
								//System.out.println(getIndividual(o) + "\t" + getIndividual(p));
							}
						}
					}
				}
			} else {
				System.out.println(whichIndex);
				int cpt = 0;
				for( Object s : Dictionary.getInstance().getStore(whichIndex).keySet().toArray() ) {
					for( Object p : Dictionary.getInstance().getStore(whichIndex).get(s).keySet().toArray() ) {
						for( Object o : Dictionary.getInstance().getStore(whichIndex).get(s).get(p) ) {
							resultPattern.add("(" + whichIndex.charAt(0) + "=" + patterns.get(i).getSubjectVar().getName() + ";" + whichIndex.charAt(1) + "=" + patterns.get(i).getPredicateVar().getName() + ";" + whichIndex.charAt(2) + "=" + patterns.get(i).getObjectVar().getName() + ")" + getIndividual(s) + getIndividual(p) + getIndividual(o));
						}
					}	
				}
				System.out.println(cpt);
			}
			resultStarQuery.add(resultPattern);	
		}

		if(resultStarQuery.size() > 1) {
			for(int i = 0 ; i < resultStarQuery.size() ; i++) {
				intersect = resultStarQuery.get(0).stream()
						.filter(resultStarQuery.get(i)::contains)
						.collect(Collectors.toList());
			}
		} else if(resultStarQuery.size() == 1){
			intersect = new ArrayList<String>(new LinkedHashSet<String>(resultStarQuery.get(0)));
		}

		return intersect;
	}

	public String getIndividual(Object value) {
		return Dictionary.getInstance().dictionaryInverse.get(value);
	}

	public String getIndexQuery(String varPos) {
		String index = "";
		switch(varPos) {
			case "s":
				index = "pos";
				break;
			case "p":
				index = "sop";
				break;
			case "o":
				index = "spo";
				break;
			case "sp":
				index = "pos";
				break;
			case "so":
				index = "pos";
				break;
			case "ps":
				index = "spo";
				break;
			case "spo":
				index = "spo";
				break;
		}
		return index;
	}

	public String[] parseQueries(String path) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(path));
		String lines = "";
		String line = "";
		while((line = in.readLine()) != null)
			lines += line;
		in.close();

		String[] queryList = lines.split("(?=SELECT)");
		return queryList;
	}
}