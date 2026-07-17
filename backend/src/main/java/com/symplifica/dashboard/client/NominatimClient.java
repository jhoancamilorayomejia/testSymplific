package com.symplifica.dashboard.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.symplifica.dashboard.dto.NominatimPlaceDTO;
import com.symplifica.dashboard.dto.NominatimSearchResultsDTO;
import com.symplifica.dashboard.dto.UbicacionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@Slf4j
public class NominatimClient {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper = new XmlMapper();

    public NominatimClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public UbicacionDTO buscarUbicacionPorCiudad(String ciudad) {
        try {
            String url = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
        .queryParam("city", ciudad)
        .queryParam("format", "xml")
        .queryParam("limit", 1)
        .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "symplifica-dashboard-test/1.0");

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            NominatimSearchResultsDTO resultado = xmlMapper.readValue(
                    response.getBody(), NominatimSearchResultsDTO.class);

            List<NominatimPlaceDTO> places = resultado.getPlaces();
            if (places == null || places.isEmpty()) {
                log.warn("Nominatim no encontró resultados para la ciudad: {}", ciudad);
                return null;
            }

            NominatimPlaceDTO place = places.get(0);
            return new UbicacionDTO(place.getLat(), place.getLon(), place.getDisplayName());

        } catch (Exception e) {
            log.error("Error consultando Nominatim para ciudad {}: {}", ciudad, e.getMessage());
            return null;
        }
    }
}