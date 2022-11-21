package com.library.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.User;
import com.library.entity.pojo.BorrowWithUser;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.sql.Date;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BorrowController {

    @Autowired
    public BorrowMapper borrowMapper;

    @Autowired
    public BookMapper bookMapper;

    @Autowired
    public UserMapper userMapper;

    @RequestMapping("borrowBook")
    public boolean borrowBook(@RequestBody HashMap<Object,Object> query){
        Borrow borrow = new Borrow();
        for (Map.Entry ele :
                query.entrySet()) {
            System.out.println(ele.getKey());
            System.out.println(ele.getValue());
        }
        borrow.setBorrowedBookId((Integer) query.get("borrowedBookId"));
        borrow.setBorrowerId((Integer) query.get("borrowerId"));
//        日期
        Calendar calendar = Calendar.getInstance();
        borrow.setBeginTime(new Date(calendar.getTime().getTime()));
        calendar.add(Calendar.DAY_OF_YEAR,15);
        borrow.setLimitTime(new Date(calendar.getTime().getTime()));
//        向Borrow插入一条借阅记录
        this.borrowMapper.insert(borrow);
//        更改Book的库存和借出数量
        Book b = this.bookMapper.selectByPrimaryKey(query.get("borrowedBookId"));
        b.setBorrowedCount(b.getBorrowedCount()+1);
        b.setStock(b.getStock()-1);
        this.bookMapper.updateByPrimaryKey(b);
        return true;
    }
    @RequestMapping("returnBook")
    public boolean returnBook(@RequestBody Borrow borrow){
        Example example = new Example(Borrow.class);
        Example.Criteria criteria = example.createCriteria();
//        用时间来排序，
        example.setOrderByClause("beginTime");
        criteria.andEqualTo("borrowedBookId",borrow.getBorrowedBookId());
        criteria.andEqualTo("borrowerId",borrow.getBorrowerId());
//        真有可能会出现一个人借同样的书多次的情况，那么就不能用selectOne，而应该更全面考虑
        ArrayList<Borrow> borrow1 = (ArrayList<Borrow>) this.borrowMapper.selectByExample(example);
//      用时间最近的一条做。
        Borrow newBorrow = borrow1.get(0);
        newBorrow.setEndTime(new Date(System.currentTimeMillis()));
        this.borrowMapper.updateByPrimaryKey(newBorrow);
//        更新书籍信息
        Book b = this.bookMapper.selectByPrimaryKey(borrow.getBorrowedBookId());
        b.setBorrowedCount(b.getBorrowedCount()-1);
        b.setStock(b.getStock()+1);
        this.bookMapper.updateByPrimaryKey(b);
        return true;
    }
    @RequestMapping("getAllBorrowStatus")
    public HashMap<String, Object> getAllBorrowStatus(@RequestBody HashMap<Object,Object> queryParam){
        Page<BorrowWithUser> page = PageHelper.startPage((Integer) queryParam.get("current"), (Integer) queryParam.get("pageSize"), true);
        List<BorrowWithUser> borrowWithUsers = this.borrowMapper.getBorrowWithUser();
        HashMap<String,Object> result = new HashMap<String,Object>();
        long total = page.getTotal();
        result.put("data",borrowWithUsers);
        result.put("extraMessage",total);
        return result;
    }

    @RequestMapping("getBorrowers")
    public List<User> getBorrowers(@RequestBody HashMap<String,String> query){
        Example example = new Example(Borrow.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("borrowedBookId",query.get("borrowMapper"));
        ArrayList<Borrow> borrows = (ArrayList<Borrow>) this.borrowMapper.selectByExample(example);
//        得到所有的借阅记录之后，得查所有的借阅记录人的姓名
        Example example1 = new Example(User.class);
        Example.Criteria criteria1 = example1.createCriteria();
        ArrayList<Integer> borrowersId = new ArrayList<>();
        borrows.forEach((e)->{
            borrowersId.add(e.getBorrowerId());
        });
        criteria1.andIn("id",borrowersId);
        return this.userMapper.selectByExample(example1);
    }
}
