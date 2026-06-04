package essien;

import essien.TableWrapper.Cell;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author bodmas
 */
public class TableWrapperTest {

    @Test
    public void solveWhenTableIsEmptyShouldReturnEmpty() {
        validate(new String[] {""}, 5, new int[][] {{1, 0}, {0}, {0}});
    }

    @Test
    public void solveWhenTableContainsOnlyOneCellAndItsContentFitsInTheTableWidth() {
        validate(new String[] {"a boy table inside"}, 20, new int[][] {{1, 0}, {18}, {18}});
    }

    @Test
    public void solveWhenWidthIsSmallerThanAnyTokenLengthShouldThrow() {
        Assert.assertThrows(RuntimeException.class, () -> {
                new TableWrapper(new Cell[][] {
                    {new Cell(new int[]{3, 5, 6})}
                }).wrapToWidth(2);
        });
        Assert.assertThrows(RuntimeException.class, () -> {
                new TableWrapper(new Cell[][] {
                    {new Cell(new int[]{3, 5, 6}), new Cell(new int[] {2, 1}), new Cell(new int[] {1, 3, 2})}
                }).wrapToWidth(10);
        });
    }

    @Test
    public void solveWhenTableContainsOnlyOneElementWhoseTokenSumExceedsWidthShouldReturnWidth() {
        validate(new String[] {"a boy table inside"}, 9, new int[][] {{3, 3}, {6}, {9}});
    }

    @Test
    public void solveWhenTableContainsSingleColumnWhoseRowsHaveSingleTokenOfLength1ShouldReturn1() {
        validate(new String[][] {{"a"}, {"b"}}, 5, new int[][] {{1, 0}, {1}, {1}});
    }

    @Test
    public void solveShouldProduceCorrectResultWhenWidthIsSufficientSpreadEverything() {
        String[] rowValues = {
            "in the beginning was the word and the word",
            "was with God"
        };
        validate(rowValues, 54, new int[][] {{1, 0}, {42, 12}, {42, 12}});
        validate(rowValues, 64, new int[][] {{1, 0}, {42, 12}, {42, 12}});
        validate(rowValues, 1024, new int[][] {{1, 0}, {42, 12}, {42, 12}});
    }

    @Test
    public void solveShouldProduceCorrectResultWhen() {
        String[] rowValues = {
            "in the beginning was the word and the word",
            "was with God"
        };
        validate(rowValues, 44, new int[][] {{2, 5}, {33, 8}, {36, 8}});
        validate(rowValues, 20, new int[][] {{3, 1}, {16, 4}, {16, 4}});
        validate(rowValues, 30, new int[][] {{2, 1}, {21, 8}, {22, 8}});
        validate(rowValues, 52, new int[][] {{2, 8}, {37, 8}, {41, 11}});
        validate(rowValues, 53, new int[][] {{2, 37}, {37, 12}, {41, 12}});
    }

    @Test
    public void solveShouldProduceCorrectResultWhen1() {
        String[] rowValues = {
            "was with God",
            "in the beginning was the word and the word",
        };
        validate(rowValues, 31, new int[][] {{2, 0}, {8, 21}, {10, 21}});
    }

    @Test
    public void solveShouldProduceCorrectResultWhen2() {
        String[] rowValues = {
            "was with God",
            "in the beginning was the word and the word",
            "was with God"
        };
        validate(rowValues, 41, new int[][] {{2, 1}, {8, 21, 8}, {11, 22, 8}});
        validate(rowValues, 42, new int[][] {{2, 2}, {8, 21, 8}, {11, 23, 8}});
    }

    @Test
    public void solveShouldProduceCorrectResultWhen3() {
        String[] rowValues = {
            "was with God",
            "in the beginning was the word and the word",
            "and the word was God"
        };
        validate(rowValues, 42, new int[][] {{2, 0}, {8, 21, 12}, {9, 21, 12}});
    }

