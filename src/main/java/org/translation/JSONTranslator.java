package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private Map<String, JSONObject> countries = new HashMap<>();
    private JSONArray file;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);
            file = jsonArray;

            int i;
            String code;
            JSONObject line;
            for (i = 0; i < jsonArray.length(); i++) {
                code = jsonArray.getJSONObject(i).getString("alpha3").toUpperCase();
                line = jsonArray.getJSONObject(i);
                countries.put(code, line);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        if (countries.containsKey(country)){
            Set<String> lang = countries.get(country).keySet();
            lang.remove("id");
            lang.remove("alpha2");
            lang.remove("alpha3");
            List<String> languages = new ArrayList<>(lang);
            return languages;
        }
        return new ArrayList<>();

    }

    @Override
    public List<String> getCountries() {
        List<String> codes = new ArrayList<>();
        int i;
        for (i = 0; i < file.length(); i++) {
            codes.add(file.getJSONObject(i).getString("alpha3"));
        }
        return codes;
    }

    @Override
    public String translate(String country, String language) {
        if (countries.containsKey(country)) {
            int i;
            for (i = 0; i < file.length(); i++) {
                if (file.getJSONObject(i).getString("alpha3").equals(country)) {
                    if (file.getJSONObject(i).keySet().contains(language)) {
                        return file.getJSONObject(i).getString(language);
                    }
                }
            }
        }
        return null;
    }
}
