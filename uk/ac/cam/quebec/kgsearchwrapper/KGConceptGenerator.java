package uk.ac.cam.quebec.kgsearchwrapper;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.ExponentialBackOff;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Class used for generating KGConcepts for a given query string.
 *
 * Note that all the fields are thread-safe and hence the class should be thread safe
 *
 * @author tudor
 */
public class KGConceptGenerator {

    public final static int LIMIT = 10;
    public final static int RETRY_LIMIT = 5;

    private HttpTransport httpTransport;
    private HttpRequestFactory httpRequestFactory;

    public KGConceptGenerator() {
        httpTransport = new NetHttpTransport();
        httpRequestFactory = httpTransport.createRequestFactory();
    }

    /**
     * Return a list of concepts relevant to the given query string. Returns at most LIMIT number of
     * concepts.
     * @param query The string to use for the query
     * @param limit The maximum number of concepts to be returned
     * @return list of relevant KGConcept (s)
     */
    @Nonnull
    public List<KGConcept> getKGConcepts(String query, int limit) {
        LinkedList<KGConcept> ret = new LinkedList<>();
        if (query == null || query == "" || limit <= 0) {
            return ret;
        }
        try {
            ExponentialBackOff backoff = new ExponentialBackOff();
            String responseString = null;
            for (int retries = 0; retries < RETRY_LIMIT; retries++) {
                try {
                    GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
                    url.put("query", query);
                    url.put("limit", limit);
                    url.put("indent", "true");
                    url.put("key", APIConstants.getApiKey());

                    HttpRequest request = httpRequestFactory.buildGetRequest(url);
                    HttpResponse httpResponse = request.execute();
                    responseString = httpResponse.parseAsString();
                    httpResponse.disconnect();
                    break;
                } catch (HttpResponseException ex) {
                    if (ex.getStatusCode() == 429) { // REACHED API LIMIT => DO EXP BACKOFF
                        long backOffMillis = backoff.nextBackOffMillis();
                        if (backOffMillis == ExponentialBackOff.STOP) {
                            // we do not retry, so simply return empty list
                            return ret;
                        } else {
                            // retry, so first sleep for the amount of time chosen by exp backoff
                            try {
                                Thread.sleep(backOffMillis);
                            } catch (InterruptedException e) {

                            }
                        }
                    } else {
                        throw ex;
                    }
                }
            }

            if (responseString == null) {
                return ret;
            }

            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(responseString);
            JSONArray elements = (JSONArray) response.get("itemListElement");

            for (Object element : elements) {
                KGConcept concept = new KGConcept();
                String path = "$.";

                concept.setScore(JsonRead.read(element, path + "resultScore").toString());

                path += "result.";
                concept.setName(JsonRead.read(element, path + "name").toString());
                concept.setDescription(JsonRead.read(element, path + "description").toString());

                Object types = JsonRead.read(element, path + "@type");
                if (types != null && types instanceof Iterable) {
                    for (Object type : (Iterable) types) {
                        concept.addType(type.toString());
                    }
                }

                path += "detailedDescription.";
                concept.setDetailedDescription(
                        JsonRead.read(element, path + "articleBody").toString());
                concept.addRelevantURL(JsonRead.read(element, path + "url").toString());

                ret.add(concept);
            }
        } catch (IOException|ParseException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    /**
     * Return a list of concepts relevant to the given query string. Returns at most LIMIT number of
     * concepts.
     * @param query The string to use for the query
     * @return list of relevant KGConcept (s)
     */
    @Nonnull
    public List<KGConcept> getKGConcepts(String query) {
        return getKGConcepts(query, LIMIT);
    }

}
