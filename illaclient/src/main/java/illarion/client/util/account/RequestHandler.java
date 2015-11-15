package illarion.client.util.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import illarion.common.data.IllarionSSLSocketFactory;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * This class is sending out requests and handling the responses.
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class RequestHandler {
    @Nonnull
    private final String rootUrl;

    RequestHandler(@Nonnull String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public <T> T sendRequest(@Nonnull Request request, @Nonnull Map<Integer, Class<T>> responseMap) throws IOException {
        if (request instanceof AuthenticatedRequest) {
            java.net.Authenticator.setDefault(((AuthenticatedRequest) request).getAuthenticator());
        }
        URL requestUrl = new URL(buildRequestUrl(rootUrl, request.getRoute()));
        URLConnection urlConnection = requestUrl.openConnection();
        if (!(urlConnection instanceof HttpURLConnection)) {
            throw new IllegalStateException("Request did not create the expected http connection.");
        }
        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

        if (httpConnection instanceof HttpsURLConnection) {
            SSLSocketFactory factory = IllarionSSLSocketFactory.getFactory();
            if (factory != null) {
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(factory);
            }
        }
        httpConnection.setRequestMethod(request.getMethod());

        httpConnection.connect();
        int response = httpConnection.getResponseCode();
        Class<T> responseClass = responseMap.get(response);
        if (responseClass == null) {
            return null;
        }

        try (JsonReader rd = new JsonReader(new InputStreamReader(httpConnection.getInputStream(), Charset.forName("UTF-8")))) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
            return gson.fromJson(rd, responseClass);
        }
    }

    @Nonnull
    private static String buildRequestUrl(@Nonnull String root, @Nonnull String route) {
        String fixedRoot;
        if (root.endsWith("/")) {
            fixedRoot = root.substring(0, root.length() - 1);
        } else {
            fixedRoot = root;
        }

        if (route.startsWith("/")) {
            return fixedRoot + route;
        } else {
            return fixedRoot + '/' + route;
        }
    }
}
