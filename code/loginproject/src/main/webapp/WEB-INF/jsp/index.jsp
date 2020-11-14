<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
</head>
<body>
<h4>用户登录</h4>
<h2>我是服务器：${pageContext.request.localPort}</h2>
<h2>当前sessionId：${pageContext.session.id}</h2>
<form action="/login" method="post">
    <br/>
    用&nbsp;户&nbsp;名：<input type="text" name="username"><br/><br/>
    密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码：<input type="password" name="pwd"><br/><br/>
    <input type="submit" value="登录">
</form>
</body>
</html>