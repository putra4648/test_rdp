package com.pradana.test_rdp.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pradana.test_rdp.dto.PlanetDto;
import com.pradana.test_rdp.dto.SearchResponseDto;
import com.pradana.test_rdp.dto.StartshipDto;

@Service
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    public ResponseEntity<?> getSearch(String param) {

        Cache restCache = cacheManager.getCache("restCache");

        ValueWrapper wrapper = restCache.get("myKey");

        // Jika ada data
        if (wrapper != null) {
            Map<String, Object> res = (Map<String, Object>) wrapper.get();
            String etag = (String) res.get("etag");
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofDays(1))).eTag(etag)
                    .body(res.get("data"));
        } else {
            List<SearchResponseDto> results = new ArrayList<>();
            ResponseEntity<Map> response = restTemplate.exchange("https://swapi.dev/api/people/?search={param}",
                    HttpMethod.GET,
                    null,
                    Map.class, param);
            try {
                if (response.getStatusCode().is2xxSuccessful()) {

                    Object object = response.getBody().get("results");
                    if (object instanceof List) {
                        List<Map> data = (List<Map>) object;

                        for (int i = 0; i < data.size(); i++) {
                            Map<String, Object> d = data.get(i);
                            SearchResponseDto res = new SearchResponseDto();
                            res.setName(d.get("name").toString());
                            res.setGender(d.get("gender").toString());

                            ResponseEntity<Map> resPlanet = restTemplate.getForEntity(d.get("homeworld").toString(),
                                    Map.class);
                            if (resPlanet.getStatusCode().is2xxSuccessful()) {
                                Map<String, Object> body = resPlanet.getBody();
                                PlanetDto dto = new PlanetDto();
                                dto.setName(body.get("name").toString());
                                dto.setPopulation(body.get("population").toString());
                                res.setPlanet(dto);
                            }

                            List<String> starships = (List<String>) d.get("starships");
                            List<StartshipDto> startshipDtos = new ArrayList<>();
                            if (starships != null) {
                                if (!starships.isEmpty()) {
                                    for (String starship : starships) {
                                        ResponseEntity<Map> resStarship = restTemplate.getForEntity(
                                                starship,
                                                Map.class);
                                        if (resStarship.getStatusCode().is2xxSuccessful()) {
                                            Map<String, Object> body = resStarship.getBody();
                                            StartshipDto dto = new StartshipDto();
                                            dto.setName(body.get("name").toString());
                                            dto.setModel(body.get("model").toString());
                                            startshipDtos.add(dto);
                                        }
                                    }
                                }
                            }
                            res.setStartships(startshipDtos);
                            results.add(res);
                        }
                    }

                    // Default
                    Map<String, Object> res = new HashMap<>();
                    res.put("results", results);
                    res.put("etag", response.getHeaders().getETag());
                    restCache.put(param, res);
                    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofDays(1)))
                            .headers(response.getHeaders()).body(results);

                } else {
                    return response;
                }
            } catch (Exception e) {
                e.printStackTrace();
                HashMap<String, Object> error = new HashMap<>();
                error.put("error", true);
                error.put("message", e.getLocalizedMessage());
                return ResponseEntity.internalServerError().body(error);
            }

        }

    }

}
