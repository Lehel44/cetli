package cetli.neo4j_example;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
    OWN, CONTAIN;
}
