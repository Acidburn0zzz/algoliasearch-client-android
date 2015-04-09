package com.algolia.search.saas;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/** 
 * Entry point in the Java API.
 * You should instantiate a Client object with your ApplicationID, ApiKey and Hosts 
 * to start using Algolia Search API
 */
public class APIClient {
    private int httpSocketTimeoutMS = 30000;
    private int httpConnectTimeoutMS = 2000;
    private int httpSearchTimeoutMS = 5000;
    
    private final static String version = "1.6.3";
    
    private final String applicationID;
    private final String apiKey;
    private final List<String> readHostsArray;
    private final List<String> writeHostsArray;
    private final DefaultHttpClient httpClient;
    private String tagFilters;
    private String userToken;
    private HashMap<String, String> headers;
    
    /**
     * Algolia Search initialization
     * @param applicationID the application ID you have in your admin interface
     * @param apiKey a valid API key for the service
     */
    public APIClient(String applicationID, String apiKey) {
        this(applicationID, apiKey, null);
    }
    
    /**
     * Algolia Search initialization
     * @param applicationID the application ID you have in your admin interface
     * @param apiKey a valid API key for the service
     * @param hostsArray the list of hosts that you have received for the service
     */
    public APIClient(String applicationID, String apiKey, List<String> hostsArray) {
    	this(applicationID, apiKey, hostsArray, false, null);
    }
    
    /**
     * Algolia Search initialization
     * @param applicationID the application ID you have in your admin interface
     * @param apiKey a valid API key for the service
     * @param enableDsn set to true if your account has the Distributed Search Option
     */
    public APIClient(String applicationID, String apiKey, boolean enableDsn) {
    	this(applicationID, apiKey, null, enableDsn, null);
    }
    
    
    /**
     * Algolia Search initialization
     * @param applicationID the application ID you have in your admin interface
     * @param apiKey a valid API key for the service
     * @param hostsArray the list of hosts that you have received for the service
     * @param enableDsn set to true if your account has the Distributed Search Option
     * @param dsnHost override the automatic computation of dsn hostname
     */
    public APIClient(String applicationID, String apiKey, List<String> hostsArray, boolean enableDsn, String dsnHost) {
        if (applicationID == null || applicationID.length() == 0) {
            throw new RuntimeException("AlgoliaSearch requires an applicationID.");
        }
        this.applicationID = applicationID;
        if (apiKey == null || apiKey.length() == 0) {
            throw new RuntimeException("AlgoliaSearch requires an apiKey.");
        }
        this.apiKey = apiKey;
        if (hostsArray == null || hostsArray.size() == 0) {
        	readHostsArray = Arrays.asList(applicationID + "-dsn.algolia.net", 
            		                       applicationID + "-1.algolianet.com", 
            		                       applicationID + "-2.algolianet.com",
            		                       applicationID + "-3.algolianet.com");
        	writeHostsArray = Arrays.asList(applicationID + ".algolia.net", 
						                   applicationID + "-1.algolianet.com", 
						                   applicationID + "-2.algolianet.com",
						                   applicationID + "-3.algolianet.com");
        } else {
        	readHostsArray = writeHostsArray = hostsArray;
        }
        httpClient = new DefaultHttpClient();
        headers = new HashMap<String, String>();
    }
    
    /**
     * Allow to set custom headers
     */
    public void setExtraHeader(String key, String value) {
    	headers.put(key, value);
    }
   
    /**
     * Allow to set timeout
     * @param connectTimeout connection timeout in MS
     * @param readTimeout socket timeout in MS
     */
    public void setTimeout(int connectTimeout, int readTimeout) {
    	httpSocketTimeoutMS = readTimeout;
    	httpConnectTimeoutMS = httpSearchTimeoutMS = connectTimeout;
    }
    
    /**
     * Allow to set timeout
     * @param connectTimeout connection timeout in MS
     * @param readTimeout socket timeout in MS
     * @param search socket timeout in MS
     */
    public void setTimeout(int connectTimeout, int readTimeout, int searchTimeout) {
    	httpSocketTimeoutMS = readTimeout;
    	httpConnectTimeoutMS = connectTimeout;
    	httpSearchTimeoutMS = searchTimeout;
    }
    
