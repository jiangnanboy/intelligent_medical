package sy.neo;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sy.init.neo.NeoPoolUtil;
import sy.neo.type.EMedicalLabel;

import java.util.Optional;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class NeoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoUtil.class);

    /**
     * delete all nodes and rels and index
     */
    public static void deleteAllDataRel() {
        // delete all nodes and relations
        NeoDelete.deleteAllNodeAndRel();
        // delete only index
        EMedicalLabel[] eMedicalLabels = EMedicalLabel.values();
        for(EMedicalLabel label : eMedicalLabels) {
            NeoDelete.deleteOnlyIndex(label.name(), "name");
        }
    }

    /**
     * build node
     * @param file
     * @param nodeLabel
     * @return
     */
    public static void buildNode(String file, String nodeLabel) {
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try(Session session = driver.session()) {
                session.writeTransaction(tx -> tx.run("call apoc.periodic.iterate(" +
                        "'call apoc.load.csv(\"" + file + "\",{header:true,sep:\",\",ignore:[\"label\"]}) yield map as row return row'," +
                        "'create(p:" + nodeLabel +") set p=row'," +
                        "{batchSize:1000,iterateList:true, parallel:true})"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }

        LOGGER.info("create node " + nodeLabel + " done!");
    }

    /**
     * build a unique index
     * when a unique constraint is added to an attribute, an index is automatically added to the attribute.
     * @param nodeLabel
     * @param property
     * @return
     */
    public static boolean buildOnlyIdx(String nodeLabel, String property) {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("create constraint on (s:" + nodeLabel + ") assert s." + property + " is unique")).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("create only index on " + nodeLabel + "." +property + " done!");
        return summary.counters().constraintsAdded() == 1;
    }

    /**
     * build index
     * @param nodeLabel
     * @param property
     * @return
     */
    public static boolean buildIndex(String nodeLabel, String property) {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()){
                summary = session.writeTransaction(tx -> tx.run("create index on :" + nodeLabel + "(" + property + ")" )).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(Optional.ofNullable(driver).isPresent()) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("create index on " + nodeLabel + "." + property + "done!");
        return summary.counters().indexesAdded() == 1;
    }

    /**
     * build relation
     * @param file
     * @param startNodeLabel
     * @param endNodeLabel
     * @param relType
     * @return
     */
    public static boolean buildRel(String file, String startNodeLabel, String endNodeLabel, String relType) {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("call apoc.periodic.iterate(" +
                        "'call apoc.load.csv(\"" + file + "\",{header:true,sep:\",\"}) yield map as row match (start:" + startNodeLabel + "{name:row.start}), (end:" + endNodeLabel + "{name:row.end}) return start,end'," +
                        "'create (start)-[:" + relType + "]->(end)'," +
                        "{batchSize:1000,iterateList:true, parallel:false});")).summary(); //这里注意parallel为false，保证在创建节点之间关系时不会产生死锁问题
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("create relation of " + relType + " done!");
        return summary.counters().relationshipsCreated() == 1;
    }

    /**
     * build relation and it's othername
     * @param file
     * @param startNodeLabel
     * @param endNodeLabel
     * @param relType
     * @param relOtherName
     * @return
     */
    public static boolean buildRel(String file, String startNodeLabel, String endNodeLabel, String relType, String relOtherName) {
        Driver driver = null;
        ResultSummary summary = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()) {
                summary = session.writeTransaction(tx -> tx.run("call apoc.periodic.iterate(" +
                        "'call apoc.load.csv(\"" + file + "\",{header:true,sep:\",\"}) yield map as row match (start:" + startNodeLabel + "{name:row.start}), (end:" + endNodeLabel + "{name:row.end}) return start,end'," +
                        "'create (start)-[rel:" + relType + "{name:" + relOtherName + "}]->(end)'," +
                        "{batchSize:1000, iterateList:true, parallel:false});")).summary();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("create relation of " + relType + " done!");
        return summary.counters().relationshipsCreated() == 1;
    }

}

