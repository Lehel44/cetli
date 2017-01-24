package cetli.neo4j_example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Graph graph = new Graph();
        graph.createNodes();
        graph.setRelationShips();
        RecommendationEngine recommendationEngine = new RecommendationEngine(graph);
        recommendationEngine.processQuery();
    }
}
