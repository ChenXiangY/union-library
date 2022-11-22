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
//        设置分页
        Page<BorrowWithUser> page = PageHelper.startPage((Integer) queryParam.get("current"), (Integer) queryParam.get("pageSize"), true);
//        设置按借阅开始时间递减

        List<BorrowWithUser> borrowWithUsers = this.borrowMapper.getBorrowWithUser();
        Iterator<BorrowWithUser> iterator = borrowWithUsers.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().toString());
        }
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

//    随机生成Borrow记录，添加一条完整的，1000条
    public void addBorrow(){
        Borrow borrow = new Borrow();
        Random r = new Random();
        borrow.setBorrowedBookId(r.nextInt(2787)+2);
        borrow.setBorrowerId(r.nextInt(62)+4);
//        生成随机年数
        Integer randomYear = r.nextInt(2)+2021;
//        生成随机月数
        System.out.println("randomyear  = " + (String.valueOf(randomYear)));
        Integer randomMonth;
        if(randomYear.compareTo(2021)==0) {
            randomMonth = r.nextInt(5) + 7;
        }else{
            randomMonth = r.nextInt(10)+1;
        }
//        根据月数生成随机天数
        Calendar calendar = Calendar.getInstance();
        calendar.set(randomYear.intValue(),randomMonth.intValue(),1);
        calendar.roll(Calendar.DATE,-1);
        int day = calendar.get(Calendar.DATE);
        int randomDate=1;
        if(randomMonth.compareTo(10)==0){
        randomDate = r.nextInt(21)+1;
        }else{
            randomDate = r.nextInt(day+1)+1;
        }
        calendar.set(Calendar.DATE,randomDate);
//
        System.out.println("生成的随机开始日期为"+calendar.getTime());
        borrow.setBeginTime(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR,15);
        borrow.setLimitTime(calendar.getTime());
//        设置结束时间，让天数随机加个3-14天
//        先让时间再减回来
        calendar.add(Calendar.DAY_OF_YEAR,-15);
        calendar.add(Calendar.DAY_OF_YEAR,r.nextInt(11)+3);
        if (!(calendar.get(Calendar.YEAR) == 2022 & calendar.get(Calendar.MONTH) == Calendar.NOVEMBER & calendar.get(Calendar.DATE) > 20)) {
            borrow.setEndTime(calendar.getTime());

        }
        this.borrowMapper.insert(borrow);
//        向Borrow插入一条借阅记录
    }

//    随机生成Borrow记录，添加一条未完成的，30条
    @RequestMapping("addBorrow2")
    public void addBorrowWithoutReturn(){
        for (int i = 0; i < 100; i++) {
            this.addBorrow();
        }
    }

}
