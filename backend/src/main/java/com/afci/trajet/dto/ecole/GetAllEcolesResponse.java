package com.afci.trajet.dto.ecole;

import java.util.List;

/**
 * DTO utilisé pour renvoyer une liste paginée d’écoles.
 *
 * Ce format est idéal pour un frontend React ou Vue :
 *  - items      : la liste des écoles affichées
 *  - totalItems : nombre total en base
 *  - totalPages : nombre de pages
 *  - page       : page actuelle
 *  - pageSize   : taille demandée (ex: 10, 20, 50)
 */
public class GetAllEcolesResponse {

    private List<EcoleResponse> items;
    private long totalItems;
    private int totalPages;
    private int page;
    private int pageSize;

    public GetAllEcolesResponse(List<EcoleResponse> items,
                                long totalItems,
                                int totalPages,
                                int page,
                                int pageSize) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<EcoleResponse> getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }
}
