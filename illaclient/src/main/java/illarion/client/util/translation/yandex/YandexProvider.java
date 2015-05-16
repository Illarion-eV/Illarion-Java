/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.util.translation.yandex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import illarion.client.util.translation.TranslationDirection;
import illarion.client.util.translation.TranslationProvider;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the translation provider that utilizes Yandex for the translation.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class YandexProvider implements TranslationProvider {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(YandexProvider.class);

    @Nullable
    private final URL serviceUrl;

    @Nonnull
    private final String userAgent;

    public YandexProvider() {
        URL url = null;
        try {
            url = new URL("https://translate.yandex.net/api/v1/tr.json/translate");
        } catch (MalformedURLException e) {
            log.error("Failed to resolve the URL to the translator service. Service is not active.", e);
        }
        serviceUrl = url;
        userAgent = readUserAgent();
    }

    /**
     * Fetch the user agent that is used to request data from the service.
     *
     * @return the user agent that is supposed to be used
     */
    @Nonnull
    private static String readUserAgent() {
        String selectedUserAgent = null;
        URL resource = Thread.currentThread().getContextClassLoader().getResource("user-agents.txt");
        if (resource != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream(), "UTF-8"))) {
                List<String> agents = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    agents.add(line);
                }
                Random rand = new Random();
                selectedUserAgent = agents.get(rand.nextInt(agents.size()));
            } catch (UnsupportedEncodingException e) {
                log.error("Encoding required to read the user agents file is not supported.", e);
            } catch (IOException e) {
                log.error("Failed to read the user agents file.", e);
            }
        }
        return (selectedUserAgent == null) ? "Mozilla/5.0" : selectedUserAgent;
    }

    @Nullable
    @Override
    public String getTranslation(@Nonnull String original, @Nonnull TranslationDirection direction) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(serviceUrl).append('?');
        try {
            queryBuilder.append("text=").append(URLEncoder.encode(original, "UTF-8"));
            queryBuilder.append('&').append("lang=").append(getLang(direction));
            queryBuilder.append("&srv=tr-text");

            URL queryUrl = new URL(queryBuilder.toString());
            URLConnection connection = queryUrl.openConnection();
            connection.addRequestProperty("User-Agent", userAgent);
            try (JsonReader rd = new JsonReader(new InputStreamReader(connection.getInputStream(),
                    Charset.forName("UTF-8")))) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                Response response = gson.fromJson(rd, Response.class);
                if (response != null) {
                    List<String> result = response.getTexts();
                    if (result.size() == 1) {
                        return result.get(0);
                    } else if (result.size() > 1) {
                        StringBuilder resultBuilder = new StringBuilder();
                        for (String text : result) {
                            resultBuilder.append(text).append(' ');
                        }
                        return resultBuilder.toString().trim();
                    }
                }
            } catch (IOException e) {
                log.error("Error while reading from the service.", e);
            } catch (JsonParseException e) {
                log.error("Unexpected error while decoding json", e);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Error while encoding the text for transfer to the Yandex provider.", e);
        } catch (MalformedURLException e) {
            log.error("Generated URL for the query to Yandex appears to have a invalid format.", e);
        } catch (IOException e) {
            log.error("Failed to open connection for Yandex provider.", e);
        }
        return null;
    }

    @Nonnull
    @Contract(pure = true)
    private static String getLang(@Nonnull TranslationDirection direction) {
        switch (direction) {
            case GermanToEnglish:
                return "de-en";
            case EnglishToGerman:
                return "en-de";
        }
        throw new UnsupportedOperationException("Unexpected translation direction received.");
    }

    @Override
    public boolean isProviderWorking() {
        return serviceUrl != null;
    }
}
