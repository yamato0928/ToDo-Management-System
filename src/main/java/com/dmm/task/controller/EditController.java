package com.dmm.task.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.entity.Tasks;
import com.dmm.task.form.TaskForm;
import com.dmm.task.repository.TasksRepository;

@Controller
public class EditController {
	
	@Autowired
	private TasksRepository TaskRepo;
	
	
	@GetMapping("main/edit/{id}")
	public String edit(Model model, @PathVariable Integer id) {
		Tasks task = TaskRepo.findById(id);
		model.addAttribute("task", task);
		TaskForm taskForm = new TaskForm();
		model.addAttribute("taskForm", taskForm);
		return "edit";
	}
	
	@PostMapping("main/edit/{id}")
	@Transactional
	public String edit(@Validated TaskForm taskForm, BindingResult bindingResult, Model model, @PathVariable Integer id) {
		//バリデーション
		if(bindingResult.hasErrors()) {
			//エラーがある場合には、更新画面を返す
			Tasks task = TaskRepo.findById(id);
			model.addAttribute("task", task);
			model.addAttribute("taskForm", taskForm);		
			return "edit";
		}
		//エラーがない場合には内容を更新してメイン画面に移動する
		if(taskForm != null) {
			Tasks task = new Tasks();
			task.setId(id);
			task.setTitle(taskForm.getTitle());
			task.setName(taskForm.getName());
			task.setText(taskForm.getText());
			task.setDate(taskForm.getDate());
			task.setDone(taskForm.isDone());
			
			TaskRepo.save(task);
		}
		
		return "redirect:/main";
	}

	
	@PostMapping("main/delete/{id}")
	@Transactional
	public String delete(Model model, @PathVariable Integer id) {
		TaskRepo.deleteById(id);
		return "redirect:/main";
	}
}
