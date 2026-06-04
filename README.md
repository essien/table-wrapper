# TableWrapper

TableWrapper is a Java utility that determines how the contents of a table should be wrapped so that the table fits within a specified total width while keeping column sizing as balanced as possible.

## Features

- Calculates optimal column widths for a target table width.
- Supports tables represented as token-length metadata or raw text.
- Produces balanced wrapping across rows and columns.
- Includes a console renderer (`show`) for displaying wrapped tables.
- Comes with a JUnit test suite covering a variety of wrapping scenarios.

## Project Structure

```
src/main/java/essien/TableWrapper.java
src/test/java/essien/TableWrapperTest.java
```

## Running Tests

This repository includes JUnit-based tests in `TableWrapperTest`.

You can import the project into your preferred IDE and run the test suite directly.

## Author

Original author: bodmas