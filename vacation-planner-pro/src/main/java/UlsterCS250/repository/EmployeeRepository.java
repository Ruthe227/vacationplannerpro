package UlsterCS250.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.*;

import UlsterCS250.entities.Employee;
import UlsterCS250.viewModels.EmployeeVM;

public class EmployeeRepository {
 
    private static String dbUrl = "jdbc:postgresql://localhost:5432/auth_database";
    private static String user = "vcpp";
    private static String pass = "abc123";
    private static final Logger LOGGER = Logger.getLogger(EmployeeRepository.class.getName());


    public ArrayList<Employee> findAll() {
        ArrayList<Employee> employeesList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Employees ORDER BY employee_id")) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()){
                        Employee emp = new Employee(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        Boolean.parseBoolean(rs.getString("is_manager")),
                        Boolean.parseBoolean(rs.getString("is_active")),
                        rs.getString("last_login"),
                        rs.getString("created_at"),
                        rs.getString("department_id"),
                        rs.getString("years_of_service")
                        );
                        employeesList.add(emp);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while finding employees", e);
            e.printStackTrace();
        }
        return employeesList;
    }
    
    public boolean isUsernameUnique(String username) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Employees WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);
                return count == 0;
            }
        }
    }

    public Employee findByUsername(String username) {

        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Employees WHERE username ILIKE ?")) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("Found employee by username: " + username);
                    return new Employee(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getBoolean("is_manager"),
                        rs.getBoolean("is_active"),
                        rs.getString("last_login"),
                        rs.getString("created_at"),
                        rs.getString("department_id"),
                        rs.getString("years_of_service")
                    );
                } else {
                    LOGGER.warning("Employee not found with username: " + username);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while finding employee by username: " + username, e);
            return null;
        }
    }
    
    public void addEmployee(String username, String password) throws SQLException {
        if (!isUsernameUnique(username)) {
            throw new SQLException("Username already exists");
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Employees (username, password_hash, email, first_name, last_name, is_manager, is_active, last_login, created_at, department_id, years_of_service) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)")) {
    
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, "test@test.com");
            stmt.setString(4, "test");
            stmt.setString(5, "test");
            stmt.setBoolean(6, false);
            stmt.setBoolean(7, true);
            stmt.setInt(8, 1);
            stmt.setInt(9, 1);
    
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                LOGGER.info("Employee added successfully");
            } else {
                LOGGER.warning("Failed to add employee");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while adding employee", e);
        }
    }

    public boolean addSession(EmployeeVM employee) throws SQLException{
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Employees WHERE username = ? AND password_hash = ?")){
            stmt.setString(1, employee.getPassword());
            stmt.setString(2, employee.getPassword());
            try(ResultSet rs = stmt.executeQuery()){
                rs.next();
                int count = rs.getInt(2);
                return count == 1;
            }
        }
    }

}

