package com.example.demo.controller;


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
        final HttpEntity<String> entity = new HttpEntity<>(header);
        return entity;
    }

    @GetMapping("/categories/{language}") String getCategories(@PathVariable String language){
        RestTemplate restTemplate = new RestTemplate();
        final HttpEntity<String> header = createHeaderWithApiKey(apiKey);
        final String url=baseUrl+"/categories/"+language;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, header, String.class);
        System.out.println(response.getBody());
        return response.getBody();
        //return "categories";
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
        System.out.println("dlugosc:"+response.getBody().length());
        return response.getBody();
        //return "offers-list";
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
        System.out.println("length:"+response.getBody().length());
        System.out.println("status:"+response.getStatusCodeValue());
        System.out.println("body:"+response.getBody());
        return response.getBody();
       // return "offer-details";
    }


}
