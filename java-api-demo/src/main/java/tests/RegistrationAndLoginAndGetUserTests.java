package tests;

import api.Login;
import api.Registration;
import api.ResponseReader;
import helpers.ReadConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.IOException;
import java.io.InputStream;

import static api.Login.*;
import static api.Login.printUserID;


public class RegistrationAndLoginAndGetUserTests {

    private static String name;
    private static String email;
    private static String password;

    protected static String urlString = "http://restapi.adequateshop.com/api/users?page=1";

    //protected static String urlUserID = "http://restapi.adequateshop.com/api/users/" + getUserID();
    protected static String accessToken = "bearer" + getAccessToken();
    protected static String responseCode;
    private static String responseBody;


    @BeforeTest
    public static void credentials() {
        //name = "Tester";
        //email = "keldrick.jaxel7@fullangle.org";
        //password = "paasss";

        ReadConfig configJson = new ReadConfig();
        configJson.readJson();
        name = configJson.getName();
        email = configJson.getEmail();
        password = configJson.getPassword();

    }


    @Test(priority = 0)
    public static void testSuccessfulRegistration() throws IOException {
        Registration postRequestsRegistration = new Registration();
        postRequestsRegistration.registration(name, email, password);
        String responseCodeRegistration = postRequestsRegistration.getResponseCodeRegistration();
        Assert.assertTrue(responseCodeRegistration.contains("200"), responseCodeRegistration);
        String registrationMessage = postRequestsRegistration.getRegistrationMessage();
        Assert.assertTrue(registrationMessage.contains("success"), registrationMessage);
    }

    @Test(priority = 1)
    public static void testSuccessfulLogin() throws IOException {
        Login postRequests = new Login();
        postRequests.login(email, password);
        String responseCode = postRequests.getResponseCode();
        Assert.assertTrue(responseCode.contains("200"), responseCode);
        String authMessage = postRequests.getLoginMessage();
        Assert.assertTrue(authMessage.contains("success"), authMessage);

        printAccessToken();
        printUserID();

    }


    @Test(priority = 2)
    public static void testGetUsers() throws IOException {
        Login postRequests = new Login();
        postRequests.login(email, password);
        HttpGet getUsers         = new HttpGet(urlString);
        getUsers.setHeader("Content-type", "application/json");
        getUsers.setHeader("Authorization", "bearer " + getAccessToken());

        HttpClient httpClient    = HttpClientBuilder.create().build();
        HttpResponse response    = httpClient.execute(getUsers);
        responseCode = response.getStatusLine().toString();

        Assert.assertEquals(responseCode, "HTTP/1.1 200 OK");

        //Parse the response body
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // A Simple JSON Response Read
            InputStream instream = entity.getContent();
            responseBody = new ResponseReader().convertStreamToString(instream);
            instream.close();
        }
        System.out.println(responseCode);
        System.out.println(responseBody);
    }


    @Test(priority = 3)
    public static void testGetSpecificUser() throws IOException {

        Login postRequests = new Login();
        postRequests.login(email, password);
        HttpGet getUsers         = new HttpGet(urlString + getUserID());
        getUsers.setHeader("Content-type", "application/json");
        getUsers.setHeader("Authorization", "bearer " + getAccessToken());

        HttpClient httpClient    = HttpClientBuilder.create().build();
        HttpResponse response    = httpClient.execute(getUsers);
        responseCode = response.getStatusLine().toString();

        Assert.assertEquals(responseCode, "HTTP/1.1 200 OK");

        //Parse the response body
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // A Simple JSON Response Read
            InputStream instream = entity.getContent();
            responseBody = new ResponseReader().convertStreamToString(instream);
            instream.close();
        }
        System.out.println(responseCode);
        System.out.println(responseBody);

    }

    @Test(priority = 4)
    public static void testWrongToken() throws IOException {
        HttpGet getUserID = new HttpGet(urlString);
        getUserID.setHeader("Content-type", "application/json");
        getUserID.setHeader("Authorization", "bearer wrong");
        HttpClient httpClient    = HttpClientBuilder.create().build();
        HttpResponse response    = httpClient.execute(getUserID);
        responseCode = response.getStatusLine().toString();

        Assert.assertEquals(responseCode, "HTTP/1.1 401 Unauthorized");

        //Parse the response body
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // A Simple JSON Response Read
            InputStream instream = entity.getContent();
            responseBody = new ResponseReader().convertStreamToString(instream);
            instream.close();
        }

        System.out.println(responseCode);
        System.out.println(responseBody);

    }


    @Test(priority = 5)
    public static void testWrongID() throws IOException {
        HttpGet getUserID = new HttpGet("http://restapi.adequateshop.com/api/users/" + "wrong");
        getUserID.setHeader("Content-type", "application/json");
        //  getUserID.setHeader("Authorization", accessToken);
        HttpClient httpClient    = HttpClientBuilder.create().build();
        HttpResponse response    = httpClient.execute(getUserID);
        responseCode = response.getStatusLine().toString();

        Assert.assertEquals(responseCode, "HTTP/1.1 401 Unauthorized");

        //Parse the response body
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // A Simple JSON Response Read
            InputStream instream = entity.getContent();
            responseBody = new ResponseReader().convertStreamToString(instream);
            instream.close();
        }

        System.out.println(responseCode);
        System.out.println(responseBody);

    }

}

