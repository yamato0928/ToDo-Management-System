package com.dmm.task.form;

import java.time.LocalDate;

import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class TaskForm {
	//各値のバリデーションを定義する
	//viewからpostした情報を保存する
	@Size(min = 1, max = 255)
	private String title;
	
	private String name;
	
	@Size(min = 1, max = 255)
	private String text;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	private boolean done;
	
	
}
