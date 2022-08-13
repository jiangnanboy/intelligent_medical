package sy.neo;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;
import sy.init.neo.NeoPoolUtil;
import sy.neo.type.EMedicalLabel;
import sy.neo.type.EMedicalRel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class NeoSearch {

    /**
     * get disease kg triples
     * @param diseaseId
     * @return
     */
    public static List<Record> getDiseaseKG(String diseaseId) {
        List<Record> recordList = null;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()){
                recordList = session.readTransaction(tx ->
                        tx.run("match p=(n:" + EMedicalLabel.Disease.name() + "{id:$id})-[r]->(m) return p", Values.parameters("id", diseaseId))).list();
                List<Record> recordList1 = session.readTransaction(tx ->
                        tx.run("match p=(m)-[r]->(n:" + EMedicalLabel.Disease.name() + "{id:$id}) return p", Values.parameters("id", diseaseId))).list();
                recordList.addAll(recordList1);
            }
        } finally {
            if(Optional.ofNullable(driver).isPresent()) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        return recordList;
    }

    /**
     * get drug producer kg triples
     * @param drugName
     * @return
     */
    public static List<Record> getDrugProducerKG(String drugName) {
        List<Record> recordList = null;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()){
                recordList = session.readTransaction(tx ->
                        tx.run("match p=(n:" + EMedicalLabel.Drug.name() + "{name:$name})-[r]->(m:" + EMedicalLabel.Producer.name() + ") return p", Values.parameters("name", drugName))).list();
            }
        } finally {
            if(Optional.ofNullable(driver).isPresent()) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        return recordList;
    }

    /**
     * get department kg triples
     * @param departmentName
     * @return
     */
    public static List<Record> getDeptDeptKG(String departmentName) {
        List<Record> recordList = null;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try (Session session = driver.session()){
                recordList = session.readTransaction(tx ->
                        tx.run("match p=(n:" + EMedicalLabel.Department.name() + "{name:$name})-[r]->(m:" + EMedicalLabel.Department.name() + ") return p", Values.parameters("name", departmentName))).list();
            }
        } finally {
            if(Optional.ofNullable(driver).isPresent()) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        return recordList;
    }

    /**
     * get related disease
     * @param id
     * @param query
     * @param size
     * @return
     */
    public static List<Record> getRelatedDisease(String id, String query, int size) {
        String cypherQuery = "match(di:" + EMedicalLabel.Disease.name() + "{id:$id1})-[]->(d:" + EMedicalLabel.Department.name() + ") with d call db.index.fulltext.queryNodes('disease', $query) yield node match (node)-[:" + EMedicalRel.BELONGS_TO.name() + "]->(d) where node.id<>$id2 return node.name as name, node.id as id limit $size";
        Map<String, Object> params = Map.of("id1", id,
                                            "query", query,
                                            "id2", id,
                                            "size", size);
        List<Record> recordList = runGetCypher(cypherQuery, params);
        return recordList;
    }

    /**
     * run cypher and get record
     * @param cypher
     * @return
     */
    public static List<Record> runGetCypher(String cypher) {
        List<Record> recordList = null;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try(Session session = driver.session()) {
                recordList = session.readTransaction(tx ->
                        tx.run(cypher)).list();
            }
        } finally {
            if(Optional.ofNullable(driver).isPresent()) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        return recordList;
    }

    /**
     * run cypher and get record
     * @param cypher
     * @param params
     * @return
     */
    public static List<Record> runGetCypher(String cypher, Map<String, Object> params) {
        List<Record> recordList = null;
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            try(Session session = driver.session()) {
                if(Optional.ofNullable(params).isPresent()) {
                    recordList = session.readTransaction(tx ->
                            tx.run(cypher, params)).list();
                } else {
                    recordList = session.readTransaction(tx ->
                            tx.run(cypher)).list();
                }
            }
        } finally {
            if(Optional.ofNullable(driver).isPresent()) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
        return recordList;
    }



}

