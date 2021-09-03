package br.com.ottimizza.salesforceclientapi.services;

import br.com.ottimizza.salesforceclientapi.constraints.MethodExecution;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.ottimizza.salesforceclientapi.domain.salesforce.instance.SFInstanceProperties;
import br.com.ottimizza.salesforceclientapi.domain.salesforce.oauth.SFOAuth2Authentication;
import br.com.ottimizza.salesforceclientapi.dao.SalesForceDao;


@Service
public class SalesforceService {

    @Inject
    SalesforceAuthService salesforceAuthService;

    @Inject
    SFInstanceProperties instanceProperties;
    
    @Inject
    private SalesForceDao salesForceDao;

    @Value("${chave-acesso-client}")
    private String CHAVE_FEIGN_CLIENT;

    SFOAuth2Authentication authentication = new SFOAuth2Authentication();

    public String fetchBySalesforceId(String objectId, String id) throws Exception {
        authentication = salesforceAuthService.authorize();
        String url = this.instanceProperties.buildServiceUrl("/sobjects/{0}/{1}", objectId, id);
        return defaultGet(url);
    }

    public String fetchByExternalId(String objectId, String externalIdName, String externalId) throws Exception {
        authentication = salesforceAuthService.authorize();
        String url = this.instanceProperties.buildServiceUrl(
            "/sobjects/{0}/{1}/{2}", objectId, externalIdName, externalId
        );
        return defaultGet(url);
    }

    public String insertMultiple(String objectId, String object) throws Exception {
        authentication = salesforceAuthService.authorize();
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getAccessToken());

        HttpEntity<String> request = new HttpEntity<String>(object, headers);

        String url = this.instanceProperties.buildServiceUrl("/composite/tree/{0}", objectId);

        return template.postForObject(url, request, String.class);
    }

    public String insert(String objectId, String object) throws Exception {
        authentication = salesforceAuthService.authorize();
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getAccessToken());

        HttpEntity<String> request = new HttpEntity<String>(object, headers);

        String url = this.instanceProperties.buildServiceUrl("/sobjects/{0}", objectId);

        return template.postForObject(url, request, String.class);
    }

    public String upsert(String objectId, String externalIdName, String externalId, String object) throws Exception {
        authentication = salesforceAuthService.authorize();

        String url = this.instanceProperties.buildServiceUrl(
            "/sobjects/{0}/{1}/{2}", objectId, externalIdName, externalId
        );

        return defaultPatch(url, object);
    }

    public <T> T executeSOQL(String query, int methodExecution) throws Exception {
        switch (methodExecution){
            case MethodExecution.REST:
            default:
                authentication = salesforceAuthService.authorize();
                String url = this.instanceProperties.buildServiceUrl("/query?q={0}", query);
                return (T) defaultGet(url);
            case MethodExecution.HEROKU_CONNECT:
                return (T) salesForceDao.executeQueryOnMirroredDatabase(query);
       }
    }
    
    public String resolveURL(String salesforceUrl) throws Exception {
        authentication = salesforceAuthService.authorize();
        String url = this.instanceProperties.buildUrlFromDomain(salesforceUrl);
        return defaultGet(url);
    }

    private String defaultGet(String url) {
        RestTemplate template = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(15000);
        requestFactory.setReadTimeout(15000);

        template.setRequestFactory(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getAccessToken());

        return template.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
    }

    private String defaultPatch(String url, String body) {
        RestTemplate template = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(15000);
        requestFactory.setReadTimeout(15000);

        template.setRequestFactory(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getAccessToken());

        return template.patchForObject(url, new HttpEntity<String>(body, headers), String.class);
    }

    public String insertMultipleListener(String chave, String objectId, String object) throws Exception {
        if(!chave.equals(CHAVE_FEIGN_CLIENT))
            throw new IllegalAccessError("Chave de acesso invalida!");

            System.out.println(object);
        authentication = salesforceAuthService.authorize();
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getAccessToken());

        List<String> objetos = Arrays.asList(object.split(","));
        for(String obj : objetos) {
            System.out.println(obj);
            JSONObject sfParticularidade = new JSONObject(obj.trim());
            String url = this.instanceProperties.buildServiceUrl(
                "/sobjects/{0}/{1}/{2}", objectId, "ID_Externo__c", sfParticularidade.optString("ID_Externo__c")
            );

            defaultPatch(url, object);
        }

        return "Regras enviadas com sucesso!";
        //HttpEntity<String> request = new HttpEntity<String>(object, headers);

        //String url = this.instanceProperties.buildServiceUrl("/composite/tree/{0}", objectId);

        //return template.postForObject(url, request, String.class);
    }


    
}
