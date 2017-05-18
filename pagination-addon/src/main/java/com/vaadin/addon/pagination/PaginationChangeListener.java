package com.vaadin.addon.pagination;

import java.io.Serializable;

/**
 * Created by basakpie on 2017-05-18.
 */
public interface PaginationChangeListener extends Serializable {
    void changed(PaginationResource event);
}
