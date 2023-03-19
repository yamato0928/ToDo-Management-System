package com.dmm.task.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = "password")
public class Users {
	@Id
	@Column(name = "user_name")
	public String userName;
	
	public String password;
	
	public String name;
	
	@Column(name = "role_name")
	public String roleName;
}
