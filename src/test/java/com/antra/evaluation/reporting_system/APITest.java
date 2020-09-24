package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.service.ExcelService;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

public class APITest {
    @Mock
    ExcelService excelService;

    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService));
    }

    @Test
    public void testFileDownload() throws FileNotFoundException {
        ExcelData data = new ExcelData();
        data.setFileName("mathData");
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("mathData.xlsx"));
        Mockito.when(excelService.getExcelDataById(any())).thenReturn(data);
        given().accept("application/json").get("/excel/1/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testListFiles() throws FileNotFoundException {
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testDeleteFile() {
        given().accept("application/json").delete("/excel/1").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testExcelGeneration() throws FileNotFoundException {
        Mockito.when(excelService.generateExcelFile(any())).thenReturn("1");
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void testMultiSheetsExcelGeneration() throws FileNotFoundException {
        Mockito.when(excelService.generateExcelFile(any())).thenReturn("1");
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]], \"splitBy\":[\"Name\"]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }
}
