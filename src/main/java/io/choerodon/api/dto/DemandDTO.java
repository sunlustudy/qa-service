package io.choerodon.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DemandDTO {

    private Integer id;

    private String companyName;

    private String post;

    private String postDescription;

    private String hrName;

    private String contact;

    private Date createTime;

    private Date updateTime;

    private Boolean isDel;

    private Integer version;

}