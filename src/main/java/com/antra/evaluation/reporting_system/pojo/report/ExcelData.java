package com.antra.evaluation.reporting_system.pojo.report;

import java.time.LocalDateTime;
import java.util.List;

public class ExcelData {
    private String fileId;
    private String fileName;
    private String title;
    private LocalDateTime generatedTime;
    private List<ExcelDataSheet> sheets;
    private static final String FILENAME_EXTENNSION = ".xlsx";

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if (fileName.length() < 20) {
            this.fileName = fileName + FILENAME_EXTENNSION;
        }
        this.fileName = fileName.substring(0, 17) + "..." + FILENAME_EXTENNSION;
    }

    public void autoSetFileName() {
        if (title.length() < 20) {
            this.fileName = title + FILENAME_EXTENNSION;
        }

        this.fileName = title.substring(0, 17) + "..." + FILENAME_EXTENNSION;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

    public List<ExcelDataSheet> getSheets() {
        return sheets;
    }

    public void setSheets(List<ExcelDataSheet> sheets) {
        this.sheets = sheets;
    }
}
