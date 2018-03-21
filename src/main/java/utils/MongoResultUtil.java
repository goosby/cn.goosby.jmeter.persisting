/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package utils;

import com.mongodb.BasicDBObject;
import listener.MongoDBListener;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a means for saving/reading test results as CSV files.
 */
// For unit tests, @see TestCSVSaveService
public final class MongoResultUtil {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MongoResultUtil.class);
    /*
     * Class to handle generating the delimited string. - adds the delimiter
     * if not the first call - quotes any strings that require it
     */
    static final class StringQuoter {
        private final StringBuilder sb;
        private final char[] specials;
        private boolean addDelim;
        
        

        public StringQuoter(char delim) {
            sb = new StringBuilder(150);
            specials = new char[] { delim, QUOTING_CHAR, CharUtils.CR,CharUtils.LF };
            addDelim = false; // Don't add delimiter first time round
        }

        private void addDelim() {
            if (addDelim) {
                sb.append(specials[0]);
            } else {
                addDelim = true;
            }
        }

        // quotes:
        public void append(String s) {
            addDelim();
            // if (s == null) return;
            sb.append(quoteDelimiters(s, specials));
        }

        public void append(Object obj) {
            append(String.valueOf(obj));
        }

        // These methods handle parameters that cannot contain delimiters or
        // quotes
        public void append(int i) {
            addDelim();
            sb.append(i);
        }

        public void append(long l) {
            addDelim();
            sb.append(l);
        }

        public void append(boolean b) {
            addDelim();
            sb.append(b);
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    /**
     * Convert a result into a string, where the fields of the result are
     * separated by a specified String.
     * 
     * @param event
     *            the sample event to be converted
     * @param delimiter
     *            the separation string
     * @return the separated value representation of the result
     */
    @SuppressWarnings("deprecation")
	public static String resultToDelimitedString(SampleEvent event,final String delimiter) {
        StringQuoter text = new StringQuoter(delimiter.charAt(0));
        SampleResult sample= event.getResult();  
        text.append(sample.getTimeStamp());
        text.append(sample.getTime());
        text.append(sample.getSampleLabel());
        text.append(sample.getResponseCode());
        text.append(sample.getResponseMessage());//没有获取到
        text.append(sample.getThreadName());
        text.append(sample.getDataType());
        text.append(sample.isSuccessful());
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
            text.append(message);
        } else {
            text.append(""); 
        }
        text.append(sample.getBytes());
        text.append(sample.getGroupThreads());
        text.append(sample.getAllThreads());
        text.append(sample.getURL());
        text.append(sample.getResultFileName());
        text.append(sample.getLatency());
        text.append(sample.getDataEncodingWithDefault());
        text.append(sample.getSampleCount());
        text.append(sample.getErrorCount());
        text.append(event.getHostname());
        text.append(event.getResult().getIdleTime());
        text.append(sample.getConnectTime());
        for (int i = 0; i < SampleEvent.getVarCount(); i++) {
            text.append(event.getVarValue(i));
        }
        if(logger.isDebugEnabled()){
            logger.debug(text.toString());
        }
        return text.toString();
    }

    /**
     * <p> Returns a <code>String</code> value for a character-delimited column
     * value enclosed in the quote character, if required. </p>
     * 
     * <p> If the value contains a special character, then the String value is
     * returned enclosed in the quote character. </p>
     * 
     * <p> Any quote characters in the value are doubled up. </p>
     * 
     * <p> If the value does not contain any special characters, then the String
     * value is returned unchanged. </p>
     * 
     * <p> N.B. The list of special characters includes the quote character.
     * </p>
     * 
     * @param input the input column String, may be null (without enclosing
     * delimiters)
     * 
     * @param specialChars special characters; second one must be the quote
     * character
     * 
     * @return the input String, enclosed in quote characters if the value
     * contains a special character, <code>null</code> for null string input
     */
    public static String quoteDelimiters(String input, char[] specialChars) {
        if (StringUtils.containsNone(input, specialChars)) {
            return input;
        }
        StringBuilder buffer = new StringBuilder(input.length() + 10);
        final char quote = specialChars[1];
        buffer.append(quote);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == quote) {
                buffer.append(quote); // double the quote char
            }
            buffer.append(c);
        }
        buffer.append(quote);
        return buffer.toString();
    }

    // State of the parser
    private enum ParserState {INITIAL, PLAIN, QUOTED, EMBEDDEDQUOTE}

    public static final char QUOTING_CHAR = '"';

    /**
     * Reads from file and splits input into strings according to the delimiter,
     * taking note of quoted strings.
     * <p>
     * Handles DOS (CRLF), Unix (LF), and Mac (CR) line-endings equally.
     * <p>
     * A blank line - or a quoted blank line - both return an array containing
     * a single empty String.
     * @param infile
     *            input file - must support mark(1)
     * @param delim
     *            delimiter (e.g. comma)
     * @return array of strings, will be empty if there is no data, i.e. if the input is at EOF.
     * @throws IOException
     *             also for unexpected quote characters
     */
    public static String[] csvReadFile(BufferedReader infile, char delim)
            throws IOException {
        int ch;
        ParserState state = ParserState.INITIAL;
        List<String> list = new ArrayList<>();
        CharArrayWriter baos = new CharArrayWriter(200);
        boolean push = false;
        while (-1 != (ch = infile.read())) {
            push = false;
            switch (state) {
            case INITIAL:
                if (ch == QUOTING_CHAR) {
                    state = ParserState.QUOTED;
                } else if (isDelimOrEOL(delim, ch)) {
                    push = true;
                } else {
                    baos.write(ch);
                    state = ParserState.PLAIN;
                }
                break;
            case PLAIN:
                if (ch == QUOTING_CHAR) {
                    baos.write(ch);
                    throw new IOException(
                            "Cannot have quote-char in plain field:["
                                    + baos.toString() + "]");
                } else if (isDelimOrEOL(delim, ch)) {
                    push = true;
                    state = ParserState.INITIAL;
                } else {
                    baos.write(ch);
                }
                break;
            case QUOTED:
                if (ch == QUOTING_CHAR) {
                    state = ParserState.EMBEDDEDQUOTE;
                } else {
                    baos.write(ch);
                }
                break;
            case EMBEDDEDQUOTE:
                if (ch == QUOTING_CHAR) {
                    baos.write(QUOTING_CHAR); // doubled quote => quote
                    state = ParserState.QUOTED;
                } else if (isDelimOrEOL(delim, ch)) {
                    push = true;
                    state = ParserState.INITIAL;
                } else {
                    baos.write(QUOTING_CHAR);
                    throw new IOException(
                            "Cannot have single quote-char in quoted field:["
                                    + baos.toString() + "]");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected state " + state);
            } // switch(state)
            if (push) {
                if (ch == '\r') {// Remove following \n if present
                    infile.mark(1);
                    if (infile.read() != '\n') {
                        infile.reset(); // did not find \n, put the character
                                        // back
                    }
                }
                String s = baos.toString();
                list.add(s);
                baos.reset();
            }
            if ((ch == '\n' || ch == '\r') && state != ParserState.QUOTED) {
                break;
            }
        } // while not EOF
        if (ch == -1) {// EOF (or end of string) so collect any remaining data
            if (state == ParserState.QUOTED) {
                throw new IOException("Missing trailing quote-char in quoted field:[\""
                        + baos.toString() + "]");
            }
            // Do we have some data, or a trailing empty field?
            if (baos.size() > 0 // we have some data
                    || push // we've started a field
                    || state == ParserState.EMBEDDEDQUOTE // Just seen ""
            ) {
                list.add(baos.toString());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private static boolean isDelimOrEOL(char delim, int ch) {
        return ch == delim || ch == '\n' || ch == '\r';
    }
    
    
	@SuppressWarnings("deprecation")
	public static BasicDBObject processSampler(SampleEvent event){
		//MongoSampler sampler = new MongoSampler();
		SampleResult sample = event.getResult(); 
		BasicDBObject basicObject = new BasicDBObject();
		basicObject.put("dt", sample.getDataType());
		basicObject.put("ats", sample.getAllThreads());
		basicObject.put("b", sample.getBytes());
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
		//basicObject.put("u", sample.getURL());
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
        
		basicObject.put("vv", buffer.toString());
	    /*
	    sampler.setDataType(sample.getDataType());
	    sampler.setAllThreads(sample.getAllThreads());
	    sampler.setBytes(sample.getBytes());
	    sampler.setConnectTime(sample.getConnectTime());
	    sampler.setDataEncodingWithDefault(sample.getDataEncodingNoDefault());
	    sampler.setErrorCount(sample.getErrorCount());
	    sampler.setGroupThreads(sample.getGroupThreads());
	    sampler.setIdleTime(sample.getIdleTime());
	    sampler.setLatency(sample.getLatency());
	    sampler.setResponseCode(sample.getResponseCode());
	    sampler.setResponseMessage(sample.getResponseMessage());
	    sampler.setResultFileName(sample.getResultFileName());
	    sampler.setSampleCount(sample.getSampleCount());
	    sampler.setUrl(sample.getURL());
	    sampler.setHostname(event.getHostname());
	    sampler.setSampleLabel(sample.getSampleLabel());
	    sampler.setSuccessful(sample.isSuccessful());
	    sampler.setThreadName(sample.getThreadName());
	    sampler.setTime(sample.getTime());
	    sampler.setTimestamp(sample.getTimeStamp());
	    
        if (message != null) {
        	sampler.setAssertionResult(message);
        } else {
        	sampler.setAssertionResult(""); 
        }
        sampler.setVarValue(buffer.toString());*/
        
	    return basicObject;
	}
	

}
