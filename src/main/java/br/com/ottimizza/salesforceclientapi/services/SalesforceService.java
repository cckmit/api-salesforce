package br.com.ottimizza.salesforceclientapi.services;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.ottimizza.salesforceclientapi.domain.salesforce.instance.SFInstanceProperties;
import br.com.ottimizza.salesforceclientapi.domain.salesforce.oauth.SFOAuth2Authentication;

@Service
public class SalesforceService {

    @Inject
    SalesforceAuthService salesforceAuthService;

    @Inject
    SFInstanceProperties instanceProperties;

    SFOAuth2Authentication authentication;

    @Autowired
    public SalesforceService() throws Exception {
        // realiza autenticação no salesforce.
        authentication = salesforceAuthService.authorize();
    }

    public String insert(String objectId, String object) throws Exception {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getAccessToken());

        HttpEntity<String> request = new HttpEntity<String>(object, headers);

        String url = this.instanceProperties.buildServiceUrl("/sobjects/{0}", objectId);

        return template.postForObject(url, request, String.class);
    }

    public String upsert(String objectId, String externalIdName, String externalId, String object) throws Exception {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getAccessToken());

        HttpEntity<String> request = new HttpEntity<String>(object, headers);

        String url = this.instanceProperties.buildServiceUrl(
            "/sobjects/{0}/{1}/{2}", objectId, externalIdName, externalId
        );

        return template.postForObject(url, request, String.class);
    }


}