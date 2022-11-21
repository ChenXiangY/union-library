package com.library.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BookController {

    @Autowired
    public BookMapper bookMapper;

//    带分页的所有书籍
    @RequestMapping("getAllBooks")
    @ResponseBody
    public HashMap<String,Object>  getAllBooksWithPagination(@RequestBody HashMap<Object,Object> queryParam){
        for (Map.Entry<Object,Object> en: queryParam.entrySet()
             ) {
            System.out.println(queryParam.get(en));
        }
        Page<Book> page = PageHelper.startPage((Integer) queryParam.get("current"), (Integer) queryParam.get("pageSize"), true);
        List<Book> books = this.bookMapper.selectAll();
        long total = page.getTotal();
        HashMap<String,Object> result = new HashMap<String,Object>();
        result.put("data",books);
        result.put("extraMessage",total);
        return result;

    }

    public String addNewBook(@RequestBody Book book){
        this.bookMapper.insert(book);
        return "添加成功";
    }
    public String deleteBook(@RequestBody Book book){
        this.bookMapper.delete(book);
        return "删除成功";
    }
}