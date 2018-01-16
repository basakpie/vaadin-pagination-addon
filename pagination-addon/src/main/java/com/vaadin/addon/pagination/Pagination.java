package com.vaadin.addon.pagination;

import com.vaadin.data.HasValue;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by basakpie on 2017-05-18.
 */
@SuppressWarnings("unused")
public class Pagination extends HorizontalLayout {

    private static final long serialVersionUID = 1L;

    final List<PaginationChangeListener> listeners = new ArrayList<>();

    private PaginationResource paginationResource;

    HorizontalLayout itemsPerPage;
    HorizontalLayout pageControls;

    final ComboBox<Integer> itemsPerPageSelect = new ComboBox();

    final TextField currentPageTextField = new TextField(null, "1");
    final Label totalPageLabel = new Label();

    final Button firstButton    = new Button(VaadinIcons.ARROW_CIRCLE_LEFT);
    final Button previousButton = new Button(VaadinIcons.ARROW_CIRCLE_LEFT_O);
    final Button nextButton     = new Button(VaadinIcons.ARROW_CIRCLE_RIGHT_O);
    final Button lastButton     = new Button(VaadinIcons.ARROW_CIRCLE_RIGHT);

    public Pagination() {
    }

    public Pagination(PaginationResource paginationResource) {
        setWidth("100%");
        setSpacing(true);
        init(paginationResource);
    }

    protected void init(PaginationResource resource) {
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
        List<Integer> items = new ArrayList<>();
        for(int page : perPage) {
            items.add(page);
        }
        itemsPerPageSelect.setItems(items);
        itemsPerPageSelect.setSelectedItem(this.paginationResource.limit());
        if(!itemsPerPageSelect.isSelected(this.paginationResource.limit())) {
            throw new IllegalArgumentException("itemsPerPageSelect.isSelected(paginationResource.size()) not found!");
        }
    }
    
    public void setTotalCount(long total) {
    	paginationResource.setTotal(total);
    	totalPageLabel.setValue(String.valueOf(paginationResource.totalPage()));
        buttonsEnabled();
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
        itemsPerPageSelect.setEmptySelectionAllowed(false);
        itemsPerPageSelect.setWidth("80px");
        itemsPerPageSelect.setStyleName(ValoTheme.COMBOBOX_SMALL);
        itemsPerPageSelect.addValueChangeListener((HasValue.ValueChangeListener) event -> {
            int pageSize = (Integer)event.getValue();
            if(pageSize== paginationResource.limit()) return;
            paginationResource.setLimit((Integer)event.getValue());
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

        firstButton.addClickListener(e -> {
            PaginationResource first = paginationResource.first();
            buttonClickEvent(first);
        });

        previousButton.addClickListener(e -> {
            PaginationResource previous = paginationResource.previous();
            buttonClickEvent(previous);
        });

        nextButton.addClickListener(e -> {
            PaginationResource next = paginationResource.next();
            buttonClickEvent(next);
        });

        lastButton.addClickListener(e -> {
            PaginationResource last = paginationResource.last();
            buttonClickEvent(last);
        });

        HorizontalLayout pageFields = createPageFields();

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponents(firstButton, previousButton,  pageFields, nextButton, lastButton);
        return layout;
    }

    @SuppressWarnings("serial")
	private HorizontalLayout createPageFields() {
        currentPageTextField.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        currentPageTextField.setValueChangeMode(ValueChangeMode.BLUR);

        currentPageTextField.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                currentPageChangedEvent();
            }
        });

        currentPageTextField.addValueChangeListener((HasValue.ValueChangeListener) event -> {
            currentPageChangedEvent();
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

    protected void currentPageChangedEvent() {
        try {
            Integer.parseInt(currentPageTextField.getValue());
            int currentPage = Integer.valueOf(currentPageTextField.getValue());
            int pageNumber = paginationResource.page();
            if (currentPage==pageNumber) return;
            paginationResource.setPage(currentPage);
            firePagedChangedEvent();
        } catch (Exception e) {
            return;
        }
    }

    protected void buttonClickEvent(PaginationResource change) {
        paginationResource.setTotal(change.total());
        paginationResource.setPage(change.page());
        paginationResource.setLimit(change.limit());
        paginationResource.setInitIndex(change.initIndex());
        firePagedChangedEvent();
    }

    protected void firePagedChangedEvent() {
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

    protected void buttonsEnabled() {
        firstButton.setEnabled(!this.paginationResource.isFirst());
        previousButton.setEnabled(this.paginationResource.hasPrevious());
        nextButton.setEnabled(this.paginationResource.hasNext());
        lastButton.setEnabled(!this.paginationResource.isLast());
    }

    protected Button getFirstButton() {
        return this.firstButton;
    }

    protected Button getPreButton() {
        return this.previousButton;
    }

    protected Button getNextButton() {
        return this.nextButton;
    }

    protected Button getLastButton() {
        return this.lastButton;
    }

    protected PaginationResource getPaginationResource() {
        return this.paginationResource;
    }

}
