package fr.miage.sgpa.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.miage.sgpa.model.RapportMensuel;
import fr.miage.sgpa.service.RapportService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Contrôleur des rapports financiers mensuels.
 * Affiche les données et permet l'export PDF et Excel.
 * NOTE: Font est ambigu entre iText et POI → on utilise les noms qualifiés.
 */
public class RapportController {

    private static final Logger logger = LoggerFactory.getLogger(RapportController.class);

    @FXML
    private TableView<RapportMensuel> rapportTable;
    @FXML
    private TableColumn<RapportMensuel, String> moisCol;
    @FXML
    private TableColumn<RapportMensuel, Integer> nbVentesCol;
    @FXML
    private TableColumn<RapportMensuel, String> totalCol;
    @FXML
    private Label totalGeneralLabel;

    private final RapportService rapportService = new RapportService();
    private List<RapportMensuel> rapports;

    @FXML
    public void initialize() {
        moisCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMois()));
        nbVentesCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getNbVentes()).asObject());
        totalCol.setCellValueFactory(cd -> new SimpleStringProperty(
                String.format("%.2f EUR", cd.getValue().getTotalEuros())));
        loadData();
    }

    private void loadData() {
        try {
            rapports = rapportService.getRapportMensuel();
            rapportTable.setItems(FXCollections.observableArrayList(rapports));
            BigDecimal total = rapports.stream()
                    .map(RapportMensuel::getTotalEuros).reduce(BigDecimal.ZERO, BigDecimal::add);
            int nbV = rapports.stream().mapToInt(RapportMensuel::getNbVentes).sum();
            totalGeneralLabel.setText(String.format("Total general : %d vente(s)  —  %.2f EUR", nbV, total));
        } catch (Exception e) {
            logger.error("Erreur chargement rapport", e);
            totalGeneralLabel.setText("Erreur de chargement");
        }
    }

    // ── Export PDF ──────────────────────────────────────────────────────────

    @FXML
    private void exportPDF() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le rapport PDF");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fc.setInitialFileName("rapport_ventes.pdf");
        File file = fc.showSaveDialog(rapportTable.getScene().getWindow());
        if (file == null)
            return;

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            // Titre
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            doc.add(new Paragraph("Rapport des ventes mensuelles — SGPA Pharmacie", titleFont));
            doc.add(new Paragraph(" "));

            // Tableau PDF
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 2f, 2f, 3f });

            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
            for (String h : new String[] { "Mois", "Nb ventes", "Total (EUR)" }) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new BaseColor(46, 125, 50));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            BigDecimal totalGeneral = BigDecimal.ZERO;
            int totalVentes = 0;
            for (RapportMensuel r : rapports) {
                table.addCell(r.getMois());
                table.addCell(String.valueOf(r.getNbVentes()));
                table.addCell(String.format("%.2f", r.getTotalEuros()));
                totalGeneral = totalGeneral.add(r.getTotalEuros());
                totalVentes += r.getNbVentes();
            }

            com.itextpdf.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            table.addCell(new Phrase("TOTAL", boldFont));
            table.addCell(new Phrase(String.valueOf(totalVentes), boldFont));
            table.addCell(new Phrase(String.format("%.2f", totalGeneral), boldFont));

            doc.add(table);
            doc.close();
            showInfo("PDF exporte :\n" + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Erreur export PDF", e);
            showError("Erreur PDF : " + e.getMessage());
        }
    }

    // ── Export Excel ────────────────────────────────────────────────────────

    @FXML
    private void exportExcel() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le rapport Excel");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fc.setInitialFileName("rapport_ventes.xlsx");
        File file = fc.showSaveDialog(rapportTable.getScene().getWindow());
        if (file == null)
            return;

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Rapport mensuel");

            // Style en-tête POI (noms qualifiés pour éviter l'ambiguité avec iText)
            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font hFont = wb.createFont();
            hFont.setBold(true);
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] cols = { "Mois", "Nb ventes", "Total (EUR)" };
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell c = headerRow.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            BigDecimal totalGeneral = BigDecimal.ZERO;
            int totalVentes = 0;
            int rowIdx = 1;
            for (RapportMensuel r : rapports) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getMois());
                row.createCell(1).setCellValue(r.getNbVentes());
                row.createCell(2).setCellValue(r.getTotalEuros().doubleValue());
                totalGeneral = totalGeneral.add(r.getTotalEuros());
                totalVentes += r.getNbVentes();
            }

            // Ligne TOTAL
            CellStyle boldStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font bFont = wb.createFont();
            bFont.setBold(true);
            boldStyle.setFont(bFont);
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowIdx);
            org.apache.poi.ss.usermodel.Cell c0 = totalRow.createCell(0);
            c0.setCellValue("TOTAL");
            c0.setCellStyle(boldStyle);
            org.apache.poi.ss.usermodel.Cell c1 = totalRow.createCell(1);
            c1.setCellValue(totalVentes);
            c1.setCellStyle(boldStyle);
            org.apache.poi.ss.usermodel.Cell c2 = totalRow.createCell(2);
            c2.setCellValue(totalGeneral.doubleValue());
            c2.setCellStyle(boldStyle);

            for (int i = 0; i < 3; i++)
                sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
            showInfo("Excel exporte :\n" + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Erreur export Excel", e);
            showError("Erreur Excel : " + e.getMessage());
        }
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
