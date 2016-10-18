package com.vaadin.addon.pagination.demo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.vaadin.addon.pagination.Pagination;
import com.vaadin.addon.pagination.PaginationChangeListener;
import com.vaadin.addon.pagination.PaginationResource;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("demo")
@Title("Pagination Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	private static final Logger log = LoggerFactory.getLogger(DemoUI.class);

    public static List<User> userList = new ArrayList<>();

    @Autowired
    UserRepository userRepository;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addTab(nativeSublist(), "native_sublist");
        tabSheet.addTab(jpaPagable(), "jpa_pagable");
        tabSheet.addTab(tableFindAll(), "table_findAll");
        setContent(tabSheet);
    }

    public VerticalLayout nativeSublist() {

        final int page = 1;
        final int limit = 20;

        final List<User> users = userList.subList(0, limit);
        final long total = Long.valueOf(userList.size());

        final Table table = createTable(users);
        final Pagination pagination = createPagination(total, page, limit);
        pagination.addPageChangeListener(new PaginationChangeListener() {
            @Override
            public void changed(PaginationResource event) {
                log.debug("nativeSublist: {}", event.toString());
                table.removeAllItems();
                for(User user : userList.subList(event.fromIndex(), event.toIndex())) {
                    table.addItem(user);
                }
            }
        });

        final VerticalLayout layout = createContent(table, pagination);
        return layout;
    }

    public VerticalLayout jpaPagable() {

        final int page = 1;
        final int limit = 10;

        final Page<User> users = findAll(0, limit);
        final long total = users.getTotalElements();

        final Table table = createTable(users.getContent());
        final Pagination pagination = createPagination(total, page, limit);
        pagination.addPageChangeListener(new PaginationChangeListener() {
            @Override
            public void changed(PaginationResource event) {
                log.debug("jpaPagable : {}", event.toString());
                Page<User> users = findAll(event.pageIndex(), event.limit());
                pagination.setTotalCount(users.getTotalElements());                
                table.removeAllItems();                
                for(User user : users) {
                    table.addItem(user);
                }
            }
        });

        final VerticalLayout layout = createContent(table, pagination);
        return layout;
    }

    public Component tableFindAll() {

        final int page = 1;
        final int limit = 10;

        final List<User> users = userRepository.findAll();
        final long total = users.size();

        final Table table = createTable(users);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setHeightUndefined();
        table.setPageLength(limit);

        final Pagination pagination = createPagination(total, page, limit);
        pagination.setItemsPerPageVisible(false);
        pagination.addPageChangeListener(new PaginationChangeListener() {
            @Override
            public void changed(PaginationResource event) {
                log.debug("tableFindAll : {}", event.toString());
                table.setPageLength(event.limit());
                table.setCurrentPageFirstItemIndex(event.offset());
            }
        });
        final VerticalLayout layout = createContent(table, pagination);
        return layout;
    }

    public Page<User> findAll(int page, int size) {
        Pageable pageable = new PageRequest(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return users;
    }

    private Table createTable(List<User> users) {
        final Table table = new Table();
        table.setSizeFull();
        table.setContainerDataSource(new BeanItemContainer<>(User.class, users));
        table.setVisibleColumns("id", "name","email");
        table.setColumnHeaders("ID", "Name","Email");
        return table;
    }

    private Pagination createPagination(long total, int page, int limit) {
        final PaginationResource paginationResource = PaginationResource.newBuilder().setTotal(total).setPage(page).setLimit(limit).build();
        final Pagination pagination = new Pagination(paginationResource);
        pagination.setItemsPerPage(10, 20, 50, 100);
        return pagination;
    }

    private VerticalLayout createContent(Table table, Pagination pagination) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setSizeFull();
        layout.addComponents(table, pagination);
        layout.setExpandRatio(table, 1f);
        return layout;
    }
}
