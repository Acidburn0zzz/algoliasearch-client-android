package com.algolia.search.saas;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

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

public class Query {
    public enum QueryType
    {
      /// all query words are interpreted as prefixes.
      PREFIX_ALL,
      /// only the last word is interpreted as a prefix (default behavior).
      PREFIX_LAST,
      /// no query word is interpreted as a prefix. This option is not recommended.
      PREFIX_NONE
    }
    
    public enum RemoveWordsType
    {
       // when a query does not return any result, the final word will be removed until there is results. This option is particulary useful on e-commerce websites
       REMOVE_LAST_WORDS,
       // when a query does not return any result, the first word will be removed until there is results. This option is useful on adress search.
       REMOVE_FIRST_WORDS,
       // No specific processing is done when a query does not return any result.
      REMOVE_NONE,
      /// When a query does not return any result, a second trial will be made with all words as optional (which is equivalent to transforming the AND operand between query terms in a OR operand) 
      REMOVE_ALLOPTIONAL
    }
    
    public enum TypoTolerance
    {
      /// the typotolerance is enabled and all typos are retrieved. (Default behavior)
      TYPO_TRUE,
      /// the typotolerance is disabled.
      TYPO_FALSE,
      /// only keep results with the minimum number of typos.
      TYPO_MIN,
      /// the typotolerance with a distance=2 is disabled if the results contain hits without typo.
      TYPO_STRICT
    }

    protected List<String> attributes;
    protected List<String> attributesToHighlight;
    protected List<String> attributesToSnippet;
    protected int minWordSizeForApprox1;
    protected int minWordSizeForApprox2;
    protected boolean getRankingInfo;
    protected boolean ignorePlural;
    protected int distinct;
    protected boolean advancedSyntax;
    protected int page;
    protected int minProximity;
    protected String highlightPreTag;
    protected String highlightPostTag;
    protected int hitsPerPage;
    protected String restrictSearchableAttributes;
    protected String tags;
    protected String numerics;
    protected String insideBoundingBox;
    protected String aroundLatLong;
    protected boolean aroundLatLongViaIP;
    protected String query;
    protected QueryType queryType;
    protected String optionalWords;
    protected String facets;
    protected String facetFilters;
    protected int maxNumberOfFacets;
    protected boolean analytics;
    protected boolean synonyms;
    protected boolean replaceSynonyms;
    protected boolean allowTyposOnNumericTokens;
    protected RemoveWordsType removeWordsIfNoResult;
    protected TypoTolerance typoTolerance;
    protected String analyticsTags;

    public Query(String query) {
	minProximity = 1;
        minWordSizeForApprox1 = 3;
        minWordSizeForApprox2 = 7;
        getRankingInfo = false;
        ignorePlural = false;
        distinct = 0;
        page = 0;
        minProximity = 1;
        hitsPerPage = 20;
        this.query = query;
        queryType = QueryType.PREFIX_LAST;
        maxNumberOfFacets = -1;
        advancedSyntax = false;
        analytics = synonyms = replaceSynonyms = allowTyposOnNumericTokens = true;
        analyticsTags = null;
        typoTolerance = TypoTolerance.TYPO_TRUE;
        removeWordsIfNoResult = RemoveWordsType.REMOVE_NONE;
    }
    
    public Query() {
	minProximity = 1;
        minWordSizeForApprox1 = 3;
        minWordSizeForApprox2 = 7;
        getRankingInfo = false;
        ignorePlural = false;
        distinct = 0;
        page = 0;
        minProximity = 1;
        hitsPerPage = 20;
        queryType = QueryType.PREFIX_ALL;
        maxNumberOfFacets = -1;
        advancedSyntax = false;
        analytics = synonyms = replaceSynonyms = allowTyposOnNumericTokens = true;
        analyticsTags = null;
        typoTolerance = TypoTolerance.TYPO_TRUE;
        removeWordsIfNoResult = RemoveWordsType.REMOVE_NONE;
    }
    
