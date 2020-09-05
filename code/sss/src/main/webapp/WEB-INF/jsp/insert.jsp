<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>新增简历</title>
</head>
<body>
<h4>新增简历</h4>
<script src="js/jquery.min.js"></script>
<form action="/resume/addResume" method="post">
    姓&nbsp;&nbsp;名：<input type="text" name="name"><br><br>
    地&nbsp;&nbsp;址：<input type="text" name="address"><br><br>
    电&nbsp;&nbsp;话：<input type="text" name="phone"><br>
    <br/>
    <input type="submit" value="添加">
</form>
</body>
</html>