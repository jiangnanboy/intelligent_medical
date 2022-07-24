package sy.neo;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sy.init.neo.NeoPoolUtil;

import java.util.List;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class NeoCount {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeoCount.class);


    /**
     * statistics the number of nodes whose label is nodeLabel
     * @param nodeLabel
     * @return
     */
    public int countNode(String nodeLabel) {
        int nodeSum = 0;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try(Session session = driver.session()) {
                List<Record> recordList = session.readTransaction(tx -> tx.run("match(:" + nodeLabel + ") return count(*)")).list();
                nodeSum = recordList.get(0).get(0).asInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info(nodeLabel + ": total nodes -> " + nodeSum);
        return nodeSum;
    }

    /**
     * statistics all the number of nodes
     * @return
     */
    public int countAllNode() {
        int nodeSum = 0;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()){
                List<Record> recordList = session.readTransaction(tx -> tx.run("match(n) return count(*)")).list();
                nodeSum = recordList.get(0).get(0).asInt();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("total nodes -> " + nodeSum);
        return nodeSum;
    }

    /**
     * statistics the number of relations whose type is reltype
     * @param relType
     * @return
     */
    public int countRel(String relType) {
        int relSum = 0;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try(Session session = driver.session()) {
                List<Record> recordList = session.readTransaction(tx -> tx.run("match()-[:" + relType + "]->() return count(*)")).list();
                relSum = recordList.get(0).get(0).asInt();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info(relType + ": total relations -> " + relSum);
        return relSum;
    }

    /**
     * statistics all the number of relations
     * @return
     */
    public int countAllRel() {
        int relSum = 0;
        Driver driver = null;
        try{
            driver = NeoPoolUtil.borrowDriver();
            try(Session session = driver.session()) {
                List<Record> recordList = session.readTransaction(tx -> tx.run("match()-[]->() return count(*)")).list();
                relSum = recordList.get(0).get(0).asInt();
            }
        } finally {
            if(null != driver) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        LOGGER.info("total relations -> " + relSum);
        return relSum;
    }

}

