<%
    if(session.getAttribute("name") == null) {
        response.sendRedirect("login.jsp");
    }
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <title>Clinica</title>
    <!-- Favicon-->
    <link rel="icon" type="image/x-icon" href="assets/favicon.ico" />
    <!-- Font Awesome icons (free version)-->
    <script src="https://use.fontawesome.com/releases/v5.15.4/js/all.js" crossorigin="anonymous"></script>
    <!-- Google fonts-->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css" />
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css" />
    <!-- Core theme CSS (includes Bootstrap)-->
    <link href="css/index-styles.css" rel="stylesheet" />
</head>
<body id="page-top">
    <!-- Navigation-->
    <nav class="navbar navbar-expand-lg bg-secondary text-uppercase fixed-top" id="mainNav">
        <div class="container">
            <a class="navbar-brand" href="index.jsp">Clinica AAA</a>
            <button class="navbar-toggler text-uppercase font-weight-bold bg-primary text-white rounded" type="button" data-bs-toggle="collapse" data-bs-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
                Menu <i class="fas fa-bars"></i>
            </button>
            <div class="collapse navbar-collapse" id="navbarResponsive">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item mx-0 mx-lg-1 bg-danger"><a class="nav-link py-3 px-0 px-lg-3 rounded" href="logout"><%= session.getAttribute("name") %></a></li>
                </ul>
            </div>
        </div>
    </nav>
    <!-- Masthead-->
    <header class="masthead bg-primary text-white text-center">
        <div class="container d-flex align-items-center flex-column">
            <!-- Tabelele pentru rezultatele fiecarui query pentru reaizarea tabelelor cu date -->
            <h2>Profilul Pacientului</h2>
            <table class="table table-bordered table-striped mt-4 table-light">
                <tr>
                    <th>Nume</th>
                    <td><%= request.getAttribute("nume") %></td>
                </tr>
                <tr>
                    <th>Prenume</th>
                    <td><%= request.getAttribute("prenume") %></td>
                </tr>
                <tr>
                    <th>Cost Total Consultatii</th>
                    <td><%= request.getAttribute("pretTotal") %></td>
                </tr>
                <tr>
                	<th> Diagnostic(e) </th>
                	<td> <%= request.getAttribute("diagnostice") %></td>
                </tr>
                 <tr>
                	<th> Tratament(e) </th>
                	<td> <%= request.getAttribute("tratamente") %></td>
                </tr>
                <tr>
                	<th> Servicii </th>
                	<td> <%= request.getAttribute("servicii") %></td>
                </tr>
            </table>
        </div>
    </header>
    
    <!--  Aici trebuie rezultatele queryurilor -->
    
    <div class="container">
    <h3>Programările Dvs.</h3>
    <table class="table table-bordered">
        <thead>
            <tr>
                <th>Data Programării</th>
                <th>Nume Doctor</th>
                <th>Prenume Doctor</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Map<String, String>> programari = (List<Map<String, String>>) request.getAttribute("programari");
                if (programari != null && !programari.isEmpty()) {
                    for (Map<String, String> programare : programari) {
            %>
            <tr>
                <td><%= programare.get("Data_Programare") %></td>
                <td><%= programare.get("Doctor") %></td>
                <td><%= programare.get("PrenumeDoctor") %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="3">Nu există programări disponibile.</td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>

	<div class="container">
    <h3>Doctorii Dvs.</h3>
    <table class="table table-bordered">
        <thead>
            <tr>
                <th>Nume Doctor</th>
                <th>Prenume Doctor</th>
                <th>Specializare</th>
                <th>Telefon</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Map<String, String>> doctori = (List<Map<String, String>>) request.getAttribute("doctori");
                if (doctori != null && !doctori.isEmpty()) {
                    for (Map<String, String> doctor : doctori) {
            %>
            <tr>
                <td><%= doctor.get("Doctor") %></td>
                <td><%= doctor.get("Prenume") %></td>
                <td><%= doctor.get("Specializare") %></td>
                <td><%= doctor.get("Telefon") %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="4">Nu există informații despre doctori disponibile.</td>  <!--  In cazul in care nu sunt doctori la care ai programare -->
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>
	
    
    
    <!-- Bootstrap core JS-->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Core theme JS-->
    <script src="js/scripts.js"></script>
</body>
</html>
