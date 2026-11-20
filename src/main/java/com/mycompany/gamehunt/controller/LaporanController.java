package com.mycompany.gamehunt.controller;

import com.mycompany.gamehunt.util.AppContext;
import com.mycompany.gamehunt.util.HibernateUtil;
import net.sf.jasperreports.engine.*;
import org.hibernate.Session;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/** Laporan keuangan ringkas: hitung SUM pendapatan/pengeluaran + cetak template Jasper Studio (game_hunt.jrxml). */
public class LaporanController {

    /** Lokasi template laporan (paket com.mycompany.gamehunt.report). */
    private static final String REPORT_PATH = "/com/mycompany/gamehunt/report/game_hunt.jrxml";

    /** Hasil ringkasan keuangan. */
    public static class Ringkasan {
        public final BigDecimal pendapatan, pengeluaran;
        public Ringkasan(BigDecimal pendapatan, BigDecimal pengeluaran) {
            this.pendapatan = pendapatan; this.pengeluaran = pengeluaran;
        }
        public BigDecimal laba() { return pendapatan.subtract(pengeluaran); }
    }

    /** Menghitung total pendapatan & pengeluaran dengan fungsi SQL SUM (via HQL). */
    public Ringkasan ringkasan() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal in = (BigDecimal) s.createQuery("select sum(x.total) from Sale x").uniqueResult();
            BigDecimal out = (BigDecimal) s.createQuery("select sum(x.total) from Purchase x").uniqueResult();
            return new Ringkasan(in == null ? BigDecimal.ZERO : in, out == null ? BigDecimal.ZERO : out);
        }
    }

    /**
     * Kompilasi game_hunt.jrxml lalu isi dengan data memakai koneksi JDBC (DataSource Spring).
     * Query diambil dari dalam file jrxml itu sendiri.
     * Parameter di bawah hanya terpakai jika dideklarasikan di jrxml; jika tidak, diabaikan.
     */
    public JasperPrint buatLaporan(String dicetakOleh) throws JRException {
        Ringkasan r = ringkasan();
        Map<String, Object> params = new HashMap<>();
        params.put("TOTAL_PENDAPATAN", r.pendapatan);
        params.put("TOTAL_PENGELUARAN", r.pengeluaran);
        params.put("LABA_KOTOR", r.laba());
        params.put("DICETAK_OLEH", dicetakOleh);
        params.put("TANGGAL_CETAK", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

        DataSource ds = AppContext.get().getBean("dataSource", DataSource.class);
        try (InputStream in = getClass().getResourceAsStream(REPORT_PATH);
             Connection conn = ds.getConnection()) {
            if (in == null) {
                throw new JRException("File game_hunt.jrxml tidak ditemukan di paket com.mycompany.gamehunt.report.");
            }
            JasperReport report = JasperCompileManager.compileReport(in);
            return JasperFillManager.fillReport(report, params, conn);
        } catch (java.io.IOException | SQLException e) {
            throw new JRException(e);
        }
    }

    public void simpanPdf(JasperPrint print, String path) throws JRException {
        JasperExportManager.exportReportToPdfFile(print, path);
    }
}
