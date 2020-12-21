import java.util.ArrayList;
import java.util.HashMap;

public class Index {
	private static Index instance = null;
	public static Dictionary myDictionary;
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> spo, pso, osp, sop, pos, ops;
	
	public long start;
	public long end;
	public long indexTime;
	
	public Index() {
		myDictionary = Dictionary.getInstance();
		spo = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		pso = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		osp = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		sop = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		pos = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		ops = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
	}
	
	public static Index getInstance() {
		if(instance == null)
			instance = new Index(); 
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
	
	public void creatIndex(String subject, String property, String object) {
		start = System.currentTimeMillis();
		addSPO(myDictionary.dictionary.get(subject), myDictionary.dictionary.get(property), myDictionary.dictionary.get(object));
		addPSO(myDictionary.dictionary.get(property), myDictionary.dictionary.get(subject), myDictionary.dictionary.get(object));
		addOSP(myDictionary.dictionary.get(object), myDictionary.dictionary.get(subject), myDictionary.dictionary.get(property));
		addSOP(myDictionary.dictionary.get(subject), myDictionary.dictionary.get(object), myDictionary.dictionary.get(property));
		addPOS(myDictionary.dictionary.get(property), myDictionary.dictionary.get(object), myDictionary.dictionary.get(subject));
		addOPS(myDictionary.dictionary.get(object), myDictionary.dictionary.get(property), myDictionary.dictionary.get(subject));
		end = System.currentTimeMillis();
		indexTime += end - start;
	}

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

}
