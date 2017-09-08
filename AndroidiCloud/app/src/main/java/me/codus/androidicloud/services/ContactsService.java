package me.codus.androidicloud.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.codus.androidicloud.AndroidiCloudSession;

/**
 * Created by codus on 2017/9/7.
 */

public class ContactsService {

    AndroidiCloudSession session = null;
    Map<String,String> params = new HashMap<>();
    String serviceRoot;
    String endPoint;
    String refreshUrl;
    String changesetUrl;
    JSONObject response;

    public ContactsService(String serviceRoot, AndroidiCloudSession session, Map<String,String> params) {
        this.session = session;
        this.params = params;
        this.serviceRoot = serviceRoot;
        this.endPoint = serviceRoot+"/co";
        this.refreshUrl = endPoint+"/startup";
        this.changesetUrl = endPoint+"/changeset";
    }

    public void refreshClient() throws IOException, JSONException {
        Map<String,String> contactParams = new HashMap<>();
        contactParams.putAll(params);
        contactParams.put("clientVersion","2.1");
        contactParams.put("locale","en_US");
        contactParams.put("order","last,first");

        AndroidiCloudSession.Response r = session.get(refreshUrl, contactParams);
        response = r.json();

        Map<String,String> refreshParams = new HashMap<>();
        refreshParams.putAll(params);
        refreshParams.put("prefToken", response.getString("prefToken"));
        refreshParams.put("syncToken", response.getString("syncToken"));

        session.headers.put("Content-Type","");//LISTEN: you must clear `Content-Type` here because the default value `application/x-www-form-urlencoded` result in 420 error
        session.post(changesetUrl, refreshParams, null);
        session.headers.remove("Content-Type");

        r = session.get(refreshUrl, contactParams);
        response = r.json();
    }

    public JSONArray all() throws JSONException, IOException {
        refreshClient();
        return response.getJSONArray("contacts");
    }
}
