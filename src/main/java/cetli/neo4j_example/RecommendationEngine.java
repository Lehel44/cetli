package cetli.neo4j_example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.neo4j.graphdb.Result;

public class RecommendationEngine {

	private Graph graph;
	private Writer writer;
	
	public RecommendationEngine(Graph graph) {
		this.graph = graph;
		try {
			writer = new FileWriter("result.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String queryRelatedProducts(String customerId) {
		StringBuilder resultBuilder = new StringBuilder();
		Result result = graph.getGraphDb().execute(
				"MATCH (customer1:CUSTOMER {customerId: \"" + customerId + "\"})-[:OWN]->(list1:SHOPPING_LIST)-[:CONTAIN]->(products:PRODUCT)"
				+ " <-[:CONTAIN]-(list2:SHOPPING_LIST)<-[:OWN]-(customer2:CUSTOMER),"
				+ " (customer2:CUSTOMER)-[:OWN]->(list3:SHOPPING_LIST)-[:CONTAIN]->(products2:PRODUCT)"
				+ " WHERE NOT (customer1:CUSTOMER)-[:OWN]->(list1:SHOPPING_LIST)-[:CONTAIN]->(products2:PRODUCT)"
				+ " RETURN products2.name, COUNT(DISTINCT customer2) AS frequency"
				+ " ORDER BY frequency DESC"
				+ " LIMIT 3");
		resultBuilder.append("[" + customerId + "] : {");
		int count = 0;
		while(result.hasNext()) {
			Map<String, Object> resultMap = result.next();
			if(count++ % 2 == 0) {
				resultBuilder.append("{" + resultMap.get("frequency") + " : ");
			} else {
				 resultBuilder.append(resultMap.get("products2.name }"));
			}
	
			if (result.hasNext()) {
				resultBuilder.append(", ");
			} else {
				resultBuilder.append("}\n");
			}
			
		}
		return resultBuilder.toString();
	}
	
	public void processQuery() {
		String resultRow;
		for (String customerId : graph.getDataReader().getDistinctCustomers()) {
			resultRow = queryRelatedProducts(customerId);
			try {
				writer.write(resultRow);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
