package com.symplifica.dashboard.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class NominatimPlaceDTO {

    @JacksonXmlProperty(isAttribute = true, localName = "lat")
    private String lat;

    @JacksonXmlProperty(isAttribute = true, localName = "lon")
    private String lon;

    @JacksonXmlProperty(isAttribute = true, localName = "display_name")
    private String displayName;
}