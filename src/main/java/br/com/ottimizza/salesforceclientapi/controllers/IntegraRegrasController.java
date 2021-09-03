package br.com.ottimizza.salesforceclientapi.controllers;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ottimizza.salesforceclientapi.services.SalesforceService;

@RestController
@RequestMapping("integra/v1/salesforce")
public class IntegraRegrasController {

    @Inject
    SalesforceService salesforceService;
    
    @PostMapping("/composite/tree/{objectId}")
    public ResponseEntity<?> createMultipleRecordsListener(@RequestHeader("Authorization") String chave,
                                    @PathVariable("objectId") String objectId, 
                                    @RequestBody String object) throws Exception {
        return ResponseEntity.ok(salesforceService.insertMultipleListener(chave, objectId, object));
    }

}
