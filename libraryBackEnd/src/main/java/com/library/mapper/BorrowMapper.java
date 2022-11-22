package com.library.mapper;

import com.library.entity.Borrow;
import com.library.entity.pojo.BorrowWithUser;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;

public interface BorrowMapper extends Mapper<Borrow> {
    @Select("""
select t1.*,book.name bookName from (select borrow.*,user.name userName from borrow,user where borrow.borrowerId = user.id)as t1,book where borrowedBookId=book.id order by beginTime desc   
        """)
    public ArrayList<BorrowWithUser> getBorrowWithUser();
}
