package cetli.neo4j_example;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ShoppingList {

	private GraphDatabaseService graphDb;
	private File database;
	private DataReader dataReader;

	public ShoppingList() {
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
				Node node = graphDb.createNode(Labels.SHOPPINGLIST);
				node.setProperty("listId", shoppingList);
			}
			for (String owner : dataReader.getDistinctOwners()) {
				Node node = graphDb.createNode(Labels.OWNER);
				node.setProperty("ownerId", owner);
				System.out.println(owner);
			}

			tx.success();
		}
	}

	public void setRelationShips() {

		try (Transaction tx = graphDb.beginTx()) {
			for (String ownerId : dataReader.getOwnersShoppingLists().keySet()) {
				Node owner = graphDb.findNode(Labels.OWNER, "ownerId", ownerId);
				Map<String, Map<String, String>> shoppingLists = dataReader.getOwnersShoppingLists().get(ownerId);
				for (String shoppingListId : shoppingLists.keySet()) {
					Node shoppingList = graphDb.findNode(Labels.SHOPPINGLIST, "listId", shoppingListId);
					ownsShoppingList(owner, shoppingList);
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

	public static Relationship ownsShoppingList(Node owner, Node shoppingList) {
		Relationship relationship = owner.createRelationshipTo(shoppingList, RelationshipTypes.OWN);
		return relationship;
	}

}