    /**
     * List all existing indexes
     * return an JSON Object in the form:
     * { "items": [ {"name": "contacts", "createdAt": "2013-01-18T15:33:13.556Z"},
     *              {"name": "notes", "createdAt": "2013-01-18T15:33:13.556Z"}]}
     */
    public JSONObject listIndexes() throws AlgoliaException {
        return getRequest("/1/indexes/", false);
    }

    /**
     * Delete an index
     *
     * @param indexName the name of index to delete
     * return an object containing a "deletedAt" attribute
     */
    public JSONObject deleteIndex(String indexName) throws AlgoliaException {
        try {
            return deleteRequest("/1/indexes/" + URLEncoder.encode(indexName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Move an existing index.
     * @param srcIndexName the name of index to copy.
     * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
     */
    public JSONObject moveIndex(String srcIndexName, String dstIndexName) throws AlgoliaException {
    	try {
	        JSONObject content = new JSONObject();
	        content.put("operation", "move");
	        content.put("destination", dstIndexName);
	        return postRequest("/1/indexes/" + URLEncoder.encode(srcIndexName, "UTF-8") + "/operation", content.toString(), false); 	
    	} catch (UnsupportedEncodingException e) {
    		throw new RuntimeException(e);
    	} catch (JSONException e) {
            throw new AlgoliaException(e.getMessage());
        }
    }
    
    /**
     * Copy an existing index.
     * @param srcIndexName the name of index to copy.
     * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
     */
    public JSONObject copyIndex(String srcIndexName, String dstIndexName) throws AlgoliaException {
    	try {
	        JSONObject content = new JSONObject();
	        content.put("operation", "copy");
	        content.put("destination", dstIndexName);
	        return postRequest("/1/indexes/" + URLEncoder.encode(srcIndexName, "UTF-8") + "/operation", content.toString(), false); 	
    	} catch (UnsupportedEncodingException e) {
    		throw new RuntimeException(e);
    	} catch (JSONException e) {
            throw new AlgoliaException(e.getMessage());
        }
    }
    
    /**
     * Return 10 last log entries.
     */
    public JSONObject getLogs() throws AlgoliaException {
    	 return getRequest("/1/logs", false);
    }

    /**
     * Return last logs entries.
     * @param offset Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
     * @param length Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
     */
    public JSONObject getLogs(int offset, int length) throws AlgoliaException {
    	 return getRequest("/1/logs?offset=" + offset + "&length=" + length, false);
    }
    
    /**
     * Return last logs entries.
     * @param offset Specify the first entry to retrieve (0-based, 0 is the most recent log entry).
     * @param length Specify the maximum number of entries to retrieve starting at offset. Maximum allowed value: 1000.
     */
    public JSONObject getLogs(int offset, int length, boolean onlyErrors) throws AlgoliaException {
    	 return getRequest("/1/logs?offset=" + offset + "&length=" + length + "&onlyErrors=" + onlyErrors, false);
    }
    
    /**
     * Get the index object initialized (no server call needed for initialization)
     *
     * @param indexName the name of index
     */
    public Index initIndex(String indexName) {
        return new Index(this, indexName);
    }

    /**
     * List all existing user keys with their associated ACLs
     */
    public JSONObject listUserKeys() throws AlgoliaException {
        return getRequest("/1/keys", false);
    }

    /**
     * Get ACL of a user key
     */
    public JSONObject getUserKeyACL(String key) throws AlgoliaException {
        return getRequest("/1/keys/" + key, false);
    }

    /**
     * Delete an existing user key
     */
    public JSONObject deleteUserKey(String key) throws AlgoliaException {
        return deleteRequest("/1/keys/" + key);
    }

    /**
     * Create a new user key
     *
     * @param acls the list of ACL for this key. Defined by an array of strings that 
     * can contains the following values:
     *   - search: allow to search (https and http)
     *   - addObject: allows to add/update an object in the index (https only)
     *   - deleteObject : allows to delete an existing object (https only)
     *   - deleteIndex : allows to delete index content (https only)
     *   - settings : allows to get index settings (https only)
     *   - editSettings : allows to change index settings (https only)
     */
    public JSONObject addUserKey(List<String> acls) throws AlgoliaException {
        JSONArray array = new JSONArray(acls);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("acl", array);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return postRequest("/1/keys", jsonObject.toString(), false);
    }

    /**
     * Update a user key
     *
     * @param acls the list of ACL for this key. Defined by an array of strings that 
     * can contains the following values:
     *   - search: allow to search (https and http)
     *   - addObject: allows to add/update an object in the index (https only)
     *   - deleteObject : allows to delete an existing object (https only)
     *   - deleteIndex : allows to delete index content (https only)
     *   - settings : allows to get index settings (https only)
     *   - editSettings : allows to change index settings (https only)
     */
    public JSONObject updateUserKey(String key, List<String> acls) throws AlgoliaException {
        JSONArray array = new JSONArray(acls);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("acl", array);
        } catch (JSONException e) {
            throw new RuntimeException(e); // $COVERAGE-IGNORE$
        }
        return putRequest("/1/keys/" + key, jsonObject.toString());
    }
    
    /**
     * Create a new user key
     *
     * @param acls the list of ACL for this key. Defined by an array of strings that 
     * can contains the following values:
     *   - search: allow to search (https and http)
     *   - addObject: allows to add/update an object in the index (https only)
     *   - deleteObject : allows to delete an existing object (https only)
     *   - deleteIndex : allows to delete index content (https only)
     *   - settings : allows to get index settings (https only)
     *   - editSettings : allows to change index settings (https only)
     * @param validity the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
     * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
     * @param maxHitsPerQuery Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited) 
     */
    public JSONObject addUserKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
        return addUserKey(acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, null);
    }

    /**
     * Update a user key
     *
     * @param acls the list of ACL for this key. Defined by an array of strings that 
     * can contains the following values:
     *   - search: allow to search (https and http)
     *   - addObject: allows to add/update an object in the index (https only)
     *   - deleteObject : allows to delete an existing object (https only)
     *   - deleteIndex : allows to delete index content (https only)
     *   - settings : allows to get index settings (https only)
     *   - editSettings : allows to change index settings (https only)
     * @param validity the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
     * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
     * @param maxHitsPerQuery Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited) 
     */
    public JSONObject updateUserKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery) throws AlgoliaException {
        return updateUserKey(key, acls, validity, maxQueriesPerIPPerHour, maxHitsPerQuery, null);
    }

    /**
     * Create a new user key
     *
     * @param acls the list of ACL for this key. Defined by an array of strings that 
     * can contains the following values:
     *   - search: allow to search (https and http)
     *   - addObject: allows to add/update an object in the index (https only)
     *   - deleteObject : allows to delete an existing object (https only)
     *   - deleteIndex : allows to delete index content (https only)
     *   - settings : allows to get index settings (https only)
     *   - editSettings : allows to change index settings (https only)
     * @param validity the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
     * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
     * @param maxHitsPerQuery Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
     * @param indexes the list of targeted indexes 
     */
    public JSONObject addUserKey(List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes) throws AlgoliaException {
        JSONArray array = new JSONArray(acls);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("acl", array);
            jsonObject.put("validity", validity);
            jsonObject.put("maxQueriesPerIPPerHour", maxQueriesPerIPPerHour);
            jsonObject.put("maxHitsPerQuery", maxHitsPerQuery);
            if (indexes != null) {
                jsonObject.put("indexes", new JSONArray(indexes));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return postRequest("/1/keys", jsonObject.toString(), false);
    }

    /**
     * Update a user key
     *
     * @param acls the list of ACL for this key. Defined by an array of strings that 
     * can contains the following values:
     *   - search: allow to search (https and http)
     *   - addObject: allows to add/update an object in the index (https only)
     *   - deleteObject : allows to delete an existing object (https only)
     *   - deleteIndex : allows to delete index content (https only)
     *   - settings : allows to get index settings (https only)
     *   - editSettings : allows to change index settings (https only)
     * @param validity the number of seconds after which the key will be automatically removed (0 means no time limit for this key)
     * @param maxQueriesPerIPPerHour Specify the maximum number of API calls allowed from an IP address per hour.  Defaults to 0 (no rate limit).
     * @param maxHitsPerQuery Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited)
     * @param indexes the list of targeted indexes 
     */
    public JSONObject updateUserKey(String key, List<String> acls, int validity, int maxQueriesPerIPPerHour, int maxHitsPerQuery, List<String> indexes) throws AlgoliaException {
        JSONArray array = new JSONArray(acls);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("acl", array);
            jsonObject.put("validity", validity);
            jsonObject.put("maxQueriesPerIPPerHour", maxQueriesPerIPPerHour);
            jsonObject.put("maxHitsPerQuery", maxHitsPerQuery);
            if (indexes != null) {
                jsonObject.put("indexes", new JSONArray(indexes));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e); // $COVERAGE-IGNORE$
        }
        return putRequest("/1/keys/" + key, jsonObject.toString());
    }

    /**
     * Generate a secured and public API Key from a list of tagFilters and an
     * optional user token identifying the current user
     *
     * @param privateApiKey your private API Key
     * @param tagFilters the list of tags applied to the query (used as security)
     */
    public String generateSecuredApiKey(String privateApiKey, String tagFilters) {
        return generateSecuredApiKey(privateApiKey, tagFilters, null);
    }
    
    /**
     * Generate a secured and public API Key from a list of tagFilters and an
     * optional user token identifying the current user
     *
     * @param privateApiKey your private API Key
     * @param tagFilters the list of tags applied to the query (used as security)
     * @param userToken an optional token identifying the current user
     */
    public String generateSecuredApiKey(String privateApiKey, String tagFilters, String userToken) {
        return hmac(privateApiKey, tagFilters + (userToken != null ? userToken : ""));
    }
    
    public void setSecurityTags(String tagFilters) {
        this.tagFilters = tagFilters;
    }
    
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
    
    static String hmac(String key, String msg) {
    	Mac hmac;
		try {
			hmac = Mac.getInstance("HmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			throw new Error(e);
		}
    	try {
			hmac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
		} catch (InvalidKeyException e) {
			throw new Error(e);
		}
    	byte[] rawHmac = hmac.doFinal(msg.getBytes());
        byte[] hexBytes = new Hex().encode(rawHmac);
        return new String(hexBytes);
    }
    
    private static enum Method {
        GET, POST, PUT, DELETE, OPTIONS, TRACE, HEAD;
    }
    
    protected JSONObject getRequest(String url, boolean search) throws AlgoliaException {
    	return _request(Method.GET, url, null, readHostsArray, httpConnectTimeoutMS, search ? httpSearchTimeoutMS : httpSocketTimeoutMS);
    }
    
    protected JSONObject deleteRequest(String url) throws AlgoliaException {
    	return _request(Method.DELETE, url, null, writeHostsArray, httpConnectTimeoutMS, httpSocketTimeoutMS);
    }
    
    protected JSONObject postRequest(String url, String obj, boolean readOperation) throws AlgoliaException {
    	return _request(Method.POST, url, obj, (readOperation ? readHostsArray : writeHostsArray), httpConnectTimeoutMS, (readOperation ? httpSearchTimeoutMS : httpSocketTimeoutMS));
    }
    
    protected JSONObject putRequest(String url, String obj) throws AlgoliaException {
    	return _request(Method.PUT, url, obj, writeHostsArray, httpConnectTimeoutMS, httpSocketTimeoutMS);
    }
    
    private JSONObject _getAnswerObject(InputStream istream) throws IOException, JSONException {
    	InputStreamReader is = new InputStreamReader(istream, "UTF-8");
    	StringBuilder builder= new StringBuilder();
        char[] buf = new char[1000];
        int l = 0;
        while (l >= 0) {
            builder.append(buf, 0, l);
            l = is.read(buf);
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONObject res = new JSONObject(tokener);
        is.close();
        return res;
    }
    
    private JSONObject _request(Method m, String url, String json, List<String> hostsArray, int connectTimeout, int readTimeout) throws AlgoliaException {
    	HttpRequestBase req;
    	HashMap<String, String> errors = new HashMap<String, String>();
    	// for each host
    	for (String host : hostsArray) {
        	switch (m) {
    		case DELETE:
    			req = new HttpDelete();
    			break;
    		case GET:
    			req = new HttpGet();
    			break;
    		case POST:
    			req = new HttpPost();
    			break;
    		case PUT:
    			req = new HttpPut();
    			break;
    		default:
    			throw new IllegalArgumentException("Method " + m + " is not supported");
        	}

            // set URL
            try {
    			req.setURI(new URI("https://" + host + url));
    		} catch (URISyntaxException e) {
    			// never reached
    			throw new IllegalStateException(e);
    		}
        	
        	// set auth headers
        	req.setHeader("X-Algolia-Application-Id", this.applicationID);
            req.setHeader("X-Algolia-API-Key", this.apiKey);
            for (Entry<String, String> entry : headers.entrySet()) {
            	req.setHeader(entry.getKey(), entry.getValue());
            }
            
            // set user agent
            req.setHeader("User-Agent", "Algolia for Android " + version);
            
            // set optional headers
            if (this.userToken != null) {
                req.setHeader("X-Algolia-UserToken", this.userToken);
            }
            if (this.tagFilters != null) {
                req.setHeader("X-Algolia-TagFilters", this.tagFilters);
            }
            
            // set JSON entity
            if (json != null) {
            	if (!(req instanceof HttpEntityEnclosingRequestBase)) {
            		throw new IllegalArgumentException("Method " + m + " cannot enclose entity");
            	}
	            req.setHeader("Content-type", "application/json");
	            try {
	                StringEntity se = new StringEntity(json, "UTF-8"); 
	                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	                ((HttpEntityEnclosingRequestBase) req).setEntity(se); 
	            } catch (UnsupportedEncodingException e) {
	                throw new AlgoliaException("Invalid JSON Object: " + json);
	            }
            }
            
            httpClient.getParams().setParameter("http.socket.timeout", readTimeout);
            httpClient.getParams().setParameter("http.connection.timeout", connectTimeout);

            HttpResponse response;
            try {
            	response = httpClient.execute(req);
            } catch (IOException e) {
            	// on error continue on the next host
            	errors.put(host, String.format("%s=%s", e.getClass().getName(), e.getMessage()));
            	continue;
            }
            int code = response.getStatusLine().getStatusCode();
            if ((int)code / 100 == 2) {
                // OK
            } else if ((int)code / 100 == 4) {
            	String message = "Error detected in backend";
                try {
                    message = _getAnswerObject(response.getEntity().getContent()).getString("message");
                } catch (IOException e) {
                	continue;
                } catch (JSONException e) {
                    throw new AlgoliaException("JSON decode error:" + e.getMessage());
                }
                consumeQuietly(response.getEntity());
                throw new AlgoliaException(message);
            } else {
            	try {
					errors.put(host, EntityUtils.toString(response.getEntity()));
				} catch (IOException e) {
					errors.put(host, String.valueOf(code));
				}
                consumeQuietly(response.getEntity());
                // KO, continue
                continue;
            }
            try {
                return _getAnswerObject(response.getEntity().getContent());
            } catch (IOException e) {
            	continue;
            } catch (JSONException e) {
                throw new AlgoliaException("JSON decode error:" + e.getMessage());
            }
        }
    	StringBuilder builder = new StringBuilder("Hosts unreachable: ");
    	Boolean first = true;
    	for (Map.Entry<String, String> entry : errors.entrySet()) {
    		if (!first) {
    			builder.append(", ");
    		}
    		builder.append(entry.toString());
    		first = false;
    	}
        throw new AlgoliaException(builder.toString());
    }
    
    static public class IndexQuery {
    	private String index;
    	private Query query;
    	public IndexQuery(String index, Query q)  {
    		this.index = index;
    		this.query = q;
    	}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}
		public Query getQuery() {
			return query;
		}
		public void setQuery(Query query) {
			this.query = query;
		}
    }
    /**
     * This method allows to query multiple indexes with one API call
     */
    public JSONObject multipleQueries(List<IndexQuery> queries) throws AlgoliaException {
    		try {
    			JSONArray requests = new JSONArray();
    			for (IndexQuery indexQuery : queries) {
    				String paramsString = indexQuery.getQuery().getQueryString();
				requests.put(new JSONObject().put("indexName", indexQuery.getIndex()).put("params", paramsString));
    			}
				JSONObject body = new JSONObject().put("requests", requests);
				return postRequest("/1/indexes/*/queries", body.toString(), true);
			} catch (JSONException e) {
				new AlgoliaException(e.getMessage());
			}
    		return null;
    }
    
    /**
     * Ensures that the entity content is fully consumed and the content stream, if exists,
     * is closed.
     *
     * @param entity
     */
    private void consumeQuietly(final HttpEntity entity) {
        if (entity == null) {
            return;
        }
        try {
	        if (entity.isStreaming()) {
	            InputStream instream = entity.getContent();
	            if (instream != null) {
	                instream.close();
	            }
	        }
        } catch (IOException e) {
        	// not fatal
        }
    }
}
