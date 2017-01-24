package cetli.neo4j_example;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Graph {

	private GraphDatabaseService graphDb;
	private File database;
	private DataReader dataReader;

	public Graph() {
		database = new File(Neo4jUtils.databasePath);
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(database);
		dataReader = new DataReader();
		dataReader.read();
	}

	public void createNodes() {
		try (Transaction tx = graphDb.beginTx()) {
			// Termékekből csúcsok
			for (String product : dataReader.getDistinctProducts()) {
				Node node = graphDb.createNode(Labels.PRODUCT);
				node.setProperty("name", product);
			}
			for (String shoppingList : dataReader.getDistinctshoppingLists()) {
				Node node = graphDb.createNode(Labels.SHOPPING_LIST);
				node.setProperty("listId", shoppingList);
			}
			for (String customer : dataReader.getDistinctCustomers()) {
				Node node = graphDb.createNode(Labels.CUSTOMER);
				node.setProperty("customerId", customer);
			}

			tx.success();
		}
	}

	public void setRelationShips() {

		try (Transaction tx = graphDb.beginTx()) {
			for (String customerId : dataReader.getCustomersShoppingLists().keySet()) {
				Node customer = graphDb.findNode(Labels.CUSTOMER, "customerId", customerId);
				Map<String, Map<String, String>> shoppingLists = dataReader.getCustomersShoppingLists().get(customerId);
				for (String shoppingListId : shoppingLists.keySet()) {
					Node shoppingList = graphDb.findNode(Labels.SHOPPING_LIST, "listId", shoppingListId);
					ownsShoppingList(customer, shoppingList);
					Map<String, String> products = shoppingLists.get(shoppingListId);
					for (Entry<String, String> productEntry : products.entrySet()) {
						Node product = graphDb.findNode(Labels.PRODUCT, "name", productEntry.getValue());
						String objectId = productEntry.getKey();
						containsProduct(shoppingList, product, objectId);
					}
				}
			}
			tx.success();
		}
	}

	public static Relationship containsProduct(Node shoppingList, Node product, String objectId) {
		Relationship relationship = shoppingList.createRelationshipTo(product, RelationshipTypes.CONTAIN);
		relationship.setProperty("objectId", objectId);
		return relationship;
	}

	public static Relationship ownsShoppingList(Node customer, Node shoppingList) {
		Relationship relationship = customer.createRelationshipTo(shoppingList, RelationshipTypes.OWN);
		return relationship;
	}

	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	public File getDatabase() {
		return database;
	}

	public DataReader getDataReader() {
		return dataReader;
	}
	
}
