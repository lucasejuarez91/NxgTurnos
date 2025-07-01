package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Menu;
import com.ar.nxg.nxgappts.domain.MenuItem;
import com.ar.nxg.nxgappts.domain.Role;
import com.ar.nxg.nxgappts.dto.MenuDTO;
import com.ar.nxg.nxgappts.dto.MenuItemDTO;
import com.ar.nxg.nxgappts.repositories.MenuItemRepository;
import com.ar.nxg.nxgappts.repositories.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService extends BaseService<MenuItem> {

    @Autowired
    private MenuItemRepository menuItemRepository;


    public MenuItem findByUrl(String replace) {
        return menuItemRepository.findByUrl(replace);
    }
}
