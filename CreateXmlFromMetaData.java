import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CreateXmlFromMetaData {

    public static void main(String... args) {
		String driver="oracle.jdbc.driver.OracleDriver";
        String jdbcURL = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "system";
        String password = "mca6";
        String sql = "select idno, firstname, lastname, address, salary, designation from EmployeeXML"; 

        try {
            // Connect to the database
			Class.forName(driver);
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Get the metadata of the result set
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create XML document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // Define root element
            Element rootElement = document.createElement("Company");
            document.appendChild(rootElement);

            // Process each row in the result set
            while (resultSet.next()) {
                // Define employee element
                Element employee = document.createElement("Employees");
                rootElement.appendChild(employee);

                // Add attribute to employee
                String idValue = resultSet.getString("idno");
                employee.setAttribute("id", idValue);

                // Iterate through each column and create corresponding element
                for (int i = 2; i <=columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);

                    Element element = document.createElement(columnName);
                    element.appendChild(document.createTextNode(columnValue));
                    employee.appendChild(element);
                }
            }

            //creating and writing to xml file

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("createFile1.xml"));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, streamResult);

            System.out.println("File saved to specified path!");

            // Close the database connection
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//create table EmployeeXML ( idno number primary key , firstname varchar2(10),lastname varchar2(10),address varchar2(10),salary number(10),designation varchar2(20));
