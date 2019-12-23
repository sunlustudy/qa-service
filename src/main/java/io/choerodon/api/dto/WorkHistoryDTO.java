package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.google.common.collect.ImmutableMap;
import io.choerodon.domain.WorkHistory;
import io.choerodon.domain.WorkHistoryExtend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkHistoryDTO {

    @ExcelCollection(name = "工作经历主表")
    List<WorkHistory> workHistories;

    @ExcelCollection(name = "工作经历拓展")
    List<WorkHistoryExtend> workHistoryExtends;
    
}