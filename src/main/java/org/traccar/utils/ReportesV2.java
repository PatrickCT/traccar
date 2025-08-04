/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

/**
 *
 * @author K
 */
public class ReportesV2 {

    private Workbook workbook = null;
    private int globalRow = 0;

    public ReportesV2() {

    }

    public void createReporte(String name, String sheetName, List<String> rowHeaders,
            List<String[]> data, OutputStream os, String periodo) throws IOException {
        workbook = new Workbook(os, "GPS Tracker", "1.0");
        Worksheet ws = workbook.newWorksheet(sheetName);
        int header = 0;
        int startRowHeader = 4;
        for (String h : rowHeaders) {
            ws.value(startRowHeader, header, h);
            ws.style(startRowHeader, header).bold().fontSize(11).wrapText(true).set();
            ws.style(startRowHeader, header).fillColor("5b9ad5").fontColor("ffffff").set();
            header += 1;
        }
        //ws.freezePane(rowHeaders.size(), startRowHeader + 1);

        int row = startRowHeader + 1;
        int col = 0;
        for (String[] rowData : data) {
            for (String cellData : rowData) {
                ws.value(row, col, cellData);
                ws.style(row, col).wrapText(false).set();
                col += 1;
            }
            row += 1;
            col = 0;
        }

        //Extra headers
        ws.value(1, 0, "Report type:");
        ws.style(1, 0).wrapText(true).set();

        ws.value(3, 0, "Period:");
        ws.style(3, 0).wrapText(true).set();

        ws.value(1, 1, "Entradas y Salidas");
        ws.style(1, 1).wrapText(true).set();

        ws.value(3, 1, periodo);
        ws.style(3, 1).wrapText(true).set();

        workbook.finish();
    }
}
