package de.hsmw.threemaextractor.service.main;

import javax.json.Json;
import javax.json.JsonArray;
import java.io.ByteArrayInputStream;

public class JsonUtils {

    public static JsonArray parseJsonBody(String jsonBody) {
        return Json.createReader(new ByteArrayInputStream(jsonBody.getBytes())).readArray();
    }
}
