package io.choerodon.api.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.Data;

import java.util.List;


@Data
public class InvitationExcelEntity {

    @Excel(name = "邀请人", needMerge = true)
    private String name;

    @Excel(name = "手机号", needMerge = true)
    private String phone;

    @ExcelCollection(name = "被邀请人")
    private List<AnswerBankBaseDTO> answerBankBaseDTOS;

}

