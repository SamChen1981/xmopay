<#escape x as (x)!>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="author" content="fyunli">

    <base id="base" href="${base}">
    <title>【xmoPAY】分布式开源聚合支付</title>

    <!-- Bootstrap core CSS -->
    <link href="static/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="static/css/main.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="static/plugins/html5shiv.min.js"></script>
    <script src="static/plugins/respond.min.js"></script>
    <![endif]-->
</head>

<body>

<!-- Begin page content -->
<div class="container">
    <div class="page-header">
        <h1>【XmoPay】分布式开源支付系统</h1>
    </div>
    <form action="/recharge/doPay" target="_blank" method="post">
    <div class="main" align="center">
        <div class="inwrap">
            <h3>#扫码测试#</h3>
            <h5>
                <input type="radio" name="amount" value="0.01" checked="checked"> 0.01 元&nbsp;&nbsp;
                <input type="radio" name="amount" value="1"> 1.00 元&nbsp;&nbsp;
                <input type="radio" name="amount" value="10"> 10.00 元&nbsp;&nbsp;
                任意: <input type="text" name="amount" id="othAmt" style="width: 60px;" value=""> 元
            </h5>
            <ul class="nav nav-tabs">
                <li role="presentation" class="active"><a href="/recharge/qrPay">扫码支付</a></li>
                <li role="presentation"><a href="/recharge/payment">网银支付</a></li>
            </ul>
            <div class="example" >
                <div class="row bank-list">
                    <div class="col-xs-6 col-sm-4 col-md-3 bank-col">
                        <div class="bank-item">
                            <label>
                                <input class="checkbox" type="radio" name="bank_code" value="ALIPAY_QRCODE" checked="checked">
                                <span class="bank-logo" id="ALIPAY_QRCODE">ALIPAY_QRCODE</span>
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-6 col-sm-4 col-md-3 bank-col">
                        <div class="bank-item">
                            <label>
                                <input class="checkbox" type="radio" name="bank_code" value="WXPAY_QRCODE">
                                <span class="bank-logo" id="WEIXIN_QRCODE">WXPAY_QRCODE</span>
                            </label>
                        </div>
                    </div>
                </div>
                <div><h3 id="vAmt" style="color: red">0.01元</h3></div>
                <div><button type="submit" class="btn btn-primary">立即支付</button></div>
            </div>
        </div>
    </div>
    </form>
</div>

<footer class="footer">
    <div class="container">
        <p class="text-muted">&copy;2018 xmopay </p>
    </div>
</footer>


<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<#--<script src="//cdn.jsdelivr.net/ie10-viewport/1.0.0/ie10-viewport.min.js"></script>-->
<script src="static/js/jquery.min.js"></script>
<script src="static/plugins/bootstrap/js/bootstrap.min.js"></script>

<script>

    function makeCode () {
        var elText = document.getElementById("othAmt");
        var amt = $.trim(elText.value);
        var vAmt = Number(amt).toFixed(2);
        if (amt == '') {
            amt = $("input[name='amount']:checked").val();
            vAmt = Number(amt).toFixed(2);
        }
        if(vAmt == 'NaN' || vAmt <= 0) {
            alert("输入金额不正确");
            $("#othAmt").val('');
            return;
        }
        $("#vAmt").text(vAmt+'元');
    }

    makeCode();

    $("input:radio").click(function () {
        $("#othAmt").val('');
        makeCode();
    });

    $("#othAmt").on("blur", function () {
        makeCode();
        $("input:radio[name='amount']").attr('checked',false);
    }).on("keydown", function (e) {
        if (e.keyCode == 13) {
            makeCode();
            $("input:radio[name='amount']").attr('checked',false);
        }
    });

</script>

</body>
</html>
</#escape>