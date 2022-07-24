package sy.neo;

import com.mchange.util.impl.EmptyMEnumeration;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sy.init.neo.NeoPoolUtil;
import sy.neo.type.EMedicalLabel;
import sy.neo.type.EMedicalRel;

import java.util.Map;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class NeoDelete {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeoDelete.class);

    /**
     * delete all nodes and relations
     * @return
     */
    public static boolean deleteAllNodeAndRel() {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("match(n)-[r]-(m) delete n,r,m")).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("delete all nodes and rels done!");
        return (summary.counters().nodesDeleted() == 1) || (summary.counters().relationshipsDeleted() == 1);
    }

    /**
     * delete all nodes whose label is nodeLabel (no relation)
     */
    public static boolean deleteAllNode() {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("match(n) delete n")).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("delete all nodes done!" );
        return summary.counters().nodesDeleted() == 1;
    }

    /**
     * delete all nodes whose label is nodeLabel and all relationships associated with it
     * @param nodeLabel
     * @return
     */
    public static boolean deleteAllRelation(String nodeLabel) {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("match (n:" + nodeLabel + ")-[r]-() delete r,n")).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("delete all relations done!");
        return (summary.counters().nodesDeleted() == 1) || (summary.counters().relationshipsDeleted() == 1);
    }

    /**
     * delete index
     * @param nodeLabel
     * @param property
     */
    public static boolean deleteIndex(String nodeLabel, String property) {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("drop index on:" + nodeLabel + "(" + property + ")")).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("delete index done!");
        return summary.counters().indexesRemoved() == 1;
    }

    /**
     * delete index
     * @param nodeLabelProperty
     */
    public static void deleteIndex(Map<String, String> nodeLabelProperty) {
        for(Map.Entry<String, String> entry : nodeLabelProperty.entrySet()) {
            String nodeLabel = entry.getKey();
            String property = entry.getValue();
            deleteIndex(nodeLabel, property);
        }
    }

    /**
     * delete only index
     * @param nodeLabel
     * @param property
     * @return
     */
    public static boolean deleteOnlyIndex(String nodeLabel, String property) {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("drop constraint on (s:" + nodeLabel + ") assert s." + property + " is unique")).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("delete only index done!");
        return summary.counters().constraintsRemoved() == 1;
    }


    /**
     * delete only index
     * @param nodeLablePropertyMap
     * @return
     */
    public static void deleteOnlyIndex(Map<String, String> nodeLablePropertyMap) {
        for(Map.Entry<String, String> entry : nodeLablePropertyMap.entrySet()) {
            String nodeLabel = entry.getKey();
            String property = entry.getValue();
            deleteOnlyIndex(nodeLabel, property);
        }
    }

}

