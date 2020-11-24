
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;

public class SPARQLRawParser {





	public static ArrayList<ArrayList<String>> Join(ArrayList<ArrayList<String>> T0,ArrayList<ArrayList<String>> T1) {
//		if (T0==null)
//			return T1;
//		else if(T1==null)
//			return T0;
		ArrayList<ArrayList<ArrayList<String>>> resultStarQuery = new ArrayList<ArrayList<ArrayList<String>>>();
		resultStarQuery.addAll(Arrays.asList(T0,T1));
		ArrayList<ArrayList<String>> T0_1 = new ArrayList<ArrayList<String>>();
		//System.out.println("\nT0 : "+T0);
		//System.out.println("\nT1 : "+T1);
		//System.out.println("\n*******************************************************************************************************\n");
		for (ArrayList<String> triplet0 : T0) {
			for (ArrayList<String> triplet1 : T1) {
				//System.out.println("\ntriplet0 : "+triplet0);
				//System.out.println("\ntriplet1 : "+triplet1);
				
				List<String> Fusion_T0_1 =  Stream.concat(triplet0.stream(), triplet1.stream()).collect(Collectors.toList());
				//System.out.println("\nFusion_T0_1 : "+Fusion_T0_1);
				List<String> new_Fusion_T0_1 = Fusion_T0_1.stream().distinct().collect(Collectors.toList());
				//System.out.println("\nnew_Fusion_T0_1 : "+new_Fusion_T0_1+"\n");

				if(new_Fusion_T0_1.size()==triplet0.size()+triplet1.size() || new_Fusion_T0_1.size()% 2 == 0 )
				{
					T0_1.add((ArrayList<String>) new_Fusion_T0_1);
					//System.out.println("\nT0_1 : "+new_Fusion_T0_1+"\n\n");
				}
				//System.out.println("***********************************************************************************");
			}
		}
		//System.out.println("\n*******************************************************************************************************\n");
		return T0_1;
	}










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

