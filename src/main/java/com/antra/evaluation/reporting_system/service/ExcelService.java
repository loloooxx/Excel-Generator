package com.antra.evaluation.reporting_system.service;

import java.io.InputStream;
import java.util.List;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;


public interface ExcelService {
    InputStream getExcelBodyById(String id);

    ExcelData getExcelDataById(String id);

    String generateExcelFile(ExcelRequest request);

    String generateMultiSheetExcel(MultiSheetExcelRequest request);

    ExcelFile deleteExcelFile(String id);

    List<ExcelFile> listExcelFiles();
}
