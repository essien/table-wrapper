package essien;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Given the lengths of tokens in each cell of a MxN table, this class provides functionality to
 * determine how each of those tokens should be wrapped when the total table width available is W.
 *
 * Simply put, given a number W, this class enables you to wrap the tokens of each cell of a table
 * so that the table width does not exceed W.
 *
 * It tries to achieve fair and balanced sizing by ensuring that no row or column's size is
 * unnecessarily longer/shorter than the others.
 *
 * @author bodmas
 * @see #wrapToWidth(int)
 * @since 31 May 2026.
 */
public class TableWrapper {

    private final Cell[][] table;
    private final Map<String, Integer> map;
    private String[][] tableValues;

    // Accepts textual data.
    public TableWrapper(String[][] tableValues) {
        this(Arrays.stream(tableValues).map(it -> Stream.of(it).map(TableWrapper::getTokenLengths).map(Cell::new).toArray(Cell[]::new)
        ).toArray(Cell[][]::new));
        this.tableValues = tableValues;
    }

    // Accepts pure numerical data.
    public TableWrapper(Cell[][] table) {
        validate(table);
        this.table = table;
        this.map = new HashMap<>();
    }

    private static int[] getTokenLengths(String str) {
        return Arrays.stream(str.split(" ")).mapToInt(s -> s.length()).toArray();
    }

    private void validate(Cell[][] table) {
        assert table != null : "Expected table to not be null";
        assert table.length != 0 : "Table should have at least one row";
        assert table[0].length != 0 : "Table should have at least one column";
        for (int i = 1; i < table.length; i++)
            assert table[i].length == table[0].length : "All rows should have same number of columns as first row. Violated by row index: " + i;
    }

    public static void main(String[] args) throws IOException {
        int[] rowByColumnByWidth;
        Cell[][] table;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            rowByColumnByWidth = getNumbers(br.readLine());
            table = new Cell[rowByColumnByWidth[0]][rowByColumnByWidth[1]];
            // Read cell data line by line.
            for (int row = 0; row < rowByColumnByWidth[0]; row++)
                for (int col = 0; col < rowByColumnByWidth[1]; col++)
                    table[row][col] = new Cell(getNumbers(br.readLine()));
        }

