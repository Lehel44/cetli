package cetli.neo4j_example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Result;

import com.google.common.collect.ImmutableMap;

public class RecommendationEngine {

	private Graph graph;
	private Writer writer;

	public RecommendationEngine(Graph graph) {
		this.graph = graph;
		try {
			writer = new FileWriter(Neo4jUtils.resultDataPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String queryRelatedProducts(String customerId) {
		StringBuilder resultBuilder = new StringBuilder();
		// Map<String, Object> parameters = new HashMap<>();
		// parameters.put("customerId", customerId);
		Map<String, Object> parameters = ImmutableMap.<String, Object>of("customerId", customerId);

		String query = "MATCH (customer1:CUSTOMER {customerId: { customerId }})-[:OWN]->(list1:SHOPPING_LIST)-[:CONTAIN]->(products:PRODUCT)\n"
				+ " <-[:CONTAIN]-(list2:SHOPPING_LIST)<-[:OWN]-(customer2:CUSTOMER),\n"
				+ " (customer2:CUSTOMER)-[:OWN]->(list3:SHOPPING_LIST)-[:CONTAIN]->(products2:PRODUCT)\n"
				+ " WHERE NOT (customer1:CUSTOMER)-[:OWN]->(list1:SHOPPING_LIST)-[:CONTAIN]->(products2:PRODUCT)\n"
				+ " RETURN products2.name, COUNT(DISTINCT customer2) AS frequency" + " ORDER BY frequency DESC\n"
				+ " LIMIT 3";
		System.out.println(query);
		final Result result = graph.getGraphDb().execute(query, parameters);
		System.out.println();
		resultBuilder.append("[" + customerId + "] : {");
		int count = 0;
		
		final Iterable<Map<String, Object>> resultIterable = () -> result;
		
		for (Map<String, Object> resultElement : resultIterable) {
			
		}
		
		while (result.hasNext()) {
			// Map<String, Object> resultMap = result.next();
			// if(count++ % 2 == 0) {
			// resultBuilder.append("{" + resultMap.get("frequency") + " : ");
			// } else {
			// resultBuilder.append(resultMap.get("products2.name }"));
			// }
			//
			// if (result.hasNext()) {
			// resultBuilder.append(", ");
			// } else {
			// resultBuilder.append("}\n");
			// }
			resultBuilder.append(count++ + "\n");
			System.out.println(count++ + " " + result.next());

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
