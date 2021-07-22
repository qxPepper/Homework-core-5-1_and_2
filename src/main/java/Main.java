import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFile = "data.csv";
        String xmlFile = "data.xml";
        String jsonFile = "data.json";
        String jsonFile2 = "data2.json";

        //1
        List<Employee> list = parseCSV(columnMapping, csvFile);
        String json = listToJson(list);
        writeString(json, jsonFile);

        //2
        List<Employee> list1 = parseXML(xmlFile);
        String json1 = listToJson(list1);
        writeString(json1, jsonFile2);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String csvFile) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        String json = gson.toJson(list, listType);

        return json;
    }

    public static void writeString(String json, String jsonFile) throws IOException {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static List<Employee> parseXML(String xmlFile) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        StringBuilder str = new StringBuilder();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFile));

        Node root = doc.getDocumentElement();

        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                str.append(",");

                NodeList nodeList1 = element.getChildNodes();
                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node nodeCurrent = nodeList1.item(j);
                    if (Node.ELEMENT_NODE == nodeCurrent.getNodeType()) {
                        Element element1 = (Element) nodeCurrent;
                        str.append(element1.getTextContent()).append(" ");
                    }
                }
            }
        }

        String[] sttr = str.toString().split(",");

        for (int i = 0; i < sttr.length; i++) {
            String srr = sttr[i].trim();
            if (srr.length() != 0) {
                String[] ssrr = srr.split(" ");

                list.add(new Employee(
                        Long.parseLong(ssrr[0]),
                        ssrr[1],
                        ssrr[2],
                        ssrr[3],
                        Integer.parseInt(ssrr[4])));
            }
        }
        return list;
    }
}



