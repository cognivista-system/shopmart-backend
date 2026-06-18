package com.shopmart.module.report.mapper;

import java.util.List;

/** Minimal RFC-4180-ish CSV builder for report exports. */
public final class CsvWriter {
    private CsvWriter() {}

    public static String build(List<String> header, List<List<Object>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(header.stream().map(Object.class::cast).toList()));
        for (List<Object> row : rows) {
            sb.append(line(row));
        }
        return sb.toString();
    }

    private static String line(List<Object> cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(cells.get(i)));
        }
        sb.append('\n');
        return sb.toString();
    }

    private static String escape(Object value) {
        String s = value == null ? "" : value.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }
}