    public Query(Query other) {
        if (other.attributesToHighlight != null) {
        	attributesToHighlight = new ArrayList<String>(other.attributesToHighlight);
        }
        if (other.attributes != null) {
        	attributes = new ArrayList<String>(other.attributes);
        }
        if (other.attributesToSnippet != null) {
        	attributesToSnippet = new ArrayList<String>(other.attributesToSnippet);
        }
	minProximity = other.minProximity;
	highlightPreTag = other.highlightPreTag;
	highlightPostTag = other.highlightPostTag;
        minWordSizeForApprox1 = other.minWordSizeForApprox1;
        minWordSizeForApprox2 = other.minWordSizeForApprox2;
        getRankingInfo = other.getRankingInfo;
        ignorePlural = other.ignorePlural;
        distinct = other.distinct;
        advancedSyntax = other.advancedSyntax;
        page = other.page;
        hitsPerPage = other.hitsPerPage;
        restrictSearchableAttributes = other.restrictSearchableAttributes;
        tags = other.tags;
        numerics = other.numerics;
        insideBoundingBox = other.insideBoundingBox;
        aroundLatLong = other.aroundLatLong;
        aroundLatLongViaIP = other.aroundLatLongViaIP;
        query = other.query;
        queryType = other.queryType;
        optionalWords = other.optionalWords;
        facets = other.facets;
        facetFilters = other.facetFilters;
        maxNumberOfFacets = other.maxNumberOfFacets;
        analytics = other.analytics;
        analyticsTags = other.analyticsTags;
        synonyms = other.synonyms;
        replaceSynonyms = other.replaceSynonyms;
        typoTolerance = other.typoTolerance;
        allowTyposOnNumericTokens = other.allowTyposOnNumericTokens;
        removeWordsIfNoResult = other.removeWordsIfNoResult;
    }

    /**
     * Select the strategy to adopt when a query does not return any result.
     */
    public Query removeWordsIfNoResult(RemoveWordsType type)
    {
        this.removeWordsIfNoResult = type;
        return this;
    }

    /**
     * List of attributes you want to use for textual search (must be a subset of the attributesToIndex 
     * index setting). Attributes are separated with a comma (for example @"name,address").
     * You can also use a JSON string array encoding (for example encodeURIComponent("[\"name\",\"address\"]")).
     * By default, all attributes specified in attributesToIndex settings are used to search.
     */
    public Query restrictSearchableAttributes(String attributes)
    {
        this.restrictSearchableAttributes = attributes;
        return this;
    }
    
    /**
     *  Select how the query words are interpreted:
     */
    public Query setQueryType(QueryType type)
    {
        this.queryType = type;
        return this;
    }
    
    /**
     * Set the full text query
     */
    public Query setQueryString(String query)
    {
        this.query = query;
        return this;
    }
    
