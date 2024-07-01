import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.xml.transform.OutputKeys;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CreateXmlFromDatabase {

    public static void main(String... args) {
	    String driver="oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:xe"; 
        String user = "system";
        String pass = "mca6";
        String sql = "Select idno, firstname, lastname, address, salary, designation from EmployeeXML"; 

        try {
            // Connect to the database
			Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Create XML document
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // Define root elements
            Element rootElement = document.createElement("Company");
            document.appendChild(rootElement);

            // Process each row in the result set
            while (resultSet.next()) {
                // Define employee element
                Element employee = document.createElement("Employees");
                rootElement.appendChild(employee);

                // Add attribute to employee
                Attr attribute = document.createAttribute("id");
                attribute.setValue(String.valueOf(resultSet.getInt("idno")));
                employee.setAttributeNode(attribute);

                // First name element
                Element firstname = document.createElement("firstname");
                firstname.appendChild(document.createTextNode(resultSet.getString("firstname")));
                employee.appendChild(firstname);

                // Last name element
                Element lastname = document.createElement("lastname");
                lastname.appendChild(document.createTextNode(resultSet.getString("lastname")));
                employee.appendChild(lastname);

                // Address element
                Element address = document.createElement("address");
                address.appendChild(document.createTextNode(resultSet.getString("address")));
                employee.appendChild(address);

                // Salary element
                Element salary = document.createElement("salary");
                salary.appendChild(document.createTextNode(String.valueOf(resultSet.getDouble("salary"))));
                employee.appendChild(salary);

                // Designation element
                Element designation = document.createElement("designation");
                designation.appendChild(document.createTextNode(resultSet.getString("designation")));
                employee.appendChild(designation);
            }

            // Write the content into XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("createFile.xml"));
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