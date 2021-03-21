package com.tcc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author lgdamy@raiadrogasil.com on 14/03/2021
 */
@Configuration
@EnableSwagger2
@EnableScheduling
public class SwaggerConfig {

    private static Logger log = LoggerFactory.getLogger(SwaggerConfig.class);

    private static final String V2_DOCS_URL = "/v2/api-docs";
    private static final String ZUUL_PREFIX = "zuul.routes.";
    private static final String ZUUL_SUFIX =  ".url";
    private static final String PATH_SUFIX = ".path";
    private static final String SERVICE_URL = "/swagger/";
    private static final String SWAGGER_VERSION = "2.0";
    private static final Map.Entry<String, String> AUTH_TYPE = Map.entry("type","oauth2");
    private static final Map.Entry<String, String> AUTH_FLOW = Map.entry("flow","password");
    private static final String AUTH_URL = "tokenUrl";
    private static final String AUTH_NAME = "JWT";
    private static final String SEC_DEFINITIONS = "securityDefinitions";


    private Map<String, Map<String, Object>> docsMap = new TreeMap<>(String::compareTo);

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper mapper;

    private RestTemplate restTemplate = new RestTemplate();

    @Primary
    @Bean
    @Lazy
    public SwaggerResourcesProvider swaggerResourcesProvider(InMemorySwaggerResourcesProvider defaultResourcesProvider) {
        return () -> {
            List<SwaggerResource> resources = new ArrayList<>(defaultResourcesProvider.get());
            resources.clear();
            resources.addAll(getSwaggerDefinitions());
            return resources;
        };
    }

    private List<SwaggerResource> getSwaggerDefinitions() {
        return docsMap.entrySet().stream().map(e -> {
            SwaggerResource resource = new SwaggerResource();
            resource.setLocation(SERVICE_URL + e.getKey());
            resource.setName(e.getKey());
            resource.setSwaggerVersion(SWAGGER_VERSION);
            return resource;
        }).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000L)
    public void refreshDocs() {
        if (env instanceof ConfigurableEnvironment) {
            for (PropertySource<?> source : ((ConfigurableEnvironment) env).getPropertySources()) {
                if (source instanceof EnumerablePropertySource) {
                    for (String key : ((EnumerablePropertySource) source).getPropertyNames()) {
                        if (key.startsWith(ZUUL_PREFIX) && key.endsWith(ZUUL_SUFIX)) {
                            try {
                                URI uri = UriComponentsBuilder.fromUriString(env.getProperty(key))
                                        .path(V2_DOCS_URL).build().toUri();
                                Map<String, Object> jsonSwagger = restTemplate.exchange(uri, HttpMethod.GET,null, new ParameterizedTypeReference<Map<String, Object>>(){}).getBody();
                                if (jsonSwagger != null) {
                                    docsMap.put(key.replace(ZUUL_PREFIX,"").replace(ZUUL_SUFIX,""), jsonSwagger);
                                }
                            } catch (Exception e) {
                                log.warn("Incapaz de buscar api-docs de {} ({}:{})", key.replace(ZUUL_PREFIX,"").replace(ZUUL_SUFIX,""), e.getClass().getSimpleName(), e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    public String getDefinition(String key, HttpServletRequest request) {
        try {
            URL requestURL = new URL(request.getRequestURL().toString());
            String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
            var definition = docsMap.get(key);
            definition.put("host", requestURL.getHost() + port);
            definition.put("basePath", "/" + env.getProperty(ZUUL_PREFIX + key + PATH_SUFIX).split("/", 3)[1]);
            definition.put(SEC_DEFINITIONS, securityDefinitions(requestURL, port));
            return mapper.writeValueAsString(definition);
        }catch (Exception e) {
            return "";
        }
    }

    public Map<String, Object> securityDefinitions(URL requestURL, String port) {
        Map<String, String> jwt = new LinkedHashMap<>();
        jwt.put(AUTH_TYPE.getKey(),AUTH_TYPE.getValue());
        jwt.put(AUTH_FLOW.getKey(), AUTH_FLOW.getValue());
        jwt.put(AUTH_URL, requestURL.getProtocol() + "://" + requestURL.getHost() + port + "/oauth/token");
        return Collections.singletonMap(AUTH_NAME, jwt);
    }
}
