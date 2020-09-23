package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataType;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static int HEADER_WIDTH = 10;
    private static int fileId = 0;

    @Autowired
    ExcelRepository excelRepository;

    @Autowired
    ExcelGenerationService excelGenerationService;

    @Override
    public String generateExcelFile(ExcelRequest request) {
        ExcelData excelData = convertExcelRequestToExcelData(request);
        ExcelFile excelFile = new ExcelFile();
        fileId += 1;
        String id = String.valueOf(fileId);
        excelData.setFileId(id);
        excelData.setGeneratedTime(LocalDateTime.now());
        excelFile.setFileId(id);
        excelFile.setExcelData(excelData);
        excelRepository.saveFile(excelFile);
        return id;
    }

    @Override
    public String generateMultiSheetExcel(MultiSheetExcelRequest request) {
        ExcelData excelData = convertMultiSheetExcelRequestToExcelData(request);
        ExcelFile excelFile = new ExcelFile();
        fileId += 1;
        String id = String.valueOf(fileId);
        excelData.setFileId(id);
        excelData.setGeneratedTime(LocalDateTime.now());
        excelFile.setFileId(id);
        excelFile.setExcelData(excelData);
        excelRepository.saveFile(excelFile);
        return id;
    }

    private ExcelData convertExcelRequestToExcelData(ExcelRequest request) {
        ExcelDataSheet sheet = new ExcelDataSheet();
        ExcelData excelData = new ExcelData();

        List<ExcelDataHeader> headers = new ArrayList<>();
        for (String header : request.getHeaders()) {
            ExcelDataHeader excelDataHeader = new ExcelDataHeader();
            excelDataHeader.setName(header);
            excelDataHeader.setType(ExcelDataType.STRING);
            excelDataHeader.setWidth(HEADER_WIDTH);
            headers.add(excelDataHeader);
        }

        List<List<Object>> dataRows = new ArrayList<>();
        dataRows.addAll(request.getData());

        sheet.setTitle(request.getDescription());
        sheet.setHeaders(headers);
        sheet.setDataRows(dataRows);

        List<ExcelDataSheet> sheets = new ArrayList<>();
        sheets.add(sheet);

        excelData.setTitle(sheet.getTitle());
        excelData.autoSetFileName();
        excelData.setSheets(sheets);

        return excelData;
    }

    private ExcelData convertMultiSheetExcelRequestToExcelData(MultiSheetExcelRequest request) {
        ExcelData excelData = new ExcelData();

        List<ExcelDataHeader> headers = new ArrayList<>();
        int splitByHeaderIndex = -1;

        for (int i = 0; i < request.getHeaders().size(); ++i) {
            String header = request.getHeaders().get(i);
            ExcelDataHeader excelDataHeader = new ExcelDataHeader();
            excelDataHeader.setName(header);
            excelDataHeader.setType(ExcelDataType.STRING);
            excelDataHeader.setWidth(HEADER_WIDTH);
            headers.add(excelDataHeader);

            if (request.getSplitBy().equals(header)) {
                splitByHeaderIndex = i;
            }
        }

        Map<Object, List<List<Object>>> dataRowsSplitBy = new HashMap<>();
        for (List<Object> dataRow : request.getData()) {
            Object splitByItem = dataRow.get(splitByHeaderIndex);

            if (!dataRowsSplitBy.containsKey(splitByItem)) {
                dataRowsSplitBy.put(splitByItem, new ArrayList<>());
            }

            dataRowsSplitBy.get(splitByItem).add(dataRow);
        }

        List<ExcelDataSheet> sheets = new ArrayList<>();
        for (Map.Entry<Object, List<List<Object>>> entry : dataRowsSplitBy.entrySet()) {
            ExcelDataSheet sheet = new ExcelDataSheet();
            sheet.setTitle(request.getSplitBy() + " " + entry.getKey());
            sheet.setHeaders(headers);
            sheet.setDataRows(entry.getValue());
            sheets.add(sheet);
        }

        excelData.setTitle(request.getDescription() + " split by " + request.getSplitBy());
        excelData.autoSetFileName();
        excelData.setSheets(sheets);

        return excelData;
    }

    @Override
    public InputStream getExcelBodyById(String id) {
        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);

        if (fileInfo.isPresent()) {
            try {
                File file = excelGenerationService.generateExcelReport(fileInfo.get().getExcelData());
                return new FileInputStream(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public ExcelData getExcelDataById(String id) {
        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
        if (fileInfo.isPresent()) {
            return fileInfo.get().getExcelData();
        }

        return null;
    }

    @Override
    public ExcelFile deleteExcelFile(String id) {
        return excelRepository.deleteFile(id);
    }

    @Override
    public List<ExcelFile> listExcelFiles() {
        return excelRepository.getFiles();
    }

}
