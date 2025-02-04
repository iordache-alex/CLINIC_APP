package com.uniquedeveloper.registration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/profile")
public class profile extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("name") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String pacientID = session.getAttribute("pacientID").toString(); // ID-ul pacientului
        String nume = null;
        String prenume = null;
        Double pretTotal = 0.0;
        List<String> diagnostice = new ArrayList<>();
        List<String> tratamente = new ArrayList<>();
        List<String> servicii = new ArrayList<>();
        List<Map<String, String>> programari = new ArrayList<>();
        List<Map<String, String>> doctori = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica?useSSL=false", "root", "");

            // Interogare pentru nume, prenume și total cost
            String query = """
                SELECT p.Nume, p.Prenume, 
                       (SELECT SUM(s.Pret * cs.Cantitate)
                        FROM programari pr
                        JOIN consultatii c ON pr.ProgramareID = c.ProgramareID
                        JOIN consultatii_servicii cs ON c.ConsultatieID = cs.ConsultatieID
                        JOIN servicii s ON cs.ServiciuID = s.ServiciuID
                        WHERE pr.PacientID = p.PacientID) AS PretTotal
                FROM pacient p
                WHERE p.PacientID = ?;
            """;

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, pacientID);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                nume = rs.getString("Nume");
                prenume = rs.getString("Prenume");
                pretTotal = rs.getDouble("PretTotal");
            }

            // Interogare pentru diagnostic
            String diagnosticQuery = """
                SELECT c.Diagnostic
                FROM pacient p
                JOIN programari pr ON p.PacientID = pr.PacientID
                JOIN consultatii c ON pr.ProgramareID = c.ProgramareID
                WHERE p.PacientID = ?;
            """;

            PreparedStatement diagnosticStmt = con.prepareStatement(diagnosticQuery);
            diagnosticStmt.setString(1, pacientID);
            ResultSet diagnosticRs = diagnosticStmt.executeQuery();

            while (diagnosticRs.next()) {
                diagnostice.add(diagnosticRs.getString("Diagnostic"));
            }

            // Interogare pentru tratament
            String tratamentQuery = """
                SELECT c.Tratament
                FROM pacient p
                JOIN programari pr ON p.PacientID = pr.PacientID
                JOIN consultatii c ON pr.ProgramareID = c.ProgramareID
                WHERE p.PacientID = ?;
            """;

            PreparedStatement tratamentStmt = con.prepareStatement(tratamentQuery);
            tratamentStmt.setString(1, pacientID);
            ResultSet tratamentRs = tratamentStmt.executeQuery();

            while (tratamentRs.next()) {
                tratamente.add(tratamentRs.getString("Tratament"));
            }
            // interogare pentru servicii programate/oferite
            String serviciiQuery = """
                    SELECT s.Denumire
                    FROM pacient p
                    JOIN programari pr ON p.PacientID = pr.PacientID
                    JOIN consultatii c ON pr.ProgramareID = c.ProgramareID
                    JOIN consultatii_servicii cs ON c.ConsultatieID = cs.ConsultatieID
                    JOIN servicii s ON cs.ServiciuID = s.ServiciuID
                    WHERE p.PacientID = ?;
                """;

                PreparedStatement serviciiStmt = con.prepareStatement(serviciiQuery);
                serviciiStmt.setString(1, pacientID);
                ResultSet serviciiRs = serviciiStmt.executeQuery();

                while (serviciiRs.next()) {
                    servicii.add(serviciiRs.getString("Denumire"));
                }
                
                // Interogare pentru obtinerea programarilor
                String programariQuery = """
                        SELECT p.Nume AS Pacient, p.Prenume, pr.Data_Programare, d.Nume AS Doctor, d.Prenume AS PrenumeDoctor
                		FROM pacient p
                		JOIN programari pr ON p.PacientID = pr.PacientID
                		JOIN doctor d ON pr.DoctorID = d.DoctorID
                		WHERE p.PacientID = ?
                		AND pr.Data_Programare >= CURDATE();
                    """;

                    PreparedStatement programariStmt = con.prepareStatement(programariQuery);
                    programariStmt.setString(1, pacientID);
                    ResultSet programariRs = programariStmt.executeQuery();

                    while (programariRs.next()) {
                        Map<String, String> programare = new HashMap<>();
                        programare.put("Data_Programare", programariRs.getString("Data_Programare"));
                        programare.put("Doctor", programariRs.getString("Doctor"));
                        programare.put("PrenumeDoctor", programariRs.getString("PrenumeDoctor"));
                        programari.add(programare);
                    }
                    
                    // Interogare pentru obtinerea doctorilor la care are programare pacientul
                    String doctoriQuery = """
                            SELECT DISTINCT d.Nume AS Doctor, d.Prenume, d.Specializare, d.Telefon
                            FROM programari pr
                            JOIN doctor d ON pr.DoctorID = d.DoctorID
                            WHERE pr.PacientID = ?;
                        """;

                        PreparedStatement doctoriStmt = con.prepareStatement(doctoriQuery);
                        doctoriStmt.setString(1, pacientID);
                        ResultSet doctoriRs = doctoriStmt.executeQuery();

                        while (doctoriRs.next()) {
                            Map<String, String> doctor = new HashMap<>();
                            doctor.put("Doctor", doctoriRs.getString("Doctor"));
                            doctor.put("Prenume", doctoriRs.getString("Prenume"));
                            doctor.put("Specializare", doctoriRs.getString("Specializare"));
                            doctor.put("Telefon", doctoriRs.getString("Telefon"));
                            doctori.add(doctor);
                        }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Atribuie datele pentru afișare în JSP
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("pretTotal", pretTotal);
        request.setAttribute("diagnostice", diagnostice);
        request.setAttribute("tratamente", tratamente);
        request.setAttribute("servicii", servicii);
        request.setAttribute("programari", programari);
        request.setAttribute("doctori", doctori);

        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}