        // Compute and display result.
        for (int[] res : new TableWrapper(table).wrapToWidth(rowByColumnByWidth[2]))
            System.out.println(Arrays.stream(res).mapToObj(Integer::toString).collect(Collectors.joining(" ")));
    }

    private static int[] getNumbers(String line) {
        return Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Determines the maximum width that each column is allowed to have in order to fit all column content into
     * the specify width.
     * @param width total width that all cell contents should be collectively made to fit into
     * @return the array of condensed widths of each column, followed by the array of maximum widths allowed for each column
     */
    public int[][] wrapToWidth(int width) {
        int[] ret = mostBalancedCellHeight(0, width);
        final int[] widths = extractWidths(width);
        return new int[][] {ret, computeCondensedWidths(widths), widths};
    }

    /*
     * Displays the table to fit the specified width.
     */
    public void show(int width) {
        if (tableValues == null) {
            throw new RuntimeException("Table data was not supplied");
        }
        int[][] result = wrapToWidth(width);
        Arrays.stream(result).skip(1).forEach(arr -> show(width, arr));
    }

    /*
     * Displays the table to fit the specified width.
     */
    public void show(int width, int[] maximumWidths) {
        if (tableValues == null) {
            throw new RuntimeException("Table data was not supplied");
        }
        Tabularizer tabularizer = new Tabularizer();
        tabularizer.tabularize(tableValues, maximumWidths);
    }

    static class Tabularizer {

        private boolean getNextLines(String[] next, BufferedReader[] bufferedReaders) throws IOException {
            for (int i = 0; i < next.length; i++) {
                next[i] = bufferedReaders[i].readLine();
            }
            return Stream.of(next).anyMatch(Objects::nonNull);
        }

        private void tabularize(String[][] tableValues, int[] maximumWidths) {
            System.out.println();
            System.out.println("--- Table ---");
            BufferedReader[][] bufferedReaders2d = Arrays.stream(tableValues).map(it ->
                    IntStream.range(0, it.length).mapToObj(i -> wrap(it[i], maximumWidths[i]))
                            .map(str -> new BufferedReader(new StringReader(str))).toArray(BufferedReader[]::new)
            ).toArray(BufferedReader[][]::new);

            final int tableWidth = IntStream.of(maximumWidths).sum() + 3 * maximumWidths.length + 1;
            boolean isFirstLine = true;
            System.out.println(fitToSize(null, tableWidth, "-"));
            StringBuilder sb = new StringBuilder(tableWidth);
            for (BufferedReader[] bufferedReaders1d : bufferedReaders2d) {
                { // Print demacator.
                    sb.delete(0, sb.length());
                    if (!isFirstLine) {
                        sb.append("|");
                        for (int i = 0; i < maximumWidths.length; i++) {
                            sb.append("-").append(fitToSize(null, maximumWidths[i], "-")).append("-");
                            if (i != maximumWidths.length - 1) {
                                sb.append("+");
                            }
                        }
                        sb.append("|\n");
                    }
                    System.out.print(sb);
                }

                sb.delete(0, sb.length());
                String[] next = new String[bufferedReaders1d.length];
                try {
                    // Print content.
                    while (getNextLines(next, bufferedReaders1d)) {
                        for (int i = 0; i < next.length; i++) {
                            sb.append("| ").append(fitToSize(next[i], maximumWidths[i])).append(" ");
                        }
                        sb.append("|\n");
                    }
                    System.out.print(sb);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    for (BufferedReader bufferedReader : bufferedReaders1d)
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                }
                isFirstLine = false;
            }
            System.out.println(fitToSize(null, tableWidth, "-"));
        }

        private String fitToSize(String str, int size) {
            return fitToSize(str, size, " ");
        }

        private String fitToSize(String str, int size, String toRepeat) {
            if (str == null)
                str = "";
            size -= str.length();
            return str + (toRepeat.repeat(size));
        }

        private String wrap(String str, int width) {
            String[] splits = str.split(" ");
            StringBuilder sb = new StringBuilder();
            int length = 0;
            boolean first = true;
            for (String part : splits) {
                if (!first)
                    length++; // For space character.
                length += part.length();
                if (length > width) {
                    length = part.length();
                    sb.append("\n");
                } else if (!first) {
                    sb.append(" ");
                }
                sb.append(part);
                first = false;
            }
            return sb.toString();
        }
    }

    private int[] extractWidths(int tableWidth) {
        int[] widths = new int[table[0].length];
        for (int i = 0; i < widths.length; i++) {
            int columnWidth = map.get(getKey(i, tableWidth));
            widths[i] = columnWidth;
            tableWidth -= columnWidth;
        }
        return widths;
    }

    private int[] computeCondensedWidths(int[] widths) {
        return IntStream.range(0, table[0].length).map(j ->
            IntStream.range(0, table.length).map(
                    i -> getHeightWidth(table[i][j], widths[j])[1]
            ).max().getAsInt()
        ).toArray();
    }

    /**
     * Computes the shortest maximum height obtainable for the given width and starting from the given column.
     * @param fromColumn the column to start from. Starts from this column up to the last index of widths.length
     * @param width the total available width for all columns to put their content into
     * @return an array defined as follows: [
     *      shortest maximum height of all cell width adjustments for the given table width,
     *      the remaining space between the last character of that cell and its right margin
     * ]
     */
    private int[] mostBalancedCellHeight(int fromColumn, int width) {
        if (fromColumn > table[0].length)
            throw new RuntimeException("Unexpected fromColumn: " + fromColumn);
        if (fromColumn == table[0].length)
            return new int[] {0, 0};

        // Get the smallest width possible for this column.
        int mySmallestWidth = IntStream.range(0, table.length).map(u -> table[u][fromColumn].getVerticalLength()).max().getAsInt();
        int myBiggestWidth = IntStream.range(0, table.length).map(u -> table[u][fromColumn].getHorizontalLength()).max().getAsInt();

        int maxAvailableWidth = width;
        for (int i = fromColumn + 1; i < table[0].length; i++) {
            int j = i;
            maxAvailableWidth -= IntStream.range(0, table.length).map(u -> table[u][j].getVerticalLength()).max().getAsInt();
        }

        if (myBiggestWidth > maxAvailableWidth)
            myBiggestWidth = maxAvailableWidth;

        if (myBiggestWidth < mySmallestWidth)
            throw new RuntimeException("Unable to work with the available column width " + maxAvailableWidth + " because it is not enough to contain a token of length " + mySmallestWidth + " in column " + (fromColumn + 1) + " (1-indexed).");

        myBiggestWidth++; // This allows us to find a best width in range [mySmallestWidth, (original)myBiggestWidth]
        int[] othersBestParam = null, myBestWidthParams = null;
        while (myBiggestWidth - mySmallestWidth > 1) {
            int myBestWidth = (mySmallestWidth + myBiggestWidth) / 2;
            int[] myWidthParams = longestCellHeight(fromColumn, myBestWidth);
            int myBestHeight = myWidthParams[0];
            int[] othersParam = mostBalancedCellHeight(fromColumn + 1, width - myBestWidth);
            int othersBestHeight = othersParam[0];
            if (//myBestHeight > 1 && myBestWidth + othersBestHeightWidth[1] < width ||
                myBestHeight >= othersBestHeight) {
                // Move slider right.
                mySmallestWidth = myBestWidth;
                myBestWidthParams = myWidthParams;
                othersBestParam = othersParam;
            } else {
                // Move slider left.
                myBiggestWidth = myBestWidth;
            }
        }
        if (myBestWidthParams == null) {
            myBestWidthParams = longestCellHeight(fromColumn, mySmallestWidth);
            othersBestParam = mostBalancedCellHeight(fromColumn + 1, width - mySmallestWidth);
        }

        map.put(getKey(fromColumn, width), mySmallestWidth);

        int retHeight, retRemaining;
        if (myBestWidthParams[0] > othersBestParam[0]) {
            retHeight = myBestWidthParams[0];
            retRemaining = myBestWidthParams[1];
        } else if (myBestWidthParams[0] < othersBestParam[0]) {
            retHeight = othersBestParam[0];
            retRemaining = othersBestParam[1];
        } else {
            retHeight = othersBestParam[0];
            retRemaining = Math.min(myBestWidthParams[1], othersBestParam[1]); // Smallest remaining.
        }
        return new int[] {retHeight, retRemaining};
    }

    private String getKey(int offset, int width) {
        // Offset consumes 8 bits, width consumes 24 bits. Making a total of 32 bits that an int datatype can hold.
        assert offset >= 0 && offset < 1 << 8;
        assert width >= 0 && width < 1 << 24;

        int mask = offset << 24;
        mask |= width;
        return Integer.toHexString(mask);
    }

    private int[] extractOffsetAndWidth(String key) {
        int value = Integer.parseInt(key, 16);
        return new int[] {value >>> 24, value & ((1 << 24) - 1)};
    }

    private int[] longestCellHeight(int column, int width) {
        int maxHeight = 0, remaining = 0;
        for (int row = 0; row < table.length; row++) {
            int[] cellParams = getHeightWidth(table[row][column], width);
            int thisCellHeight = cellParams[0];
            if (thisCellHeight > maxHeight) {
                remaining = cellParams[2];
                maxHeight = thisCellHeight;
            }
        }
        return new int[] {maxHeight, remaining};
    }

    private int[] getHeightWidth(Cell cell, int width) {
        int length = 0;
        int height = 1;
        int maxLength = 0;
        int lastLineLength = 0;
        boolean first = true;
        for (int tokenLength : cell.getTokenLengths()) {
            length += tokenLength;
            if (first)
                first = false;
            else
                length++; // For adding a space character.

            if (length > width) {
                height++;
                length = tokenLength;
            }
            maxLength = Math.max(maxLength, length);
            lastLineLength = length;
        }
        return new int[] {height, maxLength, /* remaining length */ width - lastLineLength};
    }

    public static class Cell {

        private final int[] tokenLengths;
        private final int horizontalLength;
        private final int verticalLength;

        public Cell(int[] tokenLengths) {
            validate(tokenLengths);
            this.horizontalLength = IntStream.of(tokenLengths).sum() + tokenLengths.length - 1; // Also caters for single spaces in between.
            this.verticalLength = IntStream.of(tokenLengths).max().getAsInt();
            this.tokenLengths = tokenLengths;
        }

        private void validate(int[] tokenLengths) {
            assert tokenLengths != null : "Token lengths array must not be null";
            assert tokenLengths.length != 0 : "Token length array must be non-empty";
            assert IntStream.of(verticalLength).min().getAsInt() >= 0 : "None of the lengths of tokens must be negative";
        }

        public int getVerticalLength() {
            return verticalLength;
        }

        public int getHorizontalLength() {
            return horizontalLength;
        }

        public int[] getTokenLengths() {
            return tokenLengths;
        }
    }
}
