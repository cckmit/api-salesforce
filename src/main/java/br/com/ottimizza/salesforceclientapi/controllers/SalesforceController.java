package br.com.ottimizza.salesforceclientapi.controllers;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ottimizza.salesforceclientapi.domain.models.SFParticularidade;
import br.com.ottimizza.salesforceclientapi.domain.salesforce.instance.SFInstanceProperties;
import br.com.ottimizza.salesforceclientapi.services.SFParticularidadeService;
import br.com.ottimizza.salesforceclientapi.services.SalesforceAuthService;
import br.com.ottimizza.salesforceclientapi.services.SalesforceService;

@RestController
@RequestMapping("/api/v1/salesforce")
public class SalesforceController {

    @Inject
    SalesforceService salesforceService;

    @GetMapping("/sobjects/{objectName}")
    public ResponseEntity<?> query(@RequestParam("q") String query, OAuth2Authentication authentication) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sobjects/{objectId}")
    public ResponseEntity<?> create(@PathVariable("objectId") String objectId, 
                                    @RequestBody String object,
                                    OAuth2Authentication authentication) throws Exception {
        return ResponseEntity.ok(salesforceService.insert(objectId, object));
    }

    @PatchMapping("/sobjects/{objectId}/{externalIdName}/{externalId}")
    public ResponseEntity<?> upsert(@PathVariable("objectId") String objectId,
                                    @PathVariable("externalIdName") String externalIdName, 
                                    @PathVariable("externalId") String externalId,
                                    @RequestBody String object,
                                    OAuth2Authentication authentication) throws Exception {
        return ResponseEntity.ok(salesforceService.upsert(objectId, externalIdName, externalId, object));
    }
    
}