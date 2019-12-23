package io.choerodon.app.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import io.choerodon.api.dto.AnswerBankBaseDTO;
import io.choerodon.api.dto.ComprehensiveReportDTO;
import io.choerodon.api.dto.PersonalReportDTO;
import io.choerodon.app.service.BaseInfoService;
import io.choerodon.app.service.ComprehensiveReportService;
import io.choerodon.app.service.ExcelService;
import io.choerodon.app.service.PersonalReportService;
import io.choerodon.infra.mapper.AnswerBankBaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.choerodon.infra.utils.ExcelUtils.workbookWrite;

@Service("excelService")
@Slf4j
public class ExcelServiceImpl implements ExcelService {
    @Autowired
    private AnswerBankBaseMapper answerBankBaseMapper;

    @Autowired
    private PersonalReportService personalReportService;

    @Autowired
    private BaseInfoService baseInfoService;

    @Autowired
    private ComprehensiveReportService comprehensiveReportService;


    /**
     * 导出邀请链的 excel 文件
     *
     * @param response
     */
    @Override
    public void exportInvitationLink(HttpServletResponse response) {

//        创建时间的倒叙
        List<AnswerBankBaseDTO> resources = answerBankBaseMapper.selectHasInviter();
//        计算完成度
        resources.stream().parallel().forEach(p ->
                p.setCompletePer(baseInfoService.getCompletePer(p.getId())));


        List<LinkedList<AnswerBankBaseDTO>> result = convertToLink(resources);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet();
        int maxLength = maxLength(result);

//        创建头部
        HSSFRow titlerRow1 = sheet.createRow(0);
        HSSFRow titlerRow2 = sheet.createRow(1);
        for (int i = 0; i < maxLength; i++) {
            int offset = i * 4;
            titlerRow1.createCell(offset + 0).setCellValue((i + 1) + "级");
            titlerRow2.createCell(offset + 0).setCellValue("姓名");
            titlerRow2.createCell(offset + 1).setCellValue("电话");
            titlerRow2.createCell(offset + 2).setCellValue("昵称");
            titlerRow2.createCell(offset + 3).setCellValue("完成度");
        }


//        创建数据部分
        for (LinkedList<AnswerBankBaseDTO> row : result) {

            int lastRowNum = sheet.getLastRowNum();
            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);

            row.forEach(bankBase -> {
                int lastCellNum = dataRow.getLastCellNum();
                if (lastCellNum != -1) {  // 若不是第一行则调整位置
                    lastCellNum--;
                }
                dataRow.createCell(lastCellNum + 1).setCellValue(bankBase.getName());
                dataRow.createCell(lastCellNum + 2).setCellValue(bankBase.getPhone());
                dataRow.createCell(lastCellNum + 3).setCellValue(bankBase.getNickname());
                dataRow.createCell(lastCellNum + 4).setCellValue(bankBase.getCompletePer());
            });

        }

//        写入
        workbookWrite(hssfWorkbook, "invitation", response);
    }

    private static int maxLength(List<LinkedList<AnswerBankBaseDTO>> lists) {
        AtomicInteger item = new AtomicInteger(lists.get(0).size());
        lists.forEach(p -> {
            if (p.size() > item.get()) {
                item.set(p.size());
            }
        });
        return item.get();
    }

    private static List<LinkedList<AnswerBankBaseDTO>> convertToLink(List<AnswerBankBaseDTO> resources) {

        List<LinkedList<AnswerBankBaseDTO>> result = new LinkedList<>();
        for (AnswerBankBaseDTO resource : resources) {
            if (ObjectUtils.isEmpty(result)) {
                LinkedList<AnswerBankBaseDTO> linkedList = new LinkedList<>();
                linkedList.add(resources.get(0));
                result.add(linkedList);
            } else {
                boolean hasInviter = false;
                for (LinkedList<AnswerBankBaseDTO> item : result) {
                    if (resource.getId().equals(item.getFirst().getInviterId())) {
                        item.addFirst(resource);
                        hasInviter = true;
                    }
                }

                if (!hasInviter) {  // 若是未找到邀请人则添加新的 list
                    LinkedList<AnswerBankBaseDTO> answerBankBaseDTOS = new LinkedList<>();
                    answerBankBaseDTOS.addFirst(resource);
                    result.add(answerBankBaseDTOS);
                }
            }

        }
        return result;
    }


    /**
     * 导出个人报告
     *
     * @param response
     */
    @Override
    public void exportPersonReport(HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("personal-report", "personal-report"),
                PersonalReportDTO.class, personalReportService.listPersonalReport());
        workbookWrite(workbook, "personal_report", response);
    }


    /**
     * 导出综合报告
     *
     * @param response
     */
    @Override
    public void exportComprehensiveReport(HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("comprehensive-report", "comprehensive-report"),
                ComprehensiveReportDTO.class, comprehensiveReportService.listComprehensiveReportDTO());
        workbookWrite(workbook, "comprehensive_report", response);
    }

}



