package com.vaadin.addon.pagination;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by gmind on 2016-10-31.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPagination extends Pagination {

    private static final Logger log = LoggerFactory.getLogger(TestPagination.class);

    @Autowired
    TestBeanRepository testBeanRepository;

    public TestPagination() {
    }

    @Before
    public void setup() throws Exception {
        loadingDate();
    }

    private void loadingDate() {

    }

    public List<TestBean> beans() {
        return TestApplication.beanList;
    }

    @Test
    public void testPagination() {

        int totalCount = beans().size();
        int currentPage = 6;
        int limit = 20;

        PaginationResource paginationResource = PaginationResource.newBuilder().setTotal(totalCount).setPage(currentPage).setLimit(limit).build();
        init(paginationResource);
        setItemsPerPage(10, 20, 50, 100);

        log.debug(getPaginationResource().toString());

        assertTrue("currentPage", getPaginationResource().page()==6);
        assertTrue("totalPage", getPaginationResource().totalPage()==12);
        assertTrue("limit", getPaginationResource().limit()==20);
        assertTrue("totalcount", getPaginationResource().total()==240);

        // Next Button Click
        buttonClickEvent(getPaginationResource().next());
        paginationAssert("next", 7, 120, 140);

        // Previous Button Click
        buttonClickEvent(getPaginationResource().previous());
        paginationAssert("previous", 6, 100, 120);

        // Last Button Click
        buttonClickEvent(getPaginationResource().last());
        paginationAssert("last", 12, 220, 240);

        // First Button Click
        buttonClickEvent(getPaginationResource().first());
        paginationAssert("first", 1, 0, 20);

        // CurrentPage SetValue
        setCurrentPage(10);
        currentPageTextField.removeAllValidators();
        currentPageChangedEvent();
        paginationAssert("currentPage", 10, 180, 200);

    }

    public void paginationAssert(String action, int currentPage, int fromIndex, int toIndex) {

        buttonsEnabled();

        log.debug("{} : {}", action, getPaginationResource().toString());

        assertTrue(action + " : currentPage", getPaginationResource().page()==currentPage);
        assertTrue(action + " : offset", getPaginationResource().offset()==fromIndex);
        assertTrue(action + " : fromIndex", getPaginationResource().fromIndex()==fromIndex);
        assertTrue(action + " : toIndex", getPaginationResource().toIndex()==toIndex);

        assertTrue(action + " : isFirst", getPaginationResource().isFirst()!=getFirstButton().isEnabled());
        assertTrue(action + " : hasPrevious", getPaginationResource().hasPrevious()==getPreButton().isEnabled());
        assertTrue(action + " : hasNext", getPaginationResource().hasNext()==getLastButton().isEnabled());
        assertTrue(action + " : isLast", getPaginationResource().isLast()!=getLastButton().isEnabled());
    }

}
