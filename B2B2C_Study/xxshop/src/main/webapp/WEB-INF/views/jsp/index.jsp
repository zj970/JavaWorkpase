<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8" />
<base href="${base}/" />
<title>首页_${site}</title>
<jsp:include page="base.jsp" />
</head>

<body class="index">
	<div class="container">
		<jsp:include page="header.jsp"></jsp:include>
		<jsp:include page="navbar.jsp"></jsp:include>
		<jsp:include page="search.jsp"></jsp:include>

		<div class="wrapper clearfix">
			<div class="sidebar f_r">
				<!--热卖商品-->
				<div class="hot box m_10">
					<div class="title">
						<h2>热卖商品</h2>
					</div>
					<div class="cont clearfix">
						<ul class="prolist">
                            <!--此处热门商品为空-->
                            <c:forEach items="${hotGoodses}" var="goods">
								<li><a href="./goods/view?goodsId=${goods.id}" target="_blank">
                                        <img src="${goods.thumbnail}" width="80" height="80" alt="" /></a>
									<p class="pro_title">
										<a title="${goods.name}" href="./goods/view?goodsId=${goods.id}" target="_blank">${goods.name}</a></p>
									<p class="brown"><b>￥${goods.price2}</b></p></li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<!--热卖商品-->
			</div>


			<h2></h2>
			<div class="main f_l">
				<!--最新商品 start-->
				<div class="box yellow m_10">
					<div class="title title3">
						<h2>
							<img src="images/front/new_product.gif" alt="最新商品" width="160"
								height="36" />
						</h2>
					</div>
					<div class="cont clearfix">
						<ul class="prolist">

							<%--<c:if test="${goodsesLasted==null}">--%>
                                <%--<h2>goodsesLasted = null</h2>--%>
                            <%--</c:if>--%>

							<c:forEach items="${goodsesLasted}" var="goods">
								<li style="overflow: hidden">
									<!--图片
										加上" target="_blank" "则跳转到新页面-->
									<a href="./goods/view?goodsId=${goods.id}" target="_blank"><img
									<%--<a href="./goods/view?goods.id=${goods.id}" target="_blank"><img--%>
										src="${goods.thumbnail}" width="170" height="170" alt="" /></a>
									<p class="pro_title">
										<!--标题-->
										<a title="" href="./goods/view?goodsId=${goods.id}" target="_blank">${goods.name}</a>
									</p>
									<p class="brown">
										惊喜价：<b>￥${goods.price2}</b>
									</p>
									<p class="light_gray">
										市场价：<s>￥${goods.price1}</s>
									</p></li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<!--最新商品 end-->

                <!--商品分类展示 start-->
                <!--商品分类统计-->
                <div class="category box">
                    <div class="title2">
                        <h2>
                            <img src="images/front/category.gif" alt="商品分类" width="155" height="36" />
                        </h2>
                    </div>
                </div>

                <table id="index_category" class="sort_table m_10" width="100%">
                    <tr>
                        <td><c:forEach items="${categories}" var="category">
                            <a href="./goods/listByCate?goodsCategoryId=${category.id}&order=sellnum" target="_blank">
                                    ${category.name}（${category.goodsNum}）</a>|
                        </c:forEach></td>
                    </tr>
                </table>


                <!--分类显示商品-->
                <c:forEach items="${categories}" var="category">
					<div class="box m_10 green" name="showGoods">
						<div class="title title3">
							<h2>
								<a href=""><strong>${category.name}</strong></a>
							</h2>
							<%--<a class="more" href="./goods/listByCate?goods.categoryId=${category.id}&order=sellnum">更多商品...</a>--%>
							<a class="more" href="./goods/listByCate?goodsCategoryId=${category.id}&order=sellnum" target="_blank">更多商品...</a>
						</div>

						<div class="cont clearfix">
							<ul class="prolist">
                                <c:forEach items="${category.goodses}" var="goods">
									<li><a href="./goods/view?goodsId=${goods.id}" target="_blank">
                                        <img src="${goods.thumbnail}" width="175" height="175" alt="" title=""></a>
										<p class="pro_title">
											<a title="${goods.name}" href="./goods/view?goodsId=${goods.id}"
                                               target="_blank">${goods.name}</a>
										</p>
										<p class="brown">
											惊喜价：<b>￥${goods.price2}</b>
										</p>
										<p class="light_gray">
											市场价：<s>￥${goods.price1}</s>
										</p></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</c:forEach>
                <!-- 分类显示商品 end-->


                <!--最新评论 start-->
				<div class="comment box m_10">
					<div class="title title3">
						<h2>
							<img src="images/front/comment.gif" alt="最新评论" width="155"
								height="36" />
						</h2>
					</div>
					<div class="cont clearfix">
						<c:forEach begin="1" end="4">
							<dl class="no_bg">
								<dt>
									<a href=""><img src="images/goods/apple.jpg" width="66"
										height="66"></a>
								</dt>
								<dd>
									<a href="goods_view.jsp">苹果（Apple）iPhone 6 (A1586) 64GB</a>
								</dd>
								<dd>
									<span class="grade"><i style="width: 42px"></i></span>
								</dd>
								<dd class="com_c">还不错</dd>
							</dl>
						</c:forEach>
					</div>
				</div>
				<!--最新评论 end-->
			</div>
		</div> 
		<jsp:include page="help.jsp"></jsp:include>
		<jsp:include page="footer.jsp"></jsp:include>
	</div>


</body>
</html>