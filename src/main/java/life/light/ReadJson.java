package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ReadJson {

    static JsonNode getCvJson(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(new File(path));
    }

    static void validateJson(JsonNode jsonNode, String schemaPath) throws IOException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        try (InputStream schemaStream = new FileInputStream(schemaPath)) {
            JsonSchema schema = factory.getSchema(schemaStream);
            Set<ValidationMessage> errors = schema.validate(jsonNode);

            if (!errors.isEmpty()) {
                StringBuilder message = new StringBuilder("Erreur de validation JSON :\n");
                for (ValidationMessage error : errors) {
                    message.append("- ").append(error.getMessage()).append("\n");
                }
                throw new IOException(message.toString());
            }
        }
    }

}
