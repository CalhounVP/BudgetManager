package com.github.calhounvp.services;

import com.github.calhounvp.entities.SpendRequest;
import com.github.calhounvp.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/***
 * The JsonService class is a class to centralize the
 * use of the simple.json API.
 * This service will be used to check the json file's
 * content or get the writable JSON content.
 */
public class JsonService {
    //____________________________________Methods___________________________________
    //*********************************Utility Method*******************************
    @SuppressWarnings("unchecked")
    public String getUsersJsonString (ArrayList<User> users) {
        //local variable
        JSONArray usersList = new JSONArray();

        //statements
        for (User user: users) {
            usersList.add(createUserJsonObject(user));
        }

        return usersList.toJSONString();
    }

    @SuppressWarnings("unchecked")
    public String getRequestJsonString (ArrayList<SpendRequest> requests) {
        //local variable
        JSONArray requestList = new JSONArray();

        //statements
        for (SpendRequest request: requests) {
            requestList.add(createSpendRequestJsonObject(request));
        }

        return requestList.toJSONString();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<User> getUserList (FileReader userFileReader) {
        //local variables
        ArrayList<User> usersList = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONArray usersJsonArray = null;

        //statements
        try {
            usersJsonArray = (JSONArray) jsonParser.parse(userFileReader);
            usersJsonArray.forEach( user -> usersList.add(getUser((JSONObject) user)));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<SpendRequest> getSpendRequestList (FileReader requestFileReader) {
        //local variables
        ArrayList<SpendRequest> requestsList = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONArray requestsJsonArray = null;

        //statements
        try {
            requestsJsonArray = (JSONArray) jsonParser.parse(requestFileReader);
            requestsJsonArray.forEach(request -> requestsList.add(getSpendRequest((JSONObject) request)));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return requestsList;
    }

    //********************************Internal Method*******************************
    @SuppressWarnings("unchecked")
    private JSONObject createUserJsonObject(User user) {
        //local variables
        JSONObject userObject = new JSONObject();
        JSONObject userDetails = new JSONObject();

        //statements
        userDetails.put("userName", user.getUserName());
        userObject.put("user", userDetails);

        return userObject;
    }

    @SuppressWarnings("unchecked")
    private JSONObject createSpendRequestJsonObject (SpendRequest request) {
        //local variables
        JSONObject requestDetails = new JSONObject();
        JSONObject requestObject = new JSONObject();
        SpendRequest.Status[] statuses = SpendRequest.Status.values();

        //statements
        requestDetails.put("reason", request.getRequestInfo());
        requestDetails.put("budget", request.getRequestCost());
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(request.getStatus())) {
                requestDetails.put("status", i);
            }
        }
        requestObject.put("request", requestDetails);

        return requestObject;
    }

    private User getUser (JSONObject user) {
        //local variables
        String userName = "";
        JSONObject userDetails = null;

        //statements
        userDetails = (JSONObject) user.get("user");
        userName = (String) userDetails.get("userName");

        return new User(userName, null);
    }

    private SpendRequest getSpendRequest (JSONObject request) {
        //local variables
        SpendRequest.Status[] statuses = SpendRequest.Status.values();
        JSONObject requestDetails = null;
        String info = "";
        BigDecimal budget = null;
        SpendRequest.Status status = null;
        Long arrayValue = null;

        //statements
        requestDetails = (JSONObject) request.get("request");
        info = (String) requestDetails.get("reason");
        budget = BigDecimal.valueOf((double) requestDetails.get("budget"));
        arrayValue = (Long) requestDetails.get("status");
        status = statuses[Math.toIntExact(arrayValue)];

        return new SpendRequest(info, budget, status);
    }

}
