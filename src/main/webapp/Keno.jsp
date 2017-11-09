<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>輸入驗證碼開始啟動抓資料</h1>
	<div>
		<form method="post" name="form" onsubmit="setCookie()"
			action="https://www.maltco.com/keno/QuickKeno_Today_Results.php"
			target="_blank">
			<table cellspacing="4" style="color: #FFFFFF">
				<tbody>
					<tr>
						<td><script type="text/javascript"
								src="https://www.google.com/recaptcha/api/challenge?k=6LdmrRgTAAAAADKgnOr76di5voXKzeXkFk1cJDoj"></script>
							<noscript>
								<iframe
									src="https://www.google.com/recaptcha/api/noscript?k=6LdmrRgTAAAAADKgnOr76di5voXKzeXkFk1cJDoj"
									height="300" width="500" frameborder="0"></iframe>
								<br>
								<textarea name="recaptcha_challenge_field" rows="3" cols="40"></textarea>
								<input type="hidden" name="recaptcha_response_field"
									value="manual_challenge">
							</noscript></td>
					</tr>
					<tr>
						<td><input type="submit" value="Check"></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
	<script>
		function setCookie() {
			var cookie = "PHPSESSID=83nr3593m8g18b10kgves0d245; expires=Thu, 18 Dec 2050 12:00:00 UTC; domain=www.maltco.com; path=/;";
			document.cookie = "PHPSESSID=83nr3593m8g18b10kgves0d245";
			alert('session=' + document.cookie + 'cookie=' + cookie);
		}
	</script>
</body>
</html>