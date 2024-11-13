package com.neon.controller

import com.neon.services.ContractChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ChatController {

    @Autowired
    lateinit var chatService: ContractChatService

    @PostMapping("/chat")
    fun chat(@RequestParam("name") name: String, @RequestParam("version") version: String, @RequestParam("userprompt") userprompt: String): String {
        return chatService.chat(name, version, userprompt)
    }
}