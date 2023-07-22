package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


class MainTest {
    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running test");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("Test complete: " + (System.currentTimeMillis() - suiteStartTime) + "ms");
    }

    @BeforeEach
    public void initTest() {
        System.out.println("Starting new test");
        testStartTime = System.currentTimeMillis();
    }

    @AfterEach
    public void finalizeTest() {
        System.out.println("Test complete: " + (System.currentTimeMillis() - testStartTime) + "ms");
    }


    @ParameterizedTest
    @CsvSource({
            "src/test/resources/test_data.csv",
    })
    void parseCSV(String filename) throws FileNotFoundException {
        List<Employee> expected = new ArrayList<>();
        expected.add(new Employee(1, "John", "Smith", "USA", 25));
        expected.add(new Employee(2, "Ivan", "Petrov", "RU", 23));
        expected.add(new Employee(3, "Maxim", "Korchagin", "RU", 28));
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> employees = Main.parseCSV(columnMapping, filename);
        Assertions.assertIterableEquals(expected, employees);
    }

    @ParameterizedTest
    @CsvSource({
            "src/test/resources/test_bad_csv.csv"
    })
    void parseBadCSV(String filename) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        Assertions.assertThrows(RuntimeException.class, () -> Main.parseCSV(columnMapping, filename));
    }

    @ParameterizedTest
    @MethodSource("fooFixture")
    void foo(String filename, Class<Exception> expectedExceptionClass) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        Assertions.assertThrows(
                expectedExceptionClass,
                () -> Main.parseCSV(columnMapping, filename)
        );
    }

    private static Stream<Arguments> fooFixture() {
        return Stream.of(
                Arguments.of("src/test/resources/test_bad_csv.csv", RuntimeException.class),
                Arguments.of("inexisting-bar-file", FileNotFoundException.class));

    }

    @ParameterizedTest
    @CsvSource({
            "src/test/resources/test_xml.xml"
    })
    void parseXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> expected = new ArrayList<>();
        expected.add(new Employee(1, "John", "Smith", "USA", 25));
        expected.add(new Employee(2, "Ivan", "Petrov", "RU", 23));
        List<Employee> actual = Main.parseXML(filename);
        Assertions.assertIterableEquals(expected, actual);
    }

    @Test
    void parseBadXML() throws ParserConfigurationException, IOException, SAXException {
        Employee expected = new Employee(1, "Jon", "Smith", "USA", 25);
        List<Employee> actual = Main.parseXML("src/test/resources/test_xml.xml");
        Assertions.assertNotEquals(expected, actual.get(0));
    }

}