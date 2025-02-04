package com.uniquedeveloper.registration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String Email = request.getParameter("username"); 
        String Telefon = request.getParameter("password");
        HttpSession session = request.getSession();
        RequestDispatcher dispatcher = null; 
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica?useSSL=false", "root", "");

            // Verificarea autentificării utilizatorului
            PreparedStatement pst = con.prepareStatement("SELECT * FROM pacient WHERE Email = ? AND Telefon = ?");
            pst.setString(1, Email);
            pst.setString(2, Telefon);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                session.setAttribute("name", rs.getString("Email"));
                session.setAttribute("pacientID", rs.getString("PacientID"));
             

                // If the email is "admin", redirect to admin page
                if ("admin".equalsIgnoreCase(Email)) {
                	List<Map<String, String>> pacienti = new ArrayList<>();
                	String query = "SELECT PacientID, Nume, Prenume, Email FROM pacient";
                    PreparedStatement pst2 = con.prepareStatement(query);
                    ResultSet rs2 = pst2.executeQuery();

                    while (rs2.next()) {
                        Map<String, String> pacient = new HashMap<>();
                        pacient.put("ID", rs2.getString("PacientID"));
                        pacient.put("Nume", rs2.getString("Nume"));
                        pacient.put("Prenume", rs2.getString("Prenume"));
                        pacient.put("Email", rs2.getString("Email"));
                        pacienti.add(pacient);
                    }
                    request.setAttribute("pacienti", pacienti);
                    dispatcher = request.getRequestDispatcher("admin.jsp");
                    response.sendRedirect("admin");
                    
                } else {
                    // Regular user login
                    // Interogare pentru pacientul care a cheltuit cel mai mult peste media generala
                    String pacientQuery = """
                        SELECT p.Nume, p.Prenume, SUM(s.Pret * cs.Cantitate) AS total_cheltuit
                    	FROM pacient p
                    	JOIN programari pr ON p.PacientID = pr.PacientID
                    	JOIN consultatii c ON pr.ProgramareID = c.ProgramareID
                    	JOIN consultatii_servicii cs ON c.ConsultatieID = cs.ConsultatieID
                    	JOIN servicii s ON cs.ServiciuID = s.ServiciuID
                    	GROUP BY p.PacientID
                    	HAVING total_cheltuit > (
                    		SELECT AVG(total_cheltuieli)
                    		FROM (
                    				SELECT SUM(s2.Pret * cs2.Cantitate) AS total_cheltuieli
                    				FROM consultatii_servicii cs2
                    				JOIN servicii s2 ON cs2.ServiciuID = s2.ServiciuID
                    				GROUP BY cs2.ConsultatieID
                    				) subquery
                    		);

                    """;
                    PreparedStatement pacientStmt = con.prepareStatement(pacientQuery);
                    ResultSet pacientRs = pacientStmt.executeQuery();
                    if (pacientRs.next()) {
                        session.setAttribute("topPacient", pacientRs.getString("Nume") + " " + pacientRs.getString("Prenume"));
                    }

                    // Interogare pentru doctorul cu programari mai multe in medie
                    String doctorQuery = """
                        SELECT d.Nume, d.Prenume
                    		FROM doctor d
                    		JOIN programari pr ON d.DoctorID = pr.DoctorID
                    		GROUP BY d.DoctorID
                    		HAVING COUNT(pr.ProgramareID) > (
                    				SELECT AVG(numar_programari)
                    				FROM (
                    						SELECT COUNT(pr2.ProgramareID) AS numar_programari
                    						FROM programari pr2
                    						GROUP BY pr2.DoctorID
                    				) subquery
                    		);
                    """;
                    PreparedStatement doctorStmt = con.prepareStatement(doctorQuery);
                    ResultSet doctorRs = doctorStmt.executeQuery();
                    if (doctorRs.next()) {
                        session.setAttribute("topDoctor", doctorRs.getString("Nume") + " " + doctorRs.getString("Prenume"));
                    }

                    // Interogare pentru serviciul cel mai popular
                    String serviciuQuery = """
                        SELECT s.Denumire
                    		FROM servicii s
                    		WHERE s.ServiciuID = (
                    				SELECT cs.ServiciuID
                    				FROM consultatii_servicii cs
                    				JOIN consultatii c ON cs.ConsultatieID = c.ConsultatieID
                    				WHERE c.Data BETWEEN DATE_FORMAT(CURDATE(), '%Y-%m-01') AND LAST_DAY(CURDATE())
                    				GROUP BY cs.ServiciuID
                    				ORDER BY COUNT(cs.ServiciuID) DESC
                    				LIMIT 1
                    				);
                    """;
                    PreparedStatement serviciuStmt = con.prepareStatement(serviciuQuery);
                    ResultSet serviciuRs = serviciuStmt.executeQuery();
                    if (serviciuRs.next()) {
                        session.setAttribute("topServiciu", serviciuRs.getString("Denumire"));
                    }

                    dispatcher = request.getRequestDispatcher("index.jsp");
                }
            } else {
                request.setAttribute("status", "failed");
                dispatcher = request.getRequestDispatcher("login.jsp");
            }  
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
