<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Keno Grabbing MT</title>
</head>
<body>

	<!-- <a href="<c:url value="/Keno.jsp" />" target="_blank"> Keno Grab</a>
	</br> -->
	<a href="https://www.maltco.com/keno/QuickKeno_Today_Results.php"
		target="_blank">QuickKeno MT</a>
	</br>
	<form method="GET" action="<c:url value="/KenoGrap" />" target="_blank">
		<input type="text" name="phpid" value="" size="30">
		<input type="submit" value="START">
	</form>
	</br>	
	<form method="GET" action="<c:url value="/CheckResult" />" target="_blank">
		<input type="hidden" name="type" value="MT" >
		<input type="submit" value="Show Keno MT">
	</form>
	</br>	
	<!-- <form method="GET" action="<c:url value="/CheckResult" />" target="_blank">
		<input type="hidden" name="type" value="iLotto" >
		<input type="submit" value="Check iLotto">
	</form>
	</br> -->
	<form method="GET" action="<c:url value="/CheckResult" />" target="_blank">
		<input type="hidden" name="type" value="keno" >
		<input type="submit" value="Check Keno">
	</form>
	</br>
	<!-- <form method="GET" action="<c:url value="/CheckResult" />" target="_blank">
		<input type="hidden" name="type" value="restart" >
		<input type="submit" value="Show Restart Result">
	</form> -->
</body>
</html>