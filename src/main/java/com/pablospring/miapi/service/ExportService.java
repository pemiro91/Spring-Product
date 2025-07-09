package com.pablospring.miapi.service;


import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pablospring.miapi.domain.model.Product;
import com.pablospring.miapi.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.output.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.awt.Color;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final ProductRepository productRepository;

    public byte[] exportToExcel() throws Exception {
        List<Product> productos = productRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Productos");

        // Estilo para el título
        CellStyle titleStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLUE.getIndex());
        titleStyle.setFont(titleFont);

        org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("📦 Reporte de Productos");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        // Estilo encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(2);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Precio");

        for (Cell cell : header) {
            cell.setCellStyle(headerStyle);
        }

        // Contenido
        int rowNum = 3;
        for (Product p : productos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getName());
            row.createCell(2).setCellValue(p.getPrice());
        }

        // Ajustar columnas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] exportToPdf() throws Exception {
        List<Product> products = productRepository.findAll();

        var document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Título
        com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
        com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("📦 Reporte de Productos", titleFont);
        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // Tabla
        PdfPTable table = new PdfPTable(3); // 3 columnas
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 5f, 2f});
        table.setSpacingBefore(10f);

        // Estilo de encabezado
        com.lowagie.text.Font headFont = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 12, Color.WHITE);

        PdfPCell h1 = new PdfPCell(new com.lowagie.text.Phrase("ID", headFont));
        h1.setBackgroundColor(Color.GRAY);
        h1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(h1);

        PdfPCell h2 = new PdfPCell(new com.lowagie.text.Phrase("Nombre", headFont));
        h2.setBackgroundColor(Color.GRAY);
        h2.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(h2);

        PdfPCell h3 = new PdfPCell(new com.lowagie.text.Phrase("Precio", headFont));
        h3.setBackgroundColor(Color.GRAY);
        h3.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(h3);

        // Contenido de productos
        for (Product p : products) {
            PdfPCell cell1 = new PdfPCell(new com.lowagie.text.Phrase(String.valueOf(p.getId())));
            PdfPCell cell2 = new PdfPCell(new com.lowagie.text.Phrase(p.getName()));
            PdfPCell cell3 = new PdfPCell(new com.lowagie.text.Phrase("$" + p.getPrice()));

            cell1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            cell2.setPaddingLeft(10);
            cell3.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }
}
