<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
    <title>登录</title>
    <link rel="stylesheet" href="css/index.css">
</head>
<body>
<canvas class="cavs" width="1575" height="1337"></canvas>

<div class="loginmain">
    <div class="login-title">
        <span>登录</span>
    </div>

    <form action="/login" method="post">
        <div class="login-con">
            <div class="login-user">
                <div class="icon">
                    <img src="image/cd-icon-username.png" alt="">
                </div>
                <input type="text" name="username" placeholder="用户名" autocomplete="off" value="">
            </div>
            <div class="login-pwd">
                <div class="icon">
                    <img src="image/cd-icon-password.png" alt="">
                </div>
                <input type="password" name="pwd" placeholder="密码" autocomplete="off" value="">
            </div>
            <div class="login-btn">
                <input type="submit" value="登录">
            </div>
        </div>
    </form>
</div>

</body>
</html>

<script src="js/jquery.min.js"></script>
<script src="js/ban.js"></script>
