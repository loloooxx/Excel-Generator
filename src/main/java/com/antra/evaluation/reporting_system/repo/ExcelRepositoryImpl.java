package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ExcelRepositoryImpl implements ExcelRepository {

    Map<String, ExcelFile> excelData = new ConcurrentHashMap<>();

    @Override
    public Optional<ExcelFile> getFileById(String id) {
        return Optional.ofNullable(excelData.get(id));
    }

    @Override
    public ExcelFile saveFile(ExcelFile file) {
        excelData.put(file.getFileId(), file);
        return file;
    }

    @Override
    public ExcelFile deleteFile(String id) {
        if (!excelData.containsKey((id))) {
            throw new RuntimeException("No such file id exists.");
        }

        ExcelFile file = excelData.get(id);
        excelData.remove(id);

        return file;
    }

    @Override
    public List<ExcelFile> getFiles() {
        List<ExcelFile> fileList = new ArrayList<>();
        for (ExcelFile file : excelData.values()) {
            fileList.add(file);
        }
        return fileList;
    }
}

