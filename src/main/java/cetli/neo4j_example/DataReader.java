package cetli.neo4j_example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class DataReader {

	private Map<String, Map<String, Map<String, String>>> ownersShoppingLists;
	private Set<String> distinctProducts;
	private Set<String> distinctshoppingLists;
	private Set<String> distinctOwners;
	
	public DataReader() {
		ownersShoppingLists = new HashMap<>();
		distinctProducts = new HashSet<>();
		distinctshoppingLists = new HashSet<>();
		distinctOwners = new HashSet<>();
	}

	public void read() {
		Scanner scanner;
		try {
			scanner = new Scanner(new File("resources/Train.csv"));
			String nextLine = scanner.nextLine();

			Map<String, String> products;
			Map<String, Map<String, String>> shoppingLists;
			while (scanner.hasNextLine()) {
				nextLine = scanner.nextLine();
				String[] lineData = nextLine.split(",");
				String ownerId = lineData[0];
				String listId = lineData[1];
				String objectId = lineData[2];
				String productName = lineData[3];
				
				// A termékek összegyűjése
				distinctProducts.add(productName);
				// A bevásárlólisták összegyűjtése
				distinctshoppingLists.add(listId);
				
				// Ha nincs még a map-ben a tulajdonos.
				if (!ownersShoppingLists.containsKey(ownerId)) {
					products = new HashMap<>();
					products.put(objectId, productName);
					shoppingLists = new HashMap<>();
					shoppingLists.put(listId, products);
					ownersShoppingLists.put(ownerId, shoppingLists);
					// Ha a tulajdonos benne van már a map-ben.
				} else {
					shoppingLists = ownersShoppingLists.get(ownerId);
					// Ha még nincs benne a tulajdonos ezen listája a map-ben.
					if (!shoppingLists.containsKey(listId)) {
						products = new HashMap<>();
						products.put(objectId, productName);
						shoppingLists.put(listId, products);
						ownersShoppingLists.put(ownerId, shoppingLists);
						// Ha már benne van ezen terméklista a map-ben.
					} else {
						shoppingLists = ownersShoppingLists.get(ownerId);
						products = shoppingLists.get(listId);
						products.put(objectId, productName);
						shoppingLists.put(listId, products);
						ownersShoppingLists.put(ownerId, shoppingLists);
					}
				}

			}
			
			for (String owner : ownersShoppingLists.keySet()) {
				distinctOwners.add(owner);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public Set<String> getDistinctOwners() {
		return distinctOwners;
	}

	public Map<String, Map<String, Map<String, String>>> getOwnersShoppingLists() {
		return ownersShoppingLists;
	}


	public Set<String> getDistinctProducts() {
		return distinctProducts;
	}

	public Set<String> getDistinctshoppingLists() {
		return distinctshoppingLists;
	}

}
