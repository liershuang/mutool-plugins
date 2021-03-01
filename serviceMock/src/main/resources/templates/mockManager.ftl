<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>微服务mock</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../layui/css/layui.css" media="all">
</head>
<body>

<div class="layui-container">
    <!-- 头部说明 -->
    <div class="layui-collapse" lay-accordion="">
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">使用方法</h2>
            <div class="layui-colla-content layui-show">
                1、导入jar包，自动解析service接口；<br>
                2、选择service接口-->方法-->设置方法返回mock数据
            </div>
        </div>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">注意事项</h2>
            <div class="layui-colla-content">
                1、启动edas注册中心；<br>
                2、导入jar包时会解析pom文件下载maven依赖包，需要确保本地settings.xml配置可以下载到相应jar包；
            </div>
        </div>
    </div>

    <fieldset class="layui-elem-field">
        <legend>设置</legend>
        <div class="layui-field-box">
            <form class="layui-form" action="">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <button type="button" onclick="saveConfig()" class="layui-btn layui-btn-normal">保存</button>
                        <label class="layui-form-label" style="width:150px;">settings.xml文件路径</label>
                        <div class="layui-input-inline" style="width:350px;">
                            <input type="text" id="settingsFilePath" autocomplete="off" class="layui-input"
                                   placeholder="默认用户目录下.m2/settings.xml">
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </fieldset>



    <button type="button" class="layui-btn" id="uploadJar"><i class="layui-icon"></i>导入jar包</button>

    <div class="layui-row layui-col-space5">
        <div class="layui-col-xs4">
            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 50px;">
                <legend>service列表</legend>
            </fieldset>
            <div style='overflow:auto'>
                <table class="layui-table">
                    <tbody id="serviceList"></tbody>
                </table>
            </div>
        </div>
        <div class="layui-col-xs4">
            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 50px;">
                <legend>方法列表</legend>
            </fieldset>
            <div style='overflow:auto'>
                <table class="layui-table">
                    <tbody id="methodList"></tbody>
                </table>
            </div>
        </div>
        <div class="layui-col-xs4">
            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 50px;">
                <legend>mock数据</legend>
            </fieldset>
            <button type="button" id="saveMockData" class="layui-btn layui-btn-normal">保存</button>
            <textarea id="mockData" placeholder="请输入mock数据" class="layui-textarea" rows="23"></textarea>
        </div>
    </div>

</div>


