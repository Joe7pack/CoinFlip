package com.guzzardo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ReadJSONAges {

    public static class KeysAndAges {
        String key;
        Integer age;
    }

    public static void main (String[] args) throws IOException {
        System.setProperty("http.agent", "Chrome");
        try {
            URL url = new URL("https://coderbyte.com/api/challenges/json/age-counting");
            try {
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder resultLine = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    resultLine.append(inputLine);
                }

                String ages = resultLine.toString();
                String originalJsonString = resultLine.toString(); //assign your JSON String here
                StringBuilder correctedJsonString = new StringBuilder(originalJsonString);
                int beginIndex = 0;
                String replacementString = "}, {key=";
                String searchString = ", key=";
                int fromIndex = correctedJsonString.indexOf("key=", beginIndex);

                correctedJsonString.delete(fromIndex-1, fromIndex);
                correctedJsonString.delete(correctedJsonString.length()-2, correctedJsonString.length()-1);
                correctedJsonString.insert(fromIndex-1, "[{");
                correctedJsonString.insert(correctedJsonString.length(), "]}");

                while (beginIndex < originalJsonString.length()) {
                    fromIndex = correctedJsonString.indexOf(searchString, beginIndex);
                    if (fromIndex < 0)
                        break;
                    correctedJsonString.replace(fromIndex,  fromIndex+replacementString.length()-2, replacementString);
                }

                String newJSONAges = new String(correctedJsonString);
                JsonObject convertedObject = new Gson().fromJson(newJSONAges, JsonObject.class);
                JsonArray jsonArray = convertedObject.getAsJsonArray("data");
                Type type = new TypeToken<List<KeysAndAges>>(){}.getType();
                List<KeysAndAges> keysAndAges = new Gson().fromJson(jsonArray, type);

                int ageCount = 0;
                for (KeysAndAges ageValue : keysAndAges) {
                    if (ageValue.age > 49)
                        ageCount++;
                }
                System.out.println("people over 49: " + ageCount);
            } catch (IOException ioEx) {
                //System.out.println(ioEx);
                throw new IOException(ioEx);
            }
        } catch (MalformedURLException malEx) {
            //System.out.println(malEx);
            throw new MalformedURLException(malEx.toString());
        }
    }
}