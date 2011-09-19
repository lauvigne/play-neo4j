package play.modules.neo4j;

import static play.modules.neo4j.Neo4jConfig.DEFAULT_CONFIG_NAME;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Play;
import play.PlayPlugin;
import play.exceptions.ConfigurationException;

public class Neo4jPlugin extends PlayPlugin {
    private static Map<String, Neo4jConfig> configs;

    @Override
    public void onApplicationStart() {
        configs = readConfigs(Play.configuration);
        for (Neo4jConfig config : configs.values()) {
            config.initialize();
        }
        Neo4j.setConfigs(configs);
        Neo4jConfig defaultConfig = configs.get(DEFAULT_CONFIG_NAME);
        if(defaultConfig != null)
            Neo4j.setDefaultConfig(defaultConfig);
    }

    @Override
    public void onApplicationStop() {
        for (Neo4jConfig config : configs.values()) {
            config.shutdown();
        }
    }

    /**
     * Looks for Neo4j configurations in properties.
     * 
     * Default instance starts with 'neo4j'. Additional instances start with
     * 'neo4j_' followed by a name.
     * 
     * @return named map of all Neo4j configurations found
     */
    protected static Map<String, Neo4jConfig> readConfigs(Properties props) {
        // preserve order
        LinkedHashMap<String, Neo4jConfig> configs = new LinkedHashMap<String, Neo4jConfig>(
                0);
        Pattern pattern = Pattern
                .compile("^neo4j\\_?([^\\.]*)\\.([^=]+)(?:$|=.*)");
        for (Entry<Object, Object> entry : props.entrySet()) {
            String propName = (String) entry.getKey();
            Matcher m = pattern.matcher(propName);
            if (m.find()) {
                String configName = m.group(1);
                
                if (DEFAULT_CONFIG_NAME.equals(configName))
                    throw new ConfigurationException(
                            "Do not use configuration prefix 'neo4j_neo4j.'. Config name 'neo4j' reserved for default configuration prefixed with 'neo4j.'.");
                
                if(configName.isEmpty())
                    configName = DEFAULT_CONFIG_NAME;
                
                Neo4jConfig config;
                
                if (!configs.containsKey(configName)) {
                    config = new Neo4jConfig(configName);
                    configs.put(configName, config);
                } else {
                    config = configs.get(configName);
                }

                String neo4jPropName = m.group(2);
                String value = (String) entry.getValue();
                if ("storeDir".equals(neo4jPropName)) {
                    config.setStoreDir(value);
                } else {
                    config.params.put(neo4jPropName, value);
                }
            }
        }
        return configs;
    }

    // Quick test;
    // public static void main(String[] args) {
    // Properties props = new Properties();
    // props.put("neo4j.storeDir", "data/foo");
    // props.put("neo4j.x", "y");
    // props.put("neo4j_b.storeDir", "data/foob");
    // props.put("neo4j_b.x", "yb");
    // props.put("neo4j_b.className", "Neo4jFunTimes");
    // Map<String, Neo4jConfig> foo = readConfigs(props);
    // }

}
