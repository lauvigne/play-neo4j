package play.modules.neo4j;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.kernel.EmbeddedGraphDatabase;

import play.modules.neo4j.Neo4jConfig;

public class Neo4j {
    private static Map<String, Neo4jConfig> neo4jConfigs = new HashMap<String, Neo4jConfig>(1);
    private static Neo4jConfig defaultConfig = null;
    
    protected static void setConfigs(Map<String, Neo4jConfig> configs) {
        neo4jConfigs = configs;
    }
    
    public static void setDefaultConfig(Neo4jConfig config) {
        defaultConfig = config;
    }
    
    public static EmbeddedGraphDatabase getGraphDb() {
        return defaultConfig.getGraphDb();
    }
    
    public static EmbeddedGraphDatabase getGraphDb(String configName) {
        return getGraphDb(configName, false);
    }
    
    public static EmbeddedGraphDatabase getGraphDb(String configName, boolean ignoreError) {
        Neo4jConfig config = neo4jConfigs.get(configName);
        if (config == null && !ignoreError) {
            throw new RuntimeException("No DBConfig found with the name " + configName);
        } else if (config == null && ignoreError) {
            return null;
        } else {
            return config.getGraphDb();
        }
    }

}
