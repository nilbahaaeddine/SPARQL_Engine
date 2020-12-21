import java.text.NumberFormat;
import java.util.HashMap;

public class Dictionary {
	public int id;
	private static Dictionary instance = null;
	public HashMap<String, Integer> dictionary;
	public HashMap<Integer, String> dictionaryInverse;
	public NumberFormat formatter;

	public long start;
	public long end;
	public long dictionaryTime;

	public HashMap<String, Integer> dictionaryOfVariables;
	public HashMap<Integer, String> dictionaryInverseOfVariables;
	public int idVariables;

	public Dictionary() {
		id = 1;
		idVariables = -1;
		dictionary = new HashMap<String, Integer>();
		dictionaryInverse = new HashMap<Integer, String>();
		dictionaryOfVariables = new HashMap<String, Integer>();
		dictionaryInverseOfVariables = new HashMap<Integer, String>();
	}

	public static Dictionary getInstance() {
		if(instance == null)
			instance = new Dictionary(); 
		return instance;
	}

	public HashMap<Integer, String> getDictionaryInverse() {
		return dictionaryInverse;
	}

	public void creatDicitonary(String subject, String property, String object) {
		start = System.currentTimeMillis();
		addIdDictionary(subject, property, object);
		end = System.currentTimeMillis();
		dictionaryTime += end - start;
	}

	public void addIdDictionary(String subject, String property, String object) {
		if(!dictionary.containsKey(subject)) {
			dictionary.put(subject, id);
			dictionaryInverse.put(id, subject);
			id++;
		}

		if(!dictionary.containsKey(property)) {
			dictionary.put(property, id);
			dictionaryInverse.put(id, property);
			id++;
		}

		if(!dictionary.containsKey(object)) {
			dictionary.put(object, id);
			dictionaryInverse.put(id, object);
			id++;
		}
	}
}