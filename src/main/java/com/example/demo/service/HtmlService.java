package com.example.demo.service;

import org.json.JSONArray;

public interface HtmlService {
     String render( String jsonData );
     String convertFromJson( Object obj );
}
