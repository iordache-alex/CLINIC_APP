package com.uniquedeveloper.registration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doPost(javax.servlet.http.HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		
		String Email = request.getParameter("email"); 
		String Adresa = request.getParameter("adresa"); 
		String Nume = request.getParameter("name"); 
		String Prenume = request.getParameter("prenume"); 
		String Telefon = request.getParameter("telefon"); 
		
		RequestDispatcher dispatcher = null;   
		
		Connection con = null; 
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); 
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/clinica?useSSL=false", "root", "");
			PreparedStatement pst = con.prepareStatement("insert into pacient (Nume, Prenume, Adresa, Telefon, Email) values(?,?,?,?,?)");
			pst.setString(1, Nume);
			pst.setString(2, Prenume);
			pst.setString(3, Adresa);
			pst.setString(4, Telefon);
			pst.setString(5, Email);
			
			// Metodologia pentru inregistrarea unui pacient in baza de date: Stringuri cu inputul nostru -> verificari pentru a nu insera gresit parametrii -> inserare
			
			int rowCount = pst.executeUpdate();
			dispatcher = (RequestDispatcher) request.getRequestDispatcher("registration.jsp");
			if (rowCount > 0) {
				request.setAttribute("status", "success");
			}else {
				request.setAttribute("status", "failed");
			}
			
			dispatcher.forward((ServletRequest) request, response); 
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			}catch (SQLException e) {
				e.printStackTrace(); 
			}
		}
;
		
	}

}
