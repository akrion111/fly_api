package com.example.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/lol")
public class LotApiMvcController {
    private final String apiKey="zEiAS4E5pE3mFnaqIKn3s6kCxsgqHCKH9VB97I0f";
    private final String baseUrl="https://api.lot.com/hr/v3";

    @Autowired
    RestTemplate restTemplate;

    public LotApiMvcController() {

    }
    @GetMapping(value ="/test")
    String testMvc(){
        return "index";
    }


    HttpEntity<String> createHeaderWithApiKey(String apiKey){
        final HttpHeaders header = new HttpHeaders();
        header.set("x-api-key", apiKey);
        return new HttpEntity<>(header);
    }

    @GetMapping("/categories/{language}") String getCategories(Model model, @PathVariable String language){
        final HttpEntity<String> header = createHeaderWithApiKey(apiKey);
        final String url=baseUrl+"/categories/"+language;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, header, String.class);

        model.addAttribute("response",response.getBody());
        System.out.println(response.getBody());
        return "categories";
    }


    @GetMapping(value = {"/offers/list/{lang}", "/offers/list/{lang}/{limit}","/offers/list/{lang}/{limit}/{offset}"})
    String getOffersList(Model model, @PathVariable(name="lang") String language
            ,@PathVariable(name="limit",required = false) Integer limit
            ,@PathVariable(name="offset",required = false) Integer offset)
    {
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
        model.addAttribute("response",response.getBody());
        System.out.println("dlugosc:"+response.getBody());
        return "offers-list";
    }

    @PostMapping("/offer/detail/{lang}")
    String fetchOfferDetails(Model model,@PathVariable(name="lang") String language,@RequestBody String req_body){
        System.out.println("no elooooo post ");
        HttpHeaders headers = new HttpHeaders();
        final String url=baseUrl+"/offer/detail/"+language;
        System.out.println("request body:"+req_body);
        String ref_id= Arrays.stream(req_body.replaceAll("%2F", "/").split("&")).filter(s->s.contains("ref_id")).collect(Collectors.joining()).replace("ref_id=","").trim();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        System.out.println("final url:"+url);
        System.out.println(ref_id);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept","*/*");
        headers.set("x-api-key", apiKey);
        body.add("ref_id",ref_id);
        HttpEntity< MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url,httpEntity,String.class);
        model.addAttribute("response",response.getBody());
        System.out.println("status:"+response.getStatusCodeValue());
        System.out.println("body:"+response.getBody());
        return "offer-details";
    }


}
