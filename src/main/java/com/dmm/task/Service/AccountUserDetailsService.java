package com.dmm.task.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dmm.task.entity.Users;
import com.dmm.task.repository.UsersRepository;

@Service
public class AccountUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UsersRepository repository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// TODO 自動生成されたメソッド・スタブ
		 if (userName == null || "".equals(userName)) {
			 throw new UsernameNotFoundException("ユーザー名が空です");
		 }
		Users user = repository.findById(userName).get();
		if(user != null) {
			return new AccountUserDetails(user);
		}
		throw new UsernameNotFoundException(userName + "は見つかりません");

	}

}
