package io.choerodon.app.service;

import javax.servlet.http.HttpServletResponse;

public interface ExcelService {

    void exportInvitationLink(HttpServletResponse response);

    void exportPersonReport(HttpServletResponse response);

    void exportComprehensiveReport(HttpServletResponse response);
}
