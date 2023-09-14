package io.github.navjotsrakhra.internalsecureproxy.controller;

import io.github.navjotsrakhra.internalsecureproxy.service.ProxyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrometheusProxyController {

    private final ProxyService service;

    public PrometheusProxyController(ProxyService service) {
        this.service = service;
    }

    @GetMapping(path = "/", produces = "text/plain")
    public ResponseEntity<String> prometheus() {
        var response = service.getPrometheusFromEnvVarWithUsernameAndPassword();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(response.length())
                .body(response)
                ;
    }
}
