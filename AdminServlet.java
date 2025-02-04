
package com.uniquedeveloper.registration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Map<String, String>> pacienti = new ArrayList<>();
        List<Map<String, String>> programari = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica?useSSL=false", "root", "");

            // Fetch patients
            String pacientiQuery = "SELECT PacientID, Nume, Prenume, Email, Telefon FROM pacient";
            PreparedStatement pacientiStmt = con.prepareStatement(pacientiQuery);
            ResultSet pacientiRs = pacientiStmt.executeQuery();

            while (pacientiRs.next()) {
                Map<String, String> pacient = new HashMap<>();
                pacient.put("ID", pacientiRs.getString("PacientID"));
                pacient.put("Nume", pacientiRs.getString("Nume"));
                pacient.put("Prenume", pacientiRs.getString("Prenume"));
                pacient.put("Email", pacientiRs.getString("Email"));
                pacient.put("Telefon",  pacientiRs.getString("Telefon"));
                pacienti.add(pacient);
            }

            // Fetch appointments
            String programariQuery = "SELECT ProgramareID, Data_Programare, PacientID, DoctorID FROM programari";
            PreparedStatement programariStmt = con.prepareStatement(programariQuery);
            ResultSet programariRs = programariStmt.executeQuery();

            while (programariRs.next()) {
                Map<String, String> programare = new HashMap<>();
                programare.put("ID", programariRs.getString("ProgramareID"));
                programare.put("Data", programariRs.getString("Data_Programare"));
                programare.put("PacientID", programariRs.getString("PacientID"));
                programare.put("DoctorID", programariRs.getString("DoctorID"));
                programari.add(programare);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("pacienti", pacienti);
        request.setAttribute("programari", programari);
        request.getRequestDispatcher("admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica?useSSL=false", "root", "");

            if ("addAppointment".equals(action)) {
                String dataProgramare = request.getParameter("dataProgramare");
                String pacientID = request.getParameter("pacientID");
                String doctorID = request.getParameter("doctorID");

                String insertQuery = "INSERT INTO programari (Data_Programare, PacientID, DoctorID) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                insertStmt.setString(1, dataProgramare);
                insertStmt.setString(2, pacientID);
                insertStmt.setString(3, doctorID);
                insertStmt.executeUpdate();

            } else if ("deleteAppointment".equals(action)) {
                String programareID = request.getParameter("programareID");

                String deleteQuery = "DELETE FROM programari WHERE ProgramareID = ?";
                PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
                deleteStmt.setString(1, programareID);
                deleteStmt.executeUpdate();
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("admin");
    }
}