    /**
     * Specify the list of attribute names to retrieve. 
     * By default all attributes are retrieved.
     */
    public Query setAttributesToRetrieve(List<String> attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * Specify the list of attribute names to highlight. 
     * By default indexed attributes are highlighted.
     */
    public Query setAttributesToHighlight(List<String> attributes) {
        this.attributesToHighlight = attributes;
        return this;
    }

    /**
     * Specify the list of attribute names to Snippet alongside the number of words to return (syntax is 'attributeName:nbWords').
     * By default no snippet is computed.
     */
    public Query setAttributesToSnippet(List<String> attributes) {
        this.attributesToSnippet = attributes;
        return this;
    }
    
    /**
     * 
     * @param If set to true, enable the distinct feature (disabled by default) if the attributeForDistinct index setting is set. 
     *   This feature is similar to the SQL "distinct" keyword: when enabled in a query with the distinct=1 parameter, 
     *   all hits containing a duplicate value for the attributeForDistinct attribute are removed from results. 
     *   For example, if the chosen attribute is show_name and several hits have the same value for show_name, then only the best 
     *   one is kept and others are removed.
     */
    public Query enableDistinct(boolean distinct) {
        this.distinct = distinct ? 1 : 0;
        return this;
    }

    /**
     * This feature is similar to the distinct just before but instead of keeping the best value per value of attributeForDistinct, it allows to keep N values.
     * @param nbHitsToKeep Specify the maximum number of hits to keep for each distinct value
     */
    public Query enableDistinct(int nbHitsToKeep) {
      this.distinct = nbHitsToKeep;
      return this;
    }

    /**
     * @param If set to false, this query will not be taken into account in analytics feature. Default to true.
     */
    public Query enableAnalytics(boolean enabled) {
        this.analytics = enabled;
        return this;
    }

    /**
     * @param Set the analytics tags identifying the query
     */
    public Query setAnalyticsTags(String analyticsTags) {
        this.analyticsTags = analyticsTags;
        return this;
    }

    /**
     * @param If set to false, this query will not use synonyms defined in configuration. Default to true.
     */
    public Query enableSynonyms(boolean enabled) {
        this.synonyms = enabled;
        return this;
    }

    /**
     * @param If set to false, words matched via synonyms expansion will not be replaced by the matched synonym in highlight result. Default to true.
     */
    public Query enableReplaceSynonymsInHighlight(boolean enabled) {
        this.replaceSynonyms = enabled;
      return this;
    }

    /**

     * @param If set to false, disable typo-tolerance. Default to true.
     */
    public Query enableTypoTolerance(boolean enabled) {
    	if (enabled) {
    		this.typoTolerance = TypoTolerance.TYPO_TRUE;
    	} else {
    		this.typoTolerance = TypoTolerance.TYPO_FALSE;
    	}
        return this;
    }
    
    /**
     * @param This option allow to control the number of typo in the results set.
     */
    public Query setTypoTolerance(TypoTolerance typoTolerance) {
        this.typoTolerance = typoTolerance;
        return this;
    }

    /**
     * Specify the minimum number of characters in a query word to accept one typo in this word. 
     * Defaults to 3.
     */
    public Query setMinWordSizeToAllowOneTypo(int nbChars) {
        minWordSizeForApprox1 = nbChars;
        return this;
    }
    
    /**
     * Specify the minimum number of characters in a query word to accept two typos in this word. 
     * Defaults to 7.
     */
    public Query setMinWordSizeToAllowTwoTypos(int nbChars) {
        minWordSizeForApprox2 = nbChars;
        return this;
    }

    /**
     * @param If set to false, disable typo-tolerance on numeric tokens. Default to true.
     */
    public Query enableTyposOnNumericTokens(boolean enabled) {
        this.allowTyposOnNumericTokens = enabled;
        return this;
    }

    /**
     * if set, the result hits will contain ranking information in _rankingInfo attribute.
     */
    public Query getRankingInfo(boolean enabled) {
        this.getRankingInfo = enabled;
        return this;
    }

    /**
     * If set to true, plural won't be considered as a typo (for example car/cars will be considered as equals). Default to false.
     */
    public Query ignorePlural(boolean enabled) {
        this.ignorePlural = enabled;
        return this;
    }

    /**
     * Set the page to retrieve (zero base). Defaults to 0.
     */
    public Query setPage(int page) {
        this.page = page;
        return this;
    }
    
    /**
     *  Set the number of hits per page. Defaults to 10.
     */
    public Query setHitsPerPage(int nbHitsPerPage) {
        this.hitsPerPage = nbHitsPerPage;
        return this;
    }
    
    /**
     *  Set the number of hits per page. Defaults to 10.
     *  @deprecated Use {@code setHitsPerPage}
     */
    @Deprecated
    public Query setNbHitsPerPage(int nbHitsPerPage) {
        return setHitsPerPage(nbHitsPerPage);
    }


    /*
     * Configure the precision of the proximity ranking criterion. 
     * By default, the minimum (and best) proximity value distance between 2 matching words is 1. Setting it to 2 (or 3) would allow 1 (or 2) words to be found between the matching words without degrading the proximity ranking value.
     *
     * Considering the query "javascript framework", if you set minProximity=2 the records "JavaScript framework" and "JavaScript charting framework" will get the same proximity score, even if the second one contains a word between the 2 matching words. Default to 1.
     */
    public Query setMinProximity(int value) {
	this.minProximity = value;
	return this;
    }

    /*
     * Specify the string that is inserted before/after the highlighted parts in the query result (default to "<em>" / "</em>").
     */
    public Query setHighlightingTags(String preTag, String postTag) {
	this.highlightPreTag = preTag;
	this.highlightPostTag = postTag;
	return this;
    }
    
    /**
     *  Search for entries around a given latitude/longitude. 
     *  @param radius set the maximum distance in meters.
     *  Note: at indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query aroundLatitudeLongitude(float latitude, float longitude, int radius) {
        aroundLatLong = "aroundLatLng=" + latitude + "," + longitude + "&aroundRadius=" + radius;
        return this;
    }
    
    /**
     *  Search for entries around a given latitude/longitude. 
     *  @param radius set the maximum distance in meters.
     *  @param precision set the precision for ranking (for example if you set precision=100, two objects that are distant of less than 100m will be considered as identical for "geo" ranking parameter).
     *  Note: at indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query aroundLatitudeLongitude(float latitude, float longitude, int radius, int precision) {
        aroundLatLong = "aroundLatLng=" + latitude + "," + longitude + "&aroundRadius=" + radius + "&aroundPrecision=" + precision;
        return this;
    }
    
    /**
     * Search for entries around the latitude/longitude of user (using IP geolocation)
     *  @param radius set the maximum distance in meters.
     *  Note: at indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query aroundLatitudeLongitudeViaIP(boolean enabled, int radius) {
        aroundLatLong = "aroundRadius=" + radius;
        aroundLatLongViaIP = enabled;
        return this;
    }

    /**
     * Search for entries around the latitude/longitude of user (using IP geolocation)
     *  @param radius set the maximum distance in meters.
     *  @param precision set the precision for ranking (for example if you set precision=100, two objects that are distant of less than 100m will be considered as identical for "geo" ranking parameter).
     *  Note: at indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query aroundLatitudeLongitudeViaIP(boolean enabled, int radius, int precision) {
        aroundLatLong = "aroundRadius=" + radius + "&aroundPrecision=" + precision;
        aroundLatLongViaIP = enabled;
        return this;
    }
    
    /**
     *  Search for entries inside a given area defined by the two extreme points of a rectangle.
     *    At indexing, geoloc of an object should be set with _geoloc attribute containing lat and lng attributes (for example {"_geoloc":{"lat":48.853409, "lng":2.348800}})
     */
    public Query insideBoundingBox(float latitudeP1, float longitudeP1, float latitudeP2, float longitudeP2) {
        insideBoundingBox = "insideBoundingBox=" + latitudeP1 + "," + longitudeP1 + "," + latitudeP2 + "," + longitudeP2;
        return this;
    }
    
    /**
     * Set the list of words that should be considered as optional when found in the query. 
     * @param words The list of optional words, comma separated.
     */
    public Query setOptionalWords(String words) {
        this.optionalWords = words;
        return this;
    }
    
    /**
     * Set the list of words that should be considered as optional when found in the query. 
     * @param words The list of optional words.
     */
    public Query setOptionalWords(List<String> words) {
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(word);
            builder.append(",");
        }
        this.optionalWords = builder.toString();
        return this;
    }
 
