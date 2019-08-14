package utils;

import com.mongodb.BasicDBObject;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

/**
 * @author goosby.liu
 *
 * 处理sampler 并返回mongo object
 *
 */
public final class MongoResultUtil {
    /**
     *
     * @param event
     * @return
     */
    public static BasicDBObject processSampler(SampleEvent event) {
        SampleResult sample = event.getResult();
        BasicDBObject basicObject = new BasicDBObject();
        basicObject.put("dt", sample.getDataType());
        basicObject.put("ats", sample.getAllThreads());
        basicObject.put("b", sample.getBytesAsLong());
        basicObject.put("ct", sample.getConnectTime());
        basicObject.put("ed", sample.getDataEncodingNoDefault());
        basicObject.put("ec", sample.getErrorCount());
        basicObject.put("gts", sample.getGroupThreads());
        basicObject.put("it", sample.getIdleTime());
        basicObject.put("ly", sample.getLatency());
        basicObject.put("rc", sample.getResponseCode());
        basicObject.put("rm", sample.getResponseMessage());
        basicObject.put("rf", sample.getResultFileName());
        basicObject.put("sc", sample.getSampleCount());
        basicObject.put("hn", event.getHostname());
        basicObject.put("sl", sample.getSampleLabel());
        basicObject.put("sf", sample.isSuccessful());
        basicObject.put("tn", sample.getThreadName());
        basicObject.put("t", sample.getTime());
        basicObject.put("ts", sample.getTimeStamp());
        String message = null;
        AssertionResult[] results = sample.getAssertionResults();
        if (results != null) {
            for (AssertionResult result : results) {
                message = result.getFailureMessage();
                if (message != null) {
                    break;
                }
            }
        }
        if (message != null) {
            basicObject.put("ar", message);
        } else {
            basicObject.put("ar", "");
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < SampleEvent.getVarCount(); i++) {
            buffer.append(event.getVarValue(i));
        }
        //是否成功
        basicObject.put("s", sample.isSuccessful());
        basicObject.put("vv", buffer.toString());
        return basicObject;
    }
}
