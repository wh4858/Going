package controller;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;

import model.Booking;
import model.Business;
import model.Picture;
import model.Room;
import service.RoomDao;
import util.DateParse;

public class RoomController extends MskimRequestMapping{

	private static Map<String, Object> map = new HashMap<String, Object>();
	
	// 객실정보 페이지
	
	@RequestMapping("roomlist")
	public String List(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		  
		String bu_email =(String)session.getAttribute("bu_email");
		
		Map<Integer, Object> map = new HashMap<>();
		
		RoomDao rd = new RoomDao();
		
		// business 메일을 사용하는 사업자의 객실 리스트 저장
		List<Room> list = rd.roomList(bu_email);
		List<Picture> picList = new ArrayList<Picture>();
		
		
		for(Room room : list) {
			picList = rd.selectPic(room.getPic_num());
			map.put(room.getRo_num(), picList.get(0).getLocation().trim());
		}
		request.setAttribute("picMap", map);
		request.setAttribute("list", list);
			
		return "/view/entrepreneur/roomlist.jsp";
	}
	
	
	// 객실등록 페이지
	@RequestMapping("roomInsert")
	public String roomInsert(HttpServletRequest request, HttpServletResponse response) {
		
		
		return "/view/entrepreneur/roomInsert.jsp";
	}
	
	@RequestMapping("roomInsertPro")
	public String roomInsertPro(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession();
		  
		String bu_email =(String)session.getAttribute("bu_email");
		
		RoomDao rd = new RoomDao();
		Room room = new Room();
		Picture p = new Picture();
		
		int roomNum = rd.nextRoNum();
		int picNum = rd.nextPicNum();
		
		room.setRo_num(roomNum);
		room.setRo_name(request.getParameter("roName"));
		room.setBu_email(bu_email);
		room.setRo_price(request.getParameter("roPrice"));
		room.setCheckin(request.getParameter("checkIn"));
		room.setCheckout(request.getParameter("checkOut"));
		room.setRo_count(request.getParameter("roCount"));
		room.setRo_info(request.getParameter("roomInfo"));
		room.setPic_num(picNum);
		
		String[] picList = request.getParameter("picLocation").split("\\n");
		
		// p객체에 저장된 값을 picture table에 저장
		for (String pic : picList) {
			p = new Picture(picNum, pic.trim());
			rd.insertPicture(p);
		}
		// room객체에 저장된 값을 room table에 저장
		int rnum = rd.insertRoom(room);
		
		String msg = "객실 등록시 오류가 발생했습니다.";
		String url = request.getContextPath() + "/room/roomInsert?bu_email="+bu_email;
		
		if(rnum > 0) {
			msg = "객실 등록이 완료되었습니다.";
			url = request.getContextPath() + "/room/roomlist?bu_email="+bu_email;
		}
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);

