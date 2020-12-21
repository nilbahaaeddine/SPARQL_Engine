import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ZTest {
	
	public static List<String> selectRandomStringlements(List<String> temp, int amount) {
		List<String> selected = new ArrayList<>();
		Random random = new Random();
		int listSize = temp.size();

		// Avoid a deadlock
		if(amount >= listSize) {
			return temp;
		}

		// Get a random item until we got the requested amount
		while(selected.size() < amount) {
			selected.add(temp.get(random.nextInt(listSize)));
		}

		return selected;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("Queries/watdiv/test.queryset"));

		String line = "";
		List<String> temp = new ArrayList<>();

		while((line = in.readLine()) != null)
			temp.add(line);
		in.close();
		
		System.out.println("Taille de temp avant : " + temp.size());
		System.out.println(temp.get(0));

		Collections.shuffle(temp, new Random());
		
		//temp = selectRandomStringlements(temp, 2360);
		
		System.out.println("Taille de temp après : " + temp.size());
		System.out.println(temp.get(0));
		
		// Ecriture dans le fichier
		try(FileWriter fw = new FileWriter("Queries/watdiv/testMerged.queryset", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			
			for(String element : temp) {
				out.write(element + "\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
