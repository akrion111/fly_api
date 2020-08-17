package com.example.demo.controller;

import com.example.demo.exception.ErrorResponse;
import com.example.demo.exception.RefIdException;
import com.example.demo.service.HtmlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class LotApiMvcController {
    private final String apiKey="zEiAS4E5pE3mFnaqIKn3s6kCxsgqHCKH9VB97I0f";
    private final String baseUrl="https://api.lot.com/hr/v3";

    @Autowired
    RestTemplate restTemplate;
    @Autowired
     HtmlService htmlService;

    private LotApiMvcController() {

    }
    @GetMapping(value ="/index")
    String testMvc(){
        return "index";
    }


    @ExceptionHandler(RefIdException.class)
    public ResponseEntity<ErrorResponse> handleException( RefIdException exc) {
        ErrorResponse error=new ErrorResponse(HttpStatus.NOT_FOUND.value(),exc.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
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
        return "categories";
    }


    @GetMapping(value = {"/offers/list/{lang}", "/offers/list/{lang}/{limit}","/offers/list/{lang}/{limit}/{offset}"})
    String getOffersList(Model model, @PathVariable(name="lang") String language
            ,@PathVariable(name="limit",required = false) Integer limit
            ,@PathVariable(name="offset",required = false) Integer offset) throws RefIdException {
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
        String htmlResponse= htmlService.render(response.getBody());
        model.addAttribute("response",htmlResponse);
        return "offers-list";
    }

    @PostMapping("/offer/detail/{lang}")
    String fetchOfferDetails(Model model,@PathVariable(name="lang") String language,@RequestBody String req_body) {
        HttpHeaders headers = new HttpHeaders();
        final String url=baseUrl+"/offer/detail/"+language;
        String ref_id= Arrays.stream(req_body.replaceAll("%2F", "/").split("&")).filter(s->s.contains("ref_id")).collect(Collectors.joining()).replace("ref_id=","").trim();
        String body = "{\"ref_id\":"+"\""+ref_id+"\""+"}";
        System.out.println("final url:"+url);
        System.out.println("ref_id:"+ref_id);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept","*/*");
        headers.set("x-api-key", apiKey);
        HttpEntity<String> httpEntity = new HttpEntity<>(body,headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url,httpEntity,String.class);
        String htmlResponse=htmlService.render(response.getBody());
        model.addAttribute("response",htmlResponse);
        return "offer-details";
    }

}
