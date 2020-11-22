import java.util.ArrayList;
import java.util.HashMap;

public class Dictionary {
	public int id;
	private static Dictionary instance = null;
	public HashMap<String, Integer> dictionary;
	public HashMap<Integer, String> dictionaryInverse;
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> spo, pso, osp, sop, pos, ops;
	
	public Dictionary() {
		id = 0;
		dictionary = new HashMap<String, Integer>();
		dictionaryInverse = new HashMap<Integer, String>();
		spo = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		pso = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		osp = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		sop = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		pos = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		ops = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
	}
     
	public static Dictionary getInstance() {           
        if (instance == null)
        	instance = new Dictionary(); 
        return instance;
    }
	
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getSpo() {
		return spo;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getPso() {
		return pso;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getOsp() {
		return osp;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getSop() {
		return sop;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getPos() {
		return pos;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getOps() {
		return ops;
	}

	public HashMap<Integer, String> getDictionaryInverse() {
		return dictionaryInverse;
	}

	public void creatDicitonaryAndIndex(String subject, String property, String object) {
    	addIdDictionary(subject, property, object);
    	addSPO(dictionary.get(subject), dictionary.get(property), dictionary.get(object));
    	addPSO(dictionary.get(property), dictionary.get(subject), dictionary.get(object));
    	addOSP(dictionary.get(object), dictionary.get(subject), dictionary.get(property));
    	addSOP(dictionary.get(subject), dictionary.get(object), dictionary.get(property));
    	addPOS(dictionary.get(property), dictionary.get(object), dictionary.get(subject));
    	addOPS(dictionary.get(object), dictionary.get(property), dictionary.get(subject));
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
    
    //public HashMap<Integer, String>
    
	public void addSPO(int subject, int property, int object) {
		HashMap<Integer, ArrayList<Integer>> po;
		if(spo.get(subject) == null) {
			po = new HashMap<Integer, ArrayList<Integer>>();
		} else {
			po = spo.get(subject); 
		}
		ArrayList<Integer> temp;
		if (po.get(property) == null) {
			temp = new ArrayList<Integer>();
		} else {
			temp = po.get(property);
		}
		temp.add(object);
		po.put(property, temp);
		spo.put(subject, po);
	}
	
	public void addPSO(int subject, int property, int object) {
		HashMap<Integer, ArrayList<Integer>> so;
		if (pso.get(subject) == null) {
			so = new HashMap<Integer, ArrayList<Integer>>();
		} else {
			so = pso.get(subject);
		}
		ArrayList<Integer> temp;
		if (so.get(property) == null) {
			temp = new ArrayList<Integer>();
		} else {
			temp = so.get(property);
		}
		temp.add(object);
		so.put(property, temp);
		
		pso.put(subject, so);
	}
	
	public void addOSP(int subject, int property, int object) {
		HashMap<Integer, ArrayList<Integer>> sp;
		if (osp.get(subject) == null) {
			sp = new HashMap<Integer, ArrayList<Integer>>();
		} else {
			sp = osp.get(subject);
		}
		ArrayList<Integer> temp;
		if (sp.get(property) == null) {
			temp = new ArrayList<Integer>();
		} else {
			temp = sp.get(property);
		}
		temp.add(object);
		sp.put(property, temp);
		
		osp.put(subject, sp);
	}
	
	public void addSOP(int subject, int property, int object) {
		HashMap<Integer, ArrayList<Integer>> op;
		if (sop.get(subject) == null) {
			op = new HashMap<Integer, ArrayList<Integer>>();
		} else {
			op = sop.get(subject); 
		}
		ArrayList<Integer> temp;
		if (op.get(property) == null) {
			temp = new ArrayList<Integer>();
		} else {
			temp = op.get(property); 
		}
		temp.add(object);
		op.put(property, temp);
		
		sop.put(subject, op);
	}
	
	public void addPOS(int subject, int property, int object) {
		HashMap<Integer, ArrayList<Integer>> os;
		if (pos.get(subject) == null) {
			os = new HashMap<Integer, ArrayList<Integer>>();
		} else {
			os = pos.get(subject); 
		}
		ArrayList<Integer> temp;
		if (os.get(property) == null) {
			temp = new ArrayList<Integer>();
		} else {
			temp = os.get(property);
		}
		temp.add(object);
		os.put(property, temp);
		
		pos.put(subject, os);
	}
	
	public void addOPS(int subject, int property, int object) {
		HashMap<Integer, ArrayList<Integer>> ps;
		if (ops.get(subject) == null) {
			ps = new HashMap<Integer, ArrayList<Integer>>();
		} else {
			ps = ops.get(subject);
		}
		ArrayList<Integer> temp;
		if (ps.get(property) == null) {
			temp = new ArrayList<Integer>();
		} else {
			temp = ps.get(property); 
		}
		temp.add(object);
		ps.put(property, temp);
		
		ops.put(subject, ps);
	}
	
	// INVERSE
	// AND GETTER DU MACHIN
    
    public void print(String store) {
    	switch (store) {
		case "SPO" :
			System.out.println(spo);
			break;
		case "PSO" :
			System.out.println(pso);
			break;
		case "OSP" :
			System.out.println(osp);
			break;
		case "SOP" :
			System.out.println(sop);
			break;
		case "POS" :
			System.out.println(pos);
			break;
		case "OPS" :
			System.out.println(ops);
			break;
		default :
			System.out.println("Error !");
    	}
    }
    
    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getStore(String store) {
    	HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> result = null;
    	switch (store) {
			case "spo" :
				result = getSpo();
				break;
			case "pso" :
				result = getPso();
				break;
			case "osp" :
				result = getOsp();
				break;
			case "sop" :
				result = getSop();
				break;
			case "pos" :
				result = getPos();
				break;
			case "ops" :
				result = getOps();
				break;
    	}
    	return result;
    }
}