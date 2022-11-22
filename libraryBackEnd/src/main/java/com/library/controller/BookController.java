package com.library.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.library.entity.Book;
import com.library.entity.BookType;
import com.library.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Type;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BookController {

    @Autowired
    public BookMapper bookMapper;

//    带分页的所有书籍,带查询功能
    @RequestMapping("getAllBooks")
    @ResponseBody
    public HashMap<String,Object>  getAllBooksWithPagination(@RequestBody HashMap<Object,Object> queryParam){
        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
//        得到前端的查询数据。
        if (queryParam.get("name") != null){
            criteria.andLike("name", "%"+(String) queryParam.get("name")+"%");
        }
        if(queryParam.get("author")!=null){
            criteria.andLike("author","%"+(String) queryParam.get("author")+"%");
        }
        if (queryParam.get("publisher")!=null){
            criteria.andLike("publisher","%"+(String) queryParam.get("publisher")+"%");
        }
//        处理类型
        if (queryParam.get("type")!=null){
            if (!((ArrayList)queryParam.get("type")).isEmpty()){
            criteria.andIn("type",(ArrayList)queryParam.get("type"));
            }
        }
//        配置分页
        Page<Book> page = PageHelper.startPage((Integer) queryParam.get("current"), (Integer) queryParam.get("pageSize"), true);
//        开始查询
        List<Book> books = this.bookMapper.selectByExample(example);
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