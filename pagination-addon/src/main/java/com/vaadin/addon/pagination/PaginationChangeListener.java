package com.vaadin.addon.pagination;

import java.io.Serializable;

/**
 * Created by basakpie on 2016-10-13.
 */
public interface PaginationChangeListener extends Serializable {
    public void changed(PaginationResource event);
}
