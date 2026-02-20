package fr.miage.sgpa.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.miage.sgpa.model.RapportMensuel;
import fr.miage.sgpa.model.TopMedicament;
import fr.miage.sgpa.model.Vente;
import fr.miage.sgpa.model.VenteLigne;
import fr.miage.sgpa.service.RapportService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Contrôleur du module Rapports financiers enrichi.
 *
 * Fonctionnement :
 * 1. Choisir période (DatePicker début/fin)
 * 2. Cliquer "Generer" → affiche KPIs + tableaux
 * 3. Exporter PDF ou Excel
 */
public class RapportController {

    private static final Logger LOG = LoggerFactory.getLogger(RapportController.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Filtres ───────────────────────────────────────────────────────────
    @FXML
    private DatePicker debutPicker;
    @FXML
    private DatePicker finPicker;

    // ── KPI labels ────────────────────────────────────────────────────────
    @FXML
    private Label kpiNbVentes;
    @FXML
    private Label kpiCaTotal;
    @FXML
    private Label kpiPanierMoyen;

    // ── Table mensuelle ───────────────────────────────────────────────────
    @FXML
    private TableView<RapportMensuel> mensuelTable;
    @FXML
    private TableColumn<RapportMensuel, String> mensuelMoisCol;
    @FXML
    private TableColumn<RapportMensuel, Integer> mensuelNbCol;
    @FXML
    private TableColumn<RapportMensuel, String> mensuelCaCol;

    // ── Top médicaments ───────────────────────────────────────────────────
    @FXML
    private TableView<TopMedicament> topTable;
    @FXML
    private TableColumn<TopMedicament, String> topNomCol;
    @FXML
    private TableColumn<TopMedicament, Integer> topQteCol;
    @FXML
    private TableColumn<TopMedicament, String> topCaCol;

    // ── Historique ventes ─────────────────────────────────────────────────
    @FXML
    private TableView<Vente> ventesTable;
    @FXML
    private TableColumn<Vente, String> vDateCol;
    @FXML
    private TableColumn<Vente, String> vMontantCol;
    @FXML
    private TableColumn<Vente, String> vOrdoCol;

    // ── Détail lignes vente sélectionnée ─────────────────────────────────
    @FXML
    private TableView<VenteLigne> lignesTable;
    @FXML
    private TableColumn<VenteLigne, String> lgNomCol;
    @FXML
    private TableColumn<VenteLigne, Integer> lgQteCol;
    @FXML
    private TableColumn<VenteLigne, String> lgPrixCol;
    @FXML
    private TableColumn<VenteLigne, String> lgTotalCol;

    private final RapportService rapportService = new RapportService();

    // Données chargées (pour export)
    private List<RapportMensuel> rapportsMensuels;
    private List<TopMedicament> topMedicaments;
    private List<Vente> ventes;
    private BigDecimal[] kpis;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;

    @FXML
    public void initialize() {
        // Configurer les cellValueFactory
        mensuelMoisCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMois()));
        mensuelNbCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getNbVentes()).asObject());
        mensuelCaCol.setCellValueFactory(cd -> new SimpleStringProperty(fmt(cd.getValue().getTotalEuros())));

        topNomCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNomCommercial()));
        topQteCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getQuantiteTotale()).asObject());
        topCaCol.setCellValueFactory(cd -> new SimpleStringProperty(fmt(cd.getValue().getCaGenere())));

        vDateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDateHeure() != null ? cd.getValue().getDateHeure().format(FMT) : ""));
        vMontantCol.setCellValueFactory(cd -> new SimpleStringProperty(fmt(cd.getValue().getMontantTotal())));
        vOrdoCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().isSurOrdonnance() ? "Oui" : "Non"));

        lgNomCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNomMedicament()));
        lgQteCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getQuantite()).asObject());
        lgPrixCol.setCellValueFactory(cd -> new SimpleStringProperty(fmt(cd.getValue().getPrixUnitaire())));
        lgTotalCol.setCellValueFactory(cd -> new SimpleStringProperty(fmt(
                cd.getValue().getPrixUnitaire().multiply(BigDecimal.valueOf(cd.getValue().getQuantite())))));

        // Sur sélection d'une vente → charger ses lignes
        ventesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                List<VenteLigne> lignes = rapportService.getLignesVente(sel.getId());
                lignesTable.setItems(FXCollections.observableArrayList(lignes));
            } else {
                lignesTable.setItems(FXCollections.emptyObservableList());
            }
        });

        // Période par défaut = mois courant
        LocalDate now = LocalDate.now();
        debutPicker.setValue(now.withDayOfMonth(1));
        finPicker.setValue(now);
    }

    @FXML
    private void handleGenerer() {
        periodeDebut = debutPicker.getValue();
        periodeFin = finPicker.getValue();

        try {
            kpis = rapportService.getKpis(periodeDebut, periodeFin);
            rapportsMensuels = rapportService.getRapportMensuelPeriode(periodeDebut, periodeFin);
            topMedicaments = rapportService.getTopMedicaments(periodeDebut, periodeFin);
            ventes = rapportService.getVentesPeriode(periodeDebut, periodeFin);

            kpiNbVentes.setText(kpis[0].toPlainString());
            kpiCaTotal.setText(fmt(kpis[1]));
            kpiPanierMoyen.setText(fmt(kpis[2]));

            mensuelTable.setItems(FXCollections.observableArrayList(rapportsMensuels));
            topTable.setItems(FXCollections.observableArrayList(topMedicaments));
            ventesTable.setItems(FXCollections.observableArrayList(ventes));
            lignesTable.setItems(FXCollections.emptyObservableList());

            LOG.info("Rapport genere : {} ventes, CA={}", kpis[0], kpis[1]);
        } catch (Exception e) {
            LOG.error("Erreur generation rapport", e);
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).showAndWait();
        }
    }

    // ── PDF ───────────────────────────────────────────────────────────────

    @FXML
    private void exportPDF() {
        if (ventes == null) {
            noData();
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer PDF");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fc.setInitialFileName("rapport_sgpa.pdf");
        java.io.File file = fc.showSaveDialog(mensuelTable.getScene().getWindow());
        if (file == null)
            return;

        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            com.itextpdf.text.Font titleF = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font h2F = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13,
                    new BaseColor(46, 125, 50));
            com.itextpdf.text.Font bodyF = FontFactory.getFont(FontFactory.HELVETICA, 10);

            doc.add(new Paragraph("Rapport Financier SGPA", titleF));
            doc.add(new Paragraph("Periode : " + fmt(periodeDebut) + " → " + fmt(periodeFin), bodyF));
            doc.add(new Paragraph(" "));

            // KPIs
            doc.add(new Paragraph("Résumé global", h2F));
            PdfPTable kpiTable = new PdfPTable(3);
            kpiTable.setWidthPercentage(100);
            for (String h : new String[] { "Nb ventes", "CA total (EUR)", "Panier moyen (EUR)" })
                kpiTable.addCell(headerCell(h));
            kpiTable.addCell(kpis[0].toPlainString());
            kpiTable.addCell(fmt(kpis[1]));
            kpiTable.addCell(fmt(kpis[2]));
            doc.add(kpiTable);
            doc.add(new Paragraph(" "));

            // CA par mois
            doc.add(new Paragraph("CA mensuel", h2F));
            PdfPTable mTable = new PdfPTable(3);
            mTable.setWidthPercentage(100);
            for (String h : new String[] { "Mois", "Nb ventes", "Total (EUR)" })
                mTable.addCell(headerCell(h));
            for (RapportMensuel r : rapportsMensuels) {
                mTable.addCell(r.getMois());
                mTable.addCell(String.valueOf(r.getNbVentes()));
                mTable.addCell(fmt(r.getTotalEuros()));
            }
            doc.add(mTable);
            doc.add(new Paragraph(" "));

            // Top médicaments
            doc.add(new Paragraph("Top médicaments vendus", h2F));
            PdfPTable tTable = new PdfPTable(3);
            tTable.setWidthPercentage(100);
            for (String h : new String[] { "Medicament", "Qte vendue", "CA (EUR)" })
                tTable.addCell(headerCell(h));
            for (TopMedicament t : topMedicaments) {
                tTable.addCell(t.getNomCommercial());
                tTable.addCell(String.valueOf(t.getQuantiteTotale()));
                tTable.addCell(fmt(t.getCaGenere()));
            }
            doc.add(tTable);
            doc.add(new Paragraph(" "));

            // Historique ventes
            doc.add(new Paragraph("Historique des ventes", h2F));
            PdfPTable vTable = new PdfPTable(3);
            vTable.setWidthPercentage(100);
            for (String h : new String[] { "Date", "Montant (EUR)", "Ordonnance" })
                vTable.addCell(headerCell(h));
            for (Vente v : ventes) {
                vTable.addCell(v.getDateHeure() != null ? v.getDateHeure().format(FMT) : "");
                vTable.addCell(fmt(v.getMontantTotal()));
                vTable.addCell(v.isSurOrdonnance() ? "Oui" : "Non");
            }
            doc.add(vTable);

            doc.close();
            LOG.info("PDF exporte : {}", file.getAbsolutePath());
            new Alert(Alert.AlertType.INFORMATION, "PDF exporte :\n" + file.getAbsolutePath()).showAndWait();
        } catch (Exception e) {
            LOG.error("Erreur export PDF", e);
            new Alert(Alert.AlertType.ERROR, "Erreur PDF : " + e.getMessage()).showAndWait();
        }
    }

    // ── Excel ─────────────────────────────────────────────────────────────

    @FXML
    private void exportExcel() {
        if (ventes == null) {
            noData();
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer Excel");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fc.setInitialFileName("rapport_sgpa.xlsx");
        java.io.File file = fc.showSaveDialog(mensuelTable.getScene().getWindow());
        if (file == null)
            return;

        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle hdrStyle = boldGreenStyle(wb);

            // Feuille 1 — Résumé
            Sheet s1 = wb.createSheet("Résumé");
            writeRow(s1.createRow(0), hdrStyle, "Nb ventes", "CA total (EUR)", "Panier moyen (EUR)");
            writeRow(s1.createRow(1), null, kpis[0].toPlainString(), fmt(kpis[1]), fmt(kpis[2]));
            autoSize(s1, 3);

            // Feuille 2 — CA mensuel
            Sheet s2 = wb.createSheet("CA mensuel");
            writeRow(s2.createRow(0), hdrStyle, "Mois", "Nb ventes", "Total (EUR)");
            int ri = 1;
            for (RapportMensuel r : rapportsMensuels)
                writeRow(s2.createRow(ri++), null, r.getMois(), String.valueOf(r.getNbVentes()),
                        fmt(r.getTotalEuros()));
            autoSize(s2, 3);

            // Feuille 3 — Ventes
            Sheet s3 = wb.createSheet("Ventes");
            writeRow(s3.createRow(0), hdrStyle, "ID", "Date", "Montant (EUR)", "Ordonnance");
            ri = 1;
            for (Vente v : ventes)
                writeRow(s3.createRow(ri++), null,
                        String.valueOf(v.getId()),
                        v.getDateHeure() != null ? v.getDateHeure().format(FMT) : "",
                        fmt(v.getMontantTotal()),
                        v.isSurOrdonnance() ? "Oui" : "Non");
            autoSize(s3, 4);

            // Feuille 4 — Détails ventes (toutes les lignes)
            Sheet s4 = wb.createSheet("Details ventes");
            writeRow(s4.createRow(0), hdrStyle, "Vente ID", "Medicament", "Qte", "Prix unit.", "Total ligne");
            ri = 1;
            for (Vente v : ventes) {
                for (VenteLigne l : rapportService.getLignesVente(v.getId())) {
                    BigDecimal total = l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite()));
                    writeRow(s4.createRow(ri++), null,
                            String.valueOf(v.getId()),
                            l.getNomMedicament(),
                            String.valueOf(l.getQuantite()),
                            fmt(l.getPrixUnitaire()),
                            fmt(total));
                }
            }
            autoSize(s4, 5);

            // Feuille 5 — Top médicaments
            Sheet s5 = wb.createSheet("Top médicaments");
            writeRow(s5.createRow(0), hdrStyle, "Medicament", "Qte vendue", "CA (EUR)");
            ri = 1;
            for (TopMedicament t : topMedicaments)
                writeRow(s5.createRow(ri++), null,
                        t.getNomCommercial(),
                        String.valueOf(t.getQuantiteTotale()),
                        fmt(t.getCaGenere()));
            autoSize(s5, 3);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
            LOG.info("Excel exporte : {}", file.getAbsolutePath());
            new Alert(Alert.AlertType.INFORMATION, "Excel exporte :\n" + file.getAbsolutePath()).showAndWait();
        } catch (Exception e) {
            LOG.error("Erreur export Excel", e);
            new Alert(Alert.AlertType.ERROR, "Erreur Excel : " + e.getMessage()).showAndWait();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String fmt(BigDecimal v) {
        return v == null ? "0.00" : String.format("%.2f", v);
    }

    private String fmt(LocalDate d) {
        return d == null ? "" : d.toString();
    }

    private void noData() {
        new Alert(Alert.AlertType.WARNING, "Veuillez d'abord generer le rapport.").showAndWait();
    }

    private PdfPCell headerCell(String text) {
        com.itextpdf.text.Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(new BaseColor(46, 125, 50));
        c.setPadding(5);
        return c;
    }

    private CellStyle boldGreenStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }

    private void writeRow(org.apache.poi.ss.usermodel.Row row, CellStyle style, String... vals) {
        for (int i = 0; i < vals.length; i++) {
            org.apache.poi.ss.usermodel.Cell c = row.createCell(i);
            c.setCellValue(vals[i]);
            if (style != null)
                c.setCellStyle(style);
        }
    }

    private void autoSize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++)
            sheet.autoSizeColumn(i);
    }
}
