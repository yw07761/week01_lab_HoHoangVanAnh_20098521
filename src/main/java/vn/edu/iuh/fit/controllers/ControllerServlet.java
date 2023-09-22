package vn.edu.iuh.vn.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.edu.iuh.fit.entities.Account;
import vn.edu.iuh.fit.entities.Log;
import vn.edu.iuh.fit.services.AccountServices;
import vn.edu.iuh.fit.services.LogServices;
import vn.edu.iuh.vn.entities.Account;
import vn.edu.iuh.vn.entities.Log;
import vn.edu.iuh.vn.services.AccountServices;
import vn.edu.iuh.vn.services.LogServices;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

    @WebServlet(name = "ControllerServlet", value = "/ControllerServlet")
    public class ControllerServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        private AccountServices accountServices;
        private LogServices logServices;
        private int log_id;

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setContentType("text/html");
            String action = request.getParameter("action");

            if ("login".equals(action)) {
                if (handleLogin(request, response)) {
                    request.getRequestDispatcher("/user.jsp").forward(request, response);
                }
            } else if ("logout".equals(action)) {
                handleLogout(request, response);
            } else if ("View As Admin".equals(action)) {
                handleViewAdmin(request, response);
            }
        }

        private boolean handleLogin(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, RemoteException {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            accountServices = new AccountServices();
            Account account = accountServices.checkAccount(email, password);
            String json = convertObjectToJson(account);
            request.setAttribute("account", json);

            if (account == null) {
                return false;
            } else {
                Log log = new Log(account.getAccountID());
                request.setAttribute("log_account", log.getAccountID());
                logServices = new LogServices();
                log_id = logServices.insertLog(log);
                return true;
            }
        }

        private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            logServices.updateLogoutTime(log_id);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }

        private void handleViewAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            List<Account> listAcc = new ArrayList<>();
            listAcc = accountServices.getAllAccountActive();
            String json = convertObjectToJson(listAcc);
            request.setAttribute("listAcc", json);
            System.out.println(json);
            request.getRequestDispatcher("/admin.jsp").forward(request, response);
        }

        private String convertObjectToJson(Object o) throws JsonProcessingException {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(o);
        }
    }