	public ArrayList<ArrayList<String>> query(List<StatementPattern> patterns, Projection projection) {
		ArrayList<ArrayList<String>> resultStarQuery = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> intersect = null;
		//System.out.println("HERE");
		//System.out.println(projection.getProjectionElemList().getElements().get(0).getSourceName());
		//for( Object s : projection.getProjectionElemList().getElements().get(1).getSourceName())
		//System.out.println(s);
		int Round = 0;
		for(int i = 0 ; i < patterns.size() ; i++) {
			ArrayList<ArrayList<String>> resultPattern = new ArrayList<ArrayList<String>>();
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
						{
							String[] pattern = {patterns.get(i).getSubjectVar().getName(),getIndividual(s)};
							resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						String[] pattern2 = {patterns.get(i).getSubjectVar().getName(),"NULL"};
						resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
					}
				} if(whichIndex.charAt(2) == 'p') {
					try {
						for( Integer p : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(objectID) )
						{
							String[] pattern = {patterns.get(i).getPredicateVar().getName(),getIndividual(p)};
							resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						String[] pattern2 = {patterns.get(i).getPredicateVar().getName(),"NULL"};
						resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
					}
				}
				else if(whichIndex.charAt(2) == 'o') {
					try {
						for( Integer o : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(propertyID) )
						{
							String[] pattern = {patterns.get(i).getObjectVar().getName(),getIndividual(o)};
							resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						String[] pattern2 = {patterns.get(i).getObjectVar().getName(),"NULL"};
						resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
					}
				}
			} else if(whichVar.length() == 2) {
				if(whichIndex.charAt(2) == 's') {
					if(whichIndex.charAt(1) == 'p') {//object ops
						for( Object p : Dictionary.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
							for( Object s : Dictionary.getInstance().getStore(whichIndex).get(objectID).get(p) ) {
								String[] pattern = {patterns.get(i).getSubjectVar().getName(),getIndividual(s),patterns.get(i).getPredicateVar().getName(),getIndividual(p)};
								resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty())
								{
									String[] pattern2 = {patterns.get(i).getSubjectVar().getName(),"NULL",patterns.get(i).getPredicateVar().getName(),"NULL"};
									resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
								}
							}
						}
					} else if(whichIndex.charAt(1) == 'o') {//property pos
						for( Object o : Dictionary.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
							for( Object s : Dictionary.getInstance().getStore(whichIndex).get(propertyID).get(o) ) {
								String[] pattern = {patterns.get(i).getSubjectVar().getName(),getIndividual(s),patterns.get(i).getObjectVar().getName(),getIndividual(o)};
								resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty())
								{
									String[] pattern2 = {patterns.get(i).getSubjectVar().getName(),"NULL",patterns.get(i).getObjectVar().getName(),"NULL"};
									resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
								}
							}
						}
					}
				}
				if(whichIndex.charAt(2) == 'p') {
					if(whichIndex.charAt(1) == 's') {//object osp
						for( Object s : Dictionary.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
							for( Object p : Dictionary.getInstance().getStore(whichIndex).get(objectID).get(s) ) {
								String[] pattern = {patterns.get(i).getPredicateVar().getName(),getIndividual(p),patterns.get(i).getSubjectVar().getName(),getIndividual(s)};
								resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty())
								{
									String[] pattern2 = {patterns.get(i).getPredicateVar().getName(),"NULL",patterns.get(i).getSubjectVar().getName(),"NULL"};
									resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
								}
							}
						}
					} else if(whichIndex.charAt(1) == 'o') {//subject sop
						for( Object o : Dictionary.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
							for( Object p : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(o) ) {
								String[] pattern = {patterns.get(i).getPredicateVar().getName(),getIndividual(p),patterns.get(i).getObjectVar().getName(),getIndividual(o)};
								resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty())
								{
									String[] pattern2 = {patterns.get(i).getPredicateVar().getName(),"NULL",patterns.get(i).getObjectVar().getName(),"NULL"};
									resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
								}
							}
						}
					}
				}
				if(whichIndex.charAt(2) == 'o') {
					if(whichIndex.charAt(1) == 's') {//property pso
						for( Object s : Dictionary.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
							for( Object o : Dictionary.getInstance().getStore(whichIndex).get(propertyID).get(s) ) {
								String[] pattern = {patterns.get(i).getObjectVar().getName(),getIndividual(o),patterns.get(i).getSubjectVar().getName(),getIndividual(s)};
								resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty())
								{
									String[] pattern2 = {patterns.get(i).getObjectVar().getName(),"NULL",patterns.get(i).getSubjectVar().getName(),"NULL"};
									resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
								}
							}
						}
					} else if(whichIndex.charAt(1) == 'p') {//subject spo
						for( Object p : Dictionary.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
							for( Object o : Dictionary.getInstance().getStore(whichIndex).get(subjectID).get(p) ) {
								String[] pattern = {patterns.get(i).getObjectVar().getName(),getIndividual(o),patterns.get(i).getPredicateVar().getName(),getIndividual(p)};
								resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty())
								{
									String[] pattern2 = {patterns.get(i).getObjectVar().getName(),"NULL",patterns.get(i).getPredicateVar().getName(),"NULL"};
									resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
								}
							}
						}
					}
				}
			} else {
				int cpt = 0;
				for( Object s : Dictionary.getInstance().getStore(whichIndex).keySet().toArray() ) {
					for( Object p : Dictionary.getInstance().getStore(whichIndex).get(s).keySet().toArray() ) {
						for( Object o : Dictionary.getInstance().getStore(whichIndex).get(s).get(p) ) {
							String[] pattern = {patterns.get(i).getSubjectVar().getName(),getIndividual(s),patterns.get(i).getPredicateVar().getName(),getIndividual(p) , patterns.get(i).getObjectVar().getName() , getIndividual(o)};
							resultPattern.add(new ArrayList<String>(Arrays.asList(pattern)));
							if(resultPattern.isEmpty())
							{
								String[] pattern2 = {patterns.get(i).getSubjectVar().getName(),"NULL",patterns.get(i).getPredicateVar().getName(),"NULL" , patterns.get(i).getObjectVar().getName() , "NULL"};
								resultPattern.add(new ArrayList<String>(Arrays.asList(pattern2)));
							}
						}
					}
				}
				System.out.println(cpt);
			}
			//			System.out.println("******************************************\n"+resultPattern+"****************************************************\n");
			
			
			//System.out.println("************"+resultPattern);
			//resultStarQuery.addAll(resultPattern);
			//System.out.println("************"+resultStarQuery);
			if (intersect==null)
				intersect=resultPattern;
			intersect=Join(intersect,resultPattern);
			//System.out.println("\n++++++++++++++++++++++\n"+intersect);
			resultPattern.clear();
			Round++;
		}

		//		if(resultStarQuery.size() > 1) {
		//			for(int i = 0 ; i < resultStarQuery.size() ; i++) {
		//				intersect = resultStarQuery.get(0).stream()
		//						.filter(resultStarQuery.get(i)::contains)
		//						.collect(Collectors.toList());
		//			}
		//		} else if(resultStarQuery.size() == 1){
		//			intersect = new ArrayList<String>(new LinkedHashSet<String>(resultStarQuery.get(0)));
		//		}
		//



		//System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"+intersect);
		//System.out.println("\n-----------------------------------------------------------------------\n"+resultStarQuery);
		
		//System.out.println("\n***********************************************************************\n"+intersect+"\n");

		
		return intersect=(ArrayList<ArrayList<String>>) intersect.stream().distinct().collect(Collectors.toList());

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
