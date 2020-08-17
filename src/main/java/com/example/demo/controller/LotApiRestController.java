package com.example.demo.controller;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;



@RestController
@RequestMapping("/api")
public class LotApiRestController {

    private final String apiKey="zEiAS4E5pE3mFnaqIKn3s6kCxsgqHCKH9VB97I0f";
    private final String baseUrl="https://api.lot.com/hr/v3";

    public LotApiRestController() {

    }
    @GetMapping("/test")
    String testMvc(){
        return "index";
    }


    HttpEntity<String> createHeaderWithApiKey(String apiKey){
        final HttpHeaders header = new HttpHeaders();
        header.set("x-api-key", apiKey);
        return new HttpEntity<>(header);
    }

    @GetMapping("/categories/{language}") String getCategories(@PathVariable String language){
        RestTemplate restTemplate = new RestTemplate();
        final HttpEntity<String> header = createHeaderWithApiKey(apiKey);
        final String url=baseUrl+"/categories/"+language;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, header, String.class);
        System.out.println(response.getBody());
        return response.getBody();
    }


    @GetMapping(value = {"/offers/list/{lang}", "/offers/list/{lang}/{limit}","/offers/list/{lang}/{limit}/{offset}"})
    String getOffersList(@PathVariable(name="lang") String language
            ,@PathVariable(name="limit",required = false) Integer limit
            ,@PathVariable(name="offset",required = false) Integer offset)
    {
        RestTemplate restTemplate = new RestTemplate();
        final HttpEntity<String> header = createHeaderWithApiKey(apiKey);
        String url=baseUrl+"/offers/list/"+language;
        if(limit!=null)
        {
            url+="/"+limit;
        }
        if(offset!=null)
        {
            url+="/"+offset;
        }
        System.out.println("final url:"+url);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, header, String.class);
        return response.getBody();
    }

   @PostMapping("/offer/detail/{lang}")
   @ResponseBody
   String fetchOfferDetails(@PathVariable(name="lang") String language,@RequestBody String ref_id)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        final String url=baseUrl+"/offer/detail/"+language;
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        System.out.println("final url:"+url);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        body.add("ref_id",ref_id);
        HttpEntity< MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url,httpEntity,String.class);
        String htmlData=renderHtml(response.getBody());
        System.out.println("status:"+response.getStatusCodeValue());
        System.out.println("body:"+response.getBody());
        return response.getBody();
    }



    public String renderHtml( String JsonData ) {
        return jsonToHtml( new JSONObject( JsonData ) );
    }


    private String jsonToHtml( Object obj ) {
        StringBuilder html = new StringBuilder( );

        try {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject)obj;
                String[] keys = JSONObject.getNames( jsonObject );

                html.append("<div class=\"json_object\">");

                if (keys.length > 0) {
                    for (String key : keys) {
                        // print the key and open a DIV
                        html.append("<div><span class=\"json_key\">")
                                .append(key).append("</span> : ");

                        Object val = jsonObject.get(key);
                        // recursive call
                        html.append( jsonToHtml( val ) );
                        // close the div
                        html.append("</div>");
                    }
                }

                html.append("</div>");

            } else if (obj instanceof JSONArray) {
                JSONArray array = (JSONArray)obj;
                for ( int i=0; i < array.length( ); i++) {
                    // recursive call
                    html.append( jsonToHtml( array.get(i) ) );
                }
            } else {
                // print the value
                html.append( obj );
            }
        } catch (Exception e) { return e.getMessage(); }

        return html.toString( );
    }

}
