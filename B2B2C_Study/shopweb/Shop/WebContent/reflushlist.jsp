<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% String context = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>reflushlist</title>
</head>
<body>
		//这样获取页面数
	<meta http-equiv='refresh' content='0;url=<%=context %>/from/listaction!list?page=${page }'>
</body>
</html>