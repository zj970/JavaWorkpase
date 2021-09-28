<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/layouts/base.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>导航管理 - 猫宁Morning</title>
<link rel="stylesheet" href="${ctxsta}/common/bootstrap-table/bootstrap-table.min.css" />
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content">
  <div class="row">
    <div class="col-sm-12">
      <div class="ibox float-e-margins">
        <div class="ibox-title">
          <h5>导航管理</h5>
          <div class="ibox-tools"> <a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a class="close-link"><i class="fa fa-times"></i></a> </div>
        </div>
        <div class="ibox-content">
          <div class="row row-lg">
            <div class="col-sm-12">
              <div class="example-wrap">
                <div class="example">
                  <div id="toolbar" class="btn-group m-t-sm">
                    <shiro:hasPermission name="online:navigation:create">
                      <button type="button" class="btn btn-default"  title="创建导航" onclick="layer_show('创建导航','${ctx}/online/navigation/create','800','700')"> <i class="glyphicon glyphicon-plus"></i> </button>
                    </shiro:hasPermission>
                  </div>
                  <table id="table"
                         data-toggle="table"
                         data-height="600"
                         data-search="true"
                         data-show-refresh="true"
                         data-show-toggle="true"
                         data-show-export="true"
                         data-show-pagination-switch="true"
                         data-show-columns="true"
                         data-url="${ctx}/online/navigation/"
                         data-pagination="true"
                         data-page-size="20"
                         data-page-list="[20, 50, 100, 200]"
                         data-side-pagination="server"
                         data-striped="true"
                         data-pagination="true"
                         data-sort-order="desc"
                         data-toolbar="#toolbar">
                    <thead>
                      <tr>
                        <th data-field="navigationId" data-halign="center" data-align="center" data-sortable="true">导航编号</th>
                        <th data-field="code" data-halign="center" data-align="center" data-sortable="true">导航标志</th>
                        <th data-field="name" data-halign="center" data-align="center" data-sortable="true">导航名称</th>
                        <th data-field="status" data-formatter="statusFormatter" data-halign="center" data-align="center" data-sortable="true">状态</th> 
                        <th data-field="number" data-halign="center" data-align="center" data-sortable="true">导航栏数量</th>
                        <th data-field="showNumber" data-halign="center" data-align="center" data-sortable="true">显示数量</th>
                        <th data-field="createTime" data-formatter="timeFormatter" data-halign="center" data-align="center" data-sortable="true">创建时间</th>
                        <th data-field="createBy" data-halign="center" data-align="center" data-sortable="true">创建者</th>                 
                        <th data-field="updateTime" data-formatter="timeFormatter" data-halign="center" data-align="center" data-sortable="true">更新时间</th>
                        <th data-field="updateBy" data-halign="center" data-align="center" data-sortable="true">更新者</th>
                        <th data-field="remarks" data-halign="center" data-align="center" data-sortable="true">备注</th>
                        <th data-formatter="actionFormatter" data-events="actionEvents" data-halign="center" data-align="center" data-sortable="true">操作</th>
                      </tr>
                    </thead>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<myfooter> 
  <!-- Bootstrap table --> 
  <script src="${ctxsta}/common/bootstrap-table/bootstrap-table.min.js"></script> 
  <script src="${ctxsta}/common/bootstrap-table/extensions/export/bootstrap-table-export.js"></script> 
  <script src="${ctxsta}/common/bootstrap-table/tableExport.js"></script> 
  <script src="${ctxsta}/common/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script> 
  <!-- 自定义js --> 
  <script src="${ctxsta}/cms/js/onlineNavigation.js"></script> 
</myfooter>
</body>
</html>