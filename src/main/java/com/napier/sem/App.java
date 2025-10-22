package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App
{

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false&allowPublicKeyRetrieval=true", "root", "example");

                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Gets all the current employees and salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalariesByRole()
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary\n" +
                            "FROM employees, salaries, titles\n" +
                            "WHERE employees.emp_no = salaries.emp_no\n" +
                            "AND employees.emp_no = titles.emp_no\n" +
                            "AND salaries.to_date = '9999-01-01'\n" +
                            "AND titles.to_date = '9999-01-01'\n" +
                            "AND titles.title = 'Engineer'\n" +
                            "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public department getDepartment(int dept_no){


        return null;
    }
    public department getDepartment(String dept_name) {
        try {
            Statement stmt = con.createStatement();
            String strSelect = String.format(
                    "SELECT dept_no, dept_name FROM departments WHERE dept_name = '%s'", dept_name);

            ResultSet rset = stmt.executeQuery(strSelect);

            // Check if department exists
            if (rset.next()) {
                department dept = new department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                return dept;
            } else {
                System.out.println("Department not found: " + dept_name);
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department");
            return null;
        }
    }
    public ArrayList<Employee> getSalariesByDepartment(department dept) {
        try {
            Statement stmt = con.createStatement();
            String strSelect = String.format(
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries, dept_emp, departments " +
                            "WHERE employees.emp_no = salaries.emp_no " +
                            "AND employees.emp_no = dept_emp.emp_no " +
                            "AND dept_emp.dept_no = departments.dept_no " +
                            "AND salaries.to_date = '9999-01-01' " +
                            "AND departments.dept_no = '%s' " +
                            "ORDER BY employees.emp_no ASC",
                    dept.dept_no);

            ResultSet rset = stmt.executeQuery(strSelect);
            ArrayList<Employee> employees = new ArrayList<>();

            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                emp.dept = dept;
                employees.add(emp);
            }

            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employees by department");
            return null;
        }
    }
    public void printSalaries(ArrayList<Employee> employees) {
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees) {
            String emp_string = String.format("%-10s %-15s %-20s %-8s",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    /**

     Main entry point for the application.*/
    public static void main(String[] args) {// Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // --- Test: Get Department ---
        department dept = a.getDepartment("Sales");
        if (dept != null) {
            System.out.println("Department Found: " + dept.dept_name + " (" + dept.dept_no + ")");
        }

        // --- Test: Get all employees and salaries in that department ---
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);

        // Print size and data
        if (employees != null) {
            System.out.println("Total Employees in Department: " + employees.size());
            a.printSalaries(employees);
        }

        // Disconnect
        a.disconnect();
    }
}
