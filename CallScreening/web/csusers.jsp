<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head>


	
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link rel="shortcut icon" type="image/ico" href="http://www.freelancesuite.com/images/favicon.ico">	
		<title>My Company Name1</title>		
		<link href="My%20Company%20Name1_files/styles.css" type="text/css" media="screen" rel="stylesheet">		<style type="text/css">
		img, div { behavior: url(iepngfix.htc) }
		</style>
	</head><body id="login">
		<div id="wrappertop"></div>
			<div id="wrapper">
					<div id="content">
						<div id="header">
							<h2><a href=""><img src="My%20Company%20Name1_files/logo.png" alt="Users To be Blocked"></a></h>
						</div>
						<!<div id="darkbanner" class="banner320">
							<!<h2></h2>
						<!</div>
						<!<div id="darkbannerwrap">
						</div>

<%
System.out.println("inside csusers");

System.out.println("Screened user " + request.getParameter("user_name") + "\n");

application.setAttribute("ScreenedUser", request.getParameter("user_name"));        

String oldscuser=(String)application.getAttribute("ScreenedUser");
if(oldscuser==null){

%>
No users screened by You
<br/>
<br/>
<br/>
<%
}
else{
%>
Screened User: <%= oldscuser %>
<br/><br/><br/><br/><br/><br/>
<%
}

%>						

<form name="form1" method="post" action="csusers.jsp">
						<fieldset class="form">
                        	                                 
                                                      <p>
								<label for="user_name">User1</label>
								<input name="user_name" id="user_name" type="text">
							</p>
                                                      <p>
								<label for="user_name">User2</label>
								<input name="user_name" id="user_name" type="text">
							</p>
                                                      <p>
								<label for="user_name">User3</label>
								<input name="user_name" id="user_name" type="text">
							</p>
                                                      <p>
								<label for="user_name">User4</label>
								<input name="user_name" id="user_name" type="text">
							</p>
							
							<button type="submit" class="positive" name="Submit">
								<img alt="Announcement">Submit</button>
								<ul id="forgottenpassword">
								<li class="boldtext"></li>
								<li><a href="http://www.freelancesuite.com/demo/forgot.php">  </a></li>
							</ul>
                            						</fieldset>
						
						
					</form></div>
				</div>   

<div id="wrapperbottom_branding"><div id="wrapperbottom_branding_text">By <a href="http://www.freelancesuite.com/" style="text-decoration: none;">TCS</a> <a href="http://www.freelancesuite.com/" style="text-decoration: none;">CNS Lab</a></div></div></body></html>
