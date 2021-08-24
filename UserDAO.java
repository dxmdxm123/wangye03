package com.du.dao;


import com.du.bean.User;
import com.du.util.DBHelper;
import com.du.util.PageBeanUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// dao 层应该是个接口, 为什么 因为 可以使用 aop 啊. 目前啊 你不用aop , 就可以直接写成类了.
public class UserDAO {
    // 曾 删  改  查
    // 查询  select * from t_user
    // dao 层 如何 和 数据裤 做对接, 我们用的知识点叫做 jdbc , 很基础的一个 必须的技术.
    // 很多框架 都是 基于这个 jdbc 来的, 所以必须 学习 牛逼.
    // 要链接数据库,就需要用到刚刚  DBHelper.getConnection() 来创建一个  和 mysql 的一个链接的对象
    //  这个对象可以负责 和 mysql 链接

    // 登陆  select * from t_user where username =? and password =?
    public User login(String username, String password){
        User user =null;
        // 1. 创建链接
        Connection connection = DBHelper.getConnection();
        // 2. 建出 sql 语句
        String sql = " select * from t_user where username = ? and password = ?  ";
        // 3. 使用链接对象 获取 预编译对象
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,password);
            // 4. 执行 预编译对象,得出结果集
            rs = ps.executeQuery();
            if(rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setCreate_time(rs.getString("create_time"));
                user.setImg(rs.getString("img"));
                user.setIs_del(rs.getInt("is_del"));
                user.setModify_time(rs.getString("modify_time"));
                user.setPassword(rs.getString("password"));
                user.setReal_name(rs.getString("real_name"));
                user.setType(rs.getInt("type"));
                user.setUsername(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    //查询 所有的 业务员
    public List<User> selectAllByService() {
        List<User> users=new ArrayList<>();
        // 步骤1: 创建出 链接对象
        Connection connection = DBHelper.getConnection();
        // 步骤2: 创建出 sql 语句
        String sql = "select * from t_user where type = 2";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 步骤3: 使用链接对象 获取 预编译对象
            ps = connection.prepareStatement(sql);
            System.out.println("ps = " + ps);
            // 步骤4: 执行 预编译对象,得出结果集
            rs = ps.executeQuery();
            //步骤5: 遍历结果集 , 一一的取对象
            while(rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setCreate_time(rs.getString("create_time"));
                user.setImg(rs.getString("img"));
                user.setIs_del(rs.getInt("is_del"));
                user.setModify_time(rs.getString("modify_time"));
                user.setPassword(rs.getString("password"));
                user.setReal_name(rs.getString("real_name"));
                user.setType(rs.getInt("type"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    // 动态的带参数的分页查询   这个 很吊!!!  以后 mybatis 会 简化
    //  页数 page
    //  条数 limit
    public List<User> selectAllByParam(Map map){
        System.out.println("   jinlaiel    dao  " );
        System.out.println("map dao  = " + map);
        for (Object o : map.keySet()) {
            System.out.println("o = " + o);
        }
        String page = (String) map.get("page"); // 接收前段的参数,放入到 map中, 这里直接获取就可以了
        String limit = (String) map.get("limit");

        String real_name = (String) map.get("real_name");
        String type = (String) map.get("type");
        String username = (String) map.get("username");
        // 如果说  real_name 不为空
        //sql  =   select * from t_user where  real_name like %张%  limit ? , ?
        // 如果说  type 不为空   real_name 不为空
    /// sql  =   select * from t_user where  real_name like %张%  and  type=1   limit ? , ?
        // 如果说  type 不为空   real_name 不为空    username  不为空
        /// sql  =   select * from t_user where  real_name like %张%  and  type=1  and username like %李%   limit ? , ?
        List<User> lists = new ArrayList<>();
            // 1. 开链接
        Connection connection = DBHelper.getConnection();
        // 2. 写sql
        String  sql ="  select  *  from t_user  where 1=1  "; // where 1=1  因为 有多余的 and
        if (null!=real_name&&real_name.length()>0){
                sql = sql + " and real_name   like  '%"+real_name+"%'   ";
        }
        if (null!=type&&type.length()>0){
            sql = sql + " and type   =  "+type+"   ";
        }
        if (null!=username&&username.length()>0){
            sql = sql + " and username   like  '%"+username+"%'   ";
        }
        sql = sql + " limit  ? ,  ?";
        System.out.println(" dao de  sql = " + sql);
        PreparedStatement ps=null;
        ResultSet rs=null;
        PageBeanUtil pageBeanUtil = new PageBeanUtil(Integer.parseInt(page),Integer.parseInt(limit)); // 因为第一个问好需要求出来
        // 3. 编译 sql
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1,pageBeanUtil.getStart());  // 这是索引
            ps.setInt(2,Integer.parseInt(limit));
            //4, 执行sql
            rs = ps.executeQuery();
            while(rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setCreate_time(rs.getString("create_time"));
                user.setImg(rs.getString("img"));
                user.setIs_del(rs.getInt("is_del"));
                user.setModify_time(rs.getString("modify_time"));
                user.setPassword(rs.getString("password"));
                user.setReal_name(rs.getString("real_name"));
                user.setType(rs.getInt("type"));
                user.setUsername(rs.getString("username"));
                lists.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return lists;
    }

    //查询总条数
    public int selectCount(Map map1){

        String real_name = (String) map1.get("real_name");
        String type = (String) map1.get("type");
        String username = (String) map1.get("username");

        int total=0;
        // 1. 开链接
        Connection connection = DBHelper.getConnection();
        //2. 写sql
        String  sql = "  select count(*) total  from t_user  where 1=1  ";
        if (null!=real_name&&real_name.length()>0){
            sql = sql + " and real_name   like  '%"+real_name+"%'   ";
        }
        if (null!=type&&type.length()>0){
            sql = sql + " and type   =  "+type+"   ";
        }
        if (null!=username&&username.length()>0){
            sql = sql + " and username   like  '%"+username+"%'   ";
        }
        System.out.println("sql  count 的  = " + sql);
        //3. 编译
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
        ps = connection.prepareStatement(sql);
        // 4. zhixing
          rs = ps.executeQuery();
          if (rs.next()){
               total = rs.getInt("total");
          }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    // 新增
    public int addUser(User user){
        // 第一:创建链接对象
        Connection connection = DBHelper.getConnection();
        // 第二: sql 语句 , 因为 填加 的数据是变量 , 所以要用问题代替
        String sql = " insert into t_user values ( null, ? , ? , ? ,? , ? , ? , ? , ? ) ";
        PreparedStatement ps =null;
        int i = 0;
        try {
            // 第三部:预编译 sql
            ps = connection.prepareStatement(sql);
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getPassword());
            ps.setString(3,user.getReal_name());
            ps.setString(4,user.getImg());
            ps.setInt(5,user.getType());
            ps.setInt(6,user.getIs_del());
            ps.setString(7,user.getCreate_time());
            ps.setString(8,user.getModify_time());
            // 第四部,执行 预编译对象
             i = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return i;
    }

    //修改
    public int updateUserById(Integer sfDel , Integer userId){
        // 开链接
        Connection connection = DBHelper.getConnection();
        // 写sql
        String sql = " update t_user set is_del= ?  where id = ?  ";
        // 编译
        PreparedStatement ps =null;
        int i =0;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1,sfDel);
            ps.setInt(2,userId);
            // 执行
           i = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  i;
    }

    // 全部的修改
    public int updateUser(User user){
        // 开链接
        Connection connection = DBHelper.getConnection();
        // 写sql
        String sql = " update t_user set create_time=? , img=? , is_del= ? , modify_time=? , password=? , real_name=? , type=? , username=?  where id = ?  ";
        // 编译
        PreparedStatement ps=null;
        int i =0;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1,user.getCreate_time());
            ps.setString(2,user.getImg());
            ps.setInt(3,user.getIs_del());
            ps.setString(4,user.getModify_time());
            ps.setString(5,user.getPassword());
            ps.setString(6,user.getReal_name());
            ps.setInt(7,user.getType());
            ps.setString(8,user.getUsername());
            ps.setInt(9,user.getId());
            // 执行
             i = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    //  根据id 做查询
    public User selectUserById(Integer id){
        User user = new User();
        // 开链接
        Connection connection = DBHelper.getConnection();
        // 写sql
        String sql = "  select * from t_user where  id = ? ";
        // 编译
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
             ps = connection.prepareStatement(sql);
             ps.setInt(1,id);
             // 执行
            rs = ps.executeQuery();
           if (rs.next()){
               user.setId(rs.getInt("id"));
               user.setCreate_time(rs.getString("create_time"));
               user.setImg(rs.getString("img"));
               user.setIs_del(rs.getInt("is_del"));
               user.setModify_time(rs.getString("modify_time"));
               user.setPassword(rs.getString("password"));
               user.setReal_name(rs.getString("real_name"));
               user.setType(rs.getInt("type"));
               user.setUsername(rs.getString("username"));
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    public static void main(String[] args) {
       UserDAO dao=new UserDAO();

       // 测试修改
        int i = dao.updateUserById(1, 39);
        System.out.println("i = " + i);
        //测试全查
//        List<User> users = dao.selectAll();
//        for (User user : users) {
//            System.out.println("user = " + user);
//        }
        // 新增的测试
//        User user = new User();
//        user.setUsername("sunchaoyang");
//        user.setType(1);
//        user.setReal_name("孙超阳");
//        user.setPassword("123");
//        user.setModify_time("2013-09-07");
//        user.setIs_del(1);
//        user.setImg("xxxx");
//        user.setCreate_time("2013-09-07");
//        int i = dao.addUser(user);
//        System.out.println("i = " + i);
        
        //登陆的测试
//        User abc = dao.login("abc", "123456");
//        System.out.println("abc = " + abc);
        //分页查询 的测试
//        List<User> users = dao.selectAllByParam(2, 5);
//        System.out.println("users = " + users);
//        System.out.println("users = " + users.size());

        // 查总条数的 测试
//        int i = dao.selectCount();
//        System.out.println("i = " + i);

    }
}
