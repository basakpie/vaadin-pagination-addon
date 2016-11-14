package com.vaadin.addon.pagination;

import java.io.Serializable;

/**
 * Created by basakpie on 2016-10-13.
 */
public class PaginationResource implements Serializable {

    private static final long serialVersionUID = 1L;

    private long total;
    private int page;
    private int limit;
    private int initIndex;

    public long total() {
        return this.total;
    }

    public int limit() {
        return this.limit;
    }

    public int page() {
        return this.page == 0 ? 1 : this.page;
    }

    public int initIndex() {
        return this.initIndex;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setInitIndex(int type) {
        this.initIndex = type;
    }

    public int offset() {
        return fromIndex();
    }

    public int pageIndex() {
        if(initIndex==0) {
            return page - 1;
        }
        return page;
    }

    public int fromIndex() {
        int fromIndex = (page - 1) * limit;
        if(fromIndex < 0) {
            return 0;
        }
        if(initIndex==0) {
            return fromIndex;
        }
        return fromIndex + initIndex;
    }

    public int toIndex() {
        int toIndex = page * limit;
        int tatalIndex = (int) total;
        if(toIndex > tatalIndex) {
            toIndex = tatalIndex;
        }
        if(initIndex==0) {
            return toIndex;
        }
        return toIndex + initIndex;
    }

    public int totalPage() {
        int totalPage = (limit == 0) ? 1 : (int) Math.ceil((double) total / (double) limit);
        return totalPage == 0 ? 1 : totalPage;
    }

    public PaginationResource first() {
        return PaginationResource.newBuilder().setTotal(total).setPage(1).setLimit(limit).setInitIndex(initIndex).build();
    }

    public PaginationResource previous() {
        if(!hasPrevious()) {
            return this;
        }

        return PaginationResource.newBuilder().setTotal(total).setPage(page - 1).setLimit(limit).setInitIndex(initIndex).build();
    }

    public PaginationResource next() {
        if(!hasNext()) {
            return this;
        }
        return PaginationResource.newBuilder().setTotal(total).setPage(page + 1).setLimit(limit).setInitIndex(initIndex).build();
    }

    public PaginationResource last() {
        int lastPageNumber = totalPage() > 0 ? totalPage() : 1;
        return PaginationResource.newBuilder().setTotal(total).setPage(lastPageNumber).setLimit(limit).setInitIndex(initIndex).build();
    }

    public boolean isFirst() {
        return !hasPrevious();
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public boolean hasNext() {
        return page < totalPage();
    }

    public boolean isLast() {
        return !hasNext();
    }

    public static Builder newBuilder() {
        return Builder.create();
    }

    private PaginationResource(Builder builder) {
        this.total = builder.total;
        this.page = builder.page;
        this.limit = builder.limit;
        this.initIndex = builder.initIndex;
    }

    public static final class Builder {

        private long total;
        private int page;
        private int limit;
        private int initIndex;

        private Builder(){
        }

        private static Builder create() {
            return new Builder();
        }

        public Builder setTotal(long total) {
            this.total = total;
            return this;
        }

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder setInitIndex(int initIndex) {
            this.initIndex = initIndex;
            return this;
        }

        public PaginationResource build() {
            if (page < 0) {
                throw new IllegalArgumentException("Page index must not be less than zero!");
            }
            if (limit < 1) {
                throw new IllegalArgumentException("Page size must not be less than one!");
            }
            return new PaginationResource(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaginationResource that = (PaginationResource) o;

        if (total != that.total) return false;
        if (page != that.page) return false;
        if (limit != that.limit) return false;
        if (initIndex != that.initIndex) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (total ^ (total >>> 32));
        result = 31 * result + page;
        result = 31 * result + limit;
        result = 31 * result + initIndex;
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Page Resources : { ");
        sb.append("currentPage: " + page + " / " + totalPage());
        sb.append(", limit: " + limit);
        sb.append(", offset: " + offset());
        sb.append(", pageIndex: " + pageIndex());
        sb.append(", fromIndex: " + fromIndex());
        sb.append(", toIndex: " + toIndex());
        sb.append(", isFirst: " + isFirst());
        sb.append(", hasPrevious: " + hasPrevious());
        sb.append(", hasNext: " + hasNext());
        sb.append(", isLast: " + isLast());
        sb.append(", totalCount: " + total);
        sb.append(", initIndex: " + initIndex);
        sb.append(" }");
        return sb.toString();
    }
}
