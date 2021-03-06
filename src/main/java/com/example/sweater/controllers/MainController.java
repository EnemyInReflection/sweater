package com.example.sweater.controllers;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class MainController {

    private MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public MainController(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @GetMapping()
    public String greeting(Model model) {

        return "greeting";
    }

    @GetMapping("/main")
    public String main(

            @RequestParam(required = false , defaultValue = "") String filter,
            Model model){
        Iterable<Message> messages=messageRepo.findAll();
        if(filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        }
        else {
            messages = messageRepo.findAll();
        }
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user,
                      @RequestParam String text,
                      @RequestParam String tag, Model model) throws IOException {
     Message message=new Message(text,tag ,user);
    if(file!=null){
        File uploadDir=new File(uploadPath);
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }

        String uuidFile=UUID.randomUUID().toString();
        String resultFilename=uuidFile + "." + file.getOriginalFilename();

        file.transferTo(new File(uploadPath + "/" + resultFilename));

        message.setFilename(resultFilename);
    }
     messageRepo.save(message);
     Iterable<Message> messages=messageRepo.findAll();

     model.addAttribute("messages", messages);

     return "main";
    }

//    @PostMapping("/filter")
//    public String filter(@RequestParam(required = false) String filter, Model model){
//        Iterable<Message> messages;
//        if(filter != null && !filter.isEmpty()) {
//            messages = messageRepo.findByTag(filter);
//        }
//        else {
//            messages = messageRepo.findAll();
//        }
//        model.addAttribute("messages", messages);
//        return "main";
//    }

}
