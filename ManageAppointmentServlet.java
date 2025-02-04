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

@WebServlet("/manageAppointment")
public class ManageAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica?useSSL=false", "root", "");

            if ("delete".equals(action)) {
                // Șterge programare
                String programareID = request.getParameter("programareID");
                String query = "DELETE FROM programari WHERE ProgramareID = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, programareID);
                pst.executeUpdate();

            } else if ("edit".equals(action)) {
                // Editează programare
                String programareID = request.getParameter("programareID");
                String dataProgramare = request.getParameter("dataProgramare");
                String pacientID = request.getParameter("pacientID");
                String doctorID = request.getParameter("doctorID");

                String query = "UPDATE programari SET Data_Programare = ?, PacientID = ?, DoctorID = ? WHERE ProgramareID = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, dataProgramare);
                pst.setString(2, pacientID);
                pst.setString(3, doctorID);
                pst.setString(4, programareID);
                pst.executeUpdate();

            } else if ("add".equals(action)) {
                // Adaugă programare nouă
                String dataProgramare = request.getParameter("dataProgramare");
                String pacientID = request.getParameter("pacientID");
                String doctorID = request.getParameter("doctorID");

                String query = "INSERT INTO programari (Data_Programare, PacientID, DoctorID) VALUES (?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, dataProgramare);
                pst.setString(2, pacientID);
                pst.setString(3, doctorID);
                pst.executeUpdate();
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redirectează înapoi la pagina de administrare
        response.sendRedirect("admin");
    }
}
