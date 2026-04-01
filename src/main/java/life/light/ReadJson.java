package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ReadJson {

    static JsonNode getCvJson(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(new File(path));
    }

}
