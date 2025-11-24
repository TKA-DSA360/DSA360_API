package com.dsa360.api.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.dao.AuditLogDao;
import com.dsa360.api.entity.AuditLog;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

	@Autowired
	private AuditLogDao auditLogDao;

	@GetMapping("/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=audit_logs.csv");

		List<AuditLog> logs = auditLogDao.findAll();

		try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(response.getOutputStream()),
				CSVFormat.DEFAULT.withHeader("Username", "Action", "Timestamp", "Entity", "Description"))) {
			for (AuditLog log : logs) {
				csvPrinter.printRecord(log.getUsername(), log.getAction(), log.getTimestamp(), log.getEntity(),
						log.getDescription());
			}
		}
	}

	@GetMapping("/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=audit_logs.pdf");

		List<AuditLog> logs = auditLogDao.findAll();

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
		Paragraph title = new Paragraph("Audit Log Report", font);
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		document.add(Chunk.NEWLINE);

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 2f, 2f, 3f, 3f, 4f });

		Stream.of("Username", "Action", "Timestamp", "Entity", "Description").forEach(header -> {
			PdfPCell cell = new PdfPCell(new Phrase(header));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
		});

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		for (AuditLog log : logs) {
			table.addCell(log.getUsername());
			table.addCell(log.getAction());
			table.addCell(log.getTimestamp().format(formatter));
			table.addCell(log.getEntity());
			table.addCell(log.getDescription());
		}

		document.add(table);
		document.close();
	}
	
	
	@GetMapping("/sample")
	public String sample() {
		return "Sample endpoint for AuditLogController";
	}
	
}