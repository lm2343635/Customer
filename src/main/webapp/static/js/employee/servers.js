var editingSid = null;
var names = [];

$(document).ready(function () {

    checkEmployeeSession(function () {
        loadServers(function () {
            loadHighlightDomains();
        });
    });
    
    $("#add-server-submit").click(function () {
        var name = $("#add-server-name").val();
        var address = $("#add-server-address").val();
        var remark = $("#add-server-remark").val();
        var validate = true;
        if (name == "" || name == null) {
            $("#add-server-name").parent().addClass("has-error");
            validate = false;
        } else {
            $("#add-server-name").parent().removeClass("has-error");
        }
        if (address == "" || address == null) {
            $("#add-server-address").parent().addClass("has-error");
            validate = false;
        } else {
            $("#add-server-address").parent().removeClass("has-error");
        }
        if (!validate) {
            return;
        }
        if (editingSid == null) {
            ServerManager.add(name, address, remark, function (result) {
                if (!result.session) {
                    sessionError();
                    return;
                }
                if (!result.privilege) {
                    $.messager.popup("当前用户无权限创建服务器！");
                    return;
                }
                $("#add-server-modal").modal("hide");
                $.messager.popup(result.data != null ? "新建成功！" : "新建失败，请重试！");
                loadServers();
            });
        } else {
            ServerManager.modify(editingSid, name, address, remark, function (result) {
                if (!result.session) {
                    sessionError();
                    return;
                }
                if (!result.privilege) {
                    $.messager.popup("当前用户无权限编辑服务器！");
                    return;
                }
                $("#add-server-modal").modal("hide");
                $.messager.popup(result.data ? "修改成功！" : "修改失败，请重试！");
                loadServers();
            })
        }
    });

    $("#add-server-modal").on("hidden.bs.modal", function () {
        $("#add-server-modal .input-group").removeClass("has-error");
        $("#add-server-modal input").val("");
        editingSid = null;
    });
    
});

function loadServers(done) {
    ServerManager.getAll(function (result) {
        if (!result.session) {
            sessionError();
            return;
        }

        if (!result.privilege) {
            $.messager.popup("当前账户无权限管理域名！");
            return;
        }

        $("#server-list tbody").mengularClear();
        for (var i in result.data) {
            var server = result.data[i];
            names[server.sid] = server.name;

            $("#server-list tbody").mengular(".server-list-template", {
                sid: server.sid,
                createAt: server.createAt.format(DATE_HOUR_MINUTE_FORMAT),
                updateAt: server.updateAt.format(DATE_HOUR_MINUTE_FORMAT),
                name: server.name,
                address: server.address,
                domains: server.domains,
                remark: server.remark
            });
            
            $("#" + server.sid + " .server-list-edit").click(function () {
                editingSid = $(this).mengularId();
                ServerManager.get(editingSid, function (result) {
                    if (!result.session) {
                        sessionError();
                        return;
                    }
                    if (!result.privilege) {
                        return;
                    }
                    var server = result.data;
                    fillValue({
                        "add-server-name": server.name,
                        "add-server-address": server.address,
                        "add-server-remark": server.remark
                    });
                    $("#add-server-modal").modal("show");
                });
            });

            $("#" + server.sid + " .server-list-remove").click(function () {
                var sid = $(this).mengularId();
                var name = $("#" + sid + " .server-list-name").text();
                var domains = parseInt($("#" + sid + " .server-list-domains").text());
                if (domains > 0) {
                    $.messager.popup("该服务器下有域名，无法删除！");
                    return;
                }
                $.messager.confirm("删除服务器", "确认删除服务器" + name + "吗？", function () {
                    ServerManager.remove(sid, function (result) {
                        if (!result.session) {
                            sessionError();
                            return;
                        }
                        if (!result.privilege) {
                            $.messager.popup("当前用户无权限删除服务器！");
                            return;
                        }
                        if (result.data) {
                            $("#" + sid).remove();
                            $.messager.popup("删除成功！");
                        } else {
                            $.messager.popup("删除失败！");
                        }
                    });
                });
            });
        }

        done();
    });

}

function loadHighlightDomains() {
    DomainManager.getHightlightDomains(function (result) {
        if (!result.session) {
            sessionError();
            return;
        }
        if (!result.privilege) {
            return;
        }

        $("#domain-list tbody").mengularClear();

        $("#domain-panel .panel-title").fillText({
            domains: result.data.length
        });

        for (var i in result.data) {
            var domain = result.data[i];
            var sites = domain.domains.split(",");
            var links = ""
            for (var j in sites) {
                links += "<a href='http://" + sites[j] + "' target='_blank'>" + sites[j] + "</a>";
                if (j != sites.length - 1) {
                    links += "<br>";
                }
            }

            $("#domain-list tbody").mengular(".domain-list-template", {
                did: domain.did,
                createAt: domain.createAt.format(DATE_HOUR_MINUTE_FORMAT),
                updateAt: domain.updateAt.format(DATE_HOUR_MINUTE_FORMAT),
                name: domain.name,
                domains: links,
                language: domain.language,
                resolution: domain.resolution,
                path: domain.path,
                remark: domain.remark,
                sid: domain.sid,
                server: names[domain.sid]
            });

            $("#" + domain.did + " .domain-list-remove").click(function () {
                var did = $(this).mengularId();
                $.messager.confirm("移除待处理域名", "确认从待处理域名列表中移除该域名吗？<br>从待处理域名列表中移除不会删除该域名。", function () {
                    DomainManager.setHighlight(did, false, function(result) {
                        if (!result.session) {
                            sessionError();
                            return;
                        }
                        if (!result.privilege) {
                            $.messager.popup("当前用户无权从待处理列表中移除该域名！");
                            return;
                        }
                        $("#" + did).remove();
                    });
                });
            });
        }
    });
}
