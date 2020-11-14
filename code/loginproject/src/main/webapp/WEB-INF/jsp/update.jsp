<%@ page contentType="text/html;charset=UTF-8" %>
<%@page isELIgnored="false" %>
<html>
<head>
    <title>新增简历</title>
</head>
<body>
<h4>更新简历</h4>
<form action="/resume/addResume" method="post">
    <input type="hidden" name="id" value="${resume.id}"><br><br>
    姓&nbsp;&nbsp;名：<input type="text" name="name" value="${resume.name}"><br><br>
    地&nbsp;&nbsp;址：<input type="text" name="address" value="${resume.address}"><br><br>
    电&nbsp;&nbsp;话：<input type="text" name="phone" value="${resume.phone}"><br>
    <br/>
    <input type="submit" value="更新">
</form>
<script>

</script>
</body>
</html>