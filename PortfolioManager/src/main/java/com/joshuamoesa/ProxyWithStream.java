package com.joshuamoesa;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.Writer;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.LambdaLogger;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


public class ProxyWithStream implements RequestStreamHandler {
	JSONParser parser = new JSONParser();


    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

    	LambdaLogger logger = context.getLogger();
    	logger.log("Loading Java Lambda handler of ProxyWithStream");


        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        String name = "World";
        String responseCode = "200";

        try {
        	JSONObject event = (JSONObject)parser.parse(reader);
            if (event.get("queryStringParameters") != null) {
                JSONObject qps = (JSONObject)event.get("queryStringParameters");
                if ( qps.get("name") != null) {
                    name = (String)qps.get("name");
                }
                if (qps.get("httpStatus") != null) {
                    responseCode = qps.get("httpStatus)").toString();
                }
            }


        	JSONObject responseBody = new JSONObject();
        	responseBody.put("input", event.toJSONString());
        	responseBody.put("message", "Hello " + name + "!");

        	JSONObject headerJson = new JSONObject();
        	headerJson.put("x-custom-response-header", "my custom response header value");

        	responseJson.put("statusCode", responseCode);
        	responseJson.put("headers", headerJson);
        	responseJson.put("body", responseBody.toString());  

        } catch(ParseException pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }

    	logger.log(responseJson.toJSONString());
    	OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    	writer.write(responseJson.toJSONString());  
    	writer.close();
    }
}