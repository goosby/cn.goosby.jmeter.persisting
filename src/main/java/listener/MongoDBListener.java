package listener;

import com.mongodb.*;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import utils.MongoResultUtil;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 
 *
 */
public class MongoDBListener extends AbstractTestElement implements Serializable, SampleListener,TestStateListener {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggingManager.getLoggerForClass();
	private static final SimpleDateFormat SFORMAT = new SimpleDateFormat("yyyyMMddHHmm");
	//private static final SimpleDateFormat SFORMAT = new SimpleDateFormat("yyyyMMdd");

	private static MongoClient mongoClient;
	private static String mongodb_name;

	
    @Override
	public void sampleOccurred(SampleEvent event) {
		BasicDBObject basicObject = MongoResultUtil.generateSampler(event);
		DB db = mongoClient.getDB(mongodb_name);
		DBCollection collection = db.getCollection(getTestCaseName());
		collection.insert(basicObject);
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
        mongodb_name = getTestCaseName()+ "_" + SFORMAT.format(new Date());
        if(null == mongoClient){
            try {
                ServerAddress address = new ServerAddress(getMongoHost());
                mongoClient = new MongoClient(address,getConfOptions());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void testStarted(String host) {
		
	}

	@Override
	public void testEnded() {
        if(mongoClient != null){
            mongoClient.close();
        }
	}

	@Override
	public void testEnded(String host) {

	}
	
	private static MongoClientOptions getConfOptions() {
		return new MongoClientOptions.Builder().socketKeepAlive(true) // 是否保持长链接
		.connectTimeout(5000) // 链接超时时间
		.socketTimeout(5000) // read数据超时时间
		.readPreference(ReadPreference.primary()) // 最近优先策略
		.autoConnectRetry(false) // 是否重试机制
		.connectionsPerHost(30) // 每个地址最大请求数
		.maxWaitTime(1000 * 60 * 2) // 长链接的最大等待时间
		.threadsAllowedToBlockForConnectionMultiplier(50) //一个socket最大的等待请求数
		.writeConcern(WriteConcern.NORMAL).build();
	}

	private String getMongoHost(){
	    return getPropertyAsString("mongodb_host");
    }
    private String getTestCaseName(){
	    return getPropertyAsString("test_case");
    }

}
