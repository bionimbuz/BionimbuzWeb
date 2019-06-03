package jobs.helpers;

import app.models.security.TokenModel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TokenHelper {
    public static TokenModel update_identity(String cloudType, TokenModel token, String credentialStr) {

        if (!(cloudType.equals("openstack"))) {
            return token;
        }

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(credentialStr);

        String identity = json.get("host").getAsString() + "/" + json.get("project_id").getAsString();

        token.setIdentity(identity);
        return token;
    }

}
