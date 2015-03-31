package com.tcs.voice;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import com.sinotar.algorithm.Base64;

import javax.servlet.ServletContext;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.io.IOException;
import java.util.*;
import java.net.*;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;


public class CSusers extends HttpServlet 
{
    Connection con = null;
	//String connectionURL = "jdbc:pointbase:server://localhost/appboxusers";
	String connectionURL = "jdbc:pointbase:server://localhost/assistant";
	//PreparedStatement pst_update;
	PreparedStatement pst_select;
	ResultSet rs;  
    

    //@     ServerSocket def_socket = null;
    //@ 	Socket client_sock = null; 
	
//	private static Logger log = Logger.getLogger(ACSAuthServlet.class);	
	
	public void init(ServletConfig config) throws ServletException 
	{
		System.out.println("Inside CSusers Servlet init.............");
		super.init(config);
		
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException 
    {
         res.setContentType("text/html");
         System.out.println("inside doGet");
         
         System.out.println ("Screened user " + req.getParameter("users") + "\n");
         
         HttpSession session=req.getSession();
         
         session.setAttribute("ScreenedUser", req.getParameter("users"));	
         	
         	
    }
    
   
     
  }
