package com.vaadin.addon.pagination;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by basakpie on 2016-10-10.
 */
@SuppressWarnings("unused")
public class Pagination extends HorizontalLayout {

    private static final long serialVersionUID = 1L;

    final List<PaginationChangeListener> listeners = new ArrayList<PaginationChangeListener>();

    private PaginationResource paginationResource;

    HorizontalLayout itemsPerPage;
    HorizontalLayout pageControls;

    final ComboBox itemsPerPageSelect = new ComboBox();

    final TextField currentPageTextField = new TextField();
    final Label totalPageLabel = new Label();

    final Button firstButton    = new Button(FontAwesome.ARROW_CIRCLE_LEFT);
    final Button previousButton = new Button(FontAwesome.ARROW_CIRCLE_O_LEFT);
    final Button nextButton     = new Button(FontAwesome.ARROW_CIRCLE_O_RIGHT);
    final Button lastButton     = new Button(FontAwesome.ARROW_CIRCLE_RIGHT);

    public Pagination(PaginationResource paginationResource) {
        setWidth("100%");
        setSpacing(true);
        init(paginationResource);
    }

    private void init(PaginationResource resource) {
        if(getComponentCount()>0) {
            removeAllComponents();
        }
        paginationResource = resource;
        itemsPerPage = createItemsPerPage();
        pageControls = createPageControlFields();
        addComponents(itemsPerPage, pageControls);
        setComponentAlignment(pageControls, Alignment.MIDDLE_RIGHT);
        setExpandRatio(pageControls, 1);
        buttonsEnabled();
    }

    public void setItemsPerPage(int... perPage) {
        if(itemsPerPageSelect.size()>0) {
            itemsPerPageSelect.removeAllItems();
        }
        for(int i=0; i < perPage.length; i++) {
            itemsPerPageSelect.addItem(perPage[i]);
        }
        itemsPerPageSelect.select(this.paginationResource.limit());
        if(!itemsPerPageSelect.isSelected(this.paginationResource.limit())) {
            throw new IllegalArgumentException("itemsPerPageSelect.isSelected(paginationResource.size()) not found!");
        }
    }
    
    public void setTotalCount(long total) {
    	paginationResource.setTotal(total);
    	totalPageLabel.setValue(String.valueOf(paginationResource.totalPage()));
    }

    public void setItemsPerPageEnabled(boolean enabled) {
        itemsPerPage.setEnabled(enabled);
    }

    public void setItemsPerPageVisible(boolean enabled) {
        itemsPerPage.setVisible(enabled);
        setPageControlsAlignment(Alignment.MIDDLE_CENTER);
    }

    public void addPageChangeListener(PaginationChangeListener listener) {
        listeners.add(listener);
    }

    public void removePageChangeListener(PaginationChangeListener listener) {
        listeners.remove(listener);
    }

    public void setCurrentPage(int page) {
        currentPageTextField.setValue(String.valueOf(page));
    }

    public void firstClick() {
        firstButton.click();
    }

    public void previousClick() {
        previousButton.click();
    }

    public void nextClick() {
        nextButton.click();
    }

    public void lastClick() {
        lastButton.click();
    }

	private void setItemsPerPageAlignment(Alignment alignment) {
        setComponentAlignment(itemsPerPage, alignment);
    }

    private void setPageControlsAlignment(Alignment alignment) {
        setComponentAlignment(pageControls, alignment);
    }

    private HorizontalLayout createItemsPerPage() {
        final Label itemsPerPageLabel = new Label("&nbsp;Items per page", ContentMode.HTML);
        itemsPerPageSelect.setTextInputAllowed(false);
        itemsPerPageSelect.setImmediate(true);
        itemsPerPageSelect.setNullSelectionAllowed(false);
        itemsPerPageSelect.setWidth("80px");
        itemsPerPageSelect.setStyleName(ValoTheme.COMBOBOX_SMALL);
        itemsPerPageSelect.addValueChangeListener((Property.ValueChangeListener) event -> {
            int pageSize = (Integer)event.getProperty().getValue();
            if(pageSize== paginationResource.limit()) return;
            paginationResource.setLimit((Integer)event.getProperty().getValue());
            paginationResource.setPage(1);
            firePagedChangedEvent();
        });
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponents(itemsPerPageLabel, itemsPerPageSelect);
        return layout;
    }

