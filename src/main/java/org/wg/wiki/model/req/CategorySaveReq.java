package org.wg.wiki.model.req;

import javax.validation.constraints.NotNull;

public class CategorySaveReq {
    private Long id;

    @NotNull(message = "【名称】不能为空")
    private String name;

    private Long parentId;

    @NotNull(message = "【排序】不能为空")
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "CategorySaveReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", sort=" + sort +
                '}';
    }
}