<script src="../js/jquery-3.5.1.min.js" charset="utf-8"></script>
<script src="../layui/layui.js" charset="utf-8"></script>
<script>document.write('<script src="../js/util.js?t=' + new Date().getTime() + '" charset="utf-8"><\/script>')</script>
<script>
    layui.use(['element', 'layer'], function () {
        var element = layui.element;
        var layer = layui.layer;


        //初始化页面
        initPage();

        /**
         * 初始化页面，加载服务列表，清空方法和mock栏
         */
        function initPage() {
            //设置配置
            $.ajax({
                type: "post",
                url: "/mock/config/getConfig",
                async: false,
                success: function (data) {
                    if (data.code != "200") {
                        layer.open({title: '提示', content: data.msg, time: 1500});
                        return;
                    }
                    $("#settingsFilePath").val(data.data.settings_file_path);
                }
            });
            //加载service列表
            $.ajax({
                type: "post",
                url: "/mock/queryServiceList",
                async: false,
                success: function (data) {
                    if (data.code != "200") {
                        layer.open({title: '提示', content: data.msg, time: 1500});
                        return;
                    }
                    var serviceListHtml = organServiceListHtml(data.data);
                    $("#serviceList").html(serviceListHtml);
                }
            });
        }

        /**
         * 组织service列表html
         * @param serviceList
         * @returns {string}
         */
        function organServiceListHtml(serviceList) {
            if (serviceList == null || serviceList.length == 0) {
                return '';
            }
            var serviceListHtml = '';
            $.each(serviceList, function (index, value) {
                serviceListHtml +=
                    '<tr>' +
                    '<td class="service" serviceName="' + value.interfaceName + '">' +
                    value.interfaceName + ':' + '<input type="text" class="versionNumber" value="' + value.version + '">' +
                    '</td>' +
                    '</tr>';
            });
            return serviceListHtml;
        }


        $(".versionNumber").on('blur', function () {
            var version = $(this).val();
            var serviceName = $(this).parent().attr("serviceName");
            ;
            $.ajax({
                type: "post",
                url: "/mock/updateService",
                data: {interfaceName: serviceName, version: version},
                success: function (data) {
                    if (data.code != "200") {
                        layer.open({title: '提示', content: data.msg, time: 1500});
                        return;
                    }
                    layer.msg("设置成功");
                }
            });
        })

        //保存mock数据
        $("#saveMockData").click(function () {
            var selectedMethod = $(".selectedMethod").attr("methodFullName");
            if (!selectedMethod) {
                return;
            }
            var mockData = $("#mockData").val();
            $.ajax({
                type: "post",
                url: "/mock/saveMethodMockData",
                data: {methodFullName: selectedMethod, mockData: mockData},
                success: function (data) {
                    if (data.code != "200") {
                        layer.open({title: '提示', content: data.msg, time: 1500});
                        return;
                    }
                    layer.msg("保存成功");
                }
            });
        })

        $(".service").click(function () {
            var serviceName = $(this).attr("serviceName");
            drawMethodList(serviceName);
            //清空背景色设置当前选择的背景色为灰色
            $(".service").each(function () {
                $(this).css("background-color", '#ffffff');
            });
            $(this).css("background-color", '#ebedee');
        })

        $("#methodList").on('click', '.methodName', function () {
            var methodFullName = $(this).attr("methodFullName");
            setMethodMockData(methodFullName);
            //清空背景色设置当前选择的背景色为灰色
            $(".methodName").each(function () {
                $(this).css("background-color", '#ffffff');
                $(this).removeClass("selectedMethod");
            });
            $(this).css("background-color", '#ebedee');
            $(this).addClass("selectedMethod");
        })

        //渲染方法列表
        function drawMethodList(serviceName) {
            $.ajax({
                type: "post",
                url: "/mock/queryMethodList",
                data: {serviceName: serviceName},
                success: function (data) {
                    if (data.code != "200") {
                        layer.open({title: '提示', content: data.msg, time: 1500});
                        return;
                    }
                    var methodListHtml = organMethodListHtml(data.data);
                    $("#methodList").html(methodListHtml);
                }
            });
        }

        function setMethodMockData(methodFullName) {
            $.ajax({
                type: "post",
                url: "/mock/queryMethodMockData",
                data: {methodFullName: methodFullName},
                success: function (data) {
                    if (data.code != "200") {
                        layer.open({title: '提示', content: data.msg, time: 1500});
                        return;
                    }
                    $("#mockData").val(data.data);
                }
            });
        }

        /**
         * 组织方法列表html
         * @param methodList
         * @returns {string}
         */
        function organMethodListHtml(methodList) {
            if (methodList == null || methodList.length == 0) {
                return '';
            }
            var methodListHtml = '';
            $.each(methodList, function (index, value) {
                methodListHtml += '<tr><td class="methodName" methodFullName="' + value.methodFullName + '">' + value.methodFullName + '</td></tr>';
            });
            return methodListHtml;
        }

        //jar包导入
        layui.use('upload', function () {
            var $ = layui.jquery
                , upload = layui.upload;

            //指定允许上传的文件类型
            upload.render({
                elem: '#uploadJar'
                , url: '/mock/importJar' //改成您自己的上传接口
                , accept: 'file' //普通文件
                , done: function (res) {
                    layer.msg('上传成功');
                    window.location.reload();
                }
            });
        })

        function saveConfig() {
            var settingsFilePath = $("#settingsFilePath").val();
            if (!settingsFilePath) {
                return;
            }

            $.ajax({
                type: "post",
                url: "/mock/config/setUserSettingsFile",
                data: {settingsFilePath: settingsFilePath},
                success: function (data) {
                    if (data.code != "200") {
                        layer.open({title: '提示', content: data.msg, time: 1500});
                        return;
                    }
                    layer.msg("设置成功");
                }
            });
        }

    });


</script>

</body>
</html>