    /**
     * Filter the query by a list of facets. Each filter is encoded as `attributeName:value`.
     */
    public Query setFacetFilters(List<String> facets) {
        JSONArray obj = new JSONArray();
        for (String facet : facets) {
            obj.put(facet);
        }
        this.facetFilters = obj.toString();
        return this;
    }
    
    /**
     * Filter the query by a list of facets. Filters are separated by commas and each facet is encoded as `attributeName:value`.
     * To OR facets, you must add parentheses. For example: `(category:Book,category:Movie),author:John%20Doe`.
     * You can also use a JSON string array encoding, for example `"[[\"category:Book\",\"category:Movie\"],\"author:John Doe\"]"`.
     */
    public Query setFacetFilters(String facetsFilter) {
        this.facetFilters = facetsFilter;
        return this;
    }
   
    /**
     * List of object attributes that you want to use for faceting. <br/>
     * Only attributes that have been added in **attributesForFaceting** index setting can be used in this parameter. 
     * You can also use `*` to perform faceting on all attributes specified in **attributesForFaceting**.
     */
    public Query setFacets(List<String> facets) {
        JSONArray obj = new JSONArray();
        for (String facet : facets) {
            obj.put(facet);
        }
        this.facets = obj.toString();
        return this;
    }
    
    /**
     * Limit the number of facet values returned for each facet.
     */
    public Query setMaxNumberOfFacets(int n) {
        this.maxNumberOfFacets = n;
        return this;
    }

