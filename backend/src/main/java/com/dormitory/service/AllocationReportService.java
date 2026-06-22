package com.dormitory.service;

import com.dormitory.mapper.AllocationResultMapper;
import com.dormitory.mapper.DormBatchMapper;
import com.dormitory.model.AllocationResult;
import com.dormitory.model.DormBatch;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AllocationReportService {

    private final AllocationResultMapper allocationResultMapper;
    private final DormBatchMapper batchMapper;

    public AllocationReportService(AllocationResultMapper allocationResultMapper, DormBatchMapper batchMapper) {
        this.allocationResultMapper = allocationResultMapper;
        this.batchMapper = batchMapper;
    }

    public List<AllocationResult> getReport(Long batchId) {
        return allocationResultMapper.findByBatchId(batchId);
    }

    public void exportToExcel(Long batchId, HttpServletResponse response) throws IOException {
        DormBatch batch = batchMapper.findById(batchId);
        List<AllocationResult> results = allocationResultMapper.findByBatchId(batchId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("分配明细");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "性别", "专业", "楼栋", "房间号", "床位号", "匹配度", "状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 256 * 16);
            }

            int rowIdx = 1;
            for (AllocationResult ar : results) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(ar.getStudentNo() != null ? ar.getStudentNo() : "");
                row.createCell(1).setCellValue(ar.getStudentName() != null ? ar.getStudentName() : "");
                row.createCell(2).setCellValue("");
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue("");
                row.createCell(5).setCellValue(ar.getRoomNumber() != null ? ar.getRoomNumber() : "");
                row.createCell(6).setCellValue(ar.getBedNumber() != null ? ar.getBedNumber() : "");
                row.createCell(7).setCellValue(ar.getMatchScore() != null ? ar.getMatchScore().doubleValue() : 0);
                row.createCell(8).setCellValue(statusLabel(ar.getStatus()));
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = URLEncoder.encode(
                    (batch != null ? batch.getName() : "分配报表") + ".xlsx",
                    StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    private String statusLabel(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("recommended", "推荐");
        map.put("confirmed", "已确认");
        map.put("auto_confirmed", "自动确认");
        map.put("manual_assigned", "手动分配");
        map.put("adjusted", "已调换");
        return map.getOrDefault(status, status);
    }
}
