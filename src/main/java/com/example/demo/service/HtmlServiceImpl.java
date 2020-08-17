package com.example.demo.service;

import com.example.demo.exception.RefIdException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


@Service
public class HtmlServiceImpl implements HtmlService{

    public String render( String jsonData ) {
        return convertFromJson( new JSONObject( jsonData ) );
    }


    public String convertFromJson( Object obj ) {
        StringBuilder html = new StringBuilder( );
        try {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject)obj;
                String[] keys = JSONObject.getNames( jsonObject );
                if(keys==null) throw new RefIdException("wrong 'ref_id' paremeter!");

                html.append("<div class=\"json_object\">");

                if (keys.length > 0) {
                    int index=0;
                    for (String key : keys) {
                        html.append("<div><span class=\"json_key\">")
                                .append(key).append("</span> : ");
                        Object val = jsonObject.get(key);
                        html.append( convertFromJson( val ) );
                        html.append("</div>");
                        index++;
                        if(index==keys.length-1)html.append("</br>------------------------------------");

                    }
                }


                html.append("</div>");

            } else if (obj instanceof JSONArray) {
                JSONArray array = (JSONArray)obj;
                for ( int i=0; i < array.length( ); i++) {
                    html.append( convertFromJson( array.get(i) ) );
                }
            } else {
                html.append( obj );
            }
        } catch (Exception e) { return e.getMessage(); }

        return html.toString( );
    }
}
