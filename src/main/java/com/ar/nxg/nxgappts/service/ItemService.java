package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Item;
import com.ar.nxg.nxgappts.domain.User;
import com.ar.nxg.nxgappts.repositories.ItemRepository;
import com.ar.nxg.nxgappts.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService extends BaseService<Item> {

    @Autowired
    private ItemRepository itemRepository;

    public Item findById(long item) {
        return itemRepository.findById(item).orElseThrow();
    }

    public List<Item> findItemsByCompany(Company company) {
        return itemRepository.findByCompany(company);
    }

    public Item findByNameAndCompany(String reservation, Company company) {
        return itemRepository.findByNameAndCompany(reservation, company);
    }
}

