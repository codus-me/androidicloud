package me.codus.androidicloud;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.codus.androidicloud.services.ContactsService;

/**
 * Created by codus on 2017/9/7.
 */

public class AndroidiCloudService {

    String homeEndpoint = "https://www.icloud.com";
    String setupEndpoint = "https://setup.icloud.com/setup/ws/1";
    String baseLoginUrl = setupEndpoint+"/login";
    String clientID = UUID.randomUUID().toString().toUpperCase();
    AndroidiCloudSession session = null;
    JSONObject account = new JSONObject();
    Map<String,String> params = new HashMap<>();
    JSONObject webservices = null;
    JSONObject data = null;

    public AndroidiCloudService(String username, String password) throws JSONException, IOException {
        account.put("apple_id",username.toLowerCase());
        account.put("password",password);

        session = new AndroidiCloudSession();
        session.headers.put("Origin", homeEndpoint);
        session.headers.put("Referer", homeEndpoint+"/");
        session.headers.put("User-Agent", "Opera/9.52 (X11; Linux i686; U; en)");

        params.put("clientBuildNumber", "14E45");
        params.put("clientId", clientID);

        authenticate();
    }

    public void authenticate() throws JSONException, IOException {
        account.put("extended_login",false);

        AndroidiCloudSession.Response r = session.post(baseLoginUrl, params, account.toString());
        JSONObject resp = r.json();

        String dsid = "";
        JSONObject dsInfo;
        if(resp!=null && (dsInfo = resp.getJSONObject("dsInfo")) != null) dsid = dsInfo.getString("dsid");
        params.put("dsid",dsid);

        data = resp;
        webservices = data.getJSONObject("webservices");
    }

    public boolean is2FARequired() {
        boolean required = false;
        try {
            if(data.has("hsaChallengeRequired")) required = data.getBoolean("hsaChallengeRequired");
        } catch (JSONException ignore) { }
        return required;
    }

    public ContactsService contacts() throws JSONException {
        String serviceRoot = webservices.getJSONObject("contacts").getString("url");
        return new ContactsService(serviceRoot, session, params);
    }
}
