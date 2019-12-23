package io.choerodon.infra.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ExcelUtils {


    public static void workbookWrite(Workbook workbook, String fileName, HttpServletResponse response) {
        try {
            response.reset();
            response.setContentType("application/ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName + ".xls");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("generate excel file failure {}", e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.warn("workbook close error fileName {}", fileName);
            }
        }
    }
}

