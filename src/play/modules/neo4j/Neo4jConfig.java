package play.modules.neo4j;

import java.util.LinkedHashMap;
import java.util.Map;

import org.neo4j.kernel.EmbeddedGraphDatabase;

public class Neo4jConfig {
    public static final String DEFAULT_CONFIG_NAME = "neo4j";
    
    private String storeDir;
    private final String name;
    private volatile EmbeddedGraphDatabase graphDb;

    private static Object lock = new Object[0];
    public final Map<String, String> params = new LinkedHashMap<String, String>(0);

    public Neo4jConfig(String name) {
        this.name = name;
    }

    public void initialize() {
        synchronized (lock) {
            if (graphDb != null) {
                return;
            }

            if (params.size() > 0) {
                graphDb = new EmbeddedGraphDatabase(getStoreDir(), params);
            } else {
                graphDb = new EmbeddedGraphDatabase(getStoreDir());
            }

            // In event of nice kill, try to shut down database cleanly.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdown();
                }
            });

        }
    }

    public void shutdown() {
        synchronized (lock) {
            EmbeddedGraphDatabase neo = graphDb;
            if (neo != null)
                neo.shutdown();
            graphDb = null;
        }
    }

    public EmbeddedGraphDatabase getGraphDb() {
        synchronized (lock) {
            return graphDb;
        }
    }

    public void setStoreDir(String storeDir) {
        this.storeDir = storeDir;
    }

    public String getStoreDir() {
        return storeDir;
    }

    public String getName() {
        return name;
    }
}
