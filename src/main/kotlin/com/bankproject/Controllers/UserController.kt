package com.bankproject.Controllers

import com.bankproject.Repositories.User
import com.bankproject.Services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(@Autowired private val userService: UserService) {

	@PostMapping("/new")
	fun createUser(@RequestBody user: User): String {
		return userService.registerUser(user)
	}
}

