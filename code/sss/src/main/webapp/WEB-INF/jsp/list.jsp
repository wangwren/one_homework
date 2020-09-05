<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>简历列表</title>
</head>
<body>
<h4>简历列表</h4>
<button onclick="insertResume()">新增</button>
<div class="row margin-top-20">
    <table class="table" border="1">
        <thead>
        <tr>
            <th class="seq">id</th>
            <th>姓名</th>
            <th>地址</th>
            <th>电话</th>
            <th>删除</th>
            <th>修改</th>
        </tr>
        </thead>
        <tbody id="resumeTable">
        </tbody>
    </table>
</div>
<script src="js/jquery.min.js"></script>
<script>
    window.onload =function()
    {
        // 发送ajax请求
        $.ajax({
            url: '/resume/queryAll',
            type: 'POST',
            data: '',
            contentType: 'application/json;charset=utf-8',
            dataType: 'json',
            success: function (data) {
                var str1 = "";
                //清空table中的html
                $("#resumeTable").html("");
                for(var i = 0;i<data.length;i++){
                    str1 = "<tr>" +
                        "<td>"+data[i].id + "</td>" +
                        "<td>"+data[i].name + "</td>" +
                        "<td>"+data[i].address + "</td>" +
                        "<td>"+data[i].phone + "</td>" +
                        "<td><a onclick=deleteResume(" + data[i].id + ")>删除</a></td>" +
                        "<td><a onclick=updateResume(" + data[i].id + ")>修改</a></td>" +
                        "</tr>";
                    $("#resumeTable").append(str1);
                };
            }
        });
    }

    /**
     * 根据id删除数据
     */
    function deleteResume(id){
        // 发送ajax请求
        $.ajax({
            url: '/resume/deleteById?id=' + id,
            type: 'GET',
            data: id,
            contentType: 'application/json;charset=utf-8',
            dataType: 'json',
            success: function (data) {
                alert("删除成功！");
                window.location.href="/list"
            }
        });
    }

    /**
     * 新增
     */
    function insertResume(){
        window.location.href="/insert"
    }

    /**
     * 更新
     */
    function updateResume(id){
        window.location.href="/resume/queryInfo?id=" + id;
    }

</script>
</body>
</html>