package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "src/data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        //System.out.println(json);
        writeString(json, "src/new_json.json");

        List<Employee> xmlList = parseXML("src/data.xml");
        //System.out.println(xmlList);

        String xmlJson = listToJson(xmlList);
        writeString(xmlJson, "src/xml_json.json");

        String task3 = readString("src/new_json.json");
        //System.out.println(task3);

        List<Employee> task3List = jsonToList(task3);
        task3List.forEach(System.out::println);
    }

    public static List<Employee> jsonToList(String json) throws ParseException {
        List<Employee> list = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        for (Object o : jsonArray) {
            JSONObject jso = (JSONObject) o;
            list.add(gson.fromJson(String.valueOf(jso), Employee.class));
        }
        return list;
        }
    public static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static List<Employee> parseCSV(String[] columnMapping, String filename) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
            //staff.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }



    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                if ("employee".equals(element.getNodeName())) {
                    NodeList childNodes = element.getChildNodes();
                    Employee employee = new Employee();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        switch (childNode.getNodeName()) {
                            case "id" -> employee.setId(Integer.parseInt(childNode.getTextContent()));
                            case "firstName" -> employee.setFirstName(childNode.getTextContent());
                            case "lastName" -> employee.setLastName(childNode.getTextContent());
                            case "country" -> employee.setCountry(childNode.getTextContent());
                            case "age" -> employee.setAge(Integer.parseInt(childNode.getTextContent()));
                        }
                    }
//                    String id = element.getElementsByTagName("id").item(0).getTextContent();
//                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
//                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
//                    String country = element.getElementsByTagName("country").item(0).getTextContent();
//                    String age = element.getElementsByTagName("age").item(0).getTextContent();

                    list.add(employee);
                }
            }
        }
       return list;
    }


    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}