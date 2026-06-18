package com.shopmart.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal, dependency-free single-page PDF generator (PDF 1.4).
 * Renders a list of monospaced text lines (Courier / Courier-Bold) onto an A4 page.
 * Good enough for invoices and receipts without pulling in iText/PDFBox.
 */
public final class PdfWriter {

    public record Line(String text, boolean bold) {
        public static Line of(String t) { return new Line(t, false); }
        public static Line bold(String t) { return new Line(t, true); }
    }

    private PdfWriter() {}

    public static byte[] render(List<Line> lines) {
        StringBuilder content = new StringBuilder();
        content.append("BT\n/F2 10 Tf\n13 TL\n50 800 Td\n");
        for (Line line : lines) {
            content.append(line.bold() ? "/F1 10 Tf\n" : "/F2 10 Tf\n");
            content.append('(').append(escape(line.text())).append(") Tj\nT*\n");
        }
        content.append("ET");
        byte[] stream = content.toString().getBytes(StandardCharsets.ISO_8859_1);

        List<String> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");
        objects.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                + "/Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> /Contents 4 0 R >>");
        objects.add("<< /Length " + stream.length + " >>\nstream\n" + new String(stream, StandardCharsets.ISO_8859_1) + "\nendstream");
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Courier-Bold >>");
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        write(out, "%PDF-1.4\n");
        write(out, "%\u00E2\u00E3\u00CF\u00D3\n");
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(out.size());
            write(out, (i + 1) + " 0 obj\n" + objects.get(i) + "\nendobj\n");
        }
        int xrefStart = out.size();
        StringBuilder xref = new StringBuilder();
        int count = objects.size() + 1;
        xref.append("xref\n0 ").append(count).append("\n");
        xref.append("0000000000 65535 f \n");
        for (int off : offsets) {
            xref.append(String.format("%010d 00000 n \n", off));
        }
        xref.append("trailer\n<< /Size ").append(count).append(" /Root 1 0 R >>\n");
        xref.append("startxref\n").append(xrefStart).append("\n%%EOF");
        write(out, xref.toString());
        return out.toByteArray();
    }

    private static void write(ByteArrayOutputStream out, String s) {
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        out.write(b, 0, b.length);
    }

    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c == '\\' || c == '(' || c == ')') sb.append('\\').append(c);
            else if (c >= 32 && c < 127) sb.append(c);
            else sb.append(' '); // drop non-Latin1 printable chars
        }
        return sb.toString();
    }
}
