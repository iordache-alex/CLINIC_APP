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

@WebServlet("/managePatient")
public class ManagePatientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String pacientID = request.getParameter("pacientID");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica?useSSL=false", "root", "");

            if ("delete".equals(action)) {
                // Șterge pacient
                String query = "DELETE FROM pacient WHERE PacientID = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, pacientID);
                pst.executeUpdate();

            } else if ("edit".equals(action)) {
                // Editează pacient
                String nume = request.getParameter("nume");
                String prenume = request.getParameter("prenume");
                String email = request.getParameter("email");
                String telefon = request.getParameter("telefon");

                String query = "UPDATE pacient SET Nume = ?, Prenume = ?, Email = ?, Telefon = ? WHERE PacientID = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, nume);
                pst.setString(2, prenume);
                pst.setString(3, email);
                pst.setString(4, telefon);
                pst.setString(5, pacientID);
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