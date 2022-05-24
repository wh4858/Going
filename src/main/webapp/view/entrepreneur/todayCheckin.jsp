<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
</head>
<body>
<div style="margin:100px auto; width: 80%;">
  <nav class="navbar navbar-expand-sm bg-light navbar-light" style="width: 60%; margin:0px auto;">
  	<div style="margin: 0px auto;">
	  <ul class="navbar-nav">
	  	<li class="nav-item">
	      <a class="nav-link" href="${pageContext.request.contextPath}/room/roomInsert">객실 등록</a>
	    </li>
	    <li class="nav-item">
	      <a class="nav-link" href="${pageContext.request.contextPath}/room/roomlist">객실 정보</a>
	    </li>
	    <li class="nav-item">
	      <a class="nav-link" href="${pageContext.request.contextPath}/room/reservation">예약 확인</a>
	    </li>
	    <li class="nav-item">
	      <a class="nav-link" href="${pageContext.request.contextPath}/room/sales">매출</a>
	    </li>
	    <li class="nav-item">
	      <a class="nav-link" href="${pageContext.request.contextPath}/room/todayCheckin">체크인</a>
	    </li>
	    <li class="nav-item">
	      <a class="nav-link" href="${pageContext.request.contextPath}/room/todayCheckOut">체크아웃</a>
	    </li>
	    <li class="nav-item">
	      <a class="nav-link" href="${pageContext.request.contextPath}/room/map">지도</a>
	    </li>
	  </ul>
	  </div>
</nav>
<h2 style="text-align: center; margin: 30px 0px;">오늘 체크인목록</h2>

<div class="container col-sm-6" style="margin-top: 50px; float: left;">
  <h2 style="text-align: center;">입실 전</h2>
  <table class="table table-hover" style="margin-top: 30px; text-align: center;">
    <thead>
      <tr>
      	<th>번호</th>
     	<th>객실 이름</th>
        <th>이용인원수</th>
        <th>체크인</th>
        <th>체크아웃</th>
        <th>예약자이름</th>
        <th>핸드폰번호</th>
        <th>예약상태</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="nc" items="${notCheckin}" varStatus="i">
      <tr>
        <td>${nc.bo_num }</td>
     	<td>${nc.ro_name}</td>
        <td>${nc.ro_count}</td>
        <td>${nc.checkin}</td>
        <td>${nc.checkout}</td>
     	<td>${nc.name}</td>
        <td>${nc.tel}</td>
        <td><input type="button" class="update" value="체크인"></td> <!-- id는 유니크 값이어야하는데 반복문에 의해 동일한 id가
																생기면서 제일 먼저 발견된 1행째의 버튼에만 이벤트가 발생하는거 같습니다.-->
     </tr>
    </c:forEach>  
    </tbody>
  </table>

</div>

<div class="container col-sm-6" style="margin-top: 50px; float: right;">
  <h2 style="text-align: center;">입실완료</h2>
  <table class="table table-hover" style="margin-top: 30px; text-align: center;">
    <thead>
      <tr>
      	<th>번호</th>
     	<th>객실 이름</th>
        <th>이용인원수</th>
        <th>체크인</th>
        <th>체크아웃</th>
        <th>예약자이름</th>
        <th>핸드폰번호</th>
        <th>예약상태</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="co" items="${checkinOk}" varStatus="i">
      <tr>
      	<td>${co.bo_num }</td>
     	<td>${co.ro_name}</td>
        <td>${co.ro_count}</td>
        <td>${co.checkin}</td>
        <td>${co.checkout}</td>
     	<td>${co.name}</td>
        <td>${co.tel}</td>
        <td>입실완료</td>
      </tr>
    </c:forEach>  
    </tbody>
  </table>
</div>
</div>
<script type="text/javascript">
// 버튼 클릭시 Row 값 가져오기
$(".update").click(function(){ 
    
    const str = ""
    const tdArr = new Array();    // 배열 선언
    const checkBtn = $(this);

 	// checkBtn.parent() : checkBtn의 부모는 <td>이다.
    // checkBtn.parent().parent() : <td>의 부모이므로 <tr>이다.
    const tr = checkBtn.parent().parent();
    const td = tr.children();
    
//	테이블의 td에서 0번째위치한 td의 값 가져오기
    const no = td.eq(0).text();
    const result = confirm(no+'번의 예약상태를 변경하시겠습니까?')
    if(result){
    	location.href = '${pageContext.request.contextPath}/room/updateTodayCheckin?bo_num='+no;
    }
	console.log(no)
})
</script>
</body>
</html>