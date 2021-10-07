package com.clickhouse.client.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.clickhouse.client.ClickHouseChecker;
import com.clickhouse.client.ClickHouseColumn;
import com.clickhouse.client.ClickHouseFormat;

public class ClickHouseExternalTable {
    public static class Builder {
        private String name;
        private InputStream content;
        private ClickHouseFormat format;
        private List<ClickHouseColumn> columns;

        protected Builder() {
            columns = new LinkedList<>();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withContent(InputStream content) {
            this.content = content;
            return this;
        }

        public Builder withFormat(ClickHouseFormat format) {
            this.format = format;
            return this;
        }

        public Builder addColumn(String name, String type) {
            this.columns.add(ClickHouseColumn.of(name, type));
            return this;
        }

        public Builder removeColumn(String name) {
            Iterator<ClickHouseColumn> iterator = columns.iterator();
            while (iterator.hasNext()) {
                ClickHouseColumn c = iterator.next();
                if (c.getColumnName().equals(name)) {
                    iterator.remove();
                }
            }

            return this;
        }

        public Builder removeColumn(ClickHouseColumn column) {
            this.columns.remove(column);
            return this;
        }

        public Builder withColumns(Collection<ClickHouseColumn> columns) {
            if (columns != null) {
                for (ClickHouseColumn c : columns) {
                    this.columns.add(c);
                }
            }
            return this;
        }

        public ClickHouseExternalTable build() {
            return new ClickHouseExternalTable(name, content, format, columns);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String name;
    private final InputStream content;
    private final ClickHouseFormat format;
    private final List<ClickHouseColumn> columns;

    private final String structure;

    protected ClickHouseExternalTable(String name, InputStream content, ClickHouseFormat format,
            Collection<ClickHouseColumn> columns) {
        this.name = name == null ? "" : name.trim();
        this.content = ClickHouseChecker.nonNull(content, "content");
        this.format = format == null ? ClickHouseFormat.TabSeparated : format;

        int size = columns == null ? 0 : columns.size();
        if (size == 0) {
            this.columns = Collections.emptyList();
            this.structure = "";
        } else {
            StringBuilder builder = new StringBuilder();
            List<ClickHouseColumn> list = new ArrayList<>(size);
            for (ClickHouseColumn c : columns) {
                list.add(c);
                builder.append(c.getColumnName()).append(' ').append(c.getOriginalTypeName()).append(',');
            }
            this.columns = Collections.unmodifiableList(list);
            this.structure = builder.deleteCharAt(builder.length() - 1).toString();
        }
    }

    public boolean hasName() {
        return !name.isEmpty();
    }

    public String getName() {
        return name;
    }

    public InputStream getContent() {
        return content;
    }

    public ClickHouseFormat getFormat() {
        return format;
    }

    public List<ClickHouseColumn> getColumns() {
        return columns;
    }

    public String getStructure() {
        return structure;
    }
}
