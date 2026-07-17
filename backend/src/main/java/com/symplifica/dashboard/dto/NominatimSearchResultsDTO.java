package com.symplifica.dashboard.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "searchresults")
public class NominatimSearchResultsDTO {

    @JacksonXmlProperty(localName = "place")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NominatimPlaceDTO> places;
}