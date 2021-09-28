<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/layouts/base.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>创建广告位 - 猫宁Morning</title>
<link rel="stylesheet" href="${ctxsta}/common/icheck/flat/green.css" />
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content">
  <div class="row">
    <div class="col-sm-12">
      <div class="ibox float-e-margins">
        <div class="ibox-title">
          <h5>创建广告位<small> 广告位信息时应当遵循合法、正当、必要的原则，明示目的、方式和范围。</small></h5>
          <div class="ibox-tools"> <a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a class="close-link"><i class="fa fa-times"></i></a> </div>
        </div>
        <div class="ibox-content">
          <form id="form" class="form-horizontal" action="${ctx}/online/advert" data-method="post">
            <div class="form-group m-t">
              <label class="col-sm-2 col-xs-offset-1 control-label">广告位名称：</label>
              <div class="col-sm-7">
                <input type="text" class="form-control" name="name">
              </div>
            </div>
            <div class="hr-line-dashed"></div>
            <div class="form-group">
              <label class="col-sm-2 col-xs-offset-1 control-label">广告位标志：</label>
              <div class="col-sm-7">
                <input type="text" class="form-control" name="code">
              </div>
            </div>
            <div class="hr-line-dashed"></div>
            <div class="form-group">
              <label class="col-sm-2 col-xs-offset-1 control-label">宽度：</label>
              <div class="col-sm-7">
                <input type="text" class="form-control" name="width">
              </div>
            </div>
            <div class="hr-line-dashed"></div>    
            <div class="form-group">
              <label class="col-sm-2 col-xs-offset-1 control-label">高度：</label>
              <div class="col-sm-7">
                <input type="text" class="form-control" name="height">
              </div>
            </div>
            <div class="hr-line-dashed"></div>                       
            <div class="form-group">
              <label class="col-sm-2 col-xs-offset-1 control-label">是否可用：</label>
              <div class="col-sm-9">
                <label class="radio-inline">
                  <input type="radio" name="status" value="1" checked="checked">
                  显示</label>
                <label class="radio-inline">
                  <input type="radio" name="status" value="0">
                  隐藏</label>
                <label class="radio-inline status-tip"><strong>提示：</strong> 状态</label>
              </div>
            </div>
            <div class="hr-line-dashed"></div>
            <div class="form-group">
              <label class="col-sm-2 col-xs-offset-1 control-label">广告类型：</label>
              <div class="col-sm-9">
                <label class="radio-inline">
                  <input type="radio" name="type" value="1" checked="checked">
                  图片</label>
                <label class="radio-inline">
                  <input type="radio" name="type" value="0">
                  文本</label>
              </div>
            </div>
            <div class="hr-line-dashed"></div>            
            <div class="form-group">
              <label class="col-sm-2 col-xs-offset-1 control-label">显示数量：</label>
              <div class="col-sm-7">
                <input type="text" maxlength="10" class="form-control" name="defultNumber">
              </div>
            </div>
            <div class="hr-line-dashed"></div>            
            <div class="form-group">
              <label class="col-sm-2  col-xs-offset-1 control-label">广告位描述：</label>
              <div class="col-sm-7">
                <textarea class="form-control" rows="2" name="description" placeholder="请输入消息..."></textarea>
              </div>
            </div>
            <div class="hr-line-dashed"></div>
            <div class="form-group">
              <div class="col-sm-12 text-center">
                <button class="btn btn-primary" type="submit">提交</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>
<myfooter> 
  <!-- 自定义js --> 
  <script src="${ctxsta}/cms/js/onlineAdvert.js"></script> 
</myfooter>
</body>
</html>
