<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administrare Pacienți și Programări</title>
    <link href="css/admin-styles.css" rel="stylesheet" />
</head>
<body>
    <div class="container">
        <h1>Lista Pacienților</h1>

        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nume</th>
                    <th>Prenume</th>
                    <th>Email</th>
                    <th>Telefon</th>
                    <th>Acțiuni</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Map<String, String>> pacienti = (List<Map<String, String>>) request.getAttribute("pacienti");
                    if (pacienti != null && !pacienti.isEmpty()) {
                        for (Map<String, String> pacient : pacienti) {
                %>
                <tr>
                    <td><%= pacient.get("ID") %></td>
                    <td><%= pacient.get("Nume") %></td>
                    <td><%= pacient.get("Prenume") %></td>
                    <td><%= pacient.get("Email") %></td>
                    <td><%= pacient.get("Telefon") %></td>
                    <td>
                        <form action="/PROIECT/managePatient" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="pacientID" value="<%= pacient.get("ID") %>" />
                            <button type="submit" class="btn btn-danger">Șterge</button>
                        </form>
                        <button class="btn btn-warning" onclick="showEditPacientForm('<%= pacient.get("ID") %>', '<%= pacient.get("Nume") %>', '<%= pacient.get("Prenume") %>', '<%= pacient.get("Email") %>', '<%= pacient.get("Telefon") %>')">Editează</button>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="6">Nu există pacienți în baza de date.</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>

        <div id="editPacientForm" style="display:none;">
            <h2>Editează Pacient</h2>
            <form action="/PROIECT/managePatient" method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editPacientID" name="pacientID">
                <div class="form-group">
                    <label for="editPacientNume">Nume:</label>
                    <input type="text" id="editPacientNume" name="nume" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="editPacientPrenume">Prenume:</label>
                    <input type="text" id="editPacientPrenume" name="prenume" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="editPacientEmail">Email:</label>
                    <input type="email" id="editPacientEmail" name="email" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="editPacientTelefon">Telefon:</label>
                    <input type="text" id="editPacientTelefon" name="telefon" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-primary">Salvează</button>
                <button type="button" class="btn btn-secondary" onclick="hideEditPacientForm()">Anulează</button>
            </form>
        </div>

        <script>
            function showEditPacientForm(id, nume, prenume, email, telefon) {
                document.getElementById('editPacientID').value = id;
                document.getElementById('editPacientNume').value = nume;
                document.getElementById('editPacientPrenume').value = prenume;
                document.getElementById('editPacientEmail').value = email;
                document.getElementById('editPacientTelefon').value = telefon;
                document.getElementById('editPacientForm').style.display = 'block';
            }

            function hideEditPacientForm() {
                document.getElementById('editPacientForm').style.display = 'none';
            }
        </script>

        <h1>Lista Programărilor</h1>

        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>ID Programare</th>
                    <th>Data Programării</th>
                    <th>ID Pacient</th>
                    <th>ID Doctor</th>
                    <th>Acțiuni</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    List<Map<String, String>> programari = (List<Map<String, String>>) request.getAttribute("programari");
                    if (programari != null && !programari.isEmpty()) {
                        for (Map<String, String> programare : programari) {
                %>
                <tr>
                    <td><%= programare.get("ID") %></td>
                    <td><%= programare.get("Data") %></td>
                    <td><%= programare.get("PacientID") %></td>
                    <td><%= programare.get("DoctorID") %></td>
                    <td>
                        <form action="/PROIECT/manageAppointment" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="programareID" value="<%= programare.get("ID") %>" />
                            <button type="submit" class="btn btn-danger">Șterge</button>
                        </form>
                        <button class="btn btn-warning" onclick="showEditProgramareForm('<%= programare.get("ID") %>', '<%= programare.get("Data") %>', '<%= programare.get("PacientID") %>', '<%= programare.get("DoctorID") %>')">Editează</button>
                    </td>
                </tr>
                <% 
                        }
                    } else {
                %>
                <tr>
                    <td colspan="5">Nu există programări în baza de date.</td>
                </tr>
                <% 
                    }
                %>
            </tbody>
        </table>

        <div id="editProgramareForm" style="display:none;">
            <h2>Editează Programare</h2>
            <form action="/PROIECT/manageAppointment" method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editProgramareID" name="programareID">
                <div class="form-group">
                    <label for="editDataProgramare">Data Programării:</label>
                    <input type="date" id="editDataProgramare" name="dataProgramare" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="editPacientID">ID Pacient:</label>
                    <input type="text" id="editPacientID" name="pacientID" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="editDoctorID">ID Doctor:</label>
                    <input type="text" id="editDoctorID" name="doctorID" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-primary">Salvează</button>
                <button type="button" class="btn btn-secondary" onclick="hideEditProgramareForm()">Anulează</button>
            </form>
        </div>

        <h2>Adaugă Programare</h2>
        <form action="/PROIECT/manageAppointment" method="post">
            <input type="hidden" name="action" value="add">
            <div class="form-group">
                <label for="dataProgramare">Data Programării:</label>
                <input type="date" id="dataProgramare" name="dataProgramare" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="pacientID">ID Pacient:</label>
                <input type="text" id="pacientID" name="pacientID" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="doctorID">ID Doctor:</label>
                <input type="text" id="doctorID" name="doctorID" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary">Adaugă</button>
        </form>

        <script>
            function showEditProgramareForm(id, data, pacientID, doctorID) {
                document.getElementById('editProgramareID').value = id;
                document.getElementById('editDataProgramare').value = data;
                document.getElementById('editPacientID').value = pacientID;
                document.getElementById('editDoctorID').value = doctorID;
                document.getElementById('editProgramareForm').style.display = 'block';
            }

            function hideEditProgramareForm() {
                document.getElementById('editProgramareForm').style.display = 'none';
            }
        </script>
    </div>
</body>
</html>
