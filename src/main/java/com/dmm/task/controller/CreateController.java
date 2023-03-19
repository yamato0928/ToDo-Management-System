package com.dmm.task.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.Service.AccountUserDetails;
import com.dmm.task.entity.Tasks;
import com.dmm.task.form.TaskForm;
import com.dmm.task.repository.TasksRepository;

@Controller
public class CreateController {
	
	@Autowired
	private TasksRepository taskRepo;
	
	//予定作成画面の表示
	@GetMapping("/main/create/{date}")
	public String create(Model model, @PathVariable String date) {
		TaskForm taskForm = new TaskForm();
		taskForm.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		model.addAttribute("taskForm", taskForm);
		return "/create";
	}
	
	
	
	//予定を新規作成する
	@PostMapping("/main/create")
	public String create(@Validated TaskForm taskForm, BindingResult bindingResult, @AuthenticationPrincipal AccountUserDetails user, Model model) {
		//バリデーション
		if(bindingResult.hasErrors()) {
			//エラーがある場合は登録画面を返す
			if(taskForm.getDate()!=null) {
				taskForm.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}else {
				taskForm.setDate(LocalDate.now());
			}
			model.addAttribute("taskForm", taskForm);
			return "/create";
		}
		
		Tasks task = new Tasks();
		task.setTitle(taskForm.getTitle());
		task.setName(user.getName());
		task.setText(taskForm.getText());
		task.setDate(taskForm.getDate());
		task.setDone(false);
		
		taskRepo.save(task);
		
		return "redirect:/main";
	}
}
