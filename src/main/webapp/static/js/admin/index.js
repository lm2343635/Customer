var redirect = decodeURIComponent(request("redirect"));

$(document).ready(function () {
    $("#admin-submit-button").click(function () {
        var name = $("#admin-number-input").val();
        var password = $("#admin-password-input").val();
        var validate = true;
        if (name == "" || name == null) {
            $("#admin-number-input").parent().addClass("has-error");
            validate = false;
        } else {
            $("#admin-number-input").parent().removeClass("has-error");
        }
        if (password == "" || password == null) {
            $("#admin-password-input").parent().addClass("has-error");
            validate = false;
        } else {
            $("#admin-password-input").parent().removeClass("has-error");
        }
        if (validate) {
            AdminManager.login(name, md5(password), function (success) {
                if (!success) {
                    $("#admin-number-input").parent().addClass("has-error");
                    $("#admin-password-input").parent().addClass("has-error");
                    $.messager.popup("用户名或密码错误");
                    return;
                }

                location.href = (redirect == null || redirect == "") ? "employees.html" : redirect;
            });
        }
    });

    $("body").keydown(function () {
        if (event.keyCode == 13) {
            $("#admin-submit-button").click();
        }
    });

});