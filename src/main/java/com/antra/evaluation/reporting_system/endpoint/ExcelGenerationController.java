package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExcelGenerationController {

    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);

    ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/excel")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) {
        String id = excelService.generateExcelFile(request);
        ExcelResponse response = new ExcelResponse();
        response.setFileId(id);
        log.info("Generate an excel file successfully, file id is {}.", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) {
        String id = excelService.generateMultiSheetExcel(request);
        ExcelResponse response = new ExcelResponse();
        response.setFileId(id);
        log.info("Generate a multi-sheet excel file successfully, file id is {}.", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<List<ExcelResponse>> listExcels() {
        List<ExcelFile> files = excelService.listExcelFiles();
        var response = new ArrayList<ExcelResponse>();

        for (ExcelFile file : files) {
            ExcelResponse res = new ExcelResponse();
            res.setFileId(file.getFileId());
            response.add(res);
        }

        log.info("List all the excel files successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel/{id}/content")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {
        InputStream fis = excelService.getExcelBodyById(id);
        ExcelData data = excelService.getExcelDataById(id);
        response.setHeader("Content-Type","application/vnd.ms-excel");
        response.setHeader("Content-Disposition","attachment; filename=\"" + data.getFileName() + "\"");
        FileCopyUtils.copy(fis, response.getOutputStream());
        log.info("Download an excel file successfully, file id is {}.", id);
    }

    @DeleteMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) {
        excelService.deleteExcelFile(id);
        var response = new ExcelResponse();
        response.setFileId(id);
        log.info("Delete an excel file successfully, file id is {}.", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

