package cetli.neo4j_example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.createNodes();
        shoppingList.setRelationShips();
    }
}
