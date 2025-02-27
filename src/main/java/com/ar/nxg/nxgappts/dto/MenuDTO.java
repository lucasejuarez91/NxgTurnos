package com.ar.nxg.nxgappts.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuDTO {

    private String name; // Nombre de la categoría
    private String icon;
    private List<MenuItemDTO> items; // Elementos de menú asociados a esta categoría

}
