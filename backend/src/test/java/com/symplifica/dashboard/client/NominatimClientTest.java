package com.symplifica.dashboard.client;

import com.symplifica.dashboard.dto.UbicacionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NominatimClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NominatimClient nominatimClient;

    private static final String XML_RESPUESTA_VALIDA = """
        <?xml version="1.0" encoding="UTF-8" ?>
        <searchresults>
          <place place_id="123" lat="3.4519988" lon="-76.5325259"
                 display_name="Cali ciudad, Cali, Sur, Valle del Cauca, Colombia" />
        </searchresults>
        """;

    private static final String XML_SIN_RESULTADOS = """
        <?xml version="1.0" encoding="UTF-8" ?>
        <searchresults></searchresults>
        """;

    @Test
    void deberiaExtraerCoordenadasDeUnaRespuestaValida() {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(XML_RESPUESTA_VALIDA));

        UbicacionDTO resultado = nominatimClient.buscarUbicacionPorCiudad("Cali");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getLatitud()).isEqualTo("3.4519988");
        assertThat(resultado.getLongitud()).isEqualTo("-76.5325259");
        assertThat(resultado.getNombreCompleto()).contains("Cali");
    }

    @Test
    void deberiaRetornarNullCuandoNoHayResultados() {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(XML_SIN_RESULTADOS));

        UbicacionDTO resultado = nominatimClient.buscarUbicacionPorCiudad("CiudadInexistente");

        assertThat(resultado).isNull();
    }

    @Test
    void deberiaRetornarNullCuandoLaLlamadaFalla() {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("timeout"));

        UbicacionDTO resultado = nominatimClient.buscarUbicacionPorCiudad("Cali");

        assertThat(resultado).isNull();
    }
}