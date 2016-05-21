package com.mymapper;

public class Page {
    private Integer pageSize = 10;
    private Integer currentPage = 1;
    private Integer totalCount;
    private Integer numberOfPage;

    public Page() {
    }

    public Page(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Page(Integer pageSize, Integer currentPage) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getNumberOfPage() {
        return numberOfPage;
    }

    public void setNumberOfPage(Integer numberOfPage) {
        this.numberOfPage = numberOfPage;
    }

    @Override
    public String toString() {
        return "Page{" +
            "pageSize=" + pageSize +
            ", currentPage=" + currentPage +
            ", totalCount=" + totalCount +
            ", numberOfPage=" + numberOfPage +
            '}';
    }
}