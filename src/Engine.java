
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;

public class Engine {
	public String whichIndex;

	public ArrayList<ArrayList<Integer>> starQuery(List<StatementPattern> patterns, Projection projection) {
		ArrayList<ArrayList<Integer>> intersect = null;
		int Round=0;
		for(int i = 0 ; i < patterns.size() ; i++) {
			ArrayList<ArrayList<Integer>> resultPattern = new ArrayList<ArrayList<Integer>>();
			String whichVar = "";
			String subject = "";
			String property = "";
			String object = "";

			// Check if subject is a variable
			if(patterns.get(i).getSubjectVar().getValue() == null) {
				whichVar += "s";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getSubjectVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getSubjectVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getSubjectVar().getName());
				}
			}
			else
				subject = patterns.get(i).getSubjectVar().getValue().toString();

			//Check if property is a variable
			if(patterns.get(i).getPredicateVar().getValue() == null) {
				whichVar += "p";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getPredicateVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getPredicateVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getPredicateVar().getName());
				}
			}
			else
				property = patterns.get(i).getPredicateVar().getValue().toString();

			//Check if object is a variable
			if(patterns.get(i).getObjectVar().getValue() == null) {
				whichVar += "o";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getObjectVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getObjectVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getObjectVar().getName());
				}
			}
			else
				object = patterns.get(i).getObjectVar().getValue().toString();

			// Check which index to use
			whichIndex = this.getIndexQuery(whichVar);

			// choose the index with the fewest elements
			if(whichIndex.length() != 3)
				whichIndex = rightIndex(whichIndex);

			// Get ID of each element
			Integer subjectID = Dictionary.getInstance().dictionary.get(subject);
			Integer propertyID = Dictionary.getInstance().dictionary.get(property);
			Integer objectID = Dictionary.getInstance().dictionary.get(object);


			if(whichVar.length() == 1) {
				if(whichIndex.charAt(2) == 's') {
					try {
						for( Integer s : Index.getInstance().getStore(whichIndex).get(propertyID).get(objectID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),s};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				} if(whichIndex.charAt(2) == 'p') {
					try {
						for( Integer p : Index.getInstance().getStore(whichIndex).get(subjectID).get(objectID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),p};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				}
				else if(whichIndex.charAt(2) == 'o') {
					try {
						for( Integer o : Index.getInstance().getStore(whichIndex).get(subjectID).get(propertyID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),o};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				}
			}

			resultPattern = (ArrayList<ArrayList<Integer>>) resultPattern.stream().distinct().collect(Collectors.toList());

			for(ArrayList<Integer> triplet : resultPattern) {
				if(triplet.contains(0)) {
					Dictionary.getInstance().dictionaryOfVariables.clear();
					Dictionary.getInstance().idVariables = -1;
					return  new ArrayList<ArrayList<Integer>>();
				}
			}
			
			if(intersect == null && Round == 0) {    
				intersect = new ArrayList<ArrayList<Integer>>(resultPattern);
			} else {
				intersect = merge(intersect, resultPattern);
			}
			resultPattern.clear();
			Round++;
		}

		Dictionary.getInstance().dictionaryOfVariables.clear();
		Dictionary.getInstance().idVariables = -1;

		return intersect;
	}

	public ArrayList<ArrayList<Integer>> queryNonOpt(List<StatementPattern> patterns, Projection projection) {
		ArrayList<ArrayList<Integer>> intersect = null;
		int Round=0;
		for(int i = 0 ; i < patterns.size() ; i++) {
			ArrayList<ArrayList<Integer>> resultPattern = new ArrayList<ArrayList<Integer>>();
			String whichVar = "";
			String subject = "";
			String property = "";
			String object = "";

			// Check if subject is a variable
			if(patterns.get(i).getSubjectVar().getValue() == null) {
				whichVar += "s";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getSubjectVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getSubjectVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getSubjectVar().getName());
				}
			}
			else
				subject = patterns.get(i).getSubjectVar().getValue().toString();

			//Check if property is a variable
			if(patterns.get(i).getPredicateVar().getValue() == null) {
				whichVar += "p";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getPredicateVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getPredicateVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getPredicateVar().getName());
				}
			}
			else
				property = patterns.get(i).getPredicateVar().getValue().toString();

			//Check if object is a variable
			if(patterns.get(i).getObjectVar().getValue() == null) {
				whichVar += "o";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getObjectVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getObjectVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getObjectVar().getName());
				}
			}
			else
				object = patterns.get(i).getObjectVar().getValue().toString();

			// Check which index to use
			whichIndex = this.getIndexQuery(whichVar);

			// choose the index with the fewest elements
			if(whichIndex.length() != 3)
				whichIndex = rightIndex(whichIndex);

			// Get ID of each element
			Integer subjectID = Dictionary.getInstance().dictionary.get(subject);
			Integer propertyID = Dictionary.getInstance().dictionary.get(property);
			Integer objectID = Dictionary.getInstance().dictionary.get(object);

			if(whichVar.length() == 1) {
				if(whichIndex.charAt(2) == 's') {
					try {
						for( Integer s : Index.getInstance().getStore(whichIndex).get(propertyID).get(objectID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),s};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				} if(whichIndex.charAt(2) == 'p') {
					try {
						for( Integer p : Index.getInstance().getStore(whichIndex).get(subjectID).get(objectID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),p};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				}
				else if(whichIndex.charAt(2) == 'o') {
					try {
						for( Integer o : Index.getInstance().getStore(whichIndex).get(subjectID).get(propertyID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),o};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				}
			}
			else if(whichVar.length() == 2) {
				if(whichIndex.charAt(2) == 's') {
					if(whichIndex.charAt(1) == 'p') {//object ops
						try {
							for( Object p : Index.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
								for( Integer s : Index.getInstance().getStore(whichIndex).get(objectID).get(p) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),s,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()), (Integer) p};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						}catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					} else if(whichIndex.charAt(1) == 'o') {//property pos
						try {
							//System.out.println("HEDI HYA");
							for( Object o : Index.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
								for( Integer s : Index.getInstance().getStore(whichIndex).get(propertyID).get(o) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()), s,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()), (Integer) o};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					}
				}
				if(whichIndex.charAt(2) == 'p') {
					if(whichIndex.charAt(1) == 's') {//object osp
						try {
							for( Object s : Index.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
								for( Integer p : Index.getInstance().getStore(whichIndex).get(objectID).get(s) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),p,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()), (Integer) s};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					} else if(whichIndex.charAt(1) == 'o') {//subject sop
						try {
							for( Object o : Index.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
								for( Integer p : Index.getInstance().getStore(whichIndex).get(subjectID).get(o) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),p,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),(Integer) o};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					}
				}
				if(whichIndex.charAt(2) == 'o') {
					if(whichIndex.charAt(1) == 's') {//property pso
						try{
							for( Object s : Index.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
								for( Integer o : Index.getInstance().getStore(whichIndex).get(propertyID).get(s) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),o,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),(Integer) s};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					} else if(whichIndex.charAt(1) == 'p') {//subject spo
						try {
							for( Object p : Index.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
								for( Integer o : Index.getInstance().getStore(whichIndex).get(subjectID).get(p) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),o,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),(Integer) p};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					}
				}
			} else {
				try {
					for( Object s : Index.getInstance().getStore(whichIndex).keySet().toArray() ) {
						for( Object p : Index.getInstance().getStore(whichIndex).get(s).keySet().toArray() ) {
							for( Integer o : Index.getInstance().getStore(whichIndex).get(s).get(p) ) {
								Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()), (Integer) s,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()), (Integer) p , Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()) , o};
								resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty()) {
									Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0 , Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()) , 0};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
								}
							}
						}
					}
				} catch(Exception e) {
					Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0 , Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()) , 0};
					resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
				}
			}
			resultPattern = (ArrayList<ArrayList<Integer>>) resultPattern.stream().distinct().collect(Collectors.toList());

			if(intersect == null && Round == 0) {    
				for(ArrayList<Integer> triplet : resultPattern)
					if(triplet.contains(0)) {
						Dictionary.getInstance().dictionaryOfVariables.clear();
						Dictionary.getInstance().idVariables = -1;
						return  new ArrayList<ArrayList<Integer>>();
					}

				intersect = new ArrayList<ArrayList<Integer>>(resultPattern);

			} else {
				intersect = merge(intersect, resultPattern);
			}
			resultPattern.clear();
			Round++;
		}

		Dictionary.getInstance().dictionaryOfVariables.clear();
		Dictionary.getInstance().idVariables = -1;

		return intersect;
	}

	public ArrayList<ArrayList<Integer>> query(List<StatementPattern> patterns, Projection projection) {
		ArrayList<ArrayList<Integer>> intersect = null;
		int Round=0;
		for(int i = 0 ; i < patterns.size() ; i++) {
			ArrayList<ArrayList<Integer>> resultPattern = new ArrayList<ArrayList<Integer>>();
			String whichVar = "";
			String subject = "";
			String property = "";
			String object = "";

			// Check if subject is a variable
			if(patterns.get(i).getSubjectVar().getValue() == null) {
				whichVar += "s";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getSubjectVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getSubjectVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getSubjectVar().getName());
				}
			}
			else
				subject = patterns.get(i).getSubjectVar().getValue().toString();

			//Check if property is a variable
			if(patterns.get(i).getPredicateVar().getValue() == null) {
				whichVar += "p";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getPredicateVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getPredicateVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getPredicateVar().getName());
				}
			}
			else
				property = patterns.get(i).getPredicateVar().getValue().toString();

			//Check if object is a variable
			if(patterns.get(i).getObjectVar().getValue() == null) {
				whichVar += "o";
				if(!Dictionary.getInstance().dictionaryOfVariables.containsKey(patterns.get(i).getObjectVar().getName())) {
					Dictionary.getInstance().dictionaryOfVariables.put(patterns.get(i).getObjectVar().getName(), Dictionary.getInstance().idVariables);
					Dictionary.getInstance().dictionaryInverseOfVariables.put(Dictionary.getInstance().idVariables--, patterns.get(i).getObjectVar().getName());
				}
			}
			else
				object = patterns.get(i).getObjectVar().getValue().toString();

			// Check which index to use
			whichIndex = this.getIndexQuery(whichVar);

			// choose the index with the fewest elements
			if(whichIndex.length() != 3)
				whichIndex = rightIndex(whichIndex);

			// Get ID of each element
			Integer subjectID = Dictionary.getInstance().dictionary.get(subject);
			Integer propertyID = Dictionary.getInstance().dictionary.get(property);
			Integer objectID = Dictionary.getInstance().dictionary.get(object);

			if(whichVar.length() == 1) {
				if(whichIndex.charAt(2) == 's') {
					try {
						for( Integer s : Index.getInstance().getStore(whichIndex).get(propertyID).get(objectID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),s};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				} if(whichIndex.charAt(2) == 'p') {
					try {
						for( Integer p : Index.getInstance().getStore(whichIndex).get(subjectID).get(objectID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),p};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				}
				else if(whichIndex.charAt(2) == 'o') {
					try {
						for( Integer o : Index.getInstance().getStore(whichIndex).get(subjectID).get(propertyID) )
						{
							Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),o};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
						}
					} catch (Exception e) {
						Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
						resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
					}
				}
			}
			else if(whichVar.length() == 2) {
				if(whichIndex.charAt(2) == 's') {
					if(whichIndex.charAt(1) == 'p') {//object ops
						try {
							for( Object p : Index.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
								for( Integer s : Index.getInstance().getStore(whichIndex).get(objectID).get(p) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),s,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()), (Integer) p};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						}catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					} else if(whichIndex.charAt(1) == 'o') {//property pos
						try {
							//System.out.println("HEDI HYA");
							for( Object o : Index.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
								for( Integer s : Index.getInstance().getStore(whichIndex).get(propertyID).get(o) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()), s,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()), (Integer) o};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					}
				}
				if(whichIndex.charAt(2) == 'p') {
					if(whichIndex.charAt(1) == 's') {//object osp
						try {
							for( Object s : Index.getInstance().getStore(whichIndex).get(objectID).keySet().toArray() ) {
								for( Integer p : Index.getInstance().getStore(whichIndex).get(objectID).get(s) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),p,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()), (Integer) s};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					} else if(whichIndex.charAt(1) == 'o') {//subject sop
						try {
							for( Object o : Index.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
								for( Integer p : Index.getInstance().getStore(whichIndex).get(subjectID).get(o) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),p,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),(Integer) o};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					}
				}
				if(whichIndex.charAt(2) == 'o') {
					if(whichIndex.charAt(1) == 's') {//property pso
						try{
							for( Object s : Index.getInstance().getStore(whichIndex).get(propertyID).keySet().toArray() ) {
								for( Integer o : Index.getInstance().getStore(whichIndex).get(propertyID).get(s) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),o,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),(Integer) s};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					} else if(whichIndex.charAt(1) == 'p') {//subject spo
						try {
							for( Object p : Index.getInstance().getStore(whichIndex).get(subjectID).keySet().toArray() ) {
								for( Integer o : Index.getInstance().getStore(whichIndex).get(subjectID).get(p) ) {
									Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),o,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),(Integer) p};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
									if(resultPattern.isEmpty()) {
										Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
										resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
									}
								}
							}
						} catch(Exception e) {
							Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0};
							resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
						}
					}
				}
			} else {
				try {
					for( Object s : Index.getInstance().getStore(whichIndex).keySet().toArray() ) {
						for( Object p : Index.getInstance().getStore(whichIndex).get(s).keySet().toArray() ) {
							for( Integer o : Index.getInstance().getStore(whichIndex).get(s).get(p) ) {
								Integer[] pattern = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()), (Integer) s,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()), (Integer) p , Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()) , o};
								resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern)));
								if(resultPattern.isEmpty()) {
									Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0 , Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()) , 0};
									resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
								}
							}
						}
					}
				} catch(Exception e) {
					Integer[] pattern2 = {Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getSubjectVar().getName()),0,Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getPredicateVar().getName()),0 , Dictionary.getInstance().dictionaryOfVariables.get(patterns.get(i).getObjectVar().getName()) , 0};
					resultPattern.add(new ArrayList<Integer>(Arrays.asList(pattern2)));
				}
			}
			resultPattern = (ArrayList<ArrayList<Integer>>) resultPattern.stream().distinct().collect(Collectors.toList());

			for(ArrayList<Integer> triplet : resultPattern) {
				if(triplet.contains(0)) {
					Dictionary.getInstance().dictionaryOfVariables.clear();
					Dictionary.getInstance().idVariables = -1;
					return  new ArrayList<ArrayList<Integer>>();
				}

			}
			
			if(intersect == null && Round == 0) {    
				intersect = new ArrayList<ArrayList<Integer>>(resultPattern);
			} else {
				intersect = merge(intersect, resultPattern);
			}
			resultPattern.clear();
			Round++;
		}

		Dictionary.getInstance().dictionaryOfVariables.clear();
		Dictionary.getInstance().idVariables = -1;

		return intersect;
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
			index = "osp|ops";
			break;
		case "so":
			index = "pso|pos";
			break;
		case "po":
			index = "spo|sop";
			break;
		case "spo":
			index = "spo";
			break;
		}
		return index;
	}
	
	public String rightIndex(String index) {
		String[] indexSplitted = index.split("\\|");
		if(Index.getInstance().getStore(indexSplitted[0]).size() > Index.getInstance().getStore(indexSplitted[1]).size()) {
			return indexSplitted[1];
		} else {
			return indexSplitted[0];
		}
	}
	
	public static ArrayList<ArrayList<Integer>> merge(ArrayList<ArrayList<Integer>> intersect,ArrayList<ArrayList<Integer>> resultPattern) {
		ArrayList<ArrayList<Integer>> T0_1 = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> triplet0 : intersect) {
			for (ArrayList<Integer> triplet1 : resultPattern) {
				List<Integer> Fusion_T0_1 = new ArrayList<Integer>();
				List<Integer> new_Fusion_T0_1 = new ArrayList<Integer>();

				if(triplet1.size()==4) {
					if(triplet0.contains(triplet1.get(0)) && !triplet0.contains(triplet1.get(2))) {
						if( triplet0.get(triplet0.indexOf(triplet1.get(0))+1).intValue() == triplet1.get(1).intValue() ) {
							new_Fusion_T0_1.addAll(triplet0);
							new_Fusion_T0_1.add(triplet1.get(2));
							new_Fusion_T0_1.add(triplet1.get(3));
						}
					}
					else if(!triplet0.contains(triplet1.get(0)) && triplet0.contains(triplet1.get(2))) {
						if( triplet0.get(triplet0.indexOf(triplet1.get(2))+1).intValue() == triplet1.get(3).intValue() ) {
							new_Fusion_T0_1.addAll(triplet0);
							new_Fusion_T0_1.add(triplet1.get(0));
							new_Fusion_T0_1.add(triplet1.get(1));
						}
					}
					else if(triplet0.contains(triplet1.get(0)) && triplet0.contains(triplet1.get(2))) {
						if( triplet0.get(triplet0.indexOf(triplet1.get(0))+1).intValue() == triplet1.get(1).intValue() && triplet0.get(triplet0.indexOf(triplet1.get(2))+1).intValue() == triplet1.get(3).intValue() ) {
							Fusion_T0_1 =  Stream.concat(triplet0.stream(), triplet1.stream()).collect(Collectors.toList());
							new_Fusion_T0_1 = Fusion_T0_1.stream().distinct().collect(Collectors.toList());
						}
					}
				}
				else if(triplet1.size()==2) {
					if(triplet0.contains(triplet1.get(0))) {
						if( triplet0.get(triplet0.indexOf(triplet1.get(0))+1).intValue() == triplet1.get(1).intValue()) {
							new_Fusion_T0_1.addAll(triplet0);
						}
					}

				}
				else if(triplet1.size()==6) {
					if( triplet0.contains(triplet1.get(0)) && triplet0.contains(triplet1.get(2)) && triplet0.contains(triplet1.get(4))) {
						if( triplet0.get(triplet0.indexOf(triplet1.get(0))+1).intValue() == triplet1.get(1).intValue()  && triplet0.get(triplet0.indexOf(triplet1.get(2))+1).intValue() == triplet1.get(3).intValue() && triplet0.get(triplet0.indexOf(triplet1.get(4))+1).intValue() == triplet1.get(5).intValue() ) {
							new_Fusion_T0_1.addAll(triplet0);
						}
					}
				}

				if(!new_Fusion_T0_1.isEmpty()) {
					T0_1.add((ArrayList<Integer>) new_Fusion_T0_1);
				}
				else if(Collections.disjoint(triplet0, triplet1) && !triplet0.isEmpty() && !triplet1.isEmpty()) {
					new_Fusion_T0_1 =  Stream.concat(triplet0.stream(), triplet1.stream()).collect(Collectors.toList());
					T0_1.add((ArrayList<Integer>) new_Fusion_T0_1);    
				}
			}
		}
		return T0_1;
	}
}