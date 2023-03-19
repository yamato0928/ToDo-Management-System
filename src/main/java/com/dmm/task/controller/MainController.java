package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dmm.task.Service.AccountUserDetails;
import com.dmm.task.entity.Tasks;
import com.dmm.task.entity.Users;
import com.dmm.task.repository.TasksRepository;
import com.dmm.task.repository.UsersRepository;


@Controller
public class MainController {
	
	@Autowired
	private UsersRepository userRepo;
	@Autowired
	private TasksRepository taskRepo;
	
	
	
	@GetMapping("/main")
	public String main(Model model, @AuthenticationPrincipal AccountUserDetails loginUser, @RequestParam(name = "date", required = false) String paramDate) {		
		//日付を格納するリスト
		List<LocalDate> weekList = new ArrayList<>(); //1週間分の日付を格納するリスト（週リスト）
		List<List<LocalDate>> monthList = new ArrayList<>(); //1月分の日付を格納する二次元リスト（月リスト）
		//処理時の日付を取得する
		LocalDate today = LocalDate.now();
		//パラメータがある場合はそちらを優先する
		if(paramDate != null) {
			today = LocalDate.parse(paramDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		//その月の１日と最終日を取得する
		LocalDate firstDay = LocalDate.of(today.getYear(), today.getMonth(), 1); //その月の１日
		LocalDate lastDay = LocalDate.of(today.getYear(), today.getMonth(), today.lengthOfMonth()); //その月の最終日
		//容疑を表すDayOfWeekの取得
		DayOfWeek dayOfWeekOfFirstDay = firstDay.getDayOfWeek();
		DayOfWeek dayOfWeekOfLastDay = lastDay.getDayOfWeek();
		//カレンダーの1行目の日曜日の日付を取得する
		LocalDate firstSunDay = firstDay.minusDays(dayOfWeekOfFirstDay.getValue());
		if(dayOfWeekOfFirstDay.equals(DayOfWeek.SUNDAY)) {
			//月のはじめが日曜日の場合の特別処理
			firstSunDay = firstDay;
		}
		//カレンダーの最終行の土曜日の日付を取得する
		LocalDate lastSatarDay = lastDay.plusDays(6-dayOfWeekOfLastDay.getValue());
		if(dayOfWeekOfLastDay.equals(DayOfWeek.SUNDAY)) {
			//月の終わりが日曜日の場合の特別処理
			lastSatarDay = lastDay.plusDays(6);
		}
		//カレンダーに日付を格納していく
		LocalDate date = firstSunDay;
		while(!date.equals(lastSatarDay)) {
			//週リストに追加
			weekList.add(date);
			//1週間分の日付が集まったら月リストに格納、その後週リストをリセットする
			if(weekList.size() == 7) {
				monthList.add(weekList);
				weekList = new ArrayList<>();
			}
			date = date.plusDays(1);
		}
		//最終日、最終週を追加
		weekList.add(date);
		monthList.add(weekList);
		
		//ビューに受け渡す
		model.addAttribute("matrix", monthList);
		
		//ここまでで日付を生成する
		//ここから予定を出力する
		List<Tasks> allTasks = taskRepo.findAll();
		for(Tasks allTask:allTasks) {
			System.out.println(allTask.getDate());
		}
		
		//特定の日付をkeyに、その日の予定をvalueに持つ特殊なMap
		LinkedMultiValueMap tasks = new LinkedMultiValueMap();		
		if(loginUser.getName().equals("admin-name")) {
			//管理者のログインの場合
			//全てのユーザーを取得する
			List<Users> users = userRepo.findAll();
			//全てのユーザー名を取得する
			List<String> userNames = new ArrayList<String>();
			for(Users user: users) {
				userNames.add(user.getName());
			}
			//各ユーザーの予定を取得して、modelに導入する
			for(String userName: userNames) {
				System.out.println(userName);
				try {
					List<Tasks> userTasks = new ArrayList<>();
					//ユーザーの予定を取得する
					userTasks = taskRepo.findByDateBetween(monthList.get(0).get(0), monthList.get(monthList.size()-1).get(monthList.size()-1), userName);
					//LinkedMultiValueMapにuserEventsを格納していく
					for(Tasks userTask: userTasks) {
						tasks.add(userTask.getDate(), userTask);
					}
				}catch(NullPointerException e) {
					//予定がない場合は例外処理で対応
					System.out.println("NullPointerException");
				}
			}
			
		}else {
			//管理者以外のログインの場合
			try {
				//使用中のユーザーの予定を取得する
				List<Tasks> userTasks = taskRepo.findByDateBetween(monthList.get(0).get(0), monthList.get(monthList.size()-1).get(monthList.size()-1), loginUser.getName());
				//LinkedMultiValueMapにuserEventsを格納していく
				for(Tasks userTask: userTasks) {
					tasks.add(userTask.getDate(), userTask);
				}
			}catch(NullPointerException e) {
				//予定がない場合は例外処理で対応
				System.out.println("NullPointerException");
			}
		}
		//取得した予定をmodelに受け渡す
		model.addAttribute("tasks", tasks);
		
		//今日の年、月をviewに受け渡す
		String month = String.valueOf(today.getYear()) + "年" + String.valueOf(today.getMonth().getValue()) + "月";
		model.addAttribute("month", month);
		
		//先月をviewに受け渡す
		LocalDate previous = today.withDayOfMonth(1).minusMonths(1);
		model.addAttribute("prev", previous);
		
		//来月をviewに受け渡す
		LocalDate next = today.withDayOfMonth(1).plusMonths(1);
		model.addAttribute("next", next);
	
		return "main";
	}
	
}