    /**
     * Filter the query by a set of tags. You can AND tags by separating them by commas. To OR tags, you must add parentheses. For example tag1,(tag2,tag3) means tag1 AND (tag2 OR tag3).
     * At indexing, tags should be added in the _tags attribute of objects (for example {"_tags":["tag1","tag2"]} )
     */
    public Query setTagFilters(String tags) {
        this.tags = tags;
        return this;
    }
    
    /**
     * Add a list of numeric filters separated by a comma. 
     * The syntax of one filter is `attributeName` followed by `operand` followed by `value. Supported operands are `<`, `<=`, `=`, `>` and `>=`. 
     * You can have multiple conditions on one attribute like for example `numerics=price>100,price<1000`.
     */
    public Query setNumericFilters(String numerics) {
        this.numerics = numerics;
        return this;
    }
    
    /**
     * Add a list of numeric filters separated by a comma. 
     * The syntax of one filter is `attributeName` followed by `operand` followed by `value. Supported operands are `<`, `<=`, `=`, `>` and `>=`. 
     * You can have multiple conditions on one attribute like for example `numerics=price>100,price<1000`.
     */
    public Query setNumericFilters(List<String> numerics) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String n : numerics) {
            if (!first)
                builder.append(",");
            builder.append(n);
            first = false;
        }
        this.numerics = builder.toString();
        return this;
    }
    
    /**
     * Enable the advanced query syntax. Defaults to false.
     *   - Phrase query: a phrase query defines a particular sequence of terms.
     *     A phrase query is build by Algolia's query parser for words surrounded by ".
     *     For example, "search engine" will retrieve records having search next to engine only.
     *     Typo-tolerance is disabled on phrase queries.
     *   - Prohibit operator: The prohibit operator excludes records that contain the term after the - symbol.
     *     For example search -engine will retrieve records containing search but not engine.
     */
    public Query enableAvancedSyntax(boolean advancedSyntax) {
        this.advancedSyntax = advancedSyntax;
        return this;
    }

    protected String getQueryString() {
        StringBuilder stringBuilder = new StringBuilder();
        
        try {
            if (attributes != null) {
                stringBuilder.append("attributes=");
                boolean first = true;
                for (String attr : this.attributes) {
                    if (!first)
                        stringBuilder.append(",");
                    stringBuilder.append(URLEncoder.encode(attr, "UTF-8"));
                    first = false;
                }
            }
            if (attributesToHighlight != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("attributesToHighlight=");
                boolean first = true;
                for (String attr : this.attributesToHighlight) {
                    if (!first)
                        stringBuilder.append(',');
                    stringBuilder.append(URLEncoder.encode(attr, "UTF-8"));
                    first = false;
                }
            }
            if (attributesToSnippet != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("attributesToSnippet=");
                boolean first = true;
                for (String attr : this.attributesToSnippet) {
                    if (!first)
                        stringBuilder.append(',');
                    stringBuilder.append(URLEncoder.encode(attr, "UTF-8"));
                    first = false;
                }
            }
            if (typoTolerance != TypoTolerance.TYPO_TRUE) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("typoTolerance=");
                switch (typoTolerance) {
                case TYPO_FALSE:
                	stringBuilder.append("false");
                	break;
                case TYPO_MIN:
                	stringBuilder.append("min");
                	break;
                case TYPO_STRICT:
                	stringBuilder.append("strict");
                	break;
                case TYPO_TRUE:
                	stringBuilder.append("true");
                	break;
                }
            }
	    if (minProximity > 1) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("minProximity=");
                stringBuilder.append(minProximity);
	    }
	    if (highlightPreTag != null && highlightPostTag != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("highlightPreTag=");
                stringBuilder.append(highlightPreTag);
                stringBuilder.append("&highlightPostTag=");
                stringBuilder.append(highlightPostTag);
	    }
            if (!allowTyposOnNumericTokens) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("allowTyposOnNumericTokens=false");
            }
            if (minWordSizeForApprox1 != 3) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("minWordSizefor1Typo=");
                stringBuilder.append(minWordSizeForApprox1);
            }
            if (minWordSizeForApprox2 != 7) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("minWordSizefor2Typos=");
                stringBuilder.append(minWordSizeForApprox2);
            }
            if (getRankingInfo) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("getRankingInfo=1");
            }
            if (ignorePlural) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("ignorePlural=true");
            }
            if (!analytics) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("analytics=0");
            }
            if (analyticsTags != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("analyticsTags=" + analyticsTags);
            }
            if (!synonyms) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("synonyms=0");
            }
            if (!replaceSynonyms) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("replaceSynonymsInHighlight=0");
            }
            if (distinct > 0) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("distinct=");
		stringBuilder.append(distinct);
            }
            if (advancedSyntax) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("advancedSyntax=1");
            }
            if (page > 0) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("page=");
                stringBuilder.append(page);
            }
            if (hitsPerPage != 20 && hitsPerPage > 0) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("hitsPerPage=");
                stringBuilder.append(hitsPerPage);
            }
            if (tags != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("tagFilters=");
                stringBuilder.append(URLEncoder.encode(tags, "UTF-8"));
            }
            if (numerics != null) {
              if (stringBuilder.length() > 0)
                stringBuilder.append('&');
              stringBuilder.append("numericFilters=");
              stringBuilder.append(URLEncoder.encode(numerics, "UTF-8"));
            }
            if (insideBoundingBox != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append(insideBoundingBox);
            } else if (aroundLatLong != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append(aroundLatLong);
            }
      if (aroundLatLongViaIP) {
    if (stringBuilder.length() > 0)
        stringBuilder.append('&');
    stringBuilder.append("aroundLatLngViaIP=true");
      }
            if (query != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("query=");
                stringBuilder.append(URLEncoder.encode(query, "UTF-8"));
            }
            if (facets != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("facets=");
                stringBuilder.append(URLEncoder.encode(facets, "UTF-8"));
            }
            if (facetFilters != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("facetFilters=");
                stringBuilder.append(URLEncoder.encode(facetFilters, "UTF-8"));
            }
            if (maxNumberOfFacets > 0) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("maxNumberOfFacets=");
                stringBuilder.append(maxNumberOfFacets);
            }
            if (optionalWords != null) {
              if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("optionalWords=");
                stringBuilder.append(URLEncoder.encode(optionalWords, "UTF-8"));
            }
            if (restrictSearchableAttributes != null) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("restrictSearchableAttributes=");
                stringBuilder.append(URLEncoder.encode(restrictSearchableAttributes, "UTF-8"));
            }
            switch (removeWordsIfNoResult) {
            case REMOVE_LAST_WORDS:
              if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("removeWordsIfNoResult=LastWords");
              break;
            case REMOVE_FIRST_WORDS:
              if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("removeWordsIfNoResult=FirstWords");
              break;
            case REMOVE_ALLOPTIONAL:
              if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("removeWordsIfNoResult=allOptional");
              break;
            case REMOVE_NONE:
              break;
            }
            switch (queryType) {
            case PREFIX_ALL:
              if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("queryType=prefixAll");
              break;
            case PREFIX_LAST:
              break;
            case PREFIX_NONE:
              if (stringBuilder.length() > 0)
                    stringBuilder.append('&');
                stringBuilder.append("queryType=prefixNone");
              break;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

    /**
     * @return the attributes
     */
    public List<String> getAttributes() {
        return attributes;
    }

    /**
     * @return the attributesToHighlight
     */
    public List<String> getAttributesToHighlight() {
        return attributesToHighlight;
    }

    /**
     * @return the attributesToSnippet
     */
    public List<String> getAttributesToSnippet() {
        return attributesToSnippet;
    }

    /**
     * @return the minWordSizeForApprox1
     */
    public int getMinWordSizeForApprox1() {
        return minWordSizeForApprox1;
    }

    /**
     * @return the minWordSizeForApprox2
     */
    public int getMinWordSizeForApprox2() {
        return minWordSizeForApprox2;
    }

    /**
     * @return the getRankingInfo
     */
    public boolean isGetRankingInfo() {
        return getRankingInfo;
    }

    /**
     * @return the ignorePlural
     */
    public boolean isIgnorePlural() {
        return ignorePlural;
    }

    /**
     * @return if the distinct is set
     */
    public boolean isDistinct() {
        return distinct != 0;
    }

    /**
     * @return the distinct
     */
    public int getDistinct() {
        return distinct;
    }

    /**
     * @return the advancedSyntax
     */
    public boolean isAdvancedSyntax() {
        return advancedSyntax;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @return the hitsPerPage
     */
    public int getHitsPerPage() {
        return hitsPerPage;
    }

    /**
     * @return the restrictSearchableAttributes
     */
    public String getRestrictSearchableAttributes() {
        return restrictSearchableAttributes;
    }

    /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @return the numerics
     */
    public String getNumerics() {
        return numerics;
    }

    /**
     * @return the insideBoundingBox
     */
    public String getInsideBoundingBox() {
        return insideBoundingBox;
    }

    /**
     * @return the aroundLatLong
     */
    public String getAroundLatLong() {
        return aroundLatLong;
    }

    /**
     * @return the aroundLatLongViaIP
     */
    public boolean isAroundLatLongViaIP() {
        return aroundLatLongViaIP;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @return the queryType
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * @return the optionalWords
     */
    public String getOptionalWords() {
        return optionalWords;
    }

    /**
     * @return the facets
     */
    public String getFacets() {
        return facets;
    }

    /**
     * @return the facetFilters
     */
    public String getFacetFilters() {
        return facetFilters;
    }

    /**
     * @return the maxNumberOfFacets
     */
    public int getMaxNumberOfFacets() {
        return maxNumberOfFacets;
    }

    /**
     * @return the analytics
     */
    public boolean isAnalytics() {
        return analytics;
    }

    /**
     * @return the analytics tags
     */
    public String getAnalyticsTags() {
        return analyticsTags;
    }

    /**
     * @return the synonyms
     */
    public boolean isSynonyms() {
        return synonyms;
    }

    /**
     * @return the replaceSynonyms
     */
    public boolean isReplaceSynonyms() {
        return replaceSynonyms;
    }

    /**
     * @return the allowTyposOnNumericTokens
     */
    public boolean isAllowTyposOnNumericTokens() {
        return allowTyposOnNumericTokens;
    }

    /**
     * @return the removeWordsIfNoResult
     */
    public RemoveWordsType getRemoveWordsIfNoResult() {
        return removeWordsIfNoResult;
    }

    /**
     * @return the typoTolerance
     */
    public TypoTolerance getTypoTolerance() {
        return typoTolerance;
    }
}
