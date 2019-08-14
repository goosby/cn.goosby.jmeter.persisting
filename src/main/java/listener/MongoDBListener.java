package listener;

import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExtraValues;
import utils.MongoResultUtil;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author
 */
public class MongoDBListener extends AbstractTestElement implements Serializable, SampleListener, TestStateListener {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBListener.class);
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");


    private static MongoClient mongoClient;
    private static DB mongoDB;
    private static DBCollection collection;

    @Override
    public void sampleOccurred(SampleEvent event) {
        BasicDBObject basicObject = MongoResultUtil.processSampler(event);
        if (null != collection) {
            collection.insert(basicObject);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void sampleStarted(SampleEvent event) {
    }

    @Override
    public void sampleStopped(SampleEvent event) {
    }

    @Override
    public void testStarted() {
        logger.info("Samplers persisting to mongoDB started ");
        testStarted("");
    }

    @Override
    public void testStarted(String host) {
        try {
            String mongoHost = getMongoHost();
            int mongoPort = getMongoPort();
            String userName = getUserName();
            String passWord = getPassword();
            String dataBaseName = getDataBaseName();
            if (StringUtils.isNotBlank(mongoHost) && mongoPort > 0) {
                logger.info("MongoDB address is: {} ", mongoHost + ":" + mongoPort);
                List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
                if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(passWord) && StringUtils.isNotBlank(dataBaseName)) {
                    char[] passwords = passWord.toCharArray();
                    MongoCredential credential = MongoCredential.createMongoCRCredential(userName, dataBaseName, passwords);
                    credentialList.add(credential);
                }
                ServerAddress address = new ServerAddress(mongoHost, mongoPort);
                String currentTime = simpleDateFormat.format(new Date());
                if (credentialList.size() > 0) {
                    mongoClient = new MongoClient(address, credentialList, getConfOptions());
                } else {
                    mongoClient = new MongoClient(address, getConfOptions());
                }
                mongoDB = mongoClient.getDB(dataBaseName);
                collection = mongoDB.getCollection(getTestCaseName() + "_" + currentTime);
            } else {
                logger.warn("MongoHost address is null, samplers will not persisting");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testEnded() {
        logger.info("Samplers persisting to mongoDB end");
        testEnded("");
    }

    @Override
    public void testEnded(String host) {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    private static MongoClientOptions getConfOptions() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        // 每个地址最大请求数
        builder.connectionsPerHost(30);
        //一个socket最大的等待请求数
        builder.threadsAllowedToBlockForConnectionMultiplier(50);
        // 长链接的最大等待时间
        builder.maxWaitTime(1000 * 60 * 2);
        // 链接超时时间
        builder.connectTimeout(5000);
        // read数据超时时间
        builder.socketTimeout(5000);
        builder.alwaysUseMBeans(false);
        // 是否重试机制
        builder.autoConnectRetry(false);
        // 最近优先策略
        builder.readPreference(ReadPreference.primary());
        // 是否保持长链接
        builder.socketKeepAlive(true);
        builder.writeConcern(WriteConcern.NORMAL);
        MongoClientOptions mongoClientOptions = builder.build();

        return mongoClientOptions;
    }

    private String getMongoHost() {
        return getPropertyAsString(ExtraValues.MONGO_HOST);
    }

    private int getMongoPort() {
        int port = getPropertyAsInt(ExtraValues.MONGO_PORT);
        if (port == 0) {
            port = 27017;
        }
        return port;
    }

    private String getUserName() {
        return getPropertyAsString(ExtraValues.USERNAME);
    }

    private String getPassword() {
        return getPropertyAsString(ExtraValues.PASSWORD);
    }

    private String getDataBaseName() {
        String dataBase = getPropertyAsString(ExtraValues.DATABASE_NAME);
        if (StringUtils.isBlank(dataBase)) {
            dataBase = ExtraValues.DATABASE_NAME;
        }
        logger.info("Data base name is: {}", dataBase);
        return dataBase;
    }

    private String getTestCaseName() {
        String testCaseName = getPropertyAsString(ExtraValues.TEST_CASE_NAME);
        if (StringUtils.isBlank(testCaseName)) {
            testCaseName = ExtraValues.TEST_CASE_NAME;
        }
        logger.info("Test case name is: {}", testCaseName);
        return testCaseName;
    }
}
