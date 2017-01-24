package cetli.neo4j_example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class DataReader {

	private Map<String, Map<String, Map<String, String>>> customersShoppingLists;
	private Set<String> distinctProducts;
	private Set<String> distinctshoppingLists;
	private Set<String> distinctCustomers;
	
	public DataReader() {
		customersShoppingLists = new HashMap<>();
		distinctProducts = new HashSet<>();
		distinctshoppingLists = new HashSet<>();
		distinctCustomers = new HashSet<>();
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
				String customerId = lineData[0];
				String listId = lineData[1];
				String objectId = lineData[2];
				String productName = lineData[3];
				
				// A termékek összegyűjése
				distinctProducts.add(productName);
				// A bevásárlólisták összegyűjtése
				distinctshoppingLists.add(listId);
				
				// Ha nincs még a map-ben a tulajdonos.
				if (!customersShoppingLists.containsKey(customerId)) {
					products = new HashMap<>();
					products.put(objectId, productName);
					shoppingLists = new HashMap<>();
					shoppingLists.put(listId, products);
					customersShoppingLists.put(customerId, shoppingLists);
					// Ha a tulajdonos benne van már a map-ben.
				} else {
					shoppingLists = customersShoppingLists.get(customerId);
					// Ha még nincs benne a tulajdonos ezen listája a map-ben.
					if (!shoppingLists.containsKey(listId)) {
						products = new HashMap<>();
						products.put(objectId, productName);
						shoppingLists.put(listId, products);
						customersShoppingLists.put(customerId, shoppingLists);
						// Ha már benne van ezen terméklista a map-ben.
					} else {
						shoppingLists = customersShoppingLists.get(customerId);
						products = shoppingLists.get(listId);
						products.put(objectId, productName);
						shoppingLists.put(listId, products);
						customersShoppingLists.put(customerId, shoppingLists);
					}
				}

			}
			
			for (String customer : customersShoppingLists.keySet()) {
				distinctCustomers.add(customer);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public Set<String> getDistinctCustomers() {
		return distinctCustomers;
	}

	public Map<String, Map<String, Map<String, String>>> getCustomersShoppingLists() {
		return customersShoppingLists;
	}


	public Set<String> getDistinctProducts() {
		return distinctProducts;
	}

	public Set<String> getDistinctshoppingLists() {
		return distinctshoppingLists;
	}

}