		return "/view/alert.jsp";
		
	}
	
	
	@RequestMapping("roominfo")
	public String roominfo(HttpServletRequest request, HttpServletResponse response) {
		RoomDao rd = new RoomDao();
		int ro_num = Integer.parseInt(request.getParameter("ro_num"));
		Room room = rd.selectRoom(ro_num);
		
		List<Picture> picList = rd.selectPic(room.getPic_num());
		List<String> p_list = new ArrayList<String>();
		
		for(int i=0; i<picList.size();i++) {
			p_list.add(picList.get(i).getLocation());
		}
		
		
		// 선택한 객실의 정보를 가져와서 저장
		
		String info = room.getRo_info().replace("\r\n", "<br/>");
		
		request.setAttribute("p_list", p_list);
		request.setAttribute("room", room);
		request.setAttribute("ro_num", ro_num);
		request.setAttribute("pic_num", room.getPic_num());
		request.setAttribute("info", info);
		
		return "/view/entrepreneur/roominfo.jsp";
	}
	
	
	@RequestMapping("roomUpdate")
	public String roomUpdate(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		  
		String bu_email =(String)session.getAttribute("bu_email");
		 
		 
		int ro_num = Integer.parseInt(request.getParameter("ro_num"));
		int pic_num = Integer.parseInt(request.getParameter("pic_num"));
		
		RoomDao rd = new RoomDao();
		String pic = "";
		
		Room room = rd.selectRoom(ro_num);
		List<Picture> piclist = rd.selectPic(pic_num);
		
		for(Picture p : piclist) {
			pic += p.getLocation()+"\n";
		}
		
		request.setAttribute("pic_num", pic_num);
		request.setAttribute("room", room);
		request.setAttribute("ro_num", ro_num);
		request.setAttribute("pic", pic);
		
		return "/view/entrepreneur/roomUpdate.jsp";
	}
	
	
	@RequestMapping("roomUpdatePro")
	public String roomUpdatPro(HttpServletRequest request, HttpServletResponse response) {
		
		
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		HttpSession session = request.getSession();
		  
		String bu_email =(String)session.getAttribute("bu_email");
		 
		
		int ro_num = Integer.parseInt(request.getParameter("ro_num"));
		int pic_num = Integer.parseInt(request.getParameter("pic_num"));
		
		String path = getServletContext().getRealPath("/") + "/roomimgupload/";
		int size = 10 * 1024 * 1024;
		MultipartRequest multi = null;
		try {
			multi = new MultipartRequest(request, path, size, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		RoomDao rd = new RoomDao();
		Room room = rd.selectRoom(ro_num);
		int p = rd.deleteLocation(pic_num);
		
		room.setRo_name(multi.getParameter("roName"));
		room.setBu_email(bu_email);
		room.setRo_price(multi.getParameter("roPrice"));
		room.setCheckin(multi.getParameter("checkIn"));
		room.setCheckout(multi.getParameter("checkOut"));
		room.setRo_count(multi.getParameter("roCount"));
		room.setRo_info(multi.getParameter("roomInfo"));
		
		String[] picList = multi.getParameter("picLocation").split("\n");
		Picture picLocation = null;
		int pic = 0;
		for(String lo : picList) {
			picLocation = new Picture(pic_num,lo);

			pic = rd.insertPicture(picLocation);
		}
		
		// room객체에 저장된 값을 room table에 저장
		int rnum = rd.updateRoom(room);
		
		
		String msg = "객실 수정시 오류가 발생했습니다.";
		String url = request.getContextPath() + "/room/roomUpdate?ro_num="+ro_num+"&pic_num="+pic_num;
		
		
		if(rnum > 0 && pic > 0) {
			msg = "객실 수정이 완료되었습니다.";
			url = request.getContextPath() + "/room/roomlist?bu_email="+bu_email;
		}
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		
		
		return "/view/alert.jsp";
	}
	
	
	@RequestMapping("roomDelete")
	public String roomDelete(HttpServletRequest request, HttpServletResponse response) {
		
		String ro_num = request.getParameter("ro_num");
		
		request.setAttribute("ro_num", ro_num);
		
		return "/view/entrepreneur/roomDelete.jsp";
	}
	
	
	@RequestMapping("roomDeletePro")
	public String roomDeletePro(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		String bu_email =(String)session.getAttribute("bu_email");
		
		String pwd = request.getParameter("pwd");
		String ro_num = request.getParameter("ro_num");
		
		RoomDao rd = new RoomDao();
		map.clear();
		map.put("bu_email", bu_email);
		map.put("ro_num", ro_num);
		// 사업자 비밀번호 찾기
		Business business = rd.selectBu(bu_email);
		int room = 0;
		
		String msg = "객실 삭제시 오류가 발생했습니다.";
		String url = request.getContextPath() + "/room/roomDelete?ro_num="+ro_num;
		
		if(pwd == null || pwd.equals("") || !pwd.equals(business.getBu_password())) {
			msg = "비밀번호가 틀렸습니다.";
		}else {
			// 비밀번호가 일치하면 객실 삭제
			room = rd.deleteRoom(map);
		}
		
		if(room > 0) {
			msg = "객실 삭제가 완료되었습니다.";
			url = request.getContextPath() + "/room/roomlist?bu_email="+bu_email;
		}
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		
		
		return "/view/alert.jsp";
	}
	
	@RequestMapping("reservation")
	public String reservation(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		 
		String bu_email =(String)session.getAttribute("bu_email");
		
		RoomDao rd = new RoomDao();		
				
		// 페이지 번호
		int pageInt;
		// 한페이지에 출력할 게시글 갯수
		int limit = 10;
		String pageNum;
		
		// pageNum을 세션에 저장해서 작업후 뒤로가기할때 바로전에 보던 페이지 출력
		if(request.getParameter("pageNum") != null){
			session.setAttribute("pageNum", request.getParameter("pageNum"));
		}
		pageNum = (String) session.getAttribute("pageNum");
		if(pageNum == null)
			pageNum = "1";
				
		pageInt = Integer.parseInt(pageNum);
				
		// 한페이지에 출력할 게시글 rownum의 번호
		int startPage = (pageInt-1)*limit + 1;
		int endPage = (pageInt-1)*limit + limit;
//		게시글 갯수
		int count = 0;
		
		// =========== 현재 시간 ==============
		LocalDate now = LocalDate.now();
		// 포맷 정의
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		// 포맷 적용
		String nowDay = now.format(formatter);
		
		map.clear();
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("nowDay", nowDay);
		map.put("bu_email", bu_email);
		
		// 예약 내역 찾기
		String searchName = request.getParameter("searchName");
		String search = request.getParameter("search");
		List<Booking> bk = new ArrayList<Booking>();
		
		System.out.println("searchName : " + searchName);
		System.out.println("search : " + search);
//		검색할 컬럼이름
		map.put("searchName", searchName);
//		검색할 컬럼 값
		map.put("search", search);
		
		if("".equals(searchName) || searchName == null || search == null || "".equals(search)) {
			bk = rd.selectBkList(map);
			count = rd.countBoard(map);
		}
		else if("status".equals(searchName)) {
			if("예약완료".equals(search)) {
				map.put("status", "1");
			}
			else if("결제취소".equals(search)) {
				map.put("status", "2");
			}
			else if("이용완료".equals(search)) {
				map.put("status", "3");
			}
			else if("입실완료".equals(search)) {
				map.put("status", "4");
			}
			else {
				String msg = "예약완료, 결제취소, 이용완료 , 입실완료중 하나를 입력하세요.";
				String url = request.getContextPath()+"/room/reservation";
				request.setAttribute("msg", msg);
				request.setAttribute("url", url);
				return "/view/alert.jsp";
			}
			bk = rd.searchStatus(map);
			count = rd.countBoardStatus(map);
		}
		else {
			bk = rd.searchName(map);
			count = rd.countBoardSearchName(map);
		}
		
		System.out.println("bk : " + bk);
		
		// -----------------------------------------------------------------------------
		// 게시글 갯수를 확인하는 메서드
		
		int boardNum = count - (pageInt - 1) *limit;
		
		int bottomLine = 3;
		int startNum = (pageInt - 1) / bottomLine * bottomLine + 1;
		int endNum = startNum + bottomLine - 1;
		
		int maxNum = (count / limit) + (count % limit == 0 ? 0 : 1);
		if(endNum >= maxNum){
			endNum = maxNum;
		}
		
		request.setAttribute("search", search);
		request.setAttribute("searchName", searchName);
		request.setAttribute("bk", bk);
		request.setAttribute("boardNum", boardNum);
		request.setAttribute("bottomLine", bottomLine);
		request.setAttribute("startNum", startNum);
		request.setAttribute("endNum", endNum);
		request.setAttribute("maxNum", maxNum);
		request.setAttribute("pageInt", pageInt);
		
		return "/view/entrepreneur/reservation.jsp";
	}
	
	@RequestMapping("sales")
	public String sales(HttpServletRequest request, HttpServletResponse response) {
	
		HttpSession session = request.getSession();
		
		String bu_email =(String)session.getAttribute("bu_email");
		String[] month = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
		RoomDao rd = new RoomDao();
		map.clear();
		map.put("bu_email", bu_email);
		String result = "";
		
		for(String mon : month) {
			map.put("mon", mon);
			Booking bo = rd.selectSales(map);
			System.out.println("bo : "+bo);
			if(result!="") { 
				result += ","; 
			}

			if(bo == null) {
				result += "['"+mon+"월', "+"0"+"]";

			}
			else {
				result += "['"+mon+"월', "+bo.getPrice()+"]";
			}
		}
		
		request.setAttribute("result", result);

		return "/view/entrepreneur/sales.jsp";
	}
	
	@RequestMapping("areaSales")
	public String areaSales(HttpServletRequest request, HttpServletResponse response) {
		
		map.clear();
		
		String[] areas = {"서울", "경기", "강원", "부산"};
		String month = request.getParameter("month");
		RoomDao rd = new RoomDao();
		
		if(month == null) {
			LocalDate now = LocalDate.now();
			int month1 = now.getMonthValue();
			month = "0"+month1;
		}
		
		String result = "";
		map.put("month", month);
		for(String area : areas) {
			map.put("area", area);
			Booking bo = rd.selectAreaSales(map);
			if(result!="") { 
				result += ","; 
			}
			if(bo == null) {
				result += "['"+area+"', "+"0"+"]";
			}
			else {
				result += "['"+area+"', "+bo.getPrice()+"]";
			}
		}
		
		request.setAttribute("month", month);
		request.setAttribute("result", result);
		
		return "/view/entrepreneur/areaSales.jsp";
	}
	
	
	@RequestMapping("map")
	public String map(HttpServletRequest request, HttpServletResponse response) {
		
		DateParse dp = new DateParse();
		
		List<Business> addressList = new ArrayList<Business>();
		RoomDao rd = new RoomDao();
		String bu_id = request.getParameter("bu_id");
		String ro_count = request.getParameter("ro_count");
		String checkin = request.getParameter("checkin");
		String checkout = request.getParameter("checkout");
		String bu_address = request.getParameter("bu_address");
		
				
		map.clear();
		if(bu_address == null  || bu_id == null || checkin == null ||  checkout == null) {
			bu_address = "서울";
			bu_id = "1";
			checkin = dp.strToDate(dp.getTodayPlus(0));
			checkout = dp.strToDate(dp.getTodayPlus(1));
		}
		map.put("bu_address", bu_address);
		map.put("bu_id", bu_id);
		
		String resultAddress = "[";
		String roomTitle = "[";
		String roomPic = "[";
		String bu_email = "[";
		addressList = rd.addressList(map);
		for(int i = 0 ; i < addressList.size(); i++) {
			if(i == addressList.size()-1) {
				resultAddress += "'"+addressList.get(i).getBu_address().trim()+"'";
				roomTitle += "'"+addressList.get(i).getBu_title().trim()+"'";
				roomPic += "'"+addressList.get(i).getLocation().trim()+"'";
				bu_email += "'"+addressList.get(i).getBu_email().trim()+"'";
			}
			else {
				resultAddress += "'"+addressList.get(i).getBu_address().trim()+"', ";
				roomTitle += "'"+addressList.get(i).getBu_title().trim()+"', ";
				roomPic += "'"+addressList.get(i).getLocation().trim()+"', ";
				bu_email += "'"+addressList.get(i).getBu_email().trim()+"', ";
			}
		}
		resultAddress += "]";
		roomTitle += "]";
		roomPic += "]";
		bu_email += "]";
		
		
		request.setAttribute("resultAddress", resultAddress);
		request.setAttribute("roomTitle", roomTitle);
		request.setAttribute("roomPic", roomPic);
		request.setAttribute("bu_email", bu_email);
		request.setAttribute("bu_id", bu_id);
		request.setAttribute("ro_count", ro_count);
		request.setAttribute("checkin", checkin);
		request.setAttribute("checkout", checkout);
		request.setAttribute("bu_address", bu_address);
		return "/view/entrepreneur/map.jsp";
	}
	
	
	@RequestMapping("todayCheckin")
	public String todayCheckin(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		  
		String bu_email =(String)session.getAttribute("bu_email");
		RoomDao rd = new RoomDao();
		DateParse dp = new DateParse();
		String checkin = dp.getTodayPlus(0);
		
		map.clear();
		map.put("bu_email", bu_email);
		map.put("checkin", checkin);
		
		List<Booking> notCheckin = rd.selectNotCheckin(map);
		List<Booking> checkinOk = rd.selectcheckinOk(map);
		
		
		
		request.setAttribute("notCheckin", notCheckin);
		request.setAttribute("checkinOk", checkinOk);
		
		return "/view/entrepreneur/todayCheckin.jsp";
	}
	
	
	@RequestMapping("updateTodayCheckin")
	public String updateTodayCheckin(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		DateParse dp = new DateParse();
		String checkin = dp.getTodayPlus(0);
		String bu_email =(String)session.getAttribute("bu_email");
		String bo_num = request.getParameter("bo_num");
		System.out.println("bo_num"+bo_num);
		
		RoomDao rd = new RoomDao();
		map.clear();
		map.put("bu_email", bu_email);
		map.put("bo_num", bo_num);
		map.put("checkin", checkin);
		
		int rowCnt = rd.updateTodayCheckin(map);
		
		return "redirect:/room/todayCheckin";
	}
	
	
	@RequestMapping("todayCheckOut")
	public String todayCheckOut(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		  
		String bu_email =(String)session.getAttribute("bu_email");
		RoomDao rd = new RoomDao();
		DateParse dp = new DateParse();
		String checkout = dp.getTodayPlus(0);
		
		map.clear();
		map.put("bu_email", bu_email);
		map.put("checkout", checkout);
		
		List<Booking> notCheckOut = rd.selectNotCheckOut(map);
		List<Booking> checkOutOk = rd.selectcheckOutOk(map);
		
		
		System.out.println("checkout = "+checkout);
		System.out.println("bu_email = "+bu_email);
		System.out.println("notCheckOut = "+notCheckOut);
		System.out.println("checkOutOk = "+checkOutOk);
		
		request.setAttribute("notCheckOut", notCheckOut);
		request.setAttribute("checkOutOk", checkOutOk);
		
		return "/view/entrepreneur/todayCheckOut.jsp";
	}
	
	
	@RequestMapping("updateTodayCheckOut")
	public String updateTodayCheckOut(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		
		DateParse dp = new DateParse(); 
		String bu_email =(String)session.getAttribute("bu_email");
		String bo_num = request.getParameter("bo_num");
		String checkout = dp.getTodayPlus(0);
		System.out.println("bo_num"+bo_num);
		
		RoomDao rd = new RoomDao();
		map.clear();
		map.put("bu_email", bu_email);
		map.put("bo_num", bo_num);
		map.put("checkout", checkout);
		
//		int rowCnt = rd.updateAndDeleteTodayCheckOut(map);
		
		
		
		return "redirect:/room/todayCheckOut";
	}
}
