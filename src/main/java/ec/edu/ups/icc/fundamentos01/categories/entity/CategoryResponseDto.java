package ec.edu.ups.icc.fundamentos01.categories.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "descripcion", "id", "nombre" })
public class CategoryResponseDto {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("nombre")
    public String name;

    @JsonProperty("descripcion")
    public String description;
}
