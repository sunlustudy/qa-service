package io.choerodon.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkHistoryExtend {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    @Excel(name = "年份", width = 15, needMerge = true)
    private String year;

    @Excel(name = "销售指标", width = 15, needMerge = true)
    private BigDecimal salesIndicators;

    @Excel(name = "年度完成金额", width = 15, needMerge = true)
    private BigDecimal completion;

    @Excel(name = "负责行业", width = 15, needMerge = true)
    private String industry;

    @Excel(name = "负责产品和服务", width = 15, needMerge = true)
    private String productsServices;
}