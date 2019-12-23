package io.choerodon.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "推送简历列表")
public class ReportListDTO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    @ApiModelProperty(value = "候选人姓名")
    private String name;

    @ApiModelProperty(value = "工作年限")
    private String workYears;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "应聘职位")
    private String expectPost;

    @ApiModelProperty(value = "投递时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date deliveryTime;

    @ApiModelProperty(value = "岗位契合度")
    private String fitDegree;

    @ApiModelProperty(value = "需求id")
    private Integer demandId;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "状态 1、转到待沟通，2、待面试，3、录用，4、不合适")
    private Integer status;

    @ApiModelProperty(value = "开始时间（筛选）")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date startTime;

    @ApiModelProperty(value = "结束时间（筛选）")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date endTime;

    @ApiModelProperty(value = "简历已推荐次数")
    private Integer recommendCount;


}