    @SuppressWarnings("serial")
	private HorizontalLayout createPageControlFields() {
        firstButton.setStyleName(ValoTheme.BUTTON_LINK);
        previousButton.setStyleName(ValoTheme.BUTTON_LINK);
        nextButton.setStyleName(ValoTheme.BUTTON_LINK);
        lastButton.setStyleName(ValoTheme.BUTTON_LINK);

        firstButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                PaginationResource first = paginationResource.first();
                buttonClickEvent(first);
            }
        });

        previousButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                PaginationResource previous = paginationResource.previous();
                buttonClickEvent(previous);
            }
        });

        nextButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                PaginationResource next = paginationResource.next();
                buttonClickEvent(next);
            }
        });

        lastButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                PaginationResource last = paginationResource.last();
                buttonClickEvent(last);
            }
        });

        HorizontalLayout pageFields = createPageFields();

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponents(firstButton, previousButton,  pageFields, nextButton, lastButton);
        return layout;
    }

    private HorizontalLayout createPageFields() {
        currentPageTextField.setValue(String.valueOf(paginationResource.page()));
        currentPageTextField.setConverter(Integer.class);

        final IntegerRangeValidator validator = new IntegerRangeValidator("Wrong page number", 0, (int) paginationResource.total());
        currentPageTextField.addValidator(validator);

        currentPageTextField.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        currentPageTextField.setImmediate(true);

        currentPageTextField.addValueChangeListener((Property.ValueChangeListener) event -> {
            int currentPage = Integer.valueOf(String.valueOf(currentPageTextField.getValue()));
            int pageNumber = paginationResource.page();
            if (!currentPageTextField.isValid()) return;
            if (currentPageTextField.getValue()==null) return;
            if (currentPage==pageNumber) return;
            paginationResource.setPage(currentPage);
            firePagedChangedEvent();
        });

        currentPageTextField.setWidth("50px");
        currentPageTextField.setStyleName(ValoTheme.TEXTFIELD_SMALL);

        final Label pageLabel = new Label("Page&nbsp;", ContentMode.HTML);
        final Label sepLabel = new Label("&nbsp;/&nbsp;", ContentMode.HTML);
        totalPageLabel.setValue(String.valueOf(this.paginationResource.totalPage()));

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponents(pageLabel, currentPageTextField, sepLabel, totalPageLabel);
        layout.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(currentPageTextField, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(sepLabel, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(totalPageLabel, Alignment.MIDDLE_LEFT);
        return layout;
    }

    private void buttonClickEvent(PaginationResource change) {
        paginationResource.setTotal(change.total());
        paginationResource.setPage(change.page());
        paginationResource.setLimit(change.limit());
        paginationResource.setInitIndex(change.initIndex());
        firePagedChangedEvent();
    }

    private void firePagedChangedEvent() {
        buttonsEnabled();
        currentPageTextField.setValue(String.valueOf(paginationResource.page()));
        totalPageLabel.setValue(String.valueOf(paginationResource.totalPage()));
        if (listeners != null) {
            for(int i =0; i < listeners.size(); i++) {
                PaginationChangeListener listener = listeners.get(i);
                listener.changed(paginationResource);
            }
        }
    }

    private void buttonsEnabled() {
        firstButton.setEnabled(!this.paginationResource.isFirst());
        previousButton.setEnabled(this.paginationResource.hasPrevious());
        nextButton.setEnabled(this.paginationResource.hasNext());
        lastButton.setEnabled(!this.paginationResource.isLast());
    }
}
