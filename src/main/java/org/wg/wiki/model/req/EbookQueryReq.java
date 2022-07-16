package org.wg.wiki.model.req;

/**
 * 继承PageReq，可含有分页请求数据
 */
public class EbookQueryReq extends PageReq {
    private Long id;

    private String name;

    private Long category2Id;
    
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

    public Long getCategory2Id() {
        return category2Id;
    }

    public void setCategory2Id(Long category2Id) {
        this.category2Id = category2Id;
    }

    @Override
    public String toString() {
        return "EbookQueryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category2Id=" + category2Id +
                '}';
    }

}
