package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.autofill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from sky_take_out.employee where username = #{username}")
    Employee getByUsername(String username);


    @autofill(value = OperationType.INSERT)
    @Insert("insert into sky_take_out.employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user)"+ "values" +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void save(Employee employee);//新增员工

    /*分页查询*/
    Page page(EmployeePageQueryDTO employeePageQueryDTO);


    /*修改员工*/
    @autofill(value=OperationType.UPDATE)
    void update(Employee employee);


    /*根据id查询员工*/
    @Select("select * from sky_take_out.employee where id=#{id}")
    Employee selectfromid(Integer id);
}