    @Test
    public void solveShouldProduceCorrectResultWhen4() {
        validate(new String[] {
            "a 1 2 3 4",
            "b 1 2",
            "c"
        }, 5, new int[][]{{3, 0}, {3, 1, 1}, {3, 1, 1}});
        validate(new String[] {
            "a 1 2 3 4",
            "c",
            "b 1 2",
        }, 5, new int[][]{{3, 0}, {3, 1, 1}, {3, 1, 1}});
        validate(new String[] {
            "b 1 2",
            "a 1 2 3 4",
            "c"
        }, 5, new int[][]{{3, 0}, {1, 3, 1}, {1, 3, 1}});
        validate(new String[] {
            "c",
            "b 1 2",
            "a 1 2 3 4",
        }, 5, new int[][]{{3, 0}, {1, 1, 3}, {1, 1, 3}});
    }

    @Test
    public void solveShouldProduceCorrectResultWhen5() {
        validate(new String[] {
            "d 1 2 3",
            "e 1 2"
        }, 6, new int[][] {{2, 0}, {3, 3}, {3, 3}});
        validate(new String[] {
            "a 1 2 3 4",
            "b 1 2",
            "c",
            "d 1 2 3 4 5 6 7",
            "e 1 2 3 4 5 6"
        }, 9, new int[][] {{5, 0}, {1, 1, 1, 3, 3}, {1, 1, 1, 3, 3}});
    }

    @Test
    public void solveShouldWorkForMultiRowsAndColumns() {
        validate(new String[][] {
                {"a 1 2 3 4", "the lord is my shepherd", "i shall not want", "he makes me lie down in green pastures"},
                {"b 1 2", "he leads me beside the still waters", "he restores my soul", "he leads me in the path of righteousness"},
                {"c", "for his namesake.", "even though i walk through the valley of the shadow of death", "i fear no evil"},
                {"d 1 2 3 4 5 6 7", "for thou art with me", "thy rod and thy shaff", "they comfort me"},
                {"e 1 2 3 4 5 6", "though preparest a table before me", "in the presence of my enemies", "thou annointest my head with oil"},
                {"f 1 2 3", "my cup runneth over.", "surely, God's goodness and mercies shall follow me", "all the days of my life"},
                {"g 1 2 3 4 5 6 7 8 9", "and I shall dwell in the house of the lord", "forever and ever", "amen"}
            }, 50, new int[][] {{4, 1}, {5, 15, 16, 13}, {6, 15, 16, 13}}, true, true);
    }

    @Test
    public void showShouldDoAsItSays() {
        new TableWrapper(new String[][] {
            {"a 1 2 3 4", "the lord is my shepherd", "i shall not want", "he makes me lie down in green pastures"},
            {"b 1 2", "he leads me beside the still waters", "he restores my soul", "he leads me in the path of righteousness"},
            {"c", "for his namesake.", "even though i walk through the valley of the shadow of death", "i fear no evil"},
            {"d 1 2 3 4 5 6 7", "for thou art with me", "thy rod and thy shaff", "they comfort me"},
            {"e 1 2 3 4 5 6", "though preparest a table before me", "in the presence of my enemies", "thou annointest my head with oil"},
            {"f 1 2 3", "my cup runneth over.", "surely, God's goodness and mercies shall follow me", "all the days of my life"},
            {"g 1 2 3 4 5 6 7 8 9", "and I shall dwell in the house of the lord", "forever and ever", "amen"}
        }).show(50);
    }

    private void validate(String[] rowValues, int width, int[][] individualWidths) {
        validate(rowValues, width, individualWidths, true, false);
    }

    private void validate(String[][] rowValues, int width, int[][] individualWidths) {
        validate(rowValues, width, individualWidths, true, false);
    }

    private void validate(String[] rowValues, int width, int[][] individualWidths, boolean validate, boolean shouldPrint) {
        validate(new String[][] { rowValues }, width, individualWidths, validate, shouldPrint);
    }

    private void validate(String[][] tableValues, int width, int[][] individualWidths, boolean validate, boolean shouldPrint) {
        TableWrapper markdownTable = new TableWrapper(tableValues);

        int[][] maximumWidths = markdownTable.wrapToWidth(width);
        if (validate) {
            Assert.assertArrayEquals(maximumWidths, individualWidths);
        }
        if (shouldPrint) {
            System.out.println("Width: " + width);
            Arrays.stream(maximumWidths).map(Arrays::toString).forEach(System.out::println);
            Arrays.stream(maximumWidths).skip(1).forEach(arr -> markdownTable.show(width, arr));
        }
    }
}