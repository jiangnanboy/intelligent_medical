package sy.init.neo;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import java.util.Optional;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class NeoPool implements PooledObjectFactory<Driver>{

    String host;
    String name;
    String pwd;

    public NeoPool(String host) {
        this.host = host;
    }

    public NeoPool(String host, String name, String pwd) {
        this.host = host;
        this.name = name;
        this.pwd = pwd;
    }
    public NeoPool() {}

    public void setHost(String host) {
        this.host = host;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public PooledObject<Driver> makeObject() throws Exception {
        Driver driver = null;
        if(Optional.ofNullable(this.host).isPresent() && Optional.ofNullable(this.name).isPresent() && Optional.ofNullable(this.pwd).isPresent()) {
            driver = GraphDatabase.driver(host, AuthTokens.basic(name, pwd));
        } else if(Optional.ofNullable(this.host).isPresent()) {
            driver = GraphDatabase.driver(host);
        }
        return new DefaultPooledObject<>(driver);
    }

    @Override
    public void destroyObject(PooledObject<Driver> pooledObject) throws Exception {
        Driver driver = pooledObject.getObject();
        driver.close();
    }

    @Override
    public boolean validateObject(PooledObject<Driver> pooledObject) {
        System.out.println("neo4j validateObject!");
        return false;
    }

    @Override
    public void activateObject(PooledObject<Driver> pooledObject) throws Exception {
        System.out.println("neo4j activateObject");
    }

    @Override
    public void passivateObject(PooledObject<Driver> pooledObject) throws Exception {
        System.out.println("neo4j passivateObject");
    }
}

