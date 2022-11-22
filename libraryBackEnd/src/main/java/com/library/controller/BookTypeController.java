package com.library.controller;

import com.library.entity.BookType;
import com.library.mapper.BookTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BookTypeController {

    @Autowired
    public BookTypeMapper bookTypeMapper;

    @RequestMapping("getBookTypes")
    @ResponseBody
    public ArrayList<BookType> getBookTypes(){
        return (ArrayList<BookType>) this.bookTypeMapper.selectAll();
    }
}
