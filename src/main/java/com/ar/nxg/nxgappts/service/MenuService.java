package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Menu;
import com.ar.nxg.nxgappts.domain.MenuItem;
import com.ar.nxg.nxgappts.domain.Role;
import com.ar.nxg.nxgappts.dto.MenuDTO;
import com.ar.nxg.nxgappts.dto.MenuItemDTO;
import com.ar.nxg.nxgappts.repositories.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public String getMenuForUser(List<Role> roles) {
        // Obtener todas las categorías de menú
        List<Menu> allMenuCategories = menuRepository.findAll();
        allMenuCategories.sort(Comparator.comparingInt(Menu::getOrdering));

        // Crear una lista para almacenar las categorías con los ítems filtrados
        List<MenuDTO> menuCategories = new ArrayList<>();

        // Recorrer las categorías de menú
        for (Menu category : allMenuCategories) {
            List<MenuItem> items = category.getItems();
            // Filtrar los elementos de menú que el usuario puede acceder, según sus roles
            List<MenuItemDTO> filteredMenuItems = items.stream()
                    .sorted(Comparator.comparingInt(MenuItem::getOrdering))
                    .filter(item -> item.getRoles().stream().anyMatch(roles::contains))
                    .map(item -> new MenuItemDTO(item.getTitle(), item.getUrl(), item.getIcon())) // Convertir a DTO
                    .collect(Collectors.toList());

            // Solo agregar la categoría si tiene ítems filtrados
            if (!filteredMenuItems.isEmpty()) {
                MenuDTO categoryDTO = new MenuDTO();
                categoryDTO.setName(category.getName());
                categoryDTO.setItems(filteredMenuItems);
                menuCategories.add(categoryDTO);
            }
        }
        return generateMenuHtml(menuCategories);
    }

    private String generateMenuHtml(List<MenuDTO> menuCategories) {
        StringBuilder htmlBuilder = new StringBuilder();


        for (MenuDTO menu : menuCategories) {
            if(menu.getItems().isEmpty()){
                /*
                 * <li class="nav-item">
                 *         <a class="nav-link " href="index.html">
                 *           <i class="bi bi-grid"></i>
                 *           <span>Dashboard</span>
                 *         </a>
                 *       </li>
                 */
                htmlBuilder.append("<li class='nav-item'>")
                        .append("<a class='nav-link ' href='#'>")
                        .append(String.format("<i class='%s'></i>", menu.getIcon()))
                        .append((String.format("<span>%s</span>",menu.getName())))
                        .append("</a>")
                        .append("</li>");
            } else {
                /*
                <li class="nav-item">
                    <a class="nav-link collapsed" data-bs-target="#components-nav" data-bs-toggle="collapse" href="#">
                      <i class="bi bi-menu-button-wide"></i>
                      <span>Components</span>
                      <i class="bi bi-chevron-down ms-auto"></i>
                    </a>
                 */
                htmlBuilder.append("<li class='nav-item'>")
                        .append(String.format("<a class='nav-link collapsed' data-bs-target='#%s-nav' data-bs-toggle='collapse' href='#' ",menu.getName().toLowerCase()))
                        .append(String.format("<i class='%s'></i>", menu.getIcon()))
                        .append((String.format("<span>%s</span>",menu.getName())))
                        .append("<i class='bi bi-chevron-down ms-auto'></i>");

                for (MenuItemDTO item : menu.getItems()) {
                    /*
                        <ul id="components-nav" class="nav-content collapse " data-bs-parent="#sidebar-nav">
                          <li>
                            <a href="components-alerts.html">
                              <i class="bi bi-circle"></i>
                              <span>Alerts</span>
                            </a>
                          </li>

                        </ul>
                      </li>
                     */
                    htmlBuilder.append(String.format("<ul id='%s-nav' class='nav-content collapse' data-bs-parent='#sidebar-nav'>",menu.getName().toLowerCase()))
                            .append("<a class='nav-link collapsed' data-bs-target='#components-nav' data-bs-toggle='collapse' href='#'")
                            .append("<li>")
                            .append(String.format("<a href='%s'>",item.getUrl()))
                            .append(String.format("<i class='%s'></i><span>%s</span>", item.getIcon(), item.getTitle()))
                            .append("</a>")
                            .append("</li>")
                            .append(item.getTitle())
                            .append("</span>")
                            .append("</a>")
                            .append("</li>");
                    htmlBuilder.append("</ul></li>");
                }

            }
        }
        return htmlBuilder.toString();
    }